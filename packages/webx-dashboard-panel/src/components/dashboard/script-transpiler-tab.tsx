"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { AlertCircle, CheckCircle2, Loader2, Sparkles } from "lucide-react"

interface TranspileResult {
  success: boolean
  code?: string
  error?: string
  timestamp: number
}

const defaultTs = `// TypeScript / JSX transpilation via swc4j
interface Player {
  name: string
  hp: number
}

const player: Player = { name: "Alex", hp: 20 }
console.log(player)
`;

export function ScriptTranspilerTab() {
  const [code, setCode] = useState(defaultTs)
  const [filename, setFilename] = useState("script.ts")
  const [result, setResult] = useState<TranspileResult | null>(null)
  const [loading, setLoading] = useState(false)

  const handleTranspile = async () => {
    setLoading(true)
    setResult(null)
    try {
      const response = await fetch("/api/script/transpile", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code, filename }),
      })

      const data = await response.json()
      setResult({
        success: Boolean(data.success),
        code: data.code,
        error: data.error,
        timestamp: Date.now(),
      })
    } catch (error) {
      setResult({
        success: false,
        error: error instanceof Error ? error.message : "Unknown error",
        timestamp: Date.now(),
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader className="space-y-2">
          <div className="flex items-center justify-between">
            <div>
              <CardTitle className="flex items-center gap-2">
                <Sparkles className="w-4 h-4 text-primary" />
                TypeScript / JSX Transpiler
              </CardTitle>
              <CardDescription>swc4j (V8 backend) для быстрого преобразования TS/JSX в JS</CardDescription>
            </div>
            <div className="flex gap-2">
              <Input
                value={filename}
                onChange={(e) => setFilename(e.target.value)}
                className="w-40"
                placeholder="script.ts"
              />
              <Button onClick={handleTranspile} disabled={loading}>
                {loading ? (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    Транспиляция...
                  </>
                ) : (
                  "Транспилировать"
                )}
              </Button>
            </div>
          </div>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">TypeScript / JSX</label>
            <Textarea
              value={code}
              onChange={(e) => setCode(e.target.value)}
              rows={12}
              className="font-mono text-sm"
              spellCheck={false}
            />
          </div>
        </CardContent>
      </Card>

      {result && (
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle className="text-base">Результат</CardTitle>
              <CardDescription>swc4j вернул сгенерированный JavaScript</CardDescription>
            </div>
            <Badge variant={result.success ? "default" : "destructive"}>
              {result.success ? (
                <span className="flex items-center gap-1"><CheckCircle2 className="w-3 h-3" /> OK</span>
              ) : (
                <span className="flex items-center gap-1"><AlertCircle className="w-3 h-3" /> Ошибка</span>
              )}
            </Badge>
          </CardHeader>
          <CardContent className="space-y-3">
            {result.success ? (
              <pre className="bg-muted p-3 rounded text-sm overflow-auto max-h-64 font-mono">
                {result.code || ''}
              </pre>
            ) : (
              <div className="bg-red-50 border border-red-200 p-3 rounded text-sm text-red-900">
                {result.error || "Не удалось транспилировать код"}
              </div>
            )}
            <div className="text-xs text-muted-foreground">
              Время: {new Date(result.timestamp).toLocaleTimeString()}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
