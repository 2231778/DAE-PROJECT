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

            <NuxtLink v-if="canManageTags" to="/tags" class="hover:text-primary transition-colors">Tags</NuxtLink>

            <template v-if="isAdmin">
              <NuxtLink to="/admin/users" class="hover:text-primary transition-colors">Users</NuxtLink>
              <NuxtLink to="/admin/logs" class="hover:text-primary transition-colors">Activity Logs</NuxtLink>
            </template>
          </div>
        </div>

        <div class="flex items-center gap-4">
          <template v-if="authStore.token && authStore.user">
            <Badge
                variant="outline"
                :class="['hidden sm:flex items-center gap-1.5 px-3 py-1 rounded-full font-medium text-[11px] uppercase tracking-wider shadow-sm', roleBadgeConfig.class]"
            >
              <span>{{ roleBadgeConfig.emoji }}</span>
              {{ roleBadgeConfig.label }}
            </Badge>

            <DropdownMenu>
              <DropdownMenuTrigger as-child>
                <Avatar class="h-9 w-9 cursor-pointer border hover:ring-2 hover:ring-slate-200 transition-all">
                  <AvatarImage
                      :src="`http://localhost:8080/academics${authStore.user.profilePicture}`"
                      :alt="authStore.user.name"
                  />
                  <AvatarFallback class="bg-slate-100 text-slate-900 font-bold text-xs">
                    {{ getInitials(authStore.user.name) }}
                  </AvatarFallback>
                </Avatar>
              </DropdownMenuTrigger>

              <DropdownMenuContent class="w-56" align="end">
                <DropdownMenuLabel>
                  <div class="flex flex-col space-y-1">
                    <p class="text-sm font-medium leading-none">{{ authStore.user.name }}</p>
                    <p class="text-xs leading-none text-muted-foreground">{{ authStore.user.email }}</p>
                  </div>
                </DropdownMenuLabel>
                <DropdownMenuSeparator />

                <DropdownMenuItem as-child>
                  <NuxtLink to="/profile" class="w-full cursor-pointer">👤 My Profile</NuxtLink>
                </DropdownMenuItem>

                <DropdownMenuItem as-child>
                  <NuxtLink to="/publications/mine" class="w-full cursor-pointer">📚 My Publications</NuxtLink>
                </DropdownMenuItem>

                <DropdownMenuSeparator />

                <DropdownMenuItem @click="handleLogout" class="text-red-600 cursor-pointer focus:bg-red-50 focus:text-red-600">
                  Logout
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </template>

          <template v-else>
            <Button variant="ghost" as-child size="sm">
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
import { computed } from 'vue'
import { Button } from '~/components/ui/button'
import { Badge } from '~/components/ui/badge'
import { useAuthStore } from '~/stores/auth-store'
import { Avatar, AvatarImage, AvatarFallback } from '~/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '~/components/ui/dropdown-menu'

const authStore = useAuthStore()

// 1. Lógica de Administrador (Para Gestão de Users e Logs)
const isAdmin = computed(() => {
  return authStore.user?.role?.toUpperCase() === 'ADMIN'
})

// 2. Lógica de Tags (ADMIN ou RESPONSAVEL)
const canManageTags = computed(() => {
  const role = authStore.user?.role?.toUpperCase() || ''
  return role === 'ADMIN' || role === 'RESPONSAVEL'
})

// 3. Configuração dos Badges
const roleBadgeConfig = computed(() => {
  const role = authStore.user?.role?.toUpperCase() || ''
  switch (role) {
    case 'ADMIN':
      return { label: 'Administrator', class: 'bg-red-50 text-red-700 border-red-100', emoji: '🛡️' }
    case 'RESPONSAVEL':
      return { label: 'Lead Researcher', class: 'bg-amber-50 text-amber-700 border-amber-100', emoji: '🔬' }
    default:
      return { label: 'Collaborator', class: 'bg-slate-50 text-slate-600 border-slate-100', emoji: '🎓' }
  }
})

function getInitials(name) {
  if (!name) return 'U'
  return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2)
}

function handleLogout() {
  authStore.logout()
  navigateTo('/')
}
</script>

<style scoped>
@reference "@/assets/css/tailwind.css";

.router-link-active:not([href="/"]) {
  @apply text-primary font-bold border-b-2 border-primary pb-1 transition-all;
}
</style>