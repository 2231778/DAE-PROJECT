<template>
  <div class="p-8 max-w-7xl mx-auto space-y-8">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-slate-900">My Publications</h1>
        <p class="text-muted-foreground text-sm">Manage your research papers and track revision history.</p>
      </div>
      <Button as-child class="font-bold">
        <NuxtLink to="/publications/new">+ Submit Work</NuxtLink>
      </Button>
    </div>

    <div class="border rounded-lg bg-white shadow-sm overflow-hidden border-slate-200">
      <table class="w-full text-left text-sm">
        <thead class="bg-slate-50 border-b">
        <tr>
          <th class="p-4 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Publication</th>
          <th class="p-4 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Timeline</th>
          <th class="p-4 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Visibility</th>
          <th class="p-4 text-right font-bold uppercase text-[10px] text-slate-500 tracking-widest px-8">Actions</th>
        </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
        <tr v-for="pub in sortedPublications" :key="pub.id" class="hover:bg-slate-50/50 transition-colors">
          <td class="p-4">
            <div class="font-bold text-slate-900">{{ pub.title }}</div>
            <div class="text-[10px] text-slate-400 font-medium">ID: #{{ pub.id }} | Author: {{ pub.author }}</div>
          </td>

          <td class="p-4 space-y-1">
            <div class="text-[11px]"><span class="text-slate-400 font-bold uppercase">Created:</span> {{ formatDate(pub.creationDate) }}</div>
            <div class="text-[11px]"><span class="text-slate-400 font-bold uppercase">Updated:</span> {{ formatDate(pub.updatedDate) }}</div>
          </td>

          <td class="p-4">
            <div class="flex items-center gap-3">
              <Badge :variant="pub.visibility === 'VISIBLE' ? 'default' : 'secondary'" class="text-[10px] font-bold">
                {{ pub.visibility }}
              </Badge>
            </div>
          </td>

          <td class="p-4 text-right space-x-1 px-8">
            <Button variant="outline" size="sm" class="h-8 text-[11px] font-bold text-blue-600" @click="viewLog(pub.id)">
              📜 History
            </Button>
            <Button variant="outline" size="sm" class="h-8 text-[11px] font-bold" @click="editPub(pub.id)">View Details and Edit</Button>
          </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { usePublicationStore } from '@/stores/publication-store'
import { Button } from '@/components/ui/button'

definePageMeta({ middleware: 'auth' })
const pubStore = usePublicationStore()

const sortedPublications = computed(() => {
  return [...pubStore.myPublications].sort((a, b) => b.id - a.id)
})

onMounted(() => pubStore.fetchMyPublications())

function formatDate(dateString) {
  const date = new Date(dateString)
  return `${date.toLocaleDateString()} ${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`
}

function editPub(id) { navigateTo(`/publications/${id}`) }
function viewLog(id) { navigateTo(`/publications/log/${id}`) }
</script>