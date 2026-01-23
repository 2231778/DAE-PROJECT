export default defineNuxtRouteMiddleware((to, from) => {
    const authStore = useAuthStore()

    const status = (authStore.user as any)?.status
    const role = (authStore.user as any)?.role?.toUpperCase()

    // 1. Se não houver token ou se estiver inativo, barra logo
    if (!authStore.token || status !== 'Active') {
        return navigateTo('/auth/login')
    }

    if (role !== 'ADMIN') {
        return navigateTo('/dashboard')
    }
})