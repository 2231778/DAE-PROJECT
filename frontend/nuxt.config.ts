import tailwindcss from '@tailwindcss/vite'

export default defineNuxtConfig({
    compatibilityDate: '2026-01-22',

    css: ['~/assets/css/tailwind.css'],

    modules: [
        'shadcn-nuxt',
        '@pinia/nuxt'
    ],

    runtimeConfig: {
        public: {
            apiBase: 'http://localhost:8080/academics/api'
        }
    },

    vite: {
        plugins: [
            tailwindcss(),
        ],
    },

    shadcn: {
        prefix: '',
        componentDir: './app/components/ui'
    },

    future: {
        compatibilityVersion: 4,
    }
})