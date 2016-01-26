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

package graphene.export;

import graphene.model.idl.G_Entity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.DateFormat;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntitiesToXLS {
	private static Logger logger = LoggerFactory.getLogger(EntitiesToXLS.class);

	/**
	 * @param workbook
	 * @param sheetNum
	 * @return
	 */
	private WritableSheet buildSheet(final WritableWorkbook workbook, final int sheetNum) {
		/**
		 * Why use MAX_SHEETS + 1? Because using a sheet number greater than the
		 * number of sheets will guarantee that new sheets get put at the end
		 * (appended). SheetNum is more for label use than for the actual 0
		 * based sheet index.
		 */
		final WritableSheet sheet = workbook.createSheet("Sheet  " + sheetNum, ExportMessages.MAX_SHEETS + 1);

		sheet.setColumnView(0, 12);
		sheet.setColumnView(1, 15);
		sheet.setColumnView(2, 13);
		sheet.setColumnView(3, 13);
		sheet.setColumnView(4, 50);

		final int row = 0;

		final WritableCellFormat labelFormat = new WritableCellFormat();
		try {
			labelFormat.setAlignment(jxl.format.Alignment.CENTRE);
		} catch (final WriteException e4) {
			logger.error("WriteException during labelFormat.setAlignment: " + e4.getMessage(), e4);
		}
		// Headers
		Label label;

		try {
			label = new Label(0, row, "Date");
			label.setCellFormat(labelFormat);
			sheet.addCell(label);

			label = new Label(1, row, "Account Number");
			label.setCellFormat(labelFormat);
			sheet.addCell(label);

			label = new Label(2, row, "Debit");
			label.setCellFormat(labelFormat);
			sheet.addCell(label);

			label = new Label(3, row, "Credit");
			label.setCellFormat(labelFormat);
			sheet.addCell(label);

			sheet.addCell(new Label(4, row, "Comments"));

		} catch (final RowsExceededException e) {
			logger.error("RowsExceededException during XLS export on row " + row + ": " + e.getMessage(), e);
		} catch (final WriteException e) {
			logger.error("WriteException during XLS export on row " + row + ": " + e.getMessage(), e);
		}
		sheet.getSettings().setVerticalFreeze(1);
		return sheet;
	}

	private WritableSheet buildWarningSheet(final WritableWorkbook workbook, final List<String> warnings) {
		/**
		 * Using sheet number 0 so it ends up as the final sheet.
		 */
		final WritableSheet sheet = workbook.createSheet("WARNING ", 0);

		sheet.setColumnView(0, 25);

		final int row = 0;

		final WritableCellFormat labelFormat = new WritableCellFormat();
		try {
			labelFormat.setAlignment(jxl.format.Alignment.CENTRE);
		} catch (final WriteException e4) {
			logger.error("WriteException during labelFormat.setAlignment: " + e4.getMessage(), e4);
		}
		// Headers
		Label label;

		try {
			label = new Label(0, row, "WARNING");
			label.setCellFormat(labelFormat);
			sheet.addCell(label);
			for (int i = 0; i < warnings.size(); i++) {
				sheet.addCell(new Label(0, i + 1, warnings.get(i)));
			}

		} catch (final RowsExceededException e) {
			logger.error("RowsExceededException during XLS export on row " + row + ": " + e.getMessage(), e);
		} catch (final WriteException e) {
			logger.error("WriteException during XLS export on row " + row + ": " + e.getMessage(), e);
		}

		return sheet;
	}

	public void toXLS(final List<G_Entity> lt, final List<String> keys, final String delimiter,
			final String subDelimiter, final OutputStream out, final boolean rowsWithinBounds) {
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(out);
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
			return;
		}
		int sheetNum = 1;// start at 1, to make it easier for customer to read
		buildSheet(workbook, sheetNum);

		final DateFormat customDateFormat = new DateFormat("yyyy MMM dd");
		final NumberFormat money = new NumberFormat("###,###,###.00");
		new WritableCellFormat(customDateFormat);
		new WritableCellFormat(money);

		int row = 2;// already built the sheet with headers on row 1, so start
					// on row 2
		if (rowsWithinBounds) {
			// Rows
			for (final G_Entity r : lt) {
				if ((row % 60000) == 0) {
					if (sheetNum == ExportMessages.MAX_SHEETS) {
						final ArrayList<String> warnings = new ArrayList<String>();
						warnings.add(ExportMessages.SHEETS_EXCEEDED_WARNING);
						warnings.add(ExportMessages.SUGGEST_CVS);
						buildWarningSheet(workbook, warnings);
						return;
					} else {
						buildSheet(workbook, ++sheetNum);
						// reset row to 2 if we build a new sheet.
						row = 2;
					}
				}
				if (sheetNum == ExportMessages.MAX_SHEETS) {
					// TODO:This is a bad way of doing it, refactor this entire
					// method later.
					break;
				}
				// TODO: For each property, pick the right kind of cell and put
				// the range value in there.
				//

				row++;
			}
		} else {
			final List<String> warnings = new ArrayList<String>();
			warnings.add(ExportMessages.RESULTS_EXCEEDED_WARNING);
			warnings.add(ExportMessages.RESULTS_EXCEEDED_WARNING);
			warnings.add(ExportMessages.SUGGEST_CONTACT_SUPPORT);

			buildWarningSheet(workbook, warnings);
		}
		try {
			workbook.write();
			workbook.close();
		} catch (final WriteException e1) {
			logger.error("WriteException during XLS export: " + e1.getMessage(), e1);
		} catch (final IOException e2) {
			logger.error("IOException during XLS export: " + e2.getMessage(), e2);
		}

	}
}
