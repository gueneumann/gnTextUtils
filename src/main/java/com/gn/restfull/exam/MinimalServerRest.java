package com.gn.restfull.exam;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class MinimalServerRest {

	public static void main(String[] args) throws Exception {
		// This is jersey version 1.* - outdated !
		//        ServletHolder sh = new ServletHolder(ServletContainer.class);    
		//        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
		//        sh.setInitParameter("com.sun.jersey.config.property.packages", "de.dfki.lt.nereid.restfull.exam");//Set the package where the services reside
		//        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
		//      
		//        Server server = new Server(9999);
		//        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
		//        context.addServlet(sh, "/*");
		//        System.out.println("Starting ...");
		//        server.start();
		//        server.join();   

		// Create a basic Jetty server
		Server server = new Server(9999);        

		// Create handler for responding to requests
		// Servlets are the standard way to provide application logic that handles HTTP requests. 
		// Servlets are like constrained Handlers with standard ways to map specific URIs to specific servlets.
		// A ServletContextHandler is a specialization of ContextHandler with support for standard servlets. 
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

		// This registers servlets bound to ServletContainer.class to the servlet
		ServletHolder servlet = context.addServlet(ServletContainer.class, "/*");

		// Why do I need this?
		//jerseyServlet.setInitOrder(1);

		// This I need to set the package where the services remain when using ServletContextHandler.SESSIONS
		servlet.setInitParameter("jersey.config.server.provider.packages", "de.dfki.lt.nereid.restfull.exam");
		
		// Why do I need this?
		//context.setContextPath("/");
		//context.setServer(server);
		
		// GN: I forgot this !
		server.setHandler(context);

		System.out.println("Starting ...");
		server.start();        
		server.join();  
	}

}
