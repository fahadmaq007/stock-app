/**
 * 
 */
package com.maqbool.stock.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maqbool.stock.commons.Util;
import com.maqbool.stock.dao.IDao;
import com.maqbool.stock.exceptions.DataAccessException;
import com.maqbool.stock.exceptions.ServiceException;

@Service
public class StockServiceImpl extends DocumentServiceImpl implements StockService {
	
	@Autowired
	private IDao dao;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<Map<String, Object>> listStock(String period, String[] types) throws ServiceException {
		try {
			long timestamp = Util.convertPeriodToTimestamp(period);
			String[] filters = { "timestamp:GT:" + timestamp };
			String[] sort = { "timestamp" };		
			logger.info("listing stocks after..." + timestamp);
			return dao.listDocuments(filters, sort, 1, Integer.MAX_VALUE);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	

}
