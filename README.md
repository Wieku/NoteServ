# NoteServ
It's a simple RESTful API to manage notes. It's made with Spring framework and JPA. It is made for local usage, so it does not contain any user authentication and authorization. 

## Requirements
The only additional requirement is a running [PostgreSQL](https://www.postgresql.org/) server.

Other dependencies are managed automatically by Gradle wrapper.

### Running the database server

#### Windows
You can use the Windows installer, it will start the server automatically.

#### Linux
Download the `postgresql` package and start the service.

### Creating the database

You can add the database and user using additional tool called [pgAdmin](https://www.pgadmin.org/).

If you want to do it using a terminal, follow the steps below:

On linux type: `sudo su postgres psql`

On windows you would need to run `InstallationDir\scripts\runpsql.bat`

Then type the following commands:
```$bash
postgres=# create database notesdb;
postgres=# create user noteserv with encrypted password 'password1';
postgres=# grant all privileges on database notesdb to noteserv;
```

Database credentials and address configuration for application is located in `./src/main/resources/application.properties`

## How to run the project

#### Tests
`./gradlew test`

#### Application
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
`curl -i -d "title=foo1&content=bar1" -X PUT http://host:port/notes/{id}`

### Deleting notes
`curl -i -X DELETE http://host:port/notes/{id}`