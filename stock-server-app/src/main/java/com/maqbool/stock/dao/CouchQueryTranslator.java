package com.maqbool.stock.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudant.client.api.query.Expression;
import com.cloudant.client.api.query.JsonIndex;
import com.cloudant.client.api.query.Selector;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.maqbool.stock.commons.Constants;
import com.maqbool.stock.commons.Operation;
import com.maqbool.stock.commons.PropertySpec;
import com.maqbool.stock.commons.Util;

/**
 * QueryTranslator is responsible for creating queries out of queryspec.
 * To-Do : This class is having many redundant methods which need to be clean later
 * @author niraj.gupta
 * 
 */
public class CouchQueryTranslator {
	
	private static final Logger logger = LoggerFactory.getLogger(CouchQueryTranslator.class);
	
	private CouchQueryTranslator() {

	}
	
	public static String buildIndexDefinition(String[] filters) {
		String indexDefinition = null;
		List<String> keys = new ArrayList<String>();
		String indexName = "";
		if (filters != null) {
			for (int i = 0; i < filters.length; i++) {
				String filter = filters[i];
				if (filter.contains(Constants.COLAN)) {
					String[] array = filter.split(Constants.COLAN);
					if (array.length > 2) {
						String propName = array[0];
						keys.add(propName);
						indexName += Constants.UNDERSCORE + propName;
					} else {
						throw new IllegalArgumentException(filter
								+ " should have 3 segments delimited with "
								+ Constants.COLAN);
					}
				} else {
					throw new IllegalArgumentException(filter
							+ " doesn't contain the delimiter "
							+ Constants.COLAN);
				}
			}
		}
		
		String[] indices = Arrays.copyOf(keys.toArray(), keys.size(), String[].class);
		if (keys.size() > 0) {
			indexDefinition = JsonIndex.builder().asc(indices).name("index" + indexName).definition();
		}
		logger.info("indexDef " + indexDefinition);
		return indexDefinition;
	}

	public static List<Selector> buildSelector(List<PropertySpec> specs) {
		List<Selector> selectors = new ArrayList<Selector>();
		if (Util.nullOrEmpty(specs)) {
			selectors.add(getExpression("_id", Operation.GT, ""));
		} else {
			for (PropertySpec spec : specs) {
				String propName = spec.getPropertyName();
				Operation operation = spec.getOperation();
				Object propValue = spec.getValue();
				Selector s = getExpression(propName, operation, propValue);
				selectors.add(s);
			}
		}
		logger.info("selectors: " + selectors.toString());
		return selectors;
	}
	
	private static Expression getExpression(String key, Operation operation, Object value) {
		Expression e = null;
		switch (operation) {
		case EQ:
			e = Expression.eq(key, value);
			break;

		case LIKE:
			e = Expression.regex(key, "(?i)(" + value.toString() + ")");
			break;

		case GT:
			e = Expression.gt(key, value);
			break;

		case LT:
			e = Expression.lt(key, value);
			break;

		
		case GTE:
			e = Expression.gte(key, value);
			break;

		case LTE:
			e = Expression.lte(key, value);
			break;

		case NOTEQUAL:
			e = Expression.ne(key, value);
			break;

		case ISNULL:
			e = Expression.eq(key, null);
			break;

		case IS_NOT_NULL:
			e = Expression.ne(key, null);
			break;

		case IN:
			e = Expression.in(key, value);
			break;
			
		case NOT_IN:
			e = Expression.nin(key, value);
			break;

		default:
			throw new IllegalArgumentException("invalid operation " + operation);
		}
		return e;
	}

	private static Object getObject(String s) {
		Object v = s;
		if (s != null) {
			if (NumberUtils.isNumber(s)) {
				if (s.indexOf(".") > -1) {
					v = new BigDecimal(s);
				} else {
					v = Long.valueOf(s);
				}
			} else if (s.startsWith(Constants.OPEN_BRACKET) && s.endsWith(Constants.CLOSE_BRACKET)) {
				String value = s.substring(1, s.length() - 1); // remove brackets from [value1,value2]
				v = value;
			}
		}
		return v;
	}

