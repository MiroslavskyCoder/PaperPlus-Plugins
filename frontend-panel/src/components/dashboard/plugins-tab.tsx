'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Loader } from '@/components/loader';

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

interface CurseForgeMod {
  id: number;
  name: string;
  summary: string;
}

export function PluginsTab({ plugins }: PluginsTabProps) {
  const [query, setQuery] = useState('');
  const [searching, setSearching] = useState(false);
  const [results, setResults] = useState<CurseForgeMod[]>([]);
  const [installingId, setInstallingId] = useState<number | null>(null);

  // Load initial popular plugins on mount
  useEffect(() => {
    searchCurseForge('bukkit');
  }, []);

  const searchCurseForge = async (searchQuery?: string) => {
    const q = searchQuery || query;
    setSearching(true);
    try {
      const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
      const res = await fetch(`${protocol}//${window.location.host}/api/curseforge/search?q=${encodeURIComponent(q || 'bukkit')}&gameId=432`);
      const data = await res.json();
      const mods: CurseForgeMod[] = (data?.data || []).map((m: any) => ({ id: m.id, name: m.name, summary: m.summary }));
      setResults(mods);
    } catch (e) {
      console.error(e);
    } finally {
      setSearching(false);
    }
  };

  const handleSearch = () => {
    if (query.trim()) {
      searchCurseForge();
    }
  };

  const installFromCurseForge = async (modId: number) => {
    setInstallingId(modId);
    try {
      const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
      // Fetch files and pick the first one (caller can refine later)
      const filesRes = await fetch(`${protocol}//${window.location.host}/api/curseforge/mods/${modId}/files`);
      const files = await filesRes.json();
      const first = files?.data?.[0];
      if (!first) throw new Error('No files found for this mod');

      const res = await fetch(`${protocol}//${window.location.host}/api/curseforge/install?modId=${modId}&fileId=${first.id}`, { method: 'POST' });
      const install = await res.json();
      alert(install?.message || 'Installed');
    } catch (e: any) {
      alert(e.message || 'Install failed');
    } finally {
      setInstallingId(null);
    }
  };
  return (
    <div className="space-y-6 animate-fade-in">
      {/* CurseForge Plugin Manager */}
      <Card className="bg-card border-2 border-primary/20">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            🔍 Plugin Manager (CurseForge)
          </CardTitle>
          <CardDescription>Search and install Bukkit plugins from CurseForge</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex gap-2 mb-4">
            <Input 
              placeholder="Search plugins..." 
              value={query} 
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            />
            <Button onClick={handleSearch} disabled={searching}>
              {searching ? <Loader className="mr-2 h-4 w-4" /> : null}
              Search
            </Button>
          </div>
          {results.length > 0 ? (
            <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-3">
              {results.map((m) => (
                <div key={m.id} className="border border-border rounded-lg p-4 bg-card">
                  <h3 className="font-semibold text-sm mb-1">{m.name}</h3>
                  <p className="text-xs text-muted-foreground mb-2 line-clamp-2">{m.summary}</p>
                  <div className="flex gap-2">
                    {plugins.some(p => p.name.toLowerCase() === m.name.toLowerCase()) ? (
                      <>
                        <Button size="sm" variant="outline" onClick={() => alert('Open config for ' + m.name)}>Configure</Button>
                        <Button
                          size="sm"
                          variant={plugins.find(p => p.name.toLowerCase() === m.name.toLowerCase())?.status === 'enabled' ? 'destructive' : 'default'}
                          onClick={async () => {
                            const installed = plugins.find(p => p.name.toLowerCase() === m.name.toLowerCase());
                            if (!installed) return;
                            const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
                            const path = installed.status === 'enabled' ? `/api/plugins/${encodeURIComponent(installed.name)}/disable` : `/api/plugins/${encodeURIComponent(installed.name)}/enable`;
                            try {
                              const res = await fetch(`${protocol}//${window.location.host}${path}`, { method: 'POST' });
                              const data = await res.json();
                              installed.status = installed.status === 'enabled' ? 'disabled' : 'enabled';
                              alert(data?.message || 'Done');
                            } catch (e: any) {
                              alert(e.message || 'Action failed');
                            }
                          }}
                        >
                          {plugins.find(p => p.name.toLowerCase() === m.name.toLowerCase())?.status === 'enabled' ? 'Disable' : 'Enable'}
                        </Button>
                      </>
                    ) : (
                      <Button size="sm" variant="default" onClick={() => installFromCurseForge(m.id)} disabled={installingId === m.id}>
                        {installingId === m.id ? <Loader className="mr-2 h-4 w-4" /> : null}
                        Install
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          ) : searching ? (
            <div className="text-center py-8 text-muted-foreground">
              <Loader className="mx-auto h-8 w-8" />
              <p className="mt-2">Searching plugins...</p>
            </div>
          ) : (
            <div className="text-center py-8 text-muted-foreground">
              <p>Enter a search term to find plugins</p>
            </div>
          )}
        </CardContent>
      </Card>

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
                  <Button size="sm" variant="outline" className="flex-1 text-xs" onClick={() => alert('Open config for ' + plugin.name)}>
                    Configure
                  </Button>
                  <Button
                    size="sm"
                    variant={plugin.status === 'enabled' ? 'destructive' : 'default'}
                    className="flex-1 text-xs"
                    onClick={async () => {
                      const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
                      const path = plugin.status === 'enabled' ? `/api/plugins/${encodeURIComponent(plugin.name)}/disable` : `/api/plugins/${encodeURIComponent(plugin.name)}/enable`;
                      try {
                        const res = await fetch(`${protocol}//${window.location.host}${path}`, { method: 'POST' });
                        const data = await res.json();
                        // optimistic update
                        plugin.status = plugin.status === 'enabled' ? 'disabled' : 'enabled';
                        alert(data?.message || 'Done');
                      } catch (e: any) {
                        alert(e.message || 'Action failed');
                      }
                    }}
                  >
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