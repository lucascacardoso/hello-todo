/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package academy.learnprogramming.service;

import academy.learnprogramming.entity.Todo;
import academy.learnprogramming.entity.User;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author Seeraj
 */


@Stateless
public class TodoService {
    
    @Inject
    EntityManager entityManager;

    @Inject
    UserService userService;
    
    public Todo createTodo(Todo todo, String ownerEmail){

        User owner = userService.findUserByEmail(ownerEmail);

        if(owner != null) {
            todo.setTodoOwner(owner);
            entityManager.persist(todo);
        }
        return todo;        
    }    
    
    public Todo updateTodo(Todo todo){
        entityManager.merge(todo);
        return todo;
    }    
    
    public Todo findToDoById(Long id, String email){
        List<Todo> resultList = entityManager.createNamedQuery(Todo.FIND_TODO_BY_ID, Todo.class)
            .setParameter("id", id)
            .setParameter("email", email)
            .getResultList();
        
        if(resultList.isEmpty()) {
            return null;
        }

        return resultList.get(0);
    }    
    
    public List<Todo> getTodos(String email){
        return entityManager.createNamedQuery(Todo.FIND_ALL_TODOS_BY_USER, Todo.class)
            .setParameter("email", email)
            .getResultList();
    }

    public List<Todo> findTodosByTask(String task, String email) {
        return entityManager.createNamedQuery(Todo.FIND_TODOS_BY_TASK, Todo.class)
            .setParameter("task", "%" + task + "%")
            .setParameter("email", email)
            .getResultList();
    }
}
