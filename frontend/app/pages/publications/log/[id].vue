<template>
  <div class="p-8 max-w-5xl mx-auto space-y-6">
    <div class="flex items-center gap-4">
      <Button variant="outline" size="sm" @click="$router.back()" class="h-9 px-3">
        ← Back
      </Button>
      <div>
        <h1 class="text-3xl font-bold tracking-tight text-slate-900">Publication History</h1>
        <p class="text-sm text-muted-foreground italic">
          Audit trail for Publication <span class="font-mono font-bold text-slate-900">#{{ $route.params.id }}</span>
        </p>
      </div>
    </div>

    <Card class="border-slate-200 shadow-sm overflow-hidden bg-white">
      <Table>
        <TableHeader class="bg-slate-50/80">
          <TableRow>
            <TableHead class="px-6 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Researcher</TableHead>
            <TableHead class="font-bold uppercase text-[10px] text-slate-500 tracking-widest">Operation</TableHead>
            <TableHead class="font-bold uppercase text-[10px] text-slate-500 tracking-widest">Event Details</TableHead>
            <th class="text-right px-6 font-bold uppercase text-[10px] text-slate-500 tracking-widest">Date & Time</th>
          </TableRow>
        </TableHeader>
        <TableBody class="divide-y divide-slate-100">
          <TableRow v-for="log in pubStore.publicationLogs" :key="log.id" class="hover:bg-slate-50/30 transition-colors">
            <td class="px-6 py-4">
              <div v-if="log.user" class="flex items-center gap-2">
                <Avatar class="h-6 w-6 border">
                  <AvatarImage :src="`http://localhost:8080/academics${log.user.profilePicture}`" />
                  <AvatarFallback class="text-[9px] font-bold">{{ log.user.name[0] }}</AvatarFallback>
                </Avatar>
                <span class="text-xs font-bold text-slate-700">{{ log.user.name }}</span>
              </div>
              <span v-else class="text-xs text-slate-400 italic">System Auto</span>
            </td>

            <td class="py-4">
              <Badge
                  :class="{
                  'bg-emerald-50 text-emerald-700 border-emerald-100': log.action === 'CREATE',
                  'bg-blue-50 text-blue-700 border-blue-100': log.action === 'UPDATE',
                  'bg-rose-50 text-rose-700 border-rose-100': log.action === 'DELETE'
                }"
                  class="text-[10px] font-bold border px-2 py-0"
              >
                {{ log.action }}
              </Badge>
            </td>

            <td class="py-4">
              <span class="text-sm text-slate-600 font-medium">{{ log.details }}</span>
            </td>

            <td class="text-right px-6 py-4">
              <div class="flex flex-col items-end">
                <span class="text-slate-900 text-xs font-bold">{{ new Date(log.timestamp).toLocaleDateString() }}</span>
                <span class="text-slate-400 text-[10px] font-medium">
                  {{ new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) }}
                </span>
              </div>
            </td>
          </TableRow>
        </TableBody>
      </Table>

      <div v-if="pubStore.isLoading" class="p-20 text-center">
        <div class="h-6 w-6 border-2 border-slate-900 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
        <p class="text-xs font-bold uppercase text-slate-400 tracking-widest">Fetching logs...</p>
      </div>

      <div v-else-if="pubStore.publicationLogs.length === 0" class="p-20 text-center space-y-2">
        <div class="text-3xl">📭</div>
        <h3 class="font-bold text-slate-900">No History Recorded</h3>
        <p class="text-sm text-slate-500">This publication hasn't undergone any logged changes yet.</p>
      </div>
    </Card>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { usePublicationStore } from '@/stores/publication-store'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar'

// Protection: Only researchers can see history
definePageMeta({ middleware: 'auth' })

const pubStore = usePublicationStore()
const route = useRoute()

onMounted(async () => {
  await pubStore.fetchPublicationLogs(route.params.id)
})
</script>