package com.example.smarthouse;

import com.example.smarthouse.Models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    List<Sensor> findByTimestampBetween(LocalDateTime from, LocalDateTime to);

    List<Sensor> findByTimestampBetweenAndNameIn(LocalDateTime from, LocalDateTime to, List<String> sensors);
}