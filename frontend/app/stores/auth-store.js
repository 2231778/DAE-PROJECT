import { defineStore } from 'pinia'

export const useAuthStore = defineStore('authStore', () => {
    const token = useCookie('auth_token', { maxAge: 60 * 60 * 24 * 7 })
    const user = useCookie('auth_user')

    const isAuthenticated = computed(() => !!token.value)

    function login(newToken, userData) {
        token.value = newToken
        user.value = userData
    }

    function logout() {
        token.value = null
        user.value = null
    }

    return { token, user, isAuthenticated, login, logout }
})