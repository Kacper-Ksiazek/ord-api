# @ord-api/ord-api-types

TypeScript types for the ORD API, automatically generated from the OpenAPI specification.

## Installation

```bash
# npm
npm install @ord-api/ord-api-types

# pnpm
pnpm add @ord-api/ord-api-types

# yarn
yarn add @ord-api/ord-api-types
```

## Usage with Axios

### Basic Setup

```typescript
import axios from 'axios';
import type { paths, components } from '@ord-api/ord-api-types';

// Create a typed axios instance
const api = axios.create({
  baseURL: 'https://api.ord-platform.com',
  withCredentials: true, // Important for JWT cookies
});
```

### Type-Safe API Calls

#### Authentication

```typescript
import type { paths } from '@ord-api/ord-api-types';

// Request OTP
type OtpRequestBody = paths['/api/v1/auth/otp-request']['post']['requestBody']['content']['application/json'];
type OtpRequestResponse = paths['/api/v1/auth/otp-request']['post']['responses']['200']['content']['application/json'];

const requestOtp = async (email: string) => {
  const body: OtpRequestBody = { email };
  const response = await api.post<OtpRequestResponse>('/api/v1/auth/otp-request', body);
  return response.data;
};

// Verify OTP
type OtpVerifyBody = paths['/api/v1/auth/otp-verify']['post']['requestBody']['content']['application/json'];

const verifyOtp = async (email: string, code: string) => {
  const body: OtpVerifyBody = { email, code };
  const response = await api.post('/api/v1/auth/otp-verify', body);
  return response.data;
};
```

#### Using Component Schemas

```typescript
import type { components } from '@ord-api/ord-api-types';

// Use DTOs directly
type UserDTO = components['schemas']['UserDTO'];
type CreateWordRequest = components['schemas']['CreateWordRequest'];
type WordDTO = components['schemas']['WordDTO'];
type LanguageName = components['schemas']['LanguageName'];

// Get current user
const getCurrentUser = async (): Promise<UserDTO> => {
  const response = await api.get<UserDTO>('/api/v1/users/me');
  return response.data;
};

// Create a new word
const createWord = async (word: CreateWordRequest): Promise<WordDTO> => {
  const response = await api.post<WordDTO>('/api/v1/words/', word);
  return response.data;
};
```

#### Pagination

```typescript
import type { components } from '@ord-api/ord-api-types';

type PaginatedResponse<T> = components['schemas']['PaginatedDataResponse'] & {
  data: T[];
};

type QuicklyAddedWordDTO = components['schemas']['QuicklyAddedWordDTO'];

const getQuicklyAddedWords = async (page = 0, perPage = 20) => {
  const response = await api.get<PaginatedResponse<QuicklyAddedWordDTO>>(
    '/api/v1/quickly-added-words/',
    { params: { page, perPage } }
  );

  return {
    words: response.data.data,
    pagination: response.data.pagination,
  };
};
```

#### Advanced: Type-Safe Request Helper

```typescript
type ApiPath = keyof paths;
type ApiMethod = 'get' | 'post' | 'patch' | 'delete';

type RequestBody<
  Path extends ApiPath,
  Method extends ApiMethod
> = paths[Path][Method] extends { requestBody: { content: { 'application/json': infer Body } } }
  ? Body
  : never;

type ResponseData<
  Path extends ApiPath,
  Method extends ApiMethod
> = paths[Path][Method] extends { responses: { 200: { content: { 'application/json': infer Data } } } }
  ? Data
  : never;

// Type-safe API call wrapper
async function apiCall<
  Path extends ApiPath,
  Method extends ApiMethod
>(
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
// user is typed as UserDTO automatically!

const newWord = await apiCall('post', '/api/v1/words/', {
  word: 'comprehensive',
  language: 'ENGLISH',
  definition: 'Complete; including all elements',
});
// newWord is typed as WordDTO automatically!
```

#### Enums

```typescript
import type { components } from '@ord-api/ord-api-types';

type LanguageName = components['schemas']['LanguageName'];
type WordType = components['schemas']['WordType'];
type WordExtraMark = components['schemas']['WordExtraMark'];

const createWord = async () => {
  const language: LanguageName = 'ENGLISH';
  const type: WordType = 'NOUN';
  const extraMark: WordExtraMark = 'SLANG';

  await api.post('/api/v1/words/', {
    word: 'awesome',
    language,
    type,
    extraMark,
  });
};
```

## React Example

```typescript
import { useState, useEffect } from 'react';
import type { components } from '@ord-api/ord-api-types';

type UserDTO = components['schemas']['UserDTO'];

function useCurrentUser() {
  const [user, setUser] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<UserDTO>('/api/v1/users/me')
      .then(res => setUser(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  return { user, loading };
}
```

## React Query Example

```typescript
import { useQuery, useMutation } from '@tanstack/react-query';
import type { components } from '@ord-api/ord-api-types';

type QuicklyAddedWordDTO = components['schemas']['QuicklyAddedWordDTO'];
type CreateQAWRequest = components['schemas']['CreateQAWRequest'];

// Fetch words
export function useQuicklyAddedWords() {
  return useQuery({
    queryKey: ['quickly-added-words'],
    queryFn: async () => {
      const response = await api.get<{ data: QuicklyAddedWordDTO[] }>(
        '/api/v1/quickly-added-words/'
      );
      return response.data.data;
    },
  });
}

// Create word
export function useCreateQuicklyAddedWord() {
  return useMutation({
    mutationFn: async (word: CreateQAWRequest) => {
      const response = await api.post<QuicklyAddedWordDTO>(
        '/api/v1/quickly-added-words/',
        word
      );
      return response.data;
    },
  });
}
```

## Best Practices

1. **Always use types from this package** instead of defining your own interfaces
2. **Import only what you need** to keep bundle size small
3. **Use component schemas** (`components['schemas']['...']`) for DTOs and enums
4. **Use path types** (`paths['...']['...']`) for request/response types
5. **Keep this package updated** to match backend changes

## Updating

This package is automatically published when the backend API changes. To update:

```bash
npm update @ord-api/ord-api-types
```

## License

MIT