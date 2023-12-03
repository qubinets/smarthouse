package com.example.smarthouse.Repo;

import com.example.smarthouse.Models.Command;
import com.example.smarthouse.Models.Status;
import com.example.smarthouse.Models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends  JpaRepository<Task, Long> {
    List<Task> findByStatus(Status pending);

    @Query("SELECT t FROM Task t WHERE t.command = :command AND t.status = 0")
    List<Task> findPendingTasksByCommand(@Param("command") Command command);
    Optional<Task> findFirstByStatusOrderByCreatedAsc(Status status);


}