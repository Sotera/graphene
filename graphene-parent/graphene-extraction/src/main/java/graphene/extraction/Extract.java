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

/**
 * 
 */
package graphene.extraction;

import graphene.dao.ExtractionDAO;
import graphene.hts.entityextraction.Extractor;
import graphene.hts.file.ExcelXSSFToJSONConverter;
import graphene.model.extracted.EncodedFile;
import graphene.model.extracted.ExtractedData;
import graphene.util.DataFormatConstants;
import graphene.util.fs.FileUtils;
import graphene.util.stats.TimeReporter;
import graphene.util.validator.ValidationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.UsesMappedConfiguration;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.elasticsearch.common.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author djue
 * 
 */
@UsesMappedConfiguration(Extractor.class)
public class Extract {
	private final Map<String, Extractor> singletons;

	private static boolean saveEmbeddedFile = false;

	private static boolean customConvertExcel = false;

	private static String divideRegex = "[\\r\\f\\n\\t,;]+";

	/**
	 * @return the divideRegex
	 */
	public static String getDivideRegex() {
		return divideRegex;
	}

	/**
	 * @return the customConvertExcel
	 */
	public static boolean isCustomConvertExcel() {
		return customConvertExcel;
	}

	/**
	 * @return the saveEmbeddedFile
	 */
	public static boolean isSaveEmbeddedFile() {
		return saveEmbeddedFile;
	}

	/**
	 * @param customConvertExcel
	 *            the customConvertExcel to set
	 */
	public static void setCustomConvertExcel(final boolean customConvertExcel) {
		Extract.customConvertExcel = customConvertExcel;
	}

	/**
	 * @param divideRegex
	 *            the divideRegex to set
	 */
	public static void setDivideRegex(final String divideRegex) {
		Extract.divideRegex = divideRegex;
	}

	/**
	 * @param saveEmbeddedFile
	 *            the saveEmbeddedFile to set
	 */
	public static void setSaveEmbeddedFile(final boolean saveEmbeddedFile) {
		Extract.saveEmbeddedFile = saveEmbeddedFile;
	}

	@Inject
	private ExtractionDAO dao;
	private final Logger logger = LoggerFactory.getLogger(Extract.class);

	public Extract(final Map<String, Extractor> singletons) {
		this.singletons = singletons;
	}

	// private final Extractor ipExtractor = new IPAddressExtractor();

	public void iterateOverFiles(final String path, final String documentClass, final String note, final String... exts)
			throws IOException, SAXException, TikaException, NoSuchAlgorithmException {
		int numberOfDuplicates = 0;
		int numberOfFiles = 0;
		int numberOfFilesWithExt = 0;
		final TimeReporter tr = new TimeReporter("Ingest All Documents", logger);
		for (final String ext : exts) {
			final ArrayList<File> list = FileUtils.getFilesRecursively(path, ext);
			int index = 1;
			final AutoDetectParser parser = new AutoDetectParser();
			final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			numberOfFilesWithExt = list.size();
			numberOfFiles += list.size();
			for (final File f : list) {
				DigestInputStream dis = new DigestInputStream(new FileInputStream(f), messageDigest);

				final Metadata metadata = new Metadata();
				final BodyContentHandler handler = new BodyContentHandler(-1);
				try {
					if (ValidationUtils.isValid(dis)) {
						final TimeReporter trFile = new TimeReporter("Parse file " + f.getAbsolutePath(), logger);
						parser.parse(dis, handler, metadata);
						final String md5 = FileUtils.getMD5String(messageDigest.digest());
						if (!dao.exists(md5)) {

							final DateTime now = DateTime.now();
							final ExtractedData ed = new ExtractedData(now.getMillis(),
									DataFormatConstants.formatDate(now.getMillis()));

							ed.setAbsolutePath(f.getAbsolutePath());

							ed.setContainingPath(f.getParent());

							ed.setFilename(f.getName());

							ed.setFileExtension(FileUtils.getFileExtension(f.getName()));

							ed.setMd5Hash(md5);

							ed.setDocumentClass(documentClass);

							ed.setNote(note);
							ed.setBody(handler.toString().trim());

							for (final String m : metadata.names()) {
								ed.getMetadata().put(m, metadata.getValues(m));
							}
							// if (ed.getBody().startsWith("Amount:")) {
							// final KeyValueExtractor kve = new
							// KeyValueExtractor();
							// ed.getCustomMetadata().put("amounts",
							// kve.makeMappings(ed.getBody()));
							// }
							for (final Entry<String, Extractor> entry : singletons.entrySet()) {
								ed.getCustomMetadata().put(entry.getKey(),
										entry.getValue().divideAndExtract(ed.getBody(), divideRegex));
							}

							dis.close();
							if (customConvertExcel) {
								try {
									final ExcelXSSFToJSONConverter excelConverter = new ExcelXSSFToJSONConverter();
									if (excelConverter.supports(ed.getFileExtension())) {
										ed.getCustomMetadata().put("excelConversion", excelConverter.convert(f));
										logger.debug("Excel conversion complete.");
									}
								} catch (final Exception e) {
									logger.error("Problem converting excel file to maps with " + f.getAbsolutePath()
											+ ", error:" + e.getMessage());
									e.printStackTrace();
								}
							}
							// dis.close();

							dao.saveExtraction(ed);
							logger.debug("Success " + index + "/" + list.size() + " : Wrote metadata for "
									+ ed.getFilename() + " to server with id: " + ed.getMd5Hash());
							if (saveEmbeddedFile) {
								dis = new DigestInputStream(new FileInputStream(f), messageDigest);
								final EncodedFile ef = new EncodedFile(now.getMillis(),
										DataFormatConstants.formatDate(now.getMillis()));
								ef.setAbsolutePath(f.getAbsolutePath());
								ef.setContainingPath(f.getParent());
								ef.setFilename(f.getName());
								ef.setFileExtension(FileUtils.getFileExtension(f.getName()));
								ef.setMd5Hash(md5);
								ef.setDocumentClass(documentClass);
								try {
									ef.setEncodedFile(FileUtils.encodeFileToBase64Binary(f, dis));
								} catch (final Exception e) {
									e.printStackTrace();
								} finally {
									if (ValidationUtils.isValid(dis)) {
										dis.close();
									}
								}
								dao.saveEncodedFile(ef);
								logger.debug("Success " + index + "/" + list.size() + " : Wrote encoded file "
										+ ef.getFilename() + " to server with id: " + ef.getMd5Hash());
							}
						} else {
							numberOfDuplicates++;
							logger.debug("A file with hash " + md5 + " has already been parsed.");
						}
						trFile.logAsCompleted();
					}
				} finally {
					if (ValidationUtils.isValid(dis)) {
						dis.close();
					}
				}
				index++;
			}
			System.out.println("Number of files with extension " + ext + ": " + numberOfFilesWithExt);
		}
		tr.logAsCompleted();
		System.out.println("Number of files: " + numberOfFiles);
		System.out.println("Number of duplicates: " + numberOfDuplicates);
	}
}
