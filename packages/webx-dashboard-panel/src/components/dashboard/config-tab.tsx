"use client";

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { 
  Settings, 
  Server, 
  Users, 
  Shield, 
  Bell, 
  Zap,
  Save,
  RotateCcw,
  Copy,
  Check
} from 'lucide-react';

interface ConfigTabProps {
  runCommand: (cmd: string) => Promise<void>;
}

interface ServerConfig {
  serverName: string;
  motd: string;
  maxPlayers: number;
  difficulty: 'peaceful' | 'easy' | 'normal' | 'hard';
  pvpEnabled: boolean;
  whitelist: boolean;
  maintenanceMode: boolean;
}

interface AntiCheatConfig {
  enabled: boolean;
  autoban: boolean;
  suspiciousThreshold: number;
  banDuration: number;
}

interface PerformanceConfig {
  viewDistance: number;
  simulationDistance: number;
  ticksPerSecond: number;
  entityTracking: boolean;
}

export function ConfigTab({ runCommand }: ConfigTabProps) {
  const [activeTab, setActiveTab] = useState<'general' | 'anticheat' | 'performance'>('general');
  const [saved, setSaved] = useState(false);
  const [copied, setCopied] = useState(false);

  const [serverConfig, setServerConfig] = useState<ServerConfig>({
    serverName: 'My Awesome Server',
    motd: '§6Welcome to our server! §bHave fun!',
    maxPlayers: 100,
    difficulty: 'normal',
    pvpEnabled: true,
    whitelist: false,
    maintenanceMode: false,
  });

  const [antiCheatConfig, setAntiCheatConfig] = useState<AntiCheatConfig>({
    enabled: true,
    autoban: false,
    suspiciousThreshold: 75,
    banDuration: 7,
  });

  const [performanceConfig, setPerformanceConfig] = useState<PerformanceConfig>({
    viewDistance: 12,
    simulationDistance: 8,
    ticksPerSecond: 20,
    entityTracking: true,
  });

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  const handleCopyJson = (config: object) => {
    navigator.clipboard.writeText(JSON.stringify(config, null, 2));
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const tabs = [
    { id: 'general', label: 'General', icon: Settings },
    { id: 'anticheat', label: 'Anti-Cheat', icon: Shield },
    { id: 'performance', label: 'Performance', icon: Zap },
  ] as const;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Tabs Navigation */}
      <div className="flex gap-2 border-b border-border/40 overflow-x-auto pb-4">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-all duration-200 ${
                activeTab === tab.id
                  ? 'bg-primary text-primary-foreground shadow-lg'
                  : 'text-muted-foreground hover:text-foreground hover:bg-muted/50'
              }`}
            >
              <Icon className="h-4 w-4" />
              {tab.label}
            </button>
          );
        })}
      </div>

      {/* General Config */}
      {activeTab === 'general' && (
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Server className="h-5 w-5 text-blue-500" />
              Server Configuration
            </CardTitle>
            <CardDescription>Manage general server settings and properties</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Server Name */}
              <div className="space-y-2">
                <Label htmlFor="serverName">Server Name</Label>
                <Input
                  id="serverName"
                  value={serverConfig.serverName}
                  onChange={(e) => setServerConfig({ ...serverConfig, serverName: e.target.value })}
                  placeholder="My Server"
                  className="border-blue-500/20 focus:border-blue-500"
                />
              </div>

              {/* MOTD */}
              <div className="space-y-2">
                <Label htmlFor="motd">Message of the Day (MOTD)</Label>
                <Input
                  id="motd"
                  value={serverConfig.motd}
                  onChange={(e) => setServerConfig({ ...serverConfig, motd: e.target.value })}
                  placeholder="Welcome message"
                  className="border-blue-500/20 focus:border-blue-500"
                />
                <p className="text-xs text-muted-foreground">Use §6 for color codes (§c=red, §a=green, etc.)</p>
              </div>

              {/* Max Players */}
              <div className="space-y-2">
                <Label htmlFor="maxPlayers">Max Players</Label>
                <Input
                  id="maxPlayers"
                  type="number"
                  min="1"
                  max="1000"
                  value={serverConfig.maxPlayers}
                  onChange={(e) => setServerConfig({ ...serverConfig, maxPlayers: parseInt(e.target.value) })}
                  className="border-blue-500/20 focus:border-blue-500"
                />
              </div>

              {/* Difficulty */}
              <div className="space-y-2">
                <Label htmlFor="difficulty">Difficulty</Label>
                <select
                  id="difficulty"
                  value={serverConfig.difficulty}
                  onChange={(e) => setServerConfig({ ...serverConfig, difficulty: e.target.value as any })}
                  className="w-full px-3 py-2 rounded-md border border-input bg-background text-foreground"
                >
                  <option value="peaceful">Peaceful</option>
                  <option value="easy">Easy</option>
                  <option value="normal">Normal</option>
                  <option value="hard">Hard</option>
                </select>
              </div>
            </div>

            {/* Toggle Options */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 p-4 bg-gradient-to-r from-blue-500/10 to-cyan-500/10 border border-blue-500/20 rounded-lg">
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>PvP Enabled</Label>
                  <p className="text-xs text-muted-foreground">Allow player vs player combat</p>
                </div>
                <Switch
                  checked={serverConfig.pvpEnabled}
                  onCheckedChange={(checked) => setServerConfig({ ...serverConfig, pvpEnabled: checked })}
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>Whitelist</Label>
                  <p className="text-xs text-muted-foreground">Require whitelist to join</p>
                </div>
                <Switch
                  checked={serverConfig.whitelist}
                  onCheckedChange={(checked) => setServerConfig({ ...serverConfig, whitelist: checked })}
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>Maintenance Mode</Label>
                  <p className="text-xs text-muted-foreground">Prevent player joins</p>
                </div>
                <Switch
                  checked={serverConfig.maintenanceMode}
                  onCheckedChange={(checked) => setServerConfig({ ...serverConfig, maintenanceMode: checked })}
                />
              </div>
            </div>

            {/* Actions */}
            <div className="flex gap-2 pt-4">
              <Button onClick={handleSave} className="bg-green-600 hover:bg-green-700">
                <Save className="h-4 w-4 mr-2" />
                {saved ? 'Saved!' : 'Save Configuration'}
              </Button>
              <Button 
                variant="outline"
                onClick={() => handleCopyJson(serverConfig)}
              >
                <Copy className="h-4 w-4 mr-2" />
                {copied ? 'Copied!' : 'Copy JSON'}
              </Button>
            </div>
          </CardContent>
          <BorderTrail
            className="bg-gradient-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700"
            size={120}
          />
        </Card>
      )}

      {/* Anti-Cheat Config */}
      {activeTab === 'anticheat' && (
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Shield className="h-5 w-5 text-red-500" />
              Anti-Cheat Configuration
            </CardTitle>
            <CardDescription>Configure cheat detection and prevention settings</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Main Toggles */}
            <div className="space-y-4 p-4 bg-gradient-to-r from-red-500/10 to-orange-500/10 border border-red-500/20 rounded-lg">
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>Enable Anti-Cheat System</Label>
                  <p className="text-xs text-muted-foreground">Actively monitor for suspicious behavior</p>
                </div>
                <Switch
                  checked={antiCheatConfig.enabled}
                  onCheckedChange={(checked) => setAntiCheatConfig({ ...antiCheatConfig, enabled: checked })}
                />
              </div>

              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>Automatic Banning</Label>
                  <p className="text-xs text-muted-foreground">Auto-ban players exceeding threshold</p>
                </div>
                <Switch
                  checked={antiCheatConfig.autoban}
                  onCheckedChange={(checked) => setAntiCheatConfig({ ...antiCheatConfig, autoban: checked })}
                />
              </div>
            </div>

            {/* Numeric Settings */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="suspiciousThreshold">Suspicious Threshold (%)</Label>
                <div className="flex items-center gap-2">
                  <Input
                    id="suspiciousThreshold"
                    type="range"
                    min="0"
                    max="100"
                    step="5"
                    value={antiCheatConfig.suspiciousThreshold}
                    onChange={(e) => setAntiCheatConfig({ ...antiCheatConfig, suspiciousThreshold: parseInt(e.target.value) })}
                    className="flex-1"
                  />
                  <span className="text-lg font-bold text-red-500 min-w-fit">
                    {antiCheatConfig.suspiciousThreshold}%
                  </span>
                </div>
                <p className="text-xs text-muted-foreground">When a player score reaches this percentage, flag as suspicious</p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="banDuration">Ban Duration (days)</Label>
                <Input
                  id="banDuration"
                  type="number"
                  min="1"
                  value={antiCheatConfig.banDuration}
                  onChange={(e) => setAntiCheatConfig({ ...antiCheatConfig, banDuration: parseInt(e.target.value) })}
                  className="border-red-500/20 focus:border-red-500"
                />
              </div>
            </div>

            {/* Alert Box */}
            <div className="p-4 bg-yellow-500/10 border border-yellow-500/20 rounded-lg">
              <p className="text-sm text-yellow-600 dark:text-yellow-400 font-semibold">⚠️ Warning</p>
              <p className="text-xs text-muted-foreground mt-1">
                Setting automatic banning too aggressively may lead to false positives. Recommended threshold: 75-85%
              </p>
            </div>

            {/* Actions */}
            <div className="flex gap-2 pt-4">
              <Button onClick={handleSave} className="bg-red-600 hover:bg-red-700">
                <Save className="h-4 w-4 mr-2" />
                {saved ? 'Saved!' : 'Save Settings'}
              </Button>
              <Button 
                variant="outline"
                onClick={() => handleCopyJson(antiCheatConfig)}
              >
                <Copy className="h-4 w-4 mr-2" />
                {copied ? 'Copied!' : 'Copy JSON'}
              </Button>
            </div>
          </CardContent>
          <BorderTrail
            className="bg-gradient-to-l from-red-200 via-red-500 to-red-200 dark:from-red-400 dark:via-red-500 dark:to-red-700"
            size={120}
          />
        </Card>
      )}

      {/* Performance Config */}
      {activeTab === 'performance' && (
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Zap className="h-5 w-5 text-yellow-500" />
              Performance Settings
            </CardTitle>
            <CardDescription>Optimize server performance and resource usage</CardDescription>
          </CardHeader>
          <CardContent className="space-y-6">
            {/* Distance Settings */}
            <div className="space-y-4 p-4 bg-gradient-to-r from-yellow-500/10 to-amber-500/10 border border-yellow-500/20 rounded-lg">
              <div className="space-y-2">
                <Label htmlFor="viewDistance">
                  View Distance: <span className="text-yellow-500 font-bold">{performanceConfig.viewDistance}</span> chunks
                </Label>
                <Input
                  id="viewDistance"
                  type="range"
                  min="3"
                  max="32"
                  step="1"
                  value={performanceConfig.viewDistance}
                  onChange={(e) => setPerformanceConfig({ ...performanceConfig, viewDistance: parseInt(e.target.value) })}
                />
                <p className="text-xs text-muted-foreground">How far players can see (affects client-side rendering)</p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="simulationDistance">
                  Simulation Distance: <span className="text-yellow-500 font-bold">{performanceConfig.simulationDistance}</span> chunks
                </Label>
                <Input
                  id="simulationDistance"
                  type="range"
                  min="3"
                  max="32"
                  step="1"
                  value={performanceConfig.simulationDistance}
                  onChange={(e) => setPerformanceConfig({ ...performanceConfig, simulationDistance: parseInt(e.target.value) })}
                />
                <p className="text-xs text-muted-foreground">How far game logic updates (affects server load)</p>
              </div>
            </div>

            {/* Server Settings */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="tps">Target TPS (Ticks Per Second)</Label>
                <select
                  id="tps"
                  value={performanceConfig.ticksPerSecond}
                  onChange={(e) => setPerformanceConfig({ ...performanceConfig, ticksPerSecond: parseInt(e.target.value) })}
                  className="w-full px-3 py-2 rounded-md border border-input bg-background text-foreground"
                >
                  <option value={10}>10 TPS (Low)</option>
                  <option value={15}>15 TPS</option>
                  <option value={20}>20 TPS (Normal)</option>
                  <option value={25}>25 TPS</option>
                </select>
                <p className="text-xs text-muted-foreground">Server tick rate (20 = standard Minecraft)</p>
              </div>

              <div className="flex items-center justify-between p-3 bg-gradient-to-r from-green-500/10 to-emerald-500/10 border border-green-500/20 rounded-lg">
                <div className="space-y-0.5">
                  <Label>Entity Tracking</Label>
                  <p className="text-xs text-muted-foreground">Monitor entity behavior</p>
                </div>
                <Switch
                  checked={performanceConfig.entityTracking}
                  onCheckedChange={(checked) => setPerformanceConfig({ ...performanceConfig, entityTracking: checked })}
                />
              </div>
            </div>

            {/* Performance Tips */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
              <div className="p-3 bg-blue-500/10 border border-blue-500/20 rounded-lg">
                <p className="text-sm font-semibold text-blue-600 dark:text-blue-400">💡 Low Load</p>
                <p className="text-xs text-muted-foreground mt-1">View: 8-10, Sim: 4-6</p>
              </div>
              <div className="p-3 bg-amber-500/10 border border-amber-500/20 rounded-lg">
                <p className="text-sm font-semibold text-amber-600 dark:text-amber-400">⚖️ Balanced</p>
                <p className="text-xs text-muted-foreground mt-1">View: 12, Sim: 8</p>
              </div>
              <div className="p-3 bg-red-500/10 border border-red-500/20 rounded-lg">
                <p className="text-sm font-semibold text-red-600 dark:text-red-400">⚡ High Load</p>
                <p className="text-xs text-muted-foreground mt-1">View: 16+, Sim: 10+</p>
              </div>
            </div>

            {/* Actions */}
            <div className="flex gap-2 pt-4">
              <Button onClick={handleSave} className="bg-yellow-600 hover:bg-yellow-700">
                <Save className="h-4 w-4 mr-2" />
                {saved ? 'Saved!' : 'Save Settings'}
              </Button>
              <Button 
                variant="outline"
                onClick={() => handleCopyJson(performanceConfig)}
              >
                <Copy className="h-4 w-4 mr-2" />
                {copied ? 'Copied!' : 'Copy JSON'}
              </Button>
            </div>
          </CardContent>
          <BorderTrail
            className="bg-gradient-to-l from-yellow-200 via-yellow-500 to-yellow-200 dark:from-yellow-400 dark:via-yellow-500 dark:to-yellow-700"
            size={120}
          />
        </Card>
      )}
    </div>
  );
}