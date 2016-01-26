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

package graphene.hts.file;

import graphene.util.fs.FileUtils;
import graphene.util.validator.ValidationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

public class ExcelXSSFToJSONConverter {
	Logger logger = org.slf4j.LoggerFactory.getLogger(ExcelXSSFToJSONConverter.class);

	public Map<String, List> convert(final File f) throws Exception, IOException {
		FileInputStream fis = null;
		final Map<String, List> workbookConversion = new TreeMap<String, List>();
		try {

			final String ext = FileUtils.getFileExtension(f.getName());
			boolean success = false;
			logger.debug("Ext " + ext);
			/**
			 * For modern excel files
			 */
			if (".xls".equalsIgnoreCase(ext) || ".xlsx".equalsIgnoreCase(ext)) {
				fis = new FileInputStream(f);
				try {
					logger.debug("Trying to open file " + f.getAbsolutePath() + " as an XSSF document.");
					final XSSFWorkbook workbook = new XSSFWorkbook(fis);
					for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
						workbookConversion.put(workbook.getSheetAt(i).getSheetName(), internalConvert(workbook
								.getSheetAt(i).iterator()));

					}
					success = true;
				} catch (final Exception e) {
					logger.error("Could not open file " + f.getAbsolutePath() + " as an XSSF document.", e);
				} finally {
					if (ValidationUtils.isValid(fis)) {
						fis.close();
					}
				}
			}
			/**
			 * For older excel files
			 */
			if (!success && ".xls".equalsIgnoreCase(FileUtils.getFileExtension(f.getName()))) {
				fis = new FileInputStream(f);
				try {
					logger.debug("Trying to open file " + f.getAbsolutePath() + " as an HSSF document.");
					final HSSFWorkbook workbook = new HSSFWorkbook(fis);
					for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
						workbookConversion.put(workbook.getSheetAt(i).getSheetName(), internalConvert(workbook
								.getSheetAt(i).iterator()));
					}
				} catch (final Exception e) {
					logger.error("Could not open file " + f.getAbsolutePath() + " as an HSSF document.", e);

				} finally {
					if (ValidationUtils.isValid(fis)) {
						fis.close();
					}
				}
			}
		} finally {
			if (ValidationUtils.isValid(fis)) {
				fis.close();
			}
		}
		return workbookConversion;
	}

	private List internalConvert(final Iterator<Row> rowIter) {
		final List<Map<String, String>> excelSheetConversion = new ArrayList<Map<String, String>>();
		final List<String> headerRow = new ArrayList<String>();
		if (rowIter.hasNext()) {
			final Row row = rowIter.next();
			final Iterator<Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				final Cell cell = cellIterator.next();
				String cellString = cell.getStringCellValue();
				if (!ValidationUtils.isValid(cellString)) {
					cellString = "Column " + cell.getColumnIndex();
				}
				headerRow.add(cellString);
				System.out.println("Header Column: " + cellString);
			}

		}
		while (rowIter.hasNext()) {
			final Row row = rowIter.next();
			final Map<String, String> kvMap = new TreeMap<String, String>();
			final Iterator<org.apache.poi.ss.usermodel.Cell> cellIterator = row.cellIterator();
			while (cellIterator.hasNext()) {
				final org.apache.poi.ss.usermodel.Cell cell = cellIterator.next();
				// System.out.println("Header Columns: " + headerRow);
				final int ci = cell.getColumnIndex();
				String key = "Column " + ci;
				if (ci < headerRow.size()) {
					key = headerRow.get(cell.getColumnIndex());
				}
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					if (ValidationUtils.isValid(key, cell.getBooleanCellValue())) {
						kvMap.put(key, new Boolean(cell.getBooleanCellValue()).toString());
					}
					break;
				case Cell.CELL_TYPE_STRING:
					if (ValidationUtils.isValid(key, cell.getStringCellValue())) {
						kvMap.put(key, cell.getStringCellValue());
					}
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (ValidationUtils.isValid(key, cell.getNumericCellValue())) {
						kvMap.put(key, new Double(cell.getNumericCellValue()).toString());
					}
					break;
				case Cell.CELL_TYPE_BLANK:
					break;
				default:
					break;

				}

			}
			excelSheetConversion.add(kvMap);
		}
		logger.debug("Added sheet to conversion.");
		return excelSheetConversion;
	}

	public boolean supports(final String fileExtension) {
		if (".xlsx".equalsIgnoreCase(fileExtension) || ".xls".equalsIgnoreCase(fileExtension)) {
			return true;
		}
		return false;
	}

}
