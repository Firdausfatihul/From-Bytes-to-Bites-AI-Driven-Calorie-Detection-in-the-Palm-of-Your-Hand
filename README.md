# From Bytes to Bites: AI-Driven Calorie Detection in the Palm of Your Hand

## Overview

From Bytes to Bites is an innovative Android application that brings the power of artificial intelligence to calorie tracking. By leveraging advanced computer vision and machine learning algorithms through a dedicated backend API, this app allows users to instantly detect and estimate the calorie content of their meals using just their smartphone camera.

## Key Features

- **AI-Powered Calorie Detection**: Utilizes a YOLO model via API to analyze food images and estimate calorie content
- **Real-Time Analysis**: Get instant calorie estimations by sending captured images to our processing server
- **User-Friendly Interface**: Intuitive design built with Jetpack Compose for a smooth user experience
- **Personalized Tracking**: Secure user accounts to store and analyze your calorie intake over time
- **Cloud Processing**: Leverages a powerful backend for accurate and fast image analysis

## Technical Stack

### Android App
- **Frontend**: Kotlin with Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Storage**: Room Database
- **Asynchronous Operations**: Kotlin Coroutines and Flow
- **Dependency Injection**: Manual DI (with plans to implement Hilt)
- **Networking**: OkHttp3 for API communication
- **Image Capture**: CameraX

### Backend API
- **Framework**: Flask
- **AI Model**: YOLO for object detection
- **Image Processing**: OpenCV
- **Deployment**: Local with Ngrok for public access

## Getting Started

### Android App
1. Clone the repository:
   ```
   git clone https://github.com/Firdausfatihul/From-Bytes-to-Bites-AI-Driven-Calorie-Detection-in-the-Palm-of-Your-Hand.git
   ```
2. Open the project in Android Studio
3. Update the API base URL in the app to point to your backend
4. Run the app on an emulator or physical device

### Backend API
1. Clone the API repository:
   ```
   git clone https://github.com/Firdausfatihul/bytestobyte_api.git
   ```
2. Set up a Python virtual environment and install dependencies:
   ```
   python -m venv venv
   source venv/bin/activate  # On Windows, use `venv\Scripts\activate`
   pip install -r requirements.txt
   ```
3. Run the Flask application:
   ```
   python app.py
   ```
4. (Optional) Use Ngrok to make the API publicly accessible:
   ```
   ngrok http 5000
   ```

## Required Permissions

In your Android app's `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

## API Usage

- Endpoint: `/process_image`
- Method: POST
- Request: Image file (png, jpg, or jpeg)
- Response: JSON containing calorie estimation and detected food items

Example cURL request:
```bash
curl -X POST -F "file=@path/to/your/image.jpg" http://your-api-url/process_image
```

## Roadmap

1. Enhance the YOLO model for better food recognition accuracy
   - Expand training dataset with more diverse food images
   - Fine-tune model parameters for improved performance
2. Implement user authentication and data synchronization
   - Integrate Firebase Authentication
   - Set up Cloud Firestore for data storage and sync
3. Develop a recommendation system for balanced meal planning
   - Implement collaborative filtering algorithm
   - Integrate nutritional guidelines for personalized recommendations
4. Optimize API performance for faster response times
   - Implement caching mechanisms
   - Explore serverless architecture for scalability

## Contributing

We welcome contributions to both the Android app and the backend API! Please follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/AmazingFeature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
5. Push to the branch (`git push origin feature/AmazingFeature`)
6. Open a Pull Request

## Acknowledgments

- YOLO (You Only Look Once) for object detection
- Flask for the backend API framework
- CameraX for Android camera functionality
- OkHttp3 for network operations
- Room for local data persistence
