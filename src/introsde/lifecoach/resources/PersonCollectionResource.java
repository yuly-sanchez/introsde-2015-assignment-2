package introsde.lifecoach.resources;
import introsde.lifecoach.model.*;

import java.io.IOException;
import java.util.ArrayList;
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
	 * @return list all person into DB
	 * 
	 * Request #12: GET/person?measureType={measureType}&max={max}&min={min}
	 * @return list all person into DB
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	 public List<Person> getPeople() {
	 System.out.println("--> REQUESTED: Getting list of people...");
	 System.out.println(); 
	 List<Person> people = Person.getAll();
	 return people;
	 
	}
	/*@GET
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	 public List<Person> getPeople(@QueryParam("measureType") String measureType,
			 					   @QueryParam("max") Double max,
			 					   @QueryParam("min") Double min) {
		
	 List<Person> people = null; 
	 MeasureDefinition md = MeasureDefinition.getMeasureDefinition(measureType); 
	 
	 if(max==null && min==null){ //--->DA CONTROLLARE non entra
		 System.out.println("--> REQUESTED: Getting list of people...");
		 System.out.println(); 
		 people = Person.getAll();
		
	 }else if(max != null){
		 min = 0.;
		 System.out.println("--> REQUEST: Retrieves people whose " + measureType +" value is in the ["+min+","+max+"] range");
		 System.out.println();
		 people = Person.getFilteredPersonByValuesOfRange(md, min, max);
		 
	 }else if(min != null){
		 max = 500.;
		 System.out.println("--> REQUEST: Retrieves people whose " +measureType +" value is in the ["+min+","+max+"] range");
		 System.out.println();
		 people = Person.getFilteredPersonByValuesOfRange(md, min, max);
		 
	 }else{
		 System.out.println("--> REQUEST: Retrieves people whose " +measureType +" value is in the ["+min+","+max+"] range");
		 System.out.println();
		 people = Person.getFilteredPersonByValuesOfRange(md, min, max);
		 
	 }
	 return people;
}*/	 

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

		if (person.getLifeStatus().isEmpty()) {
			System.out.println("--> Creating new person without LifeStatus...");
			return Person.savePerson(person);

		} else {
			System.out.println("--> Creating new person with LifeStatus...");

			//salvo in una lista tutto il lifestatus da creare 
			List<LifeStatus> personLifeStatus = person.getLifeStatus();
			System.out.println("--> LifeStatus-ToCreate: " + personLifeStatus.size());

			//elimino il contenuto di lifeStatus
			person.setLifeStatus(null);
			System.out.println("--> LifeStatus-Clear: " + person.getLifeStatus().size());
			
			//salvo la persona in modo da ricavarmi l'idPerson
			Person p = Person.savePerson(person);
			int personId = p.getIdPerson();
			person.setIdPerson(personId);
			System.out.println("PersonID: " + person.getIdPerson());

			//creo una lista per controllare i valori passati dalla nuova persona
			List<LifeStatus> check = new ArrayList<LifeStatus>();
			//check.addAll(personLifeStatus);
			
			// controlliamo il contenuto del lifeStatus appena salvato
			for (int i=0;i<person.getLifeStatus().size();i++) {
				LifeStatus lifeS = person.getLifeStatus().get(i);
				System.out.println(lifeS.toString());
				
				// setto la measureDefinition
				int idMeasureDef=0;
				switch (lifeS.getMeasureDefinition().getMeasureName()) {
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

				MeasureDefinition md = new MeasureDefinition();
				md = MeasureDefinition.getMeasureDefinitionById(idMeasureDef);
				lifeS.setMeasureDefinition(md);
				System.out.println("--> lifeSNewPerson-Measure: " + lifeS.getMeasureDefinition());

				// setto il value
				//lifeS.setValue(check.get(i).getValue());
				System.out.println("--> lifeSNewPerson-Value: " + lifeS.getValue());
				
				// setto la persona
				//lifeS.setPerson(person);
				System.out.println("--> lifeSNewPerson-Person: " + lifeS.getPerson());
				
				LifeStatus.saveLifeStatus(lifeS);
				check.add(lifeS);
			}
			
			person.setLifeStatus(check);
			return person;
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
