package introsde.lifecoach.model;

import introsde.lifecoach.adapter.DateAdapter;
import introsde.lifecoach.dao.LifeCoachDao;
import introsde.lifecoach.model.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The persistent class for the "HealthMeasureHistory" database table.
 * 
 */

@Entity
@Table(name="HealthMeasureHistory" )
@NamedQueries({
	
	@NamedQuery(name="HealthMeasureHistory.findAll", query="SELECT h FROM HealthMeasureHistory h "),
	
	@NamedQuery(name="HealthMeasureHistory.findByMeasureType", 
				query="SELECT h FROM HealthMeasureHistory h "
						+ "WHERE h.person= ?1 AND h.measureDefinition= ?2"),
	
	@NamedQuery(name="HealthMeasureHistory.findByMid", 
				query="SELECT h FROM HealthMeasureHistory h "
						+ "WHERE h.person= ?1 AND h.measureDefinition= ?2 AND h.idMeasureHistory= ?3"),
	
	@NamedQuery(name="HealthMeasureHistory.findByDateOfRange", 
				query="SELECT h FROM HealthMeasureHistory h "
						+ "WHERE h.person= ?1 AND h.measureDefinition= ?2 AND h.timestamp BETWEEN ?3 AND ?4")					
	})
 
@XmlType(propOrder={"idMeasureHistory", "value", "timestamp"})
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="measure")
public class HealthMeasureHistory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="sqlite_mhistory")
	@TableGenerator(name="sqlite_mhistory", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="HealthMeasureHistory")
	@Column(name="idMeasureHistory")
	@XmlElement(name="mid")
	@JsonProperty("mid")
	private int idMeasureHistory;

	@Temporal(TemporalType.DATE)
	@Column(name="timestamp")
	//@XmlJavaTypeAdapter(DateAdapter.class)
	@XmlElement(name="created")
	@JsonProperty("created")
	private Date timestamp;

	@Column(name="value")
	@XmlElement(name="value")
	@JsonProperty("value")
	private String value;

	@ManyToOne
	@JoinColumn(name = "idMeasureDef", referencedColumnName = "idMeasureDef")
	private MeasureDefinition measureDefinition;

	// notice that we haven't included a reference to the history in Person
	// this means that we don't have to make this attribute XmlTransient
	@ManyToOne
	@JoinColumn(name = "idPerson", referencedColumnName = "idPerson")
	private Person person;

	public HealthMeasureHistory() {
	}

	public int getIdMeasureHistory() {
		return this.idMeasureHistory;
	}

	public void setIdMeasureHistory(int idMeasureHistory) {
		this.idMeasureHistory = idMeasureHistory;
	}

	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public MeasureDefinition getMeasureDefinition() {
	    return measureDefinition;
	}

	public void setMeasureDefinition(MeasureDefinition param) {
	    this.measureDefinition = param;
	}

	public Person getPerson() {
	    return person;
	}

	public void setPerson(Person param) {
	    this.person = param;
	}

	@Override
	public String toString(){
		return "MeasureHistory [ id: " + this.idMeasureHistory +
								 "\tvalue: " + this.value + 
								 "\tcreated: " + this.timestamp + 
								 " ]";
	}
	
	// database operations
	public static HealthMeasureHistory getHealthMeasureHistoryById(int id) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		HealthMeasureHistory p = em.find(HealthMeasureHistory.class, id);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}
	
	public static List<HealthMeasureHistory> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<HealthMeasureHistory> list = em.createNamedQuery("HealthMeasureHistory.findAll", HealthMeasureHistory.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static HealthMeasureHistory saveHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static HealthMeasureHistory updateHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p=em.merge(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static void removeHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	}
	
	public static List<HealthMeasureHistory> getMeasureHistoryByMeasureType(Person person, MeasureDefinition measureDef){
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<HealthMeasureHistory> measureHistories = em.createNamedQuery("HealthMeasureHistory.findByMeasureType", HealthMeasureHistory.class)
				.setParameter(1, person)
				.setParameter(2, measureDef).getResultList();
		LifeCoachDao.instance.closeConnections(em);
		return measureHistories;
	}
	
	public static HealthMeasureHistory getMeasureHistoryByMid(Person person, MeasureDefinition measureDef, int mid){
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		HealthMeasureHistory measureHistory = em.createNamedQuery("HealthMeasureHistory.findByMid", HealthMeasureHistory.class)
				.setParameter(1, person)
				.setParameter(2, measureDef)
				.setParameter(3, mid).getSingleResult();
		LifeCoachDao.instance.closeConnections(em);
		return measureHistory;
	}
	
	public static List<HealthMeasureHistory> getFilterByDatesHistory(Person person, MeasureDefinition measureDef, Calendar beforeDate, Calendar afterDate){
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<HealthMeasureHistory> filteredHistory = null;
		try{
			filteredHistory = em.createNamedQuery("HealthMeasureHistory.findByDateOfRange", HealthMeasureHistory.class)
						.setParameter(1, person)
						.setParameter(2, measureDef)
						.setParameter(3, beforeDate.getTime(), TemporalType.DATE)
						.setParameter(4, afterDate.getTime(), TemporalType.DATE).getResultList();	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		LifeCoachDao.instance.closeConnections(em);
		return filteredHistory;
	}
}
























































