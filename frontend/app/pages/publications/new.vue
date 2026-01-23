<script setup lang="ts">
import { ref} from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import {useAuthStore} from "../../stores/auth-store.js"
import * as z from 'zod'


// Shadcn UI Component Imports
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Textarea } from '@/components/ui/textarea'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { FormControl, FormDescription, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'



// 1. Validation Schema
const formSchema = toTypedSchema(z.object({
  title: z.string().min(1, 'Title is required'),
  description: z.string().min(1, 'Description is too short'),
  author: z.string().min(1, 'Author is required'),
  publisherId: z.number(),
}))


const config = useRuntimeConfig()
const authStore = useAuthStore()
const selectedFile = ref<File | null>(null)


const { handleSubmit, isSubmitting: formSubmitting } = useForm<{
  title: string
  description: string
  author: string
  publisherId: number
}>({
  validationSchema: formSchema,
  initialValues: {
    title: '',
    description: '',
    author: '',
    publisherId: authStore.user?.id
  }
})


const onFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    selectedFile.value = target.files[0]
  }
}

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

    await $fetch(`${config.public.apiBase}/publications`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${authStore.token}`
      },
      body: formData
    })

    alert('Success!')
  } catch (err) {
    console.error('Error:', err)
  }
})
</script>

<template>
  <div class="container mx-auto py-10 max-w-2xl">
    <Card>
      <CardHeader>
        <CardTitle>New Publication</CardTitle>
        <CardDescription>Fill in the details to create your publication.</CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit="onSubmit" class="space-y-6">

          <FormField v-slot="{ componentField }" name="title">
            <FormItem>
              <FormLabel>Title</FormLabel>
              <FormControl>
                <Input placeholder="My Publication" v-bind="componentField" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="author">
            <FormItem>
              <FormLabel>Author</FormLabel>
              <FormControl>
                <Input placeholder="John Doe" v-bind="componentField" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ componentField }" name="description">
            <FormItem>
              <FormLabel>Description</FormLabel>
              <FormControl>
                <Textarea placeholder="Write a brief description..." v-bind="componentField" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <div class="space-y-2">
            <Label>File Attachment</Label>
            <Input type="file" @change="onFileChange" />
          </div>

          <div class="flex justify-end">
            <Button type="submit" :disabled="formSubmitting" class="bg-black text-white">
              {{ formSubmitting ? 'Publishing...' : 'Publish' }}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  </div>
</template>
