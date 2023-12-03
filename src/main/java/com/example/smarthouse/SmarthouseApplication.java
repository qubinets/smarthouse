package com.example.smarthouse;

import com.example.smarthouse.Models.TaskState;
import com.example.smarthouse.Repo.TaskStateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmarthouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarthouseApplication.class, args);
	}
	@Bean
	public CommandLineRunner addTaskStatus(TaskStateRepository taskStateRepository) {
		return args -> {
			int count = 4;
			String[] task = {"door", "light", "pump","sound"};
			for (int i = 0; i < count; i++) {
				TaskState ts =  new TaskState();
				ts.setTaskState(TaskState.State.OFF);
				ts.setTaskName(task[i]);
				taskStateRepository.save(ts);
			}
		};

	}
}
