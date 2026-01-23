<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { useAuthStore } from '@/stores/auth-store.js'

// Shadcn UI
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { FormField, FormItem, FormLabel, FormControl, FormMessage } from '@/components/ui/form'

const route = useRoute()
const router = useRouter()
const config = useRuntimeConfig()
const authStore = useAuthStore()
const selectedFile = ref<File | null>(null)
const loading = ref(true)
const error = ref('')

// ------------------
// 1. Validation Schema
// ------------------
const formSchema = toTypedSchema(z.object({
  title: z.string().min(1, 'Title is required'),
  description: z.string().min(1, 'Description is required'),
  author: z.string().min(1, 'Author is required'),
  publisherId: z.number()
}))

// ------------------
// 2. Form Setup
// ------------------

const initialValues = ref({
  title: '',
  description: '',
  author: '',
  publisherId: authStore.user?.id ?? 0
})

const { handleSubmit, setValues, isSubmitting: formSubmitting } = useForm({
  validationSchema: formSchema,
  initialValues: initialValues.value
})

// ------------------
// 3. Load existing publication
// ------------------
onMounted(async () => {
  try {
    const pub = await $fetch(`${config.public.apiBase}/publications/${route.params.id}`, {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    })

    setValues({
      title: pub.title ?? '',
      description: pub.description ?? '',
      author: pub.author ?? '',
      publisherId: pub.publisher?.id ?? authStore.user?.id ?? 0
    })

  } catch (e) {
    console.error(e)
    error.value = 'Failed to load publication'
  } finally {
    loading.value = false
  }
})


// ------------------
// 4. File input handler
// ------------------
const onFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files?.[0]) selectedFile.value = target.files[0]
}

// ------------------
// 5. Submit handler
// ------------------
const onSubmit = handleSubmit(async (values) => {
  try {
    const formData = new FormData()
    formData.append('title', values.title)
    formData.append('description', values.description)
    formData.append('author', values.author)
    formData.append('publisherId', values.publisherId.toString())

    if (selectedFile.value) {
      formData.append('file', selectedFile.value)
    }

    await $fetch(`${config.public.apiBase}/publications/${route.params.id}`, {
      method: 'PATCH', // update
      headers: {
        Authorization: `Bearer ${authStore.token}`
      },
      body: formData
    })

    alert('Publication updated!')
    router.push('/publications') // navigate back to list
  } catch (err) {
    console.error('Error updating publication:', err)
    alert('Failed to update publication')
  }
})
</script>

<template>
  <div class="container mx-auto py-10 max-w-2xl">

    <!-- Loading -->
    <div v-if="loading" class="text-center text-slate-500">Loading publication…</div>

    <!-- Error -->
    <div v-else-if="error" class="text-center text-red-500">{{ error }}</div>

    <!-- Form -->
    <div v-else>
      <Card>
        <CardHeader>
          <CardTitle>Update Publication</CardTitle>
          <CardDescription>Edit the details and submit to update.</CardDescription>
        </CardHeader>
        <CardContent>
          <form @submit.prevent="onSubmit" class="space-y-6">

            <!-- Title -->
            <FormField v-slot="{ componentField }" name="title">
              <FormItem>
                <FormLabel>Title</FormLabel>
                <FormControl>
                  <Textarea v-bind="componentField" placeholder="" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>


            <!-- Author -->
            <FormField v-slot="{ componentField }" name="author">
              <FormItem>
                <FormLabel>Author</FormLabel>
                <FormControl>
                  <Textarea v-bind="componentField" placeholder="John Doe" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <!-- Description -->
            <FormField v-slot="{ componentField }" name="description">
              <FormItem>
                <FormLabel>Description</FormLabel>
                <FormControl>
                  <Textarea v-bind="componentField" />
                </FormControl>
                <FormMessage />
              </FormItem>
            </FormField>

            <!-- File -->
            <div class="space-y-2">
              <Label>File Attachment</Label>
              <Input type="file" @change="onFileChange" />
            </div>

            <!-- Submit -->
            <div class="flex justify-end">
              <Button type="submit" :disabled="formSubmitting" class="bg-black text-white">
                {{ formSubmitting ? 'Updating...' : 'Update' }}
              </Button>
            </div>

          </form>
        </CardContent>
      </Card>
    </div>

  </div>
</template>
