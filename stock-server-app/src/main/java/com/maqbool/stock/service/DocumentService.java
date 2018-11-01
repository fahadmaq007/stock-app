/**
 * 
 */
package com.maqbool.stock.service;

import java.util.List;
import java.util.Map;

import com.maqbool.stock.exceptions.ServiceException;

public interface DocumentService {

	/**
	 * List the todo entities
	 * 
	 * @return List<Todo>
	 * @throws ServiceException
	 */
	List<Map<String, Object>> list(String[] filters, String[] sort, Integer page, Integer limit) throws ServiceException;

	/**
	 * Store the entity (upsert)
	 * 
	 * @param s - entity
	 * @return stored entity
	 * @throws ServiceException
	 */
	Map<String, Object> store(Map<String, Object> s) throws ServiceException;

}
