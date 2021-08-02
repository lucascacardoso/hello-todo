package academy.learnprogramming.rest;

import static org.junit.Assert.assertThrows;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.json.JsonArray;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import academy.learnprogramming.config.TodoConfig;
import academy.learnprogramming.entity.Todo;
import academy.learnprogramming.security.SecurityUtil;
import academy.learnprogramming.service.UserService;

@RunWith(Arquillian.class)
public class TodoResourceTest {

  @Inject
  Logger logger;

  // CDI Injection problem with this dependencies.  
  // @Inject
  // private Client client;
  // private WebTarget webTarget;

  @ArquillianResource
  private URL base;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "hello-todo.war")
      .addPackage(TodoConfig.class.getPackage())
      .addPackage(Todo.class.getPackage())
      .addPackage(TodoResource.class.getPackage())
      .addPackage(SecurityUtil.class.getPackage())
      .addPackage(UserService.class.getPackage())
      .addAsResource("persistence.xml", "META-INF/persistence.xml")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
  }

  @Before
  public void setUp(){    
  }

  @After
  public void tearDown() throws Exception {  
  }

  @Test
  public void createTodo() throws MalformedURLException {
    
    Client client = ClientBuilder
      .newBuilder()
      .connectTimeout(7, TimeUnit.SECONDS)
      .readTimeout(3, TimeUnit.SECONDS).build(); 
    
    WebTarget webTarget = client.target(URI.create(new URL(base, "api/v1/todo/new").toExternalForm()));

    NotAuthorizedException thrown = assertThrows(NotAuthorizedException.class, () -> { 
      webTarget.request(MediaType.APPLICATION_JSON).get(JsonArray.class); 
    }); 
    logger.log(Level.INFO, thrown.getMessage());

    client.close();
  }
  
}
