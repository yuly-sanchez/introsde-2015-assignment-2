package introsde.lifecoach.wrapper;

import introsde.lifecoach.model.HealthMeasureHistory;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Class wrapper used when are listened the MeasureHistory
 * @author yuly
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="measureHistory")
public class MeasureHistoryWrapper implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="measure")
	public List<HealthMeasureHistory> listMeasureHistory = null;

	@JsonValue
	public List<HealthMeasureHistory> getListMeasureHistory() {
		return listMeasureHistory;
	}

	public void setListMeasureHistory(List<HealthMeasureHistory> listMeasureHistory) {
		this.listMeasureHistory = listMeasureHistory;
	}
		
}
