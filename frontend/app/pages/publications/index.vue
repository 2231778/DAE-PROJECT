<template>
  <div class="min-h-screen bg-slate-50/50 pb-20">
    <div class="bg-white border-b mb-8">
      <div class="container mx-auto py-12 px-6">
        <h1 class="text-4xl font-bold tracking-tight text-slate-900 mb-2">Scientific Repository</h1>
        <p class="text-muted-foreground italic">Exploring the frontiers of Data and Materials Science.</p>
      </div>
    </div>

    <main class="container mx-auto px-6">
      <div class="flex flex-col md:flex-row gap-6 mb-8 items-center justify-between">
        <div class="relative w-full max-w-sm">
          <Input v-model="searchQuery" placeholder="Filter by tag (e.g. AI, Polymer)..." class="pl-10 shadow-sm bg-white" />
          <span class="absolute left-3 top-2.5 text-slate-400 text-sm">🔍</span>
        </div>

        <div class="flex gap-2 text-sm text-muted-foreground">
          Showing <strong>{{ filteredPublications.length }}</strong> publications
        </div>
      </div>

      <Card class="overflow-hidden border-slate-200">
        <Table>
          <TableHeader class="bg-slate-50/50">
            <TableRow>
              <TableHead class="font-bold text-slate-900">Publication Title</TableHead>
              <TableHead class="font-bold text-slate-900">Research Area</TableHead>
              <TableHead class="font-bold text-slate-900">Keywords/Tags</TableHead>
              <TableHead class="text-right"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="pub in filteredPublications" :key="pub.id" class="hover:bg-slate-50/80 transition-colors">
              <TableCell class="font-medium text-slate-800">{{ pub.title }}</TableCell>
              <TableCell>
                <Badge variant="outline" class="bg-white font-medium">
                  {{ pub.researchArea }}
                </Badge>
              </TableCell>
              <TableCell>
                <div class="flex flex-wrap gap-1">
                  <Badge v-for="tag in pub.tags" :key="tag" variant="secondary" class="text-[10px] uppercase tracking-wider">
                    {{ tag }}
                  </Badge>
                </div>
              </TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm" as-child>
                  <NuxtLink :to="`/publications/${pub.id}`">View Details →</NuxtLink>
                </Button>
              </TableCell>
            </TableRow>

            <TableRow v-if="filteredPublications.length === 0">
              <TableCell colspan="4" class="text-center py-20 text-muted-foreground">
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

const searchQuery = ref('')

// Mock Data (Substitute with useFetch later to connect to your Java Backend)
const publications = ref([
  { id: 1, title: 'Deep Learning in Quantum Chemistry', researchArea: 'Data Science', tags: ['AI', 'Quantum', 'Models'] },
  { id: 2, title: 'Advancements in Biodegradable Polymers', researchArea: 'Materials Science', tags: ['Polymer', 'Eco', 'Nano'] },
  { id: 3, title: 'Neural Networks for Material Analysis', researchArea: 'Data Science', tags: ['AI', 'Neural', 'Datasets'] },
])

const filteredPublications = computed(() => {
  if (!searchQuery.value) return publications.value
  const query = searchQuery.value.toLowerCase()
  return publications.value.filter(p =>
      p.tags.some(tag => tag.toLowerCase().includes(query)) ||
      p.title.toLowerCase().includes(query)
  )
})
</script>