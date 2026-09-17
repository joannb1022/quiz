import { useQuery } from '@tanstack/react-query'
import apiClient from '@/lib/api-client'
import type { UserDto } from './types'

export function useCurrentUser() {
  return useQuery<UserDto>({
    queryKey: ['user', 'me'],
    queryFn: () => apiClient.get<UserDto>('/user/me').then((r) => r.data),
    retry: false,
  })
}
