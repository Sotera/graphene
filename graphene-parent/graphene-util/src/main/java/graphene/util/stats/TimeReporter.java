/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.util.stats;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;

public class TimeReporter {

	PeriodFormatter daysHoursMinutes = new PeriodFormatterBuilder()
			.appendDays().appendSuffix(" day", " days").appendSeparator(", ")
			.appendHours().appendSuffix(" hour", " hours")

			.appendSeparator(", ").appendMinutes()
			.appendSuffix(" minute", " minutes").appendSeparator(" and ")
			.appendSeconds().appendSuffix(" second", " seconds").toFormatter();
	long lastIntervalTime = 0;
	private Logger log;
	// Set when the reporter was created.
	private long startTime;

	// A string describing what we are doing. i.e. "Making Pizza"
	private String task = null;

	public TimeReporter() {
		init();
	}

	public TimeReporter(Logger logger) {
		log = logger;
		init();
	}

	public TimeReporter(String task, Logger logger) {
		this.task = task;
		log = logger;
		init();
	}

	public long getElapsed() {
		return System.currentTimeMillis() - startTime;
	}

	/**
	 * This method allows you to use the TimeReporter as an interval calculator.
	 * For example, each time you call getInterval, it will return the amount of
	 * time since you last called it.
	 * 
	 * @return the amount of time since getInterval has been called.
	 */
	public String getInterval() {
		StringBuilder b = new StringBuilder();
		long t2 = System.currentTimeMillis();
		long tdiff = t2 - lastIntervalTime;
		if (tdiff < 1000) {
			b.append("" + tdiff + "ms");
		} else {
			tdiff /= 1000; // now seconds
			if (tdiff > 3600) {
				b.append("" + tdiff / 3600 + " hours, ");
				tdiff = tdiff % 3600;
			}
			if (tdiff > 60) {
				b.append("" + tdiff / 60 + " minutes, ");
				tdiff = tdiff % 60;
			}

			b.append("" + tdiff + " seconds");
		}
		lastIntervalTime = t2;
		return b.toString();
	}

	/**
	 * 
	 * @param reportInterval
	 *            the number of ms to divide the time difference by.
	 * @return a String like "10ms per MyTask"
	 */
	public String getInterval(long reportInterval) {
		StringBuilder b = new StringBuilder();
		long t2 = System.currentTimeMillis();

		long tdiff = t2 - lastIntervalTime;
		tdiff /= reportInterval;
		if (tdiff < 1000) {
			b.append("" + tdiff + "ms per " + task);
		} else {
			tdiff /= 1000; // now seconds
			if (tdiff > 3600) {
				b.append("" + tdiff / 3600 + " hours, ");
				tdiff = tdiff % 3600;
			}
			if (tdiff > 60) {
				b.append("" + tdiff / 60 + " minutes, ");
				tdiff = tdiff % 60;
			}

			b.append("" + tdiff + " seconds per " + task);
		}
		lastIntervalTime = t2;
		return b.toString();
	}

	/**
	 * 
	 * @param numberOfItems
	 *            the number of things you've done so far. Does not update any
	 *            intervals
	 * @param unit
	 *            a String like "records" or "widgets"
	 * @return a String like " 34 records/ms"
	 */
	public String getSpeed(long numberOfItems, String unit) {
		return (numberOfItems / getElapsed()) + " " + unit + "/ms";
	}

	public String getTask() {
		return task;
	}

	private void init() {
		startTime = System.currentTimeMillis();
		lastIntervalTime = startTime;
		logAsStarting();
	}

	private void logAsStarting() {
		if (log != null) {
			if (task != null) {
				log.info(String.format("Task:" + task + " starting."));
			} else {
				log.info("Starting task.");
			}
		}

	}

	/**
	 * logs a message like "Task: MyTask finished in : 10ms"
	 */
	public void logAsCompleted() {
		String message = "Finished in ";
		if (log != null) {
			if (task != null) {
				log.info(String.format("Task:" + task + " " + message + ": "
						+ report()));
			} else {
				log.info(String.format(message + ": " + report()));
			}
		}
	}

	public void logAverageTime(long ntests) {
		long t2 = System.currentTimeMillis();
		long tdiff = t2 - lastIntervalTime;
		if (log != null) {
			if (task != null) {
				log.info(String.format("Task:" + task
						+ " average time for %d : %d ms", ntests, tdiff));
			} else {
				log.info(String.format("average time for %d : %d ms", ntests,
						tdiff));
			}
		}
	}

	/**
	 * logs a message like "Task: MyTask Request time : 10ms"
	 */
	public void logElapsed() {
		logElapsed("Request time");
	}

	public void logElapsed(int percentComplete) {
		logElapsed(Integer.toString(percentComplete) + " percent complete in");
	}

	/**
	 * logs a message like "Task: MyTask message : 10ms"
	 * 
	 * @param message
	 */
	public void logElapsed(String message) {
		if (log != null) {
			if (task != null) {
				log.info(String.format("Task:" + task + " " + message
						+ ": %d ms", getElapsed()));
			} else {
				log.info(String.format(message + ": %d ms", getElapsed()));
			}
		}
	}

	public void logEstimation(long estimatedTotal, long numberProcessed) {
		if (estimatedTotal <= numberProcessed) {
			log.info("Should be Completed");
		} else if (numberProcessed == 0) {
			log.info("inf");
		} else {
			double rate = getElapsed() / numberProcessed;
			double guess = ((estimatedTotal - numberProcessed) * rate) / 1000;
			int d = guess > 86400 ? (int) guess / 86400 : 0;
			guess %= 86400;
			int h = guess > 3600 ? (int) guess / 3600 : 0;
			guess %= 3600;
			int m = guess > 60 ? (int) guess / 60 : 0;
			guess %= 60;
			int s = (int) (guess > 0 ? guess : 0);
			Period period = new Period(d, h, m, s);

			log.info("Time remaining: "
					+ daysHoursMinutes.print(period.normalizedStandard()));
		}
	}

	/**
	 * 
	 * @return A string like "1 hours, 23 minutes and 10 seconds"
	 */
	public String report() {
		StringBuilder b = new StringBuilder();
		long tdiff = getElapsed();
		tdiff /= 1000; // now seconds
		if (tdiff > 3600) {
			b.append("" + tdiff / 3600 + " hours, ");
			tdiff = tdiff % 3600;
		}
		if (tdiff > 60) {
			b.append("" + tdiff / 60 + " minutes, ");
			tdiff = tdiff % 60;
		}

		b.append("" + tdiff + " seconds");

		return b.toString();
	}

	/**
	 * Useful for resetting the time for loops, or between longer serial
	 * processes
	 */
	public void reset() {
		startTime = System.currentTimeMillis();
		lastIntervalTime = startTime;
	}

	public void setTask(String task) {
		this.task = task;
	}
}
