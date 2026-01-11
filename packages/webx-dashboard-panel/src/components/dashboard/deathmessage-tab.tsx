"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Skull, Settings, MessageSquare, Save, MapPin } from "lucide-react"

interface DeathMessageConfig {
  enabled: boolean
  broadcast: boolean
  showLocation: boolean
  showWorld: boolean
  messageFormat: string
  causes: Record<string, string>
}

export function DeathMessageTab() {
  const [config, setConfig] = useState<DeathMessageConfig>({
    enabled: true,
    broadcast: true,
    showLocation: true,
    showWorld: false,
    messageFormat: "§c☠ §7%player% §cdied",
    causes: {}
  })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    fetchConfig()
  }, [])

  const fetchConfig = async () => {
    try {
      const response = await fetch('http://localhost:9092/api/config/deathmessage')
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
      await fetch('http://localhost:9092/api/config/deathmessage', {
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

  const updateCause = (key: string, value: string) => {
    setConfig({
      ...config,
      causes: { ...config.causes, [key]: value }
    })
  }

  if (loading) {
    return <div className="flex items-center justify-center h-64">Loading...</div>
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-red-600 to-rose-400 bg-clip-text text-transparent">
            Death Messages
          </h2>
          <p className="text-muted-foreground mt-1">
            Customize death messages with detailed information
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
            <Skull className="h-5 w-5 text-red-500" />
            General Settings
          </CardTitle>
          <CardDescription>Configure death message behavior</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="enabled">Enable Custom Messages</Label>
                <p className="text-sm text-muted-foreground">Show custom death messages</p>
              </div>
              <Switch
                id="enabled"
                checked={config.enabled}
                onCheckedChange={(checked) => setConfig({ ...config, enabled: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="broadcast">Broadcast to All</Label>
                <p className="text-sm text-muted-foreground">Show to all players</p>
              </div>
              <Switch
                id="broadcast"
                checked={config.broadcast}
                onCheckedChange={(checked) => setConfig({ ...config, broadcast: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="showLocation" className="flex items-center gap-1">
                  <MapPin className="h-4 w-4" />
                  Show Location
                </Label>
                <p className="text-sm text-muted-foreground">Display death coordinates</p>
              </div>
              <Switch
                id="showLocation"
                checked={config.showLocation}
                onCheckedChange={(checked) => setConfig({ ...config, showLocation: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="showWorld">Show World Name</Label>
                <p className="text-sm text-muted-foreground">Display world name</p>
              </div>
              <Switch
                id="showWorld"
                checked={config.showWorld}
                onCheckedChange={(checked) => setConfig({ ...config, showWorld: checked })}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="format">Default Message Format</Label>
            <Input
              id="format"
              value={config.messageFormat}
              onChange={(e) => setConfig({ ...config, messageFormat: e.target.value })}
              placeholder="§c☠ §7%player% §cdied"
            />
            <p className="text-sm text-muted-foreground">
              Placeholders: %player%, %killer%, %cause%, %world%, %location%
            </p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <MessageSquare className="h-5 w-5 text-orange-500" />
            Death Cause Messages
          </CardTitle>
          <CardDescription>Customize messages for different death causes</CardDescription>
        </CardHeader>
        <CardContent className="space-y-3">
          {Object.entries(config.causes).map(([cause, message]) => (
            <div key={cause} className="space-y-1">
              <Label htmlFor={cause} className="capitalize font-medium">
                {cause.replace(/-/g, ' ')}
              </Label>
              <Input
                id={cause}
                value={message}
                onChange={(e) => updateCause(cause, e.target.value)}
                placeholder={`Enter ${cause} death message...`}
              />
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  )
}
