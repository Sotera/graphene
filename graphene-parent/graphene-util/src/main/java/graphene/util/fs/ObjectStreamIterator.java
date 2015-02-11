package graphene.util.fs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A generic object stream iterator, for use in iterating over disk cache copies
 * of data tables.
 * 
 * @author djue
 * 
 * @param <T>
 */
public class ObjectStreamIterator<T> implements Iterator<T> {
	private final ObjectInputStream s;
	private boolean atEndOfFile = false;
	private final Logger logger = LoggerFactory.getLogger(ObjectStreamIterator.class);

	public ObjectStreamIterator(final ObjectInputStream s) {
		this.s = s;
	}

	public void close() {
		atEndOfFile = true;
		try {
			s.close();
		} catch (final IOException e) {
			logger.error(e.getMessage());
		}
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
		throw new UnsupportedOperationException("This method is not available for this iterator");
	}

}
