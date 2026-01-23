<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-50 px-6">
    <Card class="w-full max-w-md shadow-lg border-slate-200">
      <CardHeader class="space-y-1">
        <CardTitle class="text-2xl font-bold">New Credentials</CardTitle>
        <CardDescription>Enter your new password below</CardDescription>
      </CardHeader>
      <CardContent>
        <form class="space-y-4" @submit.prevent="handleReset">
          <div class="space-y-2">
            <Label for="pass">New Password</Label>
            <Input id="pass" v-model="form.newPassword" type="password" required />
          </div>
          <div class="space-y-2">
            <Label for="confirm">Confirm New Password</Label>
            <Input id="confirm" v-model="form.confirmPassword" type="password" required />
          </div>

          <Button class="w-full bg-slate-900 text-white mt-4" :disabled="loading" type="submit">
            {{ loading ? 'Updating...' : 'Save New Password' }}
          </Button>

          <p v-if="statusMsg" :class="isError ? 'text-red-500' : 'text-green-600'" class="text-xs text-center font-bold">
            {{ statusMsg }}
          </p>
        </form>
      </CardContent>
    </Card>
  </div>
</template>

<script setup>
const route = useRoute()
const config = useRuntimeConfig()
const loading = ref(false)
const statusMsg = ref('')
const isError = ref(false)

const form = reactive({
  newPassword: '',
  confirmPassword: ''
})

async function handleReset() {
  if (form.newPassword !== form.confirmPassword) {
    isError.value = true
    statusMsg.value = "Passwords do not match."
    return
  }

  const token = route.query.token // Captura o token do link ?token=...
  if (!token) {
    isError.value = true
    statusMsg.value = "Missing security token."
    return
  }

  loading.value = true
  try {
    // Endpoint que configuraste no Java
    await $fetch(`${config.public.apiBase}/auth/reset-password`, {
      method: 'POST',
      query: { token: token },
      body: { newPassword: form.newPassword }
    })

    isError.value = false
    statusMsg.value = "Success! Password updated. Redirecting to login..."
    setTimeout(() => navigateTo('/auth/login'), 2500)
  } catch (err) {
    isError.value = true
    statusMsg.value = "Link expired or already used. Please request a new one."
  } finally {
    loading.value = false
  }
}
</script>