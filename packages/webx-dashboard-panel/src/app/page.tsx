"use client";

import { useEffect, useState, useRef } from 'react';
import { useDashboard } from './dashboard-context';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { ThemeToggle } from "@/components/ui/theme-toggle";
import { Button } from "@/components/ui/button";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarInset,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { Home, Server, Users, DollarSign, ShoppingCart, Clock, Palette, Power, Heart, Skull, Bug, Utensils, Map, Settings as SettingsIcon, Plug, Sliders, Code2, Sparkles, Radio, CalendarClock, FileCode2 } from "lucide-react";
import { StatCard } from '@/components/dashboard/stat-card';
import { ChartsGrid } from '@/components/dashboard/charts-grid';
import { HomeTab } from '@/components/dashboard/home-tab';
import { ServerTab } from '@/components/dashboard/server-tab';
import { PlayersTab } from '@/components/dashboard/players-tab';
import { MapTab } from '@/components/dashboard/map-tab';
import { ConfigTab } from '@/components/dashboard/config-tab';
import { PluginsTab } from '@/components/dashboard/plugins-tab';
import { SettingsTab } from '@/components/dashboard/settings-tab';
import { EconomyTab } from '@/components/dashboard/economy-tab';
import { ShopTab } from '@/components/dashboard/shop-tab';
import { AfkTab } from '@/components/dashboard/afk-tab';
import { WorldColorsTab } from '@/components/dashboard/worldcolors-tab';
import { AutoShutdownTab } from '@/components/dashboard/autoshutdown-tab';
import { SimpleHealTab } from '@/components/dashboard/simpleheal-tab';
import { DeathMessageTab } from '@/components/dashboard/deathmessage-tab';
import { MobCatchTab } from '@/components/dashboard/mobcatch-tab';
import { FriendFeedTab } from '@/components/dashboard/friendfeed-tab';
import { ScriptConsoleTab } from '@/components/dashboard/script-console-tab';
import { EventSystemTab } from '@/components/dashboard/event-system-tab';
import { TaskSchedulerTab } from '@/components/dashboard/task-scheduler-tab';
import { ScriptTranspilerTab } from '@/components/dashboard/script-transpiler-tab';
import { ScriptLoaderTab } from '@/components/dashboard/script-loader-tab';

interface Stats {
  cpu: number;
  memUsed: number;
  memMax: number;
  diskUsed: number;
  diskTotal: number;
  players: number;
}

interface Player {
  name: string;
  uuid: string;
  health: number;
  x: number;
  y: number;
  z: number;
}

interface Entity {
  type: string;
  name: string;
  x: number;
  y: number;
  z: number;
}

const menuItems = [
  { id: 'home', label: 'Home', icon: Home },
  { id: 'server', label: 'Server', icon: Server },
  { id: 'players', label: 'Players', icon: Users },
  { id: 'economy', label: 'Economy', icon: DollarSign },
  { id: 'shop', label: 'Shop', icon: ShoppingCart },
  { id: 'afk', label: 'AFK', icon: Clock },
  { id: 'worldcolors', label: 'Colors', icon: Palette },
  { id: 'autoshutdown', label: 'Shutdown', icon: Power },
  { id: 'simpleheal', label: 'Heal', icon: Heart },
  { id: 'deathmessage', label: 'Death', icon: Skull },
  { id: 'mobcatch', label: 'Mobs', icon: Bug },
  { id: 'friendfeed', label: 'Feed', icon: Utensils },
  { id: 'map', label: 'Map', icon: Map },
  { id: 'config', label: 'Config', icon: Sliders },
  { id: 'plugins', label: 'Plugins', icon: Plug },
  { id: 'scripts', label: 'Scripts', icon: FileCode2 },
  { id: 'script-console', label: 'JS Console', icon: Code2 },
  { id: 'script-transpiler', label: 'TS/JSX', icon: Sparkles },
  { id: 'events', label: 'Events', icon: Radio },
  { id: 'scheduler', label: 'Scheduler', icon: CalendarClock },
  { id: 'settings', label: 'Settings', icon: SettingsIcon },
];

