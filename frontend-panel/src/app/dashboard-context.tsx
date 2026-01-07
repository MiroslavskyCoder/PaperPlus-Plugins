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
    // Connect to WebSocket for real-time metrics
    const wsUrl = process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:9092/metrics';
    
    try {
      const ws = new WebSocket(wsUrl);
      
      ws.onopen = () => {
        console.log('✅ Connected to metrics WebSocket');
      };
      
      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          
          // Update current stats
          setStats({
            cpu: data.cpuUsage,
            memUsed: data.memUsed,
            memMax: data.memMax,
            diskUsed: data.diskUsed,
            diskTotal: data.diskTotal,
            players: data.onlinePlayers,
          });
          
          // Update stats history for charts (keep last 30 data points = 60 seconds)
          setStatsHistory(prev => {
            const updated = [...prev, {
              cpu: data.cpuUsage,
              memUsed: data.memUsed,
              memMax: data.memMax,
              diskUsed: data.diskUsed,
              diskTotal: data.diskTotal,
              players: data.onlinePlayers,
            }];
            return updated.slice(-30); // Keep last 30 points
          });
          
          // Update chart data
          setChartData(prev => {
            const newCpu = [...prev.cpu, data.cpuUsage].slice(-30);
            const newMemory = [...prev.memory, (data.memUsed / data.memMax) * 100].slice(-30);
            const newPlayers = [...prev.players, data.onlinePlayers].slice(-30);
            const newDisk = [...prev.disk, (data.diskUsed / data.diskTotal) * 100].slice(-30);
            const newTimestamps = [...prev.timestamps, data.timestamp].slice(-30);
            
            return {
              cpu: newCpu,
              memory: newMemory,
              players: newPlayers,
              disk: newDisk,
              timestamps: newTimestamps,
            };
          });
          
          console.log('📊 Metrics updated:', {
            cpu: data.cpuUsage.toFixed(2) + '%',
            memory: ((data.memUsed / data.memMax) * 100).toFixed(2) + '%',
            players: data.onlinePlayers,
          });
        } catch (e) {
          console.error('Error parsing metrics data:', e);
        }
      };
      
      ws.onerror = (error) => {
        console.error('❌ WebSocket error:', error);
      };
      
      ws.onclose = () => {
        console.log('🔌 Disconnected from metrics WebSocket');
      };
      
      // Store ws reference in state
      setSocket(ws as any);
      
      return () => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.close();
        }
      };
    } catch (error) {
      console.error('Error connecting to WebSocket:', error);
    }
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
      const res = await fetch('http://localhost:9092/api/players');
      if (res.ok) {
        const data = await res.json();
        setPlayers(data);
      }
    } catch (e) { console.error('Error fetching players:', e); }
  };

  const fetchMap = async () => {
    try {
      const res = await fetch('http://localhost:9092/api/map');
      if (res.ok) {
        const data = await res.json();
        setEntities(data);
      }
    } catch (e) { console.error('Error fetching map:', e); }
  };

  const fetchChartData = async () => {
    try {
      const res = await fetch('http://localhost:9092/api/stats/history');
      if (res.ok) {
        const data = await res.json();
        setChartData({
          cpu: data.map((s: Stats) => s.cpu),
          memory: data.map((s: Stats) => (s.memUsed / s.memMax) * 100),
          players: data.map((s: Stats) => s.players),
          disk: data.map((s: Stats) => (s.diskUsed / s.diskTotal) * 100),
          timestamps: data.map((s: Stats, i: number) => Date.now() - (data.length - 1 - i) * 2000), // 2 second intervals
        });
      }
    } catch (e) { console.error('Error fetching chart data:', e); }
  };

  const runCommand = async (cmd: string) => {
    try {
      await fetch('http://localhost:9092/api/command?cmd=' + encodeURIComponent(cmd), {
        method: 'POST',
      });
    } catch (e) { console.error('Error running command:', e); }
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