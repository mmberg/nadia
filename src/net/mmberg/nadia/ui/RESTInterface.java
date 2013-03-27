package net.mmberg.nadia.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.jmx.ConnectorServer;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.ServerConnector;

import net.mmberg.nadia.Nadia;
import net.mmberg.nadia.NadiaConfig;
import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage;
import net.mmberg.nadia.ui.UIConsumer.UIConsumerMessage.Meta;


@Path("/")
public class RESTInterface extends UserInterface{

	private Server server;
	private static int instance=-1;
	protected static HashMap<Integer, UIConsumer> context = new HashMap<Integer, UIConsumer>();
	
	@GET
	@Path("dialog")
	public Response createDialog() throws URISyntaxException, InstantiationException, IllegalAccessException
	{	 
		instance++;
		if (instance>0){
			UIConsumer new_consumer=context.get(0).getClass().newInstance();
			context.put(instance, new_consumer);
			Nadia.getLogger().fine("created new instance "+new_consumer.getClass().getName());
		}
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
			NadiaConfig config = NadiaConfig.getInstance();
			
			//Jetty:
			server = new Server();
			
			//main config
	        WebAppContext context = new WebAppContext();
	        context.setDescriptor(config.getProperty(NadiaConfig.JETTYWEBXMLPATH));
	        context.setResourceBase(config.getProperty(NadiaConfig.JETTYRESOURCEBASE));
	        context.setContextPath(config.getProperty(NadiaConfig.JETTYCONTEXTPATH));
	        context.setParentLoaderPriority(true);
	        server.setHandler(context);
	        
	        //ssl (https)
	        SslContextFactory sslContextFactory = new SslContextFactory(config.getProperty(NadiaConfig.JETTYKEYSTOREPATH));
	        sslContextFactory.setKeyStorePassword(config.getProperty(NadiaConfig.JETTYKEYSTOREPASS));
	
	        ServerConnector serverconn = new ServerConnector(server, sslContextFactory);
	        serverconn.setPort(443);
	        server.setConnectors(new Connector[] {serverconn});
	        
	        //start	        
	        server.start();
	        server.join();
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			server.destroy();
		}
	}

	@Override
	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
			server.destroy();
		}
	}

}
