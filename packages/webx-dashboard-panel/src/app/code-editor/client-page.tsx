"use client"

import { useEffect, useMemo, useState } from "react"
import { SerializedEditorState } from "lexical"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { ScrollArea } from "@/components/ui/scroll-area"
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select"
import { FileCode2, RefreshCw, Save, Plus, Loader2, CheckCircle2, XCircle, Pause } from "lucide-react"
import { Textarea } from "@/components/ui/textarea"
import { Editor } from "@/components/blocks/editor-00/editor"

type ScriptInfo = {
  name: string
  loaded: boolean
  success: boolean
  size?: number
  lastModified?: number
  loadedAt?: number
  error?: string
}

const API_BASE = "http://localhost:7072/api/loaderscript"

export default function CodeEditorClientPage() {
  const [scripts, setScripts] = useState<ScriptInfo[]>([])
  const [selected, setSelected] = useState<string>("")
  const [content, setContent] = useState<string>("")
  const [editorState, setEditorState] = useState<SerializedEditorState | undefined>(undefined)
  const [loading, setLoading] = useState<boolean>(false)
  const [saving, setSaving] = useState<boolean>(false)
  const [createOpen, setCreateOpen] = useState<boolean>(false)
  const [newName, setNewName] = useState<string>("")
  const [newContent, setNewContent] = useState<string>("")
  const [status, setStatus] = useState<string>("")
  const [error, setError] = useState<string>("")

  const selectedScript = useMemo(
    () => scripts.find((s) => s.name === selected) ?? null,
    [scripts, selected],
  )

  useEffect(() => {
    void loadScripts()
  }, [])

  const loadScripts = async () => {
    try {
      const response = await fetch(`${API_BASE}/scripts`)
      const data = await response.json()
      setScripts(data.scripts || [])
      if (!selected && data.scripts?.length) {
        const first = data.scripts[0].name as string
        setSelected(first)
        void loadScriptContent(first)
      }
    } catch (err) {
      console.error("Failed to load scripts", err)
      setError("Could not load scripts list")
    }
  }

  const loadScriptContent = async (name: string) => {
    setLoading(true)
    setError("")
    try {
      const response = await fetch(`${API_BASE}/scripts/${name}`)
      const data = await response.json()
      setSelected(name)
      const text = data.content || ""
      setContent(text)
      setEditorState(buildCodeState(text, inferLanguage(name)))
    } catch (err) {
      console.error("Failed to load script content", err)
      setError(`Could not load ${name}`)
    } finally {
      setLoading(false)
    }
  }

  const handleSave = async () => {
    if (!selected) return
    setSaving(true)
    setStatus("")
    setError("")
    try {
      const response = await fetch(`${API_BASE}/scripts/${selected}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content, autoReload: selectedScript?.loaded }),
      })
      const result = await response.json()
      if (result.success) {
        setStatus("Saved")
        await loadScripts()
      } else {
        setError(result.message || "Save failed")
      }
    } catch (err) {
      console.error("Failed to save script", err)
      setError("Save failed")
    } finally {
      setSaving(false)
    }
  }

  const handleReloadFromDisk = async () => {
    if (!selected) return
    await loadScriptContent(selected)
    setStatus("Reloaded from disk")
  }

  const handleCreate = async () => {
    if (!newName.trim()) return
    setSaving(true)
    setStatus("")
    setError("")
    try {
      const response = await fetch(`${API_BASE}/scripts`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: newName.trim(), content: newContent || undefined }),
      })
      const result = await response.json()
      if (result.success) {
        setCreateOpen(false)
        setNewName("")
        setNewContent("")
        await loadScripts()
        setSelected(newName.trim())
        setEditorState(buildCodeState(newContent || "", inferLanguage(newName.trim())))
        setStatus("Created")
      } else {
        setError(result.message || "Create failed")
      }
    } catch (err) {
      console.error("Failed to create script", err)
      setError("Create failed")
    } finally {
      setSaving(false)
    }
  }

  const formatBytes = (bytes: number) => {
    if (bytes < 1024) return `${bytes} B`
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(2)} KB`
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`
  }

  const formatDate = (timestamp?: number) =>
    timestamp ? new Date(timestamp).toLocaleString() : ""

  const languageHint = useMemo(() => {
    const name = selectedScript?.name ?? ""
    return inferLanguagePretty(name)
  }, [selectedScript])

  const inferLanguage = (name: string): string => {
    if (name.endsWith(".tsx")) return "tsx"
    if (name.endsWith(".ts")) return "ts"
    if (name.endsWith(".jsx")) return "jsx"
    if (name.endsWith(".js")) return "js"
    return "plain"
  }

  const inferLanguagePretty = (name: string): string => {
    const lang = inferLanguage(name)
    switch (lang) {
      case "tsx":
        return "TSX"
      case "ts":
        return "TS"
      case "jsx":
        return "JSX"
      case "js":
        return "JS"
      default:
        return "Plain"
    }
  }

  const buildCodeState = (text: string, language: string): SerializedEditorState => ({
    root: {
      children: [
        {
          children: [
            {
              detail: 0,
              format: 0,
              mode: "normal",
              style: "",
              text,
              type: "text",
              version: 1,
            },
          ],
          direction: "ltr",
          format: "",
          indent: 0,
          type: "code",
          language,
          version: 1,
        },
      ],
      direction: "ltr",
      format: "",
      indent: 0,
      type: "root",
      version: 1,
    },
  } as unknown as SerializedEditorState)

  const serializedToText = (state: SerializedEditorState | undefined): string => {
    if (!state?.root?.children?.length) return ""
    const first = state.root.children[0] as { children?: Array<{ text?: string }> }
    if (!first?.children?.length) return ""
    return first.children.map((c) => c.text ?? "").join("")
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between gap-3 flex-wrap">
        <div>
          <h1 className="text-2xl font-bold">LoaderScript Code Editor</h1>
          <p className="text-sm text-muted-foreground">
            Edit TypeScript/JSX scripts and push them back to the LoaderScript runtime.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Dialog open={createOpen} onOpenChange={setCreateOpen}>
            <DialogTrigger asChild>
              <Button variant="secondary">
                <Plus className="h-4 w-4 mr-2" />
                New Script
              </Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create script</DialogTitle>
                <DialogDescription>Use .ts/.tsx/.js/.jsx names.</DialogDescription>
              </DialogHeader>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="new-name">File name</Label>
                  <Input
                    id="new-name"
                    placeholder="example.tsx"
                    value={newName}
                    onChange={(e) => setNewName(e.target.value)}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="new-content">Initial content (optional)</Label>
                  <Textarea
                    id="new-content"
                    rows={8}
                    className="font-mono text-sm"
                    placeholder={`export default function main() {\n  console.log("hello");\n}`}
                    value={newContent}
                    onChange={(e) => setNewContent(e.target.value)}
                  />
                </div>
              </div>
              <DialogFooter>
                <Button variant="ghost" onClick={() => setCreateOpen(false)}>
                  Cancel
                </Button>
                <Button onClick={handleCreate} disabled={saving || !newName.trim()}>
                  {saving ? <Loader2 className="h-4 w-4 mr-2 animate-spin" /> : null}
                  Create
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
          <Button variant="outline" onClick={loadScripts} disabled={loading}>
            <RefreshCw className={`h-4 w-4 mr-2 ${loading ? "animate-spin" : ""}`} />
            Refresh
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <Card className="lg:col-span-1">
          <CardHeader>
            <CardTitle>Scripts</CardTitle>
            <CardDescription>Pick a LoaderScript to edit.</CardDescription>
          </CardHeader>
          <CardContent className="p-0">
            <ScrollArea className="h-[620px]">
              <div className="space-y-1 p-4">
                {scripts.map((script) => (
                  <div
                    key={script.name}
                    className={`p-3 rounded-lg border cursor-pointer transition-colors ${
                      selected === script.name
                        ? "bg-primary/10 border-primary"
                        : "hover:bg-accent"
                    }`}
                    onClick={() => loadScriptContent(script.name)}
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div className="flex items-start gap-2 flex-1 min-w-0">
                        <FileCode2 className="h-4 w-4 mt-0.5" />
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
                            {script.size ? (
                              <Badge variant="outline" className="text-xs">
                                {formatBytes(script.size)}
                              </Badge>
                            ) : null}
                          </div>
                        </div>
                      </div>
                    </div>
                    {script.error ? (
                      <p className="text-xs text-red-500 mt-2 line-clamp-2">{script.error}</p>
                    ) : null}
                  </div>
                ))}
                {scripts.length === 0 ? (
                  <div className="text-center py-12 text-muted-foreground">
                    <FileCode2 className="h-12 w-12 mx-auto mb-4 opacity-20" />
                    <p>No scripts yet</p>
                  </div>
                ) : null}
              </div>
            </ScrollArea>
          </CardContent>
        </Card>

        <Card className="lg:col-span-2">
          <CardHeader>
            <div className="flex items-center justify-between gap-3 flex-wrap">
              <div>
                <CardTitle>{selected || "Select a script"}</CardTitle>
                {selectedScript ? (
                  <CardDescription className="flex items-center gap-3 flex-wrap">
                    {selectedScript.lastModified ? (
                      <span className="text-xs">Modified: {formatDate(selectedScript.lastModified)}</span>
                    ) : null}
                    {selectedScript.loadedAt ? (
                      <span className="text-xs">Loaded: {formatDate(selectedScript.loadedAt)}</span>
                    ) : null}
                    <span className="text-xs">Language: {languageHint}</span>
                  </CardDescription>
                ) : (
                  <CardDescription>Pick a script to start editing.</CardDescription>
                )}
              </div>
              <div className="flex items-center gap-2">
                <Select value={selected} onValueChange={(value) => loadScriptContent(value)}>
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
                <Button variant="outline" onClick={handleReloadFromDisk} disabled={!selected || loading}>
                  <RefreshCw className={`h-4 w-4 mr-2 ${loading ? "animate-spin" : ""}`} />
                  Reload
                </Button>
                <Button onClick={handleSave} disabled={!selected || saving}>
                  {saving ? <Loader2 className="h-4 w-4 mr-2 animate-spin" /> : <Save className="h-4 w-4 mr-2" />}
                  Save
                </Button>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            {selected ? (
              <div className="space-y-4">
                <Editor
                  key={selected}
                  editorSerializedState={editorState}
                  onSerializedChange={(value) => {
                    setEditorState(value)
                    setContent(serializedToText(value))
                  }}
                />
                {status ? (
                  <p className="text-sm text-green-500">{status}</p>
                ) : null}
                {error ? (
                  <p className="text-sm text-red-500">{error}</p>
                ) : null}
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center py-24 text-muted-foreground">
                <FileCode2 className="h-16 w-16 mb-4 opacity-20" />
                <p className="text-lg font-medium">No script selected</p>
                <p className="text-sm">Select or create a LoaderScript to edit.</p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
