import React, { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'; 
import { BorderTrail } from '@/components/motion-primitives/border-trail'; 
import { Checkbox } from "@/components/ui/checkbox"
import { Label } from "@/components/ui/label"
import { Loader } from '../loader';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

interface AuthPlayerSettings {
    isAuthPlayerEnabled: boolean;
    inputMask?: string;  
}

interface SQLConfig {
    host: string;
    port: number;
    database: string;
    username: string;
    password: string;
    ssl?: boolean;
}

interface RedisConfig {
    host: string;
    port: number;
    password?: string;
    db?: number;
}

interface AllSettings {
    authPlayer: AuthPlayerSettings;
    sqlConfig: SQLConfig;
    redisConfig: RedisConfig;
}

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:9092/api';

const useSettingsApi = () => {
    const [settings, setSettings] = useState<AllSettings | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchSettings();
    }, []);

    const fetchSettings = useCallback(async () => {
        try {
            setIsLoading(true);
            setError(null);
            const response = await fetch(`${API_BASE_URL}/settings`);
            if (!response.ok) throw new Error('Failed to fetch settings');
            const data = await response.json();
            setSettings(data);
        } catch (err) {
            console.error("Error fetching settings:", err);
            // Fallback to default settings
            setSettings({
                authPlayer: {
                    isAuthPlayerEnabled: false,
                    inputMask: "NN-AAA-999"
                },
                sqlConfig: {
                    host: "localhost",
                    port: 5432,
                    database: "webx_dashboard",
                    username: "postgres",
                    password: "",
                    ssl: false
                },
                redisConfig: {
                    host: "localhost",
                    port: 6379,
                    password: "",
                    db: 0
                }
            });
            setError(err instanceof Error ? err.message : 'Unknown error');
        } finally {
            setIsLoading(false);
        }
    }, []);

    const saveSettings = useCallback(async (updatedSettings: Partial<AllSettings>) => {
        if (!settings) return;
        
        const newSettings = { ...settings, ...updatedSettings };
        setSettings(newSettings);
        
        try {
            const response = await fetch(`${API_BASE_URL}/settings`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newSettings)
            });
            
            if (!response.ok) {
                throw new Error('Failed to save settings');
            }
            
            console.log("Настройки успешно сохранены:", newSettings);
        } catch (err) {
            console.error("Error saving settings:", err);
            setError(err instanceof Error ? err.message : 'Failed to save settings');
            // Revert changes
            setSettings(settings);
        }
    }, [settings]);

    const testConnection = useCallback(async (type: 'sql' | 'redis') => {
        try {
            const response = await fetch(`${API_BASE_URL}/settings/test-connection`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    type,
                    config: type === 'sql' ? settings?.sqlConfig : settings?.redisConfig
                })
            });
            
            if (!response.ok) {
                throw new Error('Connection test failed');
            }
            
            const result = await response.json();
            return result.success;
        } catch (err) {
            console.error("Connection test error:", err);
            return false;
        }
    }, [settings]);

    return { settings, isLoading, error, saveSettings, testConnection, refetch: fetchSettings };
};

 
interface SettingsTabProps { 
    // userId: string;
}

