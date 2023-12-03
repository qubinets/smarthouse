package com.example.smarthouse.Controllers;


import com.example.smarthouse.Models.Condition;
import com.example.smarthouse.Models.Scenario;
import com.example.smarthouse.Repo.ConditionRepository;
import com.example.smarthouse.Repo.ScenarioRepository;
import com.example.smarthouse.Service.ScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scenarios")
public class ScenarioController {

    @Autowired
    private ScenarioService scenarioService;
    @Autowired
    private ScenarioRepository scenarioRepository;
    @Autowired
    private ConditionRepository conditionRepository;

    @GetMapping
    public ResponseEntity<List<Scenario>> getAllScenarios() {
        List<Scenario> scenarios = scenarioService.findAll();
        return new ResponseEntity<>(scenarios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Scenario> getScenarioById(@PathVariable Long id) {
        Scenario scenario = scenarioService.findById(id);
        return new ResponseEntity<>(scenario, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Scenario> createScenario(@RequestBody Scenario scenario) {
        // Сохранение сценария
        scenarioRepository.save(scenario);
        return ResponseEntity.status(HttpStatus.CREATED).body(scenario);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Scenario> updateScenario(@PathVariable Long id, @RequestBody Scenario scenario) {
        scenario.setId(id);
        Scenario updatedScenario = scenarioService.update(scenario);
        return new ResponseEntity<>(updatedScenario, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScenario(@PathVariable Long id) {
        scenarioService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
