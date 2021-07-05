# Stargazer

Android application design for the people who love to go for stargazing.

## Features

1. Containing all constellation introduction and story.
2. Containing multiple recommend places to go stargazing.
   Mainly for places in Taoyuan for current version.
3. Allow user to register account to record their favorite constellation and stargazing recommended place.
4. Allow user to check out the instant weather and weather broadcast in 3 day after.
5. Allow user to using the AR function to check the constellation which seen in the reality sky in night.

## Structure

1. ### Data base and php

   1. Using mySQL DataBase for storage user data, constellation and stargazing place.
   2. Using PHP to connect android system and mySQL database.
      Using PDO in the PHP structure, avoiding SQL injection.

2. ### Front End UI interact

   1. Basic android UI interacting with user, including basic android UI widget.
   2. Combine third party android library "MPAndroidChart" for the weather result.

3. ### Unity structure
   1. Using unity to create the night sky with stars and constellation in correct position.
   2. Using unity to catch direction which cellphone facing at to build the AR function.
   3. Pack up the Unity project as a jar file to combine into android system.