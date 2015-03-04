package graphene.util;


public interface G_CallBack<T,Q> {
	//TODO: Change method name to execute
	public boolean callBack(T t);
	public boolean callBack(T t, Q q);

}
