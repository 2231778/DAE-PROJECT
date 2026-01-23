<script setup>
import {ref, onMounted, computed } from 'vue';

const route = useRoute()
const config = useRuntimeConfig()
const authStore = useAuthStore()

const newComment = ref('')
const isSubmitting = ref(false)

const userRating = ref(0)
const hoverRating = ref(0)
const isRating = ref(false)

const downloadFile = async (id, fileName) => {
  try {
    // 1. Fetch the file as a Blob with the Authorization header
    const blob = await $fetch(`${config.public.apiBase}/publications/${id}/download`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${authStore.token}`
      },
      responseType: 'blob' // Crucial: tells Nuxt to treat the response as binary data
    })

    // 2. Create a temporary URL for the Blob
    const url = window.URL.createObjectURL(new Blob([blob]))

    // 3. Create a hidden link and click it to trigger the download
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fileName || `publication-${id}.pdf`) // Set the filename
    document.body.appendChild(link)
    link.click()

    // 4. Cleanup
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

  } catch (err) {
    console.error('Download failed:', err)
    alert('Failed to download file.')
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) return

  isSubmitting.value = true

  try {
    const createdComment = await $fetch(
        `${config.public.apiBase}/publications/${route.params.id}/comments`,
        {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${authStore.token}`,
            'Content-Type': 'application/json'
          },
          body: {
            content: newComment.value
          }
        }
    )

    // Make sure comments array exists
    publication.value.comments ??= []

    // Add comment to top
    publication.value.comments.unshift(createdComment)
    // Update the num of comments
    publication.value.numberOfComments = publication.value.numberOfComments + 1

    // Reset input
    newComment.value = ''
  } catch (err) {
    console.error('Failed to submit comment:', err)
  } finally {
    isSubmitting.value = false
  }
}

const editingCommentId = ref(null)    // ID of the comment being edited
const editContent = ref('')           // Temp content while editing

const startEdit = (comment) => {
  editingCommentId.value = comment.id
  editContent.value = comment.content
}

const cancelEdit = () => {
  editingCommentId.value = null
  editContent.value = ''
}

const saveComment = async (comment) => {
  try {
    const updated = await $fetch(
        `${config.public.apiBase}/publications/${publication.value.id}/comments/${comment.id}`,
        {
          method: 'PATCH',
          headers: { Authorization: `Bearer ${authStore.token}` },
          body: { content: editContent.value }
        }
    )
    comment.content = updated.content
    cancelEdit()
  } catch (e) {
    console.error('Failed to save comment:', e)
    alert('Failed to save comment')
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('Are you sure you want to delete this comment?')) return

  try {
    await $fetch(`${config.public.apiBase}/publications/${publication.value.id}/comments/${commentId}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${authStore.token}` }
    })


    publication.value = {
      ...publication.value,
      numberOfComments: Math.max(0, publication.value.numberOfComments - 1),
      comments: publication.value.comments.filter(c => c.id !== commentId)
    }



  } catch (err) {
    console.error('Failed to delete comment:', err)
    alert('Error deleting comment.')
  }
}

