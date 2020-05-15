# Movies Shakers


## Endpoint

### Search by title

```
GET {{api-url}}/movies?title=[String]&page=[Int]&size=[Long]
```

* title = Required
* page = Optional default value is 0
* size = Optional default value is 10

#### Examples

```
GET {{api-url}}/movies?title=superman
GET {{api-url}}/movies?title=superman&page=1
GET {{api-url}}/movies?title=superman&size=5
GET {{api-url}}/movies?title=superman&page=1&size=5
```

### Search with filters

```
GET {{api-url}}/movies?genres=[Array<String>]&sort=[Array<String>]&page=[Int]&size=[Long]
```

* genres = Optional default value is empty array
    * You can choose one or more fields separated by commas.
* sort = Optional default value is empty array
    * The sort with the following fields. You can choose one or more fields separated by commas. 
        * id
        * title
        * year
        * budget
        * tmdbId
    * You can request the descending order putting the character `-` before the field
    * You can sort with many fields, for example: title and then by year [title,year]
   
* page = Optional default value is 0
* size = Optional default value is 10

####Examples

```
GET {{api-url}}/movies
GET {{api-url}}/movies?page=1&size=20
GET {{api-url}}/movies?genres=Action
GET {{api-url}}/movies?genres=Action,Crime
GET {{api-url}}/movies?sort=title
GET {{api-url}}/movies?sort=title,-budget
GET {{api-url}}/movies?page=1&size=20&sort=title,-budget&genres=Action
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
