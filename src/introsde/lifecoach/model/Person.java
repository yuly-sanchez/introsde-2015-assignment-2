package introsde.lifecoach.model;

import introsde.lifecoach.dao.LifeCoachDao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity  // indicates that this class is an entity to persist in DB
@Table(name="Person") // to whole table must be persisted 
@NamedQueries({
	@NamedQuery(name="Person.findAll", query="SELECT p FROM Person p"),
	@NamedQuery(name="Person.findValuesOfRange", 
				query="SELECT p FROM Person p INNER JOIN p.lifeStatus l WHERE l.measureDefinition = ?1 AND "
						+ "CAST(l.value NUMERIC(10,2)) BETWEEN ?2 AND ?3")					
})

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Person implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
    @Id 
    @GeneratedValue(generator="sqlite_person")
    @TableGenerator(name="sqlite_person", table="sqlite_sequence",
        pkColumnName="name", valueColumnName="seq",
        pkColumnValue="Person")
    @Column(name="idPerson")
    @XmlElement(name="id")
    private int idPerson;
    
    @Column(name="name")
    @XmlElement(name="firstname")
    @JsonProperty("firstname")
    private String name;
    
    @Column(name="lastname")
    @XmlElement(name="lastname")
    private String lastname;
    
    @Column(name="username")
    private String username;
    
    @Temporal(TemporalType.DATE) // defines the precision of the date attribute
    @Column(name="birthdate")
    @XmlElement(name="birthdate")
    private Date birthdate; 
    
    @Column(name="email")
    private String email;
    
    // mappedBy must be equal to the name of the attribute in LifeStatus that maps this relation
    @OneToMany(mappedBy="person",cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    private List<LifeStatus> lifeStatus;
    
    @XmlElementWrapper(name = "healthProfile")
    @JsonProperty("healthProfile")
    @XmlElement(name="measureType")
    public List<LifeStatus> getLifeStatus() {
        return lifeStatus;
    }

	public void setLifeStatus(List<LifeStatus> lifeStatus) {
		this.lifeStatus = lifeStatus;
	}

	// getters
    public int getIdPerson(){
        return idPerson;
    }
    public String getLastname(){
        return lastname;
    }
    public String getName(){
        return name;
    }
    public String getUsername(){
        return username;
    }
    public Date getBirthdate(){
        return birthdate;
    }
    public String getEmail(){
        return email;
    }
    
    // setters
    public void setIdPerson(int idPerson){
        this.idPerson = idPerson;
    }
    public void setLastname(String lastname){
        this.lastname = lastname;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setBirthdate(Date birthdate){
        this.birthdate = birthdate;
    }
    public void setEmail(String email){
        this.email = email;
    }
    
    @Override
	public String toString(){
		return 	"Person [ id:" + this.idPerson +
						  "\tfirstname: " + this.name + 
						  "\t\tlastname: " + this.lastname + 
						  "\t\tbirthdate: " + this.birthdate + 
						  " ]";
	}
    
    public static Person getPersonById(int personId) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        Person p = em.find(Person.class, personId);
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static List<Person> getAll() {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        List<Person> list = em.createNamedQuery("Person.findAll", Person.class)
            .getResultList();
        LifeCoachDao.instance.closeConnections(em);
        return list;
    }

    public static Person savePerson(Person p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        return p;
    } 

    public static Person updatePerson(Person p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager(); 
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
        return p;
    }

    public static void removePerson(Person p) {
        EntityManager em = LifeCoachDao.instance.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        p=em.merge(p);
        em.remove(p);
        tx.commit();
        LifeCoachDao.instance.closeConnections(em);
    }
    
    public static List<Person> getFilteredPersonByMeasure(String measureType, String min, String max){
    	EntityManager em = LifeCoachDao.instance.createEntityManager();
    	List<Person> personList = em.createNamedQuery("Person.findValuesOfRange", Person.class)
    				.setParameter("measureType", measureType)
    				.setParameter("min", min)
    				.setParameter("max", max).getResultList();
    		for(Person p : personList){
    			System.out.println(p.toString());
    		}
    	LifeCoachDao.instance.closeConnections(em);
    	return personList;
    }
}
