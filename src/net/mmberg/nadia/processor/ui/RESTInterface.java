package net.mmberg.nadia.processor.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
import net.mmberg.nadia.processor.exceptions.ProcessingException;
import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage;
import net.mmberg.nadia.processor.ui.UIConsumer.UIConsumerMessage.Meta;


@Path("/")
public class RESTInterface extends UserInterface{

	private Server server;
	private static int instance_counter=0;
	protected static HashMap<String, UIConsumer> instances = new HashMap<String, UIConsumer>();
	private final static Logger logger = NadiaProcessor.getLogger();
	
	@Context UriInfo uri;
	@Context HttpServletRequest request;
	@Context HttpHeaders headers;
	
	public RESTInterface(){
		//if loaded externally (Application Server / war) instead of using main-method in NadiaProcessor
		if(!NadiaProcessor.isInit()){
			NadiaProcessorConfig config = NadiaProcessorConfig.getInstance();
			String path="";
			try{
//				path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().resolve("../..").getPath().toString(); //Jetty???
				path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().resolve("../../../../../../..").getPath().toString(); //Tomcat
				path = path.substring(0, path.length()-1); //remove last slash
				logger.info("Nadia Root Path: "+path);
			}
			catch(Exception ex){
				ex.printStackTrace();
				logger.severe("Nadia RESTInterface constructor failed: "+ex.getMessage());
			}
			config.setBaseDir("file://"+path);
			NadiaProcessor.loadByWar(this);
		}
	}
	
	
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
		String info="<html><head><title>Nadia Status</title></head><body><p>";
		info+="Started on: "+NadiaProcessor.getStartedOn().toString()+"<br>";
		info+="Default dialogue: "+NadiaProcessor.getDefaultDialogPathAndName()+"<br>"; //getDefaultDialog().getName()+"<br>";
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
	@Produces( MediaType.APPLICATION_XML )
	public Response getDebugInfo(
			@PathParam("instance_id") String instance_id)
	{
		if (!instances.containsKey(instance_id)) return Response.serverError().entity("Error: no such instance").build();
		
		UIConsumer instance = instances.get(instance_id);
		String debugInfo = instance.getDebugInfo();
		if(debugInfo==null || debugInfo.length()==0){
			debugInfo = "no debug info";
		}
		else{
			debugInfo = addXSDReference(debugInfo, "/nadia/context.xsl");
		}
		
		return Response.ok(debugInfo).build();
	}
	
	@GET
	@Path("dialog/{instance_id}/xml")
	@Produces( MediaType.APPLICATION_XML )
	public Response getDialogXML(
			@PathParam("instance_id") String instance_id)
	{
		if (!instances.containsKey(instance_id)) return Response.serverError().entity("Error: no such instance").build();
		
		UIConsumer instance = instances.get(instance_id);
		String xml=instance.getDialogXml();
//		xml=xml.subSequence(xml.indexOf("\n")+1, xml.length()).toString();
//		xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<?xml-stylesheet href=\"/nadia/dialog.xsl\" type=\"text/xsl\"?>\r\n"+xml;
		xml=addXSDReference(xml, "/nadia/dialog.xsl");
		return Response.ok(xml).build();
	}
	
	/*
	 * Gets the content from an URL. Used for Google TTS, because we need a client that does not send the referer.
	 */
	@GET
	@Path("redirect")
	public Response getContentFromUrl(
			@QueryParam("url") String url_str)
	{
		try{
			url_str=url_str.replaceAll(" ", "+");      	
		 	URL url = new URL(url_str);
		 	
		 	HttpURLConnection httpcon = (HttpURLConnection) url.openConnection(); 
		 	httpcon.addRequestProperty("User-Agent", "Mozilla/4.76");
		 	httpcon.connect();
	        InputStream is = httpcon.getInputStream();
	        
	        byte[] buffer = new byte[0xFFFF];
	        int length;
	        ByteArrayOutputStream output = new ByteArrayOutputStream();

	        while ((length = is.read(buffer)) != -1){
	        	output.write(buffer, 0, length);
	        }
	        
	        byte[] content=output.toByteArray();
	        is.close();
		        
			return Response.ok(content).build();
		}
		catch(Exception ex){
			ex.printStackTrace();
			return Response.noContent().build();
		}	
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
		UIConsumerMessage message;
		try{
			message = consumer.processUtterance(userUtterance);
		}
		catch(ProcessingException ex){
			message = new UIConsumerMessage(ex.getMessage(), Meta.ERROR);
			logger.severe("Nadia processing in REST interface failed: "+ex.getMessage());
		}
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
		if(instance_counter%10==0) clean_up(); //run clean up every 10 instances
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
	
	private String addXSDReference(String xml, String schemaPath){
		xml=xml.subSequence(xml.indexOf("\n")+1, xml.length()).toString();
		xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<?xml-stylesheet href=\""+schemaPath+"\" type=\"text/xsl\"?>\r\n"+xml;
		
		return xml;
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
			logger.severe("Nadia: failed to start Jetty: "+ex.getMessage());
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
