package com.jafar;

import com.microsoft.azure.functions.ExecutionContext;

import java.io.IOException;
import java.sql.*;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.logging.Logger;
import java.util.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
	
	private static final Logger log;

	    static {
	        System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-7s] %5$s %n");
	        log =Logger.getLogger(Function.class.getName());
	    }
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     * @throws IOException 
     * @throws SQLException 
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws IOException, SQLException {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);


       
        log.info("Loading application properties");
        Properties properties = new Properties();
        properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));
        
        log.info("Connecting to the database");
        Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties);
        log.info("Database connection test: " + connection.getCatalog());
        String dbName = connection.getCatalog();
       

        log.info("Closing database connection");
        connection.close();
        
        if (name == null) {
        	return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
        		return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name + ". The Database i am connected to is: " + dbName ).build();
        }
  
        
    }
}