// Function to change visibility
const changeCommentVisibility = async (comment) => {
  if (!comment || !comment.id || !publication.value?.id) return

  try {
    const updatedComment = await $fetch(
        `${config.public.apiBase}/publications/${publication.value.id}/comments/${comment.id}/visibility`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${authStore.token}`,
            'Content-Type': 'application/json'
          }
        }
    )

    // Update publication immutably
    publication.value = {
      ...publication.value,
      comments: publication.value.comments.map(c =>
          c.id === comment.id ? updatedComment : c
      )
    }

  } catch (err) {
    console.error('Failed to change comment visibility:', err)
    alert('Could not update comment visibility.')
  }
}

// Function to toggle publication visibility
const changePublicationVisibility = async () => {
  if (!publication.value?.id) return

  try {
    const updatedPublication = await $fetch(
        `${config.public.apiBase}/publications/${publication.value.id}/visibility`,
        {
          method: 'PATCH',
          headers: {
            Authorization: `Bearer ${authStore.token}`,
            'Content-Type': 'application/json'
          }
        }
    )

    // Update the publication immutably
    publication.value = {
      ...publication.value,
      visibility: updatedPublication.visibility
    }

  } catch (err) {
    console.error('Failed to change publication visibility:', err)
    alert('Error changing publication visibility.')
  }
}



const { data: publication, pending, error } = await useFetch(
    `${config.public.apiBase}/publications/${route.params.id}`,
    {
      headers: {
        Authorization: `Bearer ${authStore.token}`
      }
    }
)



const submitRating = async (value) => {
  if (isRating.value) return
  isRating.value = true

  try {
    // If userRating is 0, it's a new rating (POST)
    // If userRating is NOT 0, it's an update (PATCH)
    const method = userRating.value === 0 ? 'POST' : 'PATCH'

    await $fetch(`${config.public.apiBase}/publications/${route.params.id}/rating`, {
      method: method,
      headers: { Authorization: `Bearer ${authStore.token}` },
      body: { value }
    })

    // Update local UI state
    const currentRating = publication.value.rating || 0
    const currentCount = publication.value.numberOfReviews || 0

    if (userRating.value !== 0) {
      // Update existing: (Total - Old + New) / Count
      publication.value.rating = ((currentRating * currentCount) - userRating.value + value) / currentCount
    } else {
      // New rating: (Total + New) / (Count + 1)
      publication.value.numberOfReviews = currentCount + 1
      publication.value.rating = ((currentRating * currentCount) + value) / (currentCount + 1)
    }

    userRating.value = value
    alert(method === 'POST' ? 'Thank you for your rating!' : 'Rating updated!')

  } catch (err) {
    console.error('Rating failed:', err)
    alert('Error submitting rating.')
  } finally {
    isRating.value = false
  }
}

const deleteMyRating = async () => {
  // Prevent multiple clicks or deleting if no rating exists
  if (userRating.value === 0 || isRating.value) return

  isRating.value = true
  try {
    await $fetch(`${config.public.apiBase}/publications/${route.params.id}/rating`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${authStore.token}` }
    })

    // --- Update local UI stats mathematically ---
    const currentRating = publication.value.rating || 0
    const currentCount = publication.value.numberOfReviews || 0

    if (currentCount > 1) {
      // Formula: (TotalSum - UserValue) / (Count - 1)
      const totalSum = currentRating * currentCount
      publication.value.rating = (totalSum - userRating.value) / (currentCount - 1)
      publication.value.numberOfReviews = currentCount - 1
    } else {
      // If it was the only rating, reset everything to 0
      publication.value.rating = 0
      publication.value.numberOfReviews = 0
    }

    // Reset the user's local rating state
    userRating.value = 0
    alert('Rating removed successfully.')

  } catch (err) {
    console.error('Failed to remove rating:', err)
    alert('Error removing rating. Please try again.')
  } finally {
    isRating.value = false
  }
}

const isGenerating = ref(false)

