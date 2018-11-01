/**
 * 
 */
package com.maqbool.stock.dao;

import java.util.List;
import java.util.Map;

import com.maqbool.stock.exceptions.DataAccessException;
import com.maqbool.stock.exceptions.ServiceException;

/**
 * @author maqboolahmed
 *
 */
public interface IDao {

	/**
	 * Lists the documents by given filter
	 * @param filters
	 * @param sortParams
	 * @param page
	 * @param limit
	 * @return {@link List} of documents
	 * @throws ServiceException
	*/
	public List<Map<String, Object>> listDocuments(String[] filters, String[] sortParams, 
			Integer page, Integer limit) throws DataAccessException;
	
	/**
	 * Lists the documents by given filter
	 * @param filters
	 * @param sortParams
	 * @param fields
	 * @param page
	 * @param limit
	 * @return {@link List} of documents
	 * @throws ServiceException
	*/
	public List<Map<String, Object>> listDocuments(String[] filters, String[] sortParams, String[] fields,
			Integer page, Integer limit) throws DataAccessException;
	
	/**
	 * Upsert the document
	 * @param document
	 * @return
	 * @throws DataAccessException
	 */
	public Map<String, Object> upsert(Map<String, Object> document) throws DataAccessException;
}
