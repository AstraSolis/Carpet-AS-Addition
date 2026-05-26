# Carpet AS Addition

English | [中文](README.md)

An add-on for the [Carpet mod](https://modrinth.com/mod/carpet) that provides extra configurable rules. **Does not change vanilla behavior** with default settings.

## Overview

This mod targets server operators and single-player users who use Carpet fake players and related features. It adds two capabilities:

- **Fake player UI highlighting**: Sets a distinct background color for fake player name rows in nametags above the head, the Tab player list, and command completion lists, making them easy to distinguish from real players (requires this mod on the client as well).
- **Fake player sleep ignore**: When determining whether the night can be skipped, the server ignores fake players and only counts real players (server-side rule only; no client mod required).

Rules are registered through Carpet’s standard mechanism. Settings are stored in the world’s `carpet.conf`, shared with the base Carpet mod.

## Supported Versions

| Minecraft Version | Carpet Version | Java |
|-------------------|----------------|------|
| 1.20.1 | 1.4.112 | 17+ |
| 1.21.11 | 1.4.194 | 21+ |
| 26.1 | 26.1+v260401 | 25+ |

## Dependencies

| Mod | Notes |
|-----|-------|
| [Fabric Loader](https://fabricmc.net/) | 0.19.2 or newer |
| [Fabric API](https://modrinth.com/mod/fabric-api) | Version matching your MC version |
| [Carpet](https://modrinth.com/mod/carpet) | Version matching your MC version |

## Installation

1. Install the matching [Fabric Loader](https://fabricmc.net/) (0.19.2+ required)
2. Place [Fabric API](https://modrinth.com/mod/fabric-api), [Carpet](https://modrinth.com/mod/carpet), and this mod’s JAR in the `mods` folder
3. Launch the game

> JAR filenames follow the pattern `carpet-as-addition-<mod version>+<MC version>.jar`. Pick the build that matches your target game version.

## Usage

1. After entering a world, run `/carpet`
2. Select **[AS Addition Pack]** in the category list
3. Click a rule name to toggle or change its value; confirm when prompted to save permanently

Rule settings are stored in the world’s **`carpet.conf`**, shared with the base Carpet mod.

## Rules

### Fake player nametag colors

These three rules independently control fake player highlighting in each UI location. All are off by default. **The client must also have this mod installed to see color changes.**

Available colors: `green`, `red`, `blue`, `yellow`, `orange`, `purple`, `white`, `aqua`; set to `false` to disable.

| Rule | Default | Description |
|------|---------|-------------|
| `fakePlayerNametagHead` | `false` | Background color for fake player nametags above the head |
| `fakePlayerNametagTab` | `false` | Background color for fake player rows in the Tab player list |
| `fakePlayerNametagCommand` | `false` | Background color for fake player rows in command completion |

### Fake player sleep ignore

| Rule | Default | Description |
|------|---------|-------------|
| `fakePlayerSleepIgnore` | off | When skipping the night, ignore all fake players and count only real players; server-side only, no client mod required |

## Feedback

Please report bugs or feature requests on GitHub [Issues](https://github.com/AstraSolis/Carpet-AS-Addition/issues). Include your game version, Carpet version, and steps to reproduce.

## License

This project is licensed under the [MIT](LICENSE) License.