// Function to generate/re-generate AI summary
const generateAIText = async () => {
  if (isGenerating.value) return
  isGenerating.value = true

  try {
    const updated = await $fetch(`${config.public.apiBase}/publications/${publication.value.id}/generate-ai-text`, {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${authStore.token}`,
        'Content-Type': 'application/json',
      }
    })

    // Update the publication object with the new AI text
    publication.value.aiGeneratedSummary = updated.aiGeneratedSummary
  } catch (e) {
    console.error('Failed to generate AI text:', e)
    alert('Failed to generate AI text.')
  } finally {
    isGenerating.value = false
  }
}

// --- All tags for adding ---
const allTags = ref([])
const isTagUpdating = ref(false)
const tagError = ref('')


onMounted(async () => {
      // Fetch the publication
      try {
        publication.value = await $fetch(
            `${config.public.apiBase}/publications/${route.params.id}`,
            {
              headers: { Authorization: `Bearer ${authStore.token}` }
            }
        )

        // Make sure tags array exists
        publication.value.tags ??= []

        // --- Reorder comments: user comments first ---
        if (publication.value.comments?.length) {
          const userId = authStore.user?.id
          publication.value.comments = [
            ...publication.value.comments.filter(c => c.user?.id === userId),
            ...publication.value.comments.filter(c => c.user?.id !== userId)
          ]
        }

      } catch (e) {
        console.error('Failed to fetch publication:', e)
      }

      // Fetch all available tags
      try {
        allTags.value = await $fetch(`${config.public.apiBase}/tags`, {
          headers: { Authorization: `Bearer ${authStore.token}` }
        })
      } catch (e) {
        console.error('Failed to fetch tags:', e)
        tagError.value = 'Could not load tags'
      }
    })


// --- Add tag ---
const addTag = async (tagId) => {
  if (!publication.value || isTagUpdating.value) return

  publication.value.tags = publication.value.tags ?? []

  // prevent duplicates
  if (publication.value.tags.some(t => t.id === tagId)) return

  isTagUpdating.value = true
  try {
    await $fetch(
        `${config.public.apiBase}/publications/${publication.value.id}/subscriptions`,
        {
          method: 'POST',
          headers: { Authorization: `Bearer ${authStore.token}` },
          body: { id: tagId }
        }
    )

    const tag = allTags.value.find(t => t.id === tagId)
    if (tag) {
      publication.value.tags = [...publication.value.tags, tag]
    }

  } catch (e) {
    console.error('Failed to add tag:', e)
  } finally {
    isTagUpdating.value = false
  }
}

// --- Remove tag ---
const removeTag = async (tagId) => {
  if (!publication.value || isTagUpdating.value) return

  publication.value.tags = publication.value.tags ?? []

  isTagUpdating.value = true
  try {
    await $fetch(
        `${config.public.apiBase}/publications/${publication.value.id}/subscriptions`,
        {
          method: 'DELETE',
          headers: { Authorization: `Bearer ${authStore.token}` },
          body: { id: tagId }
        }
    )

    publication.value.tags = publication.value.tags.filter(t => t.id !== tagId)

  } catch (e) {
    console.error('Failed to remove tag:', e)
  } finally {
    isTagUpdating.value = false
  }
}




userRating.value = publication.value.rating

const formatDate = (date) => new Date(date).toLocaleString('pt-PT')

</script>


<template>
  <div class="container mx-auto py-12 max-w-4xl">

    <!-- Loading -->
    <div v-if="pending" class="text-center text-slate-500">
      Loading publication…
    </div>

    <!-- Error -->
    <div v-else-if="error" class="text-center text-red-500">
      Failed to load publication.
    </div>

    <!-- Content -->
    <div v-else class="space-y-6">

      <h1 class="text-4xl font-bold text-slate-900">
        {{ publication.title }}
        <span class="ml-20">
         <Button variant="secondary"
             v-if="['RESPONSAVEL', 'ADMIN'].includes(authStore.user?.role)"
             class="text-sm text-blue-600 hover:underline"
             @click="changePublicationVisibility"
         >
  Change Visibility
</Button>

          <Button variant="default">
              <NuxtLink :to="`/publications/update/${publication.id}`">Update</NuxtLink>
          </Button>
        </span>
      </h1>


      <div class="flex flex-wrap gap-4 text-sm text-muted-foreground">
        <span>👤 {{ publication.author }}</span>
        <span>📅 Created: {{ formatDate(publication.creationDate) }}</span>
        <span>✏️ Updated: {{ formatDate(publication.updatedDate) }}</span>
        <span v-if="['RESPONSAVEL', 'ADMIN'].includes(authStore.user?.role)">
  👁️ {{ publication.visibility }}
</span>


      </div>

      <p class="text-slate-700 leading-relaxed mb-6 mt-4 ml-6">
        {{ publication.description }}
      </p>

      <h6 class="text-slate-400">Generated Ai text</h6>
      <div class="ml-6 mb-6">
        <p class="text-slate-700 leading-relaxed mb-4 mt-4">
          {{ publication.aiGeneratedSummary !== '' ? publication.aiGeneratedSummary : '' }}
        </p>

        <button
            @click="generateAIText"
            :disabled="isGenerating"
            class="px-4 py-2 rounded bg-black text-white hover:bg-slate-800 disabled:opacity-50 disabled:cursor-not-allowed transition"
        >
          {{ publication.aiGeneratedSummary !== '' ? (isGenerating ? 'Regenerating...' : 'Re-generate') : (isGenerating ? 'Generating...' : 'Generate') }}
        </button>
      </div>


      <!-- Tags -->
      <div class="mb-6" v-if="publication">
        <h4 class="text-sm font-semibold mb-2">Tags</h4>

        <!-- Current tags -->
        <div class="flex flex-wrap gap-2 mb-4">
  <span
      v-for="tag in (publication.tags ?? [])"
      :key="tag.id"
      class="px-3 py-1 text-xs bg-slate-900 text-white rounded-full uppercase tracking-wide cursor-pointer"
      @click="removeTag(tag.id)"
      title="Click to remove"
  >
    {{ tag.name }} ✕
  </span>
        </div>

<p class="mb-4" >Tags available to</p>
        <!-- Available tags to add -->
        <div class="flex flex-wrap gap-2">
  <span
      v-for="tag in allTags"
      :key="tag.id"
      @click="(!isTagUpdating && !publication.tags?.some(t => t.id === tag.id)) && addTag(tag.id)"
      :class="[
      'px-3 py-1 text-xs rounded-full uppercase tracking-wide transition-colors',
      isTagUpdating
        ? 'bg-gray-300 text-gray-400 cursor-not-allowed'
        : publication.tags?.some(t => t.id === tag.id)
          ? 'bg-slate-900 text-white cursor-not-allowed'
          : 'bg-gray-200 text-gray-800 cursor-pointer'
    ]"
      :title="publication.tags?.some(t => t.id === tag.id) ? 'Already added' : 'Click to add'"
  >
    {{ tag.name }}
  </span>
        </div>


        <p v-if="tagError" class="text-red-500 text-xs mt-1">{{ tagError }}</p>
      </div>






      <!-- Stats -->
      <div class="flex justify-between items-center text-sm text-slate-600 border-t mt-4">

        <!-- Left side: stats -->
        <div class="flex gap-6 p-4 ">
          <!-- ⭐ Rate this Publication -->
          <div class="bg-slate-50 rounded-lg p-6 border border-slate-200 mb-6">
            <h3 class="text-sm font-semibold text-slate-800 mb-3">Rate this publication</h3>
            <div class="flex items-center gap-2">
              <div class="flex gap-1">
                <button
                    v-for="star in 5"
                    :key="star"
                    @click="submitRating(star)"
                    @mouseenter="hoverRating = star"
                    @mouseleave="hoverRating = 0"
                    class="text-2xl transition-colors duration-150 focus:outline-none"
                    :class="[
          (hoverRating || userRating) >= star ? 'text-yellow-400' : 'text-slate-300',
          isRating ? 'cursor-not-allowed opacity-50' : 'cursor-pointer'
        ]"
                >
                  ★
                </button>
              </div>
              <span class="text-sm text-slate-500 ml-2">
      {{ userRating > 0 ? `You rated this ${Number(userRating).toFixed(2)}/5` : 'Click a star to rate' }}
    </span>
            </div>
            <div v-if="userRating > 0" class="mt-4">
              <Button variant="destructive" @click="deleteMyRating" :disabled="isRating">
                {{ isRating ? 'Removing...' : 'Remove rating' }}
              </Button>
            </div>
          </div>

          <span>⭐ {{ Number(publication.rating).toFixed(2) }}</span>
          <span>📝 {{ publication.numberOfReviews }} reviews</span>
        </div>

        <button
            @click="downloadFile(publication.id, publication.file)"
            class="text-blue-600 hover:underline font-medium flex items-center gap-2"
        >
          📄 Download file
        </button>



      </div>


    </div>
    <!-- 💬 Comments -->
    <div v-if="!pending && publication" class="border-t pt-8">

      <h2 class="mt-4 text-2xl font-semibold text-slate-900 mb-4">
        Comments ({{ publication.comments.length }})
      </h2>

      <!-- ✍️ Add comment -->
      <div class="flex flex-col mb-6 bg-slate-50 rounded-lg border p-4">
        <h3 class="text-sm font-semibold text-slate-800 mb-2">
          Add a comment
        </h3>

        <textarea
            v-model="newComment"
            rows="3"
            placeholder="Write your comment…"
            class="w-full text-sm border rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-slate-300"
        ></textarea> <!-- ✅ Fixed: No longer self-closing -->

        <div class="flex justify-end mt-4"> <!-- ✅ Added 'flex' to make 'justify-end' work -->
          <button
              @click="submitComment"
              :disabled="isSubmitting || !newComment.trim()"
              class="px-4 py-1.5 text-sm rounded-md bg-black text-white disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-800 bg-slate-900 transition"
          >
            {{ isSubmitting ? 'Posting…' : 'Post comment' }}
          </button>
        </div>
      </div>

      <!-- 🚫 No comments -->
      <p
          v-if="publication.comments.length === 0"
          class="text-sm text-muted-foreground italic"
      >
        No comments yet.
      </p>

      <!-- 💬 Comment list -->
      <div v-else class="space-y-4">
        <div   v-for="comment in publication.comments.filter(comment =>
      comment.visibility === 'VISIBLE' ||
      (comment.visibility === 'INVISIBLE' && ['RESPONSAVEL', 'ADMIN'].includes(authStore.user?.role))
  )"
            :key="comment.id"
               :class="[
    'rounded-lg border p-4 shadow-sm',
    comment.visibility === 'VISIBLE' ? 'bg-white' : 'bg-yellow-50 border-yellow-300'
  ]"
        >
          <!-- Header: user name, date, and edit button -->
          <div  class="flex justify-between items-center mb-2">
            <div class="flex flex-col">
        <span  class="font-medium text-slate-800">
          {{ comment.user?.name || 'Anonymous' }}
        </span>
              <span class="text-xs text-slate-400">
          {{ formatDate(comment.creationDate) }}
        </span>

            </div>

            <!-- Botões só para autores/admin/responsáveis -->
            <div class="mt-2 flex gap-2">
              <button
                  v-if="authStore.user?.id === comment.user.id"
                  class="text-sm text-blue-600 hover:underline"
                  @click="startEdit(comment)"
              >
                Edit
              </button>

              <button
                  v-if="authStore.user?.id === comment.user.id"
                  class="text-sm text-red-600 hover:underline"
                  @click="deleteComment(comment.id)"
              >
                Delete
              </button>

              <button
                  v-if="['RESPONSAVEL', 'ADMIN'].includes(authStore.user?.role)"
                  class="text-sm text-purple-600 hover:underline"
                  @click="changeCommentVisibility(comment)"
              >
                Change Visibility
              </button>
            </div>
          </div>

          <!-- Editable textarea -->
          <div v-if="editingCommentId === comment.id" class="flex flex-col gap-2 mb-2">
      <textarea
          v-model="editContent"
          rows="3"
          class="w-full text-sm border rounded-md p-2 focus:outline-none focus:ring-2 focus:ring-slate-300"
      ></textarea>
            <div class="flex gap-2 justify-end">
              <button
                  class="px-3 py-1 text-sm bg-green-500 text-white rounded hover:bg-green-600"
                  @click="saveComment(comment)"
              >
                Save
              </button>
              <button
                  class="px-3 py-1 text-sm bg-red-500 text-white rounded hover:bg-red-600"
                  @click="cancelEdit"
              >
                Cancel
              </button>
            </div>
          </div>

          <!-- Comment content -->
          <p v-if="editingCommentId !== comment.id" class="text-xs text-slate-500 leading-relaxed">
            {{ comment.content }}
          </p>
        </div>
      </div>

    </div>


  </div>
</template>



<style scoped>

</style>