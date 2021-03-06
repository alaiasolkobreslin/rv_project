### RUN THE DOCKER FROM HW1
`docker run -it hw1:latest`

### CD INTO javamop-agent-bundle TO MAKE THE AGENT
`cd /home/mopuser/javamop-agent-bundle`
`bash make-agent.sh props agents quiet`

### CD INTO MOPUSER
1. `cd /home/mopuser/`

### CLONE STARTS, CLIENT APP AND RPS PLUGIN
2. `git clone https://github.com/TestingResearchIllinois/starts.git`

3. `git clone https://github.com/bennysteepz/toy-app.git`

4. `git clone https://github.com/bennysteepz/rv_project.git`

### INSTALL STARTS AND RPS PLUGIN
5. `cd starts`, `mvn install`, `cd ..`

6. `cd rv_project`, `mvn install`, `cd ..`

### SKIP STEPS 7-10 IF USING THE CLONED toy-app ABOVE (we already configured its pom.xml)
### UPDATE CLIENT APP POM.XML 
7. `cd toy-app`

8. open pom.xml

9. add to <pluginManagement> section:

```
<!-- INSTALL PLUGIN WITH ARGUMENTS INCLUDED FOR MAVEN INVOKER -->
<plugin>
  <artifactId>maven-install-plugin</artifactId>
  <version>2.5.2</version>
  <configuration>
    <groupId>javamop-agent</groupId>
    <artifactId>javamop-agent</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>
    <file>../javamop-agent-bundle/agents/JavaMOPAgent.jar</file>
  </configuration>
  <executions>
    <execution>
      <id>install-jar</id>
      <goals>
        <goal>install-file</goal>
      </goals>
      <phase>generate-sources</phase>
    </execution>
  </executions>
</plugin>
```

10. add STARTS, Javamop and RPS plugin to <plugins> section:
```
<!-- INCLUDE: JAVAMOP PLUGIN -->
<plugin>
 <groupId>org.apache.maven.plugins</groupId>
 <artifactId>maven-surefire-plugin</artifactId>
 <version>2.22.1</version>
 <configuration>
   <argLine>-javaagent:${settings.localRepository}/javamop-agent/javamop-agent/1.0/javamop-agent-1.0.jar</argLine>
 </configuration>
</plugin>

<!-- INCLUDE: RPS PLUGIN -->
<plugin>
 <groupId>com.steeper.ben</groupId>
 <artifactId>myexample-maven-plugin</artifactId>
 <version>1.0-SNAPSHOT</version>
</plugin>

<!-- INCLUDE: STARTS PLUGIN FOR INVOKER TO USE-->
<plugin>
 <groupId>edu.illinois</groupId>
 <artifactId>starts-maven-plugin</artifactId>
 <version>1.3</version>
</plugin>
```

### RUN RPS PLUGIN COMMAND FROM CLIENT APP
11. `cd toy-app`

12. Command to run RPS plugin:

`mvn com.steeper.ben:myexample-maven-plugin:rps -DagentsPath="../javamop-agent-bundle/agents/"`

### CHANGE CLASSES OF YOUR CHOICE
### Classes App.java, App2.java, App3.java and App4.java contain variable "x" to change for testing purposes
### Sorts.java also contains a variable that can be changed for testing purposes
`cd toy-app/src/main/java/edu/cornell/cs6156/`

### RUN RPS PLUGIN COMMAND FROM toy-app AGAIN
`mvn com.steeper.ben:myexample-maven-plugin:rps -DagentsPath="../javamop-agent-bundle/agents/"`