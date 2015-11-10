package introsde.lifecoach.resources;

import introsde.lifecoach.model.*;
import introsde.lifecoach.wrapper.HealthMeasureHistoryListWrapper;

import java.io.IOException;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
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
    @PersistenceUnit(unitName="introsde-jpa")
    EntityManager entityManager;

    // will work only inside a Java EE application
    @PersistenceContext(unitName = "introsde-jpa",type=PersistenceContextType.TRANSACTION)
    private EntityManagerFactory entityManagerFactory;

    /**
     * Request #1: GET /person  
     * @return list all person into DB 
     */
    @GET
    @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
    public List<Person> getPersonsBrowser() {
        System.out.println("Getting list of people...");
        List<Person> people = Person.getAll();
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
     * Request #4
     * @param person
     * @return	the newly created person with its assigned id and if a healthprofile is included, create also those measurements for the new person
     * @throws IOException
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML})
    public Person newPerson(Person person) throws IOException {
        System.out.println("Creating new person...");
        if(person.getLifeStatus() == null){
        	return Person.savePerson(person);
        }else{
        	//DA COMPLETARE
        	return Person.savePerson(person);
        }
    }
    

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{idPerson}/{measureType}")
    public List<HealthMeasureHistory> getMeasureHistory(@PathParam("idPerson") int idPerson, @PathParam("measureType") String measureType){
    	List<HealthMeasureHistory> hm = HealthMeasureHistory.getHealthMeasureHistory(idPerson, measureType);
    	for(HealthMeasureHistory h : hm){
    		System.out.println(h.toString());
    	}
    	return hm;
    }
    /*public HealthMeasureHistoryListWrapper getMeasureHistory(@PathParam("idPerson") int idPerson, @PathParam("measureType") String measureType){
    	HealthMeasureHistoryListWrapper hp = new HealthMeasureHistoryListWrapper();
    	System.out.println("Getting MeasureHistory to " + idPerson + " measureType " + measureType);
    	hp.setListMeasure(HealthMeasureHistory.getHealthMeasureHistory(idPerson, measureType));
    	for(HealthMeasureHistory h : hp.getListMeasure()){
    		System.out.println(h.toString());
    	}
    	return hp;
    }*/
    
    // Defines that the next path parameter after the base url is
    // treated as a parameter and passed to the PersonResources
    // Allows to type http://localhost:599/base_url/1
    // 1 will be treaded as parameter todo and passed to PersonResource
    /**
     * Request #2: GET /person/{id}
     * @param id
     * @return  all the personal information plus current measures of person identified by {id}
     */
    @Path("{personId}")
    public PersonResource getPerson(@PathParam("personId") int id) {
        return new PersonResource(uriInfo, request, id);
    }
    
}
