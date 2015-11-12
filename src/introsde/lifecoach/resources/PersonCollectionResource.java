package introsde.lifecoach.resources;

import introsde.lifecoach.model.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
@Path("/person")
public class PersonCollectionResource {

	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	// will work only inside a Java EE application
	@PersistenceUnit(unitName = "introsde-jpa")
	EntityManager entityManager;

	// will work only inside a Java EE application
	@PersistenceContext(unitName = "introsde-jpa", type = PersistenceContextType.TRANSACTION)
	private EntityManagerFactory entityManagerFactory;

	/**
	 * Request #1: GET/person
	 * @return list all person into DB
	 */
	@GET
	@Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_JSON,
			MediaType.APPLICATION_XML })
	public List<Person> getPersonsBrowser() {
		System.out.println("==========================================================================================================================");
		System.out.println("\t\t\t\t\tRequest #1 : GET /person");
		System.out.println("==========================================================================================================================");
		System.out.println();
		System.out.println("Getting list of people...");
		List<Person> people = Person.getAll();
		for (Person p : people) {
			System.out.println(p.toString());
		}
		return people;
	}

	// retuns the number of people
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		System.out.println("Getting count...");
		List<Person> people = Person.getAll();
		int count = people.size();
		return String.valueOf(count);
	}

	/**
	 * Request #4: POST/person
	 * @param person
	 * @return the newly created person with its assigned id and if a
	 *         healthprofile is included, create also those measurements for the
	 *         new person
	 * @throws IOException
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Person newPerson(Person person) throws IOException {
		System.out.println("Creating new person...");
		if (person.getLifeStatus() == null) {
			return Person.savePerson(person);
		} else {
			// DA COMPLETARE
			return Person.savePerson(person);
		}
	}

	/**
	 * Request #6: GET/person/{id}/{measureType}
	 * @param idPerson
	 * @param measureType
	 * @return the list of values the MeasureHistory of {measureType} for person
	 *         identified by {idPerson}
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{idPerson}/{measureType}")
	public List<HealthMeasureHistory> getMeasureHistories(
			@PathParam("idPerson") int idPerson,
			@PathParam("measureType") String measureType) {
		System.out.println("==========================================================================================================================");
		System.out.println("\t\t\t\t\tRequest #6 : GET /person/" + idPerson + "/" + measureType);
		System.out.println("==========================================================================================================================");
		System.out.println();
		List<HealthMeasureHistory> measureHistory = HealthMeasureHistory
				.getMeasureHistoryByMeasureType(idPerson, measureType);
		if (measureHistory.isEmpty())
			throw new RuntimeException("Get: Person with " + idPerson
					+ " and with " + measureType + " not found");
		return measureHistory;
	}

	/*
	 * public HealthMeasureHistoryListWrapper
	 * getMeasureHistory(@PathParam("idPerson") int idPerson,
	 * 
	 * @PathParam("measureType") String measureType){
	 * HealthMeasureHistoryListWrapper hp = new
	 * HealthMeasureHistoryListWrapper();
	 * System.out.println("Getting MeasureHistory to " + idPerson +
	 * " measureType " + measureType);
	 * hp.setListMeasure(HealthMeasureHistory.getHealthMeasureHistory(idPerson,
	 * measureType)); for(HealthMeasureHistory h : hp.getListMeasure()){
	 * System.out.println(h.toString()); } return hp; }
	 */

	/**
	 * Request #7: GET/person/{id}/{measureType}/{mid}
	 * @param idPerson
	 * @param measureType
	 * @param mid
	 * @return the value of {measureType} identified by {mid} for person identified by {idPerson}
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Path("{idPerson}/{measureType}/{mid}")
	public HealthMeasureHistory getMeasureHistory(
			@PathParam("idPerson") int idPerson,
			@PathParam("measureType") String measureType,
			@PathParam("mid") int mid) {
		System.out.println("==========================================================================================================================");
		System.out.println("\t\t\t\t\tRequest #7 : GET /person/" + idPerson + "/" + measureType + "/" + mid);
		System.out.println("==========================================================================================================================");
		System.out.println();
		HealthMeasureHistory measureHistory = HealthMeasureHistory
				.getMeasureHistoryByMid(idPerson, measureType, mid);
		if (measureHistory == null)
			throw new RuntimeException("Get: Person with " + idPerson
					+ " with " + measureType + " with " + mid + " not found");
		return measureHistory;
	}

	// Defines that the next path parameter after the base url is
	// treated as a parameter and passed to the PersonResources
	// Allows to type http://localhost:599/base_url/1
	// 1 will be treaded as parameter todo and passed to PersonResource
	/**
	 * Request #2: GET/person/{id}
	 * @param id
	 * @return all the personal information plus current measures of person identified by {id}
	 */
	@Path("{personId}")
	public PersonResource getPerson(@PathParam("personId") int id) {
		System.out.println("==========================================================================================================================");
		System.out.println("\t\t\t\t\tRequest #2 : GET /person/" + id);
		System.out.println("==========================================================================================================================");
		System.out.println();
		System.out.println("Get: Person with " + id + " found");
		return new PersonResource(uriInfo, request, id);
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
	@Path("{idPerson}/{measureType}/{mid}")
	public Response putMeasureType(@PathParam("idPerson") int idPerson,
			@PathParam("measureType") String measureType,
			@PathParam("mid") int mid, HealthMeasureHistory measureHistory)
			throws Exception {
		System.out.println("==========================================================================================================================");
		System.out.println("\t\t\t\t\tRequest #10 : PUT /person/" + idPerson + "/" + measureType + "/" + mid);
		System.out.println("==========================================================================================================================");
		System.out.println();

		Calendar calendar = Calendar.getInstance();

		Response res;
		HealthMeasureHistory existing = HealthMeasureHistory
				.getHealthMeasureHistoryById(mid);

		if (existing == null) {
			System.out.println("Update: MeasureType with " + mid + " not found");
			res = Response.noContent().build();

		} else {
			System.out.println("Update: MeasureType with " + mid + " found");
			res = Response.created(uriInfo.getAbsolutePath()).build();

			measureHistory.setIdMeasureHistory(mid);
			measureHistory.setPerson(existing.getPerson());
			measureHistory
					.setMeasureDefinition(existing.getMeasureDefinition());
			if (measureHistory.getValue() == null) {
				measureHistory.setValue(existing.getValue());
			}
			measureHistory.setTimestamp(calendar.getTime()); //CONVERTER DATE TO DATABASECOLUMN 
			HealthMeasureHistory.updateHealthMeasureHistory(measureHistory);
			
			Person person = Person.getPersonById(idPerson); 
			List<LifeStatus> lifeStatusList = person.getLifeStatus();
			System.out.println(lifeStatusList.toString());
			for(LifeStatus lifeStatus : lifeStatusList){
				if(lifeStatus.getMeasureDefinition().getIdMeasureDef() == measureHistory.getMeasureDefinition().getIdMeasureDef()){
					lifeStatus.setIdMeasure(lifeStatus.getIdMeasure());
					lifeStatus.setMeasureDefinition(lifeStatus.getMeasureDefinition());
					lifeStatus.setPerson(person);
					lifeStatus.setValue(measureHistory.getValue());
					LifeStatus.updateLifeStatus(lifeStatus);
				}
				
			}
			 
		}
		return res;
	}
}
