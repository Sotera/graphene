package graphene.util.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * 
 * @author djue
 * 
 * @param <T>
 *            The class of object you are serializing, usually a POJO based on a
 *            DB call.
 */
public class JavaDiskCache<T> implements DiskCache<T> {
	@Inject
	private Logger logger;

	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Class<T> clazz;

	private int numberOfRecordsCached;

	public JavaDiskCache() {
		// TODO Auto-generated constructor stub
	}

	public boolean callBack(T t) {
		return write(t);
	}

	@Override
	public void closeStreams() {
		if (output != null) {
			try {
				output.reset();
				output.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.util.fs.DiskCache#dropExisting(java.lang.String)
	 */
	@Override
	public boolean dropExisting(String fileName) {
		File f = new File(fileName);
		return f.delete();
	}

	public boolean initializeWriter(String fileName) {
		output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(fileName));
			numberOfRecordsCached = 0;
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return output != null ? true : false;
	}

	public boolean initializeReader(String fileName) {
		input = null;
		File f = new File(fileName);
		if (f.exists() && f.length() > 0) {
			logger.debug("File found: " + fileName);
			try {
				input = new ObjectInputStream(new FileInputStream(fileName));
			} catch (FileNotFoundException e) {
				logger.error("Expected to find the file " + fileName + " "
						+ e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.error("File not found: " + fileName);
		}

		return input != null ? true : false;
	}

	@Override
	public T read() {
		T t = null;
		try {
			t = (T) input.readObject();
		} catch (ClassNotFoundException | IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return t;
	}

	public boolean write(T s) {
		boolean success = false;
		try {
			output.writeObject(s);
			numberOfRecordsCached++;
			success = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public long getNumberOfRecordsCached() {
		return numberOfRecordsCached;
	}

	@Override
	public void init(Class<T> clazz) {
		// TODO Auto-generated method stub
		
	}

}
