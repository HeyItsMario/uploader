# uploader

## Getting Started



### Running the CLI app.
* There are 3 files that exists in the repo that you can use to run initial tests with; commas.txt, pipes.txt and spaces.txt.
* To run the file uploader, parser and view, run this command. (Make sure you are running this command within the root directory of the project.)

```lein run -m uploader.parser.file-parser ./commas.txt ./pipes.txt ./spaces.txt```

* The CLI takes one or more, space separated, file-paths as arguments. Make sure you provide the correct relative file path or full path name to files you would like to upload.


### Running the server

* To run the server simply run this command

```lein run```

* To upload via the API. You can run this command using cUrl.

`curl -d '{"first-name":"Joe", "last-name":"public", "gender": "male", "favorite-color": "Blue", "birthdate": "01-02-1993"}' -H "Content-Type: application/json" -X POST http://localhost:8080/records`

* The API accepts JSON, the example above show the all the valid JSON keys.
* Below is an example using text/plain.

`curl -d 'Smith Jane Female Indigo 2001-01-02' -H "Content-Type: text/plain" -X POST http://localhost:8080/records`


* To view the sorted endpoints just use these commands

`curl http://localhost:8080/records/gender`

`curl http://localhost:8080/records/birthdate`

`curl http://localhost:8080/records/name`

