/**
 * 
 */
package com.maqbool.stock.service;

import java.util.List;
import java.util.Map;

import com.maqbool.stock.exceptions.ServiceException;

public interface StockService extends DocumentService {

	/**
	 * Lists the stock by given type & period
	 * @param period
	 * @param types
	 * @return
	 */
	List<Map<String, Object>> listStock(String period, String[] types) throws ServiceException;

}
