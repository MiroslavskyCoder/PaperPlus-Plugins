// Minimal diagnostic script for LoaderScript
if (typeof requireNativeModule !== 'function') {
    throw new Error('requireNativeModule is not a function');
} else {
    const fs = requireNativeModule('filesystem');
    if (!fs) throw new Error('filesystem module not loaded');
    console.log('requireNativeModule and filesystem module are available!');
}
