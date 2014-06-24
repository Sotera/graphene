package graphene.export;

import graphene.model.view.events.DirectedEvents;
import graphene.model.view.events.DirectedEventRow;

public class DirectedEventsToCSV {
	public String toCSV(DirectedEvents lt) {
		// String eol = System.getProperty("line.separator");
		String eol = "\r\n"; // users want windows format - the above would be
								// UNIX

		StringBuilder result = new StringBuilder();

		for (DirectedEventRow r : lt.getRows()) {

			result.append(r.getDate() + ",");
			// result.append("\"");
			result.append(r.getSenderId());
			// result.append("\"");
			result.append(",");
			result.append(r.getReceiverId());
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
