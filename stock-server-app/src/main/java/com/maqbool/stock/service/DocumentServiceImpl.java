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

import com.maqbool.stock.commons.Constants;
import com.maqbool.stock.dao.IDao;
import com.maqbool.stock.exceptions.DataAccessException;
import com.maqbool.stock.exceptions.ServiceException;

@Service
public class DocumentServiceImpl implements DocumentService {
	
	@Autowired
	private IDao dao;

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public List<Map<String, Object>> list(String[] filters, String[] sort, Integer page, Integer limit) throws ServiceException {
		try {
			logger.info("listing documents...");
			return dao.listDocuments(filters, sort, page, limit);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Object> store(Map<String, Object> s) throws ServiceException {
		try {
			logger.info("storing stock... " + s.get(Constants._ID));
			return dao.upsert(s);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

}
