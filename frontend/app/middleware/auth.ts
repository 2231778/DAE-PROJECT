export default defineNuxtRouteMiddleware((to, from) => {
    const authStore = useAuthStore()

    if (!authStore.token) {
        return navigateTo('/auth/login')
    }

    const userStatus = (authStore.user as any)?.status

    if (userStatus === 'Inactive') {
        authStore.logout()
        return navigateTo('/auth/login')
    }
})