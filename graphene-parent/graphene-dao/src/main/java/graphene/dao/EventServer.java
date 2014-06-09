package graphene.dao;

import graphene.model.query.EventQuery;
import graphene.model.view.events.DirectedEvents;

public interface EventServer {

	public abstract DirectedEvents getEvents(EventQuery q);

}