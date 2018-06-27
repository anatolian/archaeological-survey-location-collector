# Code Overview

This folder contains all the Java code for the application. Java source files are divided into four packages according to their functions in the app. Below are links to their respective directories (with readmes) as well as descriptions of each package:

models - This package contains files pertaining to how data is represented
  - DataEntryElement.java - Represents a recorded find stored in the SQLite database that has yet to be synced to the web service
  - PathElement.java - Represents a recorded path stored in the SQLite database that has yet to be synced to the web service
  - StringObjectResponseWrapper.java - Represents an HTTP response for a GET request to the service

services - This package contains files pertaining to communications to remote sources
  - DatabaseHandler.java - Communicates with the app's SQLite database for saving app metadata
  - LocationCollector.java - Background processes for recording GPS positions and Reach messages
  - VolleyStringWrapper.java - Wrapper for String HTTP GET requests from the web service

ui - This package contains files pertaining to the user interface, namely activities and dialog screens
  - BaseActivity.java: A barebones activity definition that all activities extend from
  - BucketListEntryAdapter.java: An adapter for the list of finds on the finds screen
  - DataEntryActivity.java: The screen for registering a find
  - MainActivity.java: The main screen that contains the list of finds and paths
  - PathEntryActivity.java: The screen for recording paths
  - PathEntryAdapter.java: An adapter for the list of paths on the paths screen
  - SplashActivity.java: The Location Collector logo screen that first appears when the app launches
  - SyncActivity.java: The screen with the sync button

util - This package contains miscellaneous helper files
  - Constants.java: A list of static variable definitions
  - GenericFileProvider.java: An empty file provider required for Nougat+ compatibility
