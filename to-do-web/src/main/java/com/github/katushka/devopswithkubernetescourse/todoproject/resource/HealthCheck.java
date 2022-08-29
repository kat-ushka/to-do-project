package com.github.katushka.devopswithkubernetescourse.todoproject.resource;

import com.github.katushka.devopswithkubernetescourse.todoproject.services.ToDoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/healthz")
public class HealthCheck {

    @Inject
    private ToDoService toDoService;

    @GET
    public Response perform() {
        if (toDoService.isAvailable()) {
            return Response.ok().entity("Api is accessible").build();
        }
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Api is inaccessible").build();
    }
}
