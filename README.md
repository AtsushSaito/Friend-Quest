# FriendQuest

An Android app that helps you remember details about your friends and generates personalized conversation starters using AI.

## Features

- **Friend Management** — Add, edit, and view friends with details like job, hobbies, favorite foods, and dietary restrictions
- **AI Conversation Starters** — Generate personalized questions for each friend powered by the Gemini API
- **Local Storage** — All friend data is stored locally using Room Database

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material3
- **Architecture:** Clean Architecture (domain / data / UI layers)
- **Database:** Room
- **Navigation:** Navigation Compose
- **ViewModel:** Lifecycle ViewModel Compose
- **AI:** Google Gemini API (`com.google.ai.client.generativeai`)

## Setup

1. Clone the repository
2. Create a `local.properties` file in the project root and add your Gemini API key:
   ```
   GEMINI_API_KEY=your_api_key_here
   GEMINI_MODEL_NAME=gemini-flash-latest
   ```
3. Open the project in Android Studio and run on a device or emulator (minSdk 24)

## Project Structure

```
app/src/main/java/com/example/friendquest/
├── domain/          # Entities and repository interfaces
├── data/
│   ├── local/       # Room database, DAO, entities
│   └── remote/      # Gemini API repository
├── viewmodel/       # FriendViewModel
└── ui/              # Compose screens (List, Detail, Add/Edit)
```
