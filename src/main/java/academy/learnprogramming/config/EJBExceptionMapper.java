package academy.learnprogramming.config;

import javax.ejb.EJBException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException>{

  @Override
  public Response toResponse(EJBException exception) {   
    return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
  }
  
}
