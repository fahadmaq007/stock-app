package com.maqbool.stock.commons;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Utility class with common operations.
 * 
 * @author maqboolahmed
 *
 */
public class Util {

	private static Logger logger = LoggerFactory.getLogger(Util.class);

	private static final Set<String> periods = new HashSet<String>();
	
	static {
		periods.add("1M");periods.add("2M");periods.add("3M");periods.add("6M");
		periods.add("1Y");periods.add("2Y");periods.add("3Y");periods.add("4Y");periods.add("5Y");
	}

	public static void displayJson(Object src) {
		Gson gson = new Gson();
		logger.debug(gson.toJson(src));
	}

	/**
	 * Generates the json string.
	 * 
	 * @param src
	 *            source object to be converted.
	 * @return json string
	 */
	public static String toJson(Object src) {
		Gson gson = new Gson();
		return gson.toJson(src);
	}

	/**
	 * Generates the entity of a given class from json.
	 * 
	 * @param json
	 *            json string
	 * @param clazz
	 *            entity's class to be converted to.
	 * @return entity
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}

	/**
	 * Checks whether given collection is not null or non-empty.
	 * 
	 * @param c
	 *            Colletion instance
	 * @return true if null or empty, otherwise false;
	 */
	public static boolean nullOrEmpty(@SuppressWarnings("rawtypes") Collection c) {
		return c == null || c.isEmpty();
	}

	/**
	 * Checks whether the passed string is a null or empty.
	 * 
	 * @param text
	 *            to check
	 * @return true is null or empty, otherwise false.
	 */
	public static boolean nullOrEmpty(String text) {
		return text == null || text.length() == 0;
	}

	/**
	 * Returns the random UUID.
	 * 
	 * @return
	 */
	public static String newUuid() {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

	public static long convertPeriodToTimestamp(String period) {
		ZonedDateTime now = ZonedDateTime.now();
		long millis = now.minusMonths(1).toInstant().toEpochMilli();
		System.out.println(period + " " + millis + " " + periods.contains(period));
		if (periods.contains(period)) {
			int num = Integer.parseInt("" + period.charAt(0));
			char duration = period.charAt(1);
			switch (duration) {
			case 'M': 
				System.out.println(num + " months");
				millis = now.minusMonths(num).toInstant().toEpochMilli(); break;
			
			case 'Y': 
				System.out.println(num + " years");
				millis = now.minusYears(num).toInstant().toEpochMilli(); break;
			}
		}
		return millis;
	}
}