	/**
	 * This will build the sort clause.
	 * At present it supports only one param (1st)
	 * @param sortParams
	 * @return
	 */
	public static JsonArray buildSort(String[] sortParams) {
		JsonArray sortArray = new JsonArray();
		for (String param : sortParams) {
			String order = "asc";
			if (param.startsWith(Constants.HYPHEN)) {
				param = param.substring(1);
				order = "desc";
			} 
			JsonObject sortObject = new JsonObject();
            sortObject.addProperty(param, order);
            sortArray.add(sortObject);
		}
		
		return sortArray;
	}
	
	public static JsonArray buildFields(String[] fields) {
		JsonArray fieldsArray = new JsonArray();
		for (String field : fields) {
			JsonPrimitive jsonField = new JsonPrimitive(field);
            fieldsArray.add(jsonField);
		}
		
		return fieldsArray;
	}
	
	public static List<PropertySpec> getPropertySpecs(String[] filters) {
		List<PropertySpec> specs = new ArrayList<PropertySpec>();
		if (filters != null) {
			for (String filter : filters) {
				PropertySpec spec = getPropertySpec(filter);
				specs.add(spec);
			}
		} 
		return specs;
	}
	
	public static PropertySpec getPropertySpec(String filter) {
		if (filter.contains(Constants.COLAN)) {
			String[] array = filter.split(Constants.COLAN);
			if (array.length > 2) {
				String propName = array[0];
				String op = array[1];
				Operation operation = Operation.valueOf(op);
				Object propValue = getObject(array[2]);
				PropertySpec p = new PropertySpec(propName, operation, propValue);
				return p;
			} else {
				throw new IllegalArgumentException(filter
						+ " should have 3 segments delimited with "
						+ Constants.COLAN);
			}
		} else {
			throw new IllegalArgumentException(filter
					+ " doesn't contain the delimiter "
					+ Constants.COLAN);
		}
	}

	public static List<PropertySpec> getInPropertySpecs(List<PropertySpec> specs) {
		List<PropertySpec> inSpecs = new ArrayList<PropertySpec>();
		for (PropertySpec spec : specs) {
			Operation operation = spec.getOperation();
			if (Operation.IN == operation) {
				inSpecs.add(spec);
			}
		}
		return inSpecs;
	}

	public static List<List<PropertySpec>> splitInSpecs(List<PropertySpec> specs) {
		List<List<PropertySpec>> splitSpecs = new ArrayList<List<PropertySpec>>();
		List<PropertySpec> inSpecs = getInPropertySpecs(specs);
		if (! Util.nullOrEmpty(inSpecs)) {
			if (inSpecs.size() > 1) {
				throw new IllegalArgumentException("multiple IN clause in a query is not supported yet");
			}
			PropertySpec inSpec = inSpecs.get(0);
			Object value = inSpec.getValue();
			String[] values = null;
			if (value instanceof String) {
				values = ((String) value).split(",");
			}
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					String eachVal = values[i];
					List<PropertySpec> eachSplitSpecs = new ArrayList<PropertySpec>();
					for (PropertySpec spec : specs) {
						if (spec == inSpec) {
							eachSplitSpecs.add(new PropertySpec(spec.getPropertyName(), eachVal));
						} else {
							eachSplitSpecs.add(spec);
						}
					}
					splitSpecs.add(eachSplitSpecs);
				}
			}
		} else {
			splitSpecs.add(specs);
		}
		return splitSpecs;
	}
	
	public static String stringyfy(Collection<String> elements) {
		String array = "";
		int i = 0, n = elements.size();
		for (String data: elements) {
			array += data;
			if (i < n - 1) {
				array += ",";
			}
			i++;
		}
		return "[" + array + "]";
	}
}
