Sign Language Translation AppA real-time sign language translation application that converts ASL (American Sign Language) and ISL (Indian Sign Language) gestures into text and speech using computer vision.
🎯 Features

Dual Language Support - American Sign Language (ASL) & Indian Sign Language (ISL)
Two Operating Modes

Manual Mode: Click gesture buttons for instant translation
Camera Mode: Real-time gesture recognition using OpenCV


Text-to-Speech - Automatic voice output for translations
Translation History - Track all translations with timestamps and confidence scores
Gesture Recognition - Detects 6 gestures: Hello, Yes, No, Thank You, Help, I Love You

🛠️ Technology Stack
Backend: Java 11, Spring Boot 2.7, OpenCV 4.8
Frontend: HTML5, CSS3, JavaScript (Vanilla)
Database: MySQL 8.0
Computer Vision: OpenCV Java bindings for hand detection and gesture recognition
📋 Prerequisites

Java JDK 11+
Maven 3.6+
MySQL 8.0
OpenCV 4.8.0
Modern web browser (Chrome/Edge recommended)

🚀 Installation
1. Clone Repository
bashgit clone https://github.com/yourusername/sign-language-app.git
cd sign-language-app
2. Setup Database
bashmysql -u root -p
sqlCREATE DATABASE sign_language_app;
USE sign_language_app;
-- Run schema.sql file
3. Install OpenCV

Download OpenCV 4.8.0 from https://opencv.org/releases/
Extract to C:\opencv
Copy opencv-480.jar to backend/lib/
Copy opencv_java480.dll to backend/lib/
Update DLL path in GestureRecognitionService.java

4. Configure Backend
Edit backend/src/main/resources/application.properties:
propertiesspring.datasource.url=jdbc:mysql://localhost:3306/sign_language_app
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
5. Build & Run
bashcd backend
mvn clean install
mvn spring-boot:run
Backend runs on: http://localhost:8080
6. Launch Frontend
Open frontend/index.html in your browser or use:
bashcd frontend
python -m http.server 3000
Frontend runs on: http://localhost:3000
📖 Usage
Manual Mode

Select language (ASL/ISL)
Click a gesture card
Click "Translate"
Click "Speak" to hear audio

Camera Mode (OpenCV)

Click "Camera Mode" button
Allow camera access
Show hand gesture to camera
Hold steady for 2 seconds
Translation appears automatically

🤝 Contributing

Fork the repository
Create feature branch (git checkout -b feature/AmazingFeature)
Commit changes (git commit -m 'Add AmazingFeature')
Push to branch (git push origin feature/AmazingFeature)
Open Pull Request

📝 Future Enhancements

 Expand gesture library (alphabet, numbers)
 Train ML model for improved accuracy
 Add user authentication
 Mobile app version
 Support for more sign languages (BSL, JSL)
 Video tutorials for each gesture
 Real-time sentence construction

👨‍💻 Developer
Nandini Srivastava
GitHub: Nandini2277
Arpitha Ganeshan
GitHub: AG
Manotharini B
GitHub: Manotharini

🙏 Acknowledgments

OpenCV library for computer vision capabilities
Spring Boot framework
MediaPipe for inspiration on hand tracking
Sign language community for gesture references

