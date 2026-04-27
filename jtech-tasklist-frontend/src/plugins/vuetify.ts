import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'

import { createVuetify } from 'vuetify'

export const vuetify = createVuetify({
  theme: {
    defaultTheme: 'tasklist',
    themes: {
      tasklist: {
        dark: false,
        colors: {
          primary: '#1f6feb',
          secondary: '#4d6b99',
          accent: '#ff8c42',
          background: '#f4f7fb',
          surface: '#ffffff',
          error: '#b3261e',
          success: '#0f9d58',
          warning: '#f9ab00',
          info: '#1a73e8',
        },
      },
    },
  },
})
