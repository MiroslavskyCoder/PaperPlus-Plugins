"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Heart, Settings, MessageSquare, Save } from "lucide-react"

interface SimpleHealConfig {
  healHealth: boolean
  healFood: boolean
  clearEffects: boolean
  clearFire: boolean
  cooldown: number
  messages: Record<string, string>
}

export function SimpleHealTab() {
  const [config, setConfig] = useState<SimpleHealConfig>({
    healHealth: true,
    healFood: true,
    clearEffects: true,
    clearFire: true,
    cooldown: 60,
    messages: {}
  })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    fetchConfig()
  }, [])

  const fetchConfig = async () => {
    try {
      const response = await fetch('http://localhost:9092/api/config/simpleheal')
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
      await fetch('http://localhost:9092/api/config/simpleheal', {
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

  const updateMessage = (key: string, value: string) => {
    setConfig({
      ...config,
      messages: { ...config.messages, [key]: value }
    })
  }

  if (loading) {
    return <div className="flex items-center justify-center h-64">Loading...</div>
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-green-400 to-emerald-600 bg-clip-text text-transparent">
            Simple Heal
          </h2>
          <p className="text-muted-foreground mt-1">
            Configure healing commands and cooldowns
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
            <Heart className="h-5 w-5 text-green-500" />
            Heal Settings
          </CardTitle>
          <CardDescription>Configure what gets healed</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="healHealth">Restore Health</Label>
                <p className="text-sm text-muted-foreground">Restore player HP to maximum</p>
              </div>
              <Switch
                id="healHealth"
                checked={config.healHealth}
                onCheckedChange={(checked) => setConfig({ ...config, healHealth: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="healFood">Restore Food</Label>
                <p className="text-sm text-muted-foreground">Restore hunger and saturation</p>
              </div>
              <Switch
                id="healFood"
                checked={config.healFood}
                onCheckedChange={(checked) => setConfig({ ...config, healFood: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="clearEffects">Clear Effects</Label>
                <p className="text-sm text-muted-foreground">Remove all potion effects</p>
              </div>
              <Switch
                id="clearEffects"
                checked={config.clearEffects}
                onCheckedChange={(checked) => setConfig({ ...config, clearEffects: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="clearFire">Clear Fire</Label>
                <p className="text-sm text-muted-foreground">Extinguish burning players</p>
              </div>
              <Switch
                id="clearFire"
                checked={config.clearFire}
                onCheckedChange={(checked) => setConfig({ ...config, clearFire: checked })}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="cooldown" className="flex items-center gap-2">
              <Settings className="h-4 w-4" />
              Cooldown (seconds)
            </Label>
            <Input
              id="cooldown"
              type="number"
              min={0}
              value={config.cooldown}
              onChange={(e) => setConfig({ ...config, cooldown: parseInt(e.target.value) || 0 })}
              className="max-w-xs"
            />
            <p className="text-sm text-muted-foreground">
              Time players must wait between heals (0 to disable)
            </p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <MessageSquare className="h-5 w-5 text-blue-500" />
            Messages
          </CardTitle>
          <CardDescription>Customize plugin messages</CardDescription>
        </CardHeader>
        <CardContent className="space-y-3">
          {Object.entries(config.messages).map(([key, value]) => (
            <div key={key} className="space-y-1">
              <Label htmlFor={key} className="capitalize">
                {key.replace(/-/g, ' ')}
              </Label>
              <Input
                id={key}
                value={value}
                onChange={(e) => updateMessage(key, e.target.value)}
                placeholder={`Enter ${key} message...`}
              />
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  )
}
