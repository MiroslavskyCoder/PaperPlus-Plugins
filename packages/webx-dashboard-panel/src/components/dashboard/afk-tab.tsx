"use client";

import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { Moon, Save, RotateCcw, Clock, UserX, MessageSquare } from 'lucide-react';

interface AfkConfig {
  timeout: number;
  kickEnabled: boolean;
  kickTimeout: number;
  prefix: string;
  suffix: string;
  broadcastEnabled: boolean;
  autoResumeEnabled: boolean;
}

export function AfkTab() {
  const [config, setConfig] = useState<AfkConfig>({
    timeout: 10,
    kickEnabled: false,
    kickTimeout: 30,
    prefix: '§7[AFK] ',
    suffix: '',
    broadcastEnabled: true,
    autoResumeEnabled: true
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    loadConfig();
  }, []);

  const loadConfig = async () => {
    setLoading(true);
    setError('');
    
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const response = await fetch(`${protocol}//${host}/api/afk`);
      const data = await response.json();
      
      if (data.success && data.data) {
        setConfig(data.data);
      }
    } catch (err) {
      setError('Failed to load AFK configuration');
    } finally {
      setLoading(false);
    }
  };

  const saveConfig = async () => {
    setLoading(true);
    setError('');
    setSuccess('');

    // Validation
    if (config.timeout < 1) {
      setError('Timeout must be at least 1 minute');
      setLoading(false);
      return;
    }

    if (config.kickEnabled && config.kickTimeout < 1) {
      setError('Kick timeout must be at least 1 minute');
      setLoading(false);
      return;
    }

    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const response = await fetch(`${protocol}//${host}/api/afk`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(config)
      });

      const data = await response.json();

      if (data.success) {
        setSuccess('Configuration saved successfully!');
        setTimeout(() => setSuccess(''), 3000);
      } else {
        setError(data.message || 'Failed to save configuration');
      }
    } catch (err) {
      setError('Failed to save configuration');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Moon className="h-5 w-5 text-purple-500" />
            AFK System Configuration
          </CardTitle>
          <CardDescription>Configure AFK detection, kick settings, and display options</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Messages */}
          {error && (
            <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg text-destructive">
              {error}
            </div>
          )}
          {success && (
            <div className="p-4 bg-green-500/10 border border-green-500/20 rounded-lg text-green-500">
              {success}
            </div>
          )}

          {/* Configuration Form */}
          <div className="space-y-6">
            {/* Timeout Settings */}
            <div className="p-6 bg-gradient-to-br from-purple-500/10 to-blue-500/10 border border-purple-500/20 rounded-lg space-y-4">
              <h3 className="font-semibold flex items-center gap-2">
                <Clock className="h-4 w-4" />
                Timeout Settings
              </h3>
              
              <div className="space-y-2">
                <Label htmlFor="timeout">AFK Timeout (minutes)</Label>
                <Input
                  id="timeout"
                  type="number"
                  min="1"
                  value={config.timeout}
                  onChange={(e) => setConfig({ ...config, timeout: parseInt(e.target.value) || 1 })}
                />
                <p className="text-sm text-muted-foreground">
                  Time of inactivity before player is marked as AFK
                </p>
              </div>

              <div className="flex items-center justify-between space-x-2">
                <div className="space-y-0.5">
                  <Label>Auto Resume</Label>
                  <p className="text-sm text-muted-foreground">
                    Automatically remove AFK status when player moves
                  </p>
                </div>
                <Switch
                  checked={config.autoResumeEnabled}
                  onCheckedChange={(checked) => setConfig({ ...config, autoResumeEnabled: checked })}
                />
              </div>
            </div>

            {/* Kick Settings */}
            <div className="p-6 bg-gradient-to-br from-red-500/10 to-orange-500/10 border border-red-500/20 rounded-lg space-y-4">
              <h3 className="font-semibold flex items-center gap-2">
                <UserX className="h-4 w-4" />
                Kick Settings
              </h3>
              
              <div className="flex items-center justify-between space-x-2">
                <div className="space-y-0.5">
                  <Label>Enable AFK Kick</Label>
                  <p className="text-sm text-muted-foreground">
                    Automatically kick players who are AFK too long
                  </p>
                </div>
                <Switch
                  checked={config.kickEnabled}
                  onCheckedChange={(checked) => setConfig({ ...config, kickEnabled: checked })}
                />
              </div>

              {config.kickEnabled && (
                <div className="space-y-2">
                  <Label htmlFor="kickTimeout">Kick Timeout (minutes)</Label>
                  <Input
                    id="kickTimeout"
                    type="number"
                    min="1"
                    value={config.kickTimeout}
                    onChange={(e) => setConfig({ ...config, kickTimeout: parseInt(e.target.value) || 1 })}
                  />
                  <p className="text-sm text-muted-foreground">
                    Time after becoming AFK before kicking player
                  </p>
                </div>
              )}
            </div>

            {/* Display Settings */}
            <div className="p-6 bg-gradient-to-br from-green-500/10 to-teal-500/10 border border-green-500/20 rounded-lg space-y-4">
              <h3 className="font-semibold flex items-center gap-2">
                <MessageSquare className="h-4 w-4" />
                Display Settings
              </h3>
              
              <div className="space-y-2">
                <Label htmlFor="prefix">AFK Prefix</Label>
                <Input
                  id="prefix"
                  placeholder="[AFK] "
                  value={config.prefix}
                  onChange={(e) => setConfig({ ...config, prefix: e.target.value })}
                />
                <p className="text-sm text-muted-foreground">
                  Prefix shown before AFK player names (supports color codes: §7 = gray)
                </p>
              </div>

              <div className="space-y-2">
                <Label htmlFor="suffix">AFK Suffix (optional)</Label>
                <Input
                  id="suffix"
                  placeholder=""
                  value={config.suffix}
                  onChange={(e) => setConfig({ ...config, suffix: e.target.value })}
                />
                <p className="text-sm text-muted-foreground">
                  Suffix shown after AFK player names
                </p>
              </div>

              <div className="flex items-center justify-between space-x-2">
                <div className="space-y-0.5">
                  <Label>Broadcast AFK Status</Label>
                  <p className="text-sm text-muted-foreground">
                    Announce in chat when players go AFK or return
                  </p>
                </div>
                <Switch
                  checked={config.broadcastEnabled}
                  onCheckedChange={(checked) => setConfig({ ...config, broadcastEnabled: checked })}
                />
              </div>
            </div>

            {/* Preview */}
            <div className="p-4 bg-muted/50 border rounded-lg">
              <p className="text-sm text-muted-foreground mb-2">Preview:</p>
              <p className="font-mono">
                <span className="text-gray-500">{config.prefix}</span>
                <span>PlayerName</span>
                <span className="text-gray-500">{config.suffix}</span>
              </p>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-2">
            <Button onClick={saveConfig} disabled={loading}>
              <Save className="h-4 w-4 mr-2" />
              Save Configuration
            </Button>
            <Button variant="outline" onClick={loadConfig} disabled={loading}>
              <RotateCcw className="h-4 w-4 mr-2" />
              Reset
            </Button>
          </div>

          {/* Info */}
          <div className="p-4 bg-blue-500/10 border border-blue-500/20 rounded-lg text-sm text-muted-foreground">
            <p className="font-semibold mb-2">💡 Tips:</p>
            <ul className="space-y-1 list-disc list-inside">
              <li>Changes take effect immediately after saving</li>
              <li>Use Minecraft color codes (§) for colored prefix/suffix</li>
              <li>Configuration is saved to <code className="text-xs bg-muted px-1 py-0.5 rounded">plugins/AFK/afk.json</code></li>
            </ul>
          </div>
        </CardContent>
        <BorderTrail
          className="bg-gradient-to-l from-purple-200 via-purple-500 to-purple-200 dark:from-purple-400 dark:via-purple-500 dark:to-purple-700"
          size={120}
        />
      </Card>
    </div>
  );
}
