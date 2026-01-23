<template>
  <div class="p-8 max-w-7xl mx-auto space-y-8">
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
      <div>
        <h1 class="text-4xl font-bold tracking-tight text-slate-900">User Dashboard</h1>
        <p class="text-muted-foreground">Welcome back, {{ authStore.user?.name }} ({{ authStore.user?.role }})</p>
      </div>
      <div class="flex gap-2">
        <Button variant="outline" as-child>
          <NuxtLink to="/publications">Browse Publications</NuxtLink>
        </Button>
        <Button v-if="authStore.user?.role === 'Administrador'" variant="default">
          Platform Settings
        </Button>
      </div>
    </div>

    <div class="grid gap-4 md:grid-cols-3">
      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">My Contributions</CardTitle>
          <span class="text-2xl">📝</span>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">12</div>
          <p class="text-xs text-muted-foreground">Publications enriched</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">Comments</CardTitle>
          <span class="text-2xl">💬</span>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">48</div>
          <p class="text-xs text-muted-foreground">Active discussions</p>
        </CardContent>
      </Card>

      <Card>
        <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle class="text-sm font-medium">Alerts</CardTitle>
          <span class="text-2xl">🔔</span>
        </CardHeader>
        <CardContent>
          <div class="text-2xl font-bold">3</div>
          <p class="text-xs text-muted-foreground">New matching publications</p>
        </CardContent>
      </Card>
    </div>

    <div class="grid gap-8 md:grid-cols-2 mt-8">
      <Card v-if="authStore.user?.role === 'Administrador'" class="border-red-100 bg-red-50/10">
        <CardHeader>
          <CardTitle>Administration Hub</CardTitle>
          <CardDescription>Management tools for users and system roles.</CardDescription>
        </CardHeader>
        <CardContent class="space-y-2">
          <Button class="w-full justify-start" variant="secondary">Manage Users</Button>
          <Button class="w-full justify-start" variant="secondary">View System Logs</Button>
        </CardContent>
      </Card>

      <Card v-if="['ADMIN', 'Responsável'].includes(authStore.user?.role)" class="border-blue-100 bg-blue-50/10">
        <CardHeader>
          <CardTitle>Curation Tools</CardTitle>
          <CardDescription>Manage tags and moderate comments.</CardDescription>
        </CardHeader>
        <CardContent class="space-y-2">

          <Button class="w-full justify-start" variant="secondary" as-child>
            <NuxtLink to="/tags">Manage Master Tags</NuxtLink>
          </Button>
          <Button class="w-full justify-start" variant="secondary">Review Pending Comments</Button>
        </CardContent>
      </Card>

      <Card class="md:col-span-1">
        <CardHeader>
          <CardTitle>My Subscriptions</CardTitle>
          <CardDescription>You are following these research topics.</CardDescription>
        </CardHeader>
        <CardContent>
          <div class="flex gap-2 flex-wrap">
            <Badge variant="outline">#AI</Badge>
            <Badge variant="outline">#Quantum</Badge>
            <Badge variant="outline">#Sustainability</Badge>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '@/stores/auth-store'
import { Button } from '@/components/ui/button'
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

definePageMeta({
  middleware: 'auth'
})

const authStore = useAuthStore()
</script>