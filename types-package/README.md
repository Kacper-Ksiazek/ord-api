# @kacper-ksiazek/ord-api-types

TypeScript types for the ORD API, automatically generated from the OpenAPI specification.

Published to **GitHub Packages**. The GitHub npm registry requires authentication
even for public packages, so consumers must configure an `.npmrc` first.

## Installation

### 1. Configure the registry

Create an `.npmrc` in your project root (or `~/.npmrc`):

```ini
@kacper-ksiazek:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=${GITHUB_TOKEN}
```

`GITHUB_TOKEN` must be a Personal Access Token with the `read:packages` scope.
In GitHub Actions you can use the built-in `${{ secrets.GITHUB_TOKEN }}`.

### 2. Install

```bash
# pnpm (recommended for SvelteKit)
pnpm add @kacper-ksiazek/ord-api-types

# npm
npm install @kacper-ksiazek/ord-api-types

# yarn
yarn add @kacper-ksiazek/ord-api-types
```

## Setup for SvelteKit

### 1. Create Axios Instance

```typescript
// src/lib/api/client.ts
import axios from 'axios';
import { browser } from '$app/environment';

export const api = axios.create({
  baseURL: browser ? '' : 'http://localhost:8080', // Use proxy in browser, direct in SSR
  withCredentials: true, // Important for JWT cookies
});

// Optional: Add request/response interceptors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized
      goto('/login');
    }
    return Promise.reject(error);
  }
);
```

### 2. Configure SvelteKit Proxy (Optional)

```typescript
// vite.config.ts
import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
  plugins: [sveltekit()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

### 3. Export Common Types

```typescript
// src/lib/types/api-types.ts
import type { components } from '@kacper-ksiazek/ord-api-types';

// Export enums
export type LanguageName = components['schemas']['LanguageName'];
export type WordType = components['schemas']['WordType'];
export type WordExtraMark = components['schemas']['WordExtraMark'];
export type WordToggleableProperty = components['schemas']['WordToggleableProperty'];

// Export DTOs
export type UserDTO = components['schemas']['UserDTO'];
export type WordDTO = components['schemas']['WordDTO'];
export type QuicklyAddedWordDTO = components['schemas']['QuicklyAddedWordDTO'];
export type CreateQAWRequest = components['schemas']['CreateQAWRequest'];
export type PaginatedDataResponse<T> = components['schemas']['PaginatedDataResponse'] & {
  data: T[];
};
```

## Usage with TanStack Query (Svelte)

### Setup Query Client

```typescript
// src/routes/+layout.ts
import { browser } from '$app/environment';
import { QueryClient } from '@tanstack/svelte-query';

export const load = async () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        enabled: browser,
        staleTime: 60 * 1000, // 1 minute
      },
    },
  });

  return { queryClient };
};
```

```svelte
<!-- src/routes/+layout.svelte -->
<script lang="ts">
  import { QueryClientProvider } from '@tanstack/svelte-query';

  let { data, children } = $props();
</script>

<QueryClientProvider client={data.queryClient}>
  {@render children()}
</QueryClientProvider>
```

### Create Query Functions

```typescript
// src/lib/api/queries/words.ts
import { createQuery, createMutation } from '@tanstack/svelte-query';
import { api } from '$lib/api/client';
import type { QuicklyAddedWordDTO, CreateQAWRequest, PaginatedDataResponse } from '$lib/types/api-types';

export const createQuicklyAddedWordsQuery = (page = 0, perPage = 20) => {
  return createQuery({
    queryKey: ['quickly-added-words', page, perPage],
    queryFn: async () => {
      const response = await api.get<PaginatedDataResponse<QuicklyAddedWordDTO>>(
        '/api/v1/quickly-added-words/',
        { params: { page, perPage } }
      );
      return response.data;
    },
  });
};

export const createCreateQAWMutation = () => {
  return createMutation({
    mutationFn: async (word: CreateQAWRequest) => {
      const response = await api.post<QuicklyAddedWordDTO>(
        '/api/v1/quickly-added-words/',
        word
      );
      return response.data;
    },
  });
};

