package com.example.smarthouse.Models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Command command;//door,sound,pump,light
    private LocalDateTime created;
    private Status status;// PENDING,COMPLETED


    public Task() {
    }

    public Task(Command command, LocalDateTime created, Status status) {
        this.command = command;
        this.created = created;
        this.status = status;
    }

    // Геттеры и сеттеры для всех полей (id, command, created, status)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}