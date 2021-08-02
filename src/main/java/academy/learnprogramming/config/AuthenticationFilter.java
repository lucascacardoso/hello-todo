package academy.learnprogramming.config;

import java.io.IOException;
import java.security.Key;
import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import academy.learnprogramming.security.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
    
    @Inject
    private Logger logger;

    @Inject
    private SecurityUtil securityUtil;

    @Override
    public void filter(ContainerRequestContext reqCtx) throws IOException {
        //1. Get the token from the request header
        String authHeader = reqCtx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith(SecurityUtil.BEARER)) {
            logger.log(Level.SEVERE, "Wrong or no authorization header found {0}", authHeader);
            throw new NotAuthorizedException("No authorization header provided");
        }
        String token = authHeader.substring(SecurityUtil.BEARER.length()).trim();

        //2. Parse the token
        try {
            Key key = securityUtil.getSecurityKey();
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);

            SecurityContext originalSecurityContext = reqCtx.getSecurityContext();
            reqCtx.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> claims.getBody().getSubject();
                }
                @Override
                public boolean isUserInRole(String s) {
                    String role = claims.getBody().get("role", String.class);
                    return s.equals(role);
                }
                @Override
                public boolean isSecure() {
                    return originalSecurityContext.isSecure();
                }
                @Override
                public String getAuthenticationScheme() {
                    return SecurityUtil.BEARER;
                }
            });
            logger.info("Token parsed successfully");
            //3. If parsing fails, yell.
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Invalid {0}", token);
            //Another way to send exceptions to the programmatic
            reqCtx.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
