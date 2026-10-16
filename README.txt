LABASAN PS WATER ELEVATION - ANDROID PROJECT

This project includes a GitHub Actions workflow that builds a debug APK online.

BUILD STEPS
1. Open the repository's Actions tab. If GitHub asks, enable Actions.
2. Choose "Build Labasan PS APK" from the workflows list and tap Run workflow (or let the push-triggered run finish).
3. Open the completed workflow run, scroll to Artifacts, and download LabasanPS-debug-apk.
4. Extract the downloaded artifact ZIP; it contains app-debug.apk.
5. Tap app-debug.apk to install. If Android prompts, allow installs from the browser/file manager you used, then retry installation.

IMPORTANT
- This creates a debug APK for personal testing. It is not a Play Store release build.
- GitHub Actions availability/usage may depend on repository visibility and account limits.
- Do not share passwords or GitHub tokens. Never add secrets to source files.
- The app stores report data locally on that device/browser/app storage; it does not sync to a server.
