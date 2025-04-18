# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

## Server Design Sequence Diagram URL:
You can find the sequence diagram for the Chess Server Design assignment [here](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9K+KDvvorxLAC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm65Set6Qa0uWo4mkS4YWhyMDRlRQbxnKWHJpBJbITy2a5pg-4gtBJRXAMxGjAuk59NOs7NuOrZ9N+V5cYU2Q9v2g69FMQHjBJNZBqJ84TpMS6cKu3h+IEXgoOge4Hr4zDHukmSYHJF5FNQ17SAAoruzn1M5zQtA+qhPt0ImNug7a-mcQIlgFc4cUxIWYXB5m+kh8VgKhGIYfRGo4SyZIUrG-oRegpFMm6FHlNyvK5YKwowPlaChmRxVJpeMHlBV2iOs6BKZaSRgoNwmS5bCrXyIVZoRoUlrpsMEA0DRcZtZhnaceUyEWWxCB5tFnY-lcUmOTJXY5GAfYDkOukrp4BkbpCtq7tCMAAOKjqyVmnrZ57MNx173e5Xn2KO-kaYFtU-myS3VYDkWcWysUwMgsRIdCKXoe1iadfVWUwOSYADTVI3kZGlFlTGtHaEKYQ1SjWFo0VGNw2Aj2jKosJ4w142EzytrAMqD2jg6rrSo1DlOvKe7QgAPAzKCMvklOLdFy2I2o7FQ4L22llMf2M6pFT9JrKAAJLSKp1ZTCemQGhW2lPDoCCgA2FujsbQl6wAcqO2l7DAjS7ccgt2UdMAKUOGtPdruujobTtfGb+r8fcalTDbdsOwJVsh6MbujB7Xtnfp66BNgPhQNg3DwLqmQ86MKTWWeh3Q9J5Q3g0v3-cEEPoEOrujj7HaMWFaY1Z3o6ZygEH9-XzUwPhmSwnA5coEjWKy9h6PdVjOPt2gLMC2zpUc7Nwak1VFMLSvNPddPKCS7CXejNv5oEy1NqY7z83pYLYNz16mRrRt4+qw3dWfQ9aR20jAHu+1-bHUUj0dOBsjZgNzhdfOARLC9XgskGAAApCAPJK6GACEnEADY3p10+mmaolI7wtD1gDOsQMhwl2AGgqAcAIDwSgLMEB0gIGg3luDehc5GG2xYWwjhXCI48Kiv-JqwtYIwAAFa4LQLCHBrEUBolSsvamo1srY0IrjOq58xrsj3uVEm8gyYCJnEDIxo0tr0RahY4A2iZBdXdJjCk19uH3xMRNe6FJ8GVTCJLQ2dj8bcXBEEt+MEMqrw8X4LQmRvGv3kO+ERlAxHQFmEBXxJU+TYCSYYSWdFYkf34WotAv9pGpgcXtHawVOxQMDidXoSC1yGQCF4ZhXYvSwGANgEuhB4iJGrq9f2E8eKVBcm5DyXljCNL7qmcoNSoKyKiSAbgeBmauP5hfLZcI8mP0mtNQwChuYGj5u4-JkQpozXOQgA+Vz4k3I2Kcqi3MSkxLkXLfu5REB9OqSrchDSQZ+3etA0sucgA).

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
