package graphene.export;

import graphene.model.view.events.SingleSidedEventRow;
import graphene.model.view.events.SingleSidedEvents;

public class SingleSidedEventsToCSV {
	public String toCSV(SingleSidedEvents lt) {
		// String eol = System.getProperty("line.separator");
		String eol = "\r\n"; // users want windows format - the above would be
								// UNIX

		StringBuilder result = new StringBuilder();

		for (SingleSidedEventRow r : lt.getRows()) {

			result.append(r.getDate() + ",");
			// result.append("\"");
			result.append(r.getAccount());
			// result.append("\"");
			result.append(",");

			// Remove commas from amounts. Replace commas in comments with
			// spaces

			result.append(r.getDebit().replace(",", "") + ",");
			result.append(r.getCredit().replace(",", "") + ",");

			result.append(r.getComments().replace(",", " "));

			result.append(eol);
		}

		return result.toString();

	}
}
