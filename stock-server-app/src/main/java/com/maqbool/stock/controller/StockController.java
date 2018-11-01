package com.maqbool.stock.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.maqbool.stock.exceptions.ServiceException;
import com.maqbool.stock.service.DocumentService;
import com.maqbool.stock.service.StockService;

/**
 * The TodoController (REST) is exposed to the external world.
 * Consumes & Produces JSON
 * @author maqbool
 *
 */
@Controller
@CrossOrigin(origins = {"http://localhost:9000"})
@RequestMapping(value = "/stocks", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class StockController {
	
	@Autowired
	private StockService stockService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, Object>> listStock(@RequestParam(value="period", required = true) String period,
			@RequestParam(value="types", required = false) String[] types) throws ServiceException {
		List<Map<String, Object>> todos = stockService.listStock(period, types);
		return todos;
	}
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody Map<String, Object> store(@RequestBody(required = true) Map<String, Object> s) throws ServiceException {
		return stockService.store(s);
	}
	
}