export function SettingsTab({ }: SettingsTabProps) {
    const { settings, isLoading, error, saveSettings, testConnection } = useSettingsApi();
    
    const [isAuthEnabled, setIsAuthEnabled] = useState(settings?.authPlayer.isAuthPlayerEnabled || false);
    const [inputMaskValue, setInputMaskValue] = useState(settings?.authPlayer.inputMask || "");
    
    const [sqlConfig, setSqlConfig] = useState<SQLConfig>(settings?.sqlConfig || {
        host: "localhost",
        port: 5432,
        database: "webx_dashboard",
        username: "postgres",
        password: "",
        ssl: false
    });
    
    const [redisConfig, setRedisConfig] = useState<RedisConfig>(settings?.redisConfig || {
        host: "localhost",
        port: 6379,
        password: "",
        db: 0
    });

    const [testingConnection, setTestingConnection] = useState<'sql' | 'redis' | null>(null);
    const [connectionStatus, setConnectionStatus] = useState<Record<string, boolean>>({});

    useEffect(() => {
        if (settings) {
            setIsAuthEnabled(settings.authPlayer.isAuthPlayerEnabled);
            setInputMaskValue(settings.authPlayer.inputMask || "");
            setSqlConfig(settings.sqlConfig);
            setRedisConfig(settings.redisConfig);
        }
    }, [settings]);

    const handleAuthPlayerToggle = (checked: boolean) => {
        setIsAuthEnabled(checked); 
        saveSettings({
            authPlayer: { ...settings!.authPlayer, isAuthPlayerEnabled: checked }
        });
    };
    
    const handleInputMaskChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setInputMaskValue(value);
        saveSettings({
            authPlayer: { ...settings!.authPlayer, inputMask: value }
        });
    };

    const handleSqlConfigChange = (field: keyof SQLConfig, value: any) => {
        const updated = { ...sqlConfig, [field]: value };
        setSqlConfig(updated);
        saveSettings({
            sqlConfig: updated
        });
    };

    const handleRedisConfigChange = (field: keyof RedisConfig, value: any) => {
        const updated = { ...redisConfig, [field]: value };
        setRedisConfig(updated);
        saveSettings({
            redisConfig: updated
        });
    };

    const handleTestConnection = async (type: 'sql' | 'redis') => {
        setTestingConnection(type);
        try {
            const success = await testConnection(type);
            setConnectionStatus(prev => ({ ...prev, [type]: success }));
        } finally {
            setTestingConnection(null);
        }
    };

    if (isLoading) {
        return (
        <div
            key={"wave"}
            className="flex flex-col items-center justify-center gap-2 p-4 bg-primary/10 rounded-lg animate-fade-in"
        >
            <Loader variant={"wave"} size={"lg"}/> 
            <span className="text-muted-foreground text-sm">loading</span>
        </div>
        );
    }

    return (
        <div className="space-y-6 animate-fade-in w-full h-full">
            {error && (
                <div className="p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                    <p className="text-sm text-red-800 dark:text-red-200">{error}</p>
                </div>
            )}
            
            {/* AUTH PLAYER SETTINGS */}
            <Card>
                <CardHeader>
                    <CardTitle>Общие настройки аутентификации (Auth Player)</CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="flex flex-col gap-4">
                        <div className="flex items-center gap-3">
                            <Checkbox 
                                id="authplayer-checkbox"
                                checked={isAuthEnabled}
                                onCheckedChange={(e) => handleAuthPlayerToggle(e.target.checked)}
                            />
                            <Label htmlFor="authplayer-checkbox">
                                Включить авторизацию игрока (Auth Player)
                            </Label>
                        </div>

                        {isAuthEnabled && (
                            <div className="border p-4 rounded-lg bg-gray-50 dark:bg-gray-800 space-y-3 mt-4">
                                <h3 className="font-semibold text-lg text-indigo-600 dark:text-indigo-400">
                                    Настройки маски ввода (Input Mask)
                                </h3>
                                <p className='text-sm text-gray-600 dark:text-gray-400'>
                                    Установите формат, который игрок должен использовать при вводе данных, связанных с аутентификацией (например, серийный номер, код).
                                </p>
                                
                                <div className="flex flex-col gap-2">
                                    <Label htmlFor="input-mask-field">Формат маски:</Label>
                                    <Input
                                        id="input-mask-field"
                                        type="text"
                                        value={inputMaskValue}
                                        onChange={handleInputMaskChange}
                                        placeholder="Например: XXX-000-YYY"
                                    />
                                </div>
                                
                                <p className="text-xs text-gray-500 dark:text-gray-500 mt-2">
                                    Данные обновляются автоматически.
                                </p>
                            </div>
                        )}

                    </div>
                </CardContent>
            </Card>

            {/* SQL CONFIG SETTINGS */}
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                        <span>Конфигурация SQL базы данных</span>
                        {connectionStatus.sql !== undefined && (
                            <span className={`text-xs px-2 py-1 rounded ${connectionStatus.sql ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'}`}>
                                {connectionStatus.sql ? '✓ Подключено' : '✗ Ошибка подключения'}
                            </span>
                        )}
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="sql-host">Хост:</Label>
                                <Input
                                    id="sql-host"
                                    value={sqlConfig.host}
                                    onChange={(e) => handleSqlConfigChange('host', e.target.value)}
                                    placeholder="localhost"
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="sql-port">Порт:</Label>
                                <Input
                                    id="sql-port"
                                    type="number"
                                    value={sqlConfig.port}
                                    onChange={(e) => handleSqlConfigChange('port', parseInt(e.target.value))}
                                    placeholder="5432"
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="sql-database">База данных:</Label>
                                <Input
                                    id="sql-database"
                                    value={sqlConfig.database}
                                    onChange={(e) => handleSqlConfigChange('database', e.target.value)}
                                    placeholder="webx_dashboard"
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="sql-username">Пользователь:</Label>
                                <Input
                                    id="sql-username"
                                    value={sqlConfig.username}
                                    onChange={(e) => handleSqlConfigChange('username', e.target.value)}
                                    placeholder="postgres"
                                />
                            </div>
                            <div className="flex flex-col gap-2 md:col-span-2">
                                <Label htmlFor="sql-password">Пароль:</Label>
                                <Input
                                    id="sql-password"
                                    type="password"
                                    value={sqlConfig.password}
                                    onChange={(e) => handleSqlConfigChange('password', e.target.value)}
                                    placeholder="••••••••"
                                />
                            </div>
                        </div>
                        <div className="flex items-center gap-3">
                            <Checkbox 
                                id="sql-ssl"
                                checked={sqlConfig.ssl || false}
                                onCheckedChange={(checked) => handleSqlConfigChange('ssl', checked)}
                            />
                            <Label htmlFor="sql-ssl">Использовать SSL</Label>
                        </div>
                        <Button 
                            onClick={() => handleTestConnection('sql')}
                            disabled={testingConnection === 'sql'}
                            variant="outline"
                            className="w-full"
                        >
                            {testingConnection === 'sql' ? 'Проверка подключения...' : 'Проверить подключение'}
                        </Button>
                    </div>
                </CardContent>
            </Card>

            {/* REDIS CONFIG SETTINGS */}
            <Card>
                <CardHeader>
                    <CardTitle className="flex items-center justify-between">
                        <span>Конфигурация Redis</span>
                        {connectionStatus.redis !== undefined && (
                            <span className={`text-xs px-2 py-1 rounded ${connectionStatus.redis ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'}`}>
                                {connectionStatus.redis ? '✓ Подключено' : '✗ Ошибка подключения'}
                            </span>
                        )}
                    </CardTitle>
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="redis-host">Хост:</Label>
                                <Input
                                    id="redis-host"
                                    value={redisConfig.host}
                                    onChange={(e) => handleRedisConfigChange('host', e.target.value)}
                                    placeholder="localhost"
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="redis-port">Порт:</Label>
                                <Input
                                    id="redis-port"
                                    type="number"
                                    value={redisConfig.port}
                                    onChange={(e) => handleRedisConfigChange('port', parseInt(e.target.value))}
                                    placeholder="6379"
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="redis-password">Пароль:</Label>
                                <Input
                                    id="redis-password"
                                    type="password"
                                    value={redisConfig.password || ""}
                                    onChange={(e) => handleRedisConfigChange('password', e.target.value)}
                                    placeholder="Оставьте пусто если нет пароля"
                                />
                            </div>
                            <div className="flex flex-col gap-2">
                                <Label htmlFor="redis-db">БД (номер):</Label>
                                <Input
                                    id="redis-db"
                                    type="number"
                                    min="0"
                                    max="15"
                                    value={redisConfig.db || 0}
                                    onChange={(e) => handleRedisConfigChange('db', parseInt(e.target.value))}
                                    placeholder="0"
                                />
                            </div>
                        </div>
                        <Button 
                            onClick={() => handleTestConnection('redis')}
                            disabled={testingConnection === 'redis'}
                            variant="outline"
                            className="w-full"
                        >
                            {testingConnection === 'redis' ? 'Проверка подключения...' : 'Проверить подключение'}
                        </Button>
                    </div>
                </CardContent>
            </Card>

        </div>
    );
}