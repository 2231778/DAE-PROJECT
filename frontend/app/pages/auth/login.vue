<template>
  <div class="flex items-center justify-center min-h-[calc(100-64px)] bg-slate-50/50 px-4 py-20">
    <Card class="w-full max-w-md shadow-xl border-slate-200">
      <CardHeader class="space-y-2 text-center">
        <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 mb-2">
          <span class="text-primary text-xl font-bold">XYZ</span>
        </div>
        <CardTitle class="text-3xl font-bold tracking-tight text-slate-900">Sign In</CardTitle>
        <CardDescription>
          Enter your researcher credentials to access the platform
        </CardDescription>
      </CardHeader>

      <CardContent>
        <form @submit.prevent="handleLogin" class="space-y-5">
          <div class="space-y-2 text-left">
            <Label for="username" class="text-sm font-semibold">Username</Label>
            <Input
                id="username"
                v-model="credentials.username"
                type="text"
                placeholder="e.g. john_doe"
                class="bg-white"
                required
            />
          </div>

          <div class="space-y-2 text-left">
            <div class="flex items-center justify-between">
              <Label for="password" class="text-sm font-semibold">Password</Label>
            </div>
            <Input
                id="password"
                v-model="credentials.password"
                type="password"
                placeholder="••••••••"
                class="bg-white"
                required
            />
          </div>

          <Transition name="fade">
            <div v-if="errorMsg" class="p-3 text-sm text-red-600 bg-red-50 border border-red-100 rounded-lg flex items-center gap-2">
              <span class="font-bold">⚠️</span> {{ errorMsg }}
            </div>
          </Transition>

          <Button
              type="submit"
              class="w-full bg-slate-900 hover:bg-slate-800 text-white font-bold py-6 rounded-lg transition-all"
              :disabled="isLoading"
          >
            <template v-if="isLoading">
              <span class="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></span>
              Authenticating...
            </template>
            <template v-else>
              Sign In to Repository
            </template>
          </Button>
        </form>
      </CardContent>

      <CardFooter class="flex flex-col gap-4 border-t bg-slate-50/50 py-6 text-center rounded-b-xl">
        <p class="text-xs text-muted-foreground">
          Scientific Management Platform &copy; 2026
        </p>
      </CardFooter>
    </Card>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth-store'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from '@/components/ui/card'

const config = useRuntimeConfig()
const authStore = useAuthStore()

const credentials = reactive({
  username: '',
  password: ''
})

const errorMsg = ref('')
const isLoading = ref(false)

async function handleLogin() {
  errorMsg.value = ''
  isLoading.value = true

  try {
    // 1. POST Credentials to get JWT Token
    // Matches your Java Backend: @Path("/auth/login")
    const token = await $fetch(`${config.public.apiBase}/auth/login`, {
      method: 'POST',
      body: credentials
    })

    // 2. GET User Profile using the Token
    // Matches your Java Backend: @Path("/auth/user")
    const userData = await $fetch(`${config.public.apiBase}/auth/user`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    // 3. Update Global State (Pinia Store)
    authStore.login(token, userData)

    // 4. Navigate back to Home or Dashboard
    navigateTo('/')
  } catch (err) {
    // Check if it's a 401 Unauthorized or other
    errorMsg.value = 'Authentication failed. Please check your username and password.'
    console.error('Login attempt failed:', err)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>