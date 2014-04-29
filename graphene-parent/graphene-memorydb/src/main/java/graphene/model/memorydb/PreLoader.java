package graphene.model.memorydb;

/**
 * See notes in the sole implementation. This class may soon be deprecated or
 * renamed.
 * 
 * @author PWG
 * 
 */
public interface PreLoader {

	/**
	 * init function called by Spring when loads
	 * 
	 * @param postgres
	 */
	public abstract void initialize();

	public abstract boolean isLoaded();

	public abstract void setLoadAll(boolean loadAll);

	public abstract void setLoadTypeNames(boolean load);

	public abstract void setindexAll(boolean index);

	public abstract void setIndexNames(boolean loadNames);

	// for Spring
	public abstract void setIndexAddresses(boolean load);

	public abstract boolean isIndexPhones();

	public abstract void setIndexPhones(boolean indexPhones);

	public abstract long getMaxEntries();

	public abstract void setMaxEntries(int maxEntries);

}