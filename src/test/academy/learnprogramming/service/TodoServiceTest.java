package academy.learnprogramming.service;

import academy.learnprogramming.config.TodoConfig;
import academy.learnprogramming.entity.Todo;
import academy.learnprogramming.entity.User;
import academy.learnprogramming.security.SecurityUtil;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

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


@RunWith(Arquillian.class)
public class TodoServiceTest {

    @Inject
    User user;

    @Inject
    Todo todo;
  
    @Inject
    UserService userService;

    @Inject
    TodoService todoService;

    @Inject
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
        logger = Logger.getLogger(TodoService.class.getName());

        user.setEmail("bla@bla.com");
        user.setFullName("Lucas Cardoso");
        user.setPassword("password@21");

        userService.saveUser(user);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void createTodo() {
        todo.setTask("fazer compras");
        todo.setDueDate(LocalDate.now().plusDays(5));

        todo = todoService.createTodo(todo, user.getEmail());

        assertEquals(todo.getTodoOwner(), user);
        logger.log(Level.INFO, todo.getTodoOwner().getEmail());
    }
}
