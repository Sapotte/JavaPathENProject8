# Tour Guide
## Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  

## How to have gpsUtil, rewardCentral and tripPricer dependencies available ?

Run : 
```java
mvn install:install-file -D file=libs/gpsUtil.jar -D groupId=gpsUtil -D artifactId=gpsUtil -D version=1.0.0 -D packaging=jar  
mvn install:install-file -D file=libs/RewardCentral.jar -D groupId=rewardCentral -D artifactId=rewardCentral -D version=1.0.0 -D packaging=jar  
mvn install:install-file -D file=libs/TripPricer.jar -D groupId=tripPricer -D artifactId=tripPricer -D version=1.0.0 -D packaging=jar
