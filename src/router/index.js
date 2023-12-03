import { createRouter, createWebHistory } from 'vue-router';
import MainView from '../views/MainView.vue';
import SensorsView from "@/components/SensorsView";
import ScenarioView from "@/components/ScenarioView";

const routes = [
    {
        path: '/',
        name: 'MainView',
        component: MainView,
    },
    {
        path: '/sensors',
        name: 'SensorsView',
        component: SensorsView,
    },
    {
        path: '/scenario',
        name: 'ScenarioView',
        component: ScenarioView,
    },
];

const index = createRouter({
    history: createWebHistory(),
    routes,
});

export default index;