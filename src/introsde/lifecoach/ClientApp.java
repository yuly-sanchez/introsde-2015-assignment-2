package introsde.lifecoach;

import introsde.lifecoach.dao.LifeCoachDao;
import introsde.lifecoach.model.MeasureDefinition;
import introsde.lifecoach.model.Person;
import introsde.lifecoach.wrapper.MeasureHistoryWrapper;
import introsde.lifecoach.wrapper.MeasureTypesWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ClientApp {

	private static Client client;
	private static WebTarget webTarget;
	private static ClientConfig config;
	private static Response response;
	private static URI uri;

	private static String first_person_id;
	private static String last_person_id;

	private static String new_person_id;
	private static List<String> measure_types;

	private static String measure_id;
	private static String measure_Type;
	
	public static void main(String args[]) throws Exception {
		
		String MY_HEROKU_URL = null;
		MY_HEROKU_URL = "http://127.0.1.1:5700/sdelab"; // MY_HEROKU_URL = https://desolate-castle-6772.herokuapp.com
		System.out.println("Give me the server address: " + MY_HEROKU_URL);
		System.out.println();
		URI base_uri = getBaseURI(MY_HEROKU_URL);  //URI base_uri = getBaseURI(MY_HEROKU_URL + "/sdelab" );

		// elenco tutte le request
		request_1(base_uri);
		//request_2(base_uri, first_person_id);
		//request_3(base_uri, first_person_id);
		//request_5(base_uri, new_person_id);
		request_9(base_uri);
		request_6(base_uri);
		
	}

	private static URI getBaseURI(String server_url) {
		return UriBuilder.fromUri(server_url).build();
	}

	/**
	 * Step #1: Send request #1. Calculate how many people are in the response.
	 * If more than 2, result is OK, else is ERROR. Save into a variable id of
	 * the first person and of the last person.
	 * 
	 * @param server_uri
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws TransformerException 
	 */
	public static void request_1(URI server_uri)throws Exception {
		System.out.println("Request #1: \tGET " + "\t" + server_uri
				+ "/person \tAccept:APPLICATION_XML");

		uri = null;
		try {
			uri = new URI(server_uri + "/person");
		} catch (URISyntaxException e) {
			System.out.println("The URI " + uri + " not work.");
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String result = response.readEntity(String.class);
		Element rootElement = getRootElement(result);
		NodeList listNode = rootElement.getChildNodes();
		int countPeople = listNode.getLength();
		first_person_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		last_person_id = rootElement.getLastChild().getChildNodes().item(0).getTextContent();
		
		if(response.getStatus()==200 && countPeople>2){ 
			System.out.println("=> Result: " + response.getStatusInfo()); 
			System.out.println("=> HTTP Status: " + response.getStatus()); 
			System.out.println(prettyXMLPrint(result));
			
		}else if(response.getStatus()==200 && countPeople<3){ 
			System.out.println("=> Failed: Less than 3 people");
			
		}else {
			System.out.println("=> Failed: HTTP error code: " + response.getStatus()); 
		}
	}

	public static void request_2(URI server_uri, String person_id) throws Exception{
		System.out.println("Request #2: \tGET " + "\t" + server_uri + "/person/"+ person_id +"\tAccept:APPLICATION_XML");

		uri = null;
		try {
			uri = new URI(server_uri + "/person");
		} catch (URISyntaxException e) {
			System.out.println("The URI " + uri + " not work.");
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		
		if(response.getStatus()==200 || response.getStatus()==202){
			String result = response.readEntity(String.class);
			System.out.println("=> Result: " + response.getStatusInfo()); 
			System.out.println("=> HTTP Status: " + response.getStatus()); 
			System.out.println(prettyXMLPrint(result));
			
		}else if(response.getStatus() == 404){
			System.out.println("=> Result: " + "OK"); 
			System.out.println("=> HTTP Status: " + response.getStatus());
			 
		}else{
			System.out.println("=> Failed: HTTP error code: " + response.getStatus());
		}

	}
	
	public static void request_3(URI server_uri, String person_id){
		System.out.println("Request #3: \tPUT \t/person/" + person_id + " \tAccept:APPLICATION_XML \tContentType:APPLICATION_XML");
		
		String xml = "<person>"+
						"<firstname>Pavel</firstname>" +
					 "</person>";
		uri=null;
		try {
			uri = new URI(server_uri+"/person");
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).put(Entity.entity(xml, MediaType.APPLICATION_XML), Response.class);
		if(response.getStatus() == 200 || response.getStatus() == 201){
			System.out.println("=> Result: " + response.getStatusInfo());
			System.out.println("=> HTTP Status: " + response.getStatus());
			System.out.println(response.getStatusInfo());
			
		}else {
			System.out.println("=> Failed : HTTP error code : " + response.getStatus());
		}
	}
	
	public static void request_4(URI server_uri) throws Exception{
		System.out.println("Request #4: \tPOST \t/person \tAccept:APPLICATION_XML \tContentType:APPLICATION_XML");
		
		String xml = "<person>"+
						"<firstname>Chuck</firstname>" +
						"<lastname>Norris</lastname>" +
						"<birthdate>1945-01-01T00:00:00+02:00</birthdate>" +
					    "<healthProfile>" +
					        "<measureType>" +
					         	"<measure>weight</measure>" +   
					         	"<value>78.9</value>" +
					        "</measureType>" +
					        "<measureType>" +
					        	"<measure>height</measure>" +  
					        	"<value>1.73</value>" +
					        "</measureType>" +
					    "</healthProfile>" +
					 "</person>";
		uri=null;
		try {
			uri = new URI(server_uri+"/person");
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(xml, MediaType.APPLICATION_XML), Response.class);
		String result = response.readEntity(String.class);
		Element rootElement = getRootElement(result);
		new_person_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		
		if(response.getStatus()==200 || response.getStatus()==201 || response.getStatus()==202){ 
			System.out.println("=> Result: " + response.getStatusInfo()); 
			System.out.println("=> HTTP Status: " + response.getStatus()); 
			System.out.println(prettyXMLPrint(result));
		 
		}else{
			System.out.println("=> Failed: HTTP error code: " + response.getStatus());	
		}
	}
	
	public static void request_5(URI server_uri, String person_id) throws Exception{
		System.out.println("Request #4: \tDELETE \t/person/" + person_id + "\tAccept:APPLICATION_XML \tContentType:APPLICATION_XML");
		
		uri=null;
		try {
			uri = new URI(server_uri+"/person");
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).delete();
		
		//call request #2 to check the person with new_person_id
		request_2(server_uri, new_person_id);
	}
	
	
	public static void request_9(URI server_uri) throws Exception{
		System.out.println("Request #9: \tGET \t/measureTypes \tAccept:APPLICATION_XML");
		
		uri=null;
		try {
			uri = new URI(server_uri+"/measureTypes");
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String result = response.readEntity(String.class);
		Element rootElement = getRootElement(result);
		NodeList listNode = rootElement.getChildNodes();
		int countMeasureType = listNode.getLength();
		
		measure_types = new ArrayList<String>();
		for(int i=0; i<listNode.getLength(); i++){
			Node nNode = listNode.item(i);
			String measureType = nNode.getFirstChild().getTextContent();
			measure_types.add(measureType);
		}
		
		if(response.getStatus() == 200 && countMeasureType>2){
			System.out.println("=> Result: " + response.getStatusInfo());
			System.out.println("=> HTTP Status: " + response.getStatus());
			System.out.println(prettyXMLPrint(result));
			
		}else if(response.getStatus() == 200 && countMeasureType<3){
			System.out.println("=> Result: " + "ERROR");
			System.out.println("=> HTTP Status: " + response.getStatus());
			
		}else{
			System.out.println("=> Failed : HTTP error code : " + response.getStatus());
		}
	}

	public static void request_6(URI server_uri) throws Exception{
		uri=null;
		try {
			uri = new URI(server_uri+"/person");
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		String result=null;
		List<String> personList = new ArrayList<String>();
		personList.add(first_person_id);
		personList.add(last_person_id);
		
		for(int i=0; i<personList.size();i++){
			
			for(int j=0;j<measure_types.size();j++){
				
				System.out.println("Request #6: \tGET \t/person/"+personList.get(i)+"/"+ measure_types.get(j) + " \tAccept:APPLICATION_XML");
				
				webTarget = client.target(uri +"/").path(personList.get(i)+"/").path(measure_types.get(j));
				response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
				result = response.readEntity(String.class);
				
				Element rootElement = getRootElement(result);
				NodeList listNode = rootElement.getChildNodes();
				System.out.println("ListNode: " + listNode.getLength());
				
				if(response.getStatus()==200 && listNode.getLength()>0){
					measure_Type = measure_types.get(j);
					measure_id = rootElement.getFirstChild().getFirstChild().getTextContent();
					System.out.println("measureType: " + measure_Type + "\tmeasureId: " + measure_id);
					System.out.println("=> Result: " + response.getStatusInfo());
					System.out.println("=> HTTP Status: " + response.getStatus());
					System.out.println(prettyXMLPrint(result));
					return;
				}else{
					System.out.println("=> Result: " + "ERROR");
					System.out.println("=> HTTP Status: " + response.getStatus());
					return;
				}
			}
		}
	}
	
	 
	
	public static Element getRootElement(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc.getDocumentElement();
	}

	/**
	 * Print pretty XML
	 * 
	 * @param xml
	 * @return
	 * @throws TransformerException 
	 */
	public static String prettyXMLPrint(final String xml) throws TransformerException {
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		Source xmlInput = new StreamSource(new StringReader(xml));
		StringWriter stringWriter = new StringWriter();
		StreamResult xmlOutput = new StreamResult(stringWriter);

		transformer.transform(xmlInput, xmlOutput);
		return xmlOutput.getWriter().toString();
	}

}
