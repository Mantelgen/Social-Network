# Social Network GUI

Welcome to the **Social Network GUI** project! 🌐💬

This is a simple Java desktop application that simulates a social network, allowing users to connect, chat, and manage friendships in a modern graphical interface.

## Features

- 🧑‍🤝‍🧑 **User Management**: Sign up, log in, and view user profiles.
- 🤝 **Friendship System**: Send, accept, or decline friend requests.
- 💬 **Chat**: Real-time messaging between friends.
- 🔔 **Notifications**: Stay updated with alerts and pending requests.
- 🎨 **Modern UI**: Stylish design with custom CSS and images.

## Technologies Used

- **Java 17+**
- **JavaFX** for GUI
- **Gradle** for build automation

## Project Structure

```
src/main/java/com/example/socialnetworkgui/
├── Controller/         # JavaFX controllers
├── Domain/             # Domain models (User, Friendship, Message, etc.)
├── Repository/         # Data access layer
├── Service/            # Business logic
├── Utils/              # Utility classes
├── HelloApplication.java
├── Main.java
└── ...
resources/main/com/example/socialnetworkgui/
├── css/                # Stylesheets
├── images/             # UI images
└── views/              # FXML layouts
```

## Getting Started

1. **Clone the repository**
   ```powershell
   git clone https://github.com/Mantelgen/Social-Network.git
   ```
2. **Build and run the project**
   ```powershell
   cd Social-Network
   ./gradlew run
   ```
   > On Windows, use `gradlew.bat run` instead.



