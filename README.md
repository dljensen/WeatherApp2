<h2>Simple Weather Android App for <a href="http://bacraig.faculty.noctrl.edu/project2.html">class project</a></h2>

<h4>Assignment Details:</h4>

Project Requirements:
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
        <li><strike>Enter Zipcode (prompts the user for a zipcode)</strike></li>
        <li><strike>Recent Zipcodes (list of the last 5 zipcodes search)</strike></li>
        <li><strike>Current Weather (Displays the current weather -- project 1's activity)</strike></li>
        <li>7-Day Forecast (Displays the 7 day forecast activity)</li>
        <li><strike>Units (allows the user to switch between metric and imperial)</strike></li>
        <li><strike>About (shows a simple dialog with some source info, like data from www.weather.gov)</strike></li>
    </ul></li>
    <li><strike>Maintain the user's unit preference and recent zip codes between sessions using SharedPreferences.</strike></li>
<li>Give the user more feedback:
    <ul>
        <li><strike>Generate an implicit intent when the user clicks on the Current Location (a GEO intent)</strike></li>
        <li><strike>Generate notification(s) when your app downloads a weather forecast with alerts. Have the intent be an implicit intent to the link for the alert.</strike></li>
        <li><strike>Can generate multiple notifications if multiple alerts</strike></li>
        <li>Generate Toast Messages when the user does something that causes an error (bad zipcode, unknown zipcode, weather not found, no internet connection etc)</li>
    </ul></li>
<li> Generate a GUI for the extended forecast:
    <ul>
        <li>For images, check the cache directory first, then download if not found</li>
        <li><strike>Convert Project 1's activity into a fragment</strike></li>
        <li>Create a new fragment for displaying forecast information</li>
        <li>Transition between forecast days on user swipe action</li>
    </ul></li>
</ol>

<h3>Items Completed:</h3>
<ul>
    <li>Thursday, April 28: Fixed the shared preferences to store the most recent zipcodes, added dialog for recent zips</li>
    <li>Wednesday, April 27: Created an alert dialog box for about info, fixed the Project 1 fragment, added an alert dialog to enter zip codes</li>
    <li>Tuesday, April 26: Added fragment for Project 1</li>
    <li>Monday, April 25: Created a drop-down menu. It has no functionality at this point.</li>
</ul>
