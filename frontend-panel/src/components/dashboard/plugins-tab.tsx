import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from "@/components/ui/button";

interface Plugin {
  name: string;
  version: string;
  description: string;
  author: string;
  status: 'enabled' | 'disabled';
}

interface PluginsTabProps {
  plugins: Plugin[];
}

export function PluginsTab({ plugins }: PluginsTabProps) {
  return (
    <div className="space-y-6 animate-fade-in">
      <Card className="bg-card/50 backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
            Server Plugins ({plugins?.length || 0})
          </CardTitle>
          <CardDescription>
            Manage and monitor installed plugins
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {plugins.map((plugin, index) => (
              <div key={index} className="border border-border rounded-lg p-4 bg-card hover:shadow-md transition-all duration-200">
                <div className="flex items-start justify-between mb-3">
                  <div className="w-10 h-10 bg-gradient-to-r from-blue-500 to-purple-500 rounded-lg flex items-center justify-center text-white font-bold text-sm">
                    {plugin.name.charAt(0)}
                  </div>
                  <div className={`px-2 py-1 rounded-full text-xs font-medium ${
                    plugin.status === 'enabled'
                      ? 'bg-green-500/10 text-green-500 border border-green-500/20'
                      : 'bg-red-500/10 text-red-500 border border-red-500/20'
                  }`}>
                    {plugin.status}
                  </div>
                </div>
                <h3 className="font-semibold text-sm mb-1">{plugin.name}</h3>
                <p className="text-xs text-muted-foreground mb-2 line-clamp-2">{plugin.description}</p>
                <div className="flex items-center justify-between text-xs text-muted-foreground">
                  <span>v{plugin.version}</span>
                  <span>by {plugin.author}</span>
                </div>
                <div className="flex gap-2 mt-3">
                  <Button size="sm" variant="outline" className="flex-1 text-xs">
                    Configure
                  </Button>
                  <Button size="sm" variant={plugin.status === 'enabled' ? 'destructive' : 'default'} className="flex-1 text-xs">
                    {plugin.status === 'enabled' ? 'Disable' : 'Enable'}
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}