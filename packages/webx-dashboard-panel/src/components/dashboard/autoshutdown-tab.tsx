"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import { Power, Clock, AlertTriangle, Save } from "lucide-react"

interface AutoShutdownConfig {
  enabled: boolean
  timeout: number
  warnings: number[]
  shutdownMessage: string
}

export function AutoShutdownTab() {
  const [config, setConfig] = useState<AutoShutdownConfig>({
    enabled: true,
    timeout: 10,
    warnings: [5, 3, 1],
    shutdownMessage: "§cServer shutting down due to inactivity..."
  })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [newWarning, setNewWarning] = useState("")

  useEffect(() => {
    fetchConfig()
  }, [])

  const fetchConfig = async () => {
    try {
      const response = await fetch('http://localhost:9092/api/config/autoshutdown')
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
      await fetch('http://localhost:9092/api/config/autoshutdown', {
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

  const addWarning = () => {
    const warning = parseInt(newWarning)
    if (!isNaN(warning) && warning > 0 && !config.warnings.includes(warning)) {
      setConfig({
        ...config,
        warnings: [...config.warnings, warning].sort((a, b) => b - a)
      })
      setNewWarning("")
    }
  }

  const removeWarning = (warning: number) => {
    setConfig({
      ...config,
      warnings: config.warnings.filter(w => w !== warning)
    })
  }

  if (loading) {
    return <div className="flex items-center justify-center h-64">Loading...</div>
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-red-400 to-orange-600 bg-clip-text text-transparent">
            Auto Shutdown
          </h2>
          <p className="text-muted-foreground mt-1">
            Automatically shutdown server when no players are online
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
            <Power className="h-5 w-5 text-red-500" />
            General Settings
          </CardTitle>
          <CardDescription>Configure auto-shutdown behavior</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <Label htmlFor="enabled">Enable Auto Shutdown</Label>
              <p className="text-sm text-muted-foreground">Automatically stop server when empty</p>
            </div>
            <Switch
              id="enabled"
              checked={config.enabled}
              onCheckedChange={(checked) => setConfig({ ...config, enabled: checked })}
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="timeout" className="flex items-center gap-2">
              <Clock className="h-4 w-4" />
              Timeout (minutes)
            </Label>
            <Input
              id="timeout"
              type="number"
              min={1}
              value={config.timeout}
              onChange={(e) => setConfig({ ...config, timeout: parseInt(e.target.value) || 1 })}
              className="max-w-xs"
            />
            <p className="text-sm text-muted-foreground">
              Time to wait after last player leaves before shutting down
            </p>
          </div>

          <div className="space-y-2">
            <Label htmlFor="message">Shutdown Message</Label>
            <Input
              id="message"
              value={config.shutdownMessage}
              onChange={(e) => setConfig({ ...config, shutdownMessage: e.target.value })}
              placeholder="Enter shutdown message..."
            />
            <p className="text-sm text-muted-foreground">
              Message shown in console when server shuts down
            </p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <AlertTriangle className="h-5 w-5 text-orange-500" />
            Warning Times
          </CardTitle>
          <CardDescription>Configure when warnings are logged (minutes before shutdown)</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex flex-wrap gap-2">
            {config.warnings.map((warning) => (
              <Badge key={warning} variant="secondary" className="gap-2 text-base px-3 py-1">
                {warning} min
                <button
                  onClick={() => removeWarning(warning)}
                  className="ml-1 hover:text-destructive"
                >
                  ×
                </button>
              </Badge>
            ))}
          </div>

          <div className="flex gap-2">
            <Input
              type="number"
              placeholder="Minutes before shutdown"
              value={newWarning}
              onChange={(e) => setNewWarning(e.target.value)}
              className="max-w-xs"
              min={1}
            />
            <Button onClick={addWarning} variant="outline">
              Add Warning
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
