package graphene.util.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 
 * @author djue
 * 
 * @param <T>
 *            The class of object you are serializing, usually a POJO based on a
 *            DB call.
 */
public class KryoDiskCache<T> implements DiskCache<T> {

	private static final long FLUSH_THRESHOLD = 10000;

	@Inject
	private Logger logger;

	long numberOfRecordsCached = 0;

	private Kryo kryo;
	private Input input;
	private Output output;
	private Class<T> clazz;

	public KryoDiskCache() {
		kryo = new Kryo();

	}

	@Override
	public void init(Class<T> clazz) {
		kryo.register(clazz);
		this.clazz = clazz;
	}

	/**
	 * In DiskCache, this callback writes the object t to an output stream,
	 * usually a file.
	 */
	public boolean callBack(T t) {
		return write(t);
	}

	/**
	 * closes any input or output streams that are open.
	 */
	public void closeStreams() {
		if (output != null) {
			output.close();
		}
		if (input != null) {
			input.close();
		}
	}

	public boolean dropExisting(String fileName) {
		boolean deleted = false;
		try {
			logger.debug("Attempting to delete " + fileName);
			File f = new File(fileName);
			deleted = f.delete();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return deleted;
	}

	@Override
	public boolean initializeReader(String fileName) {
		input = null;
		File f = new File(fileName);
		if (f.exists() && f.length() > 0) {
			logger.debug("File found: " + fileName);
			try {
				input = new Input(new FileInputStream(fileName));
			} catch (FileNotFoundException e) {
				logger.error("Expected to find the file " + fileName);
			}
		} else {
			logger.debug("File not found: '" + fileName + "'");
		}

		return input != null ? true : false;
	}

	@Override
	public boolean initializeWriter(String fileName) {
		output = null;
		try {
			output = new Output(new FileOutputStream(fileName));
			numberOfRecordsCached = 0;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}
		return output != null ? true : false;
	}

	@Override
	public T read() {
		// logger.debug("Reading with Kryo");
		T t = null;
		if (!input.eof()) {
			t = kryo.readObject(input, clazz);
			// logger.debug("Read " + t);
		} else {
			logger.debug("EOF Reached");
		}

		return t;
	}

	public boolean write(T s) {
		kryo.writeObject(output, s);
		numberOfRecordsCached++;
		if (numberOfRecordsCached % FLUSH_THRESHOLD == 0) {
			output.flush();
		}
		return true;
	}

	@Override
	public long getNumberOfRecordsCached() {
		return numberOfRecordsCached;
	}

}
