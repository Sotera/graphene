package graphene.model.extracted;

import graphene.util.Collections;
import graphene.util.DataFormatConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrossReferenceResult {

	Map<String, Map<String, Integer>> types;
	String jobTitle;
	long dateMillis;
	String dateISO;

	private final Logger logger = LoggerFactory.getLogger(CrossReferenceResult.class);

	public CrossReferenceResult() {

		// TODO Auto-generated constructor stub
		types = new HashMap<String, Map<String, Integer>>();
	}

	/**
	 * @return the dateISO
	 */
	public String getDateISO() {
		return dateISO;
	}

	/**
	 * @return the dateMillis
	 */
	public long getDateMillis() {
		return dateMillis;
	}

	/**
	 * @return the jobTitle
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @return the types
	 */
	public Map<String, Map<String, Integer>> getTypes() {
		return types;
	}

	/**
	 * @param dateISO
	 *            the dateISO to set
	 */
	public void setDateISO(final String dateISO) {
		this.dateISO = dateISO;
	}

	/**
	 * @param dateMillis
	 *            the dateMillis to set
	 */
	public void setDateMillis(final long dateMillis) {
		this.dateMillis = dateMillis;
	}

	/**
	 * @param jobTitle
	 *            the jobTitle to set
	 */
	public void setJobTitle(final String jobTitle) {
		this.jobTitle = jobTitle;
	}

	/**
	 * @param types
	 *            the types to set
	 */
	public void setTypes(final Map<String, Map<String, Integer>> types) {
		this.types = types;
	}

	public void writeToFile(final File f) throws IOException {
		logger.debug("Creating output file for crossreference at " + f.getAbsolutePath());
		final FileOutputStream fos = new FileOutputStream(f);
		// if (!f.exists()) {
		// always overwrite
		f.createNewFile();
		// }

		fos.write(("Crossreference Results for  " + jobTitle + " on "
				+ DataFormatConstants.formatDate(DateTime.now().getMillis()) + "\n========================================\n\n")
				.toString().getBytes());
		for (final String k : types.keySet()) {
			fos.write(("\n\nMost prolific " + k + "\n====================\n").toString().getBytes());
			final Map<String, Integer> sortedMap = Collections.sortByComparator(types.get(k), false, 1);

			for (final Entry<String, Integer> s : sortedMap.entrySet()) {
				fos.write((s.getKey() + "\t" + s.getValue() + "\n").getBytes());
			}
		}
		fos.close();
	}
}