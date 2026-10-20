// 고객 관리 화면 공통 — 로그인 토큰 보관과 API 호출.
// 응답은 프레임워크(ApiResponse)가 { success, code, message, data } 로 감싼다.
const TOKEN_KEY = "customerPoc.accessToken";

function saveToken(token) {
  sessionStorage.setItem(TOKEN_KEY, token);
}

function requireLogin() {
  if (!sessionStorage.getItem(TOKEN_KEY)) {
    location.href = "index.html";
  }
}

function logout() {
  sessionStorage.removeItem(TOKEN_KEY);
  location.href = "index.html";
}

async function api(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = sessionStorage.getItem(TOKEN_KEY);
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  const response = await fetch(path, { ...options, headers });
  const body = await response.json().catch(() => ({}));
  if (response.status === 401) {
    logout();
  }
  if (!response.ok || body.success === false) {
    throw new Error(body.message || `요청 실패 (${response.status})`);
  }
  return body.data;
}

function cell(row, text) {
  const td = document.createElement("td");
  td.textContent = text ?? "";
  row.appendChild(td);
  return td;
}
