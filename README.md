# Archaeology Survey - Android App

This Android app is designed to support data collection during archaeological fieldwork.  Specifically, this app can be modified to fit the data structures and infrastructure of a field surface survey project that involves fieldwalking.  The main goal of this app is to geolocate field observations, objects, and other remains (finds) at a high level of accuracy.  An external GNSS hardware device is the central feature of this application.  New technology enables the inexpensive use of differential GNSS, where one GNSS receiver placed over a known point transmits corrections of the satellite signals to a roving GNSS unit with the field walkers.  These devices communicate over the internet.

Although it can be easily modified, the default data collection enabled by the application include the normal types of data collected during surface survey: the GNSS coordinates of a find, photographs of the finds, and descriptive information.  The app is initially designed to capture point information, though polygons and 3d information could eventually be recorded as well.

A second git project called archaeological-survey-location-service is designed to support a server-side implementation that will enable the upload of the data collected during fieldwalking to a project's central database.  This upload process could happen in real-time or be delayed until wifi is available.


In order to better enable reuse of this code base for other projects and data structures, the intent of this documentation is to briefly overview the flow of the application. Readme files within certain important project folders also help provide details on the files in those folders.

## Project File and Folder Structure
This section provides a brief overview of the structure and location of the files in this Android app, as well as links to further information about each.

- The build settings for the app including target Android SDK version, current app version name and number, dependencies, etc. can be found in [/app/build.gradle](https://github.com/anatolian/archaeological-survey-location-collector/blob/master/app/build.gradle)

- The Java code for the app can be found at [/app/src/main/java/edu/upenn/sas/archaeologyapp/](https://github.com/anatolian/archaeological-survey-location-collector/tree/master/app/src/main/java/edu/upenn/sas/archaeologyapp). That folder also contains an additional readme file specific to the Java side of the app, for further information.

- Resources are the graphical elements of the app and include screen layouts, display text, colors, images, etc.  These can be found at [/app/src/main/res/](https://github.com/anatolian/archaeological-survey-location-collector/tree/master/app/src/main/res) That folder also contains an additional readme file specific to the Resources side of the app, for further information.

  - Text displayed in the app can be displayed in multiple languages, current support is for Turkish and English.  Additional languages can be supported by adding a values subfolder and Strings.xml specific to the language in [/app/src/main/res/](https://github.com/anatolian/archaeological-survey-location-collector/tree/master/app/src/main/res).
  
  - Screen layouts are .xml files that determine how other resources appear on each screen
    
- An Android manifest file contains information about which workflows and Java classes, known as Actvities, need to be packaged into the application, and the overall permissions that the application requires from the device.  The manifest file can be found at [/app/src/main/AndroidManifest.xml](https://github.com/anatolian/archaeological-survey-location-collector/blob/master/app/src/main/AndroidManifest.xml).

- Debug APKs with details about each version can be found at [/debug-APKs/](https://github.com/anatolian/archaeological-survey-location-collector/tree/master/debug-APKs)


## The User Experience
This section provides an overivew of how the user experiences the app, and contains links to the Java Activity classes and Resource files that impact each screen.

- The app starts with a brief splash screen followed by a screen that lists the records currently present on the device.  These data were collected earlier but have not yet been uploaded.

- The records list screen has a '+' floating action button at the bottom that takes a user to the data entry screen where they can enter new data.

- When the new data entry screen opens, it immediately attempts to read a GNSS point.  This point can be updated mnaully by pressing the GNSS button again while still on the screen. The user can now take multiple photos of the item, and fill out some descriptive data. Once done, the user hits back and the record is automatically saved if the two mandatory fields have been entered (GNSS point and at least one photograph).  A warning message will popup if either is missing, allowing the user to either continue back to the list screen without saving the data, or staying on the new data entry form.

- Selecting an existing record from the records list screen opens up the data entry screen with a display of the current information about the selected record. The user can make any necessary changes, and hit back to save and go back to the records list screen again. 

- On the top right of the records list screen is the 'settings' button for configuring server information.

# LICENSE

The use of this project is governed by the license found [here](https://github.com/anatolian/archaeological-survey-location-collector/blob/master/LICENSE)
