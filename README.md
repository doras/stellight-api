# StelLight-API

Fan-made API about Korean VTuber Group StelLive.    
Developed by **doras**

# Project Setup

To run this project locally, put an `application-oauth.properties` file which includes Naver OAuth2 properties in the `~/.stellight/` folder (outside the repository).

- `./gradlew bootRun` loads it automatically and fails with a clear message if it is missing.
- When running `Application` directly from an IDE, add the VM option `-Dspring.config.additional-location=file:<your home>/.stellight/`.
- Do not put real credentials in `src/main/resources` or `src/test/resources`.

Tests don't need real credentials; they use the dummy `src/test/resources/application-oauth.properties`.
