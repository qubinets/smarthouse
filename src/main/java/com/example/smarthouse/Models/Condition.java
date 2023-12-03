package com.example.smarthouse.Models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sensorName;
    private Float value;
    @Column(name = "expected_state")
    private String expectedState;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_id")
    @JsonBackReference
    private TaskState state;

    @Enumerated(EnumType.STRING)
    private ValueComparator valueComparator; // LESS_THAN, GREATER_THAN, EQUALS,NOT

    @Enumerated(EnumType.STRING)
    private StateComparator stateComparator; // LESS_THAN, GREATER_THAN, EQUALS,NOT


    public enum ValueComparator {
        LESS_THAN,
        GREATER_THAN,
        EQUALS,
    }
    public enum StateComparator {
        EQUALS,
        NOT_EQUALS,
    }
    public Condition() {

    }

    public Condition(String sensorName, Float value, Scenario scenario, TaskState state, ValueComparator valueComparator, StateComparator stateComparator, String expectedState) {
        this.sensorName = sensorName;
        this.value = value;
        this.scenario = scenario;
        this.state = state;
        this.valueComparator = valueComparator;
        this.stateComparator = stateComparator;
        this.expectedState = expectedState;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

    public StateComparator getStateComparator() {
        return stateComparator;
    }

    public void setStateComparator(StateComparator stateComparator) {
        this.stateComparator = stateComparator;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public ValueComparator getValueComparator() {
        return valueComparator;
    }

    public void setValueComparator(ValueComparator comparator) {
        this.valueComparator = comparator;
    }
    // Создайте геттеры и сеттеры для expectedState
    public String getExpectedState() {
        return expectedState;
    }

    public void setExpectedState(String expectedState) {
        this.expectedState = expectedState;
    }
}
