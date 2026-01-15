"use client";

import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { 
  FileCode2, 
  Play, 
  Pause, 
  RefreshCw, 
  Trash2, 
  Plus, 
  Download, 
  Upload, 
  FileText,
  CheckCircle2,
  XCircle,
  Clock,
  Code2
} from 'lucide-react';

interface ScriptInfo {
  name: string;
  loaded: boolean;
  success: boolean;
  size?: number;
  lastModified?: number;
  loadedAt?: number;
  error?: string;
}

const getApiBase = () => {
  if (process.env.NEXT_PUBLIC_API_BASE) {
    return `${process.env.NEXT_PUBLIC_API_BASE.replace(/\/$/, "")}/api/loaderscript`
  }
  if (typeof window !== "undefined") {
    return `${window.location.origin}/api/loaderscript`
  }
  return "http://localhost:9092/api/loaderscript"
}

export function ScriptLoaderTab() {
  const [scripts, setScripts] = useState<ScriptInfo[]>([]);
  const [selectedScript, setSelectedScript] = useState<ScriptInfo | null>(null);
  const [scriptContent, setScriptContent] = useState('');
  const [loading, setLoading] = useState(false);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [newScriptName, setNewScriptName] = useState('');
  const [newScriptContent, setNewScriptContent] = useState('');
  const [filter, setFilter] = useState<'all' | 'loaded' | 'unloaded'>('all');

  useEffect(() => {
    loadScripts();
    const interval = setInterval(loadScripts, 5000);
    return () => clearInterval(interval);
  }, []);

  const loadScripts = async () => {
    try {
      const response = await fetch(`${getApiBase()}/scripts`);
      const data = await response.json();
      setScripts(data.scripts || []);
    } catch (error) {
      console.error('Failed to load scripts:', error);
    }
  };

  const loadScriptContent = async (scriptName: string) => {
    try {
      const response = await fetch(`${getApiBase()}/scripts/${scriptName}`);
      const data = await response.json();
      setScriptContent(data.content || '');
      setSelectedScript(scripts.find(s => s.name === scriptName) || null);
    } catch (error) {
      console.error('Failed to load script content:', error);
    }
  };

  const handleLoadScript = async (scriptName: string) => {
    setLoading(true);
    try {
      const response = await fetch(`${getApiBase()}/scripts/${scriptName}/load`, {
        method: 'POST'
      });
      const result = await response.json();
      if (result.success) {
        await loadScripts();
      }
    } catch (error) {
      console.error('Failed to load script:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleReloadScript = async (scriptName: string) => {
    setLoading(true);
    try {
      const response = await fetch(`${getApiBase()}/scripts/${scriptName}/reload`, {
        method: 'POST'
      });
      const result = await response.json();
      if (result.success) {
        await loadScripts();
      }
    } catch (error) {
      console.error('Failed to reload script:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleUnloadScript = async (scriptName: string) => {
    setLoading(true);
    try {
      const response = await fetch(`${getApiBase()}/scripts/${scriptName}/unload`, {
        method: 'POST'
      });
      const result = await response.json();
      if (result.success) {
        await loadScripts();
      }
    } catch (error) {
      console.error('Failed to unload script:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteScript = async (scriptName: string) => {
    if (!confirm(`Are you sure you want to delete ${scriptName}?`)) return;
    
    setLoading(true);
    try {
      const response = await fetch(`${getApiBase()}/scripts/${scriptName}`, {
        method: 'DELETE'
      });
      const result = await response.json();
      if (result.success) {
        await loadScripts();
        if (selectedScript?.name === scriptName) {
          setSelectedScript(null);
          setScriptContent('');
        }
      }
    } catch (error) {
      console.error('Failed to delete script:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSaveScript = async () => {
    if (!selectedScript) return;
    
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:7072/api/loaderscript/scripts/${selectedScript.name}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          content: scriptContent,
          autoReload: selectedScript.loaded 
        })
      });
      const result = await response.json();
      if (result.success) {
        await loadScripts();
      }
    } catch (error) {
      console.error('Failed to save script:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateScript = async () => {
    if (!newScriptName) return;
    
    setLoading(true);
    try {
      const response = await fetch(`${getApiBase()}/scripts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ 
          name: newScriptName,
          content: newScriptContent || undefined
        })
      });
      const result = await response.json();
      if (result.success) {
        await loadScripts();
        setCreateDialogOpen(false);
        setNewScriptName('');
        setNewScriptContent('');
      }
    } catch (error) {
      console.error('Failed to create script:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleReloadAll = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${getApiBase()}/reload-all`, {
        method: 'POST'
      });
      const result = await response.json();
      if (result.success) {
        await loadScripts();
      }
    } catch (error) {
      console.error('Failed to reload all scripts:', error);
    } finally {
      setLoading(false);
    }
  };

  const filteredScripts = scripts.filter(script => {
    if (filter === 'loaded') return script.loaded;
    if (filter === 'unloaded') return !script.loaded;
    return true;
  });

  const formatBytes = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  };

  const formatDate = (timestamp: number) => {
    return new Date(timestamp).toLocaleString();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Script Loader</h2>
          <p className="text-sm text-muted-foreground">
            Manage JavaScript/TypeScript scripts with hot-reload
          </p>
        </div>
        <div className="flex gap-2">
          <Dialog open={createDialogOpen} onOpenChange={setCreateDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="h-4 w-4 mr-2" />
                New Script
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create New Script</DialogTitle>
                <DialogDescription>
                  Create a new JavaScript or TypeScript script file
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div>
                  <Label htmlFor="scriptName">Script Name</Label>
                  <Input
                    id="scriptName"
                    placeholder="my-script.js"
                    value={newScriptName}
                    onChange={(e) => setNewScriptName(e.target.value)}
                  />
                </div>
                <div>
                  <Label htmlFor="scriptContent">Initial Content (Optional)</Label>
                  <Textarea
                    id="scriptContent"
                    placeholder="Leave empty for default template"
                    value={newScriptContent}
                    onChange={(e) => setNewScriptContent(e.target.value)}
                    rows={10}
                    className="font-mono text-sm"
                  />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setCreateDialogOpen(false)}>
                  Cancel
                </Button>
                <Button onClick={handleCreateScript} disabled={loading || !newScriptName}>
                  Create
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
          <Button variant="outline" onClick={handleReloadAll} disabled={loading}>
            <RefreshCw className={`h-4 w-4 mr-2 ${loading ? 'animate-spin' : ''}`} />
            Reload All
          </Button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Total Scripts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{scripts.length}</div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Loaded</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-500">
              {scripts.filter(s => s.loaded).length}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Success</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-500">
              {scripts.filter(s => s.success).length}
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-muted-foreground">Failed</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-red-500">
              {scripts.filter(s => s.loaded && !s.success).length}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Scripts List */}
        <Card className="lg:col-span-1">
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle>Scripts</CardTitle>
              <div className="flex gap-1">
                <Button
                  variant={filter === 'all' ? 'default' : 'ghost'}
                  size="sm"
                  onClick={() => setFilter('all')}
                >
                  All
                </Button>
                <Button
                  variant={filter === 'loaded' ? 'default' : 'ghost'}
                  size="sm"
                  onClick={() => setFilter('loaded')}
                >
                  Loaded
                </Button>
                <Button
                  variant={filter === 'unloaded' ? 'default' : 'ghost'}
                  size="sm"
                  onClick={() => setFilter('unloaded')}
                >
                  Unloaded
                </Button>
              </div>
            </div>
          </CardHeader>
          <CardContent className="p-0">
            <ScrollArea className="h-[600px]">
              <div className="space-y-1 p-4">
                {filteredScripts.map((script) => (
                  <div
                    key={script.name}
                    className={`p-3 rounded-lg border cursor-pointer transition-colors ${
                      selectedScript?.name === script.name
                        ? 'bg-primary/10 border-primary'
                        : 'hover:bg-accent'
                    }`}
                    onClick={() => loadScriptContent(script.name)}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex items-start gap-2 flex-1 min-w-0">
                        <FileCode2 className="h-4 w-4 mt-0.5 flex-shrink-0" />
                        <div className="flex-1 min-w-0">
                          <p className="font-medium text-sm truncate">{script.name}</p>
                          <div className="flex gap-1 mt-1 flex-wrap">
                            {script.loaded ? (
                              script.success ? (
                                <Badge variant="default" className="text-xs bg-green-500">
                                  <CheckCircle2 className="h-3 w-3 mr-1" />
                                  Loaded
                                </Badge>
                              ) : (
                                <Badge variant="destructive" className="text-xs">
                                  <XCircle className="h-3 w-3 mr-1" />
                                  Error
                                </Badge>
                              )
                            ) : (
                              <Badge variant="secondary" className="text-xs">
                                <Pause className="h-3 w-3 mr-1" />
                                Unloaded
                              </Badge>
                            )}
                            {script.size && (
                              <Badge variant="outline" className="text-xs">
                                {formatBytes(script.size)}
                              </Badge>
                            )}
                          </div>
                        </div>
                      </div>
                      <div className="flex gap-1 flex-shrink-0">
                        {!script.loaded && (
                          <Button
                            size="sm"
                            variant="ghost"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleLoadScript(script.name);
                            }}
                            disabled={loading}
                          >
                            <Play className="h-3 w-3" />
                          </Button>
                        )}
                        {script.loaded && (
                          <>
                            <Button
                              size="sm"
                              variant="ghost"
                              onClick={(e) => {
                                e.stopPropagation();
                                handleReloadScript(script.name);
                              }}
                              disabled={loading}
                            >
                              <RefreshCw className="h-3 w-3" />
                            </Button>
                            <Button
                              size="sm"
                              variant="ghost"
                              onClick={(e) => {
                                e.stopPropagation();
                                handleUnloadScript(script.name);
                              }}
                              disabled={loading}
                            >
                              <Pause className="h-3 w-3" />
                            </Button>
                          </>
                        )}
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDeleteScript(script.name);
                          }}
                          disabled={loading}
                        >
                          <Trash2 className="h-3 w-3 text-red-500" />
                        </Button>
                      </div>
                    </div>
                    {script.error && (
                      <p className="text-xs text-red-500 mt-2 line-clamp-2">
                        {script.error}
                      </p>
                    )}
                  </div>
                ))}
                {filteredScripts.length === 0 && (
                  <div className="text-center py-12 text-muted-foreground">
                    <FileCode2 className="h-12 w-12 mx-auto mb-4 opacity-20" />
                    <p>No scripts found</p>
                  </div>
                )}
              </div>
            </ScrollArea>
          </CardContent>
        </Card>

        {/* Script Editor */}
        <Card className="lg:col-span-2">
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle>
                  {selectedScript ? selectedScript.name : 'Select a script'}
                </CardTitle>
                {selectedScript && (
                  <CardDescription className="flex items-center gap-4 mt-1">
                    {selectedScript.lastModified && (
                      <span className="flex items-center gap-1 text-xs">
                        <Clock className="h-3 w-3" />
                        Modified: {formatDate(selectedScript.lastModified)}
                      </span>
                    )}
                    {selectedScript.loadedAt && (
                      <span className="flex items-center gap-1 text-xs">
                        <Clock className="h-3 w-3" />
                        Loaded: {formatDate(selectedScript.loadedAt)}
                      </span>
                    )}
                  </CardDescription>
                )}
              </div>
              <div className="flex items-center gap-2 flex-wrap justify-end">
                <Select
                  value={selectedScript?.name ?? ''}
                  onValueChange={(value) => loadScriptContent(value)}
                >
                  <SelectTrigger className="min-w-[220px]">
                    <SelectValue placeholder="Choose a script" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      <SelectLabel>Scripts</SelectLabel>
                      {scripts.map((script) => (
                        <SelectItem key={script.name} value={script.name}>
                          {script.name}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>

                {selectedScript && (
                  <div className="flex gap-2">
                    <Button onClick={handleSaveScript} disabled={loading}>
                      <Upload className="h-4 w-4 mr-2" />
                      Save
                    </Button>
                    {selectedScript.loaded && (
                      <Button
                        variant="outline"
                        onClick={() => handleReloadScript(selectedScript.name)}
                        disabled={loading}
                      >
                        <RefreshCw className="h-4 w-4 mr-2" />
                        Reload
                      </Button>
                    )}
                  </div>
                )}
              </div>
            </div>
          </CardHeader>
          <CardContent>
            {selectedScript ? (
              <div className="space-y-4">
                <Textarea
                  value={scriptContent}
                  onChange={(e) => setScriptContent(e.target.value)}
                  className="font-mono text-sm min-h-[500px]"
                  placeholder="// Write your JavaScript code here..."
                />
                {selectedScript.error && (
                  <div className="p-4 bg-red-500/10 border border-red-500/20 rounded-lg">
                    <p className="text-sm font-medium text-red-500 mb-2">Error:</p>
                    <pre className="text-xs text-red-400 whitespace-pre-wrap">
                      {selectedScript.error}
                    </pre>
                  </div>
                )}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-24 text-muted-foreground">
                <Code2 className="h-16 w-16 mb-4 opacity-20" />
                <p className="text-lg font-medium">No script selected</p>
                <p className="text-sm">Select a script from the list to edit</p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
