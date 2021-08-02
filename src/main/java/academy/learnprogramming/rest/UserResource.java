package academy.learnprogramming.rest;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import academy.learnprogramming.entity.User;
import academy.learnprogramming.security.SecurityUtil;
import academy.learnprogramming.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("users")
public class UserResource {

  @Context
  UriInfo uriInfo;

  @Inject
  SecurityUtil securityUtil;

  @Inject
  UserService userService;

  @POST
  @Path("register")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response register(@Valid User user) {
    userService.saveUser(user);

    return Response.ok(user).build();
  }
  
  @POST
  @Path("login")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response login(@NotNull @FormParam("email") String email,
                        @NotNull @FormParam("password") String password) {
    if(!securityUtil.authenticateUser(email, password)) {
      throw new SecurityException("Email or password not valid");
    }
    
    String token = generateToken(email);
    return Response.ok().header(HttpHeaders.AUTHORIZATION, SecurityUtil.BEARER + " " + token).build();
  }

  private String generateToken(String email) {
    Key securityKey = securityUtil.getSecurityKey();

    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setIssuer(uriInfo.getBaseUri().toString())
        .setAudience(uriInfo.getAbsolutePath().toString())
        .setExpiration(securityUtil.toDate(LocalDateTime.now().plusMinutes(15)))
        .claim("role", "user")
        .signWith(SignatureAlgorithm.HS512, securityKey)
        .compact();
  }
  
}
