package introsde.lifecoach.resources;

import java.util.Arrays;

import introsde.lifecoach.model.MeasureDefinition;
import introsde.lifecoach.wrapper.MeasureTypesWrapper;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless
@LocalBean
@Path("/measureTypes")
public class MeasureCollectionResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	/**
	 * Request #9: GET /measureTypes
	 * @return the list of measures type
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public MeasureTypesWrapper getMeasureTypes(){
		System.out.println("--> REQUEST: Getting list the measure");
		System.out.println();
		MeasureTypesWrapper mw = new MeasureTypesWrapper();
		mw.setListMeasures(MeasureDefinition.getMeasureTypes());
		System.out.println("MeasureTypes : " + Arrays.toString(mw.getListMeasures().toArray()));
		return mw;
	}
	
}
