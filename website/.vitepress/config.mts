import { defineConfig } from 'vitepress';
import { withMermaid } from 'vitepress-plugin-mermaid';
import sidebarData from './sidebar.generated.json' with { type: 'json' };

const GH = 'https://github.com/junaaid96/java-spring-boot-mastery';
const SITE = 'https://junaaid96.github.io/java-spring-boot-mastery/';
const group = (text: string, from: number, to: number) => ({
  text,
  collapsed: false,
  items: sidebarData.modules.filter((t) => +t.num >= from && +t.num <= to).map(({ text, link }) => ({ text, link })),
});

export default withMermaid(defineConfig({
  srcDir: 'content',
  base: '/java-spring-boot-mastery/',
  lang: 'en-US',
  title: 'Java Spring Boot Mastery',
  titleTemplate: ':title · Java Spring Boot Mastery',
  description: 'Learn core Java 25, collections, OOP and SOLID, Servlets, Spring MVC and Spring Boot 4, with commented code, interview questions, best practices and pitfalls.',
  cleanUrls: true,
  ignoreDeadLinks: 'localhostLinks',
  lastUpdated: true,
  sitemap: { hostname: SITE },
  head: [
    ['meta', { name: 'theme-color', content: '#3c8772' }],
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:site_name', content: 'Java Spring Boot Mastery' }],
    ['meta', { name: 'twitter:card', content: 'summary' }],
  ],
  transformPageData(page) {
    const route = page.relativePath.replace(/(^|\/)index\.md$/, '$1').replace(/\.md$/, '');
    const title = page.frontmatter.title || page.title || 'Java Spring Boot Mastery';
    const desc = page.frontmatter.description || page.description || '';
    page.frontmatter.head ??= [];
    page.frontmatter.head.push(
      ['link', { rel: 'canonical', href: SITE + route }],
      ['meta', { property: 'og:title', content: title }],
      ['meta', { property: 'og:url', content: SITE + route }],
      ['meta', { property: 'og:description', content: desc }],
    );
  },
  markdown: { lineNumbers: true },
  themeConfig: {
    nav: [
      { text: 'Study order', link: '/' },
      { text: 'Modules', link: '/modules/01-java-core' },
      { text: 'Spring Boot API', link: '/modules/06-spring-boot' },
      { text: 'Best practices', link: '/guides/best-practices' },
    ],
    sidebar: [
      group('Java foundations', 1, 2),
      group('Design', 3, 3),
      group('Web fundamentals', 4, 5),
      group('Spring Boot', 6, 6),
      { text: 'Guides', collapsed: false, items: sidebarData.guides },
    ],
    socialLinks: [{ icon: 'github', link: GH }],
    search: { provider: 'local' },
    outline: { level: [2, 3] },
    editLink: {
      pattern: ({ frontmatter }) => `https://github.com/junaaid96/java-spring-boot-mastery/blob/main/${frontmatter.source ?? ''}`,
      text: 'View source on GitHub',
    },
    footer: { message: 'Released under the MIT License.', copyright: '© 2026 Md. Junaidul Islam' },
  },
  mermaid: { theme: 'default' },
}));
