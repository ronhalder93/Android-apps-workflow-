# Eden Android source build (experimental)

This branch adds a manually triggered GitHub Actions workflow for building the upstream Eden Android source.

- Workflow: `.github/workflows/build-eden-android.yml`
- Upstream source is cloned at build time from `https://git.eden-emu.dev/eden-emu/eden.git`.
- It uses the upstream `.ci/android/build.sh -b RelWithDebInfo` script and uploads APK/AAB outputs if generated.

## Run it
1. Open the repository's **Actions** tab.
2. Select **Build Eden Android (source)**.
3. Choose **Run workflow** on branch `eden-android-build`.
4. Open the run and inspect the build logs. Download the `eden-android-build-output` artifact only if the job produced files.

## Honest status
The workflow definition is committed, but no build run has been triggered or verified from this connector. A successful CI build would still need installation and testing on the target phone. No FPS, game compatibility, save stability, or Pokémon capture fix is claimed.
