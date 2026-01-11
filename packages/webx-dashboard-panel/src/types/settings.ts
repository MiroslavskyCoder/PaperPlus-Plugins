/**
 * Settings API TypeScript Types
 * These types can be imported and used in any TypeScript/React component
 */

// Auth Player Configuration
export interface AuthPlayerSettings {
  isAuthPlayerEnabled: boolean;
  inputMask?: string;
}

// SQL Database Configuration
export interface SQLConfig {
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  ssl?: boolean;
}

// Redis Cache Configuration
export interface RedisConfig {
  host: string;
  port: number;
  password?: string;
  db?: number;
}

// Complete Settings Object
export interface AllSettings {
  authPlayer: AuthPlayerSettings;
  sqlConfig: SQLConfig;
  redisConfig: RedisConfig;
}

// API Request/Response Types
export interface TestConnectionRequest {
  type: 'sql' | 'redis';
  config: AllSettings;
}

export interface TestConnectionResponse {
  success: boolean;
}

// Hook Return Type
export interface UseSettingsApiReturn {
  settings: AllSettings | null;
  isLoading: boolean;
  error: string | null;
  saveSettings: (updatedSettings: Partial<AllSettings>) => Promise<void>;
  testConnection: (type: 'sql' | 'redis') => Promise<boolean>;
  refetch: () => Promise<void>;
}

/**
 * Example usage in components:
 * 
 * import { AllSettings, TestConnectionResponse } from '@/types/settings'
 * 
 * const MyComponent: React.FC = () => {
 *   const [settings, setSettings] = useState<AllSettings | null>(null)
 *   
 *   const updateSQL = async (config: SQLConfig) => {
 *     // update logic
 *   }
 *   
 *   return (
 *     <div>
 *       {settings && (
 *         <div>
 *           <p>{settings.sqlConfig.host}</p>
 *           <p>{settings.redisConfig.port}</p>
 *         </div>
 *       )}
 *     </div>
 *   )
 * }
 */

// Helper function to validate settings
export function validateSettings(settings: any): settings is AllSettings {
  return (
    settings &&
    typeof settings.authPlayer === 'object' &&
    typeof settings.sqlConfig === 'object' &&
    typeof settings.redisConfig === 'object' &&
    typeof settings.authPlayer.isAuthPlayerEnabled === 'boolean' &&
    typeof settings.sqlConfig.host === 'string' &&
    typeof settings.redisConfig.host === 'string'
  );
}

// Helper function to create default settings
export function createDefaultSettings(): AllSettings {
  return {
    authPlayer: {
      isAuthPlayerEnabled: false,
      inputMask: 'NN-AAA-999',
    },
    sqlConfig: {
      host: 'localhost',
      port: 5432,
      database: 'webx_dashboard',
      username: 'postgres',
      password: '',
      ssl: false,
    },
    redisConfig: {
      host: 'localhost',
      port: 6379,
      password: '',
      db: 0,
    },
  };
}

// Helper function to merge partial settings
export function mergeSettings(
  current: AllSettings,
  partial: Partial<AllSettings>
): AllSettings {
  return {
    authPlayer: { ...current.authPlayer, ...partial.authPlayer },
    sqlConfig: { ...current.sqlConfig, ...partial.sqlConfig },
    redisConfig: { ...current.redisConfig, ...partial.redisConfig },
  };
}

// Settings API client class (alternative to hook)
export class SettingsApiClient {
  private baseUrl: string;

  constructor(baseUrl: string = 'http://localhost:9092/api') {
    this.baseUrl = baseUrl;
  }

  async getSettings(): Promise<AllSettings> {
    const response = await fetch(`${this.baseUrl}/settings`);
    if (!response.ok) {
      throw new Error(`Failed to fetch settings: ${response.statusText}`);
    }
    return response.json();
  }

  async updateSettings(settings: AllSettings): Promise<AllSettings> {
    const response = await fetch(`${this.baseUrl}/settings`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(settings),
    });
    if (!response.ok) {
      throw new Error(`Failed to update settings: ${response.statusText}`);
    }
    return response.json();
  }

  async testConnection(
    type: 'sql' | 'redis',
    config: AllSettings
  ): Promise<boolean> {
    const response = await fetch(`${this.baseUrl}/settings/test-connection`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ type, config }),
    });
    if (!response.ok) {
      throw new Error(`Failed to test connection: ${response.statusText}`);
    }
    const data: TestConnectionResponse = await response.json();
    return data.success;
  }

  /**
   * Example usage:
   * 
   * const client = new SettingsApiClient()
   * const settings = await client.getSettings()
   * const success = await client.testConnection('sql', settings)
   */
}
