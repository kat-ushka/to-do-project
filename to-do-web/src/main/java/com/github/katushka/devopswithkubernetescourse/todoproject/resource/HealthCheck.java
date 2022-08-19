package com.github.katushka.devopswithkubernetescourse.todoproject.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/")
public class HealthCheck {

    @GET
    public Response perform() {
        return Response.ok().entity("Health check").build();
    }
}
