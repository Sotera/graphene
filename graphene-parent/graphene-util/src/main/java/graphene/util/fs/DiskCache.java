package graphene.util.fs;

import graphene.util.G_CallBack;

import java.io.ObjectInputStream;
import java.util.Stack;

public interface DiskCache<T> extends G_CallBack<T> {

	public abstract void closeStreams();

	public abstract boolean dropExisting(String fileName);

	public long getNumberOfRecordsCached();

	public abstract T read();

	public abstract boolean write(T s);

	public abstract boolean initializeWriter(String fileName);

	public abstract boolean initializeReader(String fileName);

	void init(Class<T> clazz);

}