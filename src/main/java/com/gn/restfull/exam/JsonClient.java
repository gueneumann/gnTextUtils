package com.gn.restfull.exam;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/*
 * https://jersey.java.net/documentation/latest/client.html#d0e4269
 */
public class JsonClient {

	public static void main(String[] args) {
		// Create client
		Client client = ClientBuilder.newClient();
		// Target get request
		WebTarget webResource = client.target("http://localhost:9999/employee/getEmployee");
		Response response = webResource.request(MediaType.APPLICATION_JSON).get();
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.toString());
		}
		
		// Bind the GET-ed java class
		Employee output = response.readEntity(Employee.class);
		
		// Target the post request
		webResource = client.target("http://localhost:9999/employee/postEmployee");
		// Apply it on GET-ed java object
		webResource.request().post(Entity.entity(output, MediaType.APPLICATION_JSON));
	}

}
