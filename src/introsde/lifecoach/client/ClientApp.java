package introsde.lifecoach.client;


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
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class ClientApp {

	private String person_id = null;
	private String first_person_id = null;
	private String last_person_id = null;

	private String new_person_id = null;
	private List<String> measure_types = null;

	private String measure_id = null;
	private String measure_Type = null;
	
	public static void main(String args[]) throws Exception {
		
		//my server local
		//String MY_SERVER = "http://127.0.1.1:5700/sdelab";
		
		//Configure client
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		WebTarget service = client.target(getBaseURI());
		
		System.out.println("Starting sdelab standalone HTTP server...");
		System.out.println("Server started on " + getBaseURI() + "\n[kill the process to exit]");
		
		int argCount = args.length;
		String mediaType = null;
		
		if(argCount < 1) {
			System.out.println("Error: You must to insert a media type.");
		} else {
			// Sets the media type either XML or JSON
			String method = args[0];
			
			if(method.equals("XML")) {
				mediaType = MediaType.APPLICATION_XML;
			}else if (method.equals("JSON")) {
				mediaType = MediaType.APPLICATION_JSON;
			}
		}
		System.out.println("MediaType: " + mediaType);
		
		try{
			
			ClientApp testClient = new ClientApp();

			testClient.request_1(service, mediaType); 
			testClient.request_2(service, mediaType); 
			testClient.request_3(service, mediaType);
			testClient.request_4(service, mediaType);
			testClient.request_5(service, mediaType);
			testClient.request_6(service, mediaType);
			testClient.request_7(service, mediaType);
			testClient.request_8(service, mediaType);
			
			testClient.request_9(service, mediaType);
			testClient.request_10(service, mediaType);
			testClient.request_11(service, mediaType);
			testClient.request_12(service, mediaType);

		}catch(Exception ex){
			ex.printStackTrace();
		}			
	}

	/**
     * Get base uri of my server that should be deployed on Heroku
     * @return
     */
	private static URI getBaseURI() {
		return UriBuilder.fromUri("https://desolate-castle-6772.herokuapp.com/sdelab").build();
		//return UriBuilder.fromUri("http://127.0.1.1:5700/sdelab").build();
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
	public void request_1(WebTarget service, String mediaType)throws Exception {
		String RESULT = "ERROR";
		String path = "/person";
		
		// Request # 1: GET /person
		Response response = service.path(path).request().accept(mediaType).get(Response.class);
		String response_1 = null;
		
		int countPeople;
		
		if(response.getStatus() == 200){
			response_1 = response.readEntity(String.class);
			
			if(mediaType == MediaType.APPLICATION_XML){
				Element rootElement = getRootElement(response_1);
				NodeList listNode = rootElement.getChildNodes();
				countPeople = listNode.getLength();
				if(countPeople > 2)
					RESULT = "OK";
				first_person_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
				last_person_id = rootElement.getLastChild().getChildNodes().item(0).getTextContent();
			
			}else if(mediaType == MediaType.APPLICATION_JSON){
				JSONObject obj = new JSONObject(response_1);
				JSONArray arr = obj.getJSONArray("people");
				countPeople = arr.length();
				if(countPeople > 2)
					RESULT = "OK";
				first_person_id = arr.getJSONObject(0).get("id").toString();
				last_person_id = arr.getJSONObject(countPeople-1).get("id").toString();
			}
			templateRequest(1, "GET", path, response, RESULT, mediaType);
			System.out.println(prettyFormatPrint(response_1, mediaType));
		}
	}

	/**
	 * Step 3.2. 
	 * Send R#2 for first_person_id.
	 * If the responses for this is 200 or 202, the result is OK.
	 * @throws Exception
	 */
	public void request_2(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person/" + first_person_id;
		
		// Request # 2: GET /person/first_person_id
		Response response = service.path(path).request().accept(mediaType).get(Response.class);
		String response_2 = null;
		
		if(response.getStatus() == 200 || response.getStatus()==202){
			response_2 = response.readEntity(String.class);
			
			if(mediaType == MediaType.APPLICATION_XML){
				RESULT = "OK";
			}else if(mediaType == MediaType.APPLICATION_JSON){
				RESULT = "OK";
			}
		}
		templateRequest(2, "GET", path, response, RESULT, mediaType);
		System.out.println(prettyFormatPrint(response_2, mediaType));	
	}
	
	/**
	 * Step 3.3. 
	 * Send R#3 for first_person_id changing the firstname. 
	 * If the responses has the name changed, the result is OK.
	 */
	public void request_3(WebTarget service, String mediaType){
		String RESULT = "ERROR";
		String path = "/person/" + first_person_id;
		
		String input = null;
		
		if(mediaType == MediaType.APPLICATION_XML){
			input = "<person>"+
							"<firstname>Felipe</firstname>" +
					"</person>";	
		}else if(mediaType == MediaType.APPLICATION_JSON){
			JSONObject obj = new JSONObject();
			obj.put("firstname", "Juan");
			input = obj.toString();
		}
		
		// Request # 3: PUT /person/first_person_id
		Response response = service.path(path).request().accept(mediaType).put(Entity.entity(input, mediaType), Response.class);
		if(response.getStatus() == 200 || response.getStatus() == 201){
			RESULT = "OK";
		}
		templateRequest(3, "PUT", path, response, RESULT, mediaType);
		System.out.println(response.getStatusInfo());	
	}
	
	/**
	 * Step 3.4. 
	 * Send R#4 to create the following person. 
	 * Store the id of the new person. 
	 * If the answer is 201 (200 or 202 are also applicable) 
	 * with a person in the body who has an ID, the result is OK.
	 * @throws Exception
	 */
	public void request_4(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person";
		
		String input = null;

		Response response = null;
		String response_4 = null;
		
		if(mediaType == MediaType.APPLICATION_XML){
			input = "<person>"+
						"<firstname>Chuck</firstname>" +
						"<lastname>Norris</lastname>" +
						"<birthdate>1945-01-01</birthdate>" +
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
			
			// Request # 4: POST /person
			response = service.path(path).request().accept(mediaType).post(Entity.entity(input, mediaType), Response.class);
			response_4 = response.readEntity(String.class);
			
			Element rootElement = getRootElement(response_4);
			new_person_id = rootElement.getFirstChild().getChildNodes().item(0).getTextContent();
			
			if(response.getStatus()>=200 && response.getStatus()<=202){ 
				RESULT = "OK";
			}
			
		}else if(mediaType == MediaType.APPLICATION_JSON){	
			JSONObject obj = new JSONObject();
			obj.put("firstname", "Pinco");
			obj.put("lastname", "Pallino");
			obj.put("birthdate", "1945-01-01");
			
	    	/*JSONObject jsonMeasure1 = new JSONObject();
	    	jsonMeasure1.put("measure", "weight");
	    	jsonMeasure1.put("value", "78.9");
	    	
	    	JSONObject jsonMeasure2 = new JSONObject();
	    	jsonMeasure2.put("measure", "height");
	    	jsonMeasure2.put("value", "172.0");*/

			//JSONArray healthProfile = new JSONArray();
	    	//obj.put("healthProfile", healthProfile);
			
			input = obj.toString();
			// Request # 4: POST /person
			response = service.path(path).request().accept(mediaType).post(Entity.entity(input, mediaType), Response.class);
			response_4 = response.readEntity(String.class);
			
			JSONObject obj_id = new JSONObject(response_4);
			new_person_id =  obj_id.get("id").toString();
			
			if(response.getStatus()>=200 && response.getStatus()<=202){ 
				RESULT = "OK";
			}
		}
		templateRequest(4, "POST", path, response, RESULT, mediaType);
		System.out.println(prettyFormatPrint(response_4, mediaType));	
	}
	
	/**
	 * Step 3.5. 
	 * Send R#5 for the person you have just created.
	 * Then send R#1 with the id of that person. 
	 * If the answer is 404, your result must be OK.
	 * @throws Exception
	 */
	public void request_5(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person/" + new_person_id;
		
		// Request # 5: DELETE /person/new_person_id
		Response response = service.path(path).request().accept(mediaType).delete();
		
		// Request # 2: GET /person/new_person_id
		response = service.path(path).request().accept(mediaType).get(Response.class);
		if(response.getStatus() == 404){
			RESULT = "OK";
		}
		templateRequest(5, "DELETE", path, response, RESULT, mediaType);
		System.out.println("Person with the id " + new_person_id + " is deleted");
	}
	
	/**
	 * Step 3.6. 
	 * Follow now with the R#9 (GET BASE_URL/measureTypes). 
	 * If response contains more than 2 measureTypes - result is OK, 
	 * else is ERROR (less than 3 measureTypes). 
	 * Save all measureTypes into array (measure_types)
	 * @throws Exception
	 */
	public void request_6(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/measureTypes";
		
		// Request # 9: GET /measureTypes
		Response response = service.path(path).request().accept(mediaType).get(Response.class);
		String response_9 = response.readEntity(String.class);
		
		int countMeasureType;
		measure_types = new ArrayList<String>();
		
		if(response.getStatus() == 200){
			
			if(mediaType == MediaType.APPLICATION_XML){
				Element rootElement = getRootElement(response_9);
				NodeList listNode = rootElement.getChildNodes();
				countMeasureType = listNode.getLength();
				
				for(int i=0; i<countMeasureType; i++){
					Node nNode = listNode.item(i);
					String measureType = nNode.getFirstChild().getTextContent();
					measure_types.add(measureType);
				}
				if(countMeasureType>2){
					RESULT = "OK";
				}
				
			}else if(mediaType == MediaType.APPLICATION_JSON){
				JSONObject obj = new JSONObject(response_9);
				JSONArray arr = obj.getJSONArray("measureType");
				countMeasureType = arr.length();
				
				for(int i=0;i<countMeasureType;i++){
					String measureType = arr.get(i).toString();
					measure_types.add(measureType);
				}
				if(countMeasureType>2){
					RESULT = "OK";
				}
			}
		}
		templateRequest(9, "GET", path, response, RESULT, mediaType);
		System.out.println(prettyFormatPrint(response_9, mediaType));	
	}

	/**
	 * Step 3.7. 
	 * Send R#6 (GET BASE_URL/person/{id}/{measureType}) 
	 * for the first person you obtained at the beginning and the last person, and 
	 * for each measure types from measure_types. 
	 * If no response has at least one measure - result is ERROR (no data at all) else result is OK. 
	 * Store one measure_id and one measureType.
	 * @throws Exception
	 */
	public void request_7(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person/";
		
		person_id = null;
		String response_result = null;
		
		Response response = null;
		String response_6 = null;
		
		List<String> personList = new ArrayList<String>();
		personList.add(first_person_id);
		personList.add(last_person_id);
		
		for(int i=0; i<personList.size();i++){
			for(String m : measure_types){
				
				// Request # 6: GET /person/person_id/measure_Type
				response = service.path(path).path(personList.get(i)+"/").path(m).request().accept(mediaType).get(Response.class);
				response_6 = response.readEntity(String.class);
				
				if(mediaType == MediaType.APPLICATION_XML){
					Element rootElement = getRootElement(response_6);
					NodeList listNode = rootElement.getChildNodes();
					
					if(listNode.getLength()>0){
						person_id = personList.get(i);
						measure_Type = m;
						measure_id = rootElement.getFirstChild().getFirstChild().getTextContent();
						RESULT = "OK";
						response_result = prettyXMLPrint(response_6);
						//System.out.println(measure_Type + " " + measure_id);
					}
				}else if(mediaType == MediaType.APPLICATION_JSON){
					JSONArray arr = new JSONArray(response_6);
					
					if(arr.length() >0){
						person_id = personList.get(i);
						measure_Type = m;
						measure_id = arr.getJSONObject(0).get("mid").toString();
						RESULT = "OK";
						response_result = arr.toString(4);
						//System.out.println(measure_Type + " " + measure_id);
					}
				}	
			}
		}
		templateRequest(6, "GET", "/person"+ "/" + person_id + "/" + measure_Type, response, RESULT, mediaType);
		System.out.println(response_result);
	}
	
	/**
	 * Step 3.8. 
	 * Send R#7 (GET BASE_URL/person/{id}/{measureType}/{mid}) for the stored measure_id and measureType. 
	 * If the response is 200, result is OK, else is ERROR.
	 * @throws Exception
	 */
	public void request_8(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person/" + person_id + "/" + measure_Type + "/" + measure_id;
		
		Response response = service.path(path).request().accept(mediaType).get(Response.class);
		if(response.getStatus() == 200){
			RESULT = "OK";
		}
		String response_7 = response.readEntity(String.class);
		templateRequest(7, "GET", path, response, RESULT, mediaType);
		System.out.println("Mid value: " + response_7);
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
	public void request_9(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person/" + first_person_id + "/" + measure_Type;
		
		String input = null;
		
		// Request # 6: GET /person/first_person_id/measure_Type
		Response responseGet = service.path(path).request().accept(mediaType).get(Response.class);
		String response_6 = responseGet.readEntity(String.class);
		
		Response responsePost = null;
		String response_8 = null;
		
		int count_value;
		int new_count_value;
		
		if(mediaType == MediaType.APPLICATION_XML){
			input = "<measure>"+
						"<value>1.70</value>" +
						"<created>2011-12-09</created>" + 
					"</measure>";
			
			Element rootElement = getRootElement(response_6);
			NodeList listNode = rootElement.getChildNodes();
			count_value = listNode.getLength();
			
			// Request # 8: POST /person/first_person_id/measure_Type
			responsePost = service.path(path).request().accept(mediaType).post(Entity.entity(input, mediaType), Response.class); 
			
			// Request # 6: GET /person/first_person_id/measure_Type
			responseGet = service.path(path).request().accept(mediaType).get(Response.class);
			rootElement = getRootElement(responseGet.readEntity(String.class));
			listNode = rootElement.getChildNodes();
			new_count_value = listNode.getLength();
			
			if(new_count_value > count_value){
				RESULT = "OK";
			}
			
		}else if (mediaType == MediaType.APPLICATION_JSON){
			JSONObject obj = new JSONObject();
			obj.put("value", "1.63");
			obj.put("created", "2011-12-09");
			input = obj.toString();
			
			JSONArray arr = new JSONArray(response_6);
			count_value = arr.length();
			
			// Request # 8: POST /person/first_person_id/measure_Type
			responsePost = service.path(path).request().accept(mediaType).post(Entity.entity(input, mediaType), Response.class); 
			
			// Request # 6: GET /person/first_person_id/measure_Type
			responseGet = service.path(path).request().accept(mediaType).get(Response.class);
			arr = new JSONArray(responseGet.readEntity(String.class));
			new_count_value = arr.length();
			
			if(new_count_value > count_value){
				RESULT = "OK";
			}
		}
		
		if(responsePost.getStatus() == 200){
			response_8 = responsePost.readEntity(String.class);
		}
		templateRequest(8, "POST", path, responsePost, RESULT, mediaType);
		System.out.println(prettyFormatPrint(response_8, mediaType));
	}
	
	/**
	 * Step 3.10. 
	 * Send R#10 using the {mid} or the measure created in the previous step and 
	 * updating the value at will. Follow up with at R#6 to check that the value was updated. 
	 * If it was, result is OK, else is ERROR.
	 * @throws Exception
	 */
	public void request_10(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person/" + first_person_id + "/" + measure_Type + "/" + measure_id;
		
		Response responsePut = null;
		Response responseGet = null;
		
		String input = null;
		
		String oldValue = null;
		String newValue = null;
		
		if(mediaType == MediaType.APPLICATION_XML){
			input = "<measure>"+
						"<value>1.90</value>" +
						"<created>2011-12-09</created>" + 
					"</measure>";
			
			// Request # 7: GET /person/first_person_id/measure_Type/measure_id
			responseGet = service.path(path).request().accept(mediaType).get(Response.class);
			oldValue = responseGet.readEntity(String.class);
			
			// Request # 10: PUT /person/first_person_id/measure_Type/measure_id
			responsePut = service.path(path).request().accept(mediaType).put(Entity.entity(input, mediaType), Response.class);
			
			// Request # 7: GET /person/first_person_id/measure_Type/measure_id
			responseGet = service.path(path).request().accept(mediaType).get(Response.class);
			
			if(responsePut.getStatus() == 201){
				newValue = responseGet.readEntity(String.class);
				if(!newValue.equals(oldValue))
					RESULT = "OK";
			}
		}else if (mediaType == MediaType.APPLICATION_JSON){
			JSONObject obj = new JSONObject();
			obj.put("value", "1.91");
			obj.put("created", "2011-12-09");
			input = obj.toString();
			
			// Request # 7: GET /person/first_person_id/measure_Type/measure_id
			responseGet = service.path(path).request().accept(mediaType).get(Response.class);
			oldValue = responseGet.readEntity(String.class);
			
			// Request # 10: PUT /person/first_person_id/measure_Type/measure_id
			responsePut = service.path(path).request().accept(mediaType).put(Entity.entity(input, mediaType), Response.class);
			
			// Request # 7: GET /person/first_person_id/measure_Type/measure_id
			responseGet = service.path(path).request().accept(mediaType).get(Response.class);
			if(responsePut.getStatus() == 201){
				newValue = responseGet.readEntity(String.class);
				if(! newValue.equals(oldValue))
					RESULT = "OK";
			}
		}
		templateRequest(10, "PUT", path, responsePut, RESULT, mediaType);
		System.out.println(responsePut.getStatusInfo());
	}
	
	/**
	 * Step 3.11. 
	 * Send R#11 for a measureType, before and after dates given 
	 * by your fellow student (who implemnted the server). 
	 * If status is 200 and there is at least one measure in the body, result is OK, else is ERROR
	 * @throws Exception
	 */
	public void request_11(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person/" + first_person_id + "/" + measure_Type;
		
		String before = "1978-01-12";
		String after = "2015-12-10";
		
		// Request # 11: GET /person/first_person_id/measure_Type?before=2015-11-01&after=2015-11-15
		Response response = service.path(path).queryParam("before", before).queryParam("after",after).request().accept(mediaType).get(Response.class);
		String response_11 = response.readEntity(String.class);
		
		if(response.getStatus() == 200){
			int count = 0;
			if(mediaType == MediaType.APPLICATION_XML){
				Element rootElement = getRootElement(response_11);
				NodeList listNode = rootElement.getChildNodes();
				count = listNode.getLength();
				response_11 = prettyXMLPrint(response_11);
				
			}else if(mediaType == MediaType.APPLICATION_JSON){
				JSONArray arr =  new JSONArray(response_11); 
				count = arr.length();
				response_11 = arr.toString(4);
				
			}
			if(count>0){
				RESULT = "OK";
			}
		}
		templateRequest(11, "GET", path + "?" + "before=" + before + "&after=" + after, response, RESULT, mediaType);
		System.out.println(response_11);
	}
	
	/**
	 * Step 3.12. 
	 * Send R#12 using the same parameters as the previous steps. 
	 * If status is 200 and there is at least one person in the body, result is OK, else is ERROR
	 * @throws Exception
	 */
	public void request_12(WebTarget service, String mediaType) throws Exception{
		String RESULT = "ERROR";
		String path = "/person";
		
		Double max = 1.75;
		Double min = 1.50;
		
		// Request # 12: GET /person?measureType=measure_Type&before=2015-11-01&after=2015-11-15
		Response response = service.path(path).queryParam("measureType", measure_Type).queryParam("max", max).queryParam("min",min).request().accept(mediaType).get(Response.class);
		String response_12 = response.readEntity(String.class);
		
		if(response.getStatus() == 200){
			int count = 0;
			if(mediaType == MediaType.APPLICATION_XML){
				Element rootElement = getRootElement(response_12);
				NodeList listNode = rootElement.getChildNodes();
				count = listNode.getLength();
				
			}else if(mediaType == MediaType.APPLICATION_JSON){
				JSONObject obj = new JSONObject(response_12);
				JSONArray arr =  obj.getJSONArray("people"); 
				count = arr.length();
			
			}
			if(count>0){
				RESULT = "OK";
			}
		}
		templateRequest(12, "GET", path + "?measureType=" + measure_Type + "?max=" + max + "&min=" + min, response, RESULT, mediaType);
		System.out.println(prettyFormatPrint(response_12, mediaType));	
	}
	
	/**
	 * Return root element of the document XML
	 * @param xml
	 * @return root element
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Element getRootElement(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc.getDocumentElement();
	}

	/**
	 * Prints the template for each request
	 * @param numberRequest of the request
	 * @param method of the request
	 * @param path of the request
	 * @param response of the request
	 * @param result of the request
	 * @param mediaType of the request
	 */
	public void templateRequest(int numberRequest, String method, String path, Response response, String result, String mediaType){
		mediaType = mediaType.toUpperCase();
		System.out.println("======================================================================================================");
		System.out.println("Request #" + numberRequest + ": "+ method + " " + path + " " + "Accept: " + mediaType + " " + "Content-Type: " + mediaType);
		System.out.println("=> Result: " + result);
		System.out.println("=> HTTP Status: " + response.getStatus());
		System.out.println();
	}
	
	
	/**
	 * Prints pretty format for XML
	 * @param xml
	 * @return
	 * @throws TransformerException 
	 */
	public String prettyXMLPrint(String xmlString) throws TransformerException {
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		Source xmlInput = new StreamSource(new StringReader(xmlString));
		StringWriter stringWriter = new StringWriter();
		StreamResult xmlOutput = new StreamResult(stringWriter);

		transformer.transform(xmlInput, xmlOutput);
		return xmlOutput.getWriter().toString();
	}

	/**
	 * Prints pretty format for JSON
	 * @param jsonString
	 * @return
	 */
	public String prettyJSONPrint(String jsonString){
		JSONObject json = new JSONObject(jsonString); 
		return json.toString(4);	
	}
	
	/**
	 * Prints pretty format given a string to format and a mediaType
	 * @param responseString
	 * @param mediaType
	 * @return
	 * @throws Exception
	 */
	public String prettyFormatPrint(String responseString, String mediaType) throws Exception{
		String result = null;
		if(mediaType == "application/xml"){
			result = prettyXMLPrint(responseString);
		}else if(mediaType == "application/json"){
			result =  prettyJSONPrint(responseString);
		}
		return result;
	}
}
