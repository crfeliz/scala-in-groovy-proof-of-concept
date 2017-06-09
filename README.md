# scala-in-groovy-proof-of-concept
### Proof of concept for calling scala (which uses play-json) from groovy

First
- cd my-scala-lib
- sbt publishM2

Run via scala
- cd my-scala-lib
- sbt run

Run via groovy
- cd my-grails-app
- grails run-app
- ```curl -X GET \
  http://localhost:8080/my-grails-app/hello-scala \
  -H 'cache-control: no-cache' \
  -H 'postman-token: 17f457e1-31f0-9b39-ddda-5f43d0bcca2c'
  ```
Groovy/Java serialization is done via reflection and play-json. 

Scala serialization is done with standard play-json formats.


