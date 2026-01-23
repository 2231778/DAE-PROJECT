import { defineStore } from 'pinia'
import { useAuthStore } from './auth-store'
import { ref } from 'vue'

export const useUserStore = defineStore('userStore', () => {
    const auth = useAuthStore()
    const isLoading = ref(false)
    const users = ref([])

    // URL Base
    const API_BASE = 'http://localhost:8080/academics/api/users'

    const getHeaders = () => ({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    })

    // GET: Listar todos
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

    // PATCH: Atualizar Dados Gerais (Nome, Email, Role)
    // Rota: http://localhost:8080/academics/api/users/{id}
    async function updateUser(id, payload) {
        try {
            await $fetch(`${API_BASE}/${id}`, {
                method: 'PATCH', // Corrigido para PATCH
                headers: getHeaders(),
                body: payload
            })
            await fetchUsers()
        } catch (error) {
            console.error('Erro ao atualizar utilizador:', error)
            throw error
        }
    }

    // PATCH: Mudar Apenas o Status (Ativo/Inativo)
    // Rota: http://localhost:8080/academics/api/users/{id}/status
    async function changeUserStatus(id, newStatus) {
        try {
            // Esta rota é específica para status, conforme o seu Bruno
            await $fetch(`${API_BASE}/${id}/status`, {
                method: 'PATCH',
                headers: getHeaders(),
                body: { status: newStatus }
            })

            // Atualizamos a lista localmente para ser mais rápido
            const user = users.value.find(u => u.id === id)
            if (user) user.status = newStatus

        } catch (error) {
            console.error('Erro ao mudar estado:', error)
            throw error
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

    return {
        users, isLoading, fetchUsers, createUser, updateUser, changeUserStatus, deleteUser
    }
})