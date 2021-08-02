package academy.learnprogramming.service;

import java.util.List;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import academy.learnprogramming.entity.User;
import academy.learnprogramming.security.SecurityUtil;

@Stateless
public class UserService {
  
  @Inject
  EntityManager entityManager;

  @Inject
  SecurityUtil securityUtil;

  public User saveUser(User user){

    if(emailExists(user.getEmail()) || user.getId() != null) {
      throw new EJBException("Email/User already exists");
    }

    Map<String, String> credMap = securityUtil.getHashedPassword(user.getPassword());

    user.setPassword(credMap.get(SecurityUtil.HASHED_PASSWORD_KEY));
    user.setSalt(credMap.get(SecurityUtil.SALT_KEY));

    entityManager.persist(user);
    credMap.clear();
    
    return user;
  }

  public User findUserByEmail(String email) {
    List<User> resultList = entityManager.createNamedQuery(User.FIND_USER_BY_EMAIL, User.class)
      .setParameter("email", email)
      .getResultList();

    if(resultList.isEmpty()) {
      return null;
    }
    
    return resultList.get(0);
  }

  public User findUserByPassword(String password) {
    List<User> resultList = entityManager.createNamedQuery(User.FIND_USER_BY_PASSWORD, User.class)
      .setParameter("password", password)
      .getResultList();

    if(resultList.isEmpty()) {
      return null;
    }

    return resultList.get(0);
  }

  public List<User> getUsers() {
    return entityManager.createNamedQuery(User.FIND_ALL_USERS, User.class)
      .getResultList();
  }

  public boolean emailExists(String email) {
    return (Long) entityManager.createNativeQuery("select count(id) from TodoUser where exists (select id from TodoUser where email = ?)")
      .setParameter(1, email)
      .getSingleResult() > 0;
  }
}
