'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { AlertCircle, CheckCircle, Loader } from 'lucide-react'

interface ScriptExecutionResult {
  success: boolean
  result?: any
  error?: string
  timestamp: number
}

export function ScriptConsoleTab() {
  const [code, setCode] = useState('// Напишите JavaScript код\nlog("Hello from WebX!")')
  const [result, setResult] = useState<ScriptExecutionResult | null>(null)
  const [loading, setLoading] = useState(false)
  const [executeMode, setExecuteMode] = useState<'sync' | 'async'>('sync')

  const handleExecute = async () => {
    setLoading(true)
    setResult(null)

    try {
      const endpoint = executeMode === 'sync'
        ? '/api/script/execute'
        : '/api/script/execute-async'

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code })
      })

      const data = await response.json()

      if (!response.ok || data.success === false) {
        throw new Error(data.error || 'Не удалось выполнить скрипт')
      }

      setResult({
        success: true,
        result: data.result,
        error: undefined,
        timestamp: Date.now()
      })
    } catch (error) {
      setResult({
        success: false,
        error: error instanceof Error ? error.message : 'Unknown error',
        timestamp: Date.now()
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>JavaScript Console</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">Код</label>
            <Textarea
              value={code}
              onChange={(e) => setCode(e.target.value)}
              placeholder="Напишите JavaScript код..."
              rows={8}
              className="font-mono text-sm"
            />
          </div>

          <div className="flex gap-2">
            <div className="flex gap-2 flex-1">
              <Button
                onClick={handleExecute}
                disabled={loading}
                className="flex-1"
              >
                {loading ? (
                  <>
                    <Loader className="w-4 h-4 mr-2 animate-spin" />
                    Выполнение...
                  </>
                ) : (
                  'Выполнить'
                )}
              </Button>
            </div>
            <select
              value={executeMode}
              onChange={(e) => setExecuteMode(e.target.value as 'sync' | 'async')}
              className="px-3 py-2 border rounded-md text-sm"
            >
              <option value="sync">Синхронно</option>
              <option value="async">Асинхронно</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {result && (
        <Card>
          <CardHeader>
            <div className="flex items-center justify-between">
              <CardTitle className="text-base">Результат</CardTitle>
              <Badge variant={result.success ? 'default' : 'destructive'}>
                {result.success ? (
                  <>
                    <CheckCircle className="w-3 h-3 mr-1" />
                    OK
                  </>
                ) : (
                  <>
                    <AlertCircle className="w-3 h-3 mr-1" />
                    Ошибка
                  </>
                )}
              </Badge>
            </div>
          </CardHeader>
          <CardContent className="space-y-3">
            {result.success ? (
              <>
                <div>
                  <label className="text-sm font-medium">Результат:</label>
                  <pre className="bg-muted p-3 rounded mt-1 overflow-auto max-h-32 text-sm">
                    {typeof result.result === 'object'
                      ? JSON.stringify(result.result, null, 2)
                      : String(result.result)}
                  </pre>
                </div>
              </>
            ) : (
              <div className="bg-red-50 border border-red-200 p-3 rounded">
                <label className="text-sm font-medium text-red-900">Ошибка:</label>
                <pre className="text-sm text-red-800 mt-1 whitespace-pre-wrap">
                  {result.error}
                </pre>
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
