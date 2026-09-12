import { useEffect, useState } from 'react'
import { getHealthStatus } from './api/healthApi'

function App() {
  const [healthMessage, setHealthMessage] = useState('Checking backend...')

  useEffect(() => {
    getHealthStatus()
      .then((response) => {
        setHealthMessage(response.data)
      })
      .catch((error) => {
        console.error('Backend connection failed:', error)
        setHealthMessage('Backend connection failed')
      })
  }, [])

  return (
    <div>
      <h1>SplitMate</h1>
      <p>{healthMessage}</p>
    </div>
  )
}

export default App