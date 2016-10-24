package com.gn.restfull.exam;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
 * https://jersey.java.net/documentation/latest/client.html#d0e4269
 */

public class XMLClient {
	public static void main(String[] args) {
		// Create client
		Client client = ClientBuilder.newClient();
		// Target a service
		WebTarget webResource = client.target("http://localhost:9999/xmlServices/student/Jane");
		
		// Apply GET
		Response response = webResource.request(MediaType.APPLICATION_XML).get();
		
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : "
			+ response.getStatus());
		}	
        
		// Get the java object
		Student output = response.readEntity(Student.class);	
		
		// And do something
		System.out.println("Output xml client .... \n");
		System.out.println(output);
	}	
}
