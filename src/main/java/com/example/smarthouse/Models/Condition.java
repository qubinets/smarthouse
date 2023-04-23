package com.example.smarthouse.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sensorName;
    private Float value;
    private Comparator comparator; // LESS_THAN, GREATER_THAN, EQUALS,NOT

    public enum Comparator {
        AND ,
        OR,
        NOT,
        LESS_THAN,
        GREATER_THAN,
        EQUALS,


    }
    public Condition() {

    }

    public Condition(String sensorName, Float value, Comparator comparator) {
        this.sensorName = sensorName;
        this.value = value;
        this.comparator = comparator;
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

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }
}
