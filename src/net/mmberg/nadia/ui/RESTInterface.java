package net.mmberg.nadia.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage;
import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage.Meta;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;

@Path("run")
public class RESTInterface extends UserInterface{

	private HttpServer server;
	private static int instance=0;
	protected static HashMap<Integer, UIConsumer> context = new HashMap<Integer, UIConsumer>();
	
	
	@GET
	@Path("dialog")
	public Response createDialog() throws URISyntaxException, InstantiationException, IllegalAccessException
	{	 
		instance++;
		context.put(instance, context.get(0).getClass().newInstance());
		return Response.seeOther(new URI("run/dialog/"+instance)).build();
	}
	
	@GET
	@Path("dialog/{instance_id}")
	@Produces( MediaType.TEXT_PLAIN )
	public Response exchange(
			@PathParam("instance_id") String instance_id,
			@QueryParam("userUtterance") String userUtterance)
	{
		consumer=context.get(Integer.parseInt(instance_id));
		UIConsumerMessage message = consumer.processUtterance(userUtterance);
		String systemUtterance = message.getSystemUtterance();
		if (message.getMeta()==Meta.UNCHANGED) return Response.noContent().build();
		else return Response.ok(systemUtterance).build();
	}

	@Override
	public void start() {
		try{
			context.put(0, consumer);
			server = HttpServerFactory.create("http://localhost:8080/nadia");
			server.start();
		}
		catch(Exception ex){
			ex.printStackTrace();
			server.stop(-1);
		}
	}

	@Override
	public void stop() {
		server.stop(0);
	}

}
