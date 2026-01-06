import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BorderTrail } from '@/components/motion-primitives/border-trail';
import { StatCard } from './stat-card';
import { ChartsGrid } from './charts-grid';
import { useDashboard } from '../../app/dashboard-context';

interface HomeTabProps {
  stats: any;
}

export function HomeTab({ stats }: HomeTabProps) {
  const { chartData } = useDashboard();
  return (
    <div className="space-y-6 animate-fade-in">
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="CPU Usage"
          value={stats ? `${stats.cpu.toFixed(2)}%` : 'N/A'}
          icon="cpu"
          trend="up"
          color="blue"
        />
        <StatCard
          title="Memory Usage"
          value={stats ? `${stats.memUsed} MB / ${stats.memMax} MB` : 'N/A'}
          icon="memory"
          trend="stable"
          color="green"
        />
        <StatCard
          title="Disk Usage"
          value={stats ? `${stats.diskUsed} GB / ${stats.diskTotal} GB` : 'N/A'}
          icon="disk"
          trend="down"
          color="purple"
        />
        <StatCard
          title="Online Players"
          value={stats ? `${stats.players}` : 'N/A'}
          icon="users"
          trend="up"
          color="orange"
        />
      </div>

      <Card className="bg-card/50 backdrop-blur-sm hover:shadow-lg transition-all duration-300">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
            Real-time Monitoring
          </CardTitle>
        </CardHeader>
        <CardContent>
          <ChartsGrid />
        </CardContent>
        <BorderTrail
          className={`hover:scale-105 bg-linear-to-l from-blue-200 via-blue-500 to-blue-200 dark:from-blue-400 dark:via-blue-500 dark:to-blue-700`}
          size={120}
        />
      </Card>
    </div>
  );
}