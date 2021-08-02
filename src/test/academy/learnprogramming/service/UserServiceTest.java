package academy.learnprogramming.service;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import academy.learnprogramming.config.TodoConfig;
import academy.learnprogramming.entity.Todo;
import academy.learnprogramming.entity.User;
import academy.learnprogramming.security.SecurityUtil;

@RunWith(Arquillian.class)
public class UserServiceTest {

  @Inject
  User user;

  @Inject
  UserService userService;
  Logger logger;

  @Deployment
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "hello-todo.war")
      .addPackage(Todo.class.getPackage())
      .addPackage(UserService.class.getPackage())
      .addPackage(SecurityUtil.class.getPackage())
      .addPackage(TodoConfig.class.getPackage())
      .addAsResource("persistence.xml", "META-INF/persistence.xml")
      .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
  }

  @Before
  public void setUp() throws Exception {
    logger = Logger.getLogger(UserService.class.getName());

    user.setEmail("bla@bla.com");
    user.setFullName("Lucas Cardoso");
    user.setPassword("password@21");

    userService.saveUser(user);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void saveUser() {

    assertNotNull(user.getId());
    logger.log(Level.INFO, user.getId().toString());

    assertNotEquals("password@21", user.getPassword());
    logger.log(Level.INFO, user.getPassword());

    User user2 = new User();
    user2.setEmail("bla@bla.com");
    user2.setFullName("fulano da silva");
    user2.setPassword("password@22");

    EJBException thrown = assertThrows(EJBException.class, () -> {
      userService.saveUser(user2);
    });
    logger.log(Level.INFO, thrown.getMessage());
  }
  
}
