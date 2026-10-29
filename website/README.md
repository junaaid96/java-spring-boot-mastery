# Docs website

The site at https://junaaid96.github.io/java-spring-boot-mastery/ is built with [VitePress](https://vitepress.dev) from the repo's own Markdown, so the module READMEs, `docs/` and the root `README.md` stay the single source of truth.

- `scripts/sync.mjs` copies those files into `content/` (git-ignored), rewrites links, and embeds each module's Java sources, resources and tests.
- `.vitepress/config.mts` holds the sidebar, search, Mermaid and SEO settings.
- `.github/workflows/pages.yml` builds and deploys on every push to `main` that touches the docs or sources.

```bash
cd website
npm install      # or npm ci once package-lock.json exists
npm run dev      # http://localhost:5173/java-spring-boot-mastery/
npm run build    # output in .vitepress/dist
```
