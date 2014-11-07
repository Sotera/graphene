package graphene.export;

import graphene.model.view.events.DirectedEvents;
import graphene.model.view.events.DirectedEventRow;

public class DirectedEventsToCSV {
	public String toCSV(DirectedEvents lt) {
		// String eol = System.getProperty("line.separator");
		String eol = "\r\n"; // users want windows format - the above would be
								// UNIX

		StringBuilder result = new StringBuilder();
		result.append("date,");
		result.append("sender,");
		result.append("receiver,");
		result.append("debit,");
		result.append("credit,");
		result.append("comments");
		result.append(eol);
		for (DirectedEventRow r : lt.getRows()) {
			org.joda.time.DateTime testd = new org.joda.time.DateTime(
					r.getDateMilliSeconds());
			result.append(testd.toString() + ",");
			result.append(r.getSenderId());
			result.append(",");
			result.append(r.getReceiverId());
			result.append(",");
			// Remove commas from amounts. Replace commas in comments with
			// spaces
			if (r.getDebit() != null) {
				result.append(r.getDebit().replaceAll(",", ""));
			}
			result.append(",");

			if (r.getCredit() != null) {
				result.append(r.getCredit().replaceAll(",", ""));
			}
			result.append(",");

			if (r.getComments() != null) {
				result.append(r.getComments().replaceAll(",", " "));
			} else {
				// last column, no append.
			}
			result.append(eol);
		}

		return result.toString();

	}
}
