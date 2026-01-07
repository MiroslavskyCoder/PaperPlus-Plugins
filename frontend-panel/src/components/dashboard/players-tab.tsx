'use client';

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Loader } from '@/components/loader';

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

interface PlayersTabProps {
  players: Player[];
}

export function PlayersTab({ players }: PlayersTabProps) {
  const [selectedPlayer, setSelectedPlayer] = useState<Player | null>(null);
  const [actionInProgress, setActionInProgress] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const getHealthColor = (health: number, maxHealth: number) => {
    const percent = (health / maxHealth) * 100;
    if (percent > 75) return 'bg-green-500';
    if (percent > 50) return 'bg-yellow-500';
    if (percent > 25) return 'bg-orange-500';
    return 'bg-red-500';
  };

  const getPingColor = (ping: number) => {
    if (ping < 50) return 'text-green-500';
    if (ping < 100) return 'text-yellow-500';
    if (ping < 150) return 'text-orange-500';
    return 'text-red-500';
  };

  const executePlayerAction = async (action: string) => {
    if (!selectedPlayer) return;

    setActionInProgress(true);
    setMessage(null);

    try {
      let endpoint = '';
      let confirmMessage = '';

      switch (action) {
        case 'kick':
          endpoint = `/api/players/${selectedPlayer.uuid}/kick?reason=Kicked%20by%20admin`;
          confirmMessage = `Are you sure you want to kick ${selectedPlayer.name}?`;
          break;
        case 'ban':
          endpoint = `/api/players/${selectedPlayer.uuid}/ban?reason=Banned%20by%20admin`;
          confirmMessage = `Are you sure you want to ban ${selectedPlayer.name}?`;
          break;
        case 'teleport':
          endpoint = `/api/players/${selectedPlayer.uuid}/teleport`;
          break;
        case 'heal':
          endpoint = `/api/players/${selectedPlayer.uuid}/heal`;
          break;
        default:
          return;
      }

      if (['kick', 'ban'].includes(action) && !window.confirm(confirmMessage)) {
        setActionInProgress(false);
        return;
      }

      const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
      const response = await fetch(`${protocol}//${window.location.host}${endpoint}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      const data = await response.json();

      if (response.ok) {
        setMessage({ type: 'success', text: data.message || `Action "${action}" completed` });
        setTimeout(() => {
          setSelectedPlayer(null);
          setMessage(null);
        }, 2000);
      } else {
        setMessage({ type: 'error', text: data.message || 'Action failed' });
      }
    } catch (error) {
      setMessage({ type: 'error', text: error instanceof Error ? error.message : 'Request failed' });
    } finally {
      setActionInProgress(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <Card className="bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle>Online Players ({players.length})</CardTitle>
        </CardHeader>
        <CardContent>
          {players.length > 0 ? (
            <div className="space-y-4">
              {players.map((player) => (
                <div
                  key={player.uuid}
                  className="p-4 bg-muted/50 rounded-lg hover:bg-muted/70 transition-all duration-300 cursor-pointer"
                  onClick={() => setSelectedPlayer(player)}
                >
                  {/* Player Header */}
                  <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-500 rounded-full flex items-center justify-center text-white font-bold">
                        {player.name.charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <p className="font-medium">{player.name}</p>
                        <p className="text-xs text-muted-foreground">Level {player.level}</p>
                      </div>
                    </div>
                    <div className={`text-sm font-medium ${getPingColor(player.ping)}`}>
                      {player.ping}ms
                    </div>
                  </div>

                  {/* Health Bar */}
                  <div className="mb-3">
                    <div className="flex justify-between text-xs mb-1">
                      <span className="text-muted-foreground">Health</span>
                      <span>{player.health.toFixed(1)} / {player.maxHealth}</span>
                    </div>
                    <div className="w-full bg-muted rounded-full h-2">
                      <div
                        className={`${getHealthColor(player.health, player.maxHealth)} h-2 rounded-full transition-all duration-200`}
                        style={{ width: `${(player.health / player.maxHealth) * 100}%` }}
                      />
                    </div>
                  </div>

                  {/* Food Bar */}
                  <div className="mb-3">
                    <div className="flex justify-between text-xs mb-1">
                      <span className="text-muted-foreground">Food</span>
                      <span>{player.foodLevel} / 20</span>
                    </div>
                    <div className="w-full bg-muted rounded-full h-2">
                      <div
                        className="bg-orange-500 h-2 rounded-full transition-all duration-200"
                        style={{ width: `${(player.foodLevel / 20) * 100}%` }}
                      />
                    </div>
                  </div>

                  {/* Location */}
                  <div className="grid grid-cols-2 gap-2 text-xs text-muted-foreground">
                    <div>
                      <span className="text-xs font-medium">Location:</span>
                      <p>X: {player.x.toFixed(1)}</p>
                      <p>Y: {player.y.toFixed(1)}</p>
                      <p>Z: {player.z.toFixed(1)}</p>
                    </div>
                    <div>
                      <span className="text-xs font-medium">Info:</span>
                      <p>World: {player.world}</p>
                      <p>EXP: {player.experience.toFixed(2)}</p>
                      <p>Status: {player.online ? '🟢 Online' : '🔴 Offline'}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-muted-foreground">
              <div className="w-16 h-16 mx-auto mb-4 bg-muted rounded-full flex items-center justify-center">
                <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <p className="text-lg font-medium">No players online</p>
            </div>
          )}
        </CardContent>
        <BorderTrail
          className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
          size={120}
        />
      </Card>

      {/* Player Actions Dialog */}
      <Dialog open={!!selectedPlayer} onOpenChange={(open) => !open && setSelectedPlayer(null)}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Player Actions</DialogTitle>
            <DialogDescription>
              Manage actions for <span className="font-medium text-foreground">{selectedPlayer?.name}</span>
            </DialogDescription>
          </DialogHeader>

          {message && (
            <div
              className={`p-3 rounded-md text-sm ${
                message.type === 'success'
                  ? 'bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-100'
                  : 'bg-red-100 dark:bg-red-900 text-red-800 dark:text-red-100'
              }`}
            >
              {message.text}
            </div>
          )}

          <div className="space-y-2">
            <Button
              onClick={() => executePlayerAction('teleport')}
              disabled={actionInProgress}
              variant="outline"
              className="w-full justify-start"
            >
              {actionInProgress ? <Loader className="mr-2 h-4 w-4" /> : '📍'}
              Teleport to Spawn
            </Button>
            <Button
              onClick={() => executePlayerAction('heal')}
              disabled={actionInProgress}
              variant="outline"
              className="w-full justify-start"
            >
              {actionInProgress ? <Loader className="mr-2 h-4 w-4" /> : '💊'}
              Heal Player
            </Button>
            <Button
              onClick={() => executePlayerAction('kick')}
              disabled={actionInProgress}
              variant="destructive"
              className="w-full justify-start"
            >
              {actionInProgress ? <Loader className="mr-2 h-4 w-4" /> : '👢'}
              Kick Player
            </Button>
            <Button
              onClick={() => executePlayerAction('ban')}
              disabled={actionInProgress}
              variant="destructive"
              className="w-full justify-start"
            >
              {actionInProgress ? <Loader className="mr-2 h-4 w-4" /> : '🚫'}
              Ban Player
            </Button>
          </div>

          <DialogFooter>
            <Button
              onClick={() => setSelectedPlayer(null)}
              variant="ghost"
              disabled={actionInProgress}
            >
              Close
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}