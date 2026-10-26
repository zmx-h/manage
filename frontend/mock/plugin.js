import { createMockMiddleware } from './server.js'

// This plugin is loaded by Vite only in `--mode mock`; it never enters browser / production bundles.
export function mockApi() {
  return {
    name: 'iecmp-local-mock',
    apply: 'serve',
    configureServer(server) { server.middlewares.use(createMockMiddleware()) },
    transformIndexHtml() {
      return [{ tag: 'script', attrs: { type: 'module', src: '/mock/toolbar.js' }, injectTo: 'body' }]
    }
  }
}
