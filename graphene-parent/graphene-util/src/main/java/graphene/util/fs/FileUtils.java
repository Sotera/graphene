package graphene.util.fs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Taken largely from Neo4J 2.0, with our own additions.
 * 
 * 
 * @author djue
 * 
 */
public class FileUtils {

	private static int WINDOWS_RETRY_COUNT = 3;

	public static ArrayList<File> getFiles(String path, String ext) {

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		ArrayList<File> csvFiles = new ArrayList<File>();
		for (File f : listOfFiles) {
			if (f.isFile()) {
				if (f.getName().substring(f.getName().lastIndexOf("."))
						.equalsIgnoreCase(ext)) {
					csvFiles.add(f);
				}
			}
		}
		System.out.println("Found files " + csvFiles);
		return csvFiles;
	}

	// Externalized so we can test it.
	public static final String ENVIRONMENTAL_VAR_REGEX = "\\$\\{(\\w+)\\}|\\$(\\w+)|\\%(\\w+)\\%";

	/**
	 * 
	 * @param path
	 * @return
	 */
	public static String convertSystemProperties(String path) {
		if (null == path) {
			return null;
		}
		// match ${ENV_VAR_NAME} or $ENV_VAR_NAME or %ENV_VAR_NAME%
		Pattern p = Pattern.compile(ENVIRONMENTAL_VAR_REGEX);
		Matcher m = p.matcher(path); // get a matcher object
		StringBuffer sb = new StringBuffer();
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
			String envVarValue = m.quoteReplacement(System.getenv(envVarName));
			m.appendReplacement(sb, null == envVarValue ? "" : envVarValue);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static void deleteRecursively(File directory) throws IOException {
		Stack<File> stack = new Stack<>();
		List<File> temp = new LinkedList<>();
		stack.push(directory.getAbsoluteFile());
		while (!stack.isEmpty()) {
			File top = stack.pop();
			File[] files = top.listFiles();
			if (files != null) {
				for (File child : files) {
					if (child.isFile()) {
						if (!deleteFile(child)) {
							throw new IOException("Failed to delete "
									+ child.getCanonicalPath());
						}
					} else {
						temp.add(child);
					}
				}
			}
			files = top.listFiles();
			if (files == null || files.length == 0) {
				if (!deleteFile(top)) {
					throw new IOException("Failed to delete "
							+ top.getCanonicalPath());
				}
			} else {
				stack.push(top);
				for (File f : temp) {
					stack.push(f);
				}
			}
			temp.clear();
		}
	}

	public static boolean deleteFile(File file) {
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
		} while (!deleted && count <= WINDOWS_RETRY_COUNT);
		return deleted;
	}

