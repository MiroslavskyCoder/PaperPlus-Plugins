"use client";

import { Tabs, TabsContent, TabsContents, TabsList, TabsTrigger } from '@/components/animate-ui/components/radix/tabs';
import { useEffect, useState, useRef } from 'react';
import { useDashboard } from './dashboard-context';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { ThemeToggle } from "@/components/ui/theme-toggle";
import { Button } from "@/components/ui/button";
import { StatCard } from '@/components/dashboard/stat-card';
import { ChartsGrid } from '@/components/dashboard/charts-grid';
import { HomeTab } from '@/components/dashboard/home-tab';
import { ServerTab } from '@/components/dashboard/server-tab';
import { PlayersTab } from '@/components/dashboard/players-tab';
import { MapTab } from '@/components/dashboard/map-tab';
import { ConfigTab } from '@/components/dashboard/config-tab';
import { PluginsTab } from '@/components/dashboard/plugins-tab';
import { SettingsTab } from '@/components/dashboard/settings-tab';

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

export default function Dashboard() {
  const { stats, statsHistory, players, entities, plugins, socket, mapImage, chartData, fetchPlayers, fetchMap, fetchChartData, runCommand } = useDashboard();

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-background to-muted/20 text-foreground">
      <div className="container mx-auto p-8">
        <div className="mb-8 text-center animate-fade-in">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-primary via-primary/80 to-primary/60 bg-clip-text text-transparent mb-2">
            Minecraft Server Control Panel
          </h1>
          <p className="text-muted-foreground">Monitor and manage your Minecraft server in real-time</p>
          <div className="flex justify-center items-center gap-4 mt-4">
            <ThemeToggle />
            <div className="flex items-center gap-2 px-3 py-1 bg-green-500/10 text-green-500 rounded-full text-sm font-medium border border-green-500/20">
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              Connected
            </div>
          </div>
        </div>

        <Tabs defaultValue="home" className="w-full">
          <TabsList className="grid w-5/6 grid-cols-7 mb-8 bg-card/50 backdrop-blur-sm border-none">
            <TabsTrigger value="home" className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground">Home</TabsTrigger>
            <TabsTrigger value="server" className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground">Server</TabsTrigger>
            <TabsTrigger value="players" className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground">Players</TabsTrigger>
            <TabsTrigger value="map" className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground">Map</TabsTrigger>
            <TabsTrigger value="config" className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground">Config</TabsTrigger>
            <TabsTrigger value="plugins" className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground">Plugins</TabsTrigger>
            <TabsTrigger value="settings" className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground">Settings</TabsTrigger>
          </TabsList>

          <TabsContents className="space-y-6">
            <TabsContent value="home" className="space-y-6 animate-fade-in">
              <HomeTab stats={stats} />
            </TabsContent>

            <TabsContent value="server" className="space-y-6 animate-fade-in">
              <ServerTab stats={stats} />
            </TabsContent>

            <TabsContent value="players" className="space-y-6 animate-fade-in">
              <PlayersTab players={players} />
            </TabsContent>

            <TabsContent value="map" className="space-y-6 animate-fade-in">
              <MapTab entities={entities} mapImage={mapImage} />
            </TabsContent>

            <TabsContent value="config" className="space-y-6 animate-fade-in">
              <ConfigTab runCommand={runCommand} />
            </TabsContent>

            <TabsContent value="plugins" className="space-y-6 animate-fade-in">
              <PluginsTab plugins={plugins} />
            </TabsContent>

            <TabsContent value="settings" className="space-y-6 animate-fade-in">
              <SettingsTab/>
            </TabsContent>
          </TabsContents>
        </Tabs>
      </div>
    </div>
  );
}