export const createDeleteQAWMutation = () => {
  return createMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/api/v1/quickly-added-words/${id}`);
    },
  });
};
```

### Use in Svelte 5 Components

```svelte
<!-- src/routes/words/+page.svelte -->
<script lang="ts">
  import { createQuicklyAddedWordsQuery, createCreateQAWMutation } from '$lib/api/queries/words';
  import type { LanguageName } from '$lib/types/api-types';

  let page = $state(0);
  let perPage = $state(20);

  // Query
  const wordsQuery = createQuicklyAddedWordsQuery(page, perPage);
  const words = $derived($wordsQuery.data?.data ?? []);
  const pagination = $derived($wordsQuery.data?.pagination);

  // Mutation
  const createWordMutation = createCreateQAWMutation();

  async function handleCreateWord(word: string, language: LanguageName) {
    await $createWordMutation.mutateAsync({
      word,
      language,
    });

    // Invalidate and refetch
    $wordsQuery.refetch();
  }
</script>

{#if $wordsQuery.isPending}
  <p>Loading...</p>
{:else if $wordsQuery.isError}
  <p>Error: {$wordsQuery.error.message}</p>
{:else}
  <div>
    <h1>Words ({pagination?.totalResults ?? 0})</h1>

    {#each words as word (word.id)}
      <div>
        <h3>{word.word}</h3>
        <p>{word.language}</p>
      </div>
    {/each}

    <!-- Pagination -->
    {#if pagination}
      <button
        disabled={page === 0}
        onclick={() => page--}
      >
        Previous
      </button>
      <span>Page {page + 1} of {pagination.totalPages}</span>
      <button
        disabled={page >= pagination.totalPages - 1}
        onclick={() => page++}
      >
        Next
      </button>
    {/if}
  </div>
{/if}
```

### Authentication Flow

```typescript
// src/lib/api/queries/auth.ts
import { createMutation } from '@tanstack/svelte-query';
import { api } from '$lib/api/client';
import type { components, paths } from '@kacper-ksiazek/ord-api-types';

type OtpRequestBody = paths['/api/v1/auth/otp-request']['post']['requestBody']['content']['application/json'];
type OtpVerifyBody = paths['/api/v1/auth/otp-verify']['post']['requestBody']['content']['application/json'];
type UserDTO = components['schemas']['UserDTO'];

export const createRequestOtpMutation = () => {
  return createMutation({
    mutationFn: async (body: OtpRequestBody) => {
      await api.post('/api/v1/auth/otp-request', body);
    },
  });
};

export const createVerifyOtpMutation = () => {
  return createMutation({
    mutationFn: async (body: OtpVerifyBody) => {
      const response = await api.post('/api/v1/auth/otp-verify', body);
      return response.data;
    },
  });
};

export const createLogoutMutation = () => {
  return createMutation({
    mutationFn: async () => {
      await api.delete('/api/v1/auth/logout');
    },
  });
};

export const createCurrentUserQuery = () => {
  return createQuery({
    queryKey: ['user', 'me'],
    queryFn: async () => {
      const response = await api.get<UserDTO>('/api/v1/users/me');
      return response.data;
    },
  });
};
```

```svelte
<!-- src/routes/login/+page.svelte -->
<script lang="ts">
  import { goto } from '$app/navigation';
  import { createRequestOtpMutation, createVerifyOtpMutation } from '$lib/api/queries/auth';

  let email = $state('');
  let code = $state('');
  let step = $state<'email' | 'code'>('email');

  const requestOtpMutation = createRequestOtpMutation();
  const verifyOtpMutation = createVerifyOtpMutation();

  async function handleRequestOtp() {
    await $requestOtpMutation.mutateAsync({ email });
    step = 'code';
  }

  async function handleVerifyOtp() {
    await $verifyOtpMutation.mutateAsync({ email, code });
    goto('/dashboard');
  }
</script>

{#if step === 'email'}
  <form onsubmit={handleRequestOtp}>
    <input
      type="email"
      bind:value={email}
      placeholder="Enter your email"
      required
    />
    <button
      type="submit"
      disabled={$requestOtpMutation.isPending}
    >
      {$requestOtpMutation.isPending ? 'Sending...' : 'Send OTP'}
    </button>
  </form>
{:else}
  <form onsubmit={handleVerifyOtp}>
    <input
      type="text"
      bind:value={code}
      placeholder="Enter 6-digit code"
      required
    />
    <button
      type="submit"
      disabled={$verifyOtpMutation.isPending}
    >
      {$verifyOtpMutation.isPending ? 'Verifying...' : 'Verify'}
    </button>
  </form>
{/if}
```

### Server Load Functions with Types

```typescript
// src/routes/words/[id]/+page.ts
import type { PageLoad } from './$types';
import type { components } from '@kacper-ksiazek/ord-api-types';
import { api } from '$lib/api/client';

type WordDTO = components['schemas']['WordDTO'];

export const load: PageLoad = async ({ params }) => {
  const response = await api.get<WordDTO>(`/api/v1/words/${params.id}`);

  return {
    word: response.data,
  };
};
```

```svelte
<!-- src/routes/words/[id]/+page.svelte -->
<script lang="ts">
  let { data } = $props();
</script>

<h1>{data.word.word}</h1>
<p>Language: {data.word.language}</p>
<p>Type: {data.word.type ?? 'Not specified'}</p>
```

## Advanced Patterns

### Type-Safe API Wrapper

```typescript
// src/lib/api/wrapper.ts
import type { paths } from '@kacper-ksiazek/ord-api-types';
import { api } from './client';

type ApiPath = keyof paths;
type ApiMethod = 'get' | 'post' | 'patch' | 'delete';

type RequestBody<Path extends ApiPath, Method extends ApiMethod> =
  paths[Path][Method] extends { requestBody: { content: { 'application/json': infer Body } } }
    ? Body
    : never;

type ResponseData<Path extends ApiPath, Method extends ApiMethod> =
  paths[Path][Method] extends { responses: { 200: { content: { 'application/json': infer Data } } } }
    ? Data
    : never;

export async function apiCall<Path extends ApiPath, Method extends ApiMethod>(
  method: Method,
  path: Path,
  body?: RequestBody<Path, Method>
): Promise<ResponseData<Path, Method>> {
  const response = await api.request({
    method,
    url: path as string,
    data: body,
  });
  return response.data;
}

// Usage
const user = await apiCall('get', '/api/v1/users/me');
const word = await apiCall('post', '/api/v1/words/', { word: 'test', language: 'ENGLISH' });
```

### Form Actions with Types

```typescript
// src/routes/words/create/+page.server.ts
import type { Actions } from './$types';
import type { CreateQAWRequest } from '$lib/types/api-types';
import { api } from '$lib/api/client';
import { fail } from '@sveltejs/kit';

export const actions = {
  default: async ({ request }) => {
    const data = await request.formData();

    const wordData: CreateQAWRequest = {
      word: data.get('word') as string,
      language: data.get('language') as any,
      definition: data.get('definition') as string || undefined,
    };

    try {
      await api.post('/api/v1/quickly-added-words/', wordData);
      return { success: true };
    } catch (error) {
      return fail(400, { error: 'Failed to create word' });
    }
  },
} satisfies Actions;
```

## Best Practices

1. **Use `$derived`** for computed values from queries
2. **Use `$state`** for local component state
3. **Export query factories** instead of hooks (Svelte pattern)
4. **Keep types in a central location** (`src/lib/types/api-types.ts`)
5. **Use SvelteKit's proxy** for API calls in development
6. **Handle SSR vs CSR** with `browser` check from `$app/environment`

## Updating

This package is automatically published when the backend API changes. To update:

```bash
pnpm update @kacper-ksiazek/ord-api-types
```

## Resources

- [TanStack Query Svelte Docs](https://tanstack.com/query/latest/docs/svelte/overview)
- [SvelteKit Docs](https://kit.svelte.dev/)
- [Svelte 5 Runes](https://svelte-5-preview.vercel.app/docs/runes)

## License

MIT