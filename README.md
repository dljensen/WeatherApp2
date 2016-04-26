Simple Weather Android App for class project

Assignment Details:

In this project you will need to:
<ol>
<li> Dynamic Data:
    <ul>
        <li>Look up zipcode latitude and longitude from http://craiginsdev.com/zipcodes/findzip.php?zip=60540</li>
        <li>Pull weather XML from weather.gov using latitude and longitude http://forecast.weather.gov/MapClick.php?lat={latitude}&lon={longitude}&unit=0&lg=english&FcstType=dwml</li>
        <li>Parse the rest of the weather xml file. See XML Breakdown for how each piece of the xml matches with eachother.</li>
        <li>Download the weather images and save them in the app's Cache directory</li>
    </ul></li>
<li> Add a Menu! The menu should include the following:
    <ul>
        <li>Enter Zipcode (prompts the user for a zipcode)</li>
        <li>Recent Zipcodes (list of the last 5 zipcodes search)</li>
        <li>Current Weather (Displays the current weather -- project 1's activity)</li>
        <li>7-Day Forecast (Displays the 7 day forecast activity)</li>
        <li>Units (allows the user to switch between metric and imperial)</li>
        <li>About (shows a simple dialog with some source info, like data from www.weather.gov)</li>
    </ul></li>
<li> Maintain the user's unit preference and recent zip codes between sessions using SharedPreferences.</li>
<li>Give the user more feedback:
    <ul>
        <li>Generate an implicit intent when the user clicks on the Current Location (a GEO intent)</li>
        <li>Generate notification(s) when your app downloads a weather forecast with alerts. Have the intent be an implicit intent to the link for the alert.</li>
        <li>Can generate multiple notifications if multiple alerts</li>
        <li>Generate Toast Messages when the user does something that causes an error (bad zipcode, unknown zipcode, weather not found, no internet connection etc)</li>
    </ul></li>
<li> Generate a GUI for the extended forecast:
    <ul>
        <li>For images, check the cache directory first, then download if not found</li>
        <li>Convert Project 1's activity into a fragment</li>
        <li>Create a new fragment for displaying forecast information</li>
        <li>Transition between forecast days on user swipe action</li>
    </ul></li>
</ol>
