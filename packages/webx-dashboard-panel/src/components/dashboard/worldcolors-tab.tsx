"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import { Palette, RefreshCw, Save } from "lucide-react"

interface WorldColorConfig {
  enabled: boolean
  worldColors: Record<string, string>
  updateInterval: number
}

const AVAILABLE_COLORS = [
  "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE",
  "GOLD", "GRAY", "DARK_GRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHT_PURPLE",
  "YELLOW", "WHITE"
]

export function WorldColorsTab() {
  const [config, setConfig] = useState<WorldColorConfig>({
    enabled: true,
    worldColors: {},
    updateInterval: 20
  })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [newWorld, setNewWorld] = useState("")
  const [newColor, setNewColor] = useState("GREEN")

  useEffect(() => {
    fetchConfig()
  }, [])

  const fetchConfig = async () => {
    try {
      const response = await fetch('http://localhost:9092/api/config/worldcolors')
      const data = await response.json()
      setConfig(data)
    } catch (error) {
      console.error('Failed to fetch config:', error)
    } finally {
      setLoading(false)
    }
  }

  const saveConfig = async () => {
    setSaving(true)
    try {
      await fetch('http://localhost:9092/api/config/worldcolors', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(config)
      })
    } catch (error) {
      console.error('Failed to save config:', error)
    } finally {
      setSaving(false)
    }
  }

  const addWorld = () => {
    if (newWorld && !config.worldColors[newWorld]) {
      setConfig({
        ...config,
        worldColors: { ...config.worldColors, [newWorld]: newColor }
      })
      setNewWorld("")
    }
  }

  const removeWorld = (world: string) => {
    const newColors = { ...config.worldColors }
    delete newColors[world]
    setConfig({ ...config, worldColors: newColors })
  }

  if (loading) {
    return <div className="flex items-center justify-center h-64">Loading...</div>
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-purple-400 to-pink-600 bg-clip-text text-transparent">
            World Colors
          </h2>
          <p className="text-muted-foreground mt-1">
            Configure player name colors based on their world
          </p>
        </div>
        <Button onClick={saveConfig} disabled={saving} className="gap-2">
          <Save className="h-4 w-4" />
          {saving ? 'Saving...' : 'Save Changes'}
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Palette className="h-5 w-5 text-purple-500" />
            General Settings
          </CardTitle>
          <CardDescription>Enable or disable world-based name coloring</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between">
            <Label htmlFor="enabled">Enable World Colors</Label>
            <Switch
              id="enabled"
              checked={config.enabled}
              onCheckedChange={(checked) => setConfig({ ...config, enabled: checked })}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="interval">Update Interval (ticks)</Label>
            <Input
              id="interval"
              type="number"
              value={config.updateInterval}
              onChange={(e) => setConfig({ ...config, updateInterval: parseInt(e.target.value) })}
              className="max-w-xs"
            />
            <p className="text-sm text-muted-foreground">How often to update player names (20 ticks = 1 second)</p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>World Color Mapping</CardTitle>
          <CardDescription>Assign colors to different worlds</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {Object.entries(config.worldColors).map(([world, color]) => (
              <Card key={world} className="bg-secondary/30">
                <CardContent className="pt-6 space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="font-medium">{world}</span>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => removeWorld(world)}
                      className="h-8 w-8 p-0"
                    >
                      ×
                    </Button>
                  </div>
                  <select
                    value={color}
                    onChange={(e) => setConfig({
                      ...config,
                      worldColors: { ...config.worldColors, [world]: e.target.value }
                    })}
                    className="w-full px-3 py-2 bg-background border rounded-md"
                  >
                    {AVAILABLE_COLORS.map(c => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                  <Badge variant="outline" className="w-full justify-center">
                    {color}
                  </Badge>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="border-t pt-4 mt-4">
            <h4 className="font-semibold mb-3">Add New World</h4>
            <div className="flex gap-2">
              <Input
                placeholder="World name (e.g., world_nether)"
                value={newWorld}
                onChange={(e) => setNewWorld(e.target.value)}
              />
              <select
                value={newColor}
                onChange={(e) => setNewColor(e.target.value)}
                className="px-3 py-2 bg-background border rounded-md min-w-[150px]"
              >
                {AVAILABLE_COLORS.map(c => (
                  <option key={c} value={c}>{c}</option>
                ))}
              </select>
              <Button onClick={addWorld}>Add</Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
