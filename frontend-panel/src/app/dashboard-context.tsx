"use client";

import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import { io, Socket } from 'socket.io-client';

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

interface Plugin {
  name: string;
  version: string;
  description: string;
  author: string;
  status: 'enabled' | 'disabled';
}

interface DashboardContextType {
  stats: Stats | null;
  statsHistory: Stats[];
  players: Player[];
  entities: Entity[];
  plugins: Plugin[];
  socket: Socket | null;
  mapImage: string;
  chartData: {
    cpu: number[];
    memory: number[];
    players: number[];
    disk: number[];
    timestamps: number[];
  };
  fetchPlayers: () => Promise<void>;
  fetchMap: () => Promise<void>;
  fetchChartData: () => Promise<void>;
  runCommand: (cmd: string) => Promise<void>;
}

const DashboardContext = createContext<DashboardContextType | undefined>(undefined);

export const useDashboard = () => {
  const context = useContext(DashboardContext);
  if (!context) {
    throw new Error('useDashboard must be used within a DashboardProvider');
  }
  return context;
};

export const DashboardProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [stats, setStats] = useState<Stats | null>(null);
  const [statsHistory, setStatsHistory] = useState<Stats[]>([]);
  const [players, setPlayers] = useState<Player[]>([]);
  const [entities, setEntities] = useState<Entity[]>([]);
  const [plugins, setPlugins] = useState<Plugin[]>([]);
  const [socket, setSocket] = useState<Socket | null>(null);
  const [mapImage, setMapImage] = useState<string>('');
  const [chartData, setChartData] = useState({
    cpu: [25, 30, 45, 50, 35, 40, 55, 60, 45, 50],
    memory: [60, 65, 70, 75, 80, 85, 90, 95, 85, 80],
    players: [5, 8, 12, 15, 10, 7, 9, 14, 11, 8],
    disk: [45, 50, 55, 60, 65, 70, 75, 80, 85, 90],
    timestamps: Array.from({ length: 10 }, (_, i) => Date.now() - (9 - i) * 1000),
  });

  useEffect(() => {
    // Connect to Socket.IO
    const newSocket = io('http://localhost:9092');
    setSocket(newSocket);

    newSocket.on('connect', () => console.log('Connected to server'));
    newSocket.on('disconnect', () => console.log('Disconnected'));

    // Listen for metrics updates
    newSocket.on('metrics', (data) => {
      setStats({
        cpu: data.cpuUsage,
        memUsed: data.memoryUsage * 100, // assuming percentage
        memMax: 100,
        diskUsed: 50, // placeholder
        diskTotal: 100,
        players: data.onlinePlayers,
      });
      // Update history
      setStatsHistory(prev => [...prev.slice(-9), {
        cpu: data.cpuUsage,
        memUsed: data.memoryUsage * 100,
        memMax: 100,
        diskUsed: 50,
        diskTotal: 100,
        players: data.onlinePlayers,
      }]);
    }); 

    // Listen for map updates
    newSocket.on('map', (data: string) => {
      try {
        const parsed = JSON.parse(data);
        if (parsed.type === 'map') {
          setMapImage(parsed.image);
        }
      } catch (e) { console.error(e); }
    });

    // Listen for player connect
    newSocket.on('connecting_success', (data: string) => {
      try {
        const parsed = JSON.parse(data);
        if (parsed.type === 'connecting_success') {
          console.log('Player connected:', parsed.playerdata);
          // Optionally update players list
          fetchPlayers();
        }
      } catch (e) { console.error(e); }
    });

    // Fetch initial data
    fetchPlayers();
    fetchMap();

    return () => {
      newSocket.disconnect();
    };
  }, []);

  // Initialize plugins data
  useEffect(() => {
    setPlugins([
      { name: 'WorldEdit', version: '7.2.15', description: 'In-game map editor and world manipulation tool', status: 'enabled', author: 'EngineHub' },
      { name: 'WorldGuard', version: '7.0.8', description: 'World protection and region management', status: 'enabled', author: 'EngineHub' },
      { name: 'Essentials', version: '2.20.1', description: 'Essential commands and features for servers', status: 'enabled', author: 'Essentials Team' },
      { name: 'LuckPerms', version: '5.4.102', description: 'Advanced permissions management system', status: 'enabled', author: 'Luck' },
      { name: 'Vault', version: '1.7.3', description: 'Economy and permissions API', status: 'enabled', author: 'Sleaker' },
      { name: 'PlaceholderAPI', version: '2.11.5', description: 'Placeholder system for other plugins', status: 'enabled', author: 'HelpChat' },
    ]);
  }, []);

  const fetchPlayers = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/players', { headers: { 'X-API-KEY': 'secret-change-me' } });
      if (res.ok) {
        const data = await res.json();
        setPlayers(data);
      }
    } catch (e) { console.error(e); }
  };

  const fetchMap = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/map', { headers: { 'X-API-KEY': 'secret-change-me' } });
      if (res.ok) {
        const data = await res.json();
        setEntities(data);
      }
    } catch (e) { console.error(e); }
  };

  const fetchChartData = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/stats/history', { headers: { 'X-API-KEY': 'secret-change-me' } });
      if (res.ok) {
        const data = await res.json();
        setChartData({
          cpu: data.map((s: Stats) => s.cpu),
          memory: data.map((s: Stats) => (s.memUsed / s.memMax) * 100),
          players: data.map((s: Stats) => s.players),
          disk: data.map((s: Stats) => (s.diskUsed / s.diskTotal) * 100),
          timestamps: data.map((s: Stats, i: number) => Date.now() - (data.length - 1 - i) * 1000),
        });
      }
    } catch (e) { console.error(e); }
  };

  const runCommand = async (cmd: string) => {
    try {
      await fetch('http://localhost:8080/api/command?cmd=' + encodeURIComponent(cmd), {
        method: 'POST',
        headers: { 'X-API-KEY': 'secret-change-me' }
      });
    } catch (e) { console.error(e); }
  };

  return (
    <DashboardContext.Provider value={{
      stats,
      statsHistory,
      players,
      entities,
      plugins,
      socket,
      mapImage,
      chartData,
      fetchPlayers,
      fetchMap,
      fetchChartData,
      runCommand
    }}>
      {children}
    </DashboardContext.Provider>
  );
};