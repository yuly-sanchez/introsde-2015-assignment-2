package introsde.lifecoach.model;

import introsde.lifecoach.adapter.DateAdapter;
import introsde.lifecoach.converter.DatePersistenceConverter;
import introsde.lifecoach.dao.LifeCoachDao;
import introsde.lifecoach.model.Person;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
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
	@NamedQuery(name="HealthMeasureHistory.findAll", query="SELECT h FROM HealthMeasureHistory h"),
	
	@NamedQuery(name="HealthMeasureHistory.findMeasureHistoryByMeasureType", 
				query="SELECT h FROM HealthMeasureHistory h, h.measureDefinition m "
						+ "WHERE h.measureDefinition.idMeasureDef=m.idMeasureDef AND h.person.idPerson=:idPerson AND h.measureDefinition.measureName=:measureType"),
	
	@NamedQuery(name="HealthMeasureHistory.findMeasureHistoryByMid", 
				query="SELECT h FROM HealthMeasureHistory h, h.measureDefinition m "
						+ "WHERE h.measureDefinition.idMeasureDef=m.idMeasureDef AND h.person.idPerson=:idPerson AND h.measureDefinition.measureName=:measureType AND h.idMeasureHistory=:mid"),
			//select * from HealthMeasureHistory hm where hm.idPerson="1" and hm.idMeasureDef="1" and strftime('%Y-%m-%d %H:%M:%S', hm.timestamp) between '2013-09-22 22:00:00' and '2015-11-11 10:00:00';
			//select * from HealthMeasureHistory hm where hm.idPerson="1" and hm.idMeasureDef="1" and hm.timestamp between '2013-08-22' and '2015-12-23';			
			//query="SELECT hm FROM HealthMeasureHistory hm, hm.measure m " + 
			//  "WHERE  hm.measure.idMeasure=m.idMeasure AND hm.person.idPerson=:idPerson AND hm.measure.name=:nameMeasure AND hm.timestamp>=:beforeDate AND hm.timestamp<=:afterDate"
	@NamedQuery(name="HealthMeasureHistory.findMeasureHistoryInTheDateOfRange", 
				query="SELECT h FROM HealthMeasureHistory h, h.measureDefinition m"
						+ "WHERE h.measureDefinition.idMeasureDef=m.idMeasureDef AND h.person.idPerson=:idPerson AND h.measureDefinition.measureName=:measureType AND strftime('%Y-%m-%d %H:%M:%S', h.timestamp) BETWEEN beforeDate AND afterDate")		
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
	@XmlJavaTypeAdapter(DateAdapter.class)
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
		return "MeasureHistory( id:" + this.idMeasureHistory +
								"\tvalue: " + this.value + 
								"\tcreated: " + this.timestamp + ")";
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
	
	public static List<HealthMeasureHistory> getMeasureHistoryByMeasureType(int idPerson, String measureType){
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		List<HealthMeasureHistory> measureHistories = em.createNamedQuery("HealthMeasureHistory.findMeasureHistoryByMeasureType", HealthMeasureHistory.class)
				.setParameter("idPerson", idPerson)
				.setParameter("measureType", measureType).getResultList();
		for(HealthMeasureHistory hm : measureHistories){
			System.out.println(hm.toString());
		}
		LifeCoachDao.instance.closeConnections(em);
		return measureHistories;
	}
	
	public static HealthMeasureHistory getMeasureHistoryByMid(int idPerson, String measureType, int mid){
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		HealthMeasureHistory measureHistory = em.createNamedQuery("HealthMeasureHistory.findMeasureHistoryByMid", HealthMeasureHistory.class)
				.setParameter("idPerson", idPerson)
				.setParameter("measureType", measureType)
				.setParameter("mid", mid).getSingleResult();
		LifeCoachDao.instance.closeConnections(em);
		return measureHistory;
	}
}
