'use client'

import { useSidebar } from '@/components/ui/sidebar'
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from '@/components/ui/sidebar'
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible'
import { ChevronDown, Code2, Cpu, Zap, Settings } from 'lucide-react'
import Link from 'next/link'

interface SidebarNavItem {
  label: string
  href: string
  icon: React.ReactNode
  badge?: string
  items?: Array<{
    label: string
    href: string
    icon?: React.ReactNode
  }>
}

const navItems: SidebarNavItem[] = [
  {
    label: 'JavaScript',
    href: '/dashboard/javascript',
    icon: <Code2 className="w-4 h-4" />,
    items: [
      {
        label: 'Console',
        href: '/dashboard/javascript/console',
      },
      {
        label: 'Task Scheduler',
        href: '/dashboard/javascript/scheduler',
      },
      {
        label: 'Events',
        href: '/dashboard/javascript/events',
      },
      {
        label: 'Modules',
        href: '/dashboard/javascript/modules',
      },
    ],
  },
  {
    label: 'Server',
    href: '/dashboard/server',
    icon: <Cpu className="w-4 h-4" />,
    items: [
      {
        label: 'Performance',
        href: '/dashboard/server/performance',
      },
      {
        label: 'Players',
        href: '/dashboard/server/players',
      },
      {
        label: 'Worlds',
        href: '/dashboard/server/worlds',
      },
    ],
  },
  {
    label: 'Plugins',
    href: '/dashboard/plugins',
    icon: <Zap className="w-4 h-4" />,
  },
  {
    label: 'Settings',
    href: '/dashboard/settings',
    icon: <Settings className="w-4 h-4" />,
  },
]

export function DashboardSidebar() {
  const { state, toggleSidebar } = useSidebar()

  return (
    <div className="h-full flex flex-col">
      <div className="p-4 border-b">
        <button
          onClick={toggleSidebar}
          className="w-full flex items-center justify-between px-3 py-2 rounded-lg hover:bg-accent text-sm font-medium"
        >
          <span className={`transition-all ${state === 'collapsed' ? 'hidden' : ''}`}>
            Dashboard
          </span>
          <ChevronDown className={`w-4 h-4 transition-transform ${state === 'collapsed' ? 'rotate-180' : ''}`} />
        </button>
      </div>

      <SidebarMenu className="flex-1 px-2 py-4">
        {navItems.map((item) => (
          <div key={item.href}>
            {item.items ? (
              <Collapsible defaultOpen>
                <SidebarMenuItem>
                  <CollapsibleTrigger asChild>
                    <SidebarMenuButton className="data-[state=open]:bg-accent">
                      {item.icon}
                      <span className={`transition-all ${state === 'collapsed' ? 'hidden' : ''}`}>
                        {item.label}
                      </span>
                      <ChevronDown className="ml-auto w-4 h-4 transition-transform group-data-[state=open]:rotate-180" />
                    </SidebarMenuButton>
                  </CollapsibleTrigger>
                  <CollapsibleContent>
                    <SidebarMenuSub>
                      {item.items.map((subItem) => (
                        <SidebarMenuSubItem key={subItem.href}>
                          <SidebarMenuSubButton asChild>
                            <Link href={subItem.href} className="flex items-center gap-2">
                              {subItem.icon && subItem.icon}
                              <span className={`transition-all ${state === 'collapsed' ? 'hidden' : ''}`}>
                                {subItem.label}
                              </span>
                            </Link>
                          </SidebarMenuSubButton>
                        </SidebarMenuSubItem>
                      ))}
                    </SidebarMenuSub>
                  </CollapsibleContent>
                </SidebarMenuItem>
              </Collapsible>
            ) : (
              <SidebarMenuItem>
                <SidebarMenuButton asChild>
                  <Link href={item.href} className="flex items-center gap-2">
                    {item.icon}
                    <span className={`transition-all ${state === 'collapsed' ? 'hidden' : ''}`}>
                      {item.label}
                    </span>
                    {item.badge && (
                      <span className={`ml-auto text-xs px-2 py-1 rounded-full bg-primary text-primary-foreground ${state === 'collapsed' ? 'hidden' : ''}`}>
                        {item.badge}
                      </span>
                    )}
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            )}
          </div>
        ))}
      </SidebarMenu>

      <div className="p-4 border-t mt-auto">
        <div className={`text-xs text-muted-foreground text-center ${state === 'collapsed' ? 'hidden' : ''}`}>
          <p>WebX Dashboard</p>
          <p className="text-xs">v1.0.0</p>
        </div>
      </div>
    </div>
  )
}
