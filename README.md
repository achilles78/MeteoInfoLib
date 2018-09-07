# MeteoInfoLib
MeteoInfo Class Library


### Build with maven

1. install maven3

2. build and install [wContour](https://github.com/Anebrithien/wContour) to your local maven repo

3. cd to project root directory, run `mvn install -Dmaven.test.skip=true`, 
then get jar file `meteoInfoLib-0.0.1-SNAPSHOT.jar` in `target/`

4. use this in your maven projects

  ```xml
  <dependency>
    <groupId>org.meteothinker</groupId>
    <artifactId>meteoInfoLib</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
  ```

5. you can install source jar by `mvn source:jar install`