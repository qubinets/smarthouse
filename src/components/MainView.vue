<template>
  <div>
    <h1>Smart House Dashboard</h1>
    <create-task></create-task>
    <task-view></task-view>
    <sensors-view></sensors-view> <!-- Добавьте тег компонента SensorsView -->
  </div>
</template>

<script>
import CreateTask from './CreateTask.vue';
import SensorsView from './SensorsView.vue';
import TaskView from "@/components/TaskView"; // Импортируйте SensorsView компонент

export default {
  name: "MainView",
  methods: {

  },
  components: {
    TaskView,
    CreateTask,
    SensorsView, // Добавьте SensorsView компонент в раздел components
  },
};
</script>