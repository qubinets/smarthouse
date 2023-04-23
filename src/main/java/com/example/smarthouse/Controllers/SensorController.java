package com.example.smarthouse.Controllers;

import com.example.smarthouse.Models.Sensor;
import com.example.smarthouse.Service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    @Autowired
    private SensorService sensorService;


    @GetMapping
    public List<Sensor> getAllSensors(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) List<String> sensors) {
        return sensorService.findFilteredSensors(from, to, sensors);
    }

    @PostMapping
    public  ResponseEntity<String> saveSensor(@RequestBody Sensor sensor, @RequestHeader("X-API-Key") String apiKey) {
        if (!apiKey.equals("a1b2c3d4e5f6g7h8i9j0")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }
        sensorService.save(sensor);
        return ResponseEntity.ok("Sensor data saved.");
    }
    @PostMapping("/all")
    public ResponseEntity<String> saveAllSensors(@RequestBody List<Sensor> sensors, @RequestHeader("X-API-Key") String apiKey) {
        if (!apiKey.equals("a1b2c3d4e5f6g7h8i9j0")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }
        for (Sensor sensor : sensors) {
            sensorService.save(sensor);
        }
        return ResponseEntity.ok("All sensor data saved.");
    }
}