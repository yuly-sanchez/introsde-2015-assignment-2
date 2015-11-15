package introsde.lifecoach;

import introsde.lifecoach.dao.LifeCoachDao;
import introsde.lifecoach.model.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;


public class ClientApp {

	private static Client client;
	private static WebTarget webTarget;
	private static ClientConfig config;
	private static Response response;
	private static URI uri;
	
	private static int first_person_id;
	private static int last_person_id;
	
	private static int new_person_id;
	
	
	public static void main(String args[]){
		//BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		
		String MY_HEROKU_URL = null;
		// --> http://127.0.1.1:5700/sdelab
		//MY_HEROKU_URL = console.readLine();
		MY_HEROKU_URL = "http://127.0.1.1:5700/sdelab";
		System.out.println("Give me the server address: " + MY_HEROKU_URL);
		System.out.println();
		URI base_uri = getBaseURI(MY_HEROKU_URL);
		//elenco tutte le request
		request_1(base_uri);
		request_2(base_uri, first_person_id);
		request_3(base_uri, first_person_id);
		request_4_XML(base_uri);
	}
	
	private static URI getBaseURI(String server_url){
		return UriBuilder.fromUri(server_url).build();
	}
	
	/**
	 * Request #1 
	 * @param server_uri
	 */
	public static void request_1(URI server_uri){
		System.out.println("Request #1: \tGET " + "\t"+server_uri+"/person \tAccept:APPLICATION_XML");
		
		uri = null;
		try{
			uri = new URI(server_uri+"/person");
		}catch(URISyntaxException e){
			System.out.println("The URI " + uri + " not work.");
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).get(Response.class);
		int count = calculatePeopleList(uri);
		if(response.getStatus()==200 && count>2){
			String result = response.readEntity(String.class);
			System.out.println("=> Result: " + response.getStatusInfo());
			System.out.println("=> HTTP Status: " + response.getStatus());
			System.out.println("[BODY]");
			System.out.println(prettyXMLPrint(result));
			System.out.println("[BODY]\n");
		}else if(response.getStatus()==200 && count<3){
			System.out.println("=> Failed: Less than 3 people");
		}else if(response.getStatus() != 200){
			System.out.println("=> Failed: HTTP error code: " + response.getStatus());
		}
	}
	
	/**
	 * Calculate how many people are in the list of people
	 * @param server_uri
	 * @return
	 */
	public static int calculatePeopleList (URI server_uri){
		webTarget = client.target(server_uri);
		List<Person> personList = webTarget.request(MediaType.APPLICATION_XML).get(new GenericType<List<Person>>(){});
		int result = personList.size();
		System.out.println("How many people are in the response: " + result);
		first_person_id = personList.get(0).getIdPerson();
		last_person_id = personList.get(result-1).getIdPerson();
		System.out.println("First idPerson: " + first_person_id + ", Last idPerson: " + last_person_id);
		return result;
	}
	
	/**
	 * Request #2
	 * @param server_uri
	 * @param first_person_id
	 */
	public static void request_2(URI server_uri, int first_person_id){
		System.out.println("Request #2: \tGET " + "\t"+server_uri+"/person"+"/"+first_person_id + "\tAccept:APPLICATION_XML");
		
		uri=null;
		try {
			uri = new URI(server_uri+"/person");
		} catch (URISyntaxException e) {
			System.out.println("The URI " + uri + " not work.");
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/"+first_person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		if(response.getStatus()==200 || response.getStatus()==202){
			String res = response.readEntity(String.class);
			System.out.println("=> Result: " + response.getStatusInfo());
			System.out.println("=> HTTP Status: " + response.getStatus());
			System.out.println("[BODY]");
			System.out.println(prettyXMLPrint(res));
			System.out.println("[BODY]\n");
		}else{
			System.out.println("=> Failed : HTTP error code : " + response.getStatus());
		}
	}
	
	public static void request_3(URI server_uri, int first_person_id){
		System.out.println("Request #3: \tPUT " + "\t"+server_uri+"/person"+"/"+first_person_id + "\tAccept:APPLICATION_XML \tContent-Type:APPLICATION_XML");
		
		uri = null;
		try{
			uri = new URI(server_uri+"/person");
		}catch(URISyntaxException e){
			System.out.println("The URI " + uri + " not work.");
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/"+first_person_id);
		
		Person person = new Person();
		person.setName("Benassai");
		
		response = webTarget.request().accept(MediaType.APPLICATION_XML).put(Entity.entity(person, MediaType.APPLICATION_XML), Response.class);
		if(response.getStatus() == 200 || response.getStatus() == 201){
			String result = response.readEntity(String.class);
			System.out.println("=> Result: " + response.getStatusInfo());
			System.out.println("=> HTTP Status: " + response.getStatus());
			System.out.println(result);
		}else{
			System.out.println("=> Failed: HTTP error code: " + response.getStatus());
		}
	}
	
	public static void request_4_XML(URI server_uri){
		System.out.println("Request #4: \tPOST " + "\t"+server_uri+"/person" + "\tAccept:APPLICATION_XML \tContent-Type:APPLICATION_XML");
		
		uri = null;
		try{
			uri = new URI(server_uri+"/person");
		}catch(URISyntaxException e){
			System.out.println("The URI " + uri + " not work.");
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 1945);
		calendar.set(Calendar.MONTH, 01-1);
		calendar.set(Calendar.DAY_OF_MONTH, 01);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		Person person = new Person();
		person.setName("Chuck");
		person.setLastname("Norris");
		person.setBirthdate(calendar.getTime());
		
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(person, MediaType.APPLICATION_XML), Response.class);
		if(response.getStatus()==200 || response.getStatus()==201 || response.getStatus()==202){
			String result = response.readEntity(String.class);
			System.out.println("=> Result: " + response.getStatusInfo());
			System.out.println("=> HTTP Status: " + response.getStatus());
			System.out.println("[BODY]");
			System.out.println(prettyXMLPrint(result));
			System.out.println("[BODY]\n");
		}else {
			System.out.println("=> Failed : HTTP error code : " + response.getStatus());
		}
	}
	
	/**
	 * Print pretty XML
	 * @param xml
	 * @return
	 */
	public static String prettyXMLPrint(final String xml){  

	    if (StringUtils.isBlank(xml)) {
	        throw new RuntimeException("xml was null or blank in prettyPrint()");
	    }
	    final StringWriter sw;

	    try {
	        final OutputFormat format = OutputFormat.createPrettyPrint();
	        final org.dom4j.Document document = DocumentHelper.parseText(xml);
	        sw = new StringWriter();
	        final XMLWriter writer = new XMLWriter(sw, format);
	        writer.write(document);
	    }
	    catch (Exception e) {
	        throw new RuntimeException("Error pretty printing xml:\n" + xml, e);
	    }
	    return sw.toString();
	}
}

















































