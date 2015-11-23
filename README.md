# Assignment 02: RESTful Services

In this **[assignment](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/assignments/assignment-2)** I implemented a server and a client calling my server. The server should be deployed on Heroku, so anybody can call it. I decide to work alone, then implement a client for my own server.


## Pre-Requisites
* Lab Session 05: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-5 "Permalink to LAB05: The REST architectural style & RESTful web services (1)")
* Lab Session 06: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-6 "Permalink to LAB06: CRUD RESTful Services (2)")
* Lab Session 07: [Webpage](https://sites.google.com/a/unitn.it/introsde_2015-16/lab-sessions/lab-session-7 "Permalink to LAB07: Reading and writing from Databases & JPA (Java Persistence API)")


## Project Structure
* **[introsde.lifecoach](https://github.com/yuly-sanchez/introsde-2015-assignment-2/tree/master/src/introsde/lifecoach) -** will contain the class App.java that is the stand alone server
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
##### Response status
```
 HTTP Status: 200
```
##### XML
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
##### JSON
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
* * *
#### GET /person/{id}
Returns all the personal information plus current measures of person identified by {id}.
##### Request
```
 GET https://desolate-castle-6772.herokuapp.com/sdelab/person/1
```
##### Response status
```
 HTTP Status: 200
```
##### XML
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
</people>
```
##### JSON
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
     } 
 ]}

```
* * *
#### PUT /person/{id}
Update the personal information of the person identified by {id}.
##### Request
```
 PUT https://desolate-castle-6772.herokuapp.com/sdelab/person/1
```
##### Body XML
```xml
 <person>
        <firstname>Felipe</firstname>
 </person>
```
##### Body JSON
```json
 {
     "firstname": "Juan"
 } 
```
##### Response status
```
 HTTP Status: 201
```
* * *
#### POST /person
Create a new person and return the newly created person with its assigned id.
##### Request
```
 POST https://desolate-castle-6772.herokuapp.com/sdelab/person
```
##### Body XML
```xml
 <person>
    <firstname>Pinco</firstname>
    <lastname>Palla</lastname>
    <birthdate>1978-09-02</birthdate>
    <healthprofile>
        <measureType>
            <measure>height</measure>
            <value>180</value>
        </measureType>
        <measureType>
            <measure>weight</measure>
            <value>86</value>
        </measureType>
    </healthprofile>
</person>
```
##### Body JSON
```json
{
  "firstname": "Pinco",
  "lastname": "Palla,
  "birthdate": "1978-09-02",
  "healthprofile": [
    {
      "value": "86",
      "measure": "weight"
    },
    {
      "value": "180",
      "measure": "height"
    }
  ]
}
```
##### Response status
```
 HTTP Status: 200
```
##### XML
```xml
 <person>
    <firstname>Pinco</firstname>
    <lastname>Palla</lastname>
    <birthdate>1978-09-02</birthdate>
    <healthprofile>
        <measureType>
            <measure>height</measure>
            <value>180</value>
        </measureType>
        <measureType>
            <measure>weight</measure>
            <value>86</value>
        </measureType>
    </healthprofile>
</person>
```
##### JSON
```json
{
  "firstname": "Pinco",
  "lastname": "Palla,
  "birthdate": "1978-09-02",
  "healthprofile": [
    {
      "value": "86",
      "measure": "weight"
    },
    {
      "value": "180",
      "measure": "height"
    }
  ]
}
```
* * *
#### DELETE /person/{id}
Delete the person identified by {id} from database.
##### Request
```
 DELETE https://desolate-castle-6772.herokuapp.com/sdelab/person/4
```
##### Response status
```
 HTTP Status: 404
 Person with the id 4 is deleted.
```
* * *
#### GET /person/{id}/{measureType}
Returns the list of values the history of {measureType} for person identified by {id}.
##### Request
```
 GET https://desolate-castle-6772.herokuapp.com/sdelab/person/1/height
```
##### Response status
```
 HTTP Status: 200
```
##### XML
```xml
 <measureHistory>
    <measure>
        <mid>2</mid>
        <value>1.75</value>
        <created>1978-09-02T00:00:00+02:00</created>
    </measure>
    <measure>
        <mid>6</mid>
        <value>1.72</value>
        <created>2011-12-09T00:00:00+01:00</created>
    </measure>
</measureHistory>

```
##### JSON
```json
 [
  {
    "mid": 2,
    "value": "1.75",
    "created": "1978-09-01"
  },
  {
    "mid": 6,
    "value": "1.72",
    "created": "2011-12-08"
  }
 ]
```
* * *
#### GET /person/{id}/{measureType}/{mid}
Return the value of {measureType} identified by {mid} for person identified by {id}.
##### Request
```
 GET https://desolate-castle-6772.herokuapp.com/sdelab/person/1/height/2
```
##### Response status
```
 HTTP Status: 200
 Mid value: 1.75
```
* * *
#### POST /person/{id}/{measureType}
Save a new value for the {measureType} for person identified by {id}.
##### Request
```
 POST https://desolate-castle-6772.herokuapp.com/sdelab/person/1/height
```
##### Body XML
```xml
 <measure>
    <measure>height</measure>
    <value>1.70</value>
 </measure>
```
##### Body JSON
```json
{ 
  "measure": "height",
  "value": "1.63"
}
```
##### Response status
```
 HTTP Status: 200
```
##### XML
```xml
 <lifeStatus>
    <measure>height</measure>
    <value>1.70</value>
 </lifeStatus>
```
##### JSON
```json
{ 
  "measure": "height",
  "value": "1.63"
 }
```
* * *
#### GET /measureTypes
Return the list the measures that my model supports.
##### Request
```
 GET https://desolate-castle-6772.herokuapp.com/sdelab/measureTypes
```
##### Response status
```
 HTTP Status: 200
```
##### XML
```xml
 <measureTypes>
    <measureType>weight</measureType>
    <measureType>height</measureType>
    <measureType>steps</measureType>
    <measureType>blood pressure</measureType>
    <measureType>heart rate</measureType>
    <measureType>bmi</measureType>
</measureTypes>
```
##### JSON
```json
 {
  "measureTypes": [
    "weight",
    "height",
    "steps",
    "blood pressure",
    "heart rate",
    "bmi"
  ]
 }
```
* * *
#### PUT /person/{id}/{measureType}/{mid}
Update the value for the {measureType} identified by {mid}, related to the person identified by {id}.
##### Request
```
 PUT https://desolate-castle-6772.herokuapp.com/sdelab/person/1/height/2
```
##### Body XML
```xml
 <measure>
    <measure>height</measure>
    <value>1.90</value>
 </measure>
```
##### Body JSON
```json
 { 
  "measure": "height",
  "value": "1.91"
 } 
```
##### Response status
```
 HTTP Status: 201
```
* * *
#### GET /person/{id}/{measureType}?before={beforeDate}&after={afterDate}
Return the history of {measureType} for person {id} in the specified range of date.
##### Request
```
 GET https://desolate-castle-6772.herokuapp.com/sdelab/person/1/height?before=1978-01-12&after=2015-12-10
```
##### Response status
```
 HTTP Status: 200
```
##### XML
```xml
  <measureHistory>
    <measure>
        <mid>2</mid>
        <value>1.90</value>
        <created>2011-12-09T00:00:00+02:00</created>
    </measure>
    <measure>
        <mid>6</mid>
        <value>1.72</value>
        <created>2011-12-08T00:00:00+01:00</created>
    </measure>
    <measure>
        <mid>7</mid>
        <value>1.63</value>
        <created>2011-12-09T00:00:00+01:00</created>
    </measure>
    <measure>
        <mid>10</mid>
        <value>1.70</value>
        <created>2011-12-08T00:00:00+01:00</created>
    </measure>
</measureHistory>
```
##### JSON
```json
 [
  {
    "mid": 2,
    "value": "1.91",
    "created": "2011-12-09"
  },
  {
    "mid": 6,
    "value": "1.72",
    "created": "2011-12-08"
  },
  {
    "mid": 7,
    "value": "1.63",
    "created": "2011-12-09"
  }
]
```
* * *
#### GET /person?measureType={measureType}&max={max}&min={min}
Return the people whose {measureType} value is in the [{min}, {max}] range.
##### Request
```
 GET https://desolate-castle-6772.herokuapp.com/sdelab/person?measureType=height?max=1.75&min=1.5
```
##### Response status
```
 HTTP Status: 200
```
##### XML
```xml
 <people>
    <person>
        <id>1</id>
        <firstname>Guido</firstname>
        <lastname>Pugliese</lastname>
        <birthdate>1979-12-11T23:00:00Z</birthdate>
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
    <person>
        <id>2</id>
        <firstname>Yuly</firstname>
        <lastname>Sanchez</lastname>
        <birthdate>1978-09-01T22:00:00Z</birthdate>
        <healthProfile>
            <measureType>
                <measure>weight</measure>
                <value>57.3</value>
            </measureType>
            <measureType>
                <measure>height</measure>
                <value>1.50</value>
            </measureType>
        </healthProfile>
    </person>
</people>

```
##### JSON
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
    {
        "firstname": "Yuly",
        "birthdate": "1978-09-01",
        "healthProfile": [
            {
                "measure": "weight",
                "value": "57.3"
            },
            {
                "measure": "height",
                "value": "1.50"
            }
        ],
        "id": 2,
        "lastname": "Sanchez"
    }
]}
```








 