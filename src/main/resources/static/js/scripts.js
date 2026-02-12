document.addEventListener("click", (e) => {
    const btn = e.target.closest(".pwd-toggle");
    if (!btn) return;

    const wrap = btn.closest(".pwd-wrap");
    const input = wrap?.querySelector("input");
    if (!input) return;

    const isPassword = input.type === "password";
    input.type = isPassword ? "text" : "password";

    btn.setAttribute("aria-label", isPassword ? "Hide password" : "Show password");
    btn.textContent = isPassword ? "🙈" : "👁";
});
