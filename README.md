# TrainingArc App

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

A fitness tracking application for monitoring workout progress with clean UI and real-time synchronization.

## Features

- Create and manage training plans
- Track exercise progress with stats (sets, reps, weight)
- View progress history with charts
- Cloud synchronization across devices
- User authentication system

## Technologies

- **Frontend**: Jetpack Compose (Declarative UI with 70% less code than XML)
- **Backend**: Firebase Realtime Database
- **Authentication**: Firebase Auth
- **Architecture**: MVVM with Android Jetpack components

## Project Structure

features/
├── auth/ # Authentication module
├── homePage/ # Main workout screens
├── profilePage/ # User profile
└── settingsPage/ # App settings
navigation/ # App navigation
ui/ # Theme and design components


## Firebase Database Structure

```json
{
  "users": {
    "exercises": {
      "exerciseId": {
        "description": "String",
        "exerciseName": "String",
        "sets": "Number",
        "reps": "Number",
        "weight": "Number",
        "history": {
          "date": "Number"
        }
      }
    },
    "sessions": {
      "sessionId": {
        "sessionName": "String",
        "sessionExercises": {
          "exerciseId": "Number"
        }
      }
    }
  }
}
```
## Screenshots

<div align="center">
  <img src="https://i.imgur.com/placeholder1.png" alt="Exercise Screen" width="30%">
  <img src="https://i.imgur.com/placeholder2.png" alt="Progress Chart" width="30%">
  <img src="https://i.imgur.com/placeholder3.png" alt="Workout List" width="30%">
</div>

## Future Plans

- 👥 Social features (friends system)  
- 🌓 Light/dark theme switching  
- 📤 Results sharing functionality  
- 📊 Advanced analytics  

## Contributors  

| Contributor       | Role(s)                          |  
|-------------------|----------------------------------|  
| **Person1**       | UI/UX, Jetpack Compose, Backend, Firebase |  
| **Person2**       | Backend, Firebase                |  

## License

© 2025 TrainingArc App. All Rights Reserved.
