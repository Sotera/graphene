/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.util.fs;

import graphene.util.validator.ValidationUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Taken largely from Neo4J 2.0, with our own additions.
 * 
 * 
 * @author djue
 * 
 */
public class FileUtils {

	private static int WINDOWS_RETRY_COUNT = 3;
	private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

	// Externalized so we can test it.
	public static final String ENVIRONMENTAL_VAR_REGEX = "\\$\\{(\\w+)\\}|\\$(\\w+)|\\%(\\w+)\\%";

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String convertSystemProperties(final String path) {
		if (null == path) {
			logger.error("A null path was provided");
			return null;
		}
		// match ${ENV_VAR_NAME} or $ENV_VAR_NAME or %ENV_VAR_NAME%
		final Pattern p = Pattern.compile(ENVIRONMENTAL_VAR_REGEX);
		final Matcher m = p.matcher(path); // get a matcher object
		final StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String envVarName = null;
			if (m.group(1) != null) {
				envVarName = m.group(1);
			} else if (m.group(2) != null) {
				envVarName = m.group(2);
			} else {
				// by elimination
				envVarName = m.group(3);
			}
			String envVarWithQuotes = System.getenv(envVarName);
			if (ValidationUtils.isValid(envVarWithQuotes)) {
				final String envVarValue = Matcher.quoteReplacement(envVarWithQuotes);
				m.appendReplacement(sb, null == envVarValue ? "" : envVarValue);
			}
		}
		m.appendTail(sb);
		String newPath = sb.toString();
		if (newPath.contains(" ")) {
			logger.debug("Resolved path [" + newPath + "] contains spaces, so wrapping it in quotes for Windows");
			newPath = "\"" + newPath + "\"";
		}
		logger.debug("New path = " + newPath);
		return newPath;
	}

	public static void copyFile(final File srcFile, final File dstFile) throws IOException {
		// noinspection ResultOfMethodCallIgnored
		dstFile.getParentFile().mkdirs();
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(srcFile);
			output = new FileOutputStream(dstFile);
			final int bufferSize = 1024;
			final byte[] buffer = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (final IOException e) {
			// Because the message from this cause may not mention which file
			// it's about
			throw new IOException("Could not copy '" + srcFile + "' to '" + dstFile + "'", e);
		} finally {
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
		}
	}

	public static void copyRecursively(final File fromDirectory, final File toDirectory) throws IOException {
		copyRecursively(fromDirectory, toDirectory, null);
	}

	public static void copyRecursively(final File fromDirectory, final File toDirectory, final FileFilter filter)
			throws IOException {
		for (final File fromFile : fromDirectory.listFiles(filter)) {
			final File toFile = new File(toDirectory, fromFile.getName());
			if (fromFile.isDirectory()) {
				toFile.mkdir();
				copyRecursively(fromFile, toFile, filter);
			} else {
				copyFile(fromFile, toFile);
			}
		}
	}

	public static boolean deleteFile(final File file) {
		if (!file.exists()) {
			return true;
		}
		int count = 0;
		boolean deleted;
		do {
			deleted = file.delete();
			if (!deleted) {
				count++;
				waitSome();
			}
		} while (!deleted && (count <= WINDOWS_RETRY_COUNT));
		return deleted;
	}

	public static File[] deleteFiles(final File directory, final String regexPattern) throws IOException {
		final Pattern pattern = Pattern.compile(regexPattern);
		final Collection<File> deletedFiles = new ArrayList<>();
		final File[] files = directory.listFiles();
		if (files == null) {
			throw new IllegalArgumentException(directory + " is not a directory");
		}
		for (final File file : files) {
			if (pattern.matcher(file.getName()).find()) {
				if (!file.delete()) {
					throw new IOException("Couldn't delete file '" + file.getAbsolutePath() + "'");
				}
				deletedFiles.add(file);
			}
		}
		return deletedFiles.toArray(new File[deletedFiles.size()]);
	}

	public static void deleteRecursively(final File directory) throws IOException {
		final Stack<File> stack = new Stack<>();
		final List<File> temp = new LinkedList<>();
		stack.push(directory.getAbsoluteFile());
		while (!stack.isEmpty()) {
			final File top = stack.pop();
			File[] files = top.listFiles();
			if (files != null) {
				for (final File child : files) {
					if (child.isFile()) {
						if (!deleteFile(child)) {
							throw new IOException("Failed to delete " + child.getCanonicalPath());
						}
					} else {
						temp.add(child);
					}
				}
			}
			files = top.listFiles();
			if ((files == null) || (files.length == 0)) {
				if (!deleteFile(top)) {
					throw new IOException("Failed to delete " + top.getCanonicalPath());
				}
			} else {
				stack.push(top);
				for (final File f : temp) {
					stack.push(f);
				}
			}
			temp.clear();
		}
	}

	public static String encodeFileToBase64Binary(final File file, final InputStream is) throws Exception {
		final byte[] bytes = loadFile(file, is);
		final byte[] encoded = Base64.encodeBase64(bytes);
		final String encodedString = new String(encoded);
		return encodedString;
	}

	public static String fixSeparatorsInPath(String path) {
		final String fileSeparator = System.getProperty("file.separator");
		if ("\\".equals(fileSeparator)) {
			path = path.replace('/', '\\');
		} else if ("/".equals(fileSeparator)) {
			path = path.replace('\\', '/');
		}
		return path;
	}

	public static String getFileExtension(final String name) {
		if (name.contains(".")) {
			return name.substring(name.lastIndexOf("."));
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param path
	 *            The path to start searching from.
	 * @param ext
	 *            the file extension without the leading '.', for example "PDF"
	 *            or "tar.gz". This is case insensitive.
	 * @return an array of File objects.
	 */
	public static ArrayList<File> getFiles(final String path, final String ext) {

		final File folder = new File(path);
		final File[] listOfFiles = folder.listFiles();
		final ArrayList<File> theFiles = new ArrayList<File>();
		for (final File f : listOfFiles) {
			if (f.isFile()) {
				if (f.getName().substring(f.getName().lastIndexOf(".")).equalsIgnoreCase(ext)) {
					theFiles.add(f);
				}
			}
		}
		System.out.println("Found files " + theFiles);
		return theFiles;
	}

	public static ArrayList<File> getFilesRecursively(final String path, final String ext) throws IOException {
		final File folder = new File(path);
		final File[] listOfFiles = folder.listFiles();
		final ArrayList<File> theFiles = new ArrayList<File>();
		for (final File currentFile : listOfFiles) {
			final String name = currentFile.getName();
			if (currentFile.isDirectory()) {
				// logger.debug("Directory name is " +
				// currentFile.getAbsolutePath());
				theFiles.addAll(getFilesRecursively(currentFile.getAbsolutePath(), ext));
			} else {
				// logger.debug("File name is " + currentFile.getName());
				if (ValidationUtils.isValid(ext)) {
					// it's a file and we care about the extension
					if (name.contains(".")) {
						if (name.startsWith(".")) {
							// ignore these temp files
							logger.debug("Ignoring file " + name);
						} else {
							final String currentExt = getFileExtension(name);
							logger.debug("current Ext is " + currentExt);
							if (("." + ext).equalsIgnoreCase(currentExt)) {
								logger.debug("Adding file " + name);
								theFiles.add(currentFile);
							}
						}
					} else {
						// what do do with files that have no period?
						logger.debug("Ignoring file " + name);
					}
				} else {
					// Just add the file
					logger.debug("Adding file " + name);
					theFiles.add(currentFile);
				}
			}
		}
		return theFiles;
	}

	public static String getMD5String(final byte[] bytes) {
		final BigInteger bigInt = new BigInteger(1, bytes);
		final String hashtext = bigInt.toString(16);
		return hashtext;

	}

	private static byte[] loadFile(final File file, final InputStream is) throws IOException {
		final long length = file.length();
		if (length > Integer.MAX_VALUE) {
			return null;

		}
		final byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while ((offset < bytes.length) && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Could not completely read file ");
			stringBuilder.append(file.getName());
			throw new IOException(stringBuilder.toString());
		}
		return bytes;

	}

	/**
	 * Utility method that moves a file from its current location to the new
	 * target location. If rename fails (for example if the target is another
	 * disk) a copy/delete will be performed instead. This is not a rename, use
	 * {@link #renameFile(File, File)} instead.
	 * 
	 * @param toMove
	 *            The File object to move.
	 * @param target
	 *            Target file to move to.
	 * @throws IOException
	 */
	public static void moveFile(final File toMove, final File target) throws IOException {
		if (!toMove.exists()) {
			throw new NotFoundException("Source file[" + toMove.getName() + "] not found");
		}
		if (target.exists()) {
			throw new NotFoundException("Target file[" + target.getName() + "] already exists");
		}

		if (toMove.renameTo(target)) {
			return;
		}

		if (toMove.isDirectory()) {
			target.mkdirs();
			copyRecursively(toMove, target);
			deleteRecursively(toMove);
		} else {
			copyFile(toMove, target);
			deleteFile(toMove);
		}
	}

	/**
	 * Utility method that moves a file from its current location to the
	 * provided target directory. If rename fails (for example if the target is
	 * another disk) a copy/delete will be performed instead. This is not a
	 * rename, use {@link #renameFile(File, File)} instead.
	 * 
	 * @param toMove
	 *            The File object to move.
	 * @param targetDirectory
	 *            the destination directory
	 * @return the new file, null iff the move was unsuccessful
	 * @throws IOException
	 */
	public static File moveFileToDirectory(final File toMove, final File targetDirectory) throws IOException {
		if (!targetDirectory.isDirectory()) {
			throw new IllegalArgumentException("Move target must be a directory, not " + targetDirectory);
		}

		final File target = new File(targetDirectory, toMove.getName());
		moveFile(toMove, target);
		return target;
	}

	public static BufferedReader newBufferedFileReader(final File file, final Charset charset)
			throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
	}

	public static PrintWriter newFilePrintWriter(final File file, final Charset charset) throws FileNotFoundException {
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), charset));
	}

	public static File path(File root, final String... path) {
		for (final String part : path) {
			root = new File(root, part);
		}
		return root;
	}

	public static File path(final String root, final String... path) {
		return path(new File(root), path);
	}

	public static boolean renameFile(final File srcFile, final File renameToFile) {
		if (!srcFile.exists()) {
			throw new NotFoundException("Source file[" + srcFile.getName() + "] not found");
		}
		if (renameToFile.exists()) {
			throw new NotFoundException("Target file[" + renameToFile.getName() + "] already exists");
		}
		if (!renameToFile.getParentFile().isDirectory()) {
			throw new NotFoundException("Target directory[" + renameToFile.getParent() + "] does not exists");
		}
		int count = 0;
		boolean renamed;
		do {
			renamed = srcFile.renameTo(renameToFile);
			if (!renamed) {
				count++;
				waitSome();
			}
		} while (!renamed && (count <= WINDOWS_RETRY_COUNT));
		return renamed;
	}

	public static void truncateFile(final File file, final long position) throws IOException {
		try (RandomAccessFile access = new RandomAccessFile(file, "rw")) {
			truncateFile(access.getChannel(), position);
		}
	}

	public static void truncateFile(final FileChannel fileChannel, final long position) throws IOException {
		int count = 0;
		boolean success = false;
		do {
			count++;
			try {
				fileChannel.truncate(position);
				success = true;
			} catch (final IOException e) {
			}

		} while (!success && (count <= WINDOWS_RETRY_COUNT));
		if (!success) {
			throw new IOException("Failure to truncateFile " + fileChannel.toString());
		}
	}

	private static void waitSome() {
		try {
			Thread.sleep(500);
		} catch (final InterruptedException ee) {
			Thread.interrupted();
		} // ok
		System.gc();
	}

	public static void writeToFile(final File target, final String text, final boolean append) throws IOException {
		if (!target.exists()) {
			target.getParentFile().mkdirs();
			target.createNewFile();
		}

		try (Writer out = new OutputStreamWriter(new FileOutputStream(target, append), "UTF-8")) {
			out.write(text);
		}
	}
}
