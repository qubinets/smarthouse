package com.example.smarthouse;

import com.example.smarthouse.Models.Task;
import com.example.smarthouse.Models.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStateRepository extends JpaRepository<TaskState, Long> {

}
