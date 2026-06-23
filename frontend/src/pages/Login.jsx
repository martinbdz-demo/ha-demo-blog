import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api'
import { useAuth } from '../App'

const styles = {
  body: { fontFamily: 'sans-serif', maxWidth: 400, margin: '6rem auto', padding: '0 1rem', color: '#222' },
  h1: { fontSize: '1.5rem', marginBottom: '1.5rem' },
  label: { display: 'block', marginBottom: '0.3rem', fontSize: '0.9rem', color: '#555' },
  input: { width: '100%', boxSizing: 'border-box', padding: '0.5rem 0.6rem', fontSize: '1rem', border: '1px solid #ccc', borderRadius: 4, marginBottom: '1rem', fontFamily: 'inherit' },
  button: { width: '100%', padding: '0.6rem', fontSize: '1rem', background: '#222', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' },
  error: { background: '#fdd', border: '1px solid #c00', padding: '0.6rem 1rem', marginBottom: '1.2rem', borderRadius: 4, fontSize: '0.9rem' },
  backLink: { display: 'inline-block', marginTop: '1.5rem', fontSize: '0.9rem', color: '#555', textDecoration: 'none' },
}

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const { setUser } = useAuth()
  const navigate = useNavigate()

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const user = await login(username, password)
      setUser(user)
      navigate('/admin')
    } catch {
      setError('Invalid username or password.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={styles.body}>
      <h1 style={styles.h1}>Admin Login</h1>
      {error && <div style={styles.error}>{error}</div>}
      <form onSubmit={handleSubmit}>
        <label style={styles.label} htmlFor="username">Username</label>
        <input id="username" type="text" style={styles.input} value={username}
          onChange={e => setUsername(e.target.value)} autoComplete="username" autoFocus />
        <label style={styles.label} htmlFor="password">Password</label>
        <input id="password" type="password" style={styles.input} value={password}
          onChange={e => setPassword(e.target.value)} autoComplete="current-password" />
        <button type="submit" style={styles.button} disabled={loading}>
          {loading ? 'Logging in…' : 'Log in'}
        </button>
      </form>
      <Link to="/" style={styles.backLink}>← Back to blog</Link>
    </div>
  )
}
