// Example 6: Date and Time Operations
function getCurrentTimestamp() {
    return Date.now();
}

function formatDateTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

function calculateDaysSince(timestamp) {
    const now = Date.now();
    const dayMs = 24 * 60 * 60 * 1000;
    return Math.floor((now - timestamp) / dayMs);
}

function calculateTimeUntilNextReset(resetHour = 0) {
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(resetHour, 0, 0, 0);
    
    const timeUntil = tomorrow - now;
    return {
        ms: timeUntil,
        seconds: Math.floor(timeUntil / 1000),
        minutes: Math.floor(timeUntil / 60000),
        hours: Math.floor(timeUntil / 3600000),
        formatted: `${Math.floor(timeUntil / 3600000)}h ${Math.floor((timeUntil % 3600000) / 60000)}m`
    };
}

function isWithinDaily(lastCompleteTime, questId) {
    if (!lastCompleteTime) return true;
    
    const now = Date.now();
    const dayMs = 24 * 60 * 60 * 1000;
    const timeSince = now - lastCompleteTime;
    
    return timeSince >= dayMs;
}
