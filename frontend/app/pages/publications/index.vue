<template>
  <div class="min-h-screen bg-slate-50/50 pb-20">
    <div class="bg-white border-b mb-8">
      <div class="container mx-auto py-12 px-6">
        <h1 class="text-4xl font-bold tracking-tight text-slate-900 mb-2">Scientific Repository</h1>
        <p class="text-muted-foreground italic">Exploring the frontiers of Data and Materials Science.</p>
      </div>
    </div>

    <main class="container mx-auto px-6">
      <div class="flex flex-col md:flex-row gap-6 mb-8 items-center">
        <!-- Search (left) -->
        <div class="relative w-full max-w-sm">
          <Input
              v-model="searchQuery"
              placeholder="Filter by tag (e.g. AI, Polymer)..."
              class="pl-10 shadow-sm bg-white"
          />
          <span class="absolute left-3 top-2.5 text-slate-400 text-sm">🔍</span>
        </div>

        <!-- Push everything after this to the right -->
        <div class="ml-auto flex items-center gap-4">
          <Button>
            <NuxtLink :to="`/publications/new`">New</NuxtLink>
          </Button>

          <div class="flex gap-2 text-sm text-muted-foreground">
            Showing <strong>{{ filteredPublications.length }}</strong> publications
          </div>
        </div>
      </div>




      <Card class="overflow-hidden border-slate-200">
        <Table>
          <TableHeader class="bg-slate-50/50">
            <TableRow>
              <TableHead class="font-bold text-slate-900">Publication Title</TableHead>
              <TableHead class="font-bold text-slate-900">Author</TableHead>
              <TableHead class="font-bold text-slate-900">Keywords/Tags</TableHead>
              <TableHead
                  class="font-bold text-slate-900 cursor-pointer select-none"
                  @click="toggleSort('updatedDate')"
              >
                Last Modified
                <span class="ml-1 text-xs">
    {{ sortDirection === 'asc' ? '▲' : '▼' }}
  </span>
              </TableHead>
              <TableHead
                  class="font-bold text-slate-900 cursor-pointer select-none"
                  @click="toggleSort('numberOfComments')"
              >
                nº Comments
                <span class="ml-1 text-xs">
    {{ sortDirection === 'asc' ? '▲' : '▼' }}
  </span>
              </TableHead>
              <TableHead
                  class="font-bold text-slate-900 cursor-pointer select-none"
                  @click="toggleSort('rating')"
              >
                Average Rating
                <span class="ml-1 text-xs">
    {{ sortDirection === 'asc' ? '▲' : '▼' }}
  </span>
              </TableHead>
              <TableHead
                  class="font-bold text-slate-900 cursor-pointer select-none"
                  @click="toggleSort('numberOfReviews')"
              >
                nº Reviews
                <span class="ml-1 text-xs">
    {{ sortDirection === 'asc' ? '▲' : '▼' }}
  </span>
              </TableHead>



              <TableHead class="text-right"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <!-- Loading  -->
            <TableRow v-if="isLoading">
              <TableCell colspan="5" class="py-20 text-center">
                <div class="flex flex-col items-center gap-3 text-slate-500">
                  <svg
                      class="animate-spin h-6 w-6"
                      xmlns="http://www.w3.org/2000/svg"
                      fill="none"
                      viewBox="0 0 24 24"
                  >
                    <circle
                        class="opacity-25"
                        cx="12"
                        cy="12"
                        r="10"
                        stroke="currentColor"
                        stroke-width="4"
                    />
                    <path
                        class="opacity-75"
                        fill="currentColor"
                        d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
                    />
                  </svg>
                  <span class="text-sm">Loading publications…</span>
                </div>
              </TableCell>
            </TableRow>

            <!-- When there is Data -->
            <TableRow
                v-else
                v-for="pub in filteredPublications"
                :key="pub.id"
                :class="[
                'hover:opacity-90 transition-colors',
                pub.visibility === 'INVISIBLE' ? 'bg-yellow-100' : ''
              ]"
            >
              <TableCell class="font-medium text-slate-800">{{ pub.title }}</TableCell>
              <TableCell class="font-medium text-slate-800">{{ pub.author }}</TableCell>
              <TableCell>
                <div class="flex flex-wrap gap-1">
                  <Badge
                      v-for="tag in pub.tags"
                      :key="tag"
                      variant="secondary"
                      class="text-[10px] uppercase tracking-wider"
                  >
                    {{ tag.name }}
                  </Badge>
                </div>
              </TableCell>
              <TableCell class="font-medium text-slate-800">
                {{ new Date(pub.updatedDate).toLocaleString('pt-PT') }}
              </TableCell>
              <TableCell class="text-right">{{ pub.numberOfComments }}</TableCell>
              <TableCell class="text-right">{{ Number(pub.rating).toFixed(2) }}</TableCell>
              <TableCell class="text-right">{{ pub.numberOfReviews }}</TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm" as-child>
                  <NuxtLink :to="`/publications/${pub.id}`">View Details →</NuxtLink>
                </Button>
              </TableCell>
            </TableRow>


            <!-- No data -->
            <TableRow v-if="!isLoading && filteredPublications.length === 0">
              <TableCell colspan="5" class="text-center py-20 text-muted-foreground">
                No publications found matching your search.
              </TableCell>
            </TableRow>
          </TableBody>

        </Table>
      </Card>
    </main>
  </div>
