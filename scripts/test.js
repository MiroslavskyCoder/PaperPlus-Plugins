// ✓ Test Script - Pure JavaScript (no TypeScript syntax)
// This script demonstrates basic LoaderScript functionality

console.log("[Test] Script loaded!");

// Example 1: Simple object (no TypeScript syntax)
const player = {
    name: "Alex",
    hp: 20,
    getInfo: function() {
        return this.name + " (HP: " + this.hp + ")";
    }
};

console.log("[Test] Player info: " + player.getInfo());

// Example 2: Event listener
if (typeof LXXVServer !== "undefined") {
    console.log("[Test] LXXVServer available - registering event listener");
    
    LXXVServer.on("playerJoin", function(playerObj) {
        const name = playerObj.getName();
        console.log("[Test] Player joined: " + name);
    });
} else {
    console.log("[Test] LXXVServer not available (dashboard testing mode)");
}

// Example 3: Simple number operation
const randomNum = Math.floor(Math.random() * 100);
console.log("[Test] Random number: " + randomNum);

console.log("[Test] Script execution complete!");
