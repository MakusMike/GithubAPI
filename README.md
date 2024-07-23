# GitHub API

This Java application interacts with the GitHub API to list all non-fork repositories for a given user, 
along with each branch's name and last commit SHA. 
It also handles the case of a non-existent GitHub user by returning a 404 response in the specified format.

@Requirements
- Java 21
- Maven

@Setup
1. Clone the repository.
2. Add the required dependencies in 'pom.xml':
    
    <dependencies>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents.client5</groupId>
            <artifactId>httpclient5</artifactId>
            <version>5.2.1</version>
        </dependency>
    </dependencies>
    

@Usage
1. Run the GitAPI class
2. Menu will be shown in the console
3. Input 1, then input a github account username to which you want to list repositories.
4. Input 0 to exit the program.
