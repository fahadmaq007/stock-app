package com.maqbool.stock;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.query.JsonIndex;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.maqbool.stock.commons.Constants;
import com.maqbool.stock.commons.Util;
import com.maqbool.stock.dao.MapDeserializerDoubleAsIntFix;
import com.maqbool.stock.exceptions.ServiceException;

public class DataImporter {

	private static final String COUCH_DB_URL = "http://localhost:5984/";
	
	private static final String COUCH_DB = "stock1";

	private static final int BATCH_SIZE = 10000;

	private static final String TYPE = "type";

	private static final String DEFAULT_SEPARATOR = ",";

	private CloudantClient client;
	
	private String[][] indiciesOn = { { TYPE }, {"timestamp"} };
	
	private static final String BSE_FILE = "/BSE.csv";
	private static final String DJI_FILE =  "/DJI.csv";

	private static final String DATE_FORMAT_BSE = "dd-MMM-yy";
	private static final String DATE_FORMAT_DJI = "dd/MM/yy";
	
	public static void main(String[] args) {
		new DataImporter().run(args);
	}
	
	private String[] columns = { "timestamp:date", "open:double", "high:double", "low:double", "close:double" };
	private void run(String[] args) {
		try {
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(new TypeToken<Map>(){}.getType(),  new MapDeserializerDoubleAsIntFix());
			client =  ClientBuilder.url(new URL(COUCH_DB_URL))
					.gsonBuilder(builder)
					.build();
			String resourcesPath = getClass().getResource("/").getPath(); 
			
			importFile("bse", new File(resourcesPath + BSE_FILE), columns, DEFAULT_SEPARATOR, DATE_FORMAT_BSE);
			importFile("dji", new File(resourcesPath + DJI_FILE), columns, DEFAULT_SEPARATOR, DATE_FORMAT_DJI);
			
			System.exit(0);
		} catch (Exception e) {
			System.err.println("Error!!" +  e.getMessage());
		}
	}
	
	private void importFile(String type, File file, String[] columns, String separator, String dateFormat)
			throws Exception {
		System.out.println("about to import data from " + file.getName());
		LineIterator it = null;
		List<Map> documents = new ArrayList<Map>();
		try {
			it = FileUtils.lineIterator(file, "UTF-8");
			int batchCount = 1;
			
			Database db = getDb(COUCH_DB);

			while (it.hasNext()) {
				String line = it.nextLine();
				String[] columnsData = line.split(Pattern.quote(separator));
				if (columnsData == null || columnsData.length < columns.length) {
					continue;
				}
				int i = 0;
				Map document = getDocument(type);
				for (String col : columns) {
					int index = col.indexOf(Constants.COLAN);
					if (index > -1) {
						String colType = col.substring(index + 1);
						col = col.substring(0, index);
						if (colType.equalsIgnoreCase("date")) {
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
							//convert String to LocalDate
							LocalDate localDate = LocalDate.parse(columnsData[i], formatter);
							long millis = localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
							document.put(col, millis);
						} else if (colType.equalsIgnoreCase("int")) {
							document.put(col, Integer.valueOf(columnsData[i]));
						} else if (colType.equalsIgnoreCase("double")) {
							document.put(col, Double.valueOf(columnsData[i]));
						}
					} else {
						String data = columnsData[i];
						document.put(col, data);
					}
					i++;
				}
				documents.add(document);
				int size = documents.size();
				if (size % BATCH_SIZE == 0) {
					System.out.println("uploading batch#" + batchCount + " of " + BATCH_SIZE);
					bulkUpsert(db, documents);
					documents = new ArrayList<Map>();
					batchCount++;
				}
			}
			if (!Util.nullOrEmpty(documents)) {
				System.out.println("last batch#" + batchCount + " of " + documents.size());
				bulkUpsert(db, documents);
			}
		} catch (Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} finally {
			if (it != null)
				LineIterator.closeQuietly(it);
		}
	}
	private Map<String, Object> getDocument(String type) {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put(Constants.TYPE, type);
		return content;
	}

	private void save(Database db, Map document) throws Exception {
		db.save(document); 
	}
	
	private int bulkUpsert(Database db, List<Map> documents) throws Exception {
		List<Response> bulkResponse = db.bulk(documents); 
		int size = bulkResponse.size();
		return size;
	}

	private Database getDb(String ds) {
		Database db = null;
		try {
			client.createDB(ds);
			db = client.database(ds, false);
			runTasksPostDbCreation(db);
		} catch (Exception e) {
			db = client.database(ds, false);
		}
		
		return db;
	}

	private void runTasksPostDbCreation(Database db) {
		for (String[] fields : indiciesOn) {
			String indexDefinition = JsonIndex.builder().asc(fields).definition();
			db.createIndex(indexDefinition);
		}
	}
}