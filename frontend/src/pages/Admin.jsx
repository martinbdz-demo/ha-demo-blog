import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getPosts, createPost, deletePost, logout } from '../api'
import { useAuth } from '../App'

const styles = {
  body: { fontFamily: 'sans-serif', maxWidth: 900, margin: '0 auto', padding: '2rem 1rem', color: '#222' },
  header: { borderBottom: '2px solid #222', marginBottom: '2rem', paddingBottom: '1rem', display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', flexWrap: 'wrap', gap: '0.5rem' },
  h1: { margin: 0, fontSize: '1.5rem' },
  headerActions: { display: 'flex', gap: '1rem', alignItems: 'center' },
  username: { fontSize: '0.85rem', color: '#777' },
  btn: { padding: '0.4rem 0.9rem', fontSize: '0.9rem', borderRadius: 4, textDecoration: 'none', cursor: 'pointer', border: '1px solid #222', background: '#fff', color: '#222' },
  btnDanger: { padding: '0.4rem 0.9rem', fontSize: '0.9rem', borderRadius: 4, cursor: 'pointer', border: '1px solid #c00', background: '#fff', color: '#c00' },
  flash: { background: '#dfd', border: '1px solid #080', padding: '0.6rem 1rem', marginBottom: '1.5rem', borderRadius: 4, fontSize: '0.9rem' },
  table: { width: '100%', borderCollapse: 'collapse', marginBottom: '2rem' },
  th: { textAlign: 'left', padding: '0.6rem 0.8rem', borderBottom: '1px solid #eee', fontSize: '0.85rem', color: '#777', textTransform: 'uppercase', letterSpacing: '0.05em' },
  td: { padding: '0.6rem 0.8rem', borderBottom: '1px solid #eee' },
  postLink: { color: '#222', textDecoration: 'none' },
  section: { marginTop: '2rem', borderTop: '1px solid #eee', paddingTop: '1.5rem' },
  h2: { fontSize: '1.1rem', marginBottom: '1rem' },
  label: { display: 'block', marginBottom: '0.3rem', fontSize: '0.85rem', color: '#555' },
  input: { width: '100%', boxSizing: 'border-box', padding: '0.5rem 0.6rem', fontSize: '0.95rem', border: '1px solid #ccc', borderRadius: 4, marginBottom: '1rem', fontFamily: 'inherit' },
  textarea: { width: '100%', boxSizing: 'border-box', padding: '0.5rem 0.6rem', fontSize: '0.95rem', border: '1px solid #ccc', borderRadius: 4, marginBottom: '1rem', fontFamily: 'inherit', minHeight: 160, resize: 'vertical' },
  submitBtn: { padding: '0.6rem 1.5rem', fontSize: '1rem', background: '#222', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer' },
  fieldError: { color: '#c00', fontSize: '0.85rem', marginTop: '-0.8rem', marginBottom: '1rem' },
}

function formatDate(iso) {
  return new Date(iso).toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

export default function Admin() {
  const { user, setUser } = useAuth()
  const navigate = useNavigate()
  const [posts, setPosts] = useState([])
  const [flash, setFlash] = useState(null)
  const [form, setForm] = useState({ title: '', excerpt: '', content: '' })
  const [errors, setErrors] = useState({})

  useEffect(() => { loadPosts() }, [])

  function loadPosts() {
    getPosts().then(setPosts).catch(() => {})
  }

  async function handleLogout() {
    await logout()
    setUser(null)
    navigate('/')
  }

  async function handleDelete(id) {
    if (!window.confirm('Delete this post?')) return
    await deletePost(id)
    showFlash('Post deleted.')
    loadPosts()
  }

  function showFlash(msg) {
    setFlash(msg)
    setTimeout(() => setFlash(null), 3000)
  }

  function validate() {
    const e = {}
    if (!form.title.trim()) e.title = 'Title is required.'
    if (!form.excerpt.trim()) e.excerpt = 'Excerpt is required.'
    if (!form.content.trim()) e.content = 'Content is required.'
    return e
  }

  async function handleCreate(e) {
    e.preventDefault()
    const e2 = validate()
    if (Object.keys(e2).length) { setErrors(e2); return }
    setErrors({})
    await createPost(form)
    setForm({ title: '', excerpt: '', content: '' })
    showFlash('Post published.')
    loadPosts()
  }

  return (
    <div style={styles.body}>
      <header style={styles.header}>
        <h1 style={styles.h1}>Admin Dashboard</h1>
        <div style={styles.headerActions}>
          <span style={styles.username}>{user?.username}</span>
          <button style={styles.btn} onClick={handleLogout}>Log out</button>
          <Link to="/" style={styles.btn}>View Blog</Link>
        </div>
      </header>

      {flash && <div style={styles.flash}>{flash}</div>}

      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>Title</th>
            <th style={styles.th}>Date</th>
            <th style={styles.th}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {posts.length === 0 && (
            <tr><td colSpan={3} style={styles.td}>No posts yet.</td></tr>
          )}
          {posts.map(post => (
            <tr key={post.id}>
              <td style={styles.td}>
                <Link to={`/post/${post.id}`} style={styles.postLink} target="_blank">{post.title}</Link>
              </td>
              <td style={styles.td}>{formatDate(post.createdAt)}</td>
              <td style={styles.td}>
                <button style={styles.btnDanger} onClick={() => handleDelete(post.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <section style={styles.section}>
        <h2 style={styles.h2}>New Post</h2>
        <form onSubmit={handleCreate}>
          <label style={styles.label} htmlFor="title">Title</label>
          <input id="title" type="text" style={styles.input} value={form.title}
            onChange={e => setForm(f => ({ ...f, title: e.target.value }))} />
          {errors.title && <div style={styles.fieldError}>{errors.title}</div>}

          <label style={styles.label} htmlFor="excerpt">Excerpt <small style={{ color: '#aaa' }}>(shown on front page)</small></label>
          <input id="excerpt" type="text" style={styles.input} value={form.excerpt}
            onChange={e => setForm(f => ({ ...f, excerpt: e.target.value }))} />
          {errors.excerpt && <div style={styles.fieldError}>{errors.excerpt}</div>}

          <label style={styles.label} htmlFor="content">Content</label>
          <textarea id="content" style={styles.textarea} value={form.content}
            onChange={e => setForm(f => ({ ...f, content: e.target.value }))} />
          {errors.content && <div style={styles.fieldError}>{errors.content}</div>}

          <button type="submit" style={styles.submitBtn}>Publish</button>
        </form>
      </section>
    </div>
  )
}
