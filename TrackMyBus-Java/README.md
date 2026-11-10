# TrackMyBus - Jyothi College Bus Tracker (Java OOP project)

A college bus tracking website. The backend is plain Java 17+ (no Maven, no frameworks, no libraries).
The website (`web/index.html`) is served by the Java program and talks to it through a JSON API.

## Run (easiest)
1. Install Java 17 or newer (JRE or JDK).
2. Windows: double-click `start.bat`. Mac/Linux: run `./start.sh`. Or type `java -jar TrackMyBus.jar`.
3. Your browser opens at http://localhost:8080 (if that port is busy it uses the next one).

The jar already contains the website. Accounts are saved in a `data` folder beside it.

## Build from source (needs a JDK)
`./run.sh` or `run.bat`. No `javac` command? Run `java Compile.java`, then `java -cp out com.trackmybus.Main`.

Driver accounts need the driver code (default `JYOTHI-DRIVER`; change it with the `DRIVER_CODE` environment variable).

## OOP concepts used
| Concept | Where |
|---|---|
| Abstraction (abstract class) | `Vehicle`, `User` |
| Inheritance | `CollegeBus extends Vehicle`, `Student` and `Driver` extend `User` |
| Interface | `Trackable`, `UserRepository`, `ApiServer.Endpoint` |
| Polymorphism | `User.role()`, `extras()`, `toPublicMap()`, `Vehicle.speedKmh()`, the `driver()` check in `ApiServer` |
| Encapsulation | private fields with getters in `Stop`, `User`, `Vehicle` |
| Composition | `Route` holds `Stop`s, `Vehicle` has a `Route`, `BusService` holds all buses |
| Exceptions | custom `AuthException` carrying an HTTP status |
| Collections and generics | `List`, `Map`, `EnumMap`, `ConcurrentHashMap` |
| Enums and records | `Trip`, `Role`, `Position`, `LiveFix`, `BusStatus` |
| Lambdas | API routes in `ApiServer.routes()` |
| Packages | `model`, `user`, `repo`, `service`, `web` |
| File I/O | `FileUserRepository` saves `data/users.tsv` |
| Security | salted PBKDF2 password hashing in `AuthService` |

## Structure
```
src/com/trackmybus/
  Main.java                      wires objects, starts server
  model/   Stop Route Vehicle CollegeBus Trackable BusStatus LiveFix Position Trip Geo
  user/    User Student Driver Role
  repo/    UserRepository FileUserRepository
  service/ AuthService AuthException BusService DataSet
  web/     ApiServer Json
web/index.html                   the website
```

## API
GET `/api/data` (stops, routes), GET `/api/status?trip=evening` (Java-computed status), GET `/api/live` (driver GPS).
POST `/api/register`, `/api/login`, `/api/logout`, `/api/prefs`, `/api/driver/location`, `/api/driver/stop`.

## Timetable rules (in `CollegeBus`)
Morning buses reach college at 08:45. Evening buses leave college at 16:10 (even bus numbers) or 16:15 (odd).
