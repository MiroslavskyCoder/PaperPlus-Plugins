// Minimal diagnostic script for LoaderScript
const globalKeys = Object.getOwnPropertyNames(globalThis || this);
console.log('[DIAG] typeof requireNativeModule:', typeof requireNativeModule);
console.log('[DIAG] global keys:', globalKeys.join(', '));
if (typeof requireNativeModule !== 'function') {
    throw new Error('requireNativeModule is not a function');
} else {
    const fs = requireNativeModule('filesystem');
    if (!fs) throw new Error('filesystem module not loaded');
    console.log('requireNativeModule and filesystem module are available!');
}
