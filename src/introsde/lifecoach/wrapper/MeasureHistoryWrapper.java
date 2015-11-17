package introsde.lifecoach.wrapper;

import introsde.lifecoach.model.HealthMeasureHistory;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="measureHistory")
public class HealthMeasureHistoryListWrapper implements Serializable{

	private static final long serialVersionUID = 1L;
	
	//@XmlElement(name="measure")
	public List<HealthMeasureHistory> listMeasure = null;
	
	public List<HealthMeasureHistory> getListMeasure() {
		return listMeasure;
	}
	
	public void setListMeasure(List<HealthMeasureHistory> listMeasure) {
		this.listMeasure = listMeasure;
	}	
}