	public static File[] deleteFiles(File directory, String regexPattern)
			throws IOException {
		Pattern pattern = Pattern.compile(regexPattern);
		Collection<File> deletedFiles = new ArrayList<>();
		File[] files = directory.listFiles();
		if (files == null) {
			throw new IllegalArgumentException(directory
					+ " is not a directory");
		}
		for (File file : files) {
			if (pattern.matcher(file.getName()).find()) {
				if (!file.delete()) {
					throw new IOException("Couldn't delete file '"
							+ file.getAbsolutePath() + "'");
				}
				deletedFiles.add(file);
			}
		}
		return deletedFiles.toArray(new File[deletedFiles.size()]);
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
	public static void moveFile(File toMove, File target) throws IOException {
		if (!toMove.exists())
			throw new NotFoundException("Source file[" + toMove.getName()
					+ "] not found");
		if (target.exists())
			throw new NotFoundException("Target file[" + target.getName()
					+ "] already exists");

		if (toMove.renameTo(target))
			return;

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
	public static File moveFileToDirectory(File toMove, File targetDirectory)
			throws IOException {
		if (!targetDirectory.isDirectory()) {
			throw new IllegalArgumentException(
					"Move target must be a directory, not " + targetDirectory);
		}

		File target = new File(targetDirectory, toMove.getName());
		moveFile(toMove, target);
		return target;
	}

	public static boolean renameFile(File srcFile, File renameToFile) {
		if (!srcFile.exists()) {
			throw new NotFoundException("Source file[" + srcFile.getName()
					+ "] not found");
		}
		if (renameToFile.exists()) {
			throw new NotFoundException("Target file[" + renameToFile.getName()
					+ "] already exists");
		}
		if (!renameToFile.getParentFile().isDirectory()) {
			throw new NotFoundException("Target directory["
					+ renameToFile.getParent() + "] does not exists");
		}
		int count = 0;
		boolean renamed;
		do {
			renamed = srcFile.renameTo(renameToFile);
			if (!renamed) {
				count++;
				waitSome();
			}
		} while (!renamed && count <= WINDOWS_RETRY_COUNT);
		return renamed;
	}

	public static void truncateFile(FileChannel fileChannel, long position)
			throws IOException {
		int count = 0;
		boolean success = false;
		IOException cause = null;
		do {
			count++;
			try {
				fileChannel.truncate(position);
				success = true;
			} catch (IOException e) {
				cause = e;
			}

		} while (!success && count <= WINDOWS_RETRY_COUNT);
		if (!success) {
			throw new IOException("Failure to truncateFile "
					+ fileChannel.toString());
		}
	}

	public static void truncateFile(File file, long position)
			throws IOException {
		try (RandomAccessFile access = new RandomAccessFile(file, "rw")) {
			truncateFile(access.getChannel(), position);
		}
	}

	private static void waitSome() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException ee) {
			Thread.interrupted();
		} // ok
		System.gc();
	}

	public static String fixSeparatorsInPath(String path) {
		String fileSeparator = System.getProperty("file.separator");
		if ("\\".equals(fileSeparator)) {
			path = path.replace('/', '\\');
		} else if ("/".equals(fileSeparator)) {
			path = path.replace('\\', '/');
		}
		return path;
	}

	public static void copyFile(File srcFile, File dstFile) throws IOException {
		// noinspection ResultOfMethodCallIgnored
		dstFile.getParentFile().mkdirs();
		FileInputStream input = null;
		FileOutputStream output = null;
		try {
			input = new FileInputStream(srcFile);
			output = new FileOutputStream(dstFile);
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int bytesRead;
			while ((bytesRead = input.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			// Because the message from this cause may not mention which file
			// it's about
			throw new IOException("Could not copy '" + srcFile + "' to '"
					+ dstFile + "'", e);
		} finally {
			if (input != null)
				input.close();
			if (output != null)
				output.close();
		}
	}

	public static void copyRecursively(File fromDirectory, File toDirectory)
			throws IOException {
		copyRecursively(fromDirectory, toDirectory, null);
	}

	public static void copyRecursively(File fromDirectory, File toDirectory,
			FileFilter filter) throws IOException {
		for (File fromFile : fromDirectory.listFiles(filter)) {
			File toFile = new File(toDirectory, fromFile.getName());
			if (fromFile.isDirectory()) {
				toFile.mkdir();
				copyRecursively(fromFile, toFile, filter);
			} else {
				copyFile(fromFile, toFile);
			}
		}
	}

	public static void writeToFile(File target, String text, boolean append)
			throws IOException {
		if (!target.exists()) {
			target.getParentFile().mkdirs();
			target.createNewFile();
		}

		try (Writer out = new OutputStreamWriter(new FileOutputStream(target,
				append), "UTF-8")) {
			out.write(text);
		}
	}

	public static BufferedReader newBufferedFileReader(File file,
			Charset charset) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(
				file), charset));
	}

	public static PrintWriter newFilePrintWriter(File file, Charset charset)
			throws FileNotFoundException {
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(
				file, true), charset));
	}

	public static File path(String root, String... path) {
		return path(new File(root), path);
	}

	public static File path(File root, String... path) {
		for (String part : path) {
			root = new File(root, part);
		}
		return root;
	}
}
