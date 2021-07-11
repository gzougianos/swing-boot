# **Swing Boot annotation processor can validate if the placing of the annotations is correct.**

## **Eclipse IDE**

**Preview:**

![Alt text](https://github.com/gzougianos/swing-boot/blob/main/processor/eclipse_ide.png)

**How to make the annotation processor work in Eclipse IDE (version: 2020-12):**

Add dependency to `pom.xml`:

```
  <dependency>
    <groupId>io.github.swingboot</groupId>
    <artifactId>processor</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>provided</scope>
  </dependency>
```

Right click on the project and go to `Properties` -> `Java Compiler` -> `Annotation Processing` and tick:
- Enable project specific settings
- Enable annotation processing
- Enable processing in editor

Then go to `Properties` -> `Java Compiler` -> `Annotation Processing` -> `Factory Path` and tick:
- Enable project specific settings

Click `Apply` and `Apply & Close`. Then agree to build the project.

Finally, go to project directory, open `.factorypath` file and paste these lines:

```
<factorypath>
  <factorypathentry kind="VARJAR" id="M2_REPO/com/google/auto/service/auto-service/1.0-rc2/auto-service-1.0-rc2.jar" enabled="true" runInBatchMode="false"/>
  <factorypathentry kind="VARJAR" id="M2_REPO/com/google/auto/auto-common/0.3/auto-common-0.3.jar" enabled="true" runInBatchMode="false"/>
  <factorypathentry kind="VARJAR" id="M2_REPO/com/google/guava/guava/18.0/guava-18.0.jar" enabled="true" runInBatchMode="false"/>
  <factorypathentry kind="VARJAR" id="M2_REPO/io/github/swingboot/processor/0.0.1-SNAPSHOT/processor-0.0.1-SNAPSHOT.jar" enabled="true" runInBatchMode="false"/>
</factorypath>
```

Save the `.factorypath` file, (restart eclipse might be required after this step) and 
refrest the maven project (select the project and press <kbd>Alt</kbd><kbd>F5</kbd>).

If you are running maven commands from Eclipse with `m2e`, add the following plugin to your `pom.xml`:
```
  <build>
    <plugins>
      <plugin>
        <groupId>com.mysema.maven</groupId>
        <artifactId>apt-maven-plugin</artifactId>
        <version>1.1.3</version>
        <executions>
          <execution>
            <goals>
              <goal>process</goal>
            </goals>
            <configuration>
              <processor>io.github.swingboot.processor.BootProcessor</processor>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

## **Intellij IDEA**

_TODO..._

## **Netbeans IDE**

_TODO..._



