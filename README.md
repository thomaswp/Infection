Infection
=========

Models a set of users who are using a product, and simulates the introduction to of new features such that users who are connected get the same version of the product.

Created by Thomas Price (twprice@ncsu.edu).

## Setup
This project can be imported directly into eclipse, or run from the command line.

### Running
To compile the project and run the included tests, make sure `java` and `javac` are on the PATH and run the following commands from the root director (where this README is).

On Windows:

    mkdir bin
    javac -cp junit-4.12.jar -d bin/ src/org/khanacademy/infection/*.java src/org/khanacademy/infection/tests/*.java
    java -cp junit-4.12.jar;hamcrest-core-1.3.jar;bin org.junit.runner.JUnitCore org.khanacademy.infection.tests.Tests

On Unix (note colons over semicolons):

    mkdir bin
    javac -cp junit-4.12.jar -d bin/ src/org/khanacademy/infection/*.java src/org/khanacademy/infection/tests/*.java
    java -cp junit-4.12.jar:hamcrest-core-1.3.jar:bin org.junit.runner.JUnitCore org.khanacademy.infection.tests.Tests

To understand each test, look at the [Tests.java](src/org/khanacademy/infection/tests/Tests.java) file, which includes documentation.
	
## Key Methods

Rather than explaining each component in this README, you will have better luck looking at individual files in the project, which are each documented. However, the most important functions are:

* [`User`](src/org/khanacademy/infection/User.java).`addCoach(User coach, User pupil)`: Adds a coaching relationship between the two given users.
* [`User`](src/org/khanacademy/infection/User.java).`infect(String condition)`: Infects this user and all connected users with the given condition.
* [`Population`](src/org/khanacademy/infection/Population.java).`limitedInfection(String condition, int n, int threshold)`: Infects approximately n users with the given condition. The algorithm will avoid breaking up any infection groups if it is possible to do so while still infecting at least `(n - threshold)` users. If this is not possible, the algorithm will infect as many whole infection groups as possible, and then infect only a portion of one additional infection group, such that n total users are infected. The only circumstance under which fewer than `(n - threshold)` users will be infected is if the total users is fewer than that quantity.
* [`Population`](src/org/khanacademy/infection/Population.java).`limitedInfectionExact(String condition, int n, int threshold)`: If threshold if 0, infects exactly n Users with the given condition without breaking up any infection groups, or fails if this is not possible. If threshold is not 0, the exact number of infected users can be m, where `(n - threshold <= m <= n + threshold)` and `abs(m-n)` is minimized. Returns the actual number of users infected, or -1 for failure.
