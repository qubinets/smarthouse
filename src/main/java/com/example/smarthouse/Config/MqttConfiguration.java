package com.example.smarthouse.Config;

import com.example.smarthouse.Models.Sensor;
import com.example.smarthouse.Models.Status;
import com.example.smarthouse.Models.Task;
import com.example.smarthouse.Models.TaskState;
import com.example.smarthouse.Service.SensorService;
import com.example.smarthouse.TaskRepository;
import com.example.smarthouse.TaskStateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler; // Замените этот импорт

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.*;


@Configuration
public class MqttConfiguration {
    @Value("${mqtt.broker.url}")
    private String mqttBrokerUrl;
    @Value("${mqtt.broker.username}")
    private String mqttUsername;
    @Value("${mqtt.broker.password}")
    private String mqttPassword;
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfiguration.class.getName());
    // Добавьте HashSet для отслеживания отправленных задач
    private final Set<Long> sentTasks = new HashSet<>();


    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { mqttBrokerUrl });
        options.setUserName(mqttUsername); // Добавьте эту строку
        options.setPassword(mqttPassword.toCharArray()); // Добавьте эту строку
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel mqttCompletedTasksInputChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel mqttSensorDataInputChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageChannel mqttTasksRequestInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("springBootClient", mqttClientFactory(), "tasks/state/+/update");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    @Bean
    public MessageProducer mqttCompletedTasksInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("springBootClientCompleted", mqttClientFactory(), "tasks/completed/update");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttCompletedTasksInputChannel());
        return adapter;
    }
    @Bean
    public MessageProducer mqttSensorDataInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("springBootClientSensorData", mqttClientFactory(), "sensors/+/value");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttSensorDataInputChannel());
        return adapter;
    }
    @Bean
    public MessageProducer mqttTasksRequestInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("springBootClientTasksRequest", mqttClientFactory(), "tasks/request");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttTasksRequestInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler(TaskStateRepository taskStateRepository) {
        return message -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            String newState = message.getPayload().toString();

            LOGGER.info("Received MQTT message: Topic={}, State={}", topic, newState);

            Long taskId = Long.parseLong(topic.split("/")[2]);

            TaskState task = taskStateRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid task id: " + taskId));
            TaskState.State newTaskState = TaskState.State.valueOf(newState);

            if (newTaskState != task.getTaskState()) {
                task.setTaskState(newTaskState);
            }
            taskStateRepository.save(task);

            LOGGER.info("Updated task state: TaskId={}, NewState={}", taskId, newTaskState);
        };
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttCompletedTasksInputChannel")
    public MessageHandler mqttCompletedTasksMessageHandler(TaskRepository taskRepository) {
        return message -> {
            String payload = message.getPayload().toString();
            String[] parts = payload.split(",");
            Long taskId = Long.parseLong(parts[0]);


            LOGGER.info("Received MQTT Status updated: TaskId={}, COMPLETED", taskId);

            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid task id: " + taskId));


                task.setStatus(Status.COMPLETED);

            taskRepository.save(task);


        };
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttSensorDataInputChannel")
    public MessageHandler mqttSensorDataMessageHandler(SensorService sensorService) {
        return message -> {
            try {
                String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
                String payload = message.getPayload().toString();
                String[] parts = payload.split(",");

                String sensorName = topic.split("/")[1];
                // Попробуйте это:
                float value;
                if (parts[0].equalsIgnoreCase("NAN")) {
                    value = Float.NaN; // Используйте значение Float.NaN для обозначения "NAN"
                } else {
                    value = Float.parseFloat(parts[0]);
                }
                String timestamp = parts[1];

                LOGGER.info("Received MQTT Sensor Data: SensorName={}, Value={}, Timestamp={}", sensorName, value, timestamp);

                Sensor sensor = new Sensor();
                sensor.setName(sensorName);
                sensor.setValue(value);
                sensor.setTimestamp(LocalDateTime.parse(timestamp));
                sensorService.save(sensor);
            } catch (NumberFormatException e) {
                // Обработайте исключение здесь
                LOGGER.error("Error parsing float value: ", e);
            }
        };
    }

}