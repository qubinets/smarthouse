package com.example.smarthouse.Service;

import com.example.smarthouse.Models.*;
import com.example.smarthouse.Repo.ScenarioRepository;
import com.example.smarthouse.Repo.TaskRepository;
import com.example.smarthouse.Repo.TaskStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SensorService sensorService;
    @Autowired
    private ScenarioRepository scenarioRepository;

    @Autowired
    private TaskStateRepository taskStateRepository;

    public List<Task> getPendingTasks() {
        return taskRepository.findByStatus(Status.PENDING);
    }
    @Transactional
    @Scheduled(fixedRate = 1000) // Запускать каждые 1 секунд
    public void processScenarios() {
        List<Scenario> scenarios = scenarioRepository.findAll();
        for (Scenario scenario : scenarios) {
            if (checkCondition(scenario.getCondition())) {
                List<Task> existingTasks = taskRepository.findPendingTasksByCommand(scenario.getCommand());
                if (existingTasks.isEmpty()) {
                    createTaskFromScenario(scenario);
                }
            }
        }
    }

    private boolean checkCondition(Condition condition) {
        // Получаем текущее значение сенсора
        Sensor sensor = sensorService.findLatestBySensorName(condition.getSensorName());
        if (sensor == null) {
            return false;
        }

        float sensorValue = sensor.getValue();
        float conditionValue = condition.getValue();
        boolean valueCheck = false;

        // Проверяем условия на основе значений сенсоров
        switch (condition.getValueComparator()) {
            case LESS_THAN:
                valueCheck = sensorValue < conditionValue;
                break;
            case GREATER_THAN:
                valueCheck = sensorValue > conditionValue;
                break;
            case EQUALS:
                valueCheck = sensorValue == conditionValue;
                break;
        }

        if (!valueCheck) {
            return false;
        }

        // Проверяем условия на основе TaskState
        TaskState currentState = taskStateRepository.findByTaskName(condition.getState().getTaskName());
        if (currentState == null) {
            return false;
        }

        boolean stateCheck = false;

        switch (condition.getStateComparator()) {
            case EQUALS:
                stateCheck = currentState.getTaskState() == condition.getState().getTaskState();
                break;
            case NOT_EQUALS:
                stateCheck = currentState.getTaskState() != condition.getState().getTaskState();
                break;
        }

        // Учитываем ожидаемое состояние (expectedState)
        if (condition.getExpectedState() != null) {
            TaskState.State expectedState = TaskState.State.valueOf(condition.getExpectedState());
            stateCheck = stateCheck && (currentState.getTaskState() == expectedState);
        }

        return stateCheck;
    }
    private void createTaskFromScenario(Scenario scenario) {
        Task task = new Task();
        TaskState state = taskStateRepository.findByTaskName(scenario.getCommand().name());
        state.setTaskState(scenario.getCondition().getState().getTaskState());
        task.setCommand(scenario.getCommand());
        task.setStatus(Status.PENDING);
        task.setCreated(LocalDateTime.now());

        taskRepository.save(task);
    }
}
