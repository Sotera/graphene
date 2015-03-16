package graphene.dao;

/**
 * This interface is for defining things you want to happen when the application
 * first starts. For instance, you may want to make sure an admin account exists
 * or that some other service is available.
 * 
 * You may also choose to use the NoOp implementation where nothing gets done.
 * 
 * @author djue
 * 
 */
public interface StartupProcedures {

	public boolean initialize();
}
