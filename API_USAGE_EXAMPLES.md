# 📖 API Usage Examples

Примеры использования API для разработчиков.

## 🌐 JavaScript / Browser

### Economy API

```javascript
// Получить монеты игрока
async function getPlayerCoins(uuid) {
    const response = await fetch(`http://localhost:9092/api/player/${uuid}/coins`);
    const data = await response.json();
    
    if (data.success) {
        console.log(`Player has ${data.data.coins} coins`);
        console.log(`Bank: ${data.data.bankBalance}`);
        console.log(`Total: ${data.data.total}`);
    }
}

// Топ 10 игроков
async function getTopPlayers() {
    const response = await fetch('http://localhost:9092/api/players/top?limit=10');
    const data = await response.json();
    
    if (data.success) {
        data.data.forEach((player, index) => {
            console.log(`#${index + 1}: ${player.uuid} - ${player.total} coins`);
        });
    }
}
```

### Shop API

```javascript
// Получить все товары
async function getShopItems() {
    const response = await fetch('http://localhost:9092/api/shop');
    const data = await response.json();
    return data.data;
}

// Добавить товар
async function addShopItem(name, material, price, icon = null) {
    const item = {
        id: Date.now().toString(),
        name: name,
        material: material,
        price: price,
        icon: icon
    };
    
    const response = await fetch('http://localhost:9092/api/shop', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(item)
    });
    
    return await response.json();
}

// Обновить товар
async function updateShopItem(id, name, material, price, icon = null) {
    const item = {
        name: name,
        material: material,
        price: price,
        icon: icon
    };
    
    const response = await fetch(`http://localhost:9092/api/shop/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(item)
    });
    
    return await response.json();
}

// Удалить товар
async function deleteShopItem(id) {
    const response = await fetch(`http://localhost:9092/api/shop/${id}`, {
        method: 'DELETE'
    });
    return await response.json();
}
```

### AFK API

```javascript
// Получить настройки AFK
async function getAfkConfig() {
    const response = await fetch('http://localhost:9092/api/afk');
    const data = await response.json();
    return data.data;
}

// Обновить настройки AFK
async function updateAfkConfig(timeout, kickEnabled, kickTimeout, prefix) {
    const config = {
        timeout: timeout,
        kickEnabled: kickEnabled,
        kickTimeout: kickTimeout,
        prefix: prefix,
        suffix: '',
        broadcastEnabled: true,
        autoResumeEnabled: true
    };
    
    const response = await fetch('http://localhost:9092/api/afk', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(config)
    });
    
    return await response.json();
}
```

---

## 🐍 Python

```python
import requests

BASE_URL = 'http://localhost:9092/api'

# Economy API
def get_player_coins(uuid):
    response = requests.get(f'{BASE_URL}/player/{uuid}/coins')
    data = response.json()
    
    if data['success']:
        print(f"Coins: {data['data']['coins']}")
        print(f"Bank: {data['data']['bankBalance']}")
        print(f"Total: {data['data']['total']}")

def get_top_players(limit=10):
    response = requests.get(f'{BASE_URL}/players/top', params={'limit': limit})
    data = response.json()
    
    if data['success']:
        for i, player in enumerate(data['data'], 1):
            print(f"#{i}: {player['uuid']} - {player['total']} coins")

# Shop API
def get_shop_items():
    response = requests.get(f'{BASE_URL}/shop')
    return response.json()['data']

def add_shop_item(name, material, price, icon=None):
    item = {
        'id': str(int(time.time() * 1000)),
        'name': name,
        'material': material,
        'price': price,
        'icon': icon
    }
    
    response = requests.post(f'{BASE_URL}/shop', json=item)
    return response.json()

def update_shop_item(item_id, name, material, price, icon=None):
    item = {
        'name': name,
        'material': material,
        'price': price,
        'icon': icon
    }
    
    response = requests.put(f'{BASE_URL}/shop/{item_id}', json=item)
    return response.json()

def delete_shop_item(item_id):
    response = requests.delete(f'{BASE_URL}/shop/{item_id}')
    return response.json()

# AFK API
def get_afk_config():
    response = requests.get(f'{BASE_URL}/afk')
    return response.json()['data']

