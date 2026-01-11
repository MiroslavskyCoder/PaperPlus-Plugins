"use client";

import { useEffect, useState, useRef } from 'react';
import { useDashboard } from './dashboard-context';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { ThemeToggle } from "@/components/ui/theme-toggle";
import { Button } from "@/components/ui/button";
import { Drawer, DrawerContent, DrawerHeader, DrawerTitle, DrawerTrigger, DrawerClose } from "@/components/ui/drawer";
import { Menu, X, Home, Server, Users, DollarSign, ShoppingCart, Clock, Palette, Power, Heart, Skull, Bug, Utensils, Map, Settings as SettingsIcon, Plug, Sliders } from "lucide-react";
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
  { id: 'settings', label: 'Settings', icon: SettingsIcon },
];

export default function Dashboard() {
  const { stats, statsHistory, players, entities, mapImage, chartData, fetchPlayers, fetchMap, fetchChartData, runCommand } = useDashboard();
  const [activeTab, setActiveTab] = useState('home');
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

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
      case 'settings': return <SettingsTab />;
      default: return <HomeTab stats={stats} />;
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-muted/20 text-foreground">
      <div className="container mx-auto p-4 md:p-8">
        <div className="mb-8 text-center animate-fade-in">
          <div className="flex items-center justify-between mb-4">
            <Drawer open={isDrawerOpen} onOpenChange={setIsDrawerOpen}>
              <DrawerTrigger asChild>
                <Button variant="outline" size="icon" className="md:hidden">
                  <Menu className="h-5 w-5" />
                </Button>
              </DrawerTrigger>
              <DrawerContent className="h-[80vh]">
                <DrawerHeader className="border-b">
                  <div className="flex items-center justify-between">
                    <DrawerTitle>Navigation</DrawerTitle>
                    <DrawerClose asChild>
                      <Button variant="ghost" size="icon">
                        <X className="h-5 w-5" />
                      </Button>
                    </DrawerClose>
                  </div>
                </DrawerHeader>
                <div className="p-4 overflow-y-auto">
                  <nav className="space-y-2">
                    {menuItems.map((item) => {
                      const Icon = item.icon;
                      return (
                        <Button
                          key={item.id}
                          variant={activeTab === item.id ? "default" : "ghost"}
                          className="w-full justify-start gap-2"
                          onClick={() => {
                            setActiveTab(item.id);
                            setIsDrawerOpen(false);
                          }}
                        >
                          <Icon className="h-4 w-4" />
                          {item.label}
                        </Button>
                      );
                    })}
                  </nav>
                </div>
              </DrawerContent>
            </Drawer>

            <div className="flex-1">
              <h1 className="text-2xl md:text-4xl font-bold bg-gradient-to-r from-primary via-primary/80 to-primary/60 bg-clip-text text-transparent mb-2">
                Minecraft Server Control Panel
              </h1>
              <p className="text-sm md:text-base text-muted-foreground">Monitor and manage your Minecraft server in real-time</p>
            </div>

            <div className="flex items-center gap-2 md:gap-4">
              <ThemeToggle />
              <div className="hidden md:flex items-center gap-2 px-3 py-1 bg-green-500/10 text-green-500 rounded-full text-sm font-medium border border-green-500/20">
                <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
                Connected
              </div>
            </div>
          </div>

          {/* Desktop Navigation */}
          <div className="hidden md:flex gap-2 flex-wrap justify-center mt-6">
            {menuItems.map((item) => {
              const Icon = item.icon;
              return (
                <Button
                  key={item.id}
                  variant={activeTab === item.id ? "default" : "outline"}
                  size="sm"
                  className="gap-2"
                  onClick={() => setActiveTab(item.id)}
                >
                  <Icon className="h-4 w-4" />
                  {item.label}
                </Button>
              );
            })}
          </div>
        </div>

        {/* Content */}
        <div className="space-y-6 animate-fade-in">
          {renderContent()}
        </div>
      </div>
    </div>
  );
}

