'use client'

import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Trash2, RefreshCw } from 'lucide-react'

interface Task {
  id: string
  type: string
  delay: number
  createdAt: number
  remaining: number
}

export function TaskSchedulerTab() {
  const [tasks, setTasks] = useState<Map<string, Task>>(new Map())
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadTasks()
    const interval = setInterval(loadTasks, 2000)
    return () => clearInterval(interval)
  }, [])

  const loadTasks = async () => {
    try {
      const response = await fetch('/api/script/tasks')
      const data = await response.json()

      if (!response.ok || data.success === false) {
        throw new Error(data.error || 'Не удалось получить задачи')
      }

      const taskMap = new Map<string, Task>()
      const now = Date.now()
      const tasksObj = data.tasks || {}

      Object.values(tasksObj).forEach((task: any) => {
        if (!task || !task.id) return
        const elapsed = now - (task.createdAt || now)
        const remaining = Math.max(0, (task.delay || 0) - elapsed)

        taskMap.set(task.id, {
          id: task.id,
          type: task.type || 'unknown',
          delay: task.delay || 0,
          createdAt: task.createdAt || now,
          remaining,
        })
      })

      setTasks(taskMap)
    } catch (error) {
      console.error('Failed to load tasks:', error)
    }
  }

  const handleCancelTask = async (taskId: string) => {
    setLoading(true)
    try {
      const response = await fetch(`/api/script/task/${taskId}`, {
        method: 'DELETE'
      })

      const data = await response.json()

      if (!response.ok || data.success === false) {
        throw new Error(data.error || 'Не удалось отменить задачу')
      }

      setTasks(prev => {
        const newMap = new Map(prev)
        newMap.delete(taskId)
        return newMap
      })
    } catch (error) {
      console.error('Failed to cancel task:', error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>Планировщик Задач</CardTitle>
            <Button
              variant="outline"
              size="sm"
              onClick={loadTasks}
              disabled={loading}
            >
              <RefreshCw className="w-4 h-4" />
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          {tasks.size === 0 ? (
            <div className="text-center py-8 text-muted-foreground">
              Нет активных задач
            </div>
          ) : (
            <div className="space-y-2">
              {Array.from(tasks.entries()).map(([taskId, task]) => (
                <div
                  key={taskId}
                  className="flex items-center justify-between p-3 border rounded-lg bg-muted/50"
                >
                  <div className="flex-1">
                    <div className="font-mono text-sm font-medium">{taskId}</div>
                    <div className="text-xs text-muted-foreground mt-1">
                      Тип: {task.type} • Период: {Math.round(task.delay / 1000)}с
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant={task.remaining > 0 ? 'default' : 'secondary'}>
                      {task.remaining > 0 ? `Осталось ~${Math.round(task.remaining / 1000)}с` : 'Выполняется/циклично'}
                    </Badge>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleCancelTask(taskId)}
                      disabled={loading}
                    >
                      <Trash2 className="w-4 h-4 text-red-500" />
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
