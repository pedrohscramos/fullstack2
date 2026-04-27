import type { AuthTokenResponse, LoginPayload, RegisterPayload, UserResponse } from '@/types/auth'
import { request } from './http'

export async function register(payload: RegisterPayload): Promise<UserResponse> {
  return request<UserResponse>('/auth/register', {
    method: 'POST',
    body: payload,
  })
}

export async function login(payload: LoginPayload): Promise<AuthTokenResponse> {
  return request<AuthTokenResponse>('/auth/login', {
    method: 'POST',
    body: payload,
  })
}

export async function refresh(refreshToken: string): Promise<AuthTokenResponse> {
  return request<AuthTokenResponse>('/auth/refresh', {
    method: 'POST',
    body: { refreshToken },
  })
}
