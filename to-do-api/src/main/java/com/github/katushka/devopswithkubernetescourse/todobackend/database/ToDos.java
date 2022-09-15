package com.github.katushka.devopswithkubernetescourse.todobackend.database;

import com.github.katushka.devopswithkubernetescourse.todoapi.beans.ToDo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ToDos {

        @Inject
        private ConnectionFactory factory;
        
        /***
         * Returns a list of all undone ToDos
         * @return a list of ToDos with is_done field equals to false
         * @throws SQLException
         */
        public List<ToDo> getToDos () throws SQLException {
                return factory.connect(connection -> {
                        final String selectAll = "SELECT * FROM todos WHERE is_done = FALSE";
                        final List<ToDo> toDoList = new ArrayList<>();
                        try (ResultSet resultSet = connection.createStatement().executeQuery(selectAll)) {
                                while (resultSet.next()) {
                                        ToDo toDo = new ToDo();
                                        toDo.setId(resultSet.getInt("id"));
                                        toDo.setText(resultSet.getString("text"));
                                        toDoList.add(toDo);
                                }
                        }
                        return toDoList;
                });
        }
        
        /***
         * Updates a ToDo with id equals to
         * @param id an id of a ToDo that should be updated
         * @param isDone a value for is_done field to be set
         * @return true if a ToDo was updated and false otherwise
         * @throws SQLException
         */
        public boolean updateToDo (int id, boolean isDone) throws SQLException {
                return factory.connect(connection -> {
                        final String update = "UPDATE todos SET is_done=(?) WHERE id = (?)";
                        try (PreparedStatement statement = connection.prepareStatement(update)) {
                                statement.setBoolean(1, isDone);
                                statement.setInt(2, id);
                                return statement.executeUpdate() > 0;
                        }
                });
        }
        
        /***
         * Creates a new ToDo object
         * @param text text value to be set for a new ToDo object
         * @return an id of created ToDo object
         * @throws SQLException
         */
        public int createToDo (String text) throws SQLException {
                if (text == null || text.isBlank()) {
                        throw new IllegalArgumentException("Can not create empty TODO!");
                }
                if (text.length() > 140) {
                        throw new IllegalArgumentException(
                                MessageFormat.format("Can not create TODO with length of {0} ( > 140)!", text.length()));
                }
                return factory.connect(connection -> {
                        final String createSql = "INSERT INTO todos (text) VALUES (?) RETURNING id";

                        try (PreparedStatement statement = connection.prepareStatement(createSql)) {
                                statement.setString(1, text);
                                if (statement.execute()) {
                                        ResultSet result = statement.getResultSet();
                                        if (result.next()) {
                                                return result.getInt(1);
                                        }
                                }
                                throw new SQLException("No new ToDo was created!");
                        }
                });
        }
}
