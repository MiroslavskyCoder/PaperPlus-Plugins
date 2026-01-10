package com.webx.dashboard.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webx.dashboard.WebDashboardPlugin;
import com.webx.dashboard.endpoints.AfkEndpoint;
import com.webx.dashboard.endpoints.PlayerEndpoint;
import com.webx.dashboard.endpoints.ShopEndpoint;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class WebApiServer {
    private final WebDashboardPlugin plugin;
    private final Gson gson;
    private Javalin app;
    private final String host;
    private final int port;
    
    public WebApiServer(WebDashboardPlugin plugin, String host, int port) {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
    
    public void start() {
        app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            
            // Enable CORS for web dashboard
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.anyHost();
                });
            });
        });
        
        // Register endpoints
        PlayerEndpoint playerEndpoint = new PlayerEndpoint(plugin, gson);
        ShopEndpoint shopEndpoint = new ShopEndpoint(plugin, gson);
        AfkEndpoint afkEndpoint = new AfkEndpoint(plugin, gson);
        
        // Health check
        app.get("/api/health", ctx -> {
            ctx.json(new Response(true, "Web Dashboard API is running"));
        });
        
        // Player endpoints
        app.get("/api/player/{uuid}/coins", playerEndpoint::getPlayerCoins);
        app.get("/api/players/top", playerEndpoint::getTopPlayers);
        
        // Shop endpoints
        app.get("/api/shop", shopEndpoint::getShopItems);
        app.post("/api/shop", shopEndpoint::updateShopItems);
        app.get("/api/shop/{id}", shopEndpoint::getShopItem);
        app.put("/api/shop/{id}", shopEndpoint::updateShopItem);
        app.delete("/api/shop/{id}", shopEndpoint::deleteShopItem);
        
        // AFK endpoints
        app.get("/api/afk", afkEndpoint::getAfkConfig);
        app.put("/api/afk", afkEndpoint::updateAfkConfig);
        
        // Static files for dashboard UI
        app.get("/dashboard", ctx -> {
            ctx.html(getDashboardHtml());
        });
        
        // Error handlers
        app.exception(Exception.class, (e, ctx) -> {
            plugin.getLogger().warning("API error: " + e.getMessage());
            ctx.status(500).json(new Response(false, "Internal server error: " + e.getMessage()));
        });
        
        app.error(404, ctx -> {
            ctx.json(new Response(false, "Endpoint not found"));
        });
        
        app.start(host, port);
    }
    
    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
    
    private String getDashboardHtml() {
        return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>WebX Dashboard</title>
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    color: #333;
                    min-height: 100vh;
                    padding: 20px;
                }
                .container {
                    max-width: 1200px;
                    margin: 0 auto;
                }
                .header {
                    background: white;
                    padding: 30px;
                    border-radius: 10px;
                    box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                    margin-bottom: 30px;
                    text-align: center;
                }
                .header h1 {
                    color: #667eea;
                    font-size: 2.5em;
                    margin-bottom: 10px;
                }
                .tabs {
                    display: flex;
                    gap: 10px;
                    margin-bottom: 20px;
                    background: white;
                    padding: 10px;
                    border-radius: 10px;
                    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                }
                .tab {
                    flex: 1;
                    padding: 15px;
                    background: #f5f5f5;
                    border: none;
                    border-radius: 5px;
                    cursor: pointer;
                    font-size: 16px;
                    transition: all 0.3s;
                }
                .tab:hover {
                    background: #e0e0e0;
                }
                .tab.active {
                    background: #667eea;
                    color: white;
                }
                .panel {
                    display: none;
                    background: white;
                    padding: 30px;
                    border-radius: 10px;
                    box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                }
                .panel.active {
                    display: block;
                }
                .form-group {
                    margin-bottom: 20px;
                }
                .form-group label {
                    display: block;
                    margin-bottom: 8px;
                    font-weight: 600;
                    color: #555;
                }
                .form-group input, .form-group textarea, .form-group select {
                    width: 100%;
                    padding: 12px;
                    border: 2px solid #ddd;
                    border-radius: 5px;
                    font-size: 14px;
                    transition: border-color 0.3s;
                }
                .form-group input:focus, .form-group textarea:focus {
                    outline: none;
                    border-color: #667eea;
                }
                .btn {
                    background: #667eea;
                    color: white;
                    padding: 12px 30px;
                    border: none;
                    border-radius: 5px;
                    font-size: 16px;
                    cursor: pointer;
                    transition: all 0.3s;
                }
                .btn:hover {
                    background: #5568d3;
                    transform: translateY(-2px);
                    box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                }
                .btn-danger {
                    background: #e74c3c;
                }
                .btn-danger:hover {
                    background: #c0392b;
                }
                .item-list {
                    display: grid;
                    gap: 15px;
                    margin-bottom: 20px;
                }
                .item-card {
                    background: #f9f9f9;
                    padding: 20px;
                    border-radius: 8px;
                    border-left: 4px solid #667eea;
                }
                .item-card h3 {
                    margin-bottom: 10px;
                    color: #667eea;
                }
                .item-card p {
                    margin: 5px 0;
                    color: #666;
                }
                .message {
                    padding: 15px;
                    border-radius: 5px;
                    margin-bottom: 20px;
                    display: none;
                }
                .message.success {
                    background: #d4edda;
                    color: #155724;
                    border: 1px solid #c3e6cb;
                }
                .message.error {
                    background: #f8d7da;
                    color: #721c24;
                    border: 1px solid #f5c6cb;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>⚙️ WebX Dashboard</h1>
                    <p>Manage your Minecraft server configuration</p>
                </div>
                
                <div class="tabs">
                    <button class="tab active" onclick="showTab('players')">👥 Players</button>
                    <button class="tab" onclick="showTab('shop')">🛒 Shop</button>
                    <button class="tab" onclick="showTab('afk')">💤 AFK System</button>
                </div>
                
                <div id="players" class="panel active">
                    <h2>Player Coins</h2>
                    <div class="form-group">
                        <label>Player UUID</label>
                        <input type="text" id="playerUuid" placeholder="Enter player UUID">
                    </div>
                    <button class="btn" onclick="searchPlayer()">Search</button>
                    <div id="playerResult" style="margin-top: 20px;"></div>
                </div>
                
                <div id="shop" class="panel">
                    <h2>Shop Configuration</h2>
                    <div id="shopMessage" class="message"></div>
                    <div id="shopItems" class="item-list"></div>
                    <h3>Add New Item</h3>
                    <form onsubmit="addShopItem(event)">
                        <div class="form-group">
                            <label>Item Name</label>
                            <input type="text" id="itemName" required>
                        </div>
                        <div class="form-group">
                            <label>Material</label>
                            <input type="text" id="itemMaterial" placeholder="e.g., DIAMOND_SWORD" required>
                        </div>
                        <div class="form-group">
                            <label>Price</label>
                            <input type="number" id="itemPrice" step="0.01" required>
                        </div>
                        <div class="form-group">
                            <label>Icon URL</label>
                            <input type="url" id="itemIcon" placeholder="https://...">
                        </div>
                        <button type="submit" class="btn">Add Item</button>
                    </form>
                </div>
                
                <div id="afk" class="panel">
                    <h2>AFK System Configuration</h2>
                    <div id="afkMessage" class="message"></div>
                    <form onsubmit="updateAfkConfig(event)">
                        <div class="form-group">
                            <label>AFK Timeout (minutes)</label>
                            <input type="number" id="afkTimeout" min="1" required>
                        </div>
                        <div class="form-group">
                            <label>Kick on AFK</label>
                            <select id="afkKick">
                                <option value="true">Yes</option>
                                <option value="false">No</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>AFK Prefix</label>
                            <input type="text" id="afkPrefix" placeholder="[AFK]">
                        </div>
                        <button type="submit" class="btn">Save Configuration</button>
                    </form>
                </div>
            </div>
            
            <script>
                const API_URL = window.location.origin + '/api';
                
                function showTab(tab) {
                    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
                    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
                    event.target.classList.add('active');
                    document.getElementById(tab).classList.add('active');
                    
                    if (tab === 'shop') loadShopItems();
                    if (tab === 'afk') loadAfkConfig();
                }
                
                async function searchPlayer() {
                    const uuid = document.getElementById('playerUuid').value;
                    const result = document.getElementById('playerResult');
                    
                    try {
                        const response = await fetch(`${API_URL}/player/${uuid}/coins`);
                        const data = await response.json();
                        
                        if (data.success) {
                            result.innerHTML = `
                                <div class="item-card">
                                    <h3>Player Information</h3>
                                    <p><strong>UUID:</strong> ${data.data.uuid}</p>
                                    <p><strong>Coins:</strong> ${data.data.coins}</p>
                                    <p><strong>Bank Balance:</strong> ${data.data.bankBalance || 0}</p>
                                    <p><strong>Total:</strong> ${data.data.total}</p>
                                </div>
                            `;
                        } else {
                            result.innerHTML = `<div class="message error" style="display:block;">${data.message}</div>`;
                        }
                    } catch (error) {
                        result.innerHTML = `<div class="message error" style="display:block;">Error: ${error.message}</div>`;
                    }
                }
                
                async function loadShopItems() {
                    try {
                        const response = await fetch(`${API_URL}/shop`);
                        const data = await response.json();
                        
                        if (data.success && data.data) {
                            const container = document.getElementById('shopItems');
                            container.innerHTML = data.data.map(item => `
                                <div class="item-card">
                                    <h3>${item.name}</h3>
                                    <p><strong>Material:</strong> ${item.material}</p>
                                    <p><strong>Price:</strong> ${item.price} coins</p>
                                    ${item.icon ? `<p><strong>Icon:</strong> <a href="${item.icon}" target="_blank">View</a></p>` : ''}
                                    <button class="btn btn-danger" onclick="deleteShopItem('${item.id}')">Delete</button>
                                </div>
                            `).join('');
                        }
                    } catch (error) {
                        showMessage('shopMessage', 'Error loading shop items: ' + error.message, false);
                    }
                }
                
                async function addShopItem(event) {
                    event.preventDefault();
                    
                    const item = {
                        id: Date.now().toString(),
                        name: document.getElementById('itemName').value,
                        material: document.getElementById('itemMaterial').value,
                        price: parseFloat(document.getElementById('itemPrice').value),
                        icon: document.getElementById('itemIcon').value
                    };
                    
                    try {
                        const response = await fetch(`${API_URL}/shop`, {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(item)
                        });
                        
                        const data = await response.json();
                        
                        if (data.success) {
                            showMessage('shopMessage', 'Item added successfully!', true);
                            event.target.reset();
                            loadShopItems();
                        } else {
                            showMessage('shopMessage', 'Error: ' + data.message, false);
                        }
                    } catch (error) {
                        showMessage('shopMessage', 'Error: ' + error.message, false);
                    }
                }
                
                async function deleteShopItem(id) {
                    if (!confirm('Delete this item?')) return;
                    
                    try {
                        const response = await fetch(`${API_URL}/shop/${id}`, { method: 'DELETE' });
                        const data = await response.json();
                        
                        if (data.success) {
                            showMessage('shopMessage', 'Item deleted!', true);
                            loadShopItems();
                        }
                    } catch (error) {
                        showMessage('shopMessage', 'Error: ' + error.message, false);
                    }
                }
                
                async function loadAfkConfig() {
                    try {
                        const response = await fetch(`${API_URL}/afk`);
                        const data = await response.json();
                        
                        if (data.success && data.data) {
                            document.getElementById('afkTimeout').value = data.data.timeout || 10;
                            document.getElementById('afkKick').value = data.data.kickEnabled || false;
                            document.getElementById('afkPrefix').value = data.data.prefix || '[AFK]';
                        }
                    } catch (error) {
                        showMessage('afkMessage', 'Error loading AFK config: ' + error.message, false);
                    }
                }
                
                async function updateAfkConfig(event) {
                    event.preventDefault();
                    
                    const config = {
                        timeout: parseInt(document.getElementById('afkTimeout').value),
                        kickEnabled: document.getElementById('afkKick').value === 'true',
                        prefix: document.getElementById('afkPrefix').value
                    };
                    
                    try {
                        const response = await fetch(`${API_URL}/afk`, {
                            method: 'PUT',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(config)
                        });
                        
                        const data = await response.json();
                        
                        if (data.success) {
                            showMessage('afkMessage', 'AFK configuration saved!', true);
                        } else {
                            showMessage('afkMessage', 'Error: ' + data.message, false);
                        }
                    } catch (error) {
                        showMessage('afkMessage', 'Error: ' + error.message, false);
                    }
                }
                
                function showMessage(elementId, message, isSuccess) {
                    const element = document.getElementById(elementId);
                    element.textContent = message;
                    element.className = 'message ' + (isSuccess ? 'success' : 'error');
                    element.style.display = 'block';
                    setTimeout(() => element.style.display = 'none', 5000);
                }
            </script>
        </body>
        </html>
        """;
    }
    
    public static class Response {
        public boolean success;
        public String message;
        public Object data;
        
        public Response(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public Response(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
    }
}
