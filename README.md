# WeatherApp
Simple Weather app in Android.

The main page presents the user with an EditText field, a "Get Weather" button, and a "Clear Search" button.
Additionaly, you will notice a location icon that can be used and, if granted permission, the textfield will populate with the city
in which the user is in. Make sure location permissions are on.

In some cases, if there are multiple cities in the country with the same name, for example, a search will yield no results.
This functionality is intended to be fixed in the future, allowing the user to narrow the search down to state.
For now, if a search yields no results, searching by postal code _should_ yield the proper results. 
This was tested using my hometown of Brentwood, CA. "Brentwood" yielded no results, but its zipcode of 94513 did.

Once the user has input a city a they can either: clear the text, or hit "get weather". 
This will then query the API (uses API from: https://darksky.net).
Once retrieved, the City Name, Summary (Mostly cloudy, sunny, etc.), Temp in Fahrenheit, 
Pressure in hPa, Humidity %, and Chance of Rain % will be displayed.

To simulate something that other weather apps do, the time was compared to a fixed "sunset time" (7pm) and "sunrise time" (7am).
If the local time is later than 7pm and earlier than 7am, the background will use a nighttime background.
Otherwise, the daytime background will be used. If curious, this can be observed by searching for cities around the world that
might be within a timezone that falls under these hours. 
The reason for the set times of 7am/7pm is because the API only provides a sunset time for the "daily" data set, and not the current
day. It does, however, provide the local time. In the future I will try to somehow improve this since sunset changes season to season.

If the user wishes to go back to the search page, they can use their devices back button, or use the back arrow at the top of the screen.
