# Overview

This document provides a brief overview of the resources folder. This folder contains:

- Images
- Stlyes
- Colors
- Strings
- Layouts

## Changing the App Icon

To change the app icon, replace ic_launcher.png under all mipmap folders.

## Changing other icons used within the App

Any other icons and images that are used within the app will appear under the drawables folder. Replace all occurences of an icon or image to change it within the app.

## Style

The themes used within the app are present in styles.xml in the values folder.

## Colors

The color palette of the overall application, and other specific colors within the screens are defined in colors.xml under the values folder.

## Strings and Translations

The english strings are present at values/strings.xml. Turkish translations of the strings are present at values-tr/strings.xml.

To change a string, it would have to be changed in all strings.xml for every language.

Please see [this](https://github.com/anatolian/archaeological-survey-location-collector/issues/3) and [this](https://github.com/anatolian/archaeological-survey-location-collector/commit/d1706bf44bf62493ac0962476d1024c265510454) to learn more about adding a new language.

## Layouts

The XML layouts for all the screens are contained here.

- Splash screen
  - activity_splash.xml
  
- Records list screen
  - activity_main.xml - The template for the screen. Defines the toolbar and the two floating action buttons for settings and new.
  - content_main.xml - The content that appears in the screen, which in this case is a list
    - bucket_list_entry.xml - The layout of each element that appears in the list
    
- Data entry screen
  - activity_data_entry.xml - The template for the screen containing the toolbar, and placeholder for content
  - content_data_entry.xml - A scrollable layout with fields to take input from the user
  
