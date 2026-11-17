import axiosClient from './axiosClient'

// Sign-in is handled by AuthReflector (single sign-on); these are the backend's /auth endpoints from the starter

/** The signed-in user ({ id, name, email, roles }) or null */
export async function getCurrentUser() {
  try {
    return (await axiosClient.get('/auth/me')).data
  } catch (error) {
    if (error.response?.status === 401) return null
    throw error
  }
}

/** Full page navigation: AuthReflector's sign-in page, then back here */
export function signIn() {
  window.location.href = '/auth/login'
}

/** Ends SplitMate's session, then AuthReflector's (single sign-out) */
export async function signOut() {
  const { logoutUrl } = (await axiosClient.post('/auth/logout')).data
  window.location.href = logoutUrl
}
