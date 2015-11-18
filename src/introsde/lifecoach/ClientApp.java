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
import javax.ws.rs.core.Application;
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
	private static URI server_uri;
	private static String HEADER="application_xml";

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
		server_uri = getBaseURI(MY_HEROKU_URL);  //URI base_uri = getBaseURI(MY_HEROKU_URL + "/sdelab" );

		// elenco tutte le request
		request_1();
		request_2(first_person_id);
		request_3();
		request_4();
		request_5();
		request_9();
		request_6();
		
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
	public static void request_1()throws Exception {
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String response_1 = response.readEntity(String.class);
		
		Element rootElement = getRootElement(response_1);
		NodeList listNode = rootElement.getChildNodes();
		int countPeople = listNode.getLength();
		first_person_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		last_person_id = rootElement.getLastChild().getChildNodes().item(0).getTextContent();
		
		if(response.getStatus()==200 && countPeople>2){ 
			RESULT = "OK";
			templateRequest(1, "GET" , "/person", response, RESULT);
			System.out.println(prettyXMLPrint(response_1));
			
		}else if(response.getStatus()==200 && countPeople<3){ 
			templateRequest(1, "GET" , "/person", response, RESULT);
		}
	}

	public static void request_2(String person_id) throws Exception{
		String RESULT = "ERROR";
		String uri =server_uri + "/person";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri + "/").path(person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		
		if(response.getStatus()==200 || response.getStatus()==202){
			RESULT = "OK";
			String response_2 = response.readEntity(String.class);
			templateRequest(2, "GET", "/person/" + person_id, response, RESULT);
			System.out.println(prettyXMLPrint(response_2));
			
		}else if(response.getStatus() == 404){
			RESULT = "OK";
			templateRequest(5, "DELETE", "/person/" + person_id, response, RESULT);
			 
		}else{
			templateRequest(2, "GET", "/person/" + person_id, response, RESULT);
		}

	}
	
	public static void request_3(){
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		String xml = "<person>"+
						"<firstname>Pavel</firstname>" +
					 "</person>";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(first_person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).put(Entity.entity(xml, MediaType.APPLICATION_XML), Response.class);
		if(response.getStatus() == 200 || response.getStatus() == 201){
			RESULT = "OK";
			templateRequest(3, "PUT", "/person/" + first_person_id, response, RESULT);
			System.out.println(response.getStatusInfo());
			
		}else {
			templateRequest(3, "PUT", "/person/" + first_person_id, response, RESULT);
		}
	}
	
	
	public static void request_4() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		/*String xml = "<person>"+
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
					 "</person>";*/
		
		String xml = "<person>"+
						"<firstname>Chuck</firstname>" +
						"<lastname>Norris</lastname>" +
						"<birthdate>1945-01-01T00:00:00+02:00</birthdate>" +
						"<healthProfile/>" +
					 "</person>";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).post(Entity.entity(xml, MediaType.APPLICATION_XML), Response.class);
		String response_4 = response.readEntity(String.class);
		Element rootElement = getRootElement(response_4);
		new_person_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		
		if(response.getStatus()==200 || response.getStatus()==201 || response.getStatus()==202){ 
			RESULT = "OK";
			templateRequest(4, "POST", "/person", response, RESULT);
			System.out.println(prettyXMLPrint(response_4));
		 
		}else{
			templateRequest(4, "POST", "/person", response, RESULT);	
		}
	}
	
	
	public static void request_5() throws Exception{
		String uri = server_uri + "/person";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(new_person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).delete();
		
		//call request #2 to check the person with new_person_id
		request_2(new_person_id);
	}
	
	
	public static void request_9() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/measureTypes";
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String response_9 = response.readEntity(String.class);
		Element rootElement = getRootElement(response_9);
		NodeList listNode = rootElement.getChildNodes();
		int countMeasureType = listNode.getLength();
		
		measure_types = new ArrayList<String>();
		for(int i=0; i<listNode.getLength(); i++){
			Node nNode = listNode.item(i);
			String measureType = nNode.getFirstChild().getTextContent();
			measure_types.add(measureType);
		}
		
		if(response.getStatus() == 200 && countMeasureType>2){
			RESULT = "OK";
			templateRequest(9, "GET", "/measureTypes", response, RESULT);
			System.out.println(prettyXMLPrint(response_9));
			
		}else{
			templateRequest(9, "GET", "/measureTypes", response, RESULT);
		}
	}

	
	public static void request_6() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		String person_id = null;
		String response_result = null;
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		
		List<String> personList = new ArrayList<String>();
		personList.add(first_person_id);
		personList.add(last_person_id);
		
		for(int i=0; i<personList.size();i++){
			
			for(String m : measure_types){
				
				webTarget = client.target(uri +"/").path(personList.get(i)+"/").path(m);
				response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
				String response_6 = response.readEntity(String.class);
				
				Element rootElement = getRootElement(response_6);
				NodeList listNode = rootElement.getChildNodes();
				
				if(response.getStatus()==200 && listNode.getLength()>0){
					response_result = response_6;
					person_id = personList.get(i);
					measure_Type = m;
					measure_id = rootElement.getFirstChild().getFirstChild().getTextContent();	
				}	
			}
		}
		if(response.getStatus()==200){
			RESULT = "OK";
			templateRequest(6, "GET", "/person"+ "/" + person_id + "/" + measure_Type, response, RESULT);
			System.out.println(prettyXMLPrint(response_result));
		}else{
			templateRequest(6, "GET", "/person"+ "/" + person_id + "/" + measure_Type, response, RESULT);
		}
	}
/*	
	public static void request_8(URI server_uri){} 
*/	
	public static Element getRootElement(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc.getDocumentElement();
	}

	public static void templateRequest(int numberRequest, String method, String url, Response response, String result){
		System.out.println("==================================================================================");
		System.out.println("Request #" + numberRequest + ": "+ method + " " + url + " " + "Accept: " + HEADER + " " + "Content-Type: " + HEADER);
		System.out.println("=> Result: " + result);
		System.out.println("=> HTTP Status: " + response.getStatus());
		System.out.println();
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
