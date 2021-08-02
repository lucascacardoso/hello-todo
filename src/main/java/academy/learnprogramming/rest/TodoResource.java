/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package academy.learnprogramming.rest;

import academy.learnprogramming.config.Role;
import academy.learnprogramming.config.Secured;
import academy.learnprogramming.entity.Todo;
import academy.learnprogramming.service.TodoService;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author Seeraj
 */

@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Secured({Role.USER})
public class TodoResource {

    @Context
    private SecurityContext securityContext;
    
    @Inject
    TodoService todoService;
    
    @Path("new")
    @POST
    public Response createTodo(@Valid Todo todo){
       todoService.createTodo(todo, securityContext.getUserPrincipal().getName());
       
       return Response.ok(todo).build();
    }
        
    @Path("update")
    @PUT
    public Response updateTodo(@Valid Todo todo){
        todoService.updateTodo(todo);
        
        return Response.ok(todo).build();
    }    
    
    @Path("{id}")
    @GET
    public Todo getTodo(@PathParam("id") Long id){
        return todoService.findToDoById(id, securityContext.getUserPrincipal().getName());
    }    
    
    @Path("list")
    @GET
    public List<Todo> getTodos(){
        return todoService.getTodos(securityContext.getUserPrincipal().getName());
    }

    @Path("status")
    @POST
    public Response markAsComplete(@QueryParam("id") Long id){
        Todo todo = todoService.findToDoById(id, securityContext.getUserPrincipal().getName());
        todo.setCompleted(true);
        todoService.updateTodo(todo);

        return Response.ok(todo).build();
    }

    @Path("task")
    @GET
    public Response findToDoByTask(@QueryParam("task") String task){
        List<Todo> todos = todoService.findTodosByTask(task, securityContext.getUserPrincipal().getName());
        
        return Response.ok(todos).build();
    }
    
}
