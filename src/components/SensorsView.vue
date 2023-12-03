<template>
  <div class="main-view-container">
    <div class="navbar-container">
      <NavbarView></NavbarView>
    </div>
    <div class="main-content">
      <div class="sensors-carts-container">
        <h2>Data</h2>
        <SensorsViewCarts @sensor-selected="handleSensorSelected"></SensorsViewCarts>
      </div>
      <div class="sensor-chart-container" id="sensor-chart-container" >
        <h3>Chart</h3>
        <div class="sensor-chart-time-range" id="sensor-chart-time-range" >
          <label>From:</label>
          <input type="datetime-local" v-model="timeRange.from" @change="fetchData" />
          <label>To:</label>
          <input type="datetime-local" v-model="timeRange.to" @change="fetchData" />
        </div>
        <canvas id="sensorChart"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
import Chart from "chart.js";
import axios from "axios";
import zoomPlugin from 'chartjs-plugin-zoom';
import NavbarView from "@/components/NavbarView";
import SensorsViewCarts from "@/components/SensorsViewCarts";

export default {
  components: {NavbarView, SensorsViewCarts},
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
      selectedSensors: [],
    };
  },
  created() {
    const now = new Date();
    const oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);

    // Преобразование в формат datetime-local
    this.timeRange = {
      from: this.formatDate(oneHourAgo),
      to: this.formatDate(now),
    };

    this.fetchData();
    this.intervalId = setInterval(() => {
      this.fetchData();
    }, 5000);
  },
  beforeUnmount() {
    clearInterval(this.intervalId);
  },
  methods: {
    formatDate(date) {
      const dateTimeFormat = new Intl.DateTimeFormat('en', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' });
      const [{ value: mo },,{ value: da },,{ value: ye },,{ value: ho },,{ value: mi },,{ value: se }] = dateTimeFormat .formatToParts(date);

      return `${ye}-${mo}-${da}T${ho}:${mi}:${se}`;
    },
    fetchData() {
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
            this.sensorData = {}; // Reset sensorData before grouping new data
            this.groupSensors();
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
    renderChart() {
      const ctx = document.getElementById("sensorChart").getContext("2d");

      const datasets = Object.entries(this.sensorData)
          .filter(([label]) => this.selectedSensors.includes(label))
          .map(([label, data], index) => ({
            label,
            data,
            backgroundColor: "rgba(83,168,236,0.2)",
            borderColor: "rgb(83,168,236)",
            borderWidth: 1,
          }));

      let minValue = Math.min(...this.sensors.map(sensor => sensor.value));
      let maxValue = Math.max(...this.sensors.map(sensor => sensor.value));

      if (this.chart) {
        this.chart.data.datasets = datasets;
        this.chart.update({duration: 0});
      } else {
        this.chart = new Chart(ctx, {
          type: "line",
          data: {
            labels: this.sensors.map((sensor) => sensor.timestamp),
            datasets,
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
                    beginAtZero: false,
                    suggestedMin: minValue,
                    suggestedMax: maxValue
                  },
                },
              ],
              legend: {
                position: "left",
              },
            },
            plugins: {
              zoom: {
                // Container for pan options
                pan: {
                  // Boolean to enable panning
                  enabled: true,

                  // Panning directions. Remove the appropriate direction to disable
                  // Eg. 'y' would only allow panning in the y direction
                  mode: 'xy'
                },

                // Container for zoom options
                zoom: {
                  // Boolean to enable zooming
                  enabled: true,

                  // Zooming directions. Remove the appropriate direction to disable
                  // Eg. 'y' would only allow zooming in the y direction
                  mode: 'xy',
                }
              }
            }
          },
        });
      }
    },

    handleSensorSelected(sensorName) {
      this.selectedSensors = [sensorName];
      this.fetchData();
      this.chart.resetZoom();
      this.chart.update();
    },
  },
};
</script>