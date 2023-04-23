<template>
  <div id="sensor-chart-container" style="width: 1000px; height: 500px;">
    <h2 style="margin-top: 50px">Sensors Data</h2>
    <div id="sensor-chart-time-range" style="margin-left: 30px">
      <label>From:</label>
      <input type="datetime-local" v-model="timeRange.from" @change="fetchData" />
      <label>To:</label>
      <input type="datetime-local" v-model="timeRange.to" @change="fetchData" />
    </div>
    <div style="margin-left: 30px">
      <br>
      <input type="checkbox" v-model="selectAll" @change="toggleSelectAll"> Select All
      <br>
      <div v-for="sensor in availableSensors" :key="sensor">
        <input type="checkbox" :id="sensor" :value="sensor" v-model="selectedSensors" @change="fetchData">
        <label :for="sensor">{{ sensor }}</label>
      </div>
    </div>
    <canvas id="sensorChart"></canvas>
  </div>
</template>

<script>
import Chart from "chart.js";
import axios from "axios";

export default {
  name: "SensorChart",
  data() {
    return {
      sensors: [],
      sensorData: {},
      intervalId: null,
      timeRange: {
        from: "",
        to: "",
      },
      availableSensors: [],
      selectedSensors: [],
      selectAll: false,
    };
  },
  created() {
    const now = new Date();
    const oneWeekAgo = new Date();
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

    this.timeRange = {
      from: oneWeekAgo.toISOString(),
      to: now.toISOString(),
    };
    this.fetchData();
    this.intervalId = setInterval(() => {

      this.fetchData();
    }, 10000);
  },
  beforeUnmount() {
    clearInterval(this.intervalId);
  },
  methods: {
    fetchData() {
      if (!this.timeRange.from || !this.timeRange.to) {
        const now = new Date();
        const oneWeekAgo = new Date();
        oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

        this.timeRange = {
          from: oneWeekAgo.toISOString(),
          to: now.toISOString(),
        };
      }

      axios
          .get("/api/sensors", {
            params: {
              from: this.timeRange.from,
              to: this.timeRange.to,
              sensors: this.selectedSensors.join(","),
            },
          })
          .then((response) => {
            this.sensors = response.data;
            this.sensorData = {}; // Сбрасываем объект sensorData перед группировкой новых данных
            this.groupSensors();
            this.updateAvailableSensors();
            this.renderChart();
          })
          .catch((error) => {
            console.error(error);
          });
    },
    groupSensors() {
      this.sensors.forEach((sensor) => {
        if (!this.sensorData[sensor.name]) {
          this.sensorData[sensor.name] = [];
        }
        this.sensorData[sensor.name].push({
          x: sensor.timestamp,
          y: sensor.value,
        });
      });
    },
    updateAvailableSensors() {
      this.availableSensors = Array.from(new Set(this.sensors.map((sensor) => sensor.name)));
    },
    renderChart() {
      const ctx = document.getElementById("sensorChart").getContext("2d");
      if (this.chart) {
        this.chart.destroy();
      }
      this.chart = new Chart(ctx, {
        type: "line",
        data: {
          labels: this.sensors.map((sensor) => sensor.timestamp),
          datasets: Object.entries(this.sensorData)
              .filter(([label]) => this.selectedSensors.includes(label))
              .map(([label, data], index) => ({
                label,
                data,
                backgroundColor: `rgba(${index * 50}, ${index * 20}, ${index * 10},
0.2), borderColor: rgba(${index * 50}, ${index * 20}, ${index * 10}, 1)`,
                borderWidth: 1,
              })),
        },
        options: {
          scales: {
            xAxes: [
              {
                type: "time",
                distribution: "linear",
                time: {
                  displayFormats: {
                    millisecond: "HH:mm:ss",
                    second: "HH:mm:ss",
                    minute: "HH:mm",
                    hour: "HH:mm",
                    day: "MMM D",
                    week: "ll",
                    month: "MMM YYYY",
                    quarter: "[Q]Q - YYYY",
                    year: "YYYY",
                  },
                },
              },
            ],
            yAxes: [
              {
                position: "right",
                ticks: {
                  beginAtZero: true,
                },
              },
            ],
          },
          legend: {
            position: "left",
          },
        },
      });
    },
    toggleSelectAll() {
      if (this.selectAll) {
        this.selectedSensors = [...this.availableSensors];
      } else {
        this.selectedSensors = [];
      }
      this.fetchData();
    },
  },
};
</script>