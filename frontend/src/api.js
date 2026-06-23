async function request(path, options = {}) {
  const res = await fetch(`/api${path}`, {
    credentials: 'same-origin',
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  });
  if (!res.ok) throw res;
  return res.status === 204 ? null : res.json();
}

export const getPosts = () => request('/posts');
export const getPost = (id) => request(`/posts/${id}`);

export const login = (username, password) =>
  request('/auth/login', { method: 'POST', body: JSON.stringify({ username, password }) });
export const logout = () => request('/auth/logout', { method: 'POST' });
export const getMe = () => request('/auth/me');

export const createPost = (post) =>
  request('/admin/posts', { method: 'POST', body: JSON.stringify(post) });
export const deletePost = (id) =>
  request(`/admin/posts/${id}`, { method: 'DELETE' });
