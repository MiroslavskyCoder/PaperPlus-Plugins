"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import { Users, Heart, Sparkles, MessageSquare, Save, Clock } from "lucide-react"

interface Effect {
  type: string
  duration: number
  amplifier: number
}

interface FriendFeedConfig {
  enabled: boolean
  requirePermission: boolean
  permission: string
  restoreHunger: number
  restoreSaturation: number
  giveEffects: boolean
  cooldown: number
  range: number
  effects: Effect[]
  messages: Record<string, string>
}

export function FriendFeedTab() {
  const [config, setConfig] = useState<FriendFeedConfig>({
    enabled: true,
    requirePermission: false,
    permission: "friendfeed.use",
    restoreHunger: 10,
    restoreSaturation: 5.0,
    giveEffects: true,
    cooldown: 30,
    range: 5.0,
    effects: [],
    messages: {}
  })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [newEffect, setNewEffect] = useState({ type: "REGENERATION", duration: 100, amplifier: 0 })

  useEffect(() => {
    fetchConfig()
  }, [])

  const fetchConfig = async () => {
    try {
      const response = await fetch('http://localhost:9092/api/config/friendfeed')
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
      await fetch('http://localhost:9092/api/config/friendfeed', {
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

  const addEffect = () => {
    setConfig({
      ...config,
      effects: [...config.effects, { ...newEffect }]
    })
  }

  const removeEffect = (index: number) => {
    setConfig({
      ...config,
      effects: config.effects.filter((_, i) => i !== index)
    })
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
          <h2 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-pink-500 to-rose-500 bg-clip-text text-transparent">
            Friend Feed
          </h2>
          <p className="text-muted-foreground mt-1">
            Feed friends by sneaking and right-clicking them
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
            <Users className="h-5 w-5 text-pink-500" />
            General Settings
          </CardTitle>
          <CardDescription>Configure friend feeding behavior</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="enabled">Enable Friend Feed</Label>
                <p className="text-sm text-muted-foreground">Allow feeding other players</p>
              </div>
              <Switch
                id="enabled"
                checked={config.enabled}
                onCheckedChange={(checked) => setConfig({ ...config, enabled: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="requirePerm">Require Permission</Label>
                <p className="text-sm text-muted-foreground">Need permission to feed</p>
              </div>
              <Switch
                id="requirePerm"
                checked={config.requirePermission}
                onCheckedChange={(checked) => setConfig({ ...config, requirePermission: checked })}
              />
            </div>

            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="giveEffects">Give Effects</Label>
                <p className="text-sm text-muted-foreground">Apply potion effects</p>
              </div>
              <Switch
                id="giveEffects"
                checked={config.giveEffects}
                onCheckedChange={(checked) => setConfig({ ...config, giveEffects: checked })}
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="hunger" className="flex items-center gap-2">
                <Heart className="h-4 w-4" />
                Restore Hunger
              </Label>
              <Input
                id="hunger"
                type="number"
                min={0}
                max={20}
                value={config.restoreHunger}
                onChange={(e) => setConfig({ ...config, restoreHunger: parseInt(e.target.value) || 0 })}
              />
              <p className="text-sm text-muted-foreground">Amount of hunger restored (0-20)</p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="saturation">Restore Saturation</Label>
              <Input
                id="saturation"
                type="number"
                step="0.1"
                min={0}
                max={20}
                value={config.restoreSaturation}
                onChange={(e) => setConfig({ ...config, restoreSaturation: parseFloat(e.target.value) || 0 })}
              />
              <p className="text-sm text-muted-foreground">Amount of saturation restored (0-20)</p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="cooldown" className="flex items-center gap-2">
                <Clock className="h-4 w-4" />
                Cooldown (seconds)
              </Label>
              <Input
                id="cooldown"
                type="number"
                min={0}
                value={config.cooldown}
                onChange={(e) => setConfig({ ...config, cooldown: parseInt(e.target.value) || 0 })}
              />
              <p className="text-sm text-muted-foreground">Time between feeding attempts</p>
            </div>

            <div className="space-y-2">
              <Label htmlFor="range">Feed Range (blocks)</Label>
              <Input
                id="range"
                type="number"
                step="0.5"
                min={1}
                value={config.range}
                onChange={(e) => setConfig({ ...config, range: parseFloat(e.target.value) || 1 })}
              />
              <p className="text-sm text-muted-foreground">Maximum distance to feed</p>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="permission">Permission Node</Label>
            <Input
              id="permission"
              value={config.permission}
              onChange={(e) => setConfig({ ...config, permission: e.target.value })}
              placeholder="friendfeed.use"
            />
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Sparkles className="h-5 w-5 text-purple-500" />
            Potion Effects
          </CardTitle>
          <CardDescription>Effects given to fed players</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            {config.effects.map((effect, index) => (
              <Card key={index} className="bg-secondary/30">
                <CardContent className="pt-4 flex items-center gap-4">
                  <div className="flex-1 grid grid-cols-3 gap-2">
                    <div>
                      <Label className="text-xs">Type</Label>
                      <Badge variant="outline" className="w-full justify-center">{effect.type}</Badge>
                    </div>
                    <div>
                      <Label className="text-xs">Duration (ticks)</Label>
                      <Badge variant="outline" className="w-full justify-center">{effect.duration}</Badge>
                    </div>
                    <div>
                      <Label className="text-xs">Amplifier</Label>
                      <Badge variant="outline" className="w-full justify-center">{effect.amplifier}</Badge>
                    </div>
                  </div>
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => removeEffect(index)}
                    className="h-8 w-8 p-0"
                  >
                    ×
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>

          <div className="border-t pt-4">
            <h4 className="font-semibold mb-3">Add Effect</h4>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-2">
              <Input
                placeholder="Effect type"
                value={newEffect.type}
                onChange={(e) => setNewEffect({ ...newEffect, type: e.target.value.toUpperCase() })}
              />
              <Input
                type="number"
                placeholder="Duration (ticks)"
                value={newEffect.duration}
                onChange={(e) => setNewEffect({ ...newEffect, duration: parseInt(e.target.value) || 0 })}
              />
              <Input
                type="number"
                placeholder="Amplifier"
                value={newEffect.amplifier}
                onChange={(e) => setNewEffect({ ...newEffect, amplifier: parseInt(e.target.value) || 0 })}
              />
              <Button onClick={addEffect}>Add Effect</Button>
            </div>
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
