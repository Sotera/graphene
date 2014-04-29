package graphene.util.fs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

/**
 * A generic object stream iterator, for use in iterating over disk cache copies
 * of data tables.
 * 
 * @author djue
 * 
 * @param <T>
 */
public class ObjectStreamIterator<T> implements Iterator<T> {
	private ObjectInputStream s;
	private boolean atEndOfFile = false;

	public ObjectStreamIterator(ObjectInputStream s) {
		this.s = s;
	}

	@Override
	public boolean hasNext() {
		return !atEndOfFile;
	}

	@Override
	public T next() {
		if (s != null) {
			try {
				return (T) s.readObject();
			} catch (ClassNotFoundException | IOException e) {
				close();
			}
		}
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"This method is not available for this iterator");
	}

	public void close() {
		atEndOfFile = true;
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
