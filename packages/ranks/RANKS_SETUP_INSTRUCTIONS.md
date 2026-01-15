// ⚠️ IMPORTANT: Add this code to register the Ranks API endpoints

// File: packages/webx-dashboard/src/main/java/com/webx/api/API.java
// Add after other endpoint registrations

// ============ STEP 1: Add imports at the top ============
import com.webx.api.endpoints.RanksEndpoints;
import com.webx.api.services.RankService;

// ============ STEP 2: Register in your API initialization method ============
// Add this code in your API initialization method (usually in a setup method or constructor):

public void setupRanksAPI(Javalin app) {
    // Initialize the Rank Service
    RankService rankService = new RankService();
    
    // Register the Ranks endpoints
    new RanksEndpoints(rankService).register(app);
    
    // Log successful registration
    logger.info("✅ Ranks API endpoints registered successfully");
}

// Call this method in your main API initialization:
// setupRanksAPI(app);

// ============ STEP 3: Add Ranks tabs to Dashboard ============
// File: packages/webx-dashboard-panel/src/app/dashboard/page.tsx

// Add imports
import { RanksTab } from "@/components/dashboard/ranks-tab";
import { PlayerRanksTab } from "@/components/dashboard/player-ranks-tab";

// Add these tabs to your TabsList component:
<TabsTrigger value="ranks">🏆 Ranks</TabsTrigger>
<TabsTrigger value="player-ranks">👥 Player Ranks</TabsTrigger>

// Add these TabsContent components:
<TabsContent value="ranks">
  <RanksTab />
</TabsContent>

<TabsContent value="player-ranks">
  <PlayerRanksTab />
</TabsContent>

// ============ STEP 4: Build the project ============
// Run this command from the root directory:
gradle clean buildAllPlugins

// ============ STEP 5: Deploy to server ============
// Copy the JAR files to your server:
cp build/libs/ranks-1.0.0.jar /path/to/server/plugins/
cp build/libs/webx-dashboard-1.0.0.jar /path/to/server/plugins/

// ============ STEP 6: Verify Installation ============
// Start the server and look for these messages in console:
// ✅ Ranks & Permissions System enabled!
// ✅ Ranks API endpoints registered successfully!

// If you see errors, check:
// 1. Is the Ranks plugin installed in plugins/ folder?
// 2. Is the webx-dashboard API properly initialized?
// 3. Are imports correct in your API file?

// ============ STEP 7: Test the API ============
// Open terminal and run:
curl http://localhost:9092/api/v1/ranks

// Should return JSON with all ranks

// ============ STEP 8: Access Web Dashboard ============
// Open browser and go to:
// http://localhost:9092/dashboard
// 
// You should now see "Ranks" and "Player Ranks" tabs
// in the main navigation drawer
