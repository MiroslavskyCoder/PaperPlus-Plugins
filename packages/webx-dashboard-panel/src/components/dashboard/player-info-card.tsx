"use client";

import { Card } from '@/components/ui/card';
import { Coins, Zap, User, Sword, Heart } from 'lucide-react';

interface PlayerInfoCardProps {
  username?: string;
  level?: number;
  money?: number;
  health?: number;
  armor?: number;
  experience?: number;
}

export function PlayerInfoCard({
  username = 'Player',
  level = 1,
  money = 0,
  health = 20,
  armor = 0,
  experience = 0,
}: PlayerInfoCardProps) {
  return (
    <Card className="fixed top-8 right-8 w-80 bg-gradient-to-br from-card/90 to-card/70 backdrop-blur-xl border border-primary/20 shadow-2xl animate-fade-in z-50">
      <div className="p-4 space-y-4">
        {/* Player Header */}
        <div className="flex items-center justify-between border-b border-border/40 pb-3">
          <div className="flex items-center gap-2">
            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-purple-500 to-pink-500 flex items-center justify-center">
              <User className="h-5 w-5 text-white" />
            </div>
            <div>
              <p className="font-semibold text-sm">{username}</p>
              <p className="text-xs text-muted-foreground">Level {level}</p>
            </div>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 gap-3">
          {/* Money */}
          <div className="p-3 bg-gradient-to-br from-yellow-500/20 to-amber-500/20 border border-yellow-500/30 rounded-lg">
            <div className="flex items-center gap-2 mb-1">
              <Coins className="h-4 w-4 text-yellow-500" />
              <span className="text-xs text-muted-foreground">Money</span>
            </div>
            <p className="text-lg font-bold text-yellow-500">${money.toFixed(2)}</p>
          </div>

          {/* Experience */}
          <div className="p-3 bg-gradient-to-br from-green-500/20 to-emerald-500/20 border border-green-500/30 rounded-lg">
            <div className="flex items-center gap-2 mb-1">
              <Zap className="h-4 w-4 text-green-500" />
              <span className="text-xs text-muted-foreground">XP</span>
            </div>
            <p className="text-lg font-bold text-green-500">{experience.toFixed(0)}</p>
          </div>

          {/* Health */}
          <div className="p-3 bg-gradient-to-br from-red-500/20 to-rose-500/20 border border-red-500/30 rounded-lg">
            <div className="flex items-center gap-2 mb-1">
              <Heart className="h-4 w-4 text-red-500" />
              <span className="text-xs text-muted-foreground">Health</span>
            </div>
            <div className="flex items-center gap-1">
              <p className="text-lg font-bold text-red-500">{health}</p>
              <span className="text-xs text-muted-foreground">/20</span>
            </div>
          </div>

          {/* Armor */}
          <div className="p-3 bg-gradient-to-br from-blue-500/20 to-cyan-500/20 border border-blue-500/30 rounded-lg">
            <div className="flex items-center gap-2 mb-1">
              <Sword className="h-4 w-4 text-blue-500" />
              <span className="text-xs text-muted-foreground">Armor</span>
            </div>
            <div className="flex items-center gap-1">
              <p className="text-lg font-bold text-blue-500">{armor}</p>
              <span className="text-xs text-muted-foreground">/20</span>
            </div>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="space-y-2">
          <div className="flex justify-between items-center">
            <span className="text-xs text-muted-foreground">Experience Progress</span>
            <span className="text-xs font-semibold text-primary">{(experience % 100).toFixed(0)}%</span>
          </div>
          <div className="w-full h-2 bg-muted rounded-full overflow-hidden">
            <div
              className="h-full bg-gradient-to-r from-green-500 to-emerald-500 transition-all duration-500"
              style={{ width: `${(experience % 100)}%` }}
            />
          </div>
        </div>

        {/* Status Indicator */}
        <div className="flex items-center gap-2 text-xs p-2 bg-green-500/10 border border-green-500/20 rounded">
          <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse" />
          <span className="text-green-600 dark:text-green-400">Online • Active</span>
        </div>
      </div>
    </Card>
  );
}
