<template>
  <div class="p-8 max-w-7xl mx-auto space-y-8">

    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-slate-900">User Management</h1>
        <p class="text-muted-foreground">Add, edit, or suspend accounts.</p>
      </div>
      <Button @click="openModal()">+ New User</Button>
    </div>

    <div class="border rounded-lg bg-white shadow overflow-hidden">
      <table class="w-full text-left text-sm">
        <thead class="bg-slate-50 border-b">
        <tr>
          <th class="p-4 font-medium text-slate-500">ID</th>
          <th class="p-4 font-medium text-slate-500">User</th>
          <th class="p-4 font-medium text-slate-500">Role</th>
          <th class="p-4 font-medium text-slate-500">Status</th>
          <th class="p-4 text-right font-medium text-slate-500">Actions</th>
        </tr>
        </thead>
        <tbody class="divide-y">
        <tr v-for="user in userStore.users" :key="user.id" class="hover:bg-slate-50 transition">
          <td class="p-4 font-mono text-xs text-slate-400">#{{ user.id }}</td>

          <td class="p-4">
            <div class="font-medium text-slate-900">{{ user.name }}</div>
            <div class="text-xs text-slate-500">{{ user.email }}</div>
          </td>

          <td class="p-4">
              <span class="px-2 py-1 rounded-full text-xs font-semibold border"
                    :class="{
                  'bg-red-50 text-red-700 border-red-200': user.role === 'Administrador',
                  'bg-blue-50 text-blue-700 border-blue-200': user.role === 'Responsável',
                  'bg-green-50 text-green-700 border-green-200': user.role === 'Colaborador'
                }">
                {{ user.role }}
              </span>
          </td>

          <td class="p-4">
              <span class="px-2 py-1 rounded-full text-xs font-semibold"
                    :class="user.status === 'Ativo' ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-600'">
                {{ user.status }}
              </span>
          </td>

          <td class="p-4 text-right space-x-2">
            <Button variant="outline" size="sm" @click="openModal(user)">Edit</Button>
            <Button variant="destructive" size="sm" @click="userStore.deleteUser(user.id)">Remove</Button>
          </td>
        </tr>
        </tbody>
      </table>

      <div v-if="userStore.isLoading" class="p-8 text-center text-muted-foreground">Loading users...</div>
    </div>

    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-white p-6 rounded-lg w-full max-w-md space-y-4 shadow-xl">
        <h2 class="text-xl font-bold">{{ isEditing ? 'Edit User' : 'New User' }}</h2>

        <form @submit.prevent="handleSave" class="space-y-4">

          <div class="space-y-1">
            <label class="text-sm font-medium">Name</label>
            <input v-model="form.name" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" placeholder="Full name" />
          </div>

          <div class="space-y-1">
            <label class="text-sm font-medium">Email</label>
            <input v-model="form.email" type="email" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" placeholder="email@example.com" />
          </div>

          <div v-if="!isEditing" class="space-y-1">
            <label class="text-sm font-medium">Password</label>
            <input v-model="form.password" type="password" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none" placeholder="******" />
          </div>

          <div class="space-y-1">
            <label class="text-sm font-medium">Role</label>
            <select v-model="form.role" class="w-full border p-2 rounded bg-white">
              <option value="Colaborador">Collaborator</option>
              <option value="Responsável">Manager (Responsável)</option>
              <option value="Administrador">Administrator</option>
            </select>
          </div>

          <div v-if="isEditing" class="space-y-1">
            <label class="text-sm font-medium">Status</label>
            <select v-model="form.status" class="w-full border p-2 rounded bg-white">
              <option value="Ativo">Active</option>
              <option value="Inativo">Inactive (Suspended)</option>
            </select>
          </div>

          <div class="flex justify-end gap-2 pt-4 border-t">
            <Button type="button" variant="ghost" @click="showModal = false">Cancel</Button>

            <Button type="submit" :disabled="isSubmitting">
              <span v-if="isSubmitting">Saving...</span>
              <span v-else>{{ isEditing ? 'Save' : 'Create' }}</span>
            </Button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user-store'
import { Button } from '@/components/ui/button'

const userStore = useUserStore()
const showModal = ref(false)
const isEditing = ref(false)
const isSubmitting = ref(false) // Variável de controle do loading
const currentId = ref(null)

const form = reactive({
  name: '',
  email: '',
  password: '',
  role: 'Colaborador',
  status: 'Ativo'
})

onMounted(() => {
  userStore.fetchUsers()
})

function openModal(user = null) {
  isSubmitting.value = false

  if (user) {
    isEditing.value = true
    currentId.value = user.id
    form.name = user.name
    form.email = user.email
    form.role = user.role
    form.status = user.status
    form.password = ''
  } else {
    isEditing.value = false
    currentId.value = null
    form.name = ''
    form.email = ''
    form.password = ''
    form.role = 'Colaborador'
    form.status = 'Ativo'
  }
  showModal.value = true
}

async function handleSave() {
  isSubmitting.value = true

  try {
    if (isEditing.value) {

      // 1️⃣ Atualiza dados base
      await userStore.updateUser(currentId.value, {
        name: form.name,
        email: form.email,
        role: form.role
      })

      // 2️⃣ Atualiza status SE mudou
      await userStore.changeUserStatus(currentId.value, form.status)

    } else {
      // CRIAR
      await userStore.createUser({
        name: form.name,
        email: form.email,
        password: form.password,
        role: form.role
      })
    }

    showModal.value = false

  } catch (error) {
    console.error(error)
    alert('Erro ao guardar utilizador')
  } finally {
    isSubmitting.value = false
  }
}

</script>