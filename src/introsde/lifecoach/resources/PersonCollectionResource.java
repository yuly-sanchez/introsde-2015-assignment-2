package introsde.lifecoach.resources;

import introsde.lifecoach.dao.LifeCoachDao;
import introsde.lifecoach.model.*;

import java.io.IOException;
import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Stateless
// will work only inside a Java EE application
@LocalBean
// will work only inside a Java EE application
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
	 * 
	 * @return list all person into DB
	 */
	/**
	 * Request #12: GET/person?measureType={measureType}&max={max}&min={min}
	 * 
	 * @return list all person into DB
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON,
			MediaType.APPLICATION_XML })
	public List<Person> getPeople() {

		System.out.println("--> REQUESTED: Getting list of people...");
		System.out.println();
		List<Person> people = Person.getAll();
		return people;
	}

	/*
	 * public List<Person> getPeople(@QueryParam("measureType") String
	 * measureType,
	 * 
	 * @QueryParam("max") String max,
	 * 
	 * @QueryParam("min") String min) {
	 * 
	 * List<Person> people = null; MeasureDefinition md =
	 * MeasureDefinition.getMeasureDefinition(measureType); Double max_double =
	 * Double.valueOf(max); Double min_double = Double.valueOf(min);
	 * 
	 * if(measureType != null && (max != null && min == null)){
	 * System.out.println("--> REQUESTED: getPeople(" + measureType + ", " + max
	 * + ")"); System.out.println();
	 * System.out.println("--> Getting all of people fulfil a condition...");
	 * people = Person.getFilteredPersonByMaxValue(md, max_double); return
	 * people; }else if(measureType != null && (max == null && min != null)){
	 * System.out.println("--> REQUESTED: getPeople(" + measureType + ", " + min
	 * + ")"); System.out.println();
	 * System.out.println("--> Getting all of people fulfil a condition...");
	 * people = Person.getFilteredPersonByMinValue(md, min_double); return
	 * people; }
	 * 
	 * System.out.println("--> REQUESTED: Getting list of people...");
	 * System.out.println(); people = Person.getAll();
	 * 
	 * return people;
	 * 
	 * if (measureType == null && max == null && min == null) {
	 * System.out.println("--> REQUESTED: Getting list of people...");
	 * System.out.println(); people = Person.getAll(); for (Person p : people) {
	 * System.out.println(p.toString()); } return people; } else {
	 * System.out.println("--> REQUESTED: getPeople(" + measureType + ", " + min
	 * + ", " + max + ")"); System.out.println(); System.out
	 * .println("--> Getting all of people fulfil a condition..."); people =
	 * Person.getFilteredPersonByValuesOfRange(md, min_double,max_double);
	 * return people; } }
	 */

	// retuns the number of people
	// to get the total number of records
	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		System.out.println("--> Getting count...");
		List<Person> people = Person.getAll();
		int count = people.size();
		return String.valueOf(count);
	}

	/**
	 * Request #4: POST/person
	 * 
	 * @param person
	 * @return the newly created person with its assigned id and if a
	 *         healthprofile is included, create also those measurements for the
	 *         new person
	 * @throws IOException
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Person createNewPerson(Person person) throws IOException {
		
		System.out.println("--> REQUESTED: createNewPerson(" + person.toString() + ")");

		if (person.getLifeStatus() == null) {
			System.out.println("--> Creating new person without LifeStatus...");
			return Person.savePerson(person);

		} else {
			System.out.println("--> Creating new person with LifeStatus...");

			List<LifeStatus> personLifeStatus = person.getLifeStatus();
			System.out.println("--> LifeStatusPersonSize: " + person.getLifeStatus().size());

			// creo una nuova lista di lifeStatus
			List<LifeStatus> newList = new ArrayList<LifeStatus>();
			
			// salvo la persona per ricavarmi l'id
			Person p = Person.savePerson(person);
			person.setIdPerson(p.getIdPerson());
			
			// controlliamo il contenuto del lifeStatus appena slavato
			// Una volta salvato tutta la lista lifeStatus nuova
			for (int i=0;i<personLifeStatus.size();i++) {
				
				LifeStatus lifeS = new LifeStatus();

				// setto la measureDefinition
				int idMeasureDef=0;
				switch (personLifeStatus.get(i).getMeasureDefinition().getMeasureName()) {
				case "weight":
					idMeasureDef = 1;
					break;
				case "height":
					idMeasureDef = 2;
					break;
				case "steps":
					idMeasureDef = 3;
					break;
				case "blood pressure":
					idMeasureDef = 4;
					break;
				case "heart rate":
					idMeasureDef = 5;
					break;
				case "bmi":
					idMeasureDef = 6;
					break;
				default:
					break;
				}

				MeasureDefinition md = MeasureDefinition.getMeasureDefinitionById(idMeasureDef);
				lifeS.setMeasureDefinition(md);
				System.out.println("--> lifeSNewPerson-Measure: " + lifeS.getMeasureDefinition());

				// setto il value
				lifeS.setValue(personLifeStatus.get(i).getValue());
				System.out.println("--> lifeSNewPerson-Value: " + lifeS.getValue());
				
				// setto la persona
				lifeS.setPerson(person);
				System.out.println("--> lifeSNewPerson-Person: " + lifeS.getPerson());
				
				LifeStatus lifeStatus = LifeStatus.saveLifeStatus(lifeS);
				lifeS.setIdMeasure(lifeStatus.getIdMeasure());
				newList.add(lifeS);
			}
			
			// rimuovo tutto il suo contenuto
			person.getLifeStatus().clear();
			System.out.println("--> LifeStatusPersonSize-Pulito: " + person.getLifeStatus().size());
			
			//aggiungo la nuova lista con i nuovi lifeStatus
			person.setLifeStatus(newList);
			System.out.println("--> LifeStatusPersonSize-Ricreato: " + person.getLifeStatus().size());
			
			return person;
		}
	}

	// Defines that the next path parameter after the base url is
	// treated as a parameter and passed to the PersonResources
	// Allows to type http://localhost:599/base_url/1
	// 1 will be treaded as parameter todo and passed to PersonResource
	/**
	 * Request #2: GET/person/{id}
	 * 
	 * @param id
	 * @return all the personal information plus current measures of person
	 *         identified by {id}
	 */
	@Path("{personId}")
	public PersonResource getPerson(@PathParam("personId") int id) {
		System.out.println("--> REQUESTED: getPerson(" + id + ")");
		System.out.println();
		System.out.println("--> Get: Person with " + id + " found");
		return new PersonResource(uriInfo, request, id);
	}
}
