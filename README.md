# Archaeology Survey - Android App

This Android app is designed to support data collection during archaeological fieldwork.  Specifically, this app can be modified to fit the data structures and infrastructure of a field surface survey project that involves fieldwalking.  The main goal of this app is to geolocate field observations, objects, and other remains (finds) at a high level of accuracy.  An external GNSS hardware device is the central feature of this application.  New technology enables the inexpensive use of differential GNSS, where one GNSS receiver placed over a known point transmits corrections of the satellite signals to a roving GNSS unit with the field walkers.  These devices communicate over the internet.

Although it can be easily modified, the default data collection enabled by the application include the normal types of data collected during surface survey: the GNSS coordinates of a find, photographs of the finds, and descriptive information.  The app is initially designed to capture point information, though polygons and 3d information could eventually be recorded as well.

A second git project called archaeological-survey-location-service is designed to support a server-side implementation that will enable the upload of the data collected during fieldwalking to a project's central database.  This upload process could happen in real-time or be delayed until wifi is available.


In order to better enable reuse of this code base for other projects and data structures, the intent of this documentation is to briefly overview the flow of the application. Readme files within certain important project folders also help provide details on the files in those folders.

## Project File and Folder Structure

- The build settings for the app including target Android SDK version, current app version name and number, dependencies, etc. can be found in [/app/build.gradle](https://github.com/anatolian/archaeological-survey-location-collector/blob/master/app/build.gradle)

- The code can be found at [/app/src/main/java/edu/upenn/sas/archaeologyapp/](https://github.com/anatolian/archaeological-survey-location-collector/tree/master/app/src/main/java/edu/upenn/sas/archaeologyapp). The folder also contains an additional readme file for further information.

- Resources including layouts, strings, colors, images, etc. can be found at [/app/src/main/res/](https://github.com/anatolian/archaeological-survey-location-collector/tree/master/app/src/main/res)

  - Strings also includes Turkish translations. Other languages can be added at the same location. Please see [this](https://github.com/anatolian/archaeological-survey-location-collector/issues/3) and [this](https://github.com/anatolian/archaeological-survey-location-collector/commit/d1706bf44bf62493ac0962476d1024c265510454) to learn more about adding a new language.
  
  - Layouts determine how elements appear on each screen
  
  - For more information about the resources folder, please see [/app/src/main/res/README.md](https://github.com/anatolian/archaeological-survey-location-collector/blob/eanvith/documentation/app/src/main/res/README.md)
  
- The manifest file can be found at [/app/src/main/AndroidManifest.xml](https://github.com/anatolian/archaeological-survey-location-collector/blob/master/app/src/main/AndroidManifest.xml). This file contains information about what Actvities need to be packaged into the application, and the permissions the application requires.

- Debug APKs with details about each version can be found at [/debug-APKs/](https://github.com/anatolian/archaeological-survey-location-collector/tree/master/debug-APKs)


## UI flow


- The app starts with a splash screen that is presented for a very brief amount of time, followed by the list of records currently present on the phone.

- The records list screen has a '+' floating action button at the bottom, clicking which opens up the data entry screen.

- On the data entry screen, users can fill in information associated with a new record. Once done, and the user hits back, the record is automatically saved if mandatory fields have been filled (GPS, picture). Else, the new record is discarded.

- Selecting an existing record from the records list screen opens up the data entry screen with information about the selected entry pre-populated. The user can then go ahead and make any necessary changes, and hit back to save and go back to the records list screen.

- On the top right of the records list screen is the 'settings' button - this is still a work in progress, but will lead the user to a settings screen where he can configure server information.


# LICENSE

The use of this project is governed by the license found [here](https://github.com/anatolian/archaeological-survey-location-collector/blob/master/LICENSE)
