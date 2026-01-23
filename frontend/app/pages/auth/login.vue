<template>
  <div class="flex items-center justify-center min-h-[calc(100-64px)] bg-slate-50/50 px-4 py-20">
    <Card class="w-full max-w-md shadow-xl border-slate-200">
      <CardHeader class="space-y-2 text-center">
        <div class="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-primary/10 mb-2">
          <span class="text-primary text-xl font-bold">XYZ</span>
        </div>
        <CardTitle class="text-3xl font-bold tracking-tight text-slate-900">Sign In</CardTitle>
        <CardDescription>
          Enter your member credentials to access the platform
        </CardDescription>
      </CardHeader>

      <CardContent>
        <form @submit.prevent="handleLogin" class="space-y-5">
          <div class="space-y-2 text-left">
            <Label for="email" class="text-sm font-semibold">Email</Label>
            <Input
                id="email"
                v-model="credentials.email"
                type="email"
                placeholder="xxx@gmail.com"
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
              type="button"
              @click="handleLogin"
              class="w-full bg-slate-900 hover:bg-slate-800 text-white font-bold py-6 rounded-lg transition-all cursor-pointer relative z-20"
              :disabled="isLoading"
          >
            <template v-if="isLoading">
              <span class="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></span>
              Authenticating...
            </template>
            <template v-else>
              Sign In
            </template>
          </Button>
          <div class="text-center mt-4">
            <NuxtLink
                to="/auth/forgot-password"
                class="text-xs text-slate-500 hover:text-blue-600 transition-colors"
            >
              Forgot your password?
            </NuxtLink>
          </div>
        </form>
      </CardContent>
    </Card>
  </div>
</template>

<script setup>
// Imports explícitos para garantir que o Nuxt não falha
import { ref, reactive, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth-store'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card'

const config = useRuntimeConfig()
const authStore = useAuthStore()

const credentials = reactive({
  email: '',
  password: ''
})

const errorMsg = ref('')
const isLoading = ref(false)


onMounted(() => {
  console.log("Login page mounted. API Base:", config.public.apiBase)
})

async function handleLogin() {
  console.log("Button clicked! Attempting login for:", credentials.username)

  errorMsg.value = ''
  isLoading.value = true

  try {
    const token = await $fetch(`${config.public.apiBase}/auth/login`, {
      method: 'POST',
      body: credentials
    })

    console.log("Token received successfully")

    const userData = await $fetch(`${config.public.apiBase}/auth/user`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    authStore.login(token, userData)

    console.log("Login successful, redirecting...")
    await navigateTo('/dashboard')
  } catch (err) {
    console.error("Login Error:", err)
    errorMsg.value = 'Authentication failed. Please check your connection and credentials.'
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>