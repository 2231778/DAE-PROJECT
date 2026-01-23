<template>
  <div class="p-8 max-w-7xl mx-auto space-y-8">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-slate-900">Tag Management</h1>
        <p class="text-muted-foreground italic">Add or remove system categories for research classification.</p>
      </div>
      <Button @click="openModal()" class="bg-slate-900 hover:bg-slate-800 text-white font-bold transition-transform active:scale-95">
        + New Tag
      </Button>
    </div>

    <div class="border rounded-lg p-4 bg-white shadow-sm overflow-hidden">
      <table class="w-full text-left text-sm">
        <thead class="border-b bg-slate-50/50">
        <tr>
          <th class="p-4 font-bold uppercase text-[11px] text-slate-500 tracking-wider">ID</th>
          <th class="p-4 font-bold uppercase text-[11px] text-slate-500 tracking-wider">Name</th>
          <th class="p-4 font-bold uppercase text-[11px] text-slate-500 tracking-wider">Description</th>
          <th class="p-4 font-bold uppercase text-[11px] text-slate-500 tracking-wider">Visibility</th>
          <th class="p-4 text-right font-bold uppercase text-[11px] text-slate-500 tracking-wider">Actions</th>
        </tr>
        </thead>
        <tbody class="divide-y">
        <tr
            v-for="tag in tagStore.tags"
            :key="tag.id"
            class="hover:bg-slate-50/50 transition-colors"
        >
          <td class="p-4 font-mono text-slate-400">#{{ tag.id }}</td>
          <td class="p-4">
              <span class="px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-bold border border-blue-100 uppercase tracking-tighter">
                {{ tag.name }}
              </span>
          </td>
          <td class="p-4 text-slate-600 max-w-xs truncate">{{ tag.description }}</td>
          <td class="p-4">
            <button
                @click="tagStore.toggleVisibility(tag.id)"
                :class="tag.visibility === 'VISIBLE' ? 'bg-green-500' : 'bg-slate-300'"
                class="relative inline-flex h-5 w-10 items-center rounded-full transition-colors cursor-pointer shadow-inner"
            >
                <span
                    :class="tag.visibility === 'VISIBLE' ? 'translate-x-5' : 'translate-x-1'"
                    class="inline-block h-3 w-3 transform rounded-full bg-white transition-transform"
                />
            </button>
          </td>
          <td class="p-4 text-right space-x-2">
            <Button variant="outline" size="sm" @click="openModal(tag)" class="h-8 text-xs font-bold border-slate-200">
              Edit
            </Button>
            <Button
                variant="destructive"
                size="sm"
                @click="confirmDelete(tag.id)"
                class="h-8 text-xs font-bold shadow-sm"
            >
              Delete
            </Button>
          </td>
        </tr>
        </tbody>
      </table>

      <div v-if="tagStore.isLoading" class="p-12 text-center text-muted-foreground">
        <div class="animate-spin h-6 w-6 border-2 border-slate-900 border-t-transparent rounded-full mx-auto mb-2"></div>
        <p class="text-xs font-bold uppercase tracking-widest">Updating Tags...</p>
      </div>
      <div v-if="!tagStore.isLoading && tagStore.tags.length === 0" class="p-20 text-center">
        <div class="text-4xl mb-4 grayscale opacity-30">🏷️</div>
        <h3 class="font-bold text-slate-900">No categories found</h3>
        <p class="text-sm text-muted-foreground mt-1">Create your first tag to start organizing publications.</p>
      </div>
    </div>

    <div v-if="showModal" class="fixed inset-0 bg-slate-900/40 backdrop-blur-[2px] flex items-center justify-center z-50 p-4">
      <div class="bg-white p-8 rounded-xl w-full max-w-md space-y-6 shadow-2xl border border-slate-100">
        <div class="space-y-1">
          <h2 class="text-2xl font-bold text-slate-900">
            {{ isEditing ? 'Edit Tag' : 'New Category' }}
          </h2>
          <p class="text-xs text-slate-500 uppercase tracking-tighter">Enter the details for this classification tag</p>
        </div>

        <div class="space-y-4">
          <div class="space-y-2">
            <Label class="text-[10px] font-bold uppercase text-slate-400">Tag Name</Label>
            <Input
                v-model="form.name"
                class="w-full bg-slate-50 border-slate-200 focus:ring-slate-900"
                placeholder="e.g. Artificial Intelligence"
            />
          </div>

          <div class="space-y-2">
            <Label class="text-[10px] font-bold uppercase text-slate-400">Description</Label>
            <textarea
                v-model="form.description"
                rows="3"
                class="w-full border border-slate-200 bg-slate-50 p-3 rounded-md text-sm focus:ring-2 focus:ring-slate-900 outline-none transition-all"
                placeholder="Briefly describe what this tag covers..."
            ></textarea>
          </div>

        </div>

        <div class="flex justify-end gap-3 pt-4 border-t border-slate-50">
          <Button variant="ghost" @click="showModal = false" class="text-xs font-bold uppercase tracking-widest text-slate-400 hover:text-slate-900">
            Cancel
          </Button>
          <Button @click="handleSave" class="bg-slate-900 text-white text-xs font-bold uppercase tracking-widest px-8">
            {{ isEditing ? 'Save Changes' : 'Create Tag' }}
          </Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue' // Importações essenciais da Vue
import { useTagStore } from '@/stores/tag-store'
import { useAuthStore } from '@/stores/auth-store' // Necessário para o middleware
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'

const tagStore = useTagStore()
const authStore = useAuthStore()

// Controlo do Modal e Formulário
const showModal = ref(false)
const isEditing = ref(false)
const currentId = ref(null)

const form = reactive({
  name: '',
  description: '',
})

// Segurança: Bloqueia utilizadores sem permissões
definePageMeta({
  middleware: function (to, from) {
    const auth = useAuthStore()
    const role = auth.user?.role?.toUpperCase()
    if (role !== 'ADMIN' && role !== 'RESPONSAVEL') {
      return navigateTo('/dashboard')
    }
  }
})

// Carregamento inicial
onMounted(() => {
  tagStore.fetchTags()
})

// Abre o modal em modo de criação ou edição
function openModal(tag = null) {
  if (tag) {
    isEditing.value = true
    currentId.value = tag.id
    form.name = tag.name
    form.description = tag.description
  } else {
    isEditing.value = false
    currentId.value = null
    form.name = ''
    form.description = ''

  }
  showModal.value = true
}

// Confirmação extra antes de apagar
async function confirmDelete(id) {
  if (confirm("Are you sure? This will remove this category from all associated publications.")) {
    await tagStore.deleteTag(id)
  }
}

// Persistência de dados
async function handleSave() {
  if (!form.name.trim()) {
    alert("Please enter a tag name.")
    return
  }

  try {
    if (isEditing.value) {
      await tagStore.updateTag(currentId.value, {
        name: form.name,
        description: form.description
      })
    } else {
      await tagStore.createTag({ ...form })
    }
    showModal.value = false
  } catch (error) {
    console.error("Failed to save tag:", error)
    alert("Error saving tag. Check the console for details.")
  }
}
</script>

<style scoped>
@reference "@/assets/css/tailwind.css";

/* Transição suave para o modal */
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>