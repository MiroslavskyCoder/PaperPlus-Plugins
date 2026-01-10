import { useDashboard } from '@/app/dashboard-context';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart";
import {
  Line,
  LineChart,
  Bar,
  BarChart,
  Area,
  AreaChart,
  Pie,
  PieChart,
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  RadialBar,
  RadialBarChart,
  ResponsiveContainer,
  XAxis,
  YAxis,
  CartesianGrid,
} from "recharts";

const lineChartConfig: ChartConfig = {
  cpu: { label: "CPU %", color: "hsl(var(--chart-1))" },
  memory: { label: "Memory %", color: "hsl(var(--chart-2))" },
};

const barChartConfig: ChartConfig = {
  players: { label: "Players", color: "hsl(var(--chart-3))" },
};

const pieChartConfig: ChartConfig = {
  used: { label: "Used", color: "hsl(var(--chart-1))" },
  free: { label: "Free", color: "hsl(var(--chart-2))" },
};

const areaChartConfig: ChartConfig = {
  cpu: { label: "CPU", color: "hsl(var(--chart-1))" },
  memory: { label: "Memory", color: "hsl(var(--chart-2))" },
  players: { label: "Players", color: "hsl(var(--chart-3))" },
};

const radarChartConfig: ChartConfig = {
  cpu: { label: "CPU", color: "hsl(var(--chart-1))" },
  memory: { label: "Memory", color: "hsl(var(--chart-2))" },
  disk: { label: "Disk", color: "hsl(var(--chart-4))" },
  players: { label: "Players", color: "hsl(var(--chart-3))" },
};

interface ChartsGridProps {}

export function ChartsGrid({}: ChartsGridProps) {
  const { chartData, stats } = useDashboard();

  // Prepare chart data
  const lineChartData = chartData.timestamps.map((time, i) => ({
    time: new Date(time).toLocaleTimeString(),
    cpu: chartData.cpu[i] || 0,
    memory: chartData.memory[i] || 0,
  }));

  const barChartData = chartData.timestamps.map((time, i) => ({
    time: new Date(time).toLocaleTimeString(),
    players: chartData.players[i] || 0,
  }));

  const pieChartData = stats ? [
    { name: "Used", value: (stats.diskUsed / stats.diskTotal) * 100, fill: "hsl(var(--chart-1))" },
    { name: "Free", value: ((stats.diskTotal - stats.diskUsed) / stats.diskTotal) * 100, fill: "hsl(var(--chart-2))" },
  ] : [];

  const areaChartData = chartData.timestamps.map((time, i) => ({
    time: new Date(time).toLocaleTimeString(),
    cpu: chartData.cpu[i] || 0,
    memory: chartData.memory[i] || 0,
    players: chartData.players[i] || 0,
  }));

  const radarChartData = stats ? [
    { subject: "CPU", value: stats.cpu },
    { subject: "Memory", value: (stats.memUsed / stats.memMax) * 100 },
    { subject: "Disk", value: (stats.diskUsed / stats.diskTotal) * 100 },
    { subject: "Players", value: stats.players },
  ] : [];

  const radialChartData = stats ? [{
    name: "CPU",
    value: stats.cpu,
    fill: "hsl(var(--chart-1))",
  }] : [];

  return (
    <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
      {/* Line Chart - CPU & Memory */}
      <div className="col-span-2">
        <Card className="bg-card/30 border-none">
          <CardHeader className="pb-2">
            <CardTitle className="text-sm">CPU & Memory Usage</CardTitle>
          </CardHeader>
          <CardContent>
            <ChartContainer config={lineChartConfig} className="h-50">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={lineChartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="time" />
                  <YAxis />
                  <ChartTooltip content={<ChartTooltipContent />} />
                  <Line type="monotone" dataKey="cpu" stroke="var(--color-cpu)" strokeWidth={2} />
                  <Line type="monotone" dataKey="memory" stroke="var(--color-memory)" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </ChartContainer>
          </CardContent>
        </Card>
      </div>

      {/* Bar Chart - Players */}
      <Card className="bg-card/30 border-none">
        <CardHeader className="pb-2">
          <CardTitle className="text-sm">Players Online</CardTitle>
        </CardHeader>
        <CardContent>
          <ChartContainer config={barChartConfig} className="h-50">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={barChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="time" />
                <YAxis />
                <ChartTooltip content={<ChartTooltipContent />} />
                <Bar dataKey="players" fill="var(--color-players)" />
              </BarChart>
            </ResponsiveContainer>
          </ChartContainer>
        </CardContent>
      </Card>

      {/* Pie Chart - Disk Usage */}
      <Card className="bg-card/30 border-none">
        <CardHeader className="pb-2">
          <CardTitle className="text-sm">Disk Usage</CardTitle>
        </CardHeader>
        <CardContent>
          <ChartContainer config={pieChartConfig} className="h-50">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <ChartTooltip content={<ChartTooltipContent />} />
                <Pie data={pieChartData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={60} />
              </PieChart>
            </ResponsiveContainer>
          </ChartContainer>
        </CardContent>
      </Card>

      {/* Area Chart - Combined */}
      <Card className="bg-card/30 border-none">
        <CardHeader className="pb-2">
          <CardTitle className="text-sm">Combined Metrics</CardTitle>
        </CardHeader>
        <CardContent>
          <ChartContainer config={areaChartConfig} className="h-50">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={areaChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="time" />
                <YAxis />
                <ChartTooltip content={<ChartTooltipContent />} />
                <Area type="monotone" dataKey="cpu" stackId="1" stroke="var(--color-cpu)" fill="var(--color-cpu)" />
                <Area type="monotone" dataKey="memory" stackId="1" stroke="var(--color-memory)" fill="var(--color-memory)" />
                <Area type="monotone" dataKey="players" stackId="1" stroke="var(--color-players)" fill="var(--color-players)" />
              </AreaChart>
            </ResponsiveContainer>
          </ChartContainer>
        </CardContent>
      </Card>

      {/* Radar Chart - Multi-dimensional */}
      <Card className="bg-card/30 border-none">
        <CardHeader className="pb-2">
          <CardTitle className="text-sm">System Overview</CardTitle>
        </CardHeader>
        <CardContent>
          <ChartContainer config={radarChartConfig} className="h-50">
            <ResponsiveContainer width="100%" height="100%">
              <RadarChart data={radarChartData}>
                <PolarGrid />
                <PolarAngleAxis dataKey="subject" />
                <PolarRadiusAxis angle={90} domain={[0, 100]} />
                <Radar name="Metrics" dataKey="value" stroke="var(--color-cpu)" fill="var(--color-cpu)" fillOpacity={0.3} />
                <ChartTooltip content={<ChartTooltipContent />} />
              </RadarChart>
            </ResponsiveContainer>
          </ChartContainer>
        </CardContent>
      </Card>

      {/* Radial Bar Chart - CPU */}
      <Card className="bg-card/30 border-none">
        <CardHeader className="pb-2">
          <CardTitle className="text-sm">CPU Load</CardTitle>
        </CardHeader>
        <CardContent>
          <ChartContainer config={lineChartConfig} className="h-50">
            <ResponsiveContainer width="100%" height="100%">
              <RadialBarChart cx="50%" cy="50%" innerRadius="10%" outerRadius="80%" data={radialChartData}>
                <RadialBar dataKey="value" cornerRadius={10} fill="var(--color-cpu)" />
                <ChartTooltip content={<ChartTooltipContent />} />
              </RadialBarChart>
            </ResponsiveContainer>
          </ChartContainer>
        </CardContent>
      </Card>
    </div>
  );
}