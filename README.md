# FastPlace Sync
## About
FastPlace Sync drastically speeds up your YouTube video editing workflow by turning gameplay block montages into instant, fully customizable editing software timelines.

Instead of spending hours manually cutting every single block placement, break, or action in post-production, FastPlace Sync tracks your actions in-game and syncs them automatically with your video recording.

## Key Features
- Automated Timestamping: Tracks every block place, break, and key action during your gameplay session and exports a lightweight clicks.txt file.

- Deep Customization: Highly configurable via the Mod Menu interface. Toggle default tracking sounds or add custom entries from a full list of every sound in Minecraft (e.g., Wind Charge sounds).

- One-Click Folder Access: Access your recorded timestamp files directly from the in-game configuration menu.

- Non-Destructive XML Timeline Generation: Uses a companion app to output timeline files rather than rendered videos. Edit individual cuts, adjust clip delays, and apply effects directly inside your editor as if you cut it by hand. **[Companion app feature]**

- Rapid Processing: Generates timeline data instantly without requiring long render times. **[Companion app feature]**

- Multi-NLE Support: Supports Final Cut Pro, DaVinci Resolve, Premiere Pro, and any editor compatible with the OpenTimelineIO standard. **[Companion app feature]**

## How It Works
Record & Calibrate: Start recording in your screen recording software (e.g., OBS). Press the mod keybind (F12 by default) within the first 10 seconds to trigger a calibration flash in the top-left corner of your screen, then play as normal.

## Typical Workflow
1. Open the mod's configuration menu to instantly grab your clicks.txt timestamp file.

2. Open the Python companion app, select your video file and clicks.txt, and run Scan and Sync Preview to auto-align the visual calibration flash with your video's audio waveform.

3. Click Generate to create your timeline file, import it directly into your editing software, and fine-tune your montage.

## Requirements & Links
- Mod Dependencies: Requires Mod Menu for in-game configuration access.

- Companion App: The timeline generator app requires Python installed on your system.

Download Companion App & Source: Grab the generator app, installation instructions, or report issues on the GitHub Repository.
