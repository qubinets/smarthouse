<template>
  <div>
    <h2>Execute Task</h2>
    <div class="task-container">
      <div v-for="taskState in orderedTaskStates" :key="taskState.id">
        <div class="task-status">
          <span class="status-indicator" :class="{ 'on': taskState.taskState === 'ON', 'off': taskState.taskState === 'OFF' }"></span>
        </div>
        <button class="btn" :disabled="isLoading[taskState.taskName]" @click="submitForm(taskState.taskName)">
          <span v-if="!isLoading[taskState.taskName]">{{ taskState.taskName }}</span>
          <span v-else class="spinner"></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      isLoading: {
        door: false,
        light: false,
        pump: false,
        sound: false,
      },
      taskStates: [],
      intervalId: null,
    };
  },
  computed: {
    orderedTaskStates() {
      return this.taskStates.slice().sort((a, b) => a.id - b.id);
    },
  },
  async created() {
    await this.fetchTaskStates();
    this.intervalId = setInterval(() => {
      this.fetchTaskStates();
    }, 100);
  },
  beforeUnmount() {
    clearInterval(this.intervalId);
  },
  methods: {
    async fetchTaskStates() {
      try {
        const response = await axios.get('/tasks/state');
        this.taskStates = response.data;
      } catch (error) {
        console.error('Error fetching task states:', error);
      }
    },
    async submitForm(command) {
      if (command) {
        this.isLoading[command] = true;
        try {
          const taskData = {
            command: command,
            created: new Date().toISOString(),
            status: "PENDING",
          };
          const response = await axios.post('/tasks', taskData);
          console.log('Task created:', response.data);
          await this.checkTaskStatus(response.data.id, command);

        } catch (error) {
          console.error('Error creating task:', error);
        } finally {
          this.isLoading[command] = false;
        }
      } else {
        console.warn('No command selected');
      }
    },
    async checkTaskStatus(taskId, command) {
      try {
        while (true) {
          const response = await axios.get(`/tasks/${taskId}`);
          if (response.data.status === 'COMPLETED') {
            console.log('Task completed:', response.data);
            break;
          }
          await new Promise((resolve) => setTimeout(resolve, 1000));
        }
      } catch (error) {
        console.error('Error checking task status:', error);
      }
    },
  },
};
</script>