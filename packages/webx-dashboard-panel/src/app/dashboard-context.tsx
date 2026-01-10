"use client";

import React, { createContext, useContext, useEffect, useState } from 'react';

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
  maxHealth: number;
  x: number;
  y: number;
  z: number;
  world: string;
  level: number;
  experience: number;
  foodLevel: number;
  ping: number;
  online: boolean;
  timestamp: number;
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

interface ServerStatus {
  name: string;
  version: string;
  onlinePlayers: number;
  maxPlayers: number;
  cpuUsage: number;
  memoryUsed: number;
  memoryMax: number;
  motd: string;
  online: boolean;
  uptime: number;
  gameMode: string;
  difficulty: number;
  pvp: boolean;
}

interface DashboardContextType {
  stats: Stats | null;
  statsHistory: Stats[];
  players: Player[];
  entities: Entity[];
  plugins: Plugin[];
  mapImage: string;
  serverStatus: ServerStatus | null;
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
  fetchServerStatus: () => Promise<void>;
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
  const [mapImage, setMapImage] = useState<string>('');
  const [serverStatus, setServerStatus] = useState<ServerStatus | null>(null);
  const [chartData, setChartData] = useState({
    cpu: [25, 30, 45, 50, 35, 40, 55, 60, 45, 50],
    memory: [60, 65, 70, 75, 80, 85, 90, 95, 85, 80],
    players: [5, 8, 12, 15, 10, 7, 9, 14, 11, 8],
    disk: [45, 50, 55, 60, 65, 70, 75, 80, 85, 90],
    timestamps: Array.from({ length: 10 }, (_, i) => Date.now() - (9 - i) * 1000),
  });

  useEffect(() => {
    // Connect to WebSocket for real-time metrics
    // Dynamically determine the WebSocket URL based on the current location
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = window.location.host;
    const wsUrl = `${protocol}//${host}/metrics`;
    
    console.log('🔌 Connecting to WebSocket:', wsUrl);
    
    try {
      const ws = new WebSocket(wsUrl);
      
      ws.onopen = () => {
        console.log('✅ Connected to metrics WebSocket');
      };
      
      ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          console.log('📥 Metrics received:', data);
          
          // Update current stats
          setStats({
            cpu: data.cpuUsage || 0,
            memUsed: data.memUsed || 0,
            memMax: data.memMax || 0,
            diskUsed: data.diskUsed || 0,
            diskTotal: data.diskTotal || 0,
            players: data.onlinePlayers || 0,
          });
          
          // Update stats history for charts (keep last 30 data points = 60 seconds)
          setStatsHistory(prev => {
            const updated = [...prev, {
              cpu: data.cpuUsage || 0,
              memUsed: data.memUsed || 0,
              memMax: data.memMax || 0,
              diskUsed: data.diskUsed || 0,
              diskTotal: data.diskTotal || 0,
              players: data.onlinePlayers || 0,
            }];
            return updated.slice(-30); // Keep last 30 points
          });
          
          // Update chart data
          setChartData(prev => {
            const newCpu = [...prev.cpu, data.cpuUsage || 0].slice(-30);
            const memoryPercent = data.memMax > 0 ? (data.memUsed / data.memMax) * 100 : 0;
            const newMemory = [...prev.memory, memoryPercent].slice(-30);
            const newPlayers = [...prev.players, data.onlinePlayers || 0].slice(-30);
            const diskPercent = data.diskTotal > 0 ? (data.diskUsed / data.diskTotal) * 100 : 0;
            const newDisk = [...prev.disk, diskPercent].slice(-30);
            const newTimestamps = [...prev.timestamps, data.timestamp || Date.now()].slice(-30);
            
            console.log('📊 Metrics updated:', {
              cpu: (data.cpuUsage || 0).toFixed(2) + '%',
              memory: memoryPercent.toFixed(2) + '%',
              players: data.onlinePlayers || 0,
            });
            
            return {
              cpu: newCpu,
              memory: newMemory,
              players: newPlayers,
              disk: newDisk,
              timestamps: newTimestamps,
            };
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
      
      // Connect to players metrics WebSocket
      const playersWsUrl = `${protocol}//${host}/players/metrics`;
      console.log('🎮 Connecting to players WebSocket:', playersWsUrl);
      
      const playersWs = new WebSocket(playersWsUrl);
      
      playersWs.onopen = () => {
        console.log('✅ Connected to players WebSocket');
      };
      
      playersWs.onmessage = (event) => {
        try {
          const playersData = JSON.parse(event.data);
          console.log('👥 Players data received:', playersData);
          setPlayers(playersData);
        } catch (e) {
          console.error('Error parsing players data:', e);
        }
      };
      
      playersWs.onerror = (error) => {
        console.error('❌ Players WebSocket error:', error);
      };
      
      playersWs.onclose = () => {
        console.log('🔌 Disconnected from players WebSocket');
      };
      
      return () => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.close();
        }
        if (playersWs.readyState === WebSocket.OPEN) {
          playersWs.close();
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
      const protocol = window.location.protocol;
      const host = window.location.host;
      const res = await fetch(`${protocol}//${host}/api/players`);
      if (res.ok) {
        const data = await res.json();
        setPlayers(data);
      }
    } catch (e) { console.error('Error fetching players:', e); }
  };

  const fetchServerStatus = async () => {
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const res = await fetch(`${protocol}//${host}/api/server/status`);
      if (res.ok) {
        const data = await res.json();
        setServerStatus(data);
      }
    } catch (e) { console.error('Error fetching server status:', e); }
  };

  const fetchMap = async () => {
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const res = await fetch(`${protocol}//${host}/api/map`);
      if (res.ok) {
        const data = await res.json();
        setEntities(data);
      }
    } catch (e) { console.error('Error fetching map:', e); }
  };

  const fetchChartData = async () => {
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const res = await fetch(`${protocol}//${host}/api/stats/history`);
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
      const protocol = window.location.protocol;
      const host = window.location.host;
      await fetch(`${protocol}//${host}/api/command?cmd=${encodeURIComponent(cmd)}`, {
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
      mapImage,
      serverStatus,
      chartData,
      fetchPlayers,
      fetchMap,
      fetchChartData,
      fetchServerStatus,
      runCommand
    }}>
      {children}
    </DashboardContext.Provider>
  );
};