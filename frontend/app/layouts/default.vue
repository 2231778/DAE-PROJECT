<template>
  <div class="min-h-screen bg-background font-sans">
    <nav class="sticky top-0 z-50 w-full border-b bg-white/95 backdrop-blur supports-[backdrop-filter]:bg-white/60">
      <div class="container mx-auto flex h-16 items-center justify-between px-6">

        <div class="flex items-center gap-8">
          <NuxtLink
              :to="authStore.token ? '/dashboard' : '/'"
              class="flex items-center gap-2 transition-opacity hover:opacity-90"
          >
            <div class="h-8 w-8 rounded bg-slate-900 flex items-center justify-center">
              <span class="text-white font-bold text-xs">XYZ</span>
            </div>
            <span class="text-xl font-bold tracking-tight text-slate-900">Research</span>
          </NuxtLink>

          <div v-if="authStore.token" class="hidden md:flex items-center gap-6 text-sm font-medium text-muted-foreground">
            <NuxtLink to="/publications" class="hover:text-primary transition-colors">Publications</NuxtLink>
          </div>
        </div>

        <div class="flex items-center gap-4">
          <template v-if="authStore.token">
            <div class="hidden sm:flex flex-col items-end mr-2">
              <span class="text-xs font-bold text-slate-900">{{ authStore.user?.name }}</span>
              <span class="text-[10px] uppercase text-slate-500 tracking-wider">{{ authStore.user?.role }}</span>
            </div>

            <Button variant="outline" size="sm" @click="handleLogout" class="gap-2 cursor-pointer relative z-20">
              Logout
            </Button>
          </template>

          <template v-else>
            <Button variant="ghost" as-child size="sm" class="hidden sm:flex">
              <NuxtLink to="/auth/login">Sign In</NuxtLink>
            </Button>
          </template>
        </div>
      </div>
    </nav>

    <main>
      <slot />
    </main>

    <footer class="border-t py-6 text-center text-xs text-muted-foreground bg-slate-50/50">
      <div class="container mx-auto px-6">
        &copy; 2026 XYZ Research Management System — DAE Project
      </div>
    </footer>
  </div>
</template>

<script setup>
import { Button } from '~/components/ui/button/index.js'
import { useAuthStore } from '~/stores/auth-store.js'

const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  navigateTo('/')
}
</script>

<style scoped>
/* Estilo para destacar o link ativo */
@reference "@/assets/css/tailwind.css";

.router-link-active:not([href="/"]) {
  @apply text-primary font-bold;
}
</style>