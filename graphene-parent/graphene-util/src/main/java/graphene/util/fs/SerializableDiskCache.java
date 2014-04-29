package graphene.util.fs;

import java.io.Serializable;

/**
 * 
 * @author djue
 * 
 * @param <T>
 *            The class of object you are serializing, usually a POJO based on a
 *            DB call.  In this implementation T must extend Serializable.
 */
public class SerializableDiskCache<T extends Serializable> extends DiskCache<T> {

}
