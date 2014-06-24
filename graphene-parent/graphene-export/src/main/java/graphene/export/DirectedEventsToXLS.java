package graphene.export;

import graphene.model.view.events.DirectedEvents;
import graphene.model.view.events.DirectedEventRow;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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

public class DirectedEventsToXLS {
	private static Logger logger = LoggerFactory
			.getLogger(DirectedEventsToXLS.class);

	public void toXLS(DirectedEvents lt, OutputStream out,
			boolean rowsWithinBounds) {
		WritableWorkbook workbook;
		try {
			workbook = Workbook.createWorkbook(out);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
			return;
		}
		// start at 1, to make it easier for customer to read
		int sheetNum = 1;
		WritableSheet sheet = buildSheet(workbook, sheetNum);

		DateFormat customDateFormat = new DateFormat("yyyy MMM dd");
		NumberFormat money = new NumberFormat("###,###,###.00");
		WritableCellFormat dateFormat = new WritableCellFormat(customDateFormat);
		WritableCellFormat moneyFormat = new WritableCellFormat(money);

		Label account;
		Label partics;
		double deb;
		double cred;
		jxl.write.Number debit;
		jxl.write.Number credit;
		jxl.write.DateTime dt;
		// already built the sheet with headers on row 1, so start
		// on row 2
		int row = 2;
		if (rowsWithinBounds) {
			// Rows
			for (DirectedEventRow r : lt.getRows()) {
				if (row % 60000 == 0) {
					if (sheetNum == ExportMessages.MAX_SHEETS) {
						ArrayList<String> warnings = new ArrayList<String>();
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
				org.joda.time.DateTime testd = new org.joda.time.DateTime(
						r.getDateMilliSeconds());
				dt = new DateTime(0, row, testd.toDate(), dateFormat);
				account = new Label(1, row, r.getSenderId());

				//deb = r.getDebitAsDouble();
				//TODO: Unit test this to make sure the Money format is parseable as a Double
				deb = Double.valueOf(r.getCredit());
				debit = new jxl.write.Number(2, row, deb, moneyFormat);
				cred = Double.valueOf(r.getDebit());
				//cred = r.getCreditAsDouble();
				credit = new jxl.write.Number(3, row, cred, moneyFormat);
				// TODO: Finish this conversion to pairs
				partics = new jxl.write.Label(4, row, r.getComments());

				try {
					sheet.addCell(dt);
					sheet.addCell(account);
					if (deb != 0) {
						sheet.addCell(debit);
					}
					if (cred != 0) {
						sheet.addCell(credit);
					}
					sheet.addCell(partics);
				} catch (RowsExceededException e) {
					logger.error(
							"RowsExceededException during XLS export on row "
									+ row + " of sheet " + sheetNum + ": "
									+ e.getMessage(), e);
				} catch (WriteException e) {
					logger.error(
							"WriteException during XLS export on row " + row
									+ " of sheet " + sheetNum + ": "
									+ e.getMessage(), e);
				}

				++row;
			}
		} else {
			List<String> warnings = new ArrayList<String>();
			warnings.add(ExportMessages.RESULTS_EXCEEDED_WARNING);
			warnings.add(ExportMessages.RESULTS_EXCEEDED_WARNING);
			warnings.add(ExportMessages.SUGGEST_CONTACT_SUPPORT);

			// too many results, so the query wasn't even run
			sheet = buildWarningSheet(workbook, warnings);
		}
		try {
			workbook.write();
			workbook.close();
		} catch (WriteException e1) {
			logger.error(
					"WriteException during XLS export: " + e1.getMessage(), e1);
		} catch (IOException e2) {
			logger.error("IOException during XLS export: " + e2.getMessage(),
					e2);
		}

	}

	private WritableSheet buildWarningSheet(WritableWorkbook workbook,
			List<String> warnings) {
		/**
		 * Using sheet number 0 so it ends up as the final sheet.
		 */
		WritableSheet sheet = workbook.createSheet("WARNING ", 0);

		sheet.setColumnView(0, 25);

		int row = 0;

		WritableCellFormat labelFormat = new WritableCellFormat();
		try {
			labelFormat.setAlignment(jxl.format.Alignment.CENTRE);
		} catch (WriteException e4) {
			logger.error("WriteException during labelFormat.setAlignment: "
					+ e4.getMessage(), e4);
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

		} catch (RowsExceededException e) {
			logger.error("RowsExceededException during XLS export on row "
					+ row + ": " + e.getMessage(), e);
		} catch (WriteException e) {
			logger.error("WriteException during XLS export on row " + row
					+ ": " + e.getMessage(), e);
		}

		return sheet;
	}

	/**
	 * @param workbook
	 * @param sheetNum
	 * @return
	 */
	private WritableSheet buildSheet(WritableWorkbook workbook, int sheetNum) {
		/**
		 * Why use MAX_SHEETS + 1? Because using a sheet number greater than the
		 * number of sheets will guarantee that new sheets get put at the end
		 * (appended). SheetNum is more for label use than for the actual 0
		 * based sheet index.
		 */
		WritableSheet sheet = workbook.createSheet("Sheet  " + sheetNum,
				ExportMessages.MAX_SHEETS + 1);

		sheet.setColumnView(0, 12);
		sheet.setColumnView(1, 15);
		sheet.setColumnView(2, 13);
		sheet.setColumnView(3, 13);
		sheet.setColumnView(4, 50);

		int row = 0;

		WritableCellFormat labelFormat = new WritableCellFormat();
		try {
			labelFormat.setAlignment(jxl.format.Alignment.CENTRE);
		} catch (WriteException e4) {
			logger.error("WriteException during labelFormat.setAlignment: "
					+ e4.getMessage(), e4);
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

		} catch (RowsExceededException e) {
			logger.error("RowsExceededException during XLS export on row "
					+ row + ": " + e.getMessage(), e);
		} catch (WriteException e) {
			logger.error("WriteException during XLS export on row " + row
					+ ": " + e.getMessage(), e);
		}
		sheet.getSettings().setVerticalFreeze(1);
		return sheet;
	}
}
