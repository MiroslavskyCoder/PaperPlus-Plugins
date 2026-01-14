'use client'

import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Send, RefreshCw } from 'lucide-react'

interface Event {
  name: string
  listenerCount: number
  timestamp: number
}

export function EventSystemTab() {
  const [events, setEvents] = useState<Event[]>([])
  const [eventName, setEventName] = useState('')
  const [eventArgs, setEventArgs] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadEvents()
    const interval = setInterval(loadEvents, 3000)
    return () => clearInterval(interval)
  }, [])

  const loadEvents = async () => {
    try {
      const response = await fetch('/api/script/events')
      const data = await response.json()

      if (!response.ok || !data.success || !Array.isArray(data.events)) {
        throw new Error(data.error || 'Не удалось получить список событий')
      }

      const eventsList = await Promise.all(
        data.events.map(async (name: string) => {
          const listenerResponse = await fetch(`/api/script/listeners/${name}`)
          const listenerData = await listenerResponse.json()

          if (!listenerResponse.ok || listenerData.success === false) {
            return { name, listenerCount: 0, timestamp: Date.now() }
          }

          return {
            name,
            listenerCount: listenerData.listeners ?? listenerData.listenerCount ?? 0,
            timestamp: Date.now()
          }
        })
      )

      setEvents(eventsList)
    } catch (error) {
      console.error('Failed to load events:', error)
    }
  }

  const handleEmitEvent = async () => {
    if (!eventName.trim()) return

    setLoading(true)
    try {
      let args = []
      if (eventArgs.trim()) {
        try {
          args = JSON.parse(`[${eventArgs}]`)
        } catch {
          args = [eventArgs]
        }
      }

      const response = await fetch(`/api/script/event/${eventName}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ args, async: false })
      })

      if (!response.ok) {
        throw new Error('Не удалось отправить событие')
      }

      setEventName('')
      setEventArgs('')
      await loadEvents()
    } catch (error) {
      console.error('Failed to emit event:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>Система Событий</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">Запустить событие</label>
            <div className="flex gap-2">
              <Input
                placeholder="Имя события"
                value={eventName}
                onChange={(e) => setEventName(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleEmitEvent()}
              />
              <Button
                onClick={handleEmitEvent}
                disabled={!eventName.trim() || loading}
              >
                <Send className="w-4 h-4" />
              </Button>
            </div>
            <Input
              placeholder="Аргументы (JSON): 'hello', 42, true"
              value={eventArgs}
              onChange={(e) => setEventArgs(e.target.value)}
              className="text-xs"
            />
          </div>

          <div>
            <div className="flex items-center justify-between mb-2">
              <label className="text-sm font-medium">Зарегистрированные события</label>
              <Button
                variant="ghost"
                size="sm"
                onClick={loadEvents}
                disabled={loading}
              >
                <RefreshCw className="w-4 h-4" />
              </Button>
            </div>

            {events.length === 0 ? (
              <div className="text-center py-8 text-muted-foreground">
                Нет событий
              </div>
            ) : (
              <div className="space-y-2">
                {events.map((event) => (
                  <div
                    key={event.name}
                    className="flex items-center justify-between p-3 border rounded-lg bg-muted/50"
                  >
                    <div>
                      <div className="font-medium text-sm">{event.name}</div>
                    </div>
                    <Badge variant="outline">
                      {event.listenerCount} слушателей
                    </Badge>
                  </div>
                ))}
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
