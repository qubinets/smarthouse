package com.example.smarthouse.Controllers;

import com.example.smarthouse.Models.Status;
import com.example.smarthouse.Models.Task;
import com.example.smarthouse.Service.TaskService;
import com.example.smarthouse.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import java.util.List;

@RestController
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    private final MessageChannel mqttTasksRequestInputChannel;

    @Autowired
    public TaskController(TaskRepository taskRepository, @Qualifier("mqttTasksRequestInputChannel") MessageChannel mqttTasksRequestInputChannel) {
        this.taskRepository = taskRepository;
        this.mqttTasksRequestInputChannel = mqttTasksRequestInputChannel;
    }

    @PostMapping("/tasks")
    public Task createTask(@RequestBody Task task) {
        taskRepository.save(task);

        return task;
    }

    @GetMapping("/tasks/pending")
    public ResponseEntity<List<Task>> getPendingTasks() {
        List<Task> tasks = taskService.getPendingTasks();
        return ResponseEntity.ok().body(tasks);
    }
    @GetMapping("/tasks/all")
    public List<Task> getTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks;
    }
    @GetMapping("/tasks/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task id: " + id));
        return ResponseEntity.ok().body(task);
    }

    @PutMapping("/api/secure/tasks/{id}/completed")
    public ResponseEntity<String> updateTaskStatus(@PathVariable("id") Long id, @RequestHeader("X-API-Key") String apiKey) {
        if (!apiKey.equals("a1b2c3d4e5f6g7h8i9j0")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task id: " + id));
        task.setStatus(Status.COMPLETED);
        taskRepository.save(task);
        return ResponseEntity.ok("Task marked as completed.");
    }
    @PostMapping("/tasks/sendMqtt")
    public ResponseEntity<Void> sendPendingTasks() {
        Message<?> message = MessageBuilder.withPayload("sendTasks").build();
        mqttTasksRequestInputChannel.send(message);
        return ResponseEntity.ok().build();
    }
}