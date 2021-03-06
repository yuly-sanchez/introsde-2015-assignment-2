package introsde.lifecoach.resources;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import introsde.lifecoach.model.*;
import introsde.lifecoach.wrapper.MeasureHistoryWrapper;

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

	
	/**
	 * Request #2: GET/person/{id} should give all the personal information plus current measures of person identified by {id} 
	 * @param id 
	 * @return the person associated to the {id}
	 */
	@GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response getPerson() {
        Person person = this.getPersonById(id);
        if (person == null)
           return Response.status(Response.Status.NOT_FOUND)
        		   .entity("Get: Person with " + id + " not found").build();
        else
           return Response.ok(person).build();
    }
	
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
	 * Request #3: PUT/person/{id} should update the personal information of the person identified by {id} 
	 * @param person
	 * @return the response of the put operation 
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
			//check the personal information of the person passed as input 
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
			res = Response.ok().build();
			Person.updatePerson(person);
			
		}
		return res;
	}

	
	/**
	 * Request #5: DELETE/person/{id} delete the person identified by {id} from the database
	 */
	@DELETE
	public void deletePerson() {
		System.out.println("--> REQUESTED: deletePerson("+id+")");
		System.out.println();
		Person person = this.getPersonById(this.id);
		if (person == null)
			throw new RuntimeException("Delete: Person with " + this.id + " not found");
		Person.removePerson(person);
	}

	public Person getPersonById(int personId) {
		System.out.println("--> Reading person from DB with id: " + personId);
		Person person = Person.getPersonById(personId);
		return person;
	}
	
	/**
	 * Request #6: GET/person/{id}/{measureType} 
	 * should return the list of values (the history) of {measureType} for person identified by {id}
	 * 
	 * Request #11: GET/person/{id}/{measureType}?before={beforeDate}&after={afterDate}
	 * should return the history of {measureType} for person {id} in the specified range of date
	 * @param measureType
	 * @return the list of the MeasureHistory
	 * @throws Exception 
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{measureType}")	
	public MeasureHistoryWrapper getMeasureHistories(@PathParam("measureType") String measureType,
														  @QueryParam("before") String beforeDate,
														  @QueryParam("after") String afterDate) throws Exception {
		
		List<HealthMeasureHistory> measureHistories = null;
		MeasureHistoryWrapper measureWrapper = new MeasureHistoryWrapper();
		
		Person p = this.getPersonById(id);
		MeasureDefinition md = MeasureDefinition.getMeasureDefinition(measureType);
		
		if(beforeDate==null && afterDate==null){
			System.out.println("--> REQUESTED: getMeasureHistories("+id+", "+measureType+")");
			System.out.println();
			measureHistories = HealthMeasureHistory.getMeasureHistoryByMeasureType(p, md);
			measureWrapper.setListMeasureHistory(measureHistories);
			
		}else{
			System.out.println("--> REQUESTED: getMeasureHistories("+id+", "+measureType+", "+beforeDate+", "+afterDate+")");
			System.out.println();
			
			Calendar before = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			before.setTime(dateFormat.parse(beforeDate));
			
			Calendar after = Calendar.getInstance();
			after.setTime(dateFormat.parse(afterDate));
			
			measureHistories = HealthMeasureHistory.getFilterByDatesHistory(p, md, before, after);
			measureWrapper.setListMeasureHistory(measureHistories);
		}
		return measureWrapper;
	}

	
	/**
	 * Request #7: GET/person/{id}/{measureType}/{mid} 
	 * should return the value of {measureType} identified by {mid} for person identified by {id}
	 * @param measureType
	 * @param mid
	 * @return a string representing the value of the HealthMeasureHistory identified by {mid}
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
			throw new RuntimeException("Get: Person with " + id + " with " + measureType + " with " + mid + " not found");
		}
		return value;
	}

	
	/**
	 * Request #10: PUT/person/{id}/{measureType}/{id} 
	 * should update the value for the {measureType} identified by {mid}, related to the person identified by {id}
	 * @param measureType
	 * @param mid of the HealthMeasureHistory to update
	 * @param measureHistory object
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
		
		Response res;
		//find measureHistory given a mid
		HealthMeasureHistory existing = HealthMeasureHistory.getHealthMeasureHistoryById(mid);

		if (existing == null) {
			System.out.println("--> Update: MeasureType with " + mid + " not found");
			res = Response.noContent().build();

		} else {
			System.out.println("--> Update: MeasureType with " + mid + " found");
			res = Response.created(uriInfo.getAbsolutePath()).build();
			existing.setValue(measureHistory.getValue());
			if(measureHistory.getTimestamp() != null){
				existing.setTimestamp(measureHistory.getTimestamp());
			}
			//update a value of the measureHistory into db 
			HealthMeasureHistory.updateHealthMeasureHistory(existing);
		}
		return res;
	}
	
	/**
	 * Request #8: POST /person/{id}/{measureType} 
	 * should save a new value for the {measureType} (e.g. weight) of person identified by {id} and 
	 * archive the old value in the history
	 * @param measureType
	 * @param measureHistory object
	 * @return the new LifeStatus
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("{measureType}")
	public LifeStatus createNewMeasureType(@PathParam("measureType") String measureType,
							HealthMeasureHistory measureHistory){
		System.out.println("--> REQUESTED: createNewMeasureType("+this.id+", "+measureType+")");
		System.out.println();
		
		//find a measureDefinition associated with the name of the measure
		MeasureDefinition measureDef = MeasureDefinition.getMeasureDefinition(measureType);
		//find a person identified by a id
		Person person = Person.getPersonById(this.id);
		System.out.println(measureType + " " + this.id);
		
		//remove current lifeStatus for a specified person and measureDefinition 
		LifeStatus ls = LifeStatus.getFilteredLifeStatus(measureDef, person);
		if(ls != null){
			LifeStatus.removeLifeStatus(ls);
		}
		
		//save new LifeStatus into db
		LifeStatus newLifeStatus = new LifeStatus(measureDef, measureHistory.getValue(), person);
		newLifeStatus = LifeStatus.saveLifeStatus(newLifeStatus);
		
		//set measureDefinition for measureName
		measureHistory.setMeasureDefinition(measureDef);
		//set person of the new MeasureHistory
		measureHistory.setPerson(person);
		//archive a new measure value in the history and save into db 
		HealthMeasureHistory.saveHealthMeasureHistory(measureHistory);
		
		return LifeStatus.getLifeStatusById(newLifeStatus.getIdMeasure());
	}
}
