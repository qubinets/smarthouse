<template>
  <h2>Tasks History</h2>
  <div id="task-view-container" style=" margin-left: 30px;
    margin-top: 20px;
    width: 380px;
    height: 380px;
    border: 2px solid #53a8ec;
    resize: both;
    overflow: auto;">
    <ul>
      <li v-for="task in tasks" :key="task.id">
        <div class="task-icon-container">
          <i v-if="task.status === 'PENDING'" class="spinner"></i>
          <i v-if="task.status === 'COMPLETED'" class="check-icon"></i>
        </div>
        <div class="task-details-container " style="flex: 1; margin-right: 20px;">
          <span> {{ task.command }} &nbsp; </span>
          <span> &nbsp;{{ task.created }}&nbsp; </span>
          <span> {{ task.status }} </span>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'TaskView',
  data() {
    return {
      tasks: [],
    };
  },
  created() {
    this.fetchData();
    setInterval(() => {
      this.fetchData();
    }, 100);
  },
  beforeUnmount() {
    clearInterval(this.intervalId);
  },
  methods: {
    fetchData() {
      axios.get('/tasks/all')
          .then(response => {
            const newTasks = response.data.slice(-10).reverse(); // Получаем последние 10 команд и разворачиваем список
            const updatedTasks = newTasks.map(task => ({
              id: task.id,
              command: task.command,
              created: new Date(task.created).toLocaleString(), // Преобразуем время создания команды в строку
              status: task.status,
            }));

            // Обновляем список команд только если получили новые данные
            if (JSON.stringify(this.tasks) !== JSON.stringify(updatedTasks)) {
              this.tasks = updatedTasks;
            }
          })
          .catch(error => {
            console.error(error);
          });
    },
  },
};
</script>

