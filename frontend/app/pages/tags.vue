<template>
  <div class="p-8 max-w-7xl mx-auto space-y-8">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-slate-900">Tag Management</h1>
        <p class="text-muted-foreground">Add or remove system categories.</p>
      </div>
      <Button @click="openModal()">+ New Tag</Button>
    </div>

    <div class="border rounded-lg p-4 bg-white shadow">
      <table class="w-full text-left text-sm">
        <thead class="border-b">
        <tr>
          <th class="p-4 font-medium">ID</th>
          <th class="p-4 font-medium">Name</th>
          <th class="p-4 font-medium">Description</th>
          <th class="p-4 font-medium">Visibility</th>
          <th class="p-4 text-right font-medium">Actions</th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="tag in tagStore.tags"
            :key="tag.id"
            class="border-b hover:bg-gray-50"
        >
          <td class="p-4 font-bold">{{ tag.id }}</td>
          <td class="p-4">
              <span
                  class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs font-semibold"
              >
                {{ tag.name }}
              </span>
          </td>
          <td class="p-4 text-gray-500">{{ tag.description }}</td>

          <td class="p-4">
            <button
                @click="tagStore.toggleVisibility(tag.id)"
                :class="tag.visibility === 'VISIBLE' ? 'bg-green-500' : 'bg-gray-300'"
                class="relative inline-flex h-6 w-11 items-center rounded-full transition-colors"
            >
                <span
                    :class="tag.visibility === 'VISIBLE' ? 'translate-x-6' : 'translate-x-1'"
                    class="inline-block h-4 w-4 transform rounded-full bg-white transition"
                />
            </button>
          </td>

          <td class="p-4 text-right space-x-2">
            <Button variant="outline" size="sm" @click="openModal(tag)">
              Edit
            </Button>
            <Button
                variant="destructive"
                size="sm"
                @click="tagStore.deleteTag(tag.id)"
            >
              Delete
            </Button>
          </td>
        </tr>
        </tbody>
      </table>

      <div
          v-if="tagStore.isLoading"
          class="p-8 text-center text-muted-foreground"
      >
        Loading...
      </div>
      <div
          v-if="!tagStore.isLoading && tagStore.tags.length === 0"
          class="p-8 text-center text-muted-foreground"
      >
        No tags found.
      </div>
    </div>

    <div
        v-if="showModal"
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-50"
    >
      <div class="bg-white p-6 rounded-lg w-full max-w-md space-y-4 shadow-xl">
        <h2 class="text-xl font-bold">
          {{ isEditing ? 'Edit Tag' : 'Create New Tag' }}
        </h2>

        <div class="space-y-2">
          <label class="block text-sm font-medium">Name</label>
          <input
              v-model="form.name"
              class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"
              placeholder="e.g. Backend"
          />
        </div>

        <div class="space-y-2">
          <label class="block text-sm font-medium">Description</label>
          <input
              v-model="form.description"
              class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 outline-none"
              placeholder="Tag description"
          />
        </div>

        <div class="space-y-2">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
                type="checkbox"
                v-model="form.visibility"
                class="w-4 h-4 text-blue-600 rounded"
            />
            <span class="text-sm">Is this tag visible publically?</span>
          </label>
        </div>

        <div class="flex justify-end gap-2 pt-4">
          <Button variant="ghost" @click="showModal = false">
            Cancel
          </Button>
          <Button @click="handleSave">
            {{ isEditing ? 'Save' : 'Create' }}
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useTagStore } from '@/stores/tag-store'
import { Button } from '@/components/ui/button'

const tagStore = useTagStore()
const showModal = ref(false)
const isEditing = ref(false)
const currentId = ref(null)

// O formulário mantém-se com boolean (true/false) para funcionar com o Checkbox
const form = reactive({
  name: '',
  description: '',
})

onMounted(() => {
  tagStore.fetchTags()
})

function openModal(tag = null) {
  if (tag) {
    isEditing.value = true
    currentId.value = tag.id
    form.name = tag.name
    form.description = tag.description

    form.visibility = tag.visibility === 'VISIBLE'
  } else {
    isEditing.value = false
    currentId.value = null
    form.name = ''
    form.description = ''

  }
  showModal.value = true
}

async function handleSave() {
  try {
    // CORREÇÃO: Prepara o objeto convertendo Boolean -> String para a API
    const payload = {
      name: form.name,
      description: form.description || null, // Envia null se vazio
      visibility: form.visibility ? 'VISIBLE' : 'INVISIBLE'
    }

    if (isEditing.value) {
      await tagStore.updateTag(currentId.value, payload)
    } else {
      await tagStore.createTag(payload)
    }

    showModal.value = false
  } catch (e) {
    // Mostra o erro real vindo do backend
    const msg = e.data?.message || e.message || "Erro desconhecido"
    alert(`Erro ao salvar: ${msg}`)
  }
}
</script>