import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getPosts } from '../api'

const styles = {
  body: { fontFamily: 'Georgia, serif', maxWidth: 760, margin: '0 auto', padding: '2rem 1rem', color: '#222' },
  header: { borderBottom: '2px solid #222', marginBottom: '2rem', paddingBottom: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' },
  h1: { margin: 0, fontSize: '2rem' },
  nav: { fontFamily: 'sans-serif', fontSize: '0.9rem', color: '#555', textDecoration: 'none' },
  entry: { marginBottom: '2.5rem', paddingBottom: '2.5rem', borderBottom: '1px solid #ddd' },
  postTitle: { margin: '0 0 0.3rem', fontSize: '1.5rem' },
  postLink: { color: '#222', textDecoration: 'none' },
  meta: { fontFamily: 'sans-serif', fontSize: '0.85rem', color: '#777', marginBottom: '0.8rem' },
  excerpt: { margin: 0, lineHeight: 1.7 },
  empty: { color: '#777', fontStyle: 'italic' },
  error: { background: '#fdd', border: '1px solid #c00', padding: '0.6rem 1rem', marginBottom: '1.5rem', borderRadius: 4, fontFamily: 'sans-serif', fontSize: '0.9rem' },
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

export default function Home() {
  const [posts, setPosts] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    getPosts()
      .then(setPosts)
      .catch(() => setError('Failed to load posts.'))
  }, [])

  return (
    <div style={styles.body}>
      <header style={styles.header}>
        <h1 style={styles.h1}>My Blog</h1>
        <Link to="/admin" style={styles.nav}>Admin</Link>
      </header>
      {error && <div style={styles.error}>{error}</div>}
      <main>
        {posts.length === 0 && !error && <p style={styles.empty}>No posts yet.</p>}
        {posts.map(post => (
          <div key={post.id} style={styles.entry}>
            <h2 style={styles.postTitle}>
              <Link to={`/post/${post.id}`} style={styles.postLink}>{post.title}</Link>
            </h2>
            <div style={styles.meta}>{formatDate(post.createdAt)}</div>
            <p style={styles.excerpt}>{post.excerpt}</p>
          </div>
        ))}
      </main>
    </div>
  )
}
