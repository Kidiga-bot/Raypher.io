🛡️ Raypher: The "Kill Switch" for Open-Device Theft
"The operating system is compromised. Trust only the Silicon."

Raypher is an Active Interception Protocol designed to solve the single biggest threat in the Global South's fintech ecosystem: "Open Device" Theft. Unlike passive antivirus tools that scan for malware, Raypher physically locks the UI of financial applications when critical transactions are detected, demanding a secondary, hardware-bound biometric override.

🚨 The Crisis: Why We Exist
The global security stack is built on a fatal flaw: Session Trust. Once a user unlocks their phone and logs into a banking app, the system assumes every subsequent action is authorized.

The Reality: In markets like Kenya, Nigeria, and Brazil, 90% of fraud is not remote hacking. It is Coercion & Snatch Theft.

The Scenario: A thief snatches an unlocked phone or coerces the PIN from the victim.

The Failure: Traditional banking apps (M-PESA, Equity, Fintechs) have no defense. If the phone is unlocked, the money is gone.

The Loss: Kenya alone lost KES 1.59 Billion (~$12.3M) to this vector in 2024.

Raypher is the "Last Mile" defense that assumes the device is already in the hands of a thief.

⚡ The Solution: Active Interception
Raypher is not a background scanner. It is a Gatekeeper.

We utilize a Broadcast-Interceptor Architecture that sits between the Banking Application and the Android OS.

Listen: Raypher monitors system broadcasts for specific INTENT_TRANSACTION_INIT signatures.

Intercept: The moment a "Send Money" packet is detected, Raypher interrupts the UI thread.

Block: We launch a System-Level Overlay ("The Red Shield") using FLAG_ACTIVITY_NEW_TASK, physically covering the banking app and disabling touch inputs.

Verify: The transaction remains frozen until the True Owner provides a biometric signature (Fingerprint/FaceID).

Result: A thief can hold your phone, know your PIN, and open your banking app—but they cannot move a single cent.

⚔️ Competitive Advantage: Why Raypher Wins
We are not competing with Antivirus. We are replacing it.

1. vs. Traditional Antivirus (e.g., Kaspersky, Avast)

Their Primary Defense: Malware Scanning.

Their Threat Model: Assumes the user is being tricked (Phishing) or the software is infected.

Their Failure: If a thief has the physical phone and the PIN, antivirus software sees a "legitimate user" and does nothing.

The Raypher Advantage: We assume the user is a thief. We physically block the transaction button even if the app is "safe," requiring a secondary biometric override that a thief cannot fake.

2. vs. Behavioral Biometrics (e.g., BioCatch)

Their Primary Defense: Analyzing typing speed, gyroscope movement, and swipe patterns.

Their Threat Model: Assumes the user is a bot or a remote attacker.

Their Failure: It is probabilistic (guessing). It often flags fraud too late—after the money has already left the wallet.

The Raypher Advantage: We are deterministic. We do not guess. We operate in real-time (Pre-Transaction). If the hardware signature is missing, the interface locks. Zero guessing.

3. vs. The Status Quo (Doing Nothing)

The Problem: Banks rely on "Session Trust." Once logged in, the app trusts every action.

The Raypher Advantage: We introduce "Zero Trust" to the UI. Just because the phone is unlocked does not mean the bank account is open. We re-verify identity at the exact moment of value transfer.

🛠️ Under the Hood: Technical Architecture
Phase 1: The MVP (Current Implementation)
Core: Native Android (Kotlin).

Detection: BroadcastReceiver listening for defined com.raypher.SECURITY_ALERT intents from partner apps.

Interception: WindowManager service launching a high-priority BlockActivity.

Security: AccessibilityService safeguards to prevent unauthorized uninstallation.

Phase 2: The Vision (Silicon-Native Migration)
We are currently migrating the core locking logic from the Android Application Layer to the Hardware Layer.

Goal: Move the "Red Shield" logic into the ARM TrustZone (TEE).

Why: Software overlays can be bypassed by Rooting. Hardware signatures cannot.

Tech: C++/Rust via JNI interfacing with the Android Keystore System.

🚀 Quick Start (MVP Demo)
Note: This is a proof-of-concept build. Do not use on production devices without backing up data.

Clone & Build:

Bash
git clone https://github.com/yourusername/raypher-android.git
cd raypher-android
./gradlew installDebug
Grant Permissions:

Raypher requires "Display Over Other Apps" to launch the Shield.

Raypher requires "Accessibility Services" to monitor app states.

Arm the Device:

Open Raypher Dashboard.

Toggle "Active Defense" to ON.

Select target apps (e.g., "M-Pesa Simulator").

Test the Attack:

Exit Raypher.

Open the target banking app and attempt a transfer.

Observation: The "Red Shield" instantly blocks the screen.

🗺️ Roadmap
[x] v0.1: MVP Release (Overlay & Broadcast Receiver).

[ ] v0.5: "Keyring" Integration (Multi-device remote kill switch).

[ ] v1.0: Silicon Migration (JNI/C++ implementation for TEE binding).

[ ] v2.0: SDK Release for Fintech Partners (Direct integration).

📄 License
MIT License © 2026 Raypher Labs.

Built with ❤️ in Nairobi. Raypher is dedicated to the millions of families who rely on mobile money for their survival.
