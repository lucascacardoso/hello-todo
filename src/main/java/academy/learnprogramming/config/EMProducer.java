package academy.learnprogramming.config;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EMProducer {

  @Produces
  @PersistenceContext
  EntityManager entityManager;
  
}
