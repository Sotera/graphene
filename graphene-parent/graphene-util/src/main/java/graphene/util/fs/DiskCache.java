package graphene.util.fs;

import graphene.util.G_CallBack;
import graphene.util.stats.TimeReporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

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
public class DiskCache<T> implements G_CallBack<T> {

	private FileOutputStream f_out;
	@Inject
	private Logger logger;

	public Logger getLogger() {
		return logger;
	}

	long numObjects = 0;

	private ObjectOutputStream obj_out;

	public DiskCache() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * In DiskCache, this callback writes the object t to an output stream,
	 * usually a file.
	 */
	public boolean callBack(T t) {

		// Write object with ObjectOutputStream
		try {
			obj_out.writeObject(t);
			numObjects++;
			if (numObjects % 100 == 0) {
				obj_out.flush();
				obj_out.reset();
			}
			t = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void closeStreams() {
		if (obj_out != null) {
			try {
				obj_out.reset();
				obj_out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (f_out != null) {
			try {
				f_out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Use this to get a stream to write objects to.
	 * 
	 * @param fileName
	 * @return a stream to write objects to
	 */
	public ObjectOutputStream getObjectStream(String fileName) {
		obj_out = null;
		f_out = null;
		try {
			// Write to disk with FileOutputStream
			f_out = new FileOutputStream(fileName);

			// Write object with ObjectOutputStream
			obj_out = new ObjectOutputStream(f_out);
			return obj_out;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			closeStreams();
		} finally {
			// don't close streams here, because it will be called before
			// returning the valid stream
		}
		return null;
	}

	public boolean dropExisting(String fileName) {
		File f = new File(fileName);
		return f.delete();
	}

	/**
	 * Returns a stream (usually from a file) which you can then iterate over,
	 * etc. Usually used in conjuction with ObjectStreamIterator T.
	 * 
	 * @param fileName
	 * @return
	 */
	public ObjectInputStream restoreFromDisk(String fileName) {
		logger.debug("Attempting to restore from disk");
		// Read from disk using FileInputStream
		FileInputStream f_in = null;
		ObjectInputStream obj_in = null;

		try {
			File f = new File(fileName);
			if (f.exists() && f.length() > 0) {
				logger.debug("File found: " + fileName);
				f_in = new FileInputStream(fileName);

				// Read object using ObjectInputStream
				logger.debug("Reading in file, this may take a few minutes...");
				obj_in = new ObjectInputStream(f_in);
				// if (obj_in..available() <= 0) {
				// return null;
				// }
				// Read an object
				// obj = obj_in.readObject();
			} else {
				logger.debug("File not found: " + fileName);
			}

		} catch (FileNotFoundException e2) {
			logger.error("Expected to find the file " + fileName);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return obj_in;
	}

	public boolean saveToDisk(Stack<T> s, String fileName) {
		TimeReporter tr = new TimeReporter("Saving to disk", logger);
		ObjectOutputStream obj_out = null;
		FileOutputStream f_out = null;
		boolean success = false;
		try {
			// Write to disk with FileOutputStream
			f_out = new FileOutputStream(fileName);

			// Write object with ObjectOutputStream
			obj_out = new ObjectOutputStream(f_out);

			// Write object out to disk
			obj_out.writeObject(s);
			success = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (obj_out != null) {
				try {
					obj_out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (f_out != null) {
				try {
					f_out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		tr.logElapsed(100);
		return success;
	}

}
