<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 px-6">
    <Card class="w-full max-w-md shadow-lg border-slate-200">
      <CardHeader>
        <CardTitle class="text-2xl font-bold">Recover Password</CardTitle>
        <CardDescription>Enter your email to receive a recovery link.</CardDescription>
      </CardHeader>
      <CardContent>
        <div class="space-y-4">
          <div class="space-y-2">
            <Label for="email">Institutional Email</Label>
            <Input id="email" v-model="email" type="email" placeholder="user@student.ipleiria.pt" required />
          </div>

          <Button class="w-full bg-slate-900 text-white" :disabled="loading" @click="handleRequest">
            {{ loading ? 'Sending...' : 'Send Recovery Link' }}
          </Button>

          <p v-if="message" :class="isError ? 'text-red-500' : 'text-green-600'" class="text-xs text-center font-bold">
            {{ message }}
          </p>

          <div class="text-center pt-2">
            <NuxtLink to="/auth/login" class="text-xs text-slate-500 hover:underline">Back to Login</NuxtLink>
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>

<script setup>
const config = useRuntimeConfig()
const email = ref('')
const loading = ref(false)
const message = ref('')
const isError = ref(false)

async function handleRequest() {
  if (!email.value.includes('@')) {
    isError.value = true
    message.value = "Please enter a valid email."
    return
  }

  loading.value = true
  try {
    // Endpoint que configuraste no Java
    await $fetch(`${config.public.apiBase}/auth/forgot-password`, {
      method: 'POST',
      body: { email: email.value }
    })
    isError.value = false
    message.value = "If the account exists, a link was sent to your inbox."
  } catch (err) {
    isError.value = true
    message.value = "Error connecting to server. Try again later."
  } finally {
    loading.value = false
  }
}
</script>