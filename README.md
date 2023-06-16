# EcoEase

## What is EcoEase 🤔

EcoEase is project focus on garbage management. Garbage management is rooting problem in our country, many problem related to garbage management such as littering happened because our people awarness about garbage management is still low compare to other country. Beside that, there also other factor like a lack of good waste management facilities from the government for certain regions. By creating this app we hope we can increase people awarness, and also can creating job field for other by joining partnership with us 🌱.

## Getting Started 📱

1. Make sure your phone's android version is above 8.0 version (Android Oreo).
2. Download from .apk file from this repository release section, or using this [link](https://github.com/ecoEase/ecoease-mobile/releases/tag/v1.0.0).
3. In order to use feature like make order, you need to give location permission so our app can pin point your location and show the orders in map screen.
4. In order to use scan garbage type feature, you need to give camera permission so our app can get image using your phone camera.
5. Beside using camera, you can also pick garbage picture from your phone gallery.
6. Don't forget to have proper internet connection so you don't have problem while making garbage order or taking garbage order.

## Preview 👏

![dashboard screen](https://i.imgur.com/BLxjO43.gif) ![profile screen](https://i.imgur.com/DZu5TNa.gif)

### Dashboard and Profil Screen

![change address screen](https://i.imgur.com/LtZ8Rio.gif) ![success create order](https://i.imgur.com/E9cLyyi.gif)

### Change Address and Making Orde

![order history screen](https://i.imgur.com/YMAT4i5.gif) ![order detail screen](https://i.imgur.com/VFmIN1A.gif)

### Order History And Detail

![map screen](https://i.imgur.com/xXOqSiB.gif)

### Map

![chatroom and chats screen](https://i.imgur.com/a2mwJ1l.gif)

### Chatroom and Chats Screen

![scan garbage](https://i.imgur.com/SBbcAvr.gif)

### Scan Garbage

# Wanna run this project locally? 💻

## To run this project in your machine please follow this steps

1. Make sure in your computer already installed Android Studio
2. Clone this project
3. First time you run this project, it will be give error because this project need several API keys and Firebase Realtime Database URL.
4. To add those required API keys, first create local.properties file in root project directory.
5. Add these variables and fill with your API keys and Firebase Realtime Database URL.
   ```
   google_map_api_key=<YOUR GOOGLE MAP API KEY>
   firebase_realtime_db_url=<YOUR FIREBASE REALTIME DATABASE URL>
   FCM_key=<YOUR FIREBASE CLOUD MESSAGING API KEY>
   ```
6. Now it's ready to run in your machine.

## Mobile Tech Stack 😎

| Library                    | Link Documentation                                                                           |
| -------------------------- | -------------------------------------------------------------------------------------------- |
| Lottie                     | [Documentation](https://github.com/airbnb/lottie-android/)                                   |
| Firebase Realtime Database | [Documentation](https://firebase.google.com/docs/database/android/start)                     |
| Firebase Cloud Messaging   | [Documentation](https://firebase.google.com/docs/cloud-messaging/android/client)             |
| CameraX                    | [Documentation](https://developer.android.com/training/camerax)                              |
| SplashScreen               | [Documentation](https://developer.android.com/develop/ui/views/launch/splash-screen/migrate) |
| Google Maps                | [Documentation](https://developer.android.com/training/maps)                                 |
| Room                       | [Documentation](https://developer.android.com/training/data-storage/room)                    |
| Datastore                  | [Documentation](https://developer.android.com/training/data-storage/room)                    |
| Retrofit                   | [Documentation](https://square.github.io/retrofit/)                                          |
