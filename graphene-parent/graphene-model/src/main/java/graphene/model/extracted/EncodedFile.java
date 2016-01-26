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

package graphene.model.extracted;

public class EncodedFile {
	private String absolutePath;
	private String containingPath;
	/**
	 * Something like Excel files, or PDFs or 'Files From USB'
	 */
	private String documentClass;
	private String encodedFile;
	private String extractionDateISO;
	private long extractionDateMillis;
	private String fileExtension;

	private String filename;

	private String md5Hash;

	public EncodedFile() {
		// TODO Auto-generated constructor stub
	}

	public EncodedFile(final long millis, final String dateISO) {

		extractionDateISO = dateISO;
		extractionDateMillis = millis;

	}

	/**
	 * @return the absolutePath
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	/**
	 * @return the containingPath
	 */
	public String getContainingPath() {
		return containingPath;
	}

	/**
	 * @return the documentClass
	 */
	public String getDocumentClass() {
		return documentClass;
	}

	/**
	 * @return the encodedFile
	 */
	public String getEncodedFile() {
		return encodedFile;
	}

	/**
	 * @return the extractionDateISO
	 */
	public String getExtractionDateISO() {
		return extractionDateISO;
	}

	/**
	 * @return the extractionDateMillis
	 */
	public long getExtractionDateMillis() {
		return extractionDateMillis;
	}

	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the md5Hash
	 */
	public String getMd5Hash() {
		return md5Hash;
	}

	/**
	 * @param absolutePath
	 *            the absolutePath to set
	 */
	public void setAbsolutePath(final String absolutePath) {
		this.absolutePath = absolutePath;
	}

	/**
	 * @param containingPath
	 *            the containingPath to set
	 */
	public void setContainingPath(final String containingPath) {
		this.containingPath = containingPath;
	}

	/**
	 * @param documentClass
	 *            the documentClass to set
	 */
	public void setDocumentClass(final String documentClass) {
		this.documentClass = documentClass;
	}

	/**
	 * @param encodedFile
	 *            the encodedFile to set
	 */
	public void setEncodedFile(final String encodedFile) {
		this.encodedFile = encodedFile;
	}

	/**
	 * @param extractionDateISO
	 *            the extractionDateISO to set
	 */
	public void setExtractionDateISO(final String extractionDateISO) {
		this.extractionDateISO = extractionDateISO;
	}

	/**
	 * @param extractionDateMillis
	 *            the extractionDateMillis to set
	 */
	public void setExtractionDateMillis(final long extractionDateMillis) {
		this.extractionDateMillis = extractionDateMillis;
	}

	/**
	 * @param fileExtension
	 *            the fileExtension to set
	 */
	public void setFileExtension(final String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(final String filename) {
		this.filename = filename;
	}

	/**
	 * @param md5Hash
	 *            the md5Hash to set
	 */
	public void setMd5Hash(final String md5Hash) {
		this.md5Hash = md5Hash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("EncodedFile [absolutePath=").append(absolutePath).append(", containingPath=")
				.append(containingPath).append(", documentClass=").append(documentClass).append(", extractionDateISO=")
				.append(extractionDateISO).append(", extractionDateMillis=").append(extractionDateMillis)
				.append(", fileExtension=").append(fileExtension).append(", filename=").append(filename)
				.append(", md5Hash=").append(md5Hash).append("]");
		return builder.toString();
	}
}
