Simple Weather Android App for class project

Assignment Details:

In this project you will need to:
1. Dynamic Data:
    -Look up zipcode latitude and longitude from http://craiginsdev.com/zipcodes/findzip.php?zip=60540
    -Pull weather XML from weather.gov using latitude and longitude
    http://forecast.weather.gov/MapClick.php?lat={latitude}&lon={longitude}&unit=0&lg=english&FcstType=dwml
    -Parse the rest of the weather xml file. See XML Breakdown for how each piece of the xml matches with eachother.
    -Download the weather images and save them in the app's Cache directory
2. Add a Menu! The menu should include the following:
    -Enter Zipcode (prompts the user for a zipcode)
    -Recent Zipcodes (list of the last 5 zipcodes search)
    -Current Weather (Displays the current weather -- project 1's activity)
    -7-Day Forecast (Displays the 7 day forecast activity)
    -Units (allows the user to switch between metric and imperial)
    -About (shows a simple dialog with some source info, like data from www.weather.gov)
3. Maintain the user's unit preference and recent zip codes between sessions using SharedPreferences.
4. Give the user more feedback:
    -Generate an implicit intent when the user clicks on the Current Location (a GEO intent)
    -Generate notification(s) when your app downloads a weather forecast with alerts. Have the intent be an implicit intent to the link for the alert. 
    -Can generate multiple notifications if multiple alerts
    -Generate Toast Messages when the user does something that causes an error (bad zipcode, unknown zipcode, weather not found, no internet connection etc)
5. Generate a GUI for the extended forecast:
    -For images, check the cache directory first, then download if not found
    -Convert Project 1's activity into a fragment
    -Create a new fragment for displaying forecast information
    -Transition between forecast days on user swipe action
