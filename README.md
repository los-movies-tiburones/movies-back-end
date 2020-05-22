# Movies Shakers


## Endpoint

### Search by title

```
GET https://movie-sharkers.herokuapp.com/movies?title=[String]&page=[Int]&size=[Long]
```

* title = Required
* page = Optional default value is 0
* size = Optional default value is 10

#### Examples

```
GET https://movie-sharkers.herokuapp.com/movies?title=superman
GET https://movie-sharkers.herokuapp.com/movies?title=superman&page=1
GET https://movie-sharkers.herokuapp.com/movies?title=superman&size=5
GET https://movie-sharkers.herokuapp.com/movies?title=superman&page=1&size=5
```

### Search with filters

```
GET https://movie-sharkers.herokuapp.com/movies?genres=[Array<String>]&sort=[Array<String>]&page=[Int]&size=[Long]
```

* genres = Optional default value is empty array
    * You can choose one or more fields separated by commas.
* sort = Optional default value is empty string
    * The following fields can be chosen: 
        * id
        * title
        * year
        * budget
        * tmdbId
    * You can request the descending order putting the character `-` before the field
    
* page = Optional default value is 0
* size = Optional default value is 10

#### Examples

```
GET https://movie-sharkers.herokuapp.com/movies
GET https://movie-sharkers.herokuapp.com/movies?page=0&size=10
GET https://movie-sharkers.herokuapp.com/movies?genres=Action
GET https://movie-sharkers.herokuapp.com/movies?genres=Action,Crime
GET https://movie-sharkers.herokuapp.com/movies?sort=title
GET https://movie-sharkers.herokuapp.com/movies?sort=-budget
GET https://movie-sharkers.herokuapp.com/movies?page=1&size=20&sort=title&genres=Action
```

## Example object json

```json
[
    {
        "id": "6",
        "title": "Heat",
        "year": "1995-12-15",
        "budget": 60000000,
        "cover": "https://image.tmdb.org/t/p/w185/lbf2ueoiEfKIJr2qlT01zIEckbC.jpg",
        "tmdbId": "949",
        "genres": [
            "Action",
            "Crime",
            "Thriller"
        ]
    },
    {
        "id": "20",
        "title": "Money Train",
        "year": "1995-11-21",
        "budget": 68000000,
        "cover": "https://image.tmdb.org/t/p/w185/jWBDz6Mf9aQVBiUS76JQsEhvoJl.jpg",
        "tmdbId": "11517",
        "genres": [
            "Action",
            "Comedy",
            "Crime",
            "Drama",
            "Thriller"
        ]
    }
]
```
