# ![ProtocolVanish Logo](https://www.spigotmc.org/attachments/protocolvanish-fulltitle-png.440827/) ![Spiget Version](https://img.shields.io/spiget/version/69445.svg?label=spigot&style=flat-square) ![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.azortis/protocolvanish.svg?style=flat-square)

**Hide yourself from players completely, even hacked clients won't know you're there.**

**This is because we intercept and modify packets so that the player client will have no single data about a vanished player unless it has the permission to do so!**

## Maven
Add the following to your pom.xml

#### Repository

```xml
<repositories>
    <repository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/content/groups/public/</url>
    </repository>
</repositories>
```

#### Dependency

```xml
<dependencies>
    <dependency>
        <groupId>com.azortis</groupId>
        <artifactId>protocolvanish</artifactId>
        <version>@VERSION</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```
