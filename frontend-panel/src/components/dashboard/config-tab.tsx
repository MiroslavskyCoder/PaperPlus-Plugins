import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from "@/components/ui/button";
import { BorderTrail } from '@/components/motion-primitives/border-trail';

interface ConfigTabProps {
  runCommand: (cmd: string) => Promise<void>;
}

export function ConfigTab({ runCommand }: ConfigTabProps) {
  const [selectedConfig, setSelectedConfig] = useState<string>('server.properties');
  const [configContent, setConfigContent] = useState<string>('');
  const [isConfigLoading, setIsConfigLoading] = useState(false);
  const [isConfigSaving, setIsConfigSaving] = useState(false);

  // Load configuration file
  const loadConfig = async (filename: string) => {
    setIsConfigLoading(true);
    try {
      const res = await fetch(`http://localhost:8080/api/config/${filename}`, {
        headers: { 'X-API-KEY': 'secret-change-me' }
      });
      if (res.ok) {
        const content = await res.text();
        setConfigContent(content);
        setSelectedConfig(filename);
      } else {
        // Fallback to sample content for demo
        const sampleConfigs: { [key: string]: string } = {
          'server.properties': `#Minecraft server properties
#Generated on ${new Date().toISOString()}
server-port=25565
max-players=20
motd=A Minecraft Server
difficulty=normal
pvp=true
online-mode=true
max-world-size=29999984
allow-nether=true
enable-command-block=true
spawn-protection=16
max-tick-time=60000
view-distance=10
simulation-distance=10`,
          'bukkit.yml': `#Bukkit Configuration
settings:
  allow-end: true
  warn-on-overload: true
  permissions-file: permissions.yml
  update-folder: update
  connection-throttle: 4000
  query-plugins: true
  deprecated-verbose: default
  shutdown-message: Server closed
spawn-limits:
  monsters: 70
  animals: 15
  water-animals: 5
  water-ambient: 20
  ambient: 15
ticks-per:
  animal-spawns: 400
  monster-spawns: 1
  water-spawns: 1
  water-ambient-spawns: 1
  ambient-spawns: 1
chunk-gc:
  period-in-ticks: 600
  load-threshold: 0`,
          'spigot.yml': `#Spigot Configuration
settings:
  save-user-cache-on-stop-only: false
  bungeecord: false
  late-bind: false
  sample-count: 12
  player-shuffle: 0
  filter-creative-items: true
  user-cache-size: 1000
  int-cache-limit: 1024
  moved-wrongly-threshold: 0.0625
  moved-too-quickly-multiplier: 10.0
  timeout-time: 60
  restart-on-crash: true
  restart-script: ./start.sh
  netty-threads: 4
  attribute:
    maxHealth:
      max: 2048.0
    movementSpeed:
      max: 2048.0
    attackDamage:
      max: 2048.0`
        };
        setConfigContent(sampleConfigs[filename] || '# Configuration file not found');
        setSelectedConfig(filename);
      }
    } catch (e) {
      console.error('Failed to load config:', e);
      setConfigContent('# Failed to load configuration');
    } finally {
      setIsConfigLoading(false);
    }
  };

  // Save configuration and restart server
  const saveConfig = async () => {
    setIsConfigSaving(true);
    try {
      const res = await fetch(`http://localhost:8080/api/config/${selectedConfig}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
          'X-API-KEY': 'secret-change-me'
        },
        body: configContent
      });

      if (res.ok) {
        // Restart server after saving
        await runCommand('restart');
        alert('Configuration saved and server restarted!');
      } else {
        alert('Failed to save configuration');
      }
    } catch (e) {
      console.error('Failed to save config:', e);
      alert('Failed to save configuration');
    } finally {
      setIsConfigSaving(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <Card className="bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            Server Configuration
          </CardTitle>
          <CardDescription>
            Edit server.properties and other configuration files
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="flex gap-2">
              <Button
                variant="outline"
                className="flex items-center gap-2"
                onClick={() => loadConfig('server.properties')}
                disabled={isConfigLoading}
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                server.properties
              </Button>
              <Button
                variant="outline"
                className="flex items-center gap-2"
                onClick={() => loadConfig('bukkit.yml')}
                disabled={isConfigLoading}
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                bukkit.yml
              </Button>
              <Button
                variant="outline"
                className="flex items-center gap-2"
                onClick={() => loadConfig('spigot.yml')}
                disabled={isConfigLoading}
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                spigot.yml
              </Button>
            </div>

            <div className="border rounded-lg p-4 bg-muted/20">
              <h4 className="font-medium mb-2">Configuration Editor</h4>
              <textarea
                className="w-full h-64 bg-background border border-border rounded-md p-3 font-mono text-sm resize-none"
                placeholder="# Configuration will be loaded here..."
                value={configContent}
                onChange={(e) => setConfigContent(e.target.value)}
                readOnly={isConfigLoading}
              />
              <div className="flex gap-2 mt-4">
                <Button
                  className="bg-green-600 hover:bg-green-700"
                  onClick={saveConfig}
                  disabled={isConfigSaving || !configContent}
                >
                  <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  Save & Restart Server
                </Button>
                <Button variant="outline">Cancel</Button>
              </div>
            </div>
          </div>
        </CardContent>
        <BorderTrail
          className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
          size={120}
        />
      </Card>
    </div>
  );
}