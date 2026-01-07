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
  
  // Calculate percentages and format values
  const cpuValue = stats ? `${Math.round(stats.cpu)}%` : 'N/A';
  const memoryPercent = stats && stats.memMax ? ((stats.memUsed / stats.memMax) * 100).toFixed(1) : 'N/A';
  const memoryValue = stats ? `${stats.memUsed} MB / ${stats.memMax} MB (${memoryPercent}%)` : 'N/A';
  const diskPercent = stats && stats.diskTotal ? ((stats.diskUsed / stats.diskTotal) * 100).toFixed(1) : 'N/A';
  const diskValue = stats ? `${stats.diskUsed} GB / ${stats.diskTotal} GB (${diskPercent}%)` : 'N/A';
  const playersValue = stats ? `${stats.players}` : 'N/A';
  
  // Determine memory trend
  let memoryTrend = 'stable';
  if (chartData.memory.length > 1) {
    const current = chartData.memory[chartData.memory.length - 1];
    const previous = chartData.memory[chartData.memory.length - 2];
    if (current > previous + 5) memoryTrend = 'up';
    if (current < previous - 5) memoryTrend = 'down';
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="CPU Usage"
          value={cpuValue}
          icon="cpu"
          trend={chartData.cpu.length > 1 && chartData.cpu[chartData.cpu.length - 1] > chartData.cpu[chartData.cpu.length - 2] ? 'up' : 'stable'}
          color="blue"
        />
        <StatCard
          title="Memory Usage"
          value={memoryValue}
          icon="memory"
          trend={memoryTrend}
          color="green"
        />
        <StatCard
          title="Disk Usage"
          value={diskValue}
          icon="disk"
          trend={chartData.disk.length > 1 && chartData.disk[chartData.disk.length - 1] < chartData.disk[chartData.disk.length - 2] ? 'down' : 'stable'}
          color="purple"
        />
        <StatCard
          title="Online Players"
          value={playersValue}
          icon="users"
          trend={chartData.players.length > 1 && chartData.players[chartData.players.length - 1] > chartData.players[chartData.players.length - 2] ? 'up' : 'stable'}
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