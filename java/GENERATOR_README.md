# Ontology to Java Generator

> **Generator som konverterar Turtle/OWL-ontologi till Java-klasser**

Denna modul innehåller `OntologyToJavaGenerator` som läser en RDF/OWL-ontologi i Turtle-format och genererar motsvarande Java-klasser.

##  Snabbstart

### Generera Java-klasser från en Turtle-fil

```bash
# Kopiera en Turtle-fil till java-katalogen (om den inte redan finns)
cp ../python/lovecraft_mythos.ttl java/

# Kör generatorn
cd java
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.OntologyToJavaGenerator" -Dexec.args="lovecraft_mythos.ttl"
```

### Använda genererade klasser

Efter generering skapas Java-klasser i paketet `org.lovecraft.ontology.generated`.

```java
import org.lovecraft.ontology.generated.*;

public class Example {
    public static void main(String[] args) {
        // Skapa en instans av en klass
        Entity entity = new Entity(OntologyFactory.CTHULHU_URI);
        System.out.println(entity.getInstanceUri());
        
        // Använda Jena-model
        Model model = OntologyFactory.createModel();
        Resource cthulhu = OntologyFactory.createJenaResource(OntologyFactory.CTHULHU_URI);
        
        // Lägg till till modellen
        model.add(cthulhu, RDF.type, model.createResource(Entity.URI));
    }
}
```

##  Genererade Klasser

Generatorn skapar följande klasser baserat på ontologin:

### Ontologiklasser

För varje OWL-klass i ontologin genereras en Java-klass med:
- `URI` - Konstant med klassens URI
- `instanceUri` - Instans-URI för specifika individer
- Konstruktorer (med och utan instans-URI)
- Getters för URI och instans-URI
- `toString()`, `equals()`, `hashCode()`

Exempel klasser:
- `Entity` - Basklass
- `Deity` - Gudomligheter
- `GreatOldOne` - Stora gamla väsen (Cthulhu, etc.)
- `OuterGod` - Yttre gudar (Nyarlathotep, Azathoth)
- `ElderGod` - Äldre gudar (Yig)
- `Creature` - Varelser
- `Human` - Människor
- `Location` - Platser
- `Artifact` - Artefakter
- `Book` - Böcker
- `Concept` - Begrepp
- `Resource` - RDL-resurser

### OntologyFactory

En fabriksklass som innehåller:
- `getModel()` - Hämtar Jena-modellen
- `createModel()` - Skapar en ny Jena-model
- `createJenaResource(String uri)` - Skapar en Jena-resurs
- URI-konstanter för alla individer (t.ex. `CTHULHU_URI`, `NECRONOMICON_URI`)

##  Användning

### Generera från kommandoraden

```bash
# Grundläggande användning (använder standard utdata-katalog)
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.OntologyToJavaGenerator" -Dexec.args="input.ttl"

# Med anpassad utdata-katalog
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.OntologyToJavaGenerator" -Dexec.args="input.ttl output/dir"

# Från projektroten
mvn -f java/pom.xml exec:java -Dexec.mainClass="org.lovecraft.ontology.OntologyToJavaGenerator" -Dexec.args="python/lovecraft_mythos.ttl java/src/main/java"
```

### Programmatisk användning

```java
import org.lovecraft.ontology.OntologyToJavaGenerator;

public class GenerateClasses {
    public static void main(String[] args) {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        
        // Generera från en Turtle-fil
        generator.generateFromTurtle("path/to/ontology.ttl", "output/directory");
    }
}
```

##  Exempel på Genererad Kod

### Entity.java

```java
package org.lovecraft.ontology.generated;

import java.util.*;
import org.apache.jena.rdf.model.*;

/**
 * Entity
 * Label: Entity
 * Description: Base class for all Lovecraftian entities
 * 
 * Auto-generated from OWL ontology
 */
public class Entity {

    public static final String URI = "https://lovecraft.frosteby.eu/ontology#Entity";

    private String instanceUri;

    public Entity(String instanceUri) {
        this.instanceUri = instanceUri;
    }

    public Entity() {
        this.instanceUri = URI + "Instance";
    }

    public String getInstanceUri() {
        return instanceUri;
    }

    public String getUri() {
        return URI;
    }

    @Override
    public String toString() {
        return "Entity{instanceUri='" + instanceUri + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity that = (Entity) o;
        return Objects.equals(instanceUri, that.instanceUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceUri);
    }
}
```

### OntologyFactory.java (utdrag)

```java
package org.lovecraft.ontology.generated;

import java.util.*;
import org.apache.jena.rdf.model.*;

public class OntologyFactory {

    private static Model model = ModelFactory.createDefaultModel();

    public static Model getModel() {
        return model;
    }

    // URI-konstanter för alla individer
    public static final String CTHULHU_URI = "https://lovecraft.frosteby.eu/instance#Cthulhu";
    public static final String NECRONOMICON_URI = "https://lovecraft.frosteby.eu/instance#Necronomicon";
    public static final String NYARLATHOTEP_URI = "https://lovecraft.frosteby.eu/instance#Nyarlathotep";
    // ... fler konstanter

    public static org.apache.jena.rdf.model.Resource createJenaResource(String uri) {
        return model.createResource(uri);
    }

    public static Model createModel() {
        return ModelFactory.createDefaultModel();
    }
}
```

##  Tekniska Detaljer

### Namnkonventioner

- Klassnamn: Extraheras från URI:en (sista delen efter `#` eller `/`)
  - `https://lovecraft.frosteby.eu/ontology#Entity` → `Entity`
  - `https://lovecraft.frosteby.eu/ontology#GreatOldOne` → `GreatOldOne`
  - Specialtecken ersätts: `-` → `_`
  - Första bokstaven görs stor

### Paketstruktur

- All genererad kod placeras i: `org.lovecraft.ontology.generated`
- Utdata-katalog kan anpassas via kommandoradsargument

### Filtrering

Generatorn filtrerar bort:
- System-klasser från `http://www.w3.org/` (OWL, RDF, RDFS, XSD)
- FOAF-klasser från `http://xmlns.com/foaf/`
- Dublin Core-klasser från `http://purl.org/dc/`

##  Versionering

De genererade klasserna markeras som "Auto-generated from OWL ontology" och bör inte redigeras manuellt. Om ontologin ändras, ska generatorn köras på nytt.

##  Integration med Maven

### Lägg till i build-processen

För att automatisera generering vid byggnad, kan du lägga till exec-maven-plugin i pom.xml:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>exec-maven-plugin</artifactId>
    <version>3.6.3</version>
    <executions>
        <execution>
            <id>generate-ontology-classes</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>java</goal>
            </goals>
            <configuration>
                <mainClass>org.lovecraft.ontology.OntologyToJavaGenerator</mainClass>
                <arguments>
                    <argument>src/main/resources/lovecraft_mythos.ttl</argument>
                    <argument>src/main/java</argument>
                </arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

##  Licens

Se huvud-projektets [LICENSE](../../LICENSE) fil.
