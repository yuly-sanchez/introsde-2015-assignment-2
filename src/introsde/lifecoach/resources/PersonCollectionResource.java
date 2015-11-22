package introsde.lifecoach.resources;
import introsde.lifecoach.model.*;
import introsde.lifecoach.wrapper.PeopleWrapper;

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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;


@Stateless 
@LocalBean 
@Path("/person")
public class PersonCollectionResource {

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
	 * Returns the list all people into DB
	 * 
	 * Request #12: GET/person?measureType={measureType}&max={max}&min={min}
	 * Retrieves people whose {measureType} value is in the [{min},{max}] range 
	 * 
	 * @return list of the people
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	 public PeopleWrapper getPeople (@QueryParam("measureType") String measureType,
			 					   	 @QueryParam("max") Double max,
			 					     @QueryParam("min") Double min) {
		
	 List<Person> people = new ArrayList<Person>();
	 
	 PeopleWrapper peopleWrapper = new PeopleWrapper();
	 
	 if(measureType!=null && (max!=null || min!=null)){ //--->DA CONTROLLARE non entra
		 min = min==null ? 0. : min ;
		 max = max==null ? 300. : max;
		 System.out.println("--> REQUEST: Retrieves people whose " +measureType +" value is in the ["+min+","+max+"] range");
		 System.out.println();
		 MeasureDefinition md = MeasureDefinition.getMeasureDefinition(measureType);
		 people = Person.getFilteredPersonByValuesOfRange(md, min, max);
		 peopleWrapper.setListPeople(people);
		 
	 }else{
		 System.out.println("--> REQUESTED: Getting list of people...");
		 System.out.println(); 
		 people = Person.getAll();
		 peopleWrapper.setListPeople(people);
	 }
	 return peopleWrapper;
}
	 

	/**
	 * Returns the number of people saved into db
	 * @return a string representing the number of the people
	 */
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
	 * should create a new person and return the newly created person with its assigned id and 
	 * if a healthprofile is included, create also those measurements for the new person
	 * @param person
	 * @return the person saved in the database 
	 * @throws IOException
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Person createNewPerson(Person person) throws IOException {
		
		System.out.println("--> REQUESTED: createNewPerson(" + person.toString() + ")");

		//checks if person include the tag HealthProfile (called LifeStatus) 
		if (person.getLifeStatus() == null) {
			System.out.println("--> Creating new person without LifeStatus...");
			return Person.savePerson(person);

		} else {
			System.out.println("--> Creating new person with LifeStatus...");

			//store the new lifestatus of the person passed as input in the another variable 
			List<LifeStatus> personLifeStatus = new ArrayList<LifeStatus>();
			personLifeStatus.addAll(person.getLifeStatus());
		
			//remove the lifeStatus in the person passed as input
			person.setLifeStatus(null);
			
			//save the person into db and retrieve the id
			Person p = Person.savePerson(person);
			int personId = p.getIdPerson();
			
			// checks the list of the lifeStatus saved
			for (int i=0;i<personLifeStatus.size();i++) {
				
				//obtain a lifeStatus of the list
				LifeStatus lifeS = personLifeStatus.get(i);
				
				// find the measureDefinition associated with the name of the measure
				MeasureDefinition md = MeasureDefinition.getMeasureDefinition(lifeS.getMeasureDefinition().getMeasureName());
				lifeS.setMeasureDefinition(md);
				//System.out.println("--> lifeSNewPerson-Measure: " + lifeS.getMeasureDefinition());

				// associated the lifeStatus with the person
				lifeS.setPerson(p);
				
				//save lifeStatus into db
				LifeStatus.saveLifeStatus(lifeS);
				
				Calendar calendar = Calendar.getInstance();
				
				// archive the new value of the lifeStatus also in the history
				HealthMeasureHistory hm = new HealthMeasureHistory();
				hm.setMeasureDefinition(md);
				hm.setPerson(p);
				hm.setTimestamp(calendar.getTime());
				hm.setValue(lifeS.getValue());
				//save a new measure into db 
				HealthMeasureHistory.saveHealthMeasureHistory(hm);
			}
			
			return Person.getPersonById(personId);
		}
	}

	/**
	 * Defines that the next path parameter after the base url is treated as a parameter and 
	 * passed to the PersonResources 
	 * Allows to type http://localhost:599/base_url/1 
	 * 1 will be treaded as parameter todo and passed to PersonResource
	 * @param id
	 * @return
	 */
	@Path("{personId}")
	public PersonResource getPerson(@PathParam("personId") int id) {
		return new PersonResource(uriInfo, request, id);
	}
}
