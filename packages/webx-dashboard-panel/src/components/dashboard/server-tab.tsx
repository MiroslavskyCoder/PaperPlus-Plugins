import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { useDashboard } from '@/app/dashboard-context';
import { useEffect } from 'react';

interface ServerTabProps {
  stats: any;
}

export function ServerTab({ stats }: ServerTabProps) {
  const { serverStatus, fetchServerStatus } = useDashboard();

  useEffect(() => {
    fetchServerStatus();
    const interval = setInterval(fetchServerStatus, 5000); // Refresh every 5 seconds
    return () => clearInterval(interval);
  }, [fetchServerStatus]);

  const formatUptime = (milliseconds: number) => {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const days = Math.floor(totalSeconds / 86400);
    const hours = Math.floor((totalSeconds % 86400) / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    
    if (days > 0) {
      return `${days}d ${hours}h ${minutes}m`;
    } else if (hours > 0) {
      return `${hours}h ${minutes}m`;
    } else {
      return `${minutes}m`;
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {/* Server Status Card */}
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <div className="w-3 h-3 bg-green-500 rounded-full animate-pulse"></div>
              Server Status
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Status</span>
                <span className="text-sm font-medium text-green-500">{serverStatus?.online ? 'Online' : 'Offline'}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Uptime</span>
                <span className="text-sm font-medium">{serverStatus ? formatUptime(serverStatus.uptime) : 'N/A'}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Version</span>
                <span className="text-sm font-medium">{serverStatus?.version || 'N/A'}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Name</span>
                <span className="text-sm font-medium">{serverStatus?.name || 'N/A'}</span>
              </div>
            </div>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={120}
          />
        </Card>

        {/* Server Properties Card */}
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle>Properties</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Game Mode</span>
                <span className="text-sm font-medium">{serverStatus?.gameMode || 'N/A'}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Difficulty</span>
                <span className="text-sm font-medium">
                  {serverStatus?.difficulty === 0 ? 'Peaceful' : 
                   serverStatus?.difficulty === 1 ? 'Easy' : 
                   serverStatus?.difficulty === 2 ? 'Normal' : 
                   serverStatus?.difficulty === 3 ? 'Hard' : 'N/A'}
                </span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">PVP</span>
                <span className="text-sm font-medium">{serverStatus?.pvp ? 'Enabled' : 'Disabled'}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-sm text-muted-foreground">Players</span>
                <span className="text-sm font-medium">{serverStatus?.onlinePlayers || 0} / {serverStatus?.maxPlayers || 0}</span>
              </div>
            </div>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={120}
          />
        </Card>

        {/* Performance Card */}
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle>Performance</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span>CPU</span>
                  <span>{stats?.cpu.toFixed(1) || 0}%</span>
                </div>
                <div className="w-full bg-muted rounded-full h-2">
                  <div className="bg-blue-500 h-2 rounded-full transition-all duration-500" style={{width: `${Math.min(stats?.cpu || 0, 100)}%`}}></div>
                </div>
              </div>
              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span>Memory</span>
                  <span>{stats ? ((stats.memUsed / stats.memMax) * 100).toFixed(1) : 0}%</span>
                </div>
                <div className="w-full bg-muted rounded-full h-2">
                  <div className="bg-green-500 h-2 rounded-full transition-all duration-500" style={{width: `${Math.min(stats ? (stats.memUsed / stats.memMax) * 100 : 0, 100)}%`}}></div>
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

      {/* MOTD Card */}
      {serverStatus?.motd && (
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle>Message of the Day</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-sm text-foreground">{serverStatus.motd}</p>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={120}
          />
        </Card>
      )}
    </div>
  );
}