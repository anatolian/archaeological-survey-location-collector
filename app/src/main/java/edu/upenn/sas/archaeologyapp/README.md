# Code Overview

This folder contains all the Java code for the application. Below is a brief overview of what each class does:

- BaseActivity.java: This is a base class for all activities, and consists of functionality commonly used within other activity classes.

- ConstantsAndHelpers.java: Any constants and helpful functions that are required within other classes are defined here.

- SplashActivity.java: This is the starting point of the application. It waits for a delay (specified in ConstantsAndHelpers.java) and starts the records list activity.

- MainActivity.java: Populates a list of current records from the DB. Also has a 'new' and 'settings' button.  This is the existing records list screen.

- BucketListEntryAdapter.java: Defines how each element in the records list needs to be shown.

- DataBaseHandler.java: Contains helper functions for adding, editing and deleting elements from the table of record elements.

- DataEntryElement.java: The model class for a record entry.

- DataEntryActivity.java: All user input for creating/updating a record is handled here, including using the GNSS and Camera.  This is the class that contains most of the logic about what this app does.
