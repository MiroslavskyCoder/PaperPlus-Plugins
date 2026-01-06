import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';

interface ServerTabProps {
  stats: any;
}

export function ServerTab({ stats }: ServerTabProps) {
  return (
    <div className="space-y-6 animate-fade-in">
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <div className="w-3 h-3 bg-green-500 rounded-full"></div>
              Server Status
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-sm text-muted-foreground">Status</span>
                <span className="text-sm font-medium text-green-500">Online</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-muted-foreground">Uptime</span>
                <span className="text-sm font-medium">24h 32m</span>
              </div>
              <div className="flex justify-between">
                <span className="text-sm text-muted-foreground">Version</span>
                <span className="text-sm font-medium">1.20.1</span>
              </div>
            </div>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={120}
          />
        </Card>

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
                  <div className="bg-blue-500 h-2 rounded-full transition-all duration-500" style={{width: `${stats?.cpu || 0}%`}}></div>
                </div>
              </div>
              <div>
                <div className="flex justify-between text-sm mb-1">
                  <span>Memory</span>
                  <span>{stats ? ((stats.memUsed / stats.memMax) * 100).toFixed(1) : 0}%</span>
                </div>
                <div className="w-full bg-muted rounded-full h-2">
                  <div className="bg-green-500 h-2 rounded-full transition-all duration-500" style={{width: `${stats ? (stats.memUsed / stats.memMax) * 100 : 0}%`}}></div>
                </div>
              </div>
            </div>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={120}
          />
        </Card>

        <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              <button className="w-full bg-primary hover:bg-primary/90 text-primary-foreground px-4 py-2 rounded-md transition-colors">
                Restart Server
              </button>
              <button className="w-full bg-secondary hover:bg-secondary/80 text-secondary-foreground px-4 py-2 rounded-md transition-colors">
                Backup World
              </button>
              <button className="w-full bg-destructive hover:bg-destructive/90 text-destructive-foreground px-4 py-2 rounded-md transition-colors">
                Stop Server
              </button>
            </div>
          </CardContent>
          <BorderTrail
            className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
            size={120}
          />
        </Card>
      </div>
    </div>
  );
}