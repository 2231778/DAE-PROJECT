import { defineStore } from 'pinia'
import { useAuthStore } from './auth-store'

export const usePublicationStore = defineStore('publicationStore', {
    state: () => ({
        myPublications: [],
        publicationLogs: [],
        isLoading: false
    }),
    actions: {
        async fetchMyPublications() {
            const auth = useAuthStore()
            this.isLoading = true
            try {
                const data = await $fetch('http://localhost:8080/academics/api/publications/my-publications', {
                    headers: {
                        Authorization: `Bearer ${auth.token}`
                    }
                })
                this.myPublications = data
            } catch (error) {
                console.error('Error fetching personal library:', error)
            } finally {
                this.isLoading = false
            }
        },
        async fetchPublicationLogs(id) {
            const auth = useAuthStore()
            this.isLoading = true
            try {
                // This will work once you create the GET /api/activity-log/publication/{id}
                const data = await $fetch(`http://localhost:8080/academics/api/activity-log/publication/${id}`, {
                    headers: { Authorization: `Bearer ${auth.token}` }
                })
                this.publicationLogs = data
            } catch (error) {
                console.error('Error fetching publication history:', error)
                this.publicationLogs = []
            } finally {
                this.isLoading = false
            }
        }
    }
})