import { defineStore } from 'pinia'
import { useAuthStore } from './auth-store'
import { ref } from 'vue'

export const useUserStore = defineStore('userStore', () => {
    const auth = useAuthStore()
    const isLoading = ref(false)
    const users = ref([])
    const allActivityLogs = ref([]) // Novo: Para a página de auditoria global

    // URLs Base
    const API_BASE = 'http://localhost:8080/academics/api/users'
    const LOGS_BASE = 'http://localhost:8080/academics/api/activity-log'

    const getHeaders = () => ({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    })

    // --- UTILIZADORES ---

    // GET: Listar todos (Admin)
    async function fetchUsers() {
        isLoading.value = true
        try {
            const data = await $fetch(API_BASE, { headers: getHeaders() })
            users.value = data
        } catch (error) {
            console.error('Erro ao buscar utilizadores:', error)
        } finally {
            isLoading.value = false
        }
    }

    // POST: Criar Utilizador
    async function createUser(payload) {
        try {
            await $fetch(API_BASE, {
                method: 'POST',
                headers: getHeaders(),
                body: payload
            })
            await fetchUsers()
        } catch (error) {
            console.error('Erro ao criar utilizador:', error)
            throw error
        }
    }

    // PATCH: Atualizar Nome, Email ou Role
    async function updateUser(id, payload) {
        try {
            await $fetch(`${API_BASE}/${id}`, {
                method: 'PATCH',
                headers: getHeaders(),
                body: payload
            })
            await fetchUsers()
        } catch (error) {
            console.error('Erro ao atualizar utilizador:', error)
            throw error
        }
    }

    // PATCH: Alternar Status (Ativo <-> Inativo)
    async function toggleUserStatus(id) {
        try {
            await $fetch(`${API_BASE}/${id}/status`, {
                method: 'PATCH',
                headers: getHeaders()
            })

            const user = users.value.find(u => u.id === id)
            if (user) {
                user.status = user.status === 'Active' ? 'Inactive' : 'Active'
            }
        } catch (error) {
            console.error('Erro ao mudar estado:', error)
        }
    }

    // DELETE: Remover Utilizador
    async function deleteUser(id) {
        if (!confirm('Tem a certeza que deseja remover este utilizador?')) return
        try {
            await $fetch(`${API_BASE}/${id}`, {
                method: 'DELETE',
                headers: getHeaders()
            })
            users.value = users.value.filter(u => u.id !== id)
        } catch (error) {
            console.error('Erro ao remover utilizador:', error)
        }
    }

    // --- LOGS DE ATIVIDADE ---

    // GET: Listar TODO o histórico do sistema (Auditoria Global)
    async function fetchAllActivityLogs() {
        isLoading.value = true
        try {
            const data = await $fetch(LOGS_BASE, {
                headers: { 'Authorization': `Bearer ${auth.token}` }
            })
            allActivityLogs.value = data
        } catch (error) {
            console.error('Erro ao buscar logs globais:', error)
        } finally {
            isLoading.value = false
        }
    }

    return {
        users,
        allActivityLogs,
        isLoading,
        fetchUsers,
        createUser,
        updateUser,
        toggleUserStatus,
        deleteUser,
        fetchAllActivityLogs
    }
})