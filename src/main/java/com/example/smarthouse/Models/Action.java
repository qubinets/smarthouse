package com.example.smarthouse.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Command command;

    private TaskState.State state;
    private LocalDateTime executionTime;

    public Action(){

    }
    public Action(Command command, LocalDateTime executionTime) {
        this.command = command;
        this.executionTime = executionTime;
    }

    public Action(Command command, TaskState.State state, LocalDateTime executionTime) {
        this.command = command;
        this.state = state;
        this.executionTime = executionTime;
    }

    public TaskState.State getState() {
        return state;
    }

    public void setState(TaskState.State state) {
        this.state = state;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public Long getId() {
        return id;
    }

    public Command getCommand() {
        return command;
    }

    public LocalDateTime getExecutionTime() {
        return executionTime;
    }
}