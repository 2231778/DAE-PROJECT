<script setup>
import { useAuthStore } from '@/stores/auth-store'
import { storeToRefs } from 'pinia'
import { ref, reactive, computed, watch } from 'vue'

// UI Components
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'

const config = useRuntimeConfig()
const authStore = useAuthStore()
const { user, token } = storeToRefs(authStore)

const isLoadingProfile = ref(false)
const isLoadingReset = ref(false)
const profileMessage = ref('')
const profileError = ref(false)

const profileForm = reactive({
  name: '',
  email: ''
})

const passForm = reactive({
  new_password: '',
  confirm_password: ''
})

watch(
    () => user.value,
    (newUser) => {
      if (!newUser) return
      profileForm.name = newUser.name ?? ''
      profileForm.email = newUser.email ?? ''
    },
    { immediate: true, deep: true }
)

const hasChanges = computed(() => {
  if (!user.value) return false
  return profileForm.name?.trim() !== user.value.name || profileForm.email?.trim() !== user.value.email
})

const displayPicture = computed(() => {
  if (!user.value?.profilePicture) return '/Default-Icon.jpg'
  if (user.value.profilePicture.startsWith('http')) return user.value.profilePicture
  return `http://localhost:8080/academics${user.value.profilePicture}`
})

function onImageError(e) {
  e.target.src = '/Default-Icon.jpg'
}

// --- NOVO: Fetch do Histórico de Atividade ---
const { data: activityLogs, pending: pendingLogs, refresh: refreshLogs } = await useFetch(
    () => `${config.public.apiBase}/activity-log/user/${user.value?.id}`,
    {
      headers: { Authorization: `Bearer ${token.value}` },
      watch: [user],
      transform: (res) => res || []
    }
)

async function confirmAndSendReset() {
  const confirmed = confirm("Do you really want to reset your password? A link will be sent to " + user.value.email)
  if (!confirmed) return

  isLoadingReset.value = true
  try {
    await $fetch(`${config.public.apiBase}/auth/forgot-password`, {
      method: 'POST',
      body: { email: user.value.email }
    })
    alert("Check your inbox! We've sent a link.")
  } catch (error) {
    alert("Error sending request.")
  } finally {
    isLoadingReset.value = false
  }
}

async function handleUpdateProfile() {
  if (!hasChanges.value) return
  isLoadingProfile.value = true
  try {
    const updatedUser = await $fetch(`${config.public.apiBase}/users/${user.value.id}`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { ...profileForm, role: user.value.role, profilePicture: user.value.profilePicture }
    })
    authStore.user = updatedUser
    profileMessage.value = 'Profile updated!'
  } catch (error) {
    profileError.value = true
    profileMessage.value = 'Error updating profile.'
  } finally {
    isLoadingProfile.value = false
  }
}

const isLoadingPass = ref(false)
const passMessage = ref('')
const passError = ref(false)

async function handleChangePassword() {
  if (passForm.new_password !== passForm.confirm_password) {
    passError.value = true
    passMessage.value = "Passwords do not match."
    return
  }
  isLoadingPass.value = true
  try {
    await $fetch(`${config.public.apiBase}/users/password`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${token.value}` },
      body: { newPassword: passForm.new_password }
    })
    passMessage.value = 'Password updated!'
    passForm.new_password = ''
    passForm.confirm_password = ''
  } catch (error) {
    passError.value = true
    passMessage.value = 'Update failed.'
  } finally {
    isLoadingPass.value = false
  }
}

async function handleFileUpload(event) {
  const file = event.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    const updatedUser = await $fetch(`${config.public.apiBase}/users/${user.value.id}/profilepicture`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token.value}` },
      body: formData
    })
    authStore.user = updatedUser
    alert('Photo uploaded!')
  } catch (error) {
    alert('Upload failed.')
  }
}
</script>

