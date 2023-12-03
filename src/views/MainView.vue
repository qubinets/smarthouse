<template>
  <div class="main-view-container">
    <div class="navbar-container">
      <NavbarView></NavbarView>
    </div>
    <div class="main-content">
      <CreateTask></CreateTask>
      <TaskView></TaskView>
    </div>
  </div>
</template>

<script>
import CreateTask from '../components/CreateTask.vue';
import TaskView from "@/components/TaskView"; // Импортируйте SensorsView компонент
import NavbarView  from "@/components/NavbarView";
export default {
  name: "MainView",
  methods: {

  },
  components: {
    NavbarView,
    TaskView,
    CreateTask,

  },
};
</script>