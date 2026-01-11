"use client";

import { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { Coins, TrendingUp, Search, Users, Wallet } from 'lucide-react';

interface PlayerCoins {
  uuid: string;
  coins: number;
  bankBalance: number;
  total: number;
}

export function EconomyTab() {
  const [playerUuid, setPlayerUuid] = useState('');
  const [playerData, setPlayerData] = useState<PlayerCoins | null>(null);
  const [topPlayers, setTopPlayers] = useState<PlayerCoins[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const searchPlayer = async () => {
    if (!playerUuid.trim()) {
      setError('Please enter a player UUID');
      return;
    }

    setLoading(true);
    setError('');
    
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const response = await fetch(`${protocol}//${host}/api/player/${playerUuid}/coins`);
      const data = await response.json();
      
      if (data.success) {
        setPlayerData(data.data);
        setError('');
      } else {
        setError(data.message || 'Player not found');
        setPlayerData(null);
      }
    } catch (err) {
      setError('Failed to fetch player data');
      setPlayerData(null);
    } finally {
      setLoading(false);
    }
  };

  const loadTopPlayers = async () => {
    setLoading(true);
    setError('');
    
    try {
      const protocol = window.location.protocol;
      const host = window.location.host;
      const response = await fetch(`${protocol}//${host}/api/players/top?limit=10`);
      const data = await response.json();
      
      if (data.success) {
        setTopPlayers(data.data);
        setError('');
      } else {
        setError(data.message || 'Failed to load top players');
      }
    } catch (err) {
      setError('Failed to fetch top players');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Coins className="h-5 w-5 text-yellow-500" />
            Economy - Player Coins
          </CardTitle>
          <CardDescription>View player balances and top rankings</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* Search Player */}
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="playerUuid">Player UUID</Label>
              <div className="flex gap-2">
                <Input
                  id="playerUuid"
                  placeholder="Enter player UUID..."
                  value={playerUuid}
                  onChange={(e) => setPlayerUuid(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && searchPlayer()}
                  className="flex-1"
                />
                <Button onClick={searchPlayer} disabled={loading}>
                  <Search className="h-4 w-4 mr-2" />
                  Search
                </Button>
              </div>
            </div>

            {error && (
              <div className="p-4 bg-destructive/10 border border-destructive/20 rounded-lg text-destructive">
                {error}
              </div>
            )}

            {playerData && (
              <div className="p-6 bg-gradient-to-br from-yellow-500/10 to-yellow-600/10 border border-yellow-500/20 rounded-lg space-y-3">
                <div className="flex items-center gap-2 mb-4">
                  <Wallet className="h-5 w-5 text-yellow-500" />
                  <h3 className="font-semibold text-lg">Player Information</h3>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-muted-foreground">UUID</p>
                    <p className="font-mono text-xs">{playerData.uuid}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">💰 Coins</p>
                    <p className="text-2xl font-bold text-yellow-500">{playerData.coins.toFixed(2)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">🏦 Bank Balance</p>
                    <p className="text-xl font-semibold">{playerData.bankBalance.toFixed(2)}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">📊 Total</p>
                    <p className="text-xl font-semibold text-green-500">{playerData.total.toFixed(2)}</p>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Top Players */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="font-semibold flex items-center gap-2">
                <TrendingUp className="h-5 w-5 text-green-500" />
                Top Players
              </h3>
              <Button variant="outline" size="sm" onClick={loadTopPlayers} disabled={loading}>
                <Users className="h-4 w-4 mr-2" />
                Load Top 10
              </Button>
            </div>

            {topPlayers.length > 0 && (
              <div className="space-y-2">
                {topPlayers.map((player, index) => (
                  <div
                    key={player.uuid}
                    className="p-4 bg-gradient-to-r from-card to-card/50 border rounded-lg hover:border-primary/50 transition-all duration-200"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <div className={`w-8 h-8 rounded-full flex items-center justify-center font-bold ${
                          index === 0 ? 'bg-yellow-500 text-white' :
                          index === 1 ? 'bg-gray-400 text-white' :
                          index === 2 ? 'bg-orange-600 text-white' :
                          'bg-muted text-muted-foreground'
                        }`}>
                          #{index + 1}
                        </div>
                        <div>
                          <p className="font-mono text-xs text-muted-foreground">{player.uuid.substring(0, 8)}...</p>
                          <div className="flex gap-4 text-sm mt-1">
                            <span className="text-yellow-500">💰 {player.coins.toFixed(2)}</span>
                            <span className="text-muted-foreground">🏦 {player.bankBalance.toFixed(2)}</span>
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className="text-lg font-bold text-green-500">{player.total.toFixed(2)}</p>
                        <p className="text-xs text-muted-foreground">Total</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </CardContent>
        <BorderTrail
          className="bg-gradient-to-l from-yellow-200 via-yellow-500 to-yellow-200 dark:from-yellow-400 dark:via-yellow-500 dark:to-yellow-700"
          size={120}
        />
      </Card>
    </div>
  );
}
