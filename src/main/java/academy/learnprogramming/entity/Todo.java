/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package academy.learnprogramming.entity;

import java.time.LocalDate;
import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Seeraj
 */

@NamedQuery(name = Todo.FIND_ALL_TODOS_BY_USER, query = "select t from Todo t where t.todoOwner.email = :email") 
@NamedQuery(name = Todo.FIND_TODO_BY_ID, query = "select t from Todo t where t.id = :id and t.todoOwner.email = :email") 
@NamedQuery(name = Todo.FIND_TODOS_BY_TASK, query = "select t from Todo t where t.task like :task and t.todoOwner.email = :email") 

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class Todo extends AbstractEntity{
    
    public static final String FIND_ALL_TODOS_BY_USER = "Todo.findByUser";
    public static final String FIND_TODO_BY_ID = "Todo.findById";
    public static final String FIND_TODOS_BY_TASK = "Todo.findByTask";

    @NotEmpty(message = "Task must be set")
    @Size(min = 10, message = "Task should not be less than 10 characters")
    private String task;


    @NotNull(message = "Due must be set")
    @FutureOrPresent(message = "Dude must be in the present or future")
    @JsonbDateFormat(value = "yyyy-MM-dd")
    private LocalDate dueDate;

    private boolean isCompleted;
    private LocalDate dateCompleted;
    private LocalDate dateCreated;

    @ManyToOne
    private User todoOwner;

    @PrePersist
    private void init() {
        setDateCreated(LocalDate.now());
    } 

    public boolean isIsCompleted() {
        return this.isCompleted;
    }
}
