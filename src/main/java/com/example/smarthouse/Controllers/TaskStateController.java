package com.example.smarthouse.Controllers;

import com.example.smarthouse.Models.Status;
import com.example.smarthouse.Models.Task;
import com.example.smarthouse.Models.TaskState;
import com.example.smarthouse.TaskRepository;
import com.example.smarthouse.TaskStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TaskStateController {
    @Autowired
    private TaskStateRepository taskStateRepository;

    @GetMapping("/tasks/state")
    public List<TaskState> getTasksStates(){
        List<TaskState> tasks = taskStateRepository.findAll();
        return tasks;
    }
    @PutMapping("/tasks/state/{id}/update")
    public ResponseEntity<String> updateTaskState(@PathVariable("id") Long id, @RequestHeader("X-API-Key") String apiKey, @RequestBody Map<String, String> updateTaskState) {
        if (!apiKey.equals("a1b2c3d4e5f6g7h8i9j0")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }
        TaskState task = taskStateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task id: " + id));

        String newTaskStateStr = updateTaskState.get("taskState");
        TaskState.State newTaskState = TaskState.State.valueOf(newTaskStateStr);

        if (newTaskState != task.getTaskState()) {
            task.setTaskState(newTaskState);
        }
        taskStateRepository.save(task);
        return ResponseEntity.ok("Task state updated.");
    }
}
