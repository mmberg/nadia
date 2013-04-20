package net.mmberg.nadia.processor.ui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.server.ServerConnector;

import com.sun.jersey.multipart.FormDataParam;

import net.mmberg.nadia.processor.NadiaProcessor;
import net.mmberg.nadia.processor.NadiaProcessorConfig;
import net.mmberg.nadia.processor.dialogmodel.Dialog;
import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage;
import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage.Meta;


@Path("/")
public class RESTInterface extends UserInterface{

	private Server server;
	private static int instance_counter=0;
	protected static HashMap<String, UIConsumer> instances = new HashMap<String, UIConsumer>();
	
	@Context UriInfo uri;
	@Context HttpServletRequest request;
	@Context HttpHeaders headers;
	  
	//Web Service Methods
	//===================
	
	@POST
	@Path("dialog")
	public Response createDefaultDialog() throws URISyntaxException, InstantiationException, IllegalAccessException
	{	 
		String nextid=next_id();
		create_dialog(nextid);
		return init_dialog(nextid); //init dialogue, i.e. get first question
	}
	
	
	@POST
	@Path("dialog/load")
	@Produces( MediaType.TEXT_PLAIN )
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createDialogFromXML(
			@FormDataParam("dialogxml") String dialogxml) throws URISyntaxException, InstantiationException, IllegalAccessException
	{
		String nextid=next_id();
		create_dialog(nextid, dialogxml);		
		return init_dialog(nextid); //init dialogue, i.e. get first question
	}
	
	@POST
	@Path("dialog/{instance_id}")
	@Produces( MediaType.TEXT_PLAIN )
	public Response exchange_text(
			@PathParam("instance_id") String instance_id,
			@FormParam("userUtterance") String userUtterance)
	{
		if(!instances.containsKey(instance_id)){ //check whether instance exists
			return Response.serverError().entity("Error: no such instance").build(); 
		}
		
		UIConsumerMessage message = process(instance_id, userUtterance);
		String systemUtterance = message.getSystemUtterance();
		
		if (message.getMeta()==Meta.UNCHANGED) return Response.noContent().build();
		else return Response.ok(systemUtterance).build();
	}
	
	@GET
	@Path("status")
	@Produces( MediaType.TEXT_HTML )
	public Response getServerInfo()
	{
		clean_up();
		String info="<html><head><title>Nadia Status</title></head><body><p>";
		info+="Started on: "+NadiaProcessor.getStartedOn().toString()+"<br>";
		info+="Default dialogue: "+NadiaProcessor.getDefaultDialog().getName()+"<br>";
		info+="Started UI: "+NadiaProcessor.getUIType()+"<br>";
		info+="Current sessions ("+instances.size()+"):<ul>";
		for(String sid : instances.keySet()){
			info+="<li><a href='/nadia/engine/dialog/"+sid+"/context'>Session "+sid+"</a></li>";
		}
		info+="</ul></p></body></html>";
		return Response.ok(info).build();
	}
	
	@GET
	@Path("dialog/{instance_id}/context")
	@Produces( MediaType.TEXT_PLAIN )
	public Response getDebugInfo(
			@PathParam("instance_id") String instance_id)
	{
		if (!instances.containsKey(instance_id)) return Response.serverError().entity("Error: no such instance").build();
		
		UIConsumer instance = instances.get(instance_id);
		return Response.ok(instance.getDebugInfo()).build();
	}

	//Convenience functions
	//=====================
	
	/**
	 * process user utterance
	 * @param instance_id
	 * @param userUtterance
	 * @return
	 */
	private UIConsumerMessage process(String instance_id, String userUtterance){
		UIConsumer consumer=instances.get(instance_id);
		UIConsumerMessage message = consumer.processUtterance(userUtterance);
		return message;
	}
	
	private void create_dialog(String instance, String dialogxml){
		UIConsumer new_consumer;
		if(dialogxml!=null){
			Dialog d=Dialog.loadFromXml(dialogxml);
			new_consumer = consumerFactory.create(d); //new DialogManager(d);
		}
		else{
			new_consumer = consumerFactory.create(); //new DialogManager();
		}
		new_consumer.setAdditionalDebugInfo("Client IP: "+request.getRemoteAddr().toString()+"; User-Agent: "+headers.getRequestHeader("User-Agent"));
		instances.put(instance, new_consumer);
		NadiaProcessor.getLogger().fine("created new instance "+new_consumer.getClass().getName());
	}
	
	private void create_dialog(String instance_id){
		create_dialog(instance_id, null);
	}
	
	private String next_id(){
		instance_counter++;
		String identifier="d"+instance_counter;
		return identifier;
	}
	
	/**
	 * deletes unused sessions
	 */
	private void clean_up(){
		
		int threshold_minutes=10;
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.MINUTE, -threshold_minutes);
	    Date threshold=cal.getTime();

	    ArrayList<String> deletionCandidates= new ArrayList<String>();
	    for(Map.Entry<String, UIConsumer> entry : instances.entrySet()){
	    	if (entry.getValue().getLastAccess().before(threshold)){
	    		deletionCandidates.add(entry.getKey());
	    	}
	    }
	    
	    for(String id : deletionCandidates){
	    	instances.remove(id);
	    }
	}
	
	/**
	 * init dialogue, i.e. get first question
	 * @param instance
	 * @return
	 * @throws URISyntaxException
	 */
	private Response init_dialog(String instance_id) throws URISyntaxException{
		UIConsumerMessage message = process(instance_id, ""); //empty utterance => init
		String systemUtterance = message.getSystemUtterance();
		
		if (message.getMeta()==Meta.UNCHANGED) return Response.created(new URI("/"+instance_id)).build();
		else return Response.created(new URI(uri.getBaseUri()+"dialog/"+instance_id)).entity(systemUtterance).build();
	}
	
	//Interface functions
	//===================
	
	@Override
	public void start(){
		try{
			NadiaProcessorConfig config = NadiaProcessorConfig.getInstance();
			
			//Jetty:
			server = new Server();
			
			//main config
	        WebAppContext context = new WebAppContext();
	        context.setDescriptor(config.getProperty(NadiaProcessorConfig.JETTYWEBXMLPATH));
	        context.setResourceBase(config.getProperty(NadiaProcessorConfig.JETTYRESOURCEBASE));
	        context.setContextPath(config.getProperty(NadiaProcessorConfig.JETTYCONTEXTPATH));
	        context.setParentLoaderPriority(true);
	        server.setHandler(context);
	        
	        //ssl (https)
	        SslContextFactory sslContextFactory = new SslContextFactory(config.getProperty(NadiaProcessorConfig.JETTYKEYSTOREPATH));
	        sslContextFactory.setKeyStorePassword(config.getProperty(NadiaProcessorConfig.JETTYKEYSTOREPASS));
	
	        ServerConnector serverconn = new ServerConnector(server, sslContextFactory);
	        serverconn.setPort(8080); //443 (or 80) not allowed on Linux unless run as root
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
