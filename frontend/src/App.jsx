import { useEffect, useState } from 'react'
import { getHealthStatus } from './api/healthApi'
import { getCurrentUser, signIn, signOut } from './api/authApi'

function App() {
  const [healthMessage, setHealthMessage] = useState('Checking backend...')
  // undefined = still checking, null = not signed in
  const [user, setUser] = useState(undefined)

  useEffect(() => {
    getHealthStatus()
      .then((response) => {
        setHealthMessage(response.data)
      })
      .catch((error) => {
        console.error('Backend connection failed:', error)
        setHealthMessage('Backend connection failed')
      })

    getCurrentUser()
      .then(setUser)
      .catch(() => setUser(null))
  }, [])

  return (
    <div>
      <h1>SplitMate</h1>
      <p>{healthMessage}</p>

      {user === undefined && <p>Checking sign-in...</p>}
      {user === null && <button onClick={signIn}>Sign in with AuthReflector</button>}
      {user && (
        <p>
          Signed in as <strong>{user.name}</strong> ({user.email}){' '}
          <button onClick={signOut}>Sign out</button>
        </p>
      )}
    </div>
  )
}

export default App
