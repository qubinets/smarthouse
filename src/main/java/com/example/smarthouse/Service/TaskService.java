package com.example.smarthouse.Service;

import com.example.smarthouse.Models.Status;
import com.example.smarthouse.Models.Task;
import com.example.smarthouse.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    public List<Task> getPendingTasks() {
        return taskRepository.findByStatus(Status.PENDING);
    }
}