</template>

<script setup>
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { onMounted } from 'vue';
import {useAuthStore} from "../../stores/auth-store.js";


const config = useRuntimeConfig()
const authStore = useAuthStore()


const publications = ref([])
const sortDirection = ref('desc') // default: new publications first
const isLoading = ref(true)


const toggleSort = (column) => {
  if (sortBy.value === column) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = column
    sortDirection.value = 'asc' // default
  }
}



onMounted(async () => {
  isLoading.value = true

  try {
    publications.value = await $fetch(
        `${config.public.apiBase}/publications`,
        { method: 'GET',headers: { 'Authorization': `Bearer ${authStore.token}` } }
    )
  } catch (e) {
    console.error('Failed to fetch publications:', e)
  }  finally {
  isLoading.value = false
}
})


const searchQuery = ref('')

const sortBy = ref('updatedDate') // default sort column

const filteredPublications = computed(() => {
  const isPrivileged = ['RESPONSAVEL', 'ADMIN'].includes(authStore.user?.role)
  let result = publications.value

  // --- Filter by search ---
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(pub => {
      const inTitle = pub.title.toLowerCase().includes(query)
      const inAuthor = pub.author.toLowerCase().includes(query)
      const inTags = pub.tags?.some(tag => tag.name.toLowerCase().includes(query))
      return inTitle || inAuthor || inTags
    })
  }

  // --- Filter by visibility ---
  result = result.filter(pub => {
    if (pub.visibility === 'VISIBLE') return true
    if (pub.visibility === 'INVISIBLE' && isPrivileged) return true
    return false
  })

  // --- Sort dynamically ---
  return [...result].sort((a, b) => {
    let valA, valB

    switch (sortBy.value) {
      case 'updatedDate':
      case 'creationDate':
        valA = new Date(a[sortBy.value])
        valB = new Date(b[sortBy.value])
        break
      case 'numberOfComments':
      case 'numberOfReviews':
        valA = a[sortBy.value] || 0
        valB = b[sortBy.value] || 0
        break
      case 'rating':
        valA = a[sortBy.value] || 0
        valB = b[sortBy.value] || 0
        break
      default:
        valA = String(a[sortBy.value] || '').toLowerCase()
        valB = String(b[sortBy.value] || '').toLowerCase()
    }

    if (valA < valB) return sortDirection.value === 'asc' ? -1 : 1
    if (valA > valB) return sortDirection.value === 'asc' ? 1 : -1
    return 0
  })
})


</script>