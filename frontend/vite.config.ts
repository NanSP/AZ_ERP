import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('react-router')) {
              return 'router'
            }

            if (id.includes('react')) {
              return 'react-vendor'
            }

            if (id.includes('axios')) {
              return 'data-vendor'
            }

            return 'vendor'
          }

          const chunkGroups = [
            ['src/pages/Core/', 'core-pages'],
            ['src/pages/Sys/', 'sys-pages'],
            ['src/pages/Fi/', 'fi-pages'],
            ['src/pages/Fiscal/', 'fiscal-pages'],
            ['src/pages/Am/', 'am-pages'],
            ['src/pages/Auditoria/', 'auditoria-pages'],
            ['src/pages/Bi/', 'bi-pages'],
            ['src/pages/Grc/', 'grc-pages'],
            ['src/pages/Mm/', 'mm-pages'],
            ['src/pages/Portal/', 'portal-pages'],
            ['src/pages/Ps/', 'ps-pages'],
            ['src/pages/Pp/', 'pp-pages'],
            ['src/pages/Sm/', 'sm-pages'],
            ['src/pages/Rh/', 'rh-pages'],
            ['src/pages/Qm/', 'qm-pages'],
            ['src/pages/Sd/', 'sd-pages'],
            ['src/pages/Module/', 'module-shell'],
            ['src/layouts/', 'layout-shell'],
            ['src/auth/', 'auth-shell'],
            ['src/components/ModuleCrud/', 'module-crud'],
          ] as const

          for (const [pattern, chunkName] of chunkGroups) {
            if (id.includes(pattern)) {
              return chunkName
            }
          }
        },
      },
    },
  },
})
