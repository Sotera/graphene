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

import graphene.util.validator.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class ExcelToJSONConverter {

	public Map<String, List> convert(final File f) throws Exception, IOException {
		final WorkbookSettings ws = new WorkbookSettings();
		ws.setEncoding("CP1252");

		final Workbook workbook = Workbook.getWorkbook(new File(f.getAbsolutePath()), ws);
		final Map<String, List> workbookConversion = new TreeMap<String, List>();
		for (final Sheet s : workbook.getSheets()) {
			new StringBuffer();
			final List<Map<String, String>> excelSheetConversion = new ArrayList<Map<String, String>>();
			for (int j = 0; j < s.getColumns(); j++) {
				new StringBuffer();
				final Map<String, String> kvMap = new TreeMap<String, String>();
				for (int i = 1; i < s.getRows(); i++) {

					s.getCell(j, 0);
					final Cell key = s.getCell(0, i);
					final Cell value = s.getCell(j, i);
					String keyString = key.getContents();
					if (!ValidationUtils.isValid(keyString)) {
						keyString = "Column " + i;
					}
					if (ValidationUtils.isValid(key.getContents())) {

						kvMap.put(key.getContents(), value.getContents());
					}
					System.out.println("key value is " + key.getContents() + " = " + value.getContents());
				}
				excelSheetConversion.add(kvMap);
			}
			workbookConversion.put(s.getName(), excelSheetConversion);
		}
		return workbookConversion;
	}

	public boolean supports(final String fileExtension) {
		if (".xls".equalsIgnoreCase(fileExtension) || ".xlsx".equalsIgnoreCase(fileExtension)) {
			return true;
		}
		return false;
	}

}
