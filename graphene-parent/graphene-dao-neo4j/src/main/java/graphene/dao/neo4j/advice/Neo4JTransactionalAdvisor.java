package graphene.dao.neo4j.advice;

import org.apache.tapestry5.ioc.MethodAdviceReceiver;

public interface Neo4JTransactionalAdvisor {

	public abstract void addTransactionAdvice(
			MethodAdviceReceiver receiver);

}