stock-server-app (server): 
*************************

The server is developed in Spring Boot framework & it uses CouchDB NoSQL Database.
1. 	Run the CouchDB database:
	docker run -d -p 5984:5984 -v $pwd/couch-data:/opt/couchdb/data --name couch-stock couchdb

2. 	Import csv files: The downloaded csv files are available in the classpath folder, run the following command to import them:
	./import-data.sh

3. 	Run application: It is a spring boot application, use the following command to run it:
	./run-app.sh

The server app should be running on http://localhost:8080

To retrieve the documents, use the following URL:
GET: http://localhost:8080/stocks?period=1M (1 month's data)
Content-Type: application/json