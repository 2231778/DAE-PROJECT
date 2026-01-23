import { defineStore } from 'pinia'
import { useAuthStore } from './auth-store'

export const useTagStore = defineStore('tagStore', () => {
    const auth = useAuthStore()
    // const config = useRuntimeConfig() // Descomente se usar config
    const API_URL = 'http://localhost:8080/academics/api/tags'

    const tags = ref([])
    const isLoading = ref(false)

    // Helper para os headers com Token
    const getHeaders = () => ({
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${auth.token}`
    })

    async function fetchTags() {
        isLoading.value = true
        try {
            const data = await $fetch(API_URL, {
                headers: getHeaders()
            })
            tags.value = data
        } catch (error) {
            console.error('Erro ao buscar tags:', error)
        } finally {
            isLoading.value = false
        }
    }

    async function createTag(payload) {
        try {
            await $fetch(API_URL, {
                method: 'POST',
                headers: getHeaders(),
                body: payload
            })
            await fetchTags() // Atualiza a lista
        } catch (error) {
            // CORREÇÃO: Log do erro detalhado do backend
            console.error('Erro ao criar tag:', error.data || error)
            throw error // Lança erro para o componente tratar
        }
    }

    async function updateTag(id, payload) {
        try {
            await $fetch(`${API_URL}/${id}`, {
                method: 'PATCH', // ou PUT
                headers: getHeaders(),
                body: payload
            })
            await fetchTags()
        } catch (error) {
            console.error('Erro ao atualizar tag:', error.data || error)
            throw error
        }
    }

    async function toggleVisibility(id) {
        try {
            await $fetch(`${API_URL}/${id}/visibility`, {
                method: 'PATCH',
                headers: getHeaders()
            })

            const tag = tags.value.find(t => t.id === id)
            if (tag) {
                tag.visibility = tag.visibility === 'VISIBLE' ? 'INVISIBLE' : 'VISIBLE'
            }
        } catch (error) {
            console.error('Erro ao mudar visibilidade:', error.data || error)
            fetchTags() // Recarrega em caso de erro para garantir sincronia
        }
    }

    async function deleteTag(id) {
        if (!confirm('Tem a certeza?')) return
        try {
            await $fetch(`${API_URL}/${id}`, {
                method: 'DELETE',
                headers: getHeaders()
            })
            tags.value = tags.value.filter(t => t.id !== id)
        } catch (error) {
            console.error('Erro ao apagar:', error.data || error)
            alert('Não foi possível apagar a tag.')
        }
    }

    return { tags, isLoading, fetchTags, createTag, updateTag, toggleVisibility, deleteTag }
})