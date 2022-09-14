package com.github.katushka.devopswithkubernetescourse.todobackend.resources;

import com.github.katushka.devopswithkubernetescourse.todobackend.database.ToDos;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

@Path ("/todos")
public class ToDoResource {
        
        private final Logger logger = LogManager.getLogger(getClass());
        
        @Inject
        private ToDos toDoList;
        
        @PUT
        @Path ("{id}")
        public Response updateToDo (@PathParam ("id") int id, @QueryParam ("isDone") boolean isDone) {
                try {
                        toDoList.updateToDo(id, isDone);
                        logger.atDebug().log("A todo {} was updated with isDone {}", id, isDone);
                        return Response.ok().build();
                } catch (SQLException e) {
                        logger.atError().withThrowable(e).log("Failed to update ToDo due to an exception: {}", e.getMessage());
                        return Response.serverError().entity(e).build();
                }
        }
        
        @GET
        public Response getToDos () {
                try {
                        return Response.ok().entity(toDoList.getToDos()).build();
                } catch (Exception e) {
                        logger.atError().withThrowable(e).log("Failed to get ToDo list due to an exception: {}", e.getMessage());
                        return Response.serverError().entity(e).build();
                }
        }
        
        @POST
        @Consumes (MediaType.APPLICATION_JSON)
        public Response createToDo (String toDoText) {
                try {
                        int id = toDoList.createToDo(toDoText);
                        logger.atDebug().log("A new todo {} was created\n\t{}", id, toDoText);
                        return Response.ok().entity(id).build();
                } catch (Exception e) {
                        logger.atError().withThrowable(e).log("Failed to create ToDo due to an exception: {}", e.getMessage());
                        return Response.serverError().entity(e).build();
                }
        }
}
