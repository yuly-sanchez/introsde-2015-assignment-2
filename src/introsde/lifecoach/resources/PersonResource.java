package introsde.lifecoach.resources;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import introsde.lifecoach.converter.DateConverter;
import introsde.lifecoach.model.*;
import introsde.lifecoach.wrapper.HealthMeasureHistoryListWrapper;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless // only used if the the application is deployed in a Java EE container
@LocalBean // only used if the the application is deployed in a Java EE container
public class PersonResource {
	
	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	int id;

	EntityManager entityManager; // only used if the application is deployed in a Java EE container

	public PersonResource(UriInfo uriInfo, Request request, int id, EntityManager em) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		this.entityManager = em;
	}

	public PersonResource(UriInfo uriInfo, Request request, int id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
	}

	// Application integration
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Person getPerson() {
		Person person = this.getPersonById(id);
		if (person == null)
			throw new RuntimeException("Get: Person with " + id + " not found");
		return person;
	}

	// for the browser
	@GET
	@Produces(MediaType.TEXT_XML)
	public Person getPersonHTML() {
		Person person = this.getPersonById(id);
		if (person == null)
			throw new RuntimeException("Get: Person with " + id + " not found");
		System.out.println("Returning person... " + person.getIdPerson());
		return person;
	}

	/**
	 * Request #3: PUT/person/{id}
	 * @param person
	 * @return the personal information of the person identified by id updated
	 */
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response putPerson(Person person) {
		
		System.out.println("--> REQUESTED: putPerson("+id+")");
		System.out.println();
		
        Response res;
		Person existing = this.getPersonById(this.id);
		
		if (existing == null) {
			System.out.println("Update: Person with " + this.id + " not found");
			res = Response.noContent().build();
		} else {
			System.out.println("Update: Person with " + this.id + " found");
			res = Response.created(uriInfo.getAbsolutePath()).build();
			
			person.setIdPerson(this.id);
			if(person.getName() == null){
				person.setName(existing.getName());
			}
			if(person.getLastname() == null){
				person.setLastname(existing.getLastname());
			}
			if(person.getBirthdate() == null){
				person.setBirthdate(existing.getBirthdate());
			}
			
			person.setLifeStatus(existing.getLifeStatus());
			Person.updatePerson(person);	
		}
		return res;
	}

	
	/**
	 * Request #5: DELETE/person/{id}
	 * delete the person identified by {id} from the database
	 */
	@DELETE
	public void deletePerson() {
		System.out.println("--> REQUESTED: deletePerson("+id+")");
		System.out.println();
		Person person = this.getPersonById(this.id);
		if (person == null)
			throw new RuntimeException("Delete: Person with " + this.id + " not found");
		System.out.println("--> Delete: Person with " + this.id + " found");
		Person.removePerson(person);
	}

	public Person getPersonById(int personId) {
		System.out.println("--> Reading person from DB with id: " + personId);
		Person person = Person.getPersonById(personId);
		System.out.println(person.toString());
		//System.out.println(person.getLifeStatus().size());
		return person;
	}
	
	/**
	 * Request #6: GET/person/{id}/{measureType}
	 * Request #11: GET/person/{id}/{measureType}?before={beforeDate}&after={afterDate}
	 * @param idPerson
	 * @param measureType
	 * @return the list of values the MeasureHistory of {measureType} for person
	 *         identified by {idPerson}
	 * @throws Exception 
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{measureType}")	
	public List<HealthMeasureHistory> getMeasureHistories(@PathParam("measureType") String measureType,
														  @QueryParam("before") String beforeDate,
														  @QueryParam("after") String afterDate) throws Exception {
		
		List<HealthMeasureHistory> measureHistories = null;
		
		Person p = this.getPersonById(id);
		MeasureDefinition md = MeasureDefinition.getMeasureDefinition(measureType);
		
		if(beforeDate==null && afterDate==null){
			System.out.println("--> REQUESTED: getMeasureHistories("+id+", "+measureType+")");
			System.out.println();
			measureHistories = HealthMeasureHistory.getMeasureHistoryByMeasureType(p, md);
			return measureHistories;
			
		}else if(beforeDate==null && afterDate!=null){
			System.out.println("--> REQUESTED: getMeasureHistories("+id+", "+measureType+")");
			System.out.println();
			measureHistories = HealthMeasureHistory.getMeasureHistoryByMeasureType(p, md);
			return measureHistories;
			
		}else if(beforeDate!=null && afterDate==null){
			System.out.println("--> REQUESTED: getMeasureHistories("+id+", "+measureType+")");
			System.out.println();
			measureHistories = HealthMeasureHistory.getMeasureHistoryByMeasureType(p, md);
			return measureHistories;
			
		}else{
			System.out.println("--> REQUESTED: getMeasureHistories("+id+", "+measureType+", "+beforeDate+", "+afterDate+")");
			System.out.println();
			
			Calendar before = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			before.setTime(dateFormat.parse(beforeDate));
			
			Calendar after = Calendar.getInstance();
			after.setTime(dateFormat.parse(afterDate));
			
			measureHistories = HealthMeasureHistory.getFilterByDatesHistory(p, md, before, after);
			return measureHistories;
		}

	}

	
	/**
	 * Request #7: GET/person/{id}/{measureType}/{mid}
	 * @param idPerson
	 * @param measureType
	 * @param mid
	 * @return the value of {measureType} identified by {mid} for person identified by {idPerson}
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{measureType}/{mid}")
	public String getValueMeasureHistory(@PathParam("measureType") String measureType,
										 @PathParam("mid") int mid) {
		System.out.println("--> REQUESTED: getValueMeasureHistory("+this.id+", "+measureType+", "+mid+")");
		System.out.println();
		Person p = this.getPersonById(id);
		MeasureDefinition md = MeasureDefinition.getMeasureDefinition(measureType);
		HealthMeasureHistory measureHistory = null;
		String value = null;
		try{
			measureHistory = HealthMeasureHistory.getMeasureHistoryByMid(p, md, mid);
			value = measureHistory.getValue();
		}catch(Exception ex){
			System.out.println(ex.getMessage());
			throw new RuntimeException("Get: Person with " + id + " with " + measureType + " with " + mid + " not found");
		}
		return value;
	}

	
	/**
	 * Request #10: PUT/person/{id}/{measureType}/{id}
	 * @param idPerson
	 * @param measureType
	 * @param mid
	 * @param measureHistory
	 * @return
	 * @throws Exception
	 */
	@PUT
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{measureType}/{mid}")
	public Response putMeasureType(
			@PathParam("measureType") String measureType,
			@PathParam("mid") int mid, HealthMeasureHistory measureHistory)
			throws Exception {
		System.out.println("--> REQUESTED: putMeasureType("+this.id+", "+measureType+", "+mid+", "+measureHistory.getValue()+")");
		System.out.println();

		Calendar calendar = Calendar.getInstance();

		Response res;
		HealthMeasureHistory existing = HealthMeasureHistory.getHealthMeasureHistoryById(mid);

		if (existing == null) {
			System.out.println("--> Update: MeasureType with " + mid + " not found");
			res = Response.noContent().build();

		} else {
			System.out.println("--> Update: MeasureType with " + mid + " found");
			res = Response.created(uriInfo.getAbsolutePath()).build();
			existing.setValue(measureHistory.getValue());
			existing.setTimestamp(calendar.getTime());
			HealthMeasureHistory.updateHealthMeasureHistory(existing);
		}
		return res;
	}
	
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("{measureType}")
	public LifeStatus newValueMeasure(@PathParam("measureType") String measureType,
							HealthMeasureHistory measureHistory){
		System.out.println("--> REQUESTED: saveNewValueMeasure("+this.id+", "+measureType+")");
		System.out.println();
		
		MeasureDefinition md = MeasureDefinition.getMeasureDefinition(measureType);
		Person person = Person.getPersonById(this.id);
		Person p = Person.getFilteredPersonSaveNewValueMeasure(person, md);
		return null;
	}
}
