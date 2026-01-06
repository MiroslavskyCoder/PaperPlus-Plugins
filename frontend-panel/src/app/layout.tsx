import type { Metadata } from 'next'
import { Inter } from 'next/font/google'
import { DashboardProvider } from './dashboard-context'
import { ThemeProvider } from "next-themes"
import './globals.css'

const inter = Inter({ subsets: ['latin'] })

export const metadata: Metadata = {
  title: 'Minecraft Web Panel',
  description: 'Web panel for Minecraft server management',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <head>
        <meta name="referrer" content="no-referrer" /> 
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" />
        <link
          href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100..900;1,100..900&family=Stack+Sans+Headline:wght@200..700&display=swap"
          rel="stylesheet"
        /> 
      </head>
      <body className={inter.className}>
        <ThemeProvider attribute="class">
          <DashboardProvider>{children}</DashboardProvider>
        </ThemeProvider> 
      </body>
    </html>
  )
}