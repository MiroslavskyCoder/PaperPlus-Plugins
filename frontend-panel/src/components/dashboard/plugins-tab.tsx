'use client';

import { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Loader } from '@/components/loader';
import { Search } from 'lucide-react';
import { InputGroup, InputGroupAddon, InputGroupInput } from '../ui/input-group';

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
  const [error, setError] = useState<string | null>(null);

  // Load initial popular plugins on mount
  useEffect(() => {
    searchCurseForge('bukkit');
  }, []);

  // Debounced search while typing
  useEffect(() => {
    const term = query.trim();
    if (!term) return;
    const id = setTimeout(() => searchCurseForge(term), 450);
    return () => clearTimeout(id);
  }, [query]);

  const searchCurseForge = async (searchQuery?: string) => {
    const q = (searchQuery || query || '').trim() || 'bukkit';
    setSearching(true);
    setError(null);
    try {
      const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
      const res = await fetch(`${protocol}//${window.location.host}/api/curseforge/search?q=${encodeURIComponent(q || 'bukkit')}&gameId=432`);
      const data = await res.json();
      const mods: CurseForgeMod[] = (data?.data || []).map((m: any) => ({ id: m.id, name: m.name, summary: m.summary }));
      setResults(mods);
    } catch (e) {
      console.error(e);
      setError('Не удалось загрузить результаты. Проверьте API ключ CurseForge и соединение.');
      setResults([]);
    } finally {
      setSearching(false);
    }
  };

  const handleSearch = () => {
    const term = query.trim();
    if (term) searchCurseForge(term);
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
    <div className="space-y-8 animate-fade-in">
      <div className="container mx-auto max-w-6xl px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-3">
            <h2 className="text-lg font-semibold tracking-tight">Plugin Manager</h2>
            <p className="text-sm text-muted-foreground">Поиск и установка плагинов из CurseForge. Ключ берется из config.yml.</p>
          </div>
          <InputGroup>
            <InputGroupInput
              placeholder="Найдите плагин: worldedit, luckperms, geyser..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') handleSearch();
              }}
            />
            <InputGroupAddon>
              <Search className="h-4 w-4" />
            </InputGroupAddon>
            <InputGroupAddon align="inline-end">
              <Button size="sm" variant="secondary" onClick={handleSearch} disabled={searching}>
                {searching ? 'Поиск...' : 'Искать'}
              </Button>
            </InputGroupAddon>
          </InputGroup>
          {error && (
            <div className="rounded-md border border-destructive/40 bg-destructive/10 px-4 py-2 text-sm text-destructive">
              {error}
            </div>
          )}
        </div>
      </div>

      <div className="container mx-auto max-w-6xl px-4 sm:px-6 lg:px-8 grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="bg-card/60 backdrop-blur-sm border-border/70">
          <CardHeader className="space-y-1">
            <CardTitle>Установленные плагины</CardTitle>
            <CardDescription>{plugins.length ? `Всего: ${plugins.length}` : 'Пока нет плагинов'}</CardDescription>
          </CardHeader>
          <CardContent className="grid gap-3 sm:grid-cols-2">
            {plugins.length === 0 && (
              <div className="col-span-full text-sm text-muted-foreground">Нет установленных плагинов.</div>
            )}
            {plugins.map((plugin) => (
              <div
                key={plugin.name}
                className="rounded-lg border border-border/70 bg-muted/30 p-4 shadow-sm hover:shadow transition-all"
              >
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="font-semibold leading-tight">{plugin.name}</p>
                    <p className="text-xs text-muted-foreground">v{plugin.version}</p>
                  </div>
                  <span
                    className={`text-xs px-2 py-1 rounded-full ${
                      plugin.status === 'enabled'
                        ? 'bg-emerald-100 text-emerald-700'
                        : 'bg-amber-100 text-amber-700'
                    }`}
                  >
                    {plugin.status === 'enabled' ? 'Включен' : 'Выключен'}
                  </span>
                </div>
                <p className="mt-2 text-xs text-muted-foreground line-clamp-2">{plugin.description || 'Нет описания'}</p>
                {plugin.author && (
                  <p className="mt-1 text-xs text-muted-foreground">Автор: {plugin.author}</p>
                )}
              </div>
            ))}
          </CardContent>
        </Card>

        <Card className="bg-card/60 backdrop-blur-sm border-border/70">
          <CardHeader className="space-y-1">
            <CardTitle>CurseForge</CardTitle>
            <CardDescription>Найдено {results.length} результатов</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {searching && (
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Loader />
                <span>Загружаем результаты...</span>
              </div>
            )}

            {!searching && results.length === 0 && (
              <div className="text-sm text-muted-foreground">Начните поиск, чтобы увидеть плагины.</div>
            )}

            <div className="grid gap-3 max-h-[520px] overflow-auto pr-1">
              {results.map((mod) => (
                <div
                  key={mod.id}
                  className="rounded-lg border border-border/70 bg-muted/30 p-4 shadow-sm hover:shadow transition-all"
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="space-y-1">
                      <p className="font-semibold leading-tight">{mod.name}</p>
                      <p className="text-xs text-muted-foreground line-clamp-2">{mod.summary}</p>
                    </div>
                    <Button
                      size="sm"
                      variant="default"
                      disabled={installingId === mod.id}
                      onClick={() => installFromCurseForge(mod.id)}
                    >
                      {installingId === mod.id ? 'Установка...' : 'Установить'}
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}