<template>
  <div class="min-h-screen bg-slate-50/50 pb-20">
    <div class="bg-white border-b mb-8 shadow-sm">
      <div class="container mx-auto py-10 px-6 text-slate-900">
        <div class="flex items-center gap-4">
          <div class="h-12 w-12 bg-slate-900 rounded-lg flex items-center justify-center text-white text-xl shadow-inner">👤</div>
          <div>
            <h1 class="text-3xl font-bold tracking-tight">My Profile</h1>
            <p class="text-muted-foreground italic">Researcher Management Console</p>
          </div>
        </div>
      </div>
    </div>

    <main class="container mx-auto px-6 grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="space-y-6">
        <Card class="border-slate-200 shadow-sm overflow-hidden">
          <CardHeader class="border-b bg-slate-50/50">
            <CardTitle class="text-lg">Personal Identity</CardTitle>
          </CardHeader>
          <CardContent class="pt-6 space-y-6">
            <div class="flex flex-col items-center text-center">
              <div class="relative group mb-4">
                <Avatar class="h-28 w-28 border-4 border-white shadow-md transition-transform group-hover:scale-105">
                  <AvatarImage :src="displayPicture" @error="onImageError" class="object-cover" />
                  <AvatarFallback class="text-2xl bg-slate-200">{{ user?.name?.[0]?.toUpperCase() }}</AvatarFallback>
                </Avatar>
                <label class="absolute inset-0 bg-black/40 rounded-full flex flex-col items-center justify-center text-white opacity-0 group-hover:opacity-100 cursor-pointer transition-all duration-300 backdrop-blur-[2px]">
                  <span class="text-xl">📸</span>
                  <span class="text-[10px] font-bold uppercase">Change</span>
                  <input type="file" class="hidden" @change="handleFileUpload" />
                </label>
              </div>
              <div class="space-y-1">
                <h2 class="font-bold text-slate-900">{{ user?.name }}</h2>
                <Badge class="bg-slate-900 text-white hover:bg-slate-800 px-3 uppercase text-[10px] tracking-widest">
                  {{ user?.role || 'Researcher' }}
                </Badge>
              </div>
            </div>

            <form class="space-y-4 pt-4" @submit.prevent="handleUpdateProfile">
              <div class="space-y-2">
                <Label class="text-xs font-bold uppercase text-slate-500">Full Name</Label>
                <Input v-model="profileForm.name" required />
              </div>
              <div class="space-y-2">
                <Label class="text-xs font-bold uppercase text-slate-500">Institutional Email</Label>
                <Input type="email" v-model="profileForm.email" required />
              </div>
              <Button type="submit" class="w-full bg-slate-900" :disabled="isLoadingProfile || !hasChanges">
                {{ isLoadingProfile ? 'Saving...' : 'Save Changes' }}
              </Button>
            </form>
          </CardContent>
        </Card>

        <Card class="border-slate-200 shadow-sm">
          <CardHeader class="border-b bg-slate-50/50">
            <CardTitle class="text-lg">Security</CardTitle>
          </CardHeader>
          <CardContent class="pt-6 space-y-4">
            <form class="space-y-4" @submit.prevent="handleChangePassword">
              <div class="space-y-2">
                <Label class="text-xs font-bold uppercase text-slate-500">New Password</Label>
                <Input type="password" v-model="passForm.new_password" required />
              </div>
              <div class="space-y-2">
                <Label class="text-xs font-bold uppercase text-slate-500">Confirm Password</Label>
                <Input type="password" v-model="passForm.confirm_password" required />
              </div>
              <Button type="submit" variant="outline" class="w-full" :disabled="isLoadingPass">
                Update Password
              </Button>
            </form>
            <div class="pt-4 border-t border-slate-100">
              <Button variant="outline" class="w-full text-blue-600 border-blue-200 hover:bg-blue-50" @click="confirmAndSendReset" :disabled="isLoadingReset">
                {{ isLoadingReset ? 'Sending...' : 'Send Reset Link to Email' }}
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>

      <div class="lg:col-span-2">
        <Card class="border-slate-200 shadow-sm min-h-[600px] flex flex-col">
          <CardHeader class="flex flex-row items-center justify-between border-b bg-slate-50/50 px-6">
            <div>
              <CardTitle>Activity History</CardTitle>
              <CardDescription>History of your actions in the repository</CardDescription>
            </div>
            <Button size="sm" variant="outline" @click="refreshLogs" class="h-8 gap-2 shadow-sm">
              <span :class="{ 'animate-spin': pendingLogs }">⟳</span> Refresh
            </Button>
          </CardHeader>

          <CardContent class="p-0 flex-1">
            <div v-if="pendingLogs" class="flex flex-col items-center justify-center py-20 text-muted-foreground">
              <div class="h-8 w-8 border-4 border-slate-200 border-t-slate-900 rounded-full animate-spin mb-4"></div>
              <p class="text-sm font-medium">Fetching history...</p>
            </div>

            <template v-else>
              <Table>
                <TableHeader class="bg-slate-50/80">
                  <TableRow>
                    <TableHead class="px-6">Action</TableHead>
                    <TableHead>Details</TableHead>
                    <TableHead class="text-right px-6">Timestamp</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  <TableRow v-for="log in activityLogs" :key="log.id" class="hover:bg-slate-50/50 transition-colors">
                    <TableCell class="px-6">
                      <Badge
                          :class="{
                          'bg-green-100 text-green-700': log.action === 'CREATE',
                          'bg-blue-100 text-blue-700': log.action === 'UPDATE',
                          'bg-amber-100 text-amber-700': log.action === 'LOGIN',
                          'bg-red-100 text-red-700': log.action === 'DELETE'
                        }"
                          class="text-[10px] uppercase font-bold border-none"
                      >
                        {{ log.action }}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <div class="flex flex-col">
                        <span class="text-sm text-slate-700 font-medium">{{ log.details }}</span>
                        <span v-if="log.publication" class="text-[10px] text-blue-500 font-bold uppercase tracking-tight">
                          Item: {{ log.publication.title }}
                        </span>
                      </div>
                    </TableCell>
                    <TableCell class="text-right px-6">
                      <div class="flex flex-col items-end">
                        <span class="text-slate-900 text-xs font-bold">{{ new Date(log.timestamp).toLocaleDateString() }}</span>
                        <span class="text-slate-400 text-[10px]">{{ new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) }}</span>
                      </div>
                    </TableCell>
                  </TableRow>
                </TableBody>
              </Table>

              <div v-if="activityLogs?.length === 0" class="flex flex-col items-center justify-center py-32 text-center px-4">
                <div class="text-5xl mb-6 grayscale opacity-50">📜</div>
                <h3 class="text-lg font-bold text-slate-900">No activity found</h3>
                <p class="text-sm text-muted-foreground mt-1">Your actions in the system will be tracked here.</p>
              </div>
            </template>
          </CardContent>
        </Card>
      </div>
    </main>
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.4s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>