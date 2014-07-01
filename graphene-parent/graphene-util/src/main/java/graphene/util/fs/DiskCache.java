package graphene.util.fs;

import graphene.util.G_CallBack;

/**
 * @author djue
 * 
 * @param <T>
 */
public interface DiskCache<T> extends G_CallBack<T> {
	/**
	 * Close any file streams
	 */
	public abstract void closeStreams();

	/**
	 * Perform the act of deleting the existing cache file
	 * 
	 * @param fileName the name of the cache file to delete
	 * @return true if the delete succeeded, false otherwise.
	 */
	public abstract boolean dropExisting(String fileName);

	/**
	 * 
	 * @return
	 */
	public long getNumberOfRecordsCached();

	/**
	 * 
	 * @return
	 */
	public abstract T read();

	/**
	 * 
	 * @param s
	 * @return
	 */
	public abstract boolean write(T s);

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public abstract boolean initializeWriter(String fileName);

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public abstract boolean initializeReader(String fileName);

	/**
	 * Just registers the class if needed.
	 * 
	 * @param clazz
	 *            the class you are going to serialize
	 */
	void init(Class<T> clazz);

}