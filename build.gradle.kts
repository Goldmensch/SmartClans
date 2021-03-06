
plugins {
    java
    // only essential lombok features like, getter and setter
    id("io.freefair.lombok") version "6.0.0-m2"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "de.goldmensch"
version = "3.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://mvnrepository.com/artifact/org.slf4j/slf4j-api")
    maven("https://eldonexus.de/repository/maven-public")
}

dependencies {
    /*--Project--*/
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    implementation("me.lucko", "adventure-platform-bukkit", "4.7.0")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
    implementation("com.zaxxer", "HikariCP", "3.4.5")
    implementation("org.slf4j", "slf4j-jdk14", "1.7.25")
    implementation("de.eldoria", "eldo-util", "1.9.0")
    // database driver
    //implementation("mysql", "mysql-connector-java", "8.0.25")
    implementation("org.mariadb.jdbc", "mariadb-java-client", "2.7.2")

    /*--Test--*/
    testImplementation("junit:junit:4.13")
}

tasks {
    /**
     * Java build settings:
     * - encoding = UTF8
     */
    compileJava {
        options.encoding = "UTF-8"
    }

    /**
     * ShadowJar settings:
     * -
     * */
    shadowJar {
        relocate("org.bstats", "de.goldmensch.bstats")
        relocate("net.kyori", "de.goldmensch.adventure")

        relocate("org.slf4j", "de.goldmensch.slf4j")
        relocate("com.zaxxer.hikari", "de.goldmensch.hikari")
        //relocate("com.mysql", "de.goldmensch.drivers.mysql")
        //relocate("com.google.protobuf", "de.goldmensch.drivers.mysql")
        relocate("org.mariadb.jdbc", "de.goldmensch.drivers.mariadb")

        mergeServiceFiles()

        minimize {
            //exclude(dependency("mysql:mysql-connector-java:8.0.25"))
            exclude(dependency("org.mariadb.jdbc:mariadb-java-client:2.7.2"))
        }
    }

    /*--Tasks--*/
    /**
     * name: buildSpigot
     *
     * Downloads the spigot buildtools and
     * setups a spigot server on for 1.16.5,
     * makes 2 start scripts: windows(batch) and unix(shell);
     * both with a restart loop
     * spigotserverfolder: spigot-server
     */
    register<BuildSpigotTask>("buildSpigot") {
        version = "1.16.5"
    }

    /**
     * name: copyToPlugins
     *
     * copy the built jar to the
     * plugins folder of the spigot server
     */
    register<SmartClansUtils>("copyToPlugins")

    /**
     * name: buildAndCopy
     *
     * builds the project and copy the jar
     * to the plugins folder of the spigot server
     * (executes: buildProject and copyToPlugins)
     */
    register("buildAndCopy") {
        dependsOn("buildProject", "copyToPlugins")
    }

    /**
     * name: test
     *
     * executes the JUnit test with a
     * maximum vm heapSize of 1gb
     */
    test {
        useJUnit()
        maxHeapSize = "1G"
    }

    /**
     * name: buildProject
     *
     * builds the project
     * (executes: shadowJar)
     */
    register("buildProject") {
        dependsOn("clean", "shadowJar")
    }

    /**
     * name: processResource
     *
     * replaced placeholders in plugin.yml
     * placeholders: version, name, api-versionpl
     */
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "version" to project.version,
                    "name" to project.name,
                    "apiVersion" to "1.16"
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}



