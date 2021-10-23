# FX Price Handler API

The FX Price Handler API is a Springboot application that accepts FX prices as Comma Separated Values (CSVs) and provides these prices when requested.

---

## Usage
There are two main parts to the application:

### Publishing FX Prices:
#### Using the REST endpoint (http://localhost:9191/fxPriceApi/postPrice)
The application provides a REST endpoint to accept FX prices as CSVs as part of a HTTP request body. This is then published onto a queue for processing (caching). The application uses an embedded ActiveMQ broker and therefore, using the REST endpoint is the easiest way to publish new FX Prices. 

To publish a price, send a request with a body containing the FX prices as CSVs to the following endpoint using either Postman, curl or Swagger (more on this below):

##### Sample requests sending 5 FX price updates:
```
curl --location --request POST 'http://localhost:9191/fxPriceApi/postPrice' \
--header 'Content-Type: text/plain' \
--data-raw '106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001
107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002
108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002
109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100
110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110
'
```

#### Direct to queue via an external ActiveMQ broker (Queue: fxPriceData.queue)
An external ActiveMQ broker can be configured and used directly instead for publishing FX Prices. To configure an external ActiveMQ broker, start the application as follows (Replacing localhost:61616 with server and port number for the broker):
```
java -Dspring.activemq.broker-url=tcp://localhost:61616 -jar fx-price-handler-1.0.0-SNAPSHOT.jar
```
FX prices can then be published on the following queue via ActiveMQ broker admin:```fxPriceData.queue```. This queue will need to be created via ActiveMQ broker admin the first time this is accessed.


### Retrieving FX Prices
The application provides a REST endpoint to retrieve an FX Price for an instrument (or currency) pair. The REST endpoint is 
```
http://localhost:9191/fxPriceApi/getPrice/{instrumentNameOne}/instrumentNameTwo
```
Sample request retrieving FX Price for GBP/USD:

##### Via Internet browser: 
```
http://localhost:9191/fxPriceApi/getPrice/GBP/USD
```
##### Using Curl: 
```
curl --location --request GET 'http://localhost:9191/fxPriceApi/getPrice/GBP/USD'
```
---


## For Developers

### Listening for FX Prices
The application listens for prices messages coming from the fxPriceData.queue using a Jms listener. This is implemented in the ```FxPriceHandlerService``` class within the ```void onMessage(String priceData)``` method. Each line of the raw price data is converted to an immutable Price object using the ```FxPriceTransformerService``` class. The ```FxPriceTransformerService``` class also applies the commission to the bid and ask prices for each Price object created.

### Caching
The application uses a concurrent Hashmap as a cache and this is encapsulated in the ```FxPriceCache``` class. The concurrent Hashmap ensures there is a less chance of dirty reads from the cache in a multithreaded environment. The ```FxPriceCache``` class does not allow access to the cache and therefore, the cache is fully managed by the class (there is no escape).

All data in the cache have a Time to live (TTL) and when they expire, they are removed from the cache using the evictExpiredPrices() method in ```FxPriceCache``` class. The TTL can be configured via the ```fxPriceCache.ttl``` property in the application properties file. It is set to 1 day by default. 

The spring scheduler that runs the ```evictExpiredPrices()``` method in the ```FxPriceCache``` class can be configured to run at a frequency and time according to requirement using CRON syntax. This configuration is via ```fxPriceCache.expiry.refreshRate``` property and it is set to run 2AM Monday to Friday by default.

### REST APIs
The application provides REST APIs to accept new FX Price messages and to retrieve prices from the cache using services described above. These REST APIs are implemented in the ```FXPriceController``` class. Details of the REST APIs have been discussed above.

### Swagger and Spring Open API Docs
To view details of the exposed APIs, the application uses Spring Open API Docs. The details of the REST API's can viewed at the following URL: 
```
http://localhost:9191/fxPriceApi/api-docs/
```
This is best viewed using Firefox browser.

The application can be tested easily using Swagger UI. To test the APIs using swagger, go the following URL:
```
http://localhost:9191/fxPriceApi/fx-price-api-swagger-ui.html
```
Click on the ```POST``` endpoint and then the ```"Try it out"``` button to publish FX prices (each price should be in CSV format and on separate lines - no need to quote).

Click on the ```GET``` endpoint to retrieve any of the prices posted

### Building and Executing
The executable is built using Maven: 
```
mvn clean install
```

As it is a Springboot application, an executable JAR is created after a maven install and this can be found in the target folder under the project root folder. 

Copy that to any location (or navigate to the target folder) and to start the application, simply run the following command via a command prompt or UNIX shell:
```
java -jar fx-price-handler-1.0.0-SNAPSHOT.jar
```

### Cloning and running locally via IDE
The code can be cloned/forked using any IDE. The unit tests can be run immediately and the application can be started locally by clicking Run on most IDEs or by Right-clicking on the ```FxPriceHandlerApplication``` class and selecting Run