export default function Dashboard() {
  const { stats, statsHistory, players, entities, mapImage, chartData, fetchPlayers, fetchMap, fetchChartData, runCommand } = useDashboard();
  const [activeTab, setActiveTab] = useState('home');

  const renderContent = () => {
    switch (activeTab) {
      case 'home': return <HomeTab stats={stats} />;
      case 'server': return <ServerTab stats={stats} />;
      case 'players': return <PlayersTab players={players} />;
      case 'economy': return <EconomyTab />;
      case 'shop': return <ShopTab />;
      case 'afk': return <AfkTab />;
      case 'worldcolors': return <WorldColorsTab />;
      case 'autoshutdown': return <AutoShutdownTab />;
      case 'simpleheal': return <SimpleHealTab />;
      case 'deathmessage': return <DeathMessageTab />;
      case 'mobcatch': return <MobCatchTab />;
      case 'friendfeed': return <FriendFeedTab />;
      case 'map': return <MapTab entities={entities} mapImage={mapImage} />;
      case 'config': return <ConfigTab runCommand={runCommand} />;
      case 'plugins': return <PluginsTab />;
      case 'scripts': return <ScriptLoaderTab />;
      case 'script-console': return <ScriptConsoleTab />;
      case 'script-transpiler': return <ScriptTranspilerTab />;
      case 'events': return <EventSystemTab />;
      case 'scheduler': return <TaskSchedulerTab />;
      case 'settings': return <SettingsTab />;
      default: return <HomeTab stats={stats} />;
    }
  };

  return (
    <SidebarProvider defaultOpen={true}>
      <Sidebar>
        <SidebarContent className="pt-6">
          <SidebarGroup>
            <div className="px-4 mb-4">
              <h2 className="text-lg font-bold text-primary">MC Panel</h2>
              <p className="text-xs text-muted-foreground">Server Manager</p>
            </div>
          </SidebarGroup>

          <SidebarGroup>
            <SidebarGroupContent>
              <SidebarMenu>
                {menuItems.map((item) => {
                  const Icon = item.icon;
                  return (
                    <SidebarMenuItem key={item.id}>
                      <SidebarMenuButton
                        asChild
                        isActive={activeTab === item.id}
                        onClick={() => setActiveTab(item.id)}
                        className="cursor-pointer"
                      >
                        <span>
                          <Icon className="h-4 w-4" />
                          <span>{item.label}</span>
                        </span>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  );
                })}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>

          <div className="mt-auto px-4 py-4 border-t border-sidebar-border space-y-3">
            <div className="flex items-center gap-2 px-3 py-2 bg-green-500/10 text-green-500 rounded-lg text-xs font-medium border border-green-500/20">
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              Connected
            </div>
            <ThemeToggle />
          </div>
        </SidebarContent>
      </Sidebar>

      <SidebarInset>
        {/* Top Header */}
        <header className="sticky top-0 bg-background/95 backdrop-blur-sm border-b border-border z-20 flex items-center justify-between p-4 md:p-6">
          <div className="flex items-center gap-4">
            <SidebarTrigger className="hidden lg:flex" />
            <div>
              <h1 className="text-2xl md:text-3xl font-bold bg-gradient-to-r from-primary via-primary/80 to-primary/60 bg-clip-text text-transparent">
                Minecraft Server Control Panel
              </h1>
              <p className="text-xs md:text-sm text-muted-foreground">Monitor and manage your Minecraft server in real-time</p>
            </div>
          </div>
          <ThemeToggle />
        </header>

        {/* Page Content */}
        <div className="flex-1 overflow-y-auto p-4 md:p-8">
          <div className="space-y-6 animate-fade-in">
            {renderContent()}
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  );
}

