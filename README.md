# NoteServ
It's a simple RESTful API to manage notes.

## Requirements
The only additional requirement is a running PostgreSQL server.

Database credentials and address are located in `./src/main/resources/application.properties`

## How to run the project
`./gradlew bootRun`

## Usage

### Showing notes
|Description|curl command|
|---|---|
|Show all existing notes|`curl -i -X GET http://host:port/notes`|
|Show the exact note|`curl -i -X GET http://host:port/notes/{id}`|
|Show note at revision|`curl -i -X GET http://host:port/notes/{id}/3`|
|Show note's history (including deleted notes)|`curl -i -X GET http://host:port/notes/{id}/history`|

### Creating notes
`curl -i -d "title=foo&content=bar" -X POST http://host:port/notes`

Successful execution will result in 201 HTTP status containing URL to the created note.

### Updating notes
`curl -i -d "title=foo1&content=bar1" -X PUT http://host:port/notes/ABCD`

### Deleting notes
`curl -i -X DELETE http://host:port/notes/ABCD`