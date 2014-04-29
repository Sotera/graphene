package graphene.dao.neo4j.advice;

import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.annotations.Neo4JTransactional;

import java.lang.reflect.Method;

import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.neo4j.graphdb.Transaction;

public class Neo4JTransactionalAdvisorImpl implements Neo4JTransactionalAdvisor {

	private final Neo4JEmbeddedService s;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry5.neo4j.Neo4JTransactionalAdvisor#
	 * addTransactionCommitAdvice(org.apache.tapestry5.ioc.MethodAdviceReceiver)
	 */
	@Override
	public void addTransactionAdvice(MethodAdviceReceiver receiver) {
		int numberAdvised = 0;
		System.out.println("Starting advice");
		for (Method m : receiver.getInterface().getMethods()) {
			if (m.getAnnotation(Neo4JTransactional.class) != null) {
				System.out.println("Method "+m.getName() +" was marked with Neo4JTransactional, Advising now");
				receiver.adviseMethod(m, advice);
			}else{
				System.out.println("Method "+m.getName() +" was not marked with Neo4JTransactional");
			}
			numberAdvised++;
		}
		if (numberAdvised == 0) {
			System.out
					.println("No particular methods were annotated, so advising all methods.");
			// this won't work well if we have more than one graph to have tx
			// on. In that case we want some methods to tx on one graph and
			// another to tx on the second graph
			receiver.adviseAllMethods(advice);
		}
	}

	public Neo4JTransactionalAdvisorImpl(Neo4JEmbeddedService s) {
		this.s = s;
	}

	private final MethodAdvice advice = new MethodAdvice() {
		public void advise(MethodInvocation invocation) {
			System.out.println("*************About to start a tx");
			try (Transaction tx = s.getGraphDb().beginTx()) {
				System.out.println("*************In a tx");
				invocation.proceed();
				// If the tx failed for some reason, the next call to
				// tx.success() won't do anything, and won't hurt anything,
				// AFAICT.
				// TODO: Test for behavior in nested tx.
				tx.success();
				System.out
						.println("*************performed method, still In a tx");
			} catch (Exception e) {
				System.out.println("*************tx failed:" + e.getMessage());

				throw new RuntimeException("Failed transaction", e);
			}
		}
	};
}
