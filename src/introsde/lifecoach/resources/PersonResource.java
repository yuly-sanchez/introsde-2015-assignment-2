package introsde.lifecoach.resources;


import introsde.lifecoach.model.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
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
		
		System.out.println("==========================================================================================================================");
    	System.out.println("\t\t\t\t\tRequest #3 : PUT /person/"+ this.id);
    	System.out.println("==========================================================================================================================");
    	System.out.println();
        System.out.println("Updating Person... " + this.id);
		
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
		System.out.println("==========================================================================================================================");
    	System.out.println("\t\t\t\t\tRequest #4 : DELETE /person/"+ this.id);
    	System.out.println("==========================================================================================================================");
    	System.out.println();
		Person person = this.getPersonById(this.id);
		if (person == null)
			throw new RuntimeException("Delete: Person with " + this.id + " not found");
		System.out.println("Delete: Person with " + this.id + " found");
		Person.removePerson(person);
	}

	public Person getPersonById(int personId) {
		System.out.println("Reading person from DB with id: " + personId);
		Person person = Person.getPersonById(personId);
		System.out.println("Person: " + person.toString());
		return person;
	}
}
