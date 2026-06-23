import { useEffect, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { getPost } from '../api'

const styles = {
  body: { fontFamily: 'Georgia, serif', maxWidth: 760, margin: '0 auto', padding: '2rem 1rem', color: '#222' },
  header: { borderBottom: '2px solid #222', marginBottom: '2rem', paddingBottom: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' },
  h1: { margin: 0, fontSize: '2rem' },
  headerLink: { fontFamily: 'sans-serif', fontSize: '0.9rem', color: '#555', textDecoration: 'none' },
  postTitle: { fontSize: '2rem', margin: '0 0 0.4rem' },
  meta: { fontFamily: 'sans-serif', fontSize: '0.85rem', color: '#777', marginBottom: '2rem' },
  content: { lineHeight: 1.8, whiteSpace: 'pre-wrap' },
  backLink: { display: 'inline-block', marginTop: '2.5rem', fontFamily: 'sans-serif', fontSize: '0.9rem', color: '#555', textDecoration: 'none' },
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' })
}

export default function PostDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [post, setPost] = useState(null)

  useEffect(() => {
    getPost(id)
      .then(setPost)
      .catch(() => navigate('/'))
  }, [id, navigate])

  if (!post) return null

  return (
    <div style={styles.body}>
      <header style={styles.header}>
        <h1 style={styles.h1}>My Blog</h1>
        <Link to="/admin" style={styles.headerLink}>Admin</Link>
      </header>
      <article>
        <h2 style={styles.postTitle}>{post.title}</h2>
        <div style={styles.meta}>{formatDate(post.createdAt)}</div>
        <div style={styles.content}>{post.content}</div>
      </article>
      <Link to="/" style={styles.backLink}>← All posts</Link>
    </div>
  )
}
