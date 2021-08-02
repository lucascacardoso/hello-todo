package academy.learnprogramming.config;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private Logger logger;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Role> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Role> methodRoles = extractRoles(resourceMethod);       

        // Check if the user is allowed to execute the method
        // The method annotations override the class annotations
        if (methodRoles.isEmpty()) {
            checkPermissions(classRoles, requestContext);
        } else {
            checkPermissions(methodRoles, requestContext);
        }        
    }

    // Extract the roles from the annotated element
    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<Role>();
        } else {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<Role>();
            } else {
                Role[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private void checkPermissions(List<Role> allowedRoles, ContainerRequestContext requestContext){
        if (allowedRoles.size() > 0 && requestContext.getSecurityContext().getUserPrincipal() == null) {
            refuseRequest();
        }

        for (Role role : allowedRoles) {
            if (requestContext.getSecurityContext().isUserInRole(role.toString().toLowerCase())) {
                logger.log(Level.INFO, "User is authorized!");
                return;
            }
        }

        refuseRequest();
    }

    private void refuseRequest() {
        logger.log(Level.SEVERE, "User not authorized!");
        throw new NotAuthorizedException("You're not authorized to request this resource");
    }
}
