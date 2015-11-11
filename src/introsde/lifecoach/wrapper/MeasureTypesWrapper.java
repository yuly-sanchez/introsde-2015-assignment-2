package introsde.lifecoach.wrapper;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="measureTypes")
public class MeasureTypesWrapper implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name="measureType")
	private List<String> listMeasures = null;

	public List<String> getListMeasures() {
		return listMeasures;
	}

	public void setListMeasures(List<String> listMeasures) {
		this.listMeasures=listMeasures;
	}

}
