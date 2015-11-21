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


@Stateless // will work only inside a Java EE application
@LocalBean // will work only inside a Java EE application
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
	 * @return list all person into DB
	 * Request #12: GET/person?measureType={measureType}&max={max}&min={min}
	 * @return list all person into DB
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
	 

	// retuns the number of people
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

			//store the new lifestatus of the person created in a new list 
			List<LifeStatus> personLifeStatus = new ArrayList<LifeStatus>();
			personLifeStatus.addAll(person.getLifeStatus());
		
			//cancel lifeStatus of the person 
			person.setLifeStatus(null);
			
			//save of the person to obtain a person id
			Person p = Person.savePerson(person);
			int personId = p.getIdPerson();
			
			// check the list of the lifeStatus saved
			for (int i=0;i<personLifeStatus.size();i++) {
				
				//obtain one lifeStatus of the list
				LifeStatus lifeS = personLifeStatus.get(i);
				
				// set measureDefiniton of the one lifeStatus
				MeasureDefinition md = MeasureDefinition.getMeasureDefinition(lifeS.getMeasureDefinition().getMeasureName());
				lifeS.setMeasureDefinition(md);
				//System.out.println("--> lifeSNewPerson-Measure: " + lifeS.getMeasureDefinition());

				// set person of the one lifeStatus 
				lifeS.setPerson(p);
				
				//save lifeStatus into db
				LifeStatus.saveLifeStatus(lifeS);
				
				Calendar calendar = Calendar.getInstance();
				
				HealthMeasureHistory hm = new HealthMeasureHistory();
				//set measureDefinition of the new measure
				hm.setMeasureDefinition(md);
				//set person of the of the new measure
				hm.setPerson(p);
				//set created date of the new measure
				hm.setTimestamp(calendar.getTime());
				//set value of the new measure
				hm.setValue(lifeS.getValue());
				
				//save a lifeStatus (new measure) into table of the MeasureHistory db 
				HealthMeasureHistory.saveHealthMeasureHistory(hm);
			}
			
			return Person.getPersonById(personId);
		}
	}

	// Defines that the next path parameter after the base url is
	// treated as a parameter and passed to the PersonResources
	// Allows to type http://localhost:599/base_url/1
	// 1 will be treaded as parameter todo and passed to PersonResource
	@Path("{personId}")
	public PersonResource getPerson(@PathParam("personId") int id) {
		return new PersonResource(uriInfo, request, id);
	}
}
