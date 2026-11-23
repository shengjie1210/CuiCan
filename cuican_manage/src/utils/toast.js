let host = null;
function getHost() {
  if (typeof document === "undefined") return null;
  if (host) return host;
  host = document.createElement("div");
  host.id = "manage-toast-host";
  host.style.cssText = "position:fixed;bottom:24px;left:50%;transform:translateX(-50%);z-index:9999999;display:flex;flex-direction:column;gap:10px;pointer-events:none;";
  document.body.appendChild(host);
  return host;
}

function show(msg, type) {
  const h = getHost();
  if (!h) return;
  const color = type === "error" ? "#dc3545" : type === "success" ? "#198754" : "#0d6efd";
  const el = document.createElement("div");
  el.textContent = msg;
  el.style.cssText = "background:" + color + ";color:#fff;padding:12px 28px;border-radius:10px;box-shadow:0 8px 30px rgba(0,0,0,.2);min-width:200px;max-width:80vw;font-size:14px;font-weight:500;pointer-events:auto;opacity:0;transition:opacity .3s ease;";
  h.appendChild(el);
  requestAnimationFrame(() => { el.style.opacity = "1"; });
  setTimeout(() => {
    el.style.opacity = "0";
    setTimeout(() => el.remove(), 350);
  }, 3000);
}

export const toast = {
  success: (m) => show(m, "success"),
  error: (m) => show(m, "error"),
  info: (m) => show(m, "info"),
};

if (typeof window !== "undefined") window.showToast = show;
