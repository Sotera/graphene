/**
 * 
 */
package graphene.web.model;

import graphene.dao.TransactionDAO;
import graphene.model.view.events.DirectedEventRow;
import graphene.util.FastNumberUtils;

import org.apache.tapestry5.ValueEncoder;

/**
 * @author djue
 * 
 */
public class EventEncoder implements ValueEncoder<DirectedEventRow> {
	private TransactionDAO dao;

	public EventEncoder(TransactionDAO dao) {
		this.dao = dao;
	}

	@Override
	public String toClient(DirectedEventRow value) {
		if (value == null) {
			return "unknown";
		}
		return String.valueOf(value.getId());
	}

	@Override
	public DirectedEventRow toValue(String clientValue) {
		return dao.findEventById(clientValue);
	}
}
