package introsde.lifecoach.wrapper;

import introsde.lifecoach.model.Person;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class wrapper used when are listened the person
 * @author yuly
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="people")
public class PeopleWrapper implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="person")
	@JsonProperty("people")
	private List<Person> listPeople = null;

	public List<Person> getListPeople() {
		return listPeople;
	}

	public void setListPeople(List<Person> listPeople) {
		this.listPeople = listPeople;
	}

	
}	