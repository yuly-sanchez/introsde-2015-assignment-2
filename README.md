# Assignment 02: RESTful Services

In this **[assignment](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/assignments/assignment-2)** I implemented a server and a client calling this server. The server should be deployed on Heroku, so anybody can call it. I decide to work alone, then implement a client for my own server.


## Pre-Requisites
* Lab Session 05: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-5 "Permalink to LAB05: The REST architectural style & RESTful web services (1)")
* Lab Session 06: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-6 "Permalink to LAB06: CRUD RESTful Services (2)")
* Lab Session 07: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-7 "Permalink to LAB07: Reading and writing from Databases & JPA (Java Persistence API)")


## Project Structure
* **[introsde.lifecoach](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach) -** will contain  
the class App.java that is the stand alone server
* **[introsde.lifecoach.dao](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/dao) -** will contain classes whose purpose will be to provide the underlying connection to the database
* **[introsde.lifecoach.model](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/model) -** will include classes that represent my domain data model and map the content in my database to objects that can be manipulated in Java
* **[introsde.lifecoach.resources](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/resources) -** will include the resources that are exposed throught the RESTful API, which can be seen as the controllers that receive requests and respond with a representation of the resources that are requested
* **[introsde.lifecoach.wrapper](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach/wrapper) -** will contain the wrapper used to format XML and JSON
* **[persistence.xml](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/WebContent/META-INF/persistence.xml) -** is a file presents into folder named META-INF  
* **[build.xml](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/build.xml) -** is an ant script which automates repetitive tasks directly from the command line.
* **[ivy.xml](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/ivy.xml) -** is a file which can specify the dependencies 
* **[lifecoach.sqlite](https://github.com/yuly-sanchez/introsde-2015-assignment-2) -** is the database that presents an evolved data model
* **[client-server-xml.log](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/client-server-xml.log) -** log file of the client calling my server using format XML format
* **[client-server-json.log](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/client-server-json.log) -** log file of the client calling my server using format JSON format


## Usage
This project contains the `ant build script` to compile source code, run tests and generate documentationn directly from the command line:
```
 ant execute.client
```
This target calls the following targets defined in the build file:
* `execute.client.myServer.xml` send all requests to my server with the body in XML format and accept response in XML. This generate the ouput saved into [client-server-xml.log](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/client-server-xml.log) file. 
* `execute.client.myServer.json` send all requests to my server with the body in JSON format and accept response in JSON. This generate the ouput saved into [client-server-json.log](https://github.com/yuly-sanchez/introsde-2015-assignment-2/blob/master/client-server-json.log) file. 


## Services through a REST APIs
* **Request #1 :**  [GET/person](#get-person)
* **Request #2 :**  [GET/person/{id}](#get-personid) 
* **Request #3 :**  [PUT/person/{id}](#put-personid)
* **Request #4 :**  [POST/person](#post-person)
* **Request #5 :**  [DELETE/person/{id}](#delete-personid) 
* **Request #6 :**  [GET/person/{id}/{measureType}](#get-personidmeasuretype)
* **Request #7 :**  [GET/person/{id}/{measureType}/{mid}](#get-personidmeasuretypemid)
* **Request #8 :**  [POST/person/{id}/{measureType}](#post-personidmeasuretype)
* **Request #9 :**  [GET/measureTypes](#get-measuretypes)
* **Request #10 :** [PUT/person/{id}/{measureType}/{mid}](#put-personidmeasuretypemid)
* **Request #11 :** [GET/person/{id}/{measureType}?before={beforeDate}&after={afterDate}](#get-personidmeasuretypebeforebeforedateafterafterdate)
* **Request #12 :** [GET /person?measureType={measureType}&max={max}&min={min}](#get-personmeasuretypemeasuretypemaxmaxminmin)

#### GET /person
Return a list all the people into database.
##### Request
```
 GET https://desolate-castle-6772.herokuapp.com/sdelab/person
```
##### XML format
```xml
 <people>
    <person>
        <id>1</id>
        <firstname>Guido</firstname>
        <lastname>Pugliese</lastname>
        <birthdate>1979-12-12T00:00:00+01:00</birthdate>
        <healthProfile>
            <measureType>
                <measure>weight</measure>
                <value>80</value>
            </measureType>
            <measureType>
                <measure>height</measure>
                <value>1.75</value>
            </measureType>
        </healthProfile>
    </person>
    
    <!--more people-->
    
</people>
```
##### JSON format
```json
 {"people": [
     {
     "firstname": "Guido",
     "birthdate": "1979-12-11",
     "healthProfile": [
        {
            "measure": "weight",
            "value": "80"
        },
        {
            "measure": "height",
            "value": "1.75"
        }
     ],
     "id": 1,
     "lastname": "Pugliese"
     },
     
        <!--more people-->
        
     ]}

```

#### GET /person/{id}

#### PUT /person/{id}

#### POST /person

#### DELETE /person/{id}

#### GET /person/{id}/{measureType}

#### GET /person/{id}/{measureType}/{mid}

#### POST /person/{id}/{measureType}

#### GET /measureTypes

#### PUT /person/{id}/{measureType}/{mid}

#### GET /person/{id}/{measureType}?before={beforeDate}&after={afterDate}

#### GET /person?measureType={measureType}&max={max}&min={min}

## License

Open source templates are Copyright (c) 2015 thoughtbot, inc.
It contains free software that may be redistributed
under the terms specified in the [LICENSE] file.

[LICENSE]: /LICENSE









 