def update_afk_config(timeout, kick_enabled, kick_timeout, prefix):
    config = {
        'timeout': timeout,
        'kickEnabled': kick_enabled,
        'kickTimeout': kick_timeout,
        'prefix': prefix,
        'suffix': '',
        'broadcastEnabled': True,
        'autoResumeEnabled': True
    }
    
    response = requests.put(f'{BASE_URL}/afk', json=config)
    return response.json()

# Пример использования
if __name__ == '__main__':
    # Test economy
    get_top_players(5)
    
    # Test shop
    items = get_shop_items()
    print(f"Shop has {len(items)} items")
    
    # Add item
    result = add_shop_item('Iron Sword', 'IRON_SWORD', 25.0)
    print(f"Added item: {result}")
    
    # Test AFK
    config = get_afk_config()
    print(f"AFK timeout: {config['timeout']} minutes")
```

---

## 💻 PowerShell

```powershell
$BASE_URL = "http://localhost:9092/api"

# Economy API
function Get-PlayerCoins {
    param([string]$UUID)
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/player/$UUID/coins"
    
    if ($response.success) {
        Write-Host "Coins: $($response.data.coins)"
        Write-Host "Bank: $($response.data.bankBalance)"
        Write-Host "Total: $($response.data.total)"
    }
}

function Get-TopPlayers {
    param([int]$Limit = 10)
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/players/top?limit=$Limit"
    
    if ($response.success) {
        $i = 1
        foreach ($player in $response.data) {
            Write-Host "#$i: $($player.uuid) - $($player.total) coins"
            $i++
        }
    }
}

# Shop API
function Get-ShopItems {
    $response = Invoke-RestMethod -Uri "$BASE_URL/shop"
    return $response.data
}

function Add-ShopItem {
    param(
        [string]$Name,
        [string]$Material,
        [double]$Price,
        [string]$Icon = $null
    )
    
    $item = @{
        id = [string](Get-Date).Ticks
        name = $Name
        material = $Material
        price = $Price
        icon = $Icon
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/shop" -Method POST `
        -Body $item -ContentType "application/json"
    
    return $response
}

function Update-ShopItem {
    param(
        [string]$Id,
        [string]$Name,
        [string]$Material,
        [double]$Price,
        [string]$Icon = $null
    )
    
    $item = @{
        name = $Name
        material = $Material
        price = $Price
        icon = $Icon
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/shop/$Id" -Method PUT `
        -Body $item -ContentType "application/json"
    
    return $response
}

function Remove-ShopItem {
    param([string]$Id)
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/shop/$Id" -Method DELETE
    return $response
}

# AFK API
function Get-AfkConfig {
    $response = Invoke-RestMethod -Uri "$BASE_URL/afk"
    return $response.data
}

function Update-AfkConfig {
    param(
        [int]$Timeout,
        [bool]$KickEnabled,
        [int]$KickTimeout,
        [string]$Prefix
    )
    
    $config = @{
        timeout = $Timeout
        kickEnabled = $KickEnabled
        kickTimeout = $KickTimeout
        prefix = $Prefix
        suffix = ""
        broadcastEnabled = $true
        autoResumeEnabled = $true
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$BASE_URL/afk" -Method PUT `
        -Body $config -ContentType "application/json"
    
    return $response
}

# Примеры использования
Get-TopPlayers -Limit 5
Get-ShopItems
Add-ShopItem -Name "Diamond" -Material "DIAMOND" -Price 50.0
Get-AfkConfig
Update-AfkConfig -Timeout 15 -KickEnabled $true -KickTimeout 30 -Prefix "[AFK] "
```

---

## ☕ Java (Bukkit Plugin)

```java
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:9092/api";
    private static final Gson gson = new Gson();
    
    // Economy API
    public static JsonObject getPlayerCoins(String uuid) throws Exception {
        String response = sendGet(BASE_URL + "/player/" + uuid + "/coins");
        return gson.fromJson(response, JsonObject.class);
    }
    
    public static JsonObject getTopPlayers(int limit) throws Exception {
        String response = sendGet(BASE_URL + "/players/top?limit=" + limit);
        return gson.fromJson(response, JsonObject.class);
    }
    
    // Shop API
    public static JsonObject getShopItems() throws Exception {
        String response = sendGet(BASE_URL + "/shop");
        return gson.fromJson(response, JsonObject.class);
    }
    
    public static JsonObject addShopItem(String name, String material, double price, String icon) throws Exception {
        JsonObject item = new JsonObject();
        item.addProperty("id", String.valueOf(System.currentTimeMillis()));
        item.addProperty("name", name);
        item.addProperty("material", material);
        item.addProperty("price", price);
        item.addProperty("icon", icon);
        
        String response = sendPost(BASE_URL + "/shop", item.toString());
        return gson.fromJson(response, JsonObject.class);
    }
    
    public static JsonObject deleteShopItem(String id) throws Exception {
        String response = sendDelete(BASE_URL + "/shop/" + id);
        return gson.fromJson(response, JsonObject.class);
    }
    
    // AFK API
    public static JsonObject getAfkConfig() throws Exception {
        String response = sendGet(BASE_URL + "/afk");
        return gson.fromJson(response, JsonObject.class);
    }
    
    public static JsonObject updateAfkConfig(int timeout, boolean kickEnabled, int kickTimeout, String prefix) throws Exception {
        JsonObject config = new JsonObject();
        config.addProperty("timeout", timeout);
        config.addProperty("kickEnabled", kickEnabled);
        config.addProperty("kickTimeout", kickTimeout);
        config.addProperty("prefix", prefix);
        config.addProperty("suffix", "");
        config.addProperty("broadcastEnabled", true);
        config.addProperty("autoResumeEnabled", true);
        
        String response = sendPut(BASE_URL + "/afk", config.toString());
        return gson.fromJson(response, JsonObject.class);
    }
    
    // HTTP Helper Methods
    private static String sendGet(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }
    
    private static String sendPost(String urlString, String jsonBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }
    
    private static String sendPut(String urlString, String jsonBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }
    
    private static String sendDelete(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }
}
```

---

## 🧪 cURL (Bash/Linux)

```bash
BASE_URL="http://localhost:9092/api"

# Economy API
# Get player coins
curl "$BASE_URL/player/{UUID}/coins"

# Get top players
curl "$BASE_URL/players/top?limit=10"

# Shop API
# Get all items
curl "$BASE_URL/shop"

# Add item
curl -X POST "$BASE_URL/shop" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "123",
    "name": "Diamond Sword",
    "material": "DIAMOND_SWORD",
    "price": 100,
    "icon": null
  }'

# Update item
curl -X PUT "$BASE_URL/shop/123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Diamond Sword",
    "material": "DIAMOND_SWORD",
    "price": 150,
    "icon": null
  }'

