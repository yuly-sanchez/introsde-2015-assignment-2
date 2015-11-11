# Assignment 02: RESTful Services

In this **[assignment](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/assignments/assignment-2)** I implemented a server and a client calling this server. The server should be deployed on Heroku, so anybody can call it. I decide to work alone, then implement a client for my own server.


## Pre-Requisites
* Lab Session 05: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-5 "Permalink to LAB05: The REST architectural style & RESTful web services (1)")
* Lab Session 06: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-6 "Permalink to LAB06: CRUD RESTful Services (2)")
* Lab Session 07: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-7 "Permalink to LAB07: Reading and writing from Databases & JPA (Java Persistence API)")


## Project Structure
* **[introsde.lifecoach](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach) -** 
* **[introsde.lifecoach.dao](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/dao) -** will contain classes whose purpose will be to provide the underlying connection to the database
* **[introsde.lifecoach.model](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/model) -** will include classes that represent my domain data model and map the content in my database to objects that can be manipulated in Java
* **[introsde.lifecoach.resources](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/resources) -** will include the resources that are exposed throught the RESTful API, which can be seen as the controllers that receive requests and respond with a representation of the resources that are requested
* **[introsde.lifecoach.wrapper](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/wrapper) -**
* **[persistence.xml](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/WebContent/META-INF/persistence.xml) -** is a file presents into folder named META-INF  
* **[build.xml](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/build.xml) -** is an ant script which automates repetitive tasks directly from the command line.
* **[ivy.xml](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/ivy.xml) -** is a file which can specify the dependencies 
* **[lifecoach.sqlite](https://github.com/yuly-sanchez/introsde-2015-assignment-2) -** is the database that presents an evolved data model


## Services through a RESTful API
* **Request #1 :** `[GET/person]()` should list all the people in my database
* **Request #2 :** `[GET/person/{id}]()` should give all the personal information plus current measures of person identified by {id}
* **Request #3 :** `[PUT/person/{id}]()` should updated the personal information of the person identified by {id}
* **Request #4 :** `[POST/person]()` should create a new person and return the newly created person with its assigned id
* **Request #5 :** `[DELETE/person/{id}]()` should delete the person identified by {id}
* **Request #6 :** `[GET/person/{id}/{measureType}]()` should return the list of values the MeasureHistory of {measureType} for person identified by {id}
* **Request #7 :** `[GET/person/{id}/{measureType}]()` should return the value of {measureType} identified by {mid} for person identified by {id}
* **Request #8 :** `[POST/person/{id}/{measureType}]()` should save a new value for the {measureType} of person identified by {id} and archive the old value in the measureHistory
* **Request #9 :** `[GET/measureType]()` should return the list of measures
* **Request #10 :** `[PUT/person/{id}/{measureType}/{mid}]()` should update the value for the {measureType} identified by {mid}, related to the person identified by {id}
* **Request #11 :** `[GET/person/{id}/{measureType}?before={beforeDate}&after={afterDate}]()` should return the history of {measureType} for person {id} in the specified range of date
* **Request #12 :** `[GET /person?measureType={measureType}&max={max}&min={min}]()` should retrieves people whose {measureType} value is in the [{min},{max}] range

### GET/person
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<people>
    <person>
        <id>1</id>
        <firstname>Chan</firstname>
        <lastname>Jecky</lastname>
        <birthdate>1978-09-02T00:00:00+02:00</birthdate>
        <healthProfile>
            <measureType>
                <measure>weight</measure>
                <value>72.3</value>
            </measureType>
            <measureType>
                <measure>weight</measure>
                <value>86</value>
            </measureType>
        </healthProfile>
    </person>
    <person>
        <id>2</id>
        <firstname>Andrea</firstname>
        <lastname>Bonte</lastname>
        <birthdate>1978-09-02T00:00:00+02:00</birthdate>
        <healthProfile>
            <measureType>
                <measure>weight</measure>
                <value>85</value>
            </measureType>
        </healthProfile>
    </person>
    <person>
        <id>3</id>
        <firstname>Pallino</firstname>
        <lastname>Pinco</lastname>
        <birthdate>1978-09-02T00:00:00+02:00</birthdate>
        <healthProfile/>
    </person>
</people>
```


## License

Open source templates are Copyright (c) 2015 thoughtbot, inc.
It contains free software that may be redistributed
under the terms specified in the [LICENSE] file.

[LICENSE]: /LICENSE









 