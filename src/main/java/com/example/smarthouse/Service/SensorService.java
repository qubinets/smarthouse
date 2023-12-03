package com.example.smarthouse.Service;

import com.example.smarthouse.Models.Sensor;
import com.example.smarthouse.Repo.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    public List<Sensor> findAll() {
        return sensorRepository.findAll();
    }
    public List<String> getAllSensorNames() {
        return sensorRepository.findDistinctSensorNames();
    }
    public Sensor save(Sensor sensor) {
        return sensorRepository.save(sensor);
    }
    public List<Sensor> findFilteredSensors(String from, String to, List<String> sensors) {
        LocalDateTime fromDateTime = from != null ? LocalDateTime.parse(from) : LocalDateTime.MIN;
        LocalDateTime toDateTime = to != null ? LocalDateTime.parse(to) : LocalDateTime.MAX;

        if (sensors == null || sensors.isEmpty()) {
            return sensorRepository.findByTimestampBetween(fromDateTime, toDateTime);
        } else {
            return sensorRepository.findByTimestampBetweenAndNameIn(fromDateTime, toDateTime, sensors);
        }
    }

    public Sensor findLatestBySensorName(String sensorName) {
        return sensorRepository.findTopByNameOrderByTimestampDesc(sensorName);
    }
}