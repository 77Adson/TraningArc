# TrainingArc App

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

### Featured Screen: Exercise Tracking
<div align="center">
  <img src="https://i.imgur.com/vFUa4GB.png" alt="Exercise Screen" width="50%" style="display: block; margin: 0 auto;">
</div>


*Detailed exercise view with stats tracking*

---

### Key Features  
| Progress Analytics | Workout Management | Home Dashboard |
|--------------------|--------------------|----------------|
| ![Progress](https://i.imgur.com/RDP0eg7.png) | ![Workouts](https://i.imgur.com/YMJxq25.png) | ![Home](https://i.imgur.com/K2AvSUv.png) |



## Future Plans

- 👥 Social features (friends system)  
- 🌓 Light/dark theme switching  
- 📤 Results sharing functionality  
- 📊 Advanced analytics  

## Contributors  

| Contributor       | Role(s)                          |  
|-------------------|----------------------------------|  
| **Adrian** (GitHub: [@77Adson](https://github.com/77Adson)) | UI/UX, Jetpack Compose, Backend, Firebase |  
| **Dawid** (GitHub: [@Skruuki](https://github.com/Skruuki))       | Backend, Firebase                |  

## License

© 2025 TrainingArc App. All Rights Reserved.
