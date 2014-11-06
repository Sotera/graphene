package graphene.export;

import graphene.model.view.events.SingleSidedEventRow;
import graphene.model.view.events.SingleSidedEvents;

public class SingleSidedEventsToCSV {
	public String toCSV(SingleSidedEvents lt) {
		// String eol = System.getProperty("line.separator");
		String eol = "\r\n"; // users want windows format - the above would be
								// UNIX

		StringBuilder result = new StringBuilder();

		result.append("date,");
		result.append("account,");
		result.append("debit,");
		result.append("credit,");
		result.append("comments");
		result.append(eol);

		for (SingleSidedEventRow r : lt.getRows()) {
			org.joda.time.DateTime testd = new org.joda.time.DateTime(
					r.getDateMilliSeconds());
			result.append(testd.toString() + ",");
			// result.append("\"");
			result.append(r.getAccount());
			// result.append("\"");
			result.append(",");

			// Remove commas from amounts. Replace commas in comments with
			// spaces

			if (r.getDebit() != null) {
				result.append(r.getDebit().replace(",", "") + ",");
			} else {
				result.append(",");
			}
			if (r.getCredit() != null) {
				result.append(r.getCredit().replace(",", "") + ",");
			} else {
				result.append(",");
			}

			if (r.getComments() != null) {
				result.append(r.getComments().replace(",", " "));
			} else {
				// last column, no append.
			}

			result.append(eol);
		}

		return result.toString();

	}
}
