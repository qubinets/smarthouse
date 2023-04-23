package com.example.smarthouse.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TaskState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskName;
    private State taskState; //ON , OFF



    public enum State {
    OFF, ON
}

    public TaskState() {

    }
    public TaskState(String taskName, State taskState) {
        this.taskName = taskName;
        this.taskState = taskState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public State getTaskState() {
        return taskState;
    }

    public void setTaskState(State taskState) {
        this.taskState = taskState;
    }
}
