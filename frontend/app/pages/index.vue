<template>
  <div class="min-h-screen bg-background font-sans selection:bg-primary/10">
    <header class="relative overflow-hidden bg-slate-950 py-24 sm:py-32">
      <div class="absolute -top-24 -left-24 h-96 w-96 rounded-full bg-primary/10 blur-3xl" />

      <div class="container relative z-10 mx-auto px-6 text-center">
        <Badge variant="outline" class="mb-6 border-primary/30 text-primary-foreground/80 px-4 py-1">
          New: AI-Powered Research Summaries ✨
        </Badge>

        <h1 class="text-5xl md:text-7xl font-extrabold tracking-tight text-white mb-6">
          XYZ Research <span class="text-primary">Center</span>
        </h1>

        <p class="text-xl text-slate-400 mb-10 max-w-2xl mx-auto leading-relaxed">
          The unified ecosystem for scientific publication management, peer-driven collaboration, and cutting-edge research enrichment.
        </p>

        <div class="flex flex-col sm:flex-row justify-center gap-4">
          <Button as-child size="lg" class="rounded-full px-8 shadow-lg shadow-primary/20 transition-all hover:scale-105">
            <NuxtLink to="/publications">Explore Publications</NuxtLink>
          </Button>

          <Button v-if="!authStore.token" variant="outline" as-child size="lg" class="rounded-full px-8 text-black border-slate-700 hover:scale-105 transition-all">
            <NuxtLink to="/auth/login">Researcher Login</NuxtLink>
          </Button>
        </div>
      </div>
    </header>

    <section class="container mx-auto py-24 px-6">
      <div class="flex flex-col items-center mb-16">
        <h2 class="text-3xl font-bold tracking-tight sm:text-4xl mb-4">Scientific Domains</h2>
        <div class="h-1.5 w-20 bg-primary rounded-full" />
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-5xl mx-auto">
        <Card class="group relative overflow-hidden border-slate-200 transition-all hover:border-primary/50 hover:shadow-xl">
          <CardHeader>
            <div class="mb-4 inline-flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10 text-primary group-hover:bg-primary group-hover:text-white transition-colors">
              <span class="text-2xl font-bold">DS</span>
            </div>
            <CardTitle class="text-2xl font-bold">Data Science</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-muted-foreground leading-relaxed">
              Advancing Machine Learning, Big Data analytics, and neural architectures to solve complex global challenges.
            </p>
          </CardContent>
        </Card>

        <Card class="group relative overflow-hidden border-slate-200 transition-all hover:border-primary/50 hover:shadow-xl">
          <CardHeader>
            <div class="mb-4 inline-flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10 text-primary group-hover:bg-primary group-hover:text-white transition-colors">
              <span class="text-2xl font-bold">MS</span>
            </div>
            <CardTitle class="text-2xl font-bold">Materials Science</CardTitle>
          </CardHeader>
          <CardContent>
            <p class="text-muted-foreground leading-relaxed">
              Innovating in nanotechnology and sustainable polymer engineering to build the infrastructure of tomorrow.
            </p>
          </CardContent>
        </Card>
      </div>
    </section>

    <section class="bg-slate-50 py-24 px-6">
      <div class="container mx-auto max-w-4xl">
        <div class="text-center mb-16">
          <h2 class="text-3xl font-bold mb-4">Core Capabilities</h2>
          <p class="text-muted-foreground">Empowering researchers with advanced digital tools.</p>
        </div>

        <div class="grid gap-12 sm:grid-cols-3 text-center">
          <div class="space-y-4">
            <div class="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-white shadow-sm ring-1 ring-slate-200">
              <div class="h-3 w-3 rounded-full bg-primary animate-pulse" />
            </div>
            <h3 class="font-bold text-lg">Enrichment</h3>
            <p class="text-sm text-muted-foreground leading-relaxed">Continuous peer-driven reviews and critical analysis.</p>
          </div>

          <div class="space-y-4">
            <div class="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-white shadow-sm ring-1 ring-slate-200">
              <div class="h-3 w-3 rounded-full bg-primary" />
            </div>
            <h3 class="font-bold text-lg">AI Summaries</h3>
            <p class="text-sm text-muted-foreground leading-relaxed">Leveraging LLMs for instant abstract generation.</p>
          </div>

          <div class="space-y-4">
            <div class="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-white shadow-sm ring-1 ring-slate-200">
              <div class="h-3 w-3 rounded-full bg-primary" />
            </div>
            <h3 class="font-bold text-lg">Alerts</h3>
            <p class="text-sm text-muted-foreground leading-relaxed">Stay updated with tag-based subscription systems.</p>
          </div>
        </div>
      </div>
    </section>

    <footer class="border-t py-12 px-6 text-center text-muted-foreground text-sm">
      <p>&copy; 2026 XYZ Research Center. Scientific Integrity in the Digital Age.</p>
    </footer>
  </div>
</template>

<script setup>
import { Button } from '@/components/ui/button'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useAuthStore } from '@/stores/auth-store'

const authStore = useAuthStore()
</script>