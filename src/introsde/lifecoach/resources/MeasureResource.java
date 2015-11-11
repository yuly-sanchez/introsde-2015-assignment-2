package introsde.lifecoach.resources;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Stateless
@LocalBean
public class MeasureResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	int  idMeasure;
	EntityManager entityManger;
	
	public MeasureResource(UriInfo uriInfo, Request request, int idMeasure, EntityManager entityManager){
		this.uriInfo = uriInfo;
		this.request = request;
		this.idMeasure = idMeasure;
		this.entityManger = entityManager;
	}
	
	public MeasureResource(UriInfo uriInfo, Request request, int idMeasure){
		this.uriInfo = uriInfo;
		this.request = request;
		this.idMeasure = idMeasure;
	}

}
