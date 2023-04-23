package com.example.smarthouse.Models;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Condition> conditions;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<Action> actions;

    public Scenario(){

    }

    public Scenario(String name, Set<Condition> conditions, Set<Action> actions) {
        this.name = name;
        this.conditions = conditions;
        this.actions = actions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(Set<Condition> conditions) {
        this.conditions = conditions;
    }

    public Set<Action> getActions() {
        return actions;
    }

    public void setActions(Set<Action> actions) {
        this.actions = actions;
    }
}