# Delete item
curl -X DELETE "$BASE_URL/shop/123"

# AFK API
# Get config
curl "$BASE_URL/afk"

# Update config
curl -X PUT "$BASE_URL/afk" \
  -H "Content-Type: application/json" \
  -d '{
    "timeout": 15,
    "kickEnabled": true,
    "kickTimeout": 30,
    "prefix": "[AFK] ",
    "suffix": "",
    "broadcastEnabled": true,
    "autoResumeEnabled": true
  }'
```

---

## 📝 Response Examples

### Economy - Get Player Coins

**Request:**
```http
GET /api/player/550e8400-e29b-41d4-a716-446655440000/coins
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "uuid": "550e8400-e29b-41d4-a716-446655440000",
    "coins": 1523.5,
    "bankBalance": 500.0,
    "total": 2023.5
  }
}
```

### Shop - Get All Items

**Request:**
```http
GET /api/shop
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": "1",
      "name": "Diamond Sword",
      "material": "DIAMOND_SWORD",
      "price": 100.0,
      "icon": null
    },
    {
      "id": "2",
      "name": "Iron Armor Set",
      "material": "IRON_CHESTPLATE",
      "price": 50.0,
      "icon": null
    }
  ]
}
```

### AFK - Get Config

**Request:**
```http
GET /api/afk
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "timeout": 10,
    "kickEnabled": false,
    "kickTimeout": 30,
    "prefix": "§7[AFK] ",
    "suffix": "",
    "broadcastEnabled": true,
    "autoResumeEnabled": true
  }
}
```

---

## 🔗 Related Documentation

- [INTEGRATION_GUIDE.md](INTEGRATION_GUIDE.md) - Полное руководство по интеграции
- [QUICK_TEST_GUIDE.md](QUICK_TEST_GUIDE.md) - Быстрый старт и тестирование
- [API_DEMO.html](API_DEMO.html) - Интерактивное демо

---

**Happy Coding! 🚀**
