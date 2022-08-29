package com.github.katushka.devopswithkubernetescourse.todoproject.services;

import com.github.katushka.devopswithkubernetescourse.todoapi.beans.ToDo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Named
public class ToDoService {

    private final Logger logger = LogManager.getLogger(getClass());

    private final String apiUri;

    public ToDoService() {
        apiUri = Optional.ofNullable(System.getenv("TODO_API_URI"))
                .orElse("http://localhost:8090/api/todos");
        logger.atDebug().log("ToDo API url is {}", apiUri);
    }

    public List<ToDo> getToDoList() {
        return performActionWithTarget(target -> {
            try (Response response = target.request().get()) {
                List<ToDo> todos = response.readEntity(List.class);
                return todos.isEmpty() ? null : todos;
            }
        });
    }

    public void createToDo(String text) {
        performActionWithTarget(target -> {
            try (Response response = target.request().post(Entity.entity(text, MediaType.APPLICATION_JSON))) {
                return response.readEntity(String.class);
            }
        });
    }

    public boolean isAvailable() {
        try {
            return performActionWithTarget(target -> true);
        } catch (Exception ex) {
            logger.atError().withThrowable(ex).log("Failed to reach service {} due to exception: {}",
                    apiUri, ex.getMessage());
            return false;
        }
    }

    private Client getClient() {
        return ClientBuilder.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    private <T> T performActionWithTarget(TargetAction<T> action) {
        Client client = getClient();
        try {
            WebTarget target = client.target(apiUri);
            return action.perform(target);
        } finally {
            client.close();
        }
    }

    private interface TargetAction<T> {
        T perform(WebTarget target);
    }
}
