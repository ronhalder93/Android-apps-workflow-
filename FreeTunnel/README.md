# FreeTunnel Android project

This folder is reserved for the FreeTunnel Gradle project. The GitHub Actions workflow at `.github/workflows/freetunnel-apk.yml` builds this folder and uploads the debug APK as the `FreeTunnel-apk` artifact.

## Required project files

Copy the corrected Android project files here so that these paths exist:

- `FreeTunnel/settings.gradle.kts`
- `FreeTunnel/build.gradle.kts`
- `FreeTunnel/app/build.gradle.kts`
- `FreeTunnel/app/src/main/AndroidManifest.xml`
- `FreeTunnel/app/src/main/java/com/venice/freetunnel/`
- `FreeTunnel/app/src/main/res/`

The workflow downloads the ARM64 teapod tun2socks AAR into `FreeTunnel/app/libs/` before building.

## Run from Android phone

1. Open this repository on GitHub.
2. Select **Actions** → **Build FreeTunnel APK**.
3. Tap **Run workflow**.
4. Open the completed run and download **FreeTunnel-apk** under Artifacts.

The APK is not available until the full project source is present and the build succeeds. A successful build does not by itself prove that SSH/VPN traffic works; test only with an SSH server you are authorized to use.
