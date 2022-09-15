package com.github.katushka.devopswithkubernetescourse.todoapi.beans;

import java.io.Serializable;

public class ToDo implements Serializable {

    private Integer id;
    private String text;
    
    private boolean isDone;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public boolean isDone () {
        return isDone;
    }
    
    public void setDone (boolean done) {
        isDone = done;
    }
}
