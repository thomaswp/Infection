mkdir bin
javac -cp junit-4.12.jar -d bin/ src/org/khanacademy/infection/*.java src/org/khanacademy/infection/tests/*.java
java -cp junit-4.12.jar;hamcrest-core-1.3.jar;bin org.junit.runner.JUnitCore org.khanacademy.infection.tests.Tests
pause