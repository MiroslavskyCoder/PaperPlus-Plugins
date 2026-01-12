'use client'

import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Trash2, RefreshCw } from 'lucide-react'

interface Task {
  id: string
  active: boolean
  cancelled: boolean
  remainingTime: number
  scheduledTime: number
}

interface TaskInfo {
  success: boolean
  count: number
  tasks: string[]
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
      const data: TaskInfo = await response.json()
      
      if (data.success) {
        // Получить детали каждой задачи
        const taskMap = new Map<string, Task>()
        for (const taskId of data.tasks) {
          // Здесь должен быть получение информации о каждой задаче
          taskMap.set(taskId, {
            id: taskId,
            active: true,
            cancelled: false,
            remainingTime: 0,
            scheduledTime: Date.now()
          })
        }
        setTasks(taskMap)
      }
    } catch (error) {
      console.error('Failed to load tasks:', error)
    }
  }

  const handleCancelTask = async (taskId: string) => {
    setLoading(true)
    try {
      await fetch(`/api/script/task/${taskId}`, {
        method: 'DELETE'
      })
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
                      Осталось: {Math.round(task.remainingTime / 1000)}сек
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant={task.active ? 'default' : 'secondary'}>
                      {task.active ? 'Активна' : 'Завершена'}
                    </Badge>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={() => handleCancelTask(taskId)}
                      disabled={!task.active || loading}
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
