<template>
  <div class="p-8 max-w-7xl mx-auto space-y-8">

    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 border-b border-slate-200 pb-6">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-slate-900">
          Welcome back, {{ authStore.user?.name?.split(' ')[0] }}
        </h1>

        <div class="flex items-center gap-2 mt-2 text-slate-500">
          <Badge v-if="authStore.user?.role" variant="secondary" class="rounded-md text-xs font-normal bg-slate-100 text-slate-600">
            {{ authStore.user?.role }}
          </Badge>
        </div>
      </div>

      <Button as-child class="shadow-sm bg-slate-900 hover:bg-slate-800 text-white">
        <NuxtLink to="/publications">
          <Compass class="mr-2 h-4 w-4" />
          Explore Publications
        </NuxtLink>
      </Button>
    </div>

    <div class="grid gap-4 md:grid-cols-3">
      <Card v-for="(stat, index) in stats" :key="index">
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium text-slate-600">
            {{ stat.label }}
          </CardTitle>
          <component :is="stat.icon" class="h-4 w-4 text-slate-400" />
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold text-slate-900">{{ stat.value }}</div>
          <div class="flex items-center text-xs mt-1" :class="stat.trendUp ? 'text-emerald-600' : 'text-rose-600'">
            <component :is="stat.trendUp ? TrendingUp : TrendingDown" class="h-3 w-3 mr-1" />
            <span>{{ stat.trend }}</span>
            <span class="text-slate-400 ml-1">vs last month</span>
          </div>
        </CardContent>
      </Card>
    </div>

    <div class="grid gap-6 md:grid-cols-7 mt-4">

      <Card class="md:col-span-4">
        <CardHeader>
          <CardTitle>Contribution Activity</CardTitle>
          <CardDescription>Your engagement frequency over the last 7 days.</CardDescription>
        </CardHeader>
        <CardContent class="pl-2">
          <div class="flex items-end justify-between h-[200px] gap-2 pt-4 px-2">
            <div
                v-for="(day, i) in activityData"
                :key="i"
                class="flex flex-col items-center justify-end gap-2 w-full h-full group cursor-pointer"
            >
              <div
                  class="w-full bg-slate-200 rounded-t-md transition-all duration-500 group-hover:bg-slate-900 relative"
                  :style="{ height: `${day.height}%` }"
              >
                <div class="opacity-0 group-hover:opacity-100 absolute -top-8 left-1/2 -translate-x-1/2 bg-slate-900 text-white text-xs py-1 px-2 rounded transition-opacity shadow-sm z-10">
                  {{ day.value }}
                </div>
              </div>
              <span class="text-xs text-slate-400 font-medium group-hover:text-slate-900 transition-colors">
                {{ day.label }}
              </span>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card class="md:col-span-3">
        <CardHeader>
          <CardTitle>Top Interests</CardTitle>
          <CardDescription>Topics you interact with the most.</CardDescription>
        </CardHeader>
        <CardContent class="space-y-6">
          <div v-for="topic in topTopics" :key="topic.name" class="space-y-2">
            <div class="flex items-center justify-between text-sm">
              <div class="flex items-center font-medium text-slate-700">
                <Hash class="mr-1 h-3 w-3 text-slate-400" />
                {{ topic.name }}
              </div>
              <span class="text-slate-500">{{ topic.percentage }}%</span>
            </div>
            <div class="h-2 w-full bg-slate-100 rounded-full overflow-hidden">
              <div
                  class="h-full bg-slate-900 rounded-full transition-all duration-1000 ease-out"
                  :style="{ width: `${topic.percentage}%` }"
              ></div>
            </div>
          </div>

          <Button variant="outline" class="w-full text-xs h-8" as-child>
            <NuxtLink to="/tags">Manage Interests</NuxtLink>
          </Button>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useAuthStore } from '@/stores/auth-store'
import { Button } from '@/components/ui/button'
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

import {
  Compass,
  FileText,
  MessageSquare,
  Bell,
  TrendingUp,
  TrendingDown,
  Hash
} from 'lucide-vue-next'

definePageMeta({
  middleware: 'auth'
})

const authStore = useAuthStore()

const isAdminOrManager = computed(() => {
  const role = authStore.user?.role
  return ['ADMIN', 'Responsável', 'Administrador', 'Manager'].includes(role || '')
})

const stats = ref([
  {
    label: 'Total Contributions',
    value: '1,284',
    icon: FileText,
    trend: '+12%',
    trendUp: true
  },
  {
    label: 'Active Discussions',
    value: '48',
    icon: MessageSquare,
    trend: '+4%',
    trendUp: true
  },
  {
    label: 'Pending Alerts',
    value: '3',
    icon: Bell,
    trend: '-2%',
    trendUp: false
  }
])

const activityData = ref([
  { label: 'Mon', height: 40, value: 4 },
  { label: 'Tue', height: 70, value: 7 },
  { label: 'Wed', height: 30, value: 3 },
  { label: 'Thu', height: 90, value: 9 },
  { label: 'Fri', height: 50, value: 5 },
  { label: 'Sat', height: 20, value: 2 },
  { label: 'Sun', height: 10, value: 1 },
])


const topTopics = ref([
  { name: 'Artificial Intelligence', percentage: 92 },
  { name: 'Java', percentage: 74 },
  { name: 'Data Structures', percentage: 55 },
])
</script>