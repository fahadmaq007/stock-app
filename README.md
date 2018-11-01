Stock App 
*********

Problem Description
-------------------
* Implement a simple stock analytics interface such that two different stock index are compared in an overlay graph. The index examples suggested are BSE and DJIA
	- Use the following website to download the BSE index data in a time series. Use 5 years of data. https://www.bseindia.com/indices/IndexArchiveData.aspx
	- Use the following website to download the DJIA index data in a time series. Use 5 years of data. https://finance.yahoo.com/quote/%5EDJI/history/
* Store the data in any data base of your choice
* Write a Java server that responds to an API or APIs that delivers this data from both sources
* Plot an interactive graph of this time series data 
* The user must be able to provide either/both of BSE and DJIA as the input and the resulting graph must be an overlay graph when both are selected
* The graph must be visually pleasing 
* The axis must be appropriately labelled
* The graph should allow for the selection of time windows on the graph by selecting the time period of interest and adding annotations to the selected section by typing in any input
* The user must be able to select the over all time window for such a graph (filter)


Solution
--------
It has client & server application.

* stock-ng-app (client): The client app is written in Angular 1, the charts are developed using highcharts (https://www.highcharts.com/)

* stock-server-app (server): The server is developed using Spring Boot & Core framework. 