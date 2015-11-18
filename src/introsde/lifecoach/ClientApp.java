package introsde.lifecoach;


import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.glassfish.jersey.client.ClientConfig;
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

	private static String first_person_id;
	private static String last_person_id;

	private static String new_person_id;
	private static List<String> measure_types;

	private static String measure_id;
	private static String measure_Type;
	
	private static String new_measure_id;
	
	private static String mediaType;
	
	public static void main(String args[]) throws Exception {
		
		String MY_HEROKU_URL = "http://127.0.1.1:5700/sdelab"; // MY_HEROKU_URL = https://desolate-castle-6772.herokuapp.com
		System.out.println("The server address: " + MY_HEROKU_URL);
		System.out.println();
		
		server_uri = getBaseURI(MY_HEROKU_URL);  //URI base_uri = getBaseURI(MY_HEROKU_URL + "/sdelab" );
		
		/*int argCount = args.length;
		if(argCount == 0) {
			System.out.println("I don't change attribute MeadiaType.");
		} else {
			String method = args[0];
			
			if(method.equals("client-server-xml")) {
				mediaType = MediaType.APPLICATION_XML;
			}else if (method.equals("client-server-json")) {
				mediaType = MediaType.APPLICATION_JSON;
			}
		}*/
		
		mediaType = MediaType.APPLICATION_XML;
		// elenco tutte le request
		request_1();
		//request_2();
		//request_3();
		//request_4();
		//request_5();
		//request_6();
		//request_7();
		//request_8();
		//request_9();
		//request_10();
		//request_11();
		//request_12();
		
	}

	private static URI getBaseURI(String server_url) {
		return UriBuilder.fromUri(server_url).build();
	}

	/**
	 * Step 3.1. 
	 * Send R#1 (GET BASE_URL/person).
	 * Calculate how many people are in the response. 
	 * If more than 2, result is OK, else is ERROR (less than 3 persons). 
	 * Save into a variable id of the first person (first_person_id) and 
	 * of the last person (last_person_id)@param server_uri
	 * @throws Exception 
	 */
	public static void request_1()throws Exception {
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		response = client.target(uri).request().accept(mediaType).get(Response.class);
		String response_1 = response.readEntity(String.class);
		
		Element rootElement = getRootElement(response_1);
		NodeList listNode = rootElement.getChildNodes();
		int countPeople = listNode.getLength();
		first_person_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		last_person_id = rootElement.getLastChild().getChildNodes().item(0).getTextContent();
		
		if(response.getStatus()==200 && countPeople>2){ 
			RESULT = "OK";
			templateRequest(1, "GET" , "/person", response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_1));
			
		}else if(response.getStatus()==200 && countPeople<3){ 
			templateRequest(1, "GET" , "/person", response, RESULT, mediaType);
		}
	}

	/**
	 * Step 3.2. 
	 * Send R#2 for first_person_id.
	 * If the responses for this is 200 or 202, the result is OK.
	 * @throws Exception
	 */
	public static void request_2() throws Exception{
		String RESULT = "ERROR";
		String uri =server_uri + "/person";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri + "/").path(first_person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		
		if(response.getStatus()==200 || response.getStatus()==202){
			RESULT = "OK";
			String response_2 = response.readEntity(String.class);
			templateRequest(2, "GET", "/person/" + first_person_id, response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_2));
			
		}else{
			templateRequest(2, "GET", "/person/" + first_person_id, response, RESULT, mediaType);
		}
	}
	
	/**
	 * Step 3.3. 
	 * Send R#3 for first_person_id changing the firstname. 
	 * If the responses has the name changed, the result is OK.
	 */
	public static void request_3(){
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		String xml = "<person>"+
						"<firstname>Prova</firstname>" +
					 "</person>";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(first_person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).put(Entity.entity(xml, MediaType.APPLICATION_XML), Response.class);
		if(response.getStatus() == 200 || response.getStatus() == 201){
			RESULT = "OK";
			templateRequest(3, "PUT", "/person/" + first_person_id, response, RESULT, mediaType);
			System.out.println(response.getStatusInfo());
			
		}else {
			templateRequest(3, "PUT", "/person/" + first_person_id, response, RESULT, mediaType);
		}
	}
	
	/**
	 * Step 3.4. 
	 * Send R#4 to create the following person. 
	 * Store the id of the new person. 
	 * If the answer is 201 (200 or 202 are also applicable) 
	 * with a person in the body who has an ID, the result is OK.
	 * @throws Exception
	 */
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
						"<birthdate>1945-01-01</birthdate>" +
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
			templateRequest(4, "POST", "/person", response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_4));
		 
		}else{
			templateRequest(4, "POST", "/person", response, RESULT, mediaType);	
		}
	}
	
	/**
	 * Step 3.5. 
	 * Send R#5 for the person you have just created.
	 * Then send R#1 with the id of that person. 
	 * If the answer is 404, your result must be OK.
	 * @throws Exception
	 */
	public static void request_5() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(new_person_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).delete();
		
		// Request # 2: GET /person/new_person_id
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		if(response.getStatus() == 404){
			RESULT = "OK";
			templateRequest(5, "DELETE", "/person/" + new_person_id, response, RESULT, mediaType);
			System.out.println("Person id " + new_measure_id + " deleted");
		}else{
			templateRequest(5, "DELETE", "/person/" + new_person_id, response, RESULT, mediaType);
		}
	}
	
	/**
	 * Step 3.6. 
	 * Follow now with the R#9 (GET BASE_URL/measureTypes). 
	 * If response contains more than 2 measureTypes - result is OK, 
	 * else is ERROR (less than 3 measureTypes). 
	 * Save all measureTypes into array (measure_types)
	 * @throws Exception
	 */
	public static void request_6() throws Exception{
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
			templateRequest(6, "GET", "/measureTypes", response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_9));
			
		}else{
			templateRequest(6, "GET", "/measureTypes", response, RESULT, mediaType);
		}
	}

	/**
	 * Step 3.7. 
	 * Send R#6 (GET BASE_URL/person/{id}/{measureType}) for the first person 
	 * you obtained at the beginning and the last person, and 
	 * for each measure types from measure_types. 
	 * If no response has at least one measure - result is ERROR (no data at all) else result is OK. 
	 * Store one measure_id and one measureType.
	 * @throws Exception
	 */
	public static void request_7() throws Exception{
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
				
				if(listNode.getLength()>0){
					response_result = response_6;
					person_id = personList.get(i);
					measure_Type = m;
					measure_id = rootElement.getFirstChild().getFirstChild().getTextContent();
				}	
			}
		}
		
		if(response.getStatus()==200){
			RESULT = "OK";
			templateRequest(7, "GET", "/person"+ "/" + person_id + "/" + measure_Type, response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_result));
		}else{
			templateRequest(7, "GET", "/person"+ "/" + person_id + "/" + measure_Type, response, RESULT, mediaType);
		}
	}
	
	/**
	 * Step 3.8. 
	 * Send R#7 (GET BASE_URL/person/{id}/{measureType}/{mid}) for the stored measure_id and measureType. 
	 * If the response is 200, result is OK, else is ERROR.
	 * @throws Exception
	 */
	public static void request_8() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri + "/").path(first_person_id + "/").path(measure_Type + "/").path(measure_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		
		if(response.getStatus() == 200){
			RESULT = "OK";
			String response_7 = response.readEntity(String.class);
			templateRequest(8, "GET", "/person" + "/" + first_person_id + "/" + measure_Type + "/" + measure_id, response, RESULT, mediaType);
			System.out.println("Mid value: " + response_7);
			
		}else{
			templateRequest(8, "GET", "/person" + "/" + first_person_id + "/" + measure_Type + "/" + measure_id, response, RESULT, mediaType);
		}
		
	} 
	
	/**
	 * Step 3.9. 
	 * Choose a measureType from measure_types and 
	 * send the request R#6 (GET BASE_URL/person/{first_person_id}/{measureType}) and 
	 * save count value (e.g. 5 measurements). 
	 * Then send R#8 (POST BASE_URL/person/{first_person_id}/{measureTypes}) with the a new measurement. 
	 * Follow up with another R#6 as the first to check the new count value. 
	 * If it is 1 measure more - print OK, else print ERROR. 
	 * Remember, first with JSON and then with XML as content-types
	 * @throws Exception
	 */
	public static void request_9() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		String xml = "<measure>"+
						"<value>72</value>" +
						"<created>2011-12-09</created>" + 
					 "</measure>";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri + "/").path(first_person_id + "/").path(measure_Type);
		
		// Request # 6: GET /person/first_person_id/measure_Type
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String response_6 = response.readEntity(String.class);
		Element rootElement = getRootElement(response_6);
		NodeList listNode = rootElement.getChildNodes();
		int count_value = listNode.getLength();
		
		// Request # 8: POST /person/first_person_id/measure_Type
		response = webTarget.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(xml, MediaType.APPLICATION_XML), Response.class); 
		String response_8 = response.readEntity(String.class);
		rootElement = getRootElement(response_8);
		listNode = rootElement.getChildNodes();
		new_measure_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
		
		// Request # 6: GET /person/first_person_id/measure_Type
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		if(response.getStatus() == 200 && count_value>1){
			RESULT = "OK";
			templateRequest(9, "GET", "/person"+ "/" + first_person_id + "/" + measure_Type, response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_6));
		}else{
			templateRequest(9, "GET", "/person"+ "/" + first_person_id + "/" + measure_Type, response, RESULT, mediaType);
		}
		
	}
	
	/**
	 * Step 3.10. 
	 * Send R#10 using the {mid} or the measure created in the previous step and 
	 * updating the value at will. Follow up with at R#6 to check that the value was updated. 
	 * If it was, result is OK, else is ERROR.
	 * @throws Exception
	 */
	public static void request_10() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		String xml = "<measure>"+
						"<value>90</value>" +
						"<created>2011-12-09</created>" + 
					 "</measure>";
	
		String first_person_id = "1";
		String new_measure_id = "2";
		String measure_Type = "weight";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(first_person_id + "/").path(measure_Type + "/").path(new_measure_id);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).put(Entity.entity(xml, MediaType.APPLICATION_XML), Response.class);
		int status_put = response.getStatus(); 
		
		// Request # 6: GET /person/first_person_id/measure_Type
		webTarget = client.target(uri+"/").path(first_person_id + "/").path(measure_Type);
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		
		if(response.getStatus() == 200 && status_put == 201){
			RESULT = "OK";
			String response_6 = response.readEntity(String.class);
			templateRequest(10, "GET", "/person/" + first_person_id + "/" + measure_Type, response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_6));
			
		}else {
			templateRequest(10, "GET", "/person/" + first_person_id + "/" + measure_Type, response, RESULT, mediaType);
		}
	}
	
	/**
	 * Step 3.11. 
	 * Send R#11 for a measureType, before and after dates given 
	 * by your fellow student (who implemnted the server). 
	 * If status is 200 and there is at least one measure in the body, result is OK, else is ERROR
	 * @throws Exception
	 */
	public static void request_11() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		
		String before = "2015-11-01";
		String after = "2015-11-15";
		
		//String first_person_id = "1";
		//String measure_Type = "height";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri+"/").path(first_person_id + "/").path(measure_Type).queryParam("before", before).queryParam("after",after);
		
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String response_11 = response.readEntity(String.class);
		Element rootElement = getRootElement(response_11);
		NodeList listNode = rootElement.getChildNodes();
		int count = listNode.getLength();
		
		if(response.getStatus() == 200 && count>0){
			RESULT = "OK";
			templateRequest(11, "GET", "/person/" + first_person_id + "/" + measure_Type + "?" + "before=" + before + "&after=" + after, response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_11));
			
		}else {
			templateRequest(11, "GET", "/person/" + first_person_id + "/" + measure_Type + "?" + "before=" + before + "&after=" + after, response, RESULT, mediaType);
		}
	}
	
	/**
	 * Step 3.12. 
	 * Send R#12 using the same parameters as the preivious steps. 
	 * If status is 200 and there is at least one person in the body, result is OK, else is ERROR
	 * @throws Exception
	 */
	public static void request_12() throws Exception{
		String RESULT = "ERROR";
		String uri = server_uri + "/person";
		
		Double max = 1.75;
		Double min = 1.70;
		
		//String measure_Type = "height";
		
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target(uri).queryParam("measureType", measure_Type).queryParam("max", max).queryParam("min",min);
		System.out.println("webTarget: " + webTarget);
		
		response = webTarget.request().accept(MediaType.APPLICATION_XML).get(Response.class);
		String response_12 = response.readEntity(String.class);
		Element rootElement = getRootElement(response_12);
		NodeList listNode = rootElement.getChildNodes();
		int count = listNode.getLength();
		System.out.println("count: " + count);
		if(response.getStatus() == 200 && count>0){
			RESULT = "OK";
			templateRequest(12, "GET", "/person" + "?measureType=" + measure_Type + "?max=" + max + "&min=" + min, response, RESULT, mediaType);
			System.out.println(prettyXMLPrint(response_12));
			
		}else {
			templateRequest(12, "GET", "/person" + "?measureType=" + measure_Type + "?max=" + max + "&min=" + min, response, RESULT, mediaType);
		}
	}
	
	/**
	 * Function that return root element
	 * @param xml
	 * @return root element
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
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

	/**
	 * Print the template request
	 * @param numberRequest
	 * @param method
	 * @param url
	 * @param response
	 * @param result
	 * @param mediaType
	 */
	public static void templateRequest(int numberRequest, String method, String url, Response response, String result, String mediaType){
		System.out.println("==================================================================================");
		System.out.println("Request #" + numberRequest + ": "+ method + " " + url + " " + "Accept: " + mediaType + " " + "Content-Type: " + mediaType);
		System.out.println("=> Result: " + result);
		System.out.println("=> HTTP Status: " + response.getStatus());
		System.out.println();
	}
	
	
	/**
	 * Print pretty XML
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
