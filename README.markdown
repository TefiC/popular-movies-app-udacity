### Android Developer Nanodegree - Udacity
---
###Project 1 - Stage 1 (Popular Movies App)
---

### UWatch

Popular movies App that displays information on the Top 20 Most Popular or Top Rated movies (according the criteria selected by the user).

######These are the different features you will find on this app:

1 - When you first access the app's main screen, you will see a circular progress bar indicating data is being fetched. When data has been received and it starts to load, a custom placeholder image will be shown on each spot where there will be a movie poster but it will fade out to show the actual poster after it's completely loaded.

2 - Movie posters are displayed on a grid. The number of columns will adjust automatically depending on the device width and orientation.

3 -  You can select the sorting criteria you would like to browse through by clicking or tapping on the spinner that shows the current selection. A dropdown menu will be displayed with the options available. Options are: "Most Popular" movies or "Top rated" movies. 

The user interface will update automatically to respond to this new selection.

4 - Once you've browsed through the posters and found a movie that you would like to learn more about, clicking or tapping on a poster will take you to a movie details screen where you can find additional data like the movie's poster, title, release year, user average rating and plot. 

You can always go back to the posters section and select a new movie.

---
#### WARNING: To use this app you need an API key from [TheMovieDB API](https://www.themoviedb.org/).
There is a comment on the NetworkUtils class indicating where you need to include a constant for your API key in String format.

----

#### Special features
---
- On device rotation on the main posters grid, the scroll position will be maintained so the user doesn't have to scroll down again to find the movie he/she was interested in.

- The movie details section has a collapsing toolbar that will show the movie's title in a smaller font for better user experience when scrolling down.

<br>

---

####Technical features
---

- Movie posters are loaded using the [Picasso](http://square.github.io/picasso/) library that handles image loading.

<br>

---
####Handling errors
---

- **No internet connection:**  If there no internet connection available, a dialog box will alert the user. If the app has loaded data previously, this data will remain responsive but the user won't be able to request new data until there is an internet connection. 

    If the user had no internet connection and no data was loaded previously but he/she reconnects and restarts the app, data will be fetched automatically.

- **Poster didn't load correctly:** An error poster will be shown instead. On the main screen, the error poster will prompt the user to click if he/she wants to access the movie data, even if the poster is not available. Once inside the movie details screen, a second error poster will display a message saying that the poster couldn't be loaded. 

<br>

---

#####Attributions
---

- This app is powered by [TheMovieDB API](https://www.themoviedb.org/) which provides movies data and posters

- Images used to create the custom error and loading placeholders were taken from [Pixabay.com](https://pixabay.com/) under creative commons license

    - [Popcorn](https://pixabay.com/en/popcorn-buttered-cinema-corn-food-155602/)
    - [Movie roll](https://pixabay.com/en/filmstrip-film-frames-camera-film-33429/)


- StackOverflow forums were extremely helpful during this project.