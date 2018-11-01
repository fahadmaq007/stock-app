package com.maqbool.stock.dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.query.JsonIndex;
import com.cloudant.client.api.query.Operation;
import com.cloudant.client.api.query.QueryResult;
import com.cloudant.client.api.query.Selector;
import com.cloudant.client.internal.query.Helpers;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.maqbool.stock.commons.PropertySpec;
import com.maqbool.stock.commons.Util;
import com.maqbool.stock.exceptions.DataAccessException;

/**
 * Implementation of the Repository interface that uses the synchronous API
 * exposed by the Couchbase Java SDK.
 * 
 * @author maqboolahmed
 */
@org.springframework.stereotype.Repository
public class CouchDao implements IDao {
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("${couch.url:http://localhost:5984/}")
	private String nodeUrl;
	
	private CloudantClient client;

	private String dataSource = "stock";
	
	private String[] indiciesOn = { "type", "timestamp" };
	
	private static final Class MAP_CLASS = Map.class;
	
	public void setNodeUrl(String nodeUrl) {
		this.nodeUrl = nodeUrl;
	}
	
	public String getNodeUrl() {
		return nodeUrl;
	}
	
	@PostConstruct
	public void initIt() throws Exception {
		logger.info("CouchDao client instance is getting created & will connect to ... " + nodeUrl);
		try {
			GsonBuilder builder = new GsonBuilder();
			builder.registerTypeAdapter(new TypeToken<Map>(){}.getType(),  new MapDeserializerDoubleAsIntFix());
			client =  ClientBuilder.url(new URL(nodeUrl))
					.gsonBuilder(builder)
					.build();
		} catch (Exception e) {
			logger.error("Either cluster creation or bucket open operation failed!!");
			throw new DataAccessException(e);
		}
	}
	
	@Override
	public List<Map<String, Object>> listDocuments(
			String[] filters, String[] sortParams,
			Integer page, Integer limit) throws DataAccessException {
		return listDocuments(filters, sortParams, null, page, limit);
	}

	@Override
	public List<Map<String, Object>> listDocuments(
			String[] filters, String[] sortParams, String[] fields,
			Integer page, Integer limit) throws DataAccessException {
		logger.info("datasource is " + this.dataSource);
		Database db = getDb(this.dataSource);
		if (filters != null) {
			String indexDefinition = CouchQueryTranslator.buildIndexDefinition(filters);
			db.createIndex(indexDefinition);
		}
		
		List<PropertySpec> specs = CouchQueryTranslator.getPropertySpecs(filters);
		List<List<PropertySpec>> splitByInPropertySpec = CouchQueryTranslator.splitInSpecs(specs);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (List<PropertySpec> eachSplitPropSpec : splitByInPropertySpec) {
			List<Map<String, Object>> list = listDocuments0(eachSplitPropSpec, sortParams, fields, page, limit);
			if (! Util.nullOrEmpty(list)) {
				if (resultList.isEmpty()) {
					resultList = list;
				} else {
					resultList.addAll(list);
				}
			}
		}
		
		return resultList;
	}
	
	private List<Map<String, Object>> listDocuments0(List<PropertySpec> specs, String[] sort, String[] fields, 
			Integer page, Integer limit) throws DataAccessException {
		try {
			logger.info("datasource is " + this.dataSource);
			Database db = getDb(this.dataSource);
			JsonObject indexObject = getIndexObject(specs, sort, fields, page, limit);
			
			String query = indexObject.toString();
			logger.info("query is: " + query);
			QueryResult<Map<String, Object>> result = db.query(query, MAP_CLASS);
			return result.getDocs();
		} catch (Exception e) {
			throw new DataAccessException(e);
		}
	}
	
	private JsonObject getIndexObject(List<PropertySpec> specs, String[] sort, String[] fields, Integer page, Integer limit) {
		JsonObject indexObject = new JsonObject();
		
		Selector selector = null;
		List<Selector> selectors = CouchQueryTranslator.buildSelector(specs);
		if (selectors.size() > 1) {
			selector = Operation.and(selectors.toArray(new Selector[selectors.size()]));
		} else {
			selector = selectors.get(0);
		}
		JsonObject selectorObj = Helpers.getJsonObjectFromSelector(selector);
		indexObject.add("selector", selectorObj);
		
		if (sort != null) {
			JsonArray sortArray = CouchQueryTranslator.buildSort(sort);
			indexObject.add("sort", sortArray);
		}

		if (fields != null && fields.length > 0) {
			JsonArray fieldsArray = CouchQueryTranslator.buildFields(fields);
			indexObject.add("fields", fieldsArray);
		}
		if (limit != null) {
			indexObject.addProperty("limit", limit);
		}
		if (page != null) {
			indexObject.addProperty("skip", (page - 1) * limit);
		}
		return indexObject;
	}

	protected String stringyfy(String[] values) {
		String array = "";
		for (int i = 0, n = values.length; i < n; i++) {
			array += "'" + values[i] + "'";
			if (i < n - 1) {
				array += ",";
			}
		}
		return "[" + array + "]";
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
		for (String indexOn : indiciesOn) {
			String indexDefinition = JsonIndex.builder().asc(indexOn).name("index_" + indexOn).definition();
			db.createIndex(indexDefinition);
		}
	}

	@Override
	public Map<String, Object> upsert(Map<String, Object> document) throws DataAccessException {
		Database db = getDb(this.dataSource);
		Response r = null;
		if (document.containsKey("_id")) {
			r = db.update(document);
		} else {
			r = db.save(document);
		}
		logger.info("upsert is successful: " + r.toString());
		return document;
	}
}