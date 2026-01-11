"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { Switch } from "@/components/ui/switch"
import { Badge } from "@/components/ui/badge"
import { Bug, Shield, MessageSquare, Save } from "lucide-react"

interface MobCatchConfig {
  enabled: boolean
  requirePermission: boolean
  permission: string
  allowedMobs: string[]
  blacklistedMobs: string[]
  messages: Record<string, string>
  eggName: string
}

export function MobCatchTab() {
  const [config, setConfig] = useState<MobCatchConfig>({
    enabled: true,
    requirePermission: true,
    permission: "mobcatch.use",
    allowedMobs: [],
    blacklistedMobs: [],
    messages: {},
    eggName: "§6Captured %mob%"
  })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [newMob, setNewMob] = useState("")
  const [newBlacklist, setNewBlacklist] = useState("")

  useEffect(() => {
    fetchConfig()
  }, [])

  const fetchConfig = async () => {
    try {
      const response = await fetch('http://localhost:9092/api/config/mobcatch')
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
      await fetch('http://localhost:9092/api/config/mobcatch', {
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

  const addAllowedMob = () => {
    const mob = newMob.toUpperCase().trim()
    if (mob && !config.allowedMobs.includes(mob)) {
      setConfig({
        ...config,
        allowedMobs: [...config.allowedMobs, mob].sort()
      })
      setNewMob("")
    }
  }

  const removeAllowedMob = (mob: string) => {
    setConfig({
      ...config,
      allowedMobs: config.allowedMobs.filter(m => m !== mob)
    })
  }

  const addBlacklistedMob = () => {
    const mob = newBlacklist.toUpperCase().trim()
    if (mob && !config.blacklistedMobs.includes(mob)) {
      setConfig({
        ...config,
        blacklistedMobs: [...config.blacklistedMobs, mob].sort()
      })
      setNewBlacklist("")
    }
  }

  const removeBlacklistedMob = (mob: string) => {
    setConfig({
      ...config,
      blacklistedMobs: config.blacklistedMobs.filter(m => m !== mob)
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
          <h2 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-green-500 to-teal-500 bg-clip-text text-transparent">
            Mob Catch
          </h2>
          <p className="text-muted-foreground mt-1">
            Catch mobs with shift + right-click
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
            <Bug className="h-5 w-5 text-green-500" />
            General Settings
          </CardTitle>
          <CardDescription>Configure mob catching behavior</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="flex items-center justify-between p-3 border rounded-lg">
              <div>
                <Label htmlFor="enabled">Enable Mob Catch</Label>
                <p className="text-sm text-muted-foreground">Allow catching mobs</p>
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
                <p className="text-sm text-muted-foreground">Need permission to catch</p>
              </div>
              <Switch
                id="requirePerm"
                checked={config.requirePermission}
                onCheckedChange={(checked) => setConfig({ ...config, requirePermission: checked })}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="permission">Permission Node</Label>
            <Input
              id="permission"
              value={config.permission}
              onChange={(e) => setConfig({ ...config, permission: e.target.value })}
              placeholder="mobcatch.use"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="eggName">Captured Egg Name</Label>
            <Input
              id="eggName"
              value={config.eggName}
              onChange={(e) => setConfig({ ...config, eggName: e.target.value })}
              placeholder="§6Captured %mob%"
            />
            <p className="text-sm text-muted-foreground">Use %mob% for mob name</p>
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle className="text-green-600">Allowed Mobs</CardTitle>
            <CardDescription>Mobs that can be caught</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex flex-wrap gap-2">
              {config.allowedMobs.map((mob) => (
                <Badge key={mob} variant="secondary" className="gap-2">
                  {mob}
                  <button
                    onClick={() => removeAllowedMob(mob)}
                    className="ml-1 hover:text-destructive"
                  >
                    ×
                  </button>
                </Badge>
              ))}
            </div>
            <div className="flex gap-2">
              <Input
                placeholder="MOB_TYPE (e.g., COW)"
                value={newMob}
                onChange={(e) => setNewMob(e.target.value)}
              />
              <Button onClick={addAllowedMob} variant="outline">Add</Button>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-red-600 flex items-center gap-2">
              <Shield className="h-5 w-5" />
              Blacklisted Mobs
            </CardTitle>
            <CardDescription>Mobs that cannot be caught</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex flex-wrap gap-2">
              {config.blacklistedMobs.map((mob) => (
                <Badge key={mob} variant="destructive" className="gap-2">
                  {mob}
                  <button
                    onClick={() => removeBlacklistedMob(mob)}
                    className="ml-1 hover:text-white"
                  >
                    ×
                  </button>
                </Badge>
              ))}
            </div>
            <div className="flex gap-2">
              <Input
                placeholder="MOB_TYPE (e.g., WITHER)"
                value={newBlacklist}
                onChange={(e) => setNewBlacklist(e.target.value)}
              />
              <Button onClick={addBlacklistedMob} variant="outline">Add</Button>
            </div>
          </CardContent>
        </Card>
      </div>

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
