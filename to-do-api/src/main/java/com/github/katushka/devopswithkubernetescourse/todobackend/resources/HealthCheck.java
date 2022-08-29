package com.github.katushka.devopswithkubernetescourse.todobackend.resources;

import com.github.katushka.devopswithkubernetescourse.todobackend.database.ConnectionFactory;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/healthz")
public class HealthCheck {

    @Inject
    private ConnectionFactory factory;

    @GET @Path("/ready")
    public Response getReady() {
        if (factory.isConnectionAvailable()) {
            return Response.ok().entity("Database connection is alive").build();
        }

        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Database connection is unavailable").build();
    }

    @GET @Path("/alive")
    public Response getAlive() {
        return Response.ok().entity("Application is alive").build();
    }
}
