<template>
  <div class="p-8 max-w-7xl mx-auto space-y-6">
    <div class="flex justify-between items-end">
      <div>
        <h1 class="text-3xl font-bold text-slate-900 tracking-tight">System Audit Logs</h1>
        <p class="text-muted-foreground italic text-sm">Real-time monitoring of researcher activities and system updates.</p>
      </div>
      <Button variant="outline" @click="userStore.fetchAllActivityLogs()" :disabled="userStore.isLoading" class="font-bold border-slate-200">
        <span :class="{ 'animate-spin': userStore.isLoading }" class="mr-2">⟳</span> Refresh Data
      </Button>
    </div>

    <Card class="p-5 border-slate-200 bg-slate-50/50 shadow-sm border-dashed">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="space-y-1.5">
          <Label class="text-[10px] font-bold uppercase text-slate-500 tracking-widest">Researcher Name</Label>
          <Input v-model="filters.name" placeholder="Search actor..." class="bg-white border-slate-200 focus:ring-slate-900" />
        </div>
        <div class="space-y-1.5">
          <Label class="text-[10px] font-bold uppercase text-slate-500 tracking-widest">Operation Type</Label>
          <select v-model="filters.action" class="w-full h-10 px-3 rounded-md border border-slate-200 bg-white text-sm focus:ring-2 focus:ring-slate-900 outline-none transition-all cursor-pointer">
            <option value="">All Actions</option>
            <option value="CREATE">CREATE</option>
            <option value="UPDATE">UPDATE</option>
            <option value="DELETE">DELETE</option>
          </select>
        </div>
        <div class="space-y-1.5">
          <Label class="text-[10px] font-bold uppercase text-slate-500 tracking-widest">Action Details</Label>
          <Input v-model="filters.details" placeholder="e.g. 'Password', 'AI Summary'..." class="bg-white border-slate-200 focus:ring-slate-900" />
        </div>
      </div>
    </Card>

    <Card class="overflow-hidden border-slate-200 shadow-sm bg-white">
      <Table>
        <TableHeader class="bg-slate-50/80">
          <TableRow>
            <TableHead class="px-6 font-bold uppercase text-[10px] text-slate-400">Actor</TableHead>
            <TableHead class="font-bold uppercase text-[10px] text-slate-400">Operation</TableHead>
            <TableHead class="font-bold uppercase text-[10px] text-slate-400">Event Description</TableHead>
            <TableHead class="text-right px-6 font-bold uppercase text-[10px] text-slate-400">Timeline</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody class="divide-y divide-slate-100">
          <TableRow v-for="log in filteredLogs" :key="log.id" class="hover:bg-slate-50/30 transition-colors">
            <TableCell class="px-6 py-4">
              <div v-if="log.user" class="flex items-center gap-2">
                <Avatar class="h-7 w-7 border shadow-sm">
                  <AvatarImage :src="`http://localhost:8080/academics${log.user.profilePicture}`" />
                  <AvatarFallback class="text-[9px] font-bold bg-slate-100">{{ log.user.name[0] }}</AvatarFallback>
                </Avatar>
                <div class="flex flex-col">
                  <span class="font-bold text-xs text-slate-700 leading-tight">{{ log.user.name }}</span>
                  <span class="text-[10px] text-slate-400 leading-tight">{{ log.user.role }}</span>
                </div>
              </div>
              <div v-else class="flex items-center gap-2 opacity-50">
                <div class="h-7 w-7 rounded-full bg-slate-200 flex items-center justify-center text-[10px]">🤖</div>
                <span class="text-xs italic text-slate-500">System Internal</span>
              </div>
            </TableCell>

            <TableCell>
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
            </TableCell>

            <TableCell>
              <div class="flex flex-col max-w-md">
                <span class="text-sm text-slate-600 font-medium">{{ log.details }}</span>
                <span v-if="log.publication" class="text-[10px] text-blue-500 font-bold uppercase mt-1 tracking-tighter">
                   → {{ log.publication.title }}
                </span>
              </div>
            </TableCell>

            <TableCell class="text-right px-6">
              <div class="flex flex-col items-end">
                <span class="text-slate-900 text-xs font-bold">{{ new Date(log.timestamp).toLocaleDateString() }}</span>
                <span class="text-slate-400 text-[10px] font-medium">{{ new Date(log.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' }) }}</span>
              </div>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>

      <div v-if="filteredLogs.length === 0" class="p-24 text-center">
        <div class="text-5xl mb-4 grayscale opacity-20">🔎</div>
        <h3 class="font-bold text-slate-900">No activity records found</h3>
        <p class="text-sm text-muted-foreground mt-1">Try adjusting your filters to find specific events.</p>
      </div>
    </Card>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user-store'
import { useRoute } from 'vue-router'

// Middleware de segurança para apenas ADMINS
definePageMeta({ middleware: 'auth-admin' })

const userStore = useUserStore()
const route = useRoute()

// Filtros baseados nos teus requisitos e no JSON recebido
const filters = reactive({
  name: route.query.name || '',
  action: '',
  details: ''
})

onMounted(() => {
  userStore.fetchAllActivityLogs()
})

const filteredLogs = computed(() => {
  return userStore.allActivityLogs.filter(log => {
    // Handling logs de sistema (como TAG CREATED) onde o user pode ser null
    const actorName = log.user?.name.toLowerCase() || 'system'
    const matchesName = actorName.includes(filters.name.toLowerCase())
    const matchesAction = !filters.action || log.action === filters.action
    const matchesDetails = log.details.toLowerCase().includes(filters.details.toLowerCase())

    return matchesName && matchesAction && matchesDetails
  })
})
</script>