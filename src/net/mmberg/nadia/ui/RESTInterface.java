package net.mmberg.nadia.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;

//import com.sun.jersey.api.container.httpserver.HttpServerFactory;
//import com.sun.net.httpserver.HttpServer;

import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage;
import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage.Meta;


@Path("/")
public class RESTInterface extends UserInterface{

	private Server server;
	private static int instance=-1;
	protected static HashMap<Integer, UIConsumer> context = new HashMap<Integer, UIConsumer>();
	
	
	@GET
	@Path("ui")
	@Produces( MediaType.TEXT_HTML )
	public String getWebpage(){
		String html="<html><head><script src='http://code.jquery.com/jquery-1.9.1.min.js'></script> <script> $(document).ready(function(){   $('#submitbtn').click(function(){     $.get('http://localhost:8080/nadia/run/dialog/0', {userUtterance: $('#utterance').val()}, function(data) { 		$('#systemUtterance').text(data); 		alert('Load was performed.'); 	});   }); }); </script> </head> <body>  <h1 id='systemUtterance'>Text</h1><input id='utterance' type='text'/><button id='submitbtn'>Los</button></body> </html>";
		return html;
	}
	
	@GET
	@Path("dialog")
	public Response createDialog() throws URISyntaxException, InstantiationException, IllegalAccessException
	{	 
		instance++;
		if (instance>0) context.put(instance, context.get(0).getClass().newInstance());
		return Response.seeOther(new URI("/dialog/"+instance)).build();
	}
	
	
	private UIConsumerMessage process(String instance_id, String userUtterance){
		consumer=context.get(Integer.parseInt(instance_id));
		UIConsumerMessage message = consumer.processUtterance(userUtterance);
		return message;
	}
	
	@GET
	@Path("dialog/{instance_id}")
	@Produces( MediaType.TEXT_PLAIN )
	public Response exchange_text(
			@PathParam("instance_id") String instance_id,
			@QueryParam("userUtterance") String userUtterance)
	{
		UIConsumerMessage message = process(instance_id, userUtterance);
		String systemUtterance = message.getSystemUtterance();
		
		if (message.getMeta()==Meta.UNCHANGED) return Response.noContent().build();
		else{
			String uri="";
			try {
				uri = new URI("engine/dialog/"+instance_id).toString();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Response.ok(systemUtterance).header("Location", uri).build();
		}
	}
	
	@GET
	@Path("dialog/{instance_id}")
	@Produces( MediaType.APPLICATION_JSON )
	public Response exchange_json(
			@PathParam("instance_id") String instance_id,
			@QueryParam("userUtterance") String userUtterance)
	{
		UIConsumerMessage message = process(instance_id, userUtterance);

		if (message.getMeta()==Meta.UNCHANGED) return Response.noContent().build();
		else return Response.ok(message).build();
	}

	@Override
	public void start(){
		try{
			context.put(0, consumer);
			
			//Embedded Server:
			//server = HttpServerFactory.create("http://localhost:8080/nadia");
			//server.start();
			
			//Jetty:
			server = new Server(8080);

	        WebAppContext context = new WebAppContext();
	        context.setDescriptor("./WEB-INF/web.xml");
	        context.setResourceBase("./res/html");
	        context.setContextPath("/nadia");
	        context.setParentLoaderPriority(true);

	        server.setHandler(context);

	        server.start();
	        server.join();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			//server.stop();
		}
	}

	@Override
	public void stop() {
		//server.stop(0);
	}

}
