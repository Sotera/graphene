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

import graphene.model.idl.G_Link;
import graphene.model.idl.G_TransactionResults;
import graphene.model.idlhelper.PropertyHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsToXLS {
	private static Logger logger = LoggerFactory.getLogger(EventsToXLS.class);

	/**
	 * @param workbook
	 * @param sheetNum
	 * @param keys
	 * @return
	 */
	private WritableSheet buildSheet(final WritableWorkbook workbook, final int sheetNum, final String... keys) {
		/**
		 * Why use MAX_SHEETS + 1? Because using a sheet number greater than the
		 * number of sheets will guarantee that new sheets get put at the end
		 * (appended). SheetNum is more for label use than for the actual 0
		 * based sheet index.
		 */
		final WritableSheet sheet = workbook.createSheet("Sheet  " + sheetNum, ExportMessages.MAX_SHEETS + 1);
		for (int colNum = 0; colNum < keys.length; colNum++) {
			sheet.setColumnView(colNum, 15);
		}
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
			for (final String k : keys) {
				label = new Label(0, row, k);
				label.setCellFormat(labelFormat);
				sheet.addCell(label);
			}
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

	public void toXLS(final G_TransactionResults lt, final OutputStream out, final boolean rowsWithinBounds,
			final String... keys) {
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(out);
		} catch (final IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
			return;
		}
		// start at 1, to make it easier for customer to read
		int sheetNum = 1;
		WritableSheet sheet = buildSheet(workbook, sheetNum, keys);

		final DateFormat customDateFormat = new DateFormat("yyyy MMM dd");
		final NumberFormat money = new NumberFormat("###,###,###.00");
		final WritableCellFormat dateFormat = new WritableCellFormat(customDateFormat);
		final WritableCellFormat moneyFormat = new WritableCellFormat(money);

		jxl.write.DateTime dt;
		// already built the sheet with headers on row 1, so start
		// on row 2
		int row = 2;
		if (rowsWithinBounds) {
			// Rows
			for (final G_Link r : lt.getResults()) {
				if ((row % 60000) == 0) {
					if (sheetNum == ExportMessages.MAX_SHEETS) {
						final ArrayList<String> warnings = new ArrayList<String>();
						warnings.add(ExportMessages.SHEETS_EXCEEDED_WARNING);
						warnings.add(ExportMessages.SUGGEST_CVS);
						sheet = buildWarningSheet(workbook, warnings);
						return;
					} else {
						sheet = buildSheet(workbook, ++sheetNum);
						// reset row to 2 if we build a new sheet.
						row = 2;
					}
				}
				if (sheetNum == ExportMessages.MAX_SHEETS) {
					// TODO:This is a bad way of doing it, refactor this entire
					// method later.
					break;
				}
				int columnNum = 1;
				for (final String k : keys) {

					try {
						final PropertyHelper prop = PropertyHelper.from(r.getProperties().get(k));
						switch (prop.getType()) {
						case DATE:
							dt = new DateTime(columnNum, row, (Date) prop.getValue(), dateFormat);
							sheet.addCell(dt);
							break;
						case LONG:
							sheet.addCell(new Label(columnNum, row, ((Long) prop.getValue()).toString()));
							break;
						case DOUBLE:
							sheet.addCell(new jxl.write.Number(columnNum, row, (Double) prop.getValue(), moneyFormat));
							break;
						case STRING:
							sheet.addCell(new Label(columnNum, row, ((String) prop.getValue())));
							break;
						default:
							break;
						}
						columnNum++;
					} catch (final RowsExceededException e) {
						logger.error("RowsExceededException during XLS export on row " + row + " of sheet " + sheetNum
								+ ": " + e.getMessage(), e);
					} catch (final WriteException e) {
						logger.error("WriteException during XLS export on row " + row + " of sheet " + sheetNum + ": "
								+ e.getMessage(), e);
					}
				}
				row++;
			}
		} else {
			final List<String> warnings = new ArrayList<String>();
			warnings.add(ExportMessages.RESULTS_EXCEEDED_WARNING);
			warnings.add(ExportMessages.RESULTS_EXCEEDED_WARNING);
			warnings.add(ExportMessages.SUGGEST_CONTACT_SUPPORT);

			// too many results, so the query wasn't even run
			sheet = buildWarningSheet(workbook, warnings);
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
