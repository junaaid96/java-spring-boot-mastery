// Generates website/content/ from the repo's Markdown (single source of truth).
// Never edit website/content by hand: it is wiped and rebuilt on every build.
const GH = 'https://github.com/junaaid96/java-spring-boot-mastery';
const RAW = 'https://raw.githubusercontent.com/junaaid96/java-spring-boot-mastery/main';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const ROOT = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '../..');
const OUT = path.resolve(ROOT, 'website/content');
fs.rmSync(OUT, { recursive: true, force: true });
fs.mkdirSync(OUT, { recursive: true });

const rel = (p) => path.relative(ROOT, p).split(path.sep).join('/');
const read = (p) => fs.readFileSync(path.join(ROOT, p), 'utf8');
const pages = new Map(); // repo-relative source -> site route

function resolveLink(fromRel, href, isImage) {
  if (/^([a-z]+:|#|\/)/i.test(href)) return href;
  const [p, hash = ''] = href.split('#');
  const target = path.posix.normalize(path.posix.join(path.posix.dirname(fromRel), p)).replace(/\/$/, '');
  const h = hash ? `#${hash}` : '';
  if (isImage) return `${RAW}/${target}`;
  for (const cand of [target, `${target}/README.md`]) if (pages.has(cand)) return `/${pages.get(cand)}${h}`;
  if (target === 'README.md' || target === '.') return `/${h}`;
  const abs = path.join(ROOT, target);
  const kind = fs.existsSync(abs) && fs.statSync(abs).isDirectory() ? 'tree' : 'blob';
  return `${GH}/${kind}/main/${target}${h}`;
}

// Rewrite links outside fenced code blocks.
function rewrite(md, fromRel) {
  let inFence = false;
  return md.split('\n').map((line) => {
    if (/^\s*(```|~~~)/.test(line)) { inFence = !inFence; return line; }
    if (inFence) return line;
    return line.replace(/(!?)\[([^\]]*)\]\(([^)\s]+)\)/g, (_, bang, t, h) => `${bang}[${t}](${resolveLink(fromRel, h, !!bang)})`);
  }).join('\n');
}

const fm = (o) => `---\n${Object.entries(o).map(([k, v]) => `${k}: ${JSON.stringify(v)}`).join('\n')}\n---\n\n`;
const firstHeading = (md) => (md.match(/^#\s+(.+)$/m) || [, ''])[1].trim();
const plain = (s) => s.replace(/[*_`]/g, '').replace(/\[([^\]]*)\]\([^)]*\)/g, '$1');
const firstPara = (md) => {
  const para = md.split('\n\n').find((b) => b.trim() && !/^(#|>|\||```|!\[|\[!\[|-)/.test(b.trim()));
  return plain((para || '').replace(/\s+/g, ' ').trim()).slice(0, 180);
};

function walk(dir, exts) {
  const abs = path.join(ROOT, dir);
  if (!fs.existsSync(abs)) return [];
  return fs.readdirSync(abs, { withFileTypes: true }).sort((a, b) => a.name.localeCompare(b.name))
    .flatMap((e) => {
      const p = `${dir}/${e.name}`;
      if (e.isDirectory()) return walk(p, exts);
      return exts.some((x) => e.name.endsWith(x)) ? [p] : [];
    });
}

const LANG = { '.java': 'java', '.cpp': 'cpp', '.xml': 'xml', '.yaml': 'yaml', '.yml': 'yaml', '.properties': 'properties', '.sql': 'sql', '.txt': 'text' };
function sourceBlocks(files, intro) {
  if (!files.length) return '';
  let s = `\n\n## Source code\n\n${intro}\n`;
  for (const r of files) {
    const test = r.includes('/src/test/');
    const lang = LANG[path.extname(r)] || 'text';
    const badge = test ? '<Badge type="tip" text="test" />' : `<Badge type="info" text="${lang === 'cpp' ? 'C++' : lang === 'java' ? 'source' : lang}" />`;
    const code = read(r).replace(/\n$/, '');
    const fence = code.includes('```') ? '````' : '```';
    s += `\n::: details ${r.split('/').slice(-1)[0]} ${badge}\n[View on GitHub](${GH}/blob/main/${r})\n\n${fence}${lang}\n${code}\n${fence}\n:::\n`;
  }
  return s;
}

function emit(route, srcRel, body, extra = {}) {
  const file = path.join(OUT, route === '' ? 'index.md' : route.endsWith('/') ? `${route}index.md` : `${route}.md`);
  fs.mkdirSync(path.dirname(file), { recursive: true });
  fs.writeFileSync(file, fm({ source: srcRel, ...extra }) + body + '\n');
}

// Root README body without title, badges and its own "Website" section.
function readmeBody() {
  return read('README.md').split('\n').filter((l, i) => !(i === 0 && l.startsWith('# ')) && !/^\s*(\[!\[|!\[)/.test(l))
    .join('\n').replace(/^## Website[\s\S]*?(?=^## )/m, '');
}

const modules = fs.readdirSync(ROOT).filter((d) => /^\d\d-/.test(d) && fs.existsSync(path.join(ROOT, d, 'README.md'))).sort();
for (const m of modules) pages.set(`${m}/README.md`, `modules/${m}`);
pages.set('docs/BEST_PRACTICES.md', 'guides/best-practices');
pages.set('CONTRIBUTING.md', 'guides/contributing');

const sidebar = { modules: [], guides: [] };
for (const m of modules) {
  const srcRel = `${m}/README.md`;
  const md = read(srcRel);
  const title = plain(firstHeading(md));
  const files = [
    ...walk(`${m}/src/main/java`, ['.java']),
    ...walk(`${m}/src/main/resources`, ['.yaml', '.yml', '.properties', '.sql']),
    ...walk(`${m}/src/test/java`, ['.java']),
  ];
  const body = rewrite(md, srcRel) + sourceBlocks(files, `Every Java file in this module, embedded straight from the repo with its learning comments. Browse the module on [GitHub](${GH}/tree/main/${m}).`);
  emit(`modules/${m}`, srcRel, body, { title: title.replace(/^\d\d · /, ''), description: firstPara(md) });
  sidebar.modules.push({ num: m.slice(0, 2), text: title.replace(' · ', '. '), link: `/modules/${m}` });
}
for (const [srcRel, route] of pages) {
  if (!route.startsWith('guides/')) continue;
  const md = read(srcRel);
  emit(route, srcRel, rewrite(md, srcRel), { title: plain(firstHeading(md)), description: firstPara(md) });
  sidebar.guides.push({ text: plain(firstHeading(md)), link: `/${route}` });
}

const body = readmeBody();
const home = fm({ layout: 'home', source: 'README.md', title: 'Java Spring Boot Mastery', description: firstPara(body) })
  .replace(/---\n$/, `hero:
  name: "Java Spring Boot Mastery"
  text: "From core Java 25 to a production Spring Boot 4 API"
  tagline: "Six Maven modules in study order. Every module explains the concepts, the most asked interview questions, best practices and common pitfalls, with the commented source inline."
  actions:
    - theme: brand
      text: Start with Java core
      link: /modules/01-java-core
    - theme: alt
      text: Best practices
      link: /guides/best-practices
    - theme: alt
      text: View on GitHub
      link: ${GH}
features:
  - title: Java foundations
    details: Records, sealed types, pattern matching, streams, generics, virtual threads and the Collections Framework.
    link: /modules/01-java-core
  - title: Design
    details: OOP pillars, SOLID with violation and fix, and the GoF patterns Spring uses.
    link: /modules/03-oop-and-solid
  - title: The web stack underneath
    details: Jakarta Servlets, then Spring MVC wired by hand with no Boot.
    link: /modules/04-servlet-basics
  - title: Spring Boot 4 API
    details: Validation, ProblemDetail, JPA + PostgreSQL, JWT security, OpenAPI, Actuator, caching and Testcontainers.
    link: /modules/06-spring-boot
---
`);
fs.writeFileSync(path.join(OUT, 'index.md'), home + '\n' + rewrite(body, 'README.md') + '\n');
fs.writeFileSync(path.resolve(ROOT, 'website/.vitepress/sidebar.generated.json'), JSON.stringify(sidebar, null, 2));
console.log(`synced ${modules.length} modules, ${sidebar.guides.length} guides`);
