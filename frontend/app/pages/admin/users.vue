<template>
  <div class="p-8 max-w-7xl mx-auto space-y-8">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-slate-900">User Management</h1>
        <p class="text-muted-foreground text-sm">Administrative control of researchers and permissions.</p>
      </div>
      <Button @click="openModal()" class="font-bold">+ New User</Button>
    </div>

    <div class="border rounded-lg bg-white shadow-sm overflow-hidden border-slate-200">
      <table class="w-full text-left text-sm">
        <thead class="bg-slate-50 border-b">
        <tr>
          <th class="p-4 font-bold uppercase text-[10px] text-slate-500 tracking-widest">ID</th>
          <th class="p-4 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Researcher</th>
          <th class="p-4 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Role</th>
          <th class="p-4 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Status</th>
          <th class="p-4 text-right font-bold uppercase text-[10px] text-slate-500 tracking-widest px-8">Actions</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
        <tr v-for="user in sortedUsers" :key="user.id" class="hover:bg-slate-50/50 transition-colors">
          <td class="p-4 font-mono text-xs text-slate-400">#{{ user.id }}</td>
          <td class="p-4">
            <div class="font-bold text-slate-900">{{ user.name }}</div>
            <div class="text-xs text-slate-500">{{ user.email }}</div>
          </td>
          <td class="p-4">
              <span class="px-2 py-0.5 rounded-full text-[10px] font-bold border uppercase tracking-tight"
                    :class="{
                  'bg-red-50 text-red-700 border-red-100': user.role === 'ADMIN',
                  'bg-amber-50 text-amber-700 border-amber-100': user.role === 'RESPONSAVEL',
                  'bg-slate-50 text-slate-600 border-slate-200': user.role === 'COLABORADOR'
                }">
                {{ user.role }}
              </span>
          </td>
          <td class="p-4">
              <span class="px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-tight"
                    :class="user.status === 'Active' ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500'">
                {{ user.status }}
              </span>
          </td>
          <td class="p-4 text-right space-x-2 px-8">
            <Button variant="ghost" size="sm" @click="viewLogs(user.name)" class="h-8 text-blue-600 hover:bg-blue-50 text-xs font-bold">📜 Logs</Button>
            <Button variant="outline" size="sm" @click="openModal(user)" class="h-8 text-xs font-bold">Edit</Button>
            <Button variant="destructive" size="sm" @click="confirmDelete(user.id)" class="h-8 text-xs font-bold">Remove</Button>
          </td>
        </tr>
        </tbody>
      </table>
      <div v-if="userStore.isLoading" class="p-12 text-center text-muted-foreground italic">
        Synchronizing database...
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div class="bg-white p-8 rounded-xl w-full max-w-md space-y-6 shadow-2xl border border-slate-100">
        <h2 class="text-2xl font-bold text-slate-900 tracking-tight">{{ isEditing ? 'Edit Profile' : 'New Researcher' }}</h2>

        <form @submit.prevent="handleSave" class="space-y-4">
          <div class="space-y-1.5">
            <label class="text-[10px] font-bold uppercase text-slate-400 tracking-widest">Full Name</label>
            <input v-model="form.name" required class="w-full border border-slate-200 bg-slate-50 p-2.5 rounded text-sm focus:ring-2 focus:ring-slate-900 outline-none transition-all" />
          </div>

          <div class="space-y-1.5">
            <label class="text-[10px] font-bold uppercase text-slate-400 tracking-widest">Email Address</label>
            <input v-model="form.email" type="email" required class="w-full border border-slate-200 bg-slate-50 p-2.5 rounded text-sm focus:ring-2 focus:ring-slate-900 outline-none transition-all" />
          </div>

          <div v-if="!isEditing" class="space-y-1.5">
            <label class="text-[10px] font-bold uppercase text-slate-400 tracking-widest">Initial Password</label>
            <input v-model="form.password" type="password" required class="w-full border border-slate-200 bg-slate-50 p-2.5 rounded text-sm focus:ring-2 focus:ring-slate-900 outline-none transition-all" />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <label class="text-[10px] font-bold uppercase text-slate-400 tracking-widest">Access Role</label>
              <select v-model="form.role" class="w-full border border-slate-200 bg-slate-50 p-2.5 rounded text-sm outline-none cursor-pointer">
                <option value="COLABORADOR">Collaborator</option>
                <option value="RESPONSAVEL">Lead Researcher</option>
                <option value="ADMIN">Administrator</option>
              </select>
            </div>

            <div v-if="isEditing" class="space-y-1.5">
              <label class="text-[10px] font-bold uppercase text-slate-400 tracking-widest">Account Status</label>
              <select v-model="form.status" class="w-full border border-slate-200 bg-slate-50 p-2.5 rounded text-sm outline-none cursor-pointer">
                <option value="Active">Active</option>
                <option value="Inactive">Suspended</option>
              </select>
            </div>
          </div>

          <div class="flex justify-end gap-3 pt-6 border-t border-slate-50">
            <Button type="button" variant="ghost" @click="showModal = false" class="text-xs font-bold uppercase tracking-widest text-slate-400 hover:text-slate-900">Cancel</Button>
            <Button type="submit" :disabled="isSubmitting" class="px-8 text-xs font-bold uppercase tracking-widest">
              {{ isSubmitting ? 'Processing...' : (isEditing ? 'Save Changes' : 'Create Account') }}
            </Button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useUserStore } from '@/stores/user-store'
import { Button } from '@/components/ui/button'

const userStore = useUserStore()
const showModal = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false)
const currentId = ref(null)
const originalStatus = ref('')

const form = reactive({
  name: '',
  email: '',
  password: '',
  role: 'COLABORADOR',
  status: 'Active',
  profilePicture: '/Default-Icon.jpg'
})

// ✅ Ordenar utilizadores por ID Decrescente para evitar "saltos" na tabela
const sortedUsers = computed(() => {
  return [...userStore.users].sort((a, b) => b.id - a.id)
})

onMounted(() => {
  userStore.fetchUsers()
})

definePageMeta({ middleware: 'auth-admin' })

function viewLogs(name) {
  navigateTo(`/admin/logs?name=${name}`)
}

function openModal(user = null) {
  isSubmitting.value = false

  // Limpar formulário antes de preencher para evitar dados "fantasma"
  Object.assign(form, {
    name: '',
    email: '',
    password: '',
    role: 'COLABORADOR',
    status: 'Active',
    profilePicture: '/Default-Icon.jpg'
  })

  if (user) {
    isEditing.value = true
    currentId.value = user.id
    originalStatus.value = user.status
    form.name = user.name
    form.email = user.email
    form.role = user.role
    form.status = user.status
    form.profilePicture = user.profilePicture || '/Default-Icon.jpg'
  } else {
    isEditing.value = false
    currentId.value = null
  }
  showModal.value = true
}

async function confirmDelete(id) {
  if (confirm("Are you sure? Removing a researcher may affect associated publications.")) {
    await userStore.deleteUser(id)
  }
}

async function handleSave() {
  isSubmitting.value = true
  try {
    if (isEditing.value) {
      await userStore.updateUser(currentId.value, {
        name: form.name,
        email: form.email,
        role: form.role,
        profilePicture: form.profilePicture
      })

      if (form.status !== originalStatus.value) {
        await userStore.toggleUserStatus(currentId.value)
      }
    } else {
      await userStore.createUser({ ...form })
    }
    showModal.value = false
  } catch (error) {
    console.error('Error details:', error)
    alert('Failed to communicate with the Academics server.')
  } finally {
    isSubmitting.value = false
  }
}
</script>