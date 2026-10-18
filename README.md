# M14-AC.1-Calculadora

A simple clone of the Windows calculator, built for Android with **Kotlin** and **Jetpack Compose**.

> [!NOTE]
> **AI disclaimer:** AI was used to turn the Windows calculator's logic into a decision table (see [Calculator decision table](./decisionTable.png)), to review the code and fix some of the bugs it found, and to help write this README.

## Features

- **Repeat the last operation** by pressing `=` again (`5 + 3 = =` shows `8`, then `11`)
- **Chain operations** without pressing `=` (`5 + 3 ×` shows `8`)
- **Change the operator** if you pressed the wrong one (`5 + ×` becomes `5 ×`)
- **Division by zero** shows `Error` instead of crashing

## How to run

### Option 1: Android Studio

Open the project in **Android Studio** and run it on an emulator or a physical device.

### Option 2: Watcher script

[`watcher.sh`](./watcher.sh) rebuilds the app and relaunches it on the connected emulator or device every time you save a file. Run it from **Git Bash**, with an emulator already running:

```bash
./watcher.sh
```

It needs these environment variables:

| Variable | Value |
| --- | --- |
| `JAVA_HOME` | Android Studio's JBR, e.g. `C:\Program Files\Android\Android Studio\jbr` |
| `ANDROID_HOME` | The Android SDK, e.g. `%LOCALAPPDATA%\Android\Sdk` |

And these folders added to `Path`:

- `%ANDROID_HOME%\platform-tools`
- `%ANDROID_HOME%\emulator`
- `%ANDROID_HOME%\cmdline-tools\latest\bin`

## How it works

The screen shows two values, and the calculator keeps a few more in memory to do the math:

| Variable | Purpose |
| --- | --- |
| `number` | The big line on screen: what you're typing, or the latest result |
| `operation` | The small line on top, e.g. `5+` or `5+3=`. Display only |
| `accumulator` | The first number, saved while you type the second one |
| `pendingOp` | The operator waiting for its second number |
| `resetNumber` | Whether the next digit starts a new number instead of adding to the current one |
| `lastOp` / `lastOperand` | The last operation and second number, reused when `=` is pressed again |

Example, `5 + 3 = =`:

| Key | Screen | Top line | `accumulator` | `pendingOp` | `lastOp` / `lastOperand` |
| --- | --- | --- | --- | --- | --- |
| `5` | 5 | | – | – | – |
| `+` | 5 | 5+ | 5 | + | – |
| `3` | 3 | 5+ | 5 | + | – |
| `=` | 8 | 5+3= | – | – | + / 3 |
| `=` | 11 | 8+3= | – | – | + / 3 |
