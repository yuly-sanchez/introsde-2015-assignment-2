package introsde.lifecoach;

import introsde.lifecoach.dao.LifeCoachDao;
import introsde.lifecoach.model.MeasureDefinition;
import introsde.lifecoach.model.Person;
import introsde.lifecoach.wrapper.HealthMeasureHistoryListWrapper;
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

	private static int first_person_id;
	private static int last_person_id;

	private static int new_person_id;
	private static List<String> measure_types;

	public static void main(String args[]) throws Exception {
		// BufferedReader console = new BufferedReader(new
		// InputStreamReader(System.in));

		String MY_HEROKU_URL = null;
		// --> http://127.0.1.1:5700/sdelab
		// MY_HEROKU_URL = console.readLine();
		MY_HEROKU_URL = "http://127.0.1.1:5700/sdelab";
		System.out.println("Give me the server address: " + MY_HEROKU_URL);
		System.out.println();
		URI base_uri = getBaseURI(MY_HEROKU_URL);

		// elenco tutte le request
		request_1(base_uri);
		//request_2(base_uri, first_person_id);
		//request_3(base_uri, first_person_id);
		request_9(base_uri);
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
		String first_person_id_str = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		String last_person_id_str = rootElement.getLastChild().getChildNodes().item(0).getTextContent();
		first_person_id = Integer.valueOf(first_person_id_str);
		last_person_id = Integer.valueOf(last_person_id_str);
		
		if(response.getStatus()==200 && countPeople>2){ 
			System.out.println("=> Result: " + response.getStatusInfo()); 
			System.out.println("=> HTTP Status: " + response.getStatus()); 
			System.out.println(prettyXMLPrint(result));
			
		}else if(response.getStatus()==200 && countPeople<3){ 
			System.out.println("=> Failed: Less than 3 people");
			
		}else if(response.getStatus() != 200){
			System.out.println("=> Failed: HTTP error code: " + response.getStatus()); 
		}
	}

	public static void request_2(URI server_uri, int first_person_id) throws Exception{
		System.out.println("Request #2: \tGET " + "\t" + server_uri + "/person/"+ first_person_id +"\tAccept:APPLICATION_XML");

		uri = null;
		try {
			uri = new URI(server_uri + "/person");
		} catch (URISyntaxException e) {
			System.out.println("The URI " + uri + " not work.");
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/"+first_person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		
		if(response.getStatus()==200 || response.getStatus()==202){
			String result = response.readEntity(String.class);
			System.out.println("=> Result: " + response.getStatusInfo()); 
			System.out.println("=> HTTP Status: " + response.getStatus()); 
			System.out.println(prettyXMLPrint(result));
			
		}else if(response.getStatus() == 404){
			System.out.println("=> Result: " + response.getStatusInfo()); 
			System.out.println("=> HTTP Status: " + response.getStatus());
			System.out.println("OK");
			 
		}else{
			System.out.println("=> Failed: HTTP error code: " + response.getStatus());
		}

	}
	
	public static void request_3(URI server_uri, int first_person_id){
		System.out.println("Request #3: \tPUT \t/person/"+first_person_id+ " \tAccept:APPLICATION_XML \tContentType:APPLICATION_XML");
		
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
		webTarget = client.target(uri+"/"+first_person_id);
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
		String new_person_id_str = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		new_person_id = Integer.valueOf(new_person_id_str);
		if(response.getStatus()==200 || response.getStatus()==201 || response.getStatus()==202){ 
			System.out.println("=> Result: " + response.getStatusInfo()); 
			System.out.println("=> HTTP Status: " + response.getStatus()); 
			System.out.println(prettyXMLPrint(result));
		 
		}else{
			System.out.println("=> Failed: HTTP error code: " + response.getStatus());	
		}
	}
	
	public static void request_5(URI server_uri, int new_person_id) throws Exception{
		System.out.println("Request #4: \tDELETE \t/person/" + new_person_id + "\tAccept:APPLICATION_XML \tContentType:APPLICATION_XML");
		
		uri=null;
		try {
			uri = new URI(server_uri+"/person");
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/"+new_person_id);
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
			System.out.println("=> Failed: Less than 3 measureTypes");
			
		}else{
			System.out.println("=> Failed : HTTP error code : " + response.getStatus());
		}
	}

	public static void request_6(URI server_uri, int person_id, String measureType) throws Exception{
		System.out.println("Request #6: \tGET \t/person/"+person_id+"/"+measureType + " \tAccept:APPLICATION_XML");
		
		uri=null;
		try {
			uri = new URI(server_uri+"/person");
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/"+person_id+"/"+measureType);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String result = response.readEntity(String.class);
		Element rootElement = getRootElement(result);
		NodeList listNode = rootElement.getChildNodes();
		
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
