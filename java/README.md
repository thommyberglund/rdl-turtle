# Lovecraft Mythos Ontology - Java Implementation

> **Java-implementation av RDL/OWL-ontologi i Turtle-format**

Denna katalog innehåller en **Java-implementation** som genererar en **OWL-ontologi** med **RDL** (Resource Description Language) i **Turtle-format**, baserad på **H.P. Lovecrafts Cthulhu-mytologi**. Implementationen använder **Apache Jena** för att hantera RDF-data.

---

## Innehåll

```
java/
├── README.md                    # Denna fil
├── GENERATOR_README.md         # Dokumentation för OntologyToJavaGenerator
├── .gitignore                   # Git-ignorera regler
├── pom.xml                     # Maven-konfiguration
└── src/
    ├── main/java/org/lovecraft/ontology/
    │   ├── Main.java                      # Huvudklass för att starta programmet
    │   ├── LovecraftOntology.java         # Huvudimplementering (OWL → Turtle)
    │   └── OntologyToJavaGenerator.java    # Generator (Turtle → Java klasser)
    └── test/java/org/lovecraft/ontology/
        └── LovecraftOntologyTest.java # Enhetstester
```

---

## Beroenden

Projektet använder följande bibliotek:
- **[Apache Jena](https://jena.apache.org/)** - RDF-bibliotek för Java
- **[JUnit 5](https://junit.org/junit5/)** - Testramverk

---

## Installation

### 1. Förutsättningar

- **Java 11** eller senare
- **Maven 3.6** eller senare

### 2. Installera beroenden

```bash
# Navigera till java-katalogen
cd java

# Ladda ner och installera beroenden
mvn dependency:resolve
```

---

## Kör programmet

### Kompilerar och kör

```bash
# Kompilera och kör huvudklassen
mvn clean compile exec:java -Dexec.mainClass="org.lovecraft.ontology.Main"
```

### Bygga JAR-filen

```bash
# Bygga en exekverbar JAR
mvn clean package

# Kör JAR-filen
java -jar target/lovecraft-ontology-1.0.0.jar
```

### Kör med Maven

```bash
# Kör direkt med Maven
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.LovecraftOntology"
```

Detta kommer att generera två Turtle-filer:
- `java/lovecraft_mythos.ttl` - Fullständig ontologi
- `java/lovecraft_mythos_simple.ttl` - Kopia för läsbarhet

---

## Kör tester

### Kör alla tester

```bash
mvn test
```

### Kör specifika tester

```bash
# Kör en specifik testklass
mvn test -Dtest=LovecraftOntologyTest

# Kör en specifik testmetod
mvn test -Dtest=LovecraftOntologyTest#testCthulhuProperties
```

### Kör tester med verbose-utdata

```bash
mvn test -X
```

---

## Tester som inkluderas

| Testklass | Beskrivning |
|-----------|--------------|
| `LovecraftOntologyTest` | Tester för ontologins struktur och innehåll |

### Testtäckning

- Ontologiheader och metadata
- Klasshierarki (OWL Classes)
- Objekt- och Datatype Properties
- Individer (Cthulhu, Necronomicon, etc.)
- RDL-resurser
- Relationer mellan entiteter
- Serialisering till Turtle-format
- Platser och artefakter

---

## API-Referens

### `LovecraftOntology`

Huvudklassen som skapar ontologin.

#### Metoder

##### `main(String[] args)`

Huvudmetod som skapar ontologin och sparar den till Turtle-filer.

**Exempel:**
```bash
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.LovecraftOntology"
```

##### `createOntology(Model model)`

Skapar ontologin i en given RDF-modell.

**Parametrar:**
- `model` (`Model`) - Apache Jena RDF-modell

**Exempel:**
```java
Model model = ModelFactory.createDefaultModel();
LovecraftOntology.createOntology(model);
```

##### `saveModel(Model model, String filename)`

Sparar en RDF-modell till en Turtle-fil.

**Parametrar:**
- `model` (`Model`) - RDF-modellen att spara
- `filename` (`String`) - Filnamn för utdata

---

## Omvänd Generator: Turtle → Java

> **Nytt!** Generator som skapar Java-klasser från Turtle-filer

Projektet inkluderar nu `OntologyToJavaGenerator` som gör det omvända: läser en Turtle/OWL-ontologi och genererar Java-klasser.

### Snabbstart

```bash
# Generera Java-klasser från en Turtle-fil
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.OntologyToJavaGenerator" -Dexec.args="lovecraft_mythos.ttl"
```

### Vad genereras?

- **Java-klasser** för varje OWL-klass (Entity, Deity, GreatOldOne, etc.)
- **OntologyFactory** med URI-konstanter för alla individer
- Fullständig Javadoc-dokumentation
- `equals()`, `hashCode()`, `toString()` metoder

### Exempel på användning

```java
import org.lovecraft.ontology.generated.*;

// Skapa instanser
Entity entity = new Entity(OntologyFactory.CTHULHU_URI);

// Använda med Jena
Model model = OntologyFactory.createModel();
Resource cthulhu = OntologyFactory.createJenaResource(OntologyFactory.CTHULHU_URI);
```

Se [GENERATOR_README.md](./GENERATOR_README.md) för full dokumentation.

---

## Ontologins Struktur

### Namnrymder

| Prefix | URI |
|--------|-----|
| `lovecraft:` | `https://lovecraft.frosteby.eu/ontology#` |
| `instance:` | `https://lovecraft.frosteby.eu/instance#` |
| `owl:` | `http://www.w3.org/2002/07/owl#` |
| `rdf:` | `http://www.w3.org/1999/02/22-rdf-syntax-ns#` |
| `rdfs:` | `http://www.w3.org/2000/01/rdf-schema#` |
| `dcterms:` | `http://purl.org/dc/terms/` |
| `xsd:` | `http://www.w3.org/2001/XMLSchema#` |

### Huvudklasser

```
Entity
├── Deity
│   ├── GreatOldOne (Cthulhu, Dagon, Hastur, Shub-Niggurath)
│   ├── OuterGod (Nyarlathotep, Azathoth, Yog-Sothoth)
│   └── ElderGod (Yig)
├── Creature (DeepOne, Nightgaunt, Shoggoth, Ghoul)
├── Human (H.P. Lovecraft, Abdul Alhazred, Randolph Carter)
├── Location (R'lyeh, Innsmouth, Arkham, ...)
├── Artifact (ElderSign, SilverKey)
├── Book (Necronomicon, BookOfEibon)
└── Concept (Dreams, Madness, Light, ...)
```

### Egenskaper

| Egenskap | Typ | Domän | Range | Beskrivning |
|----------|-----|-------|-------|--------------|
| `worships` | ObjectProperty | Entity | Deity | Vem som dyrkar vem |
| `fears` | ObjectProperty | Entity | Entity | Vad någon fruktar |
| `controls` | ObjectProperty | Deity | Entity | Vad en gudom kontrollera |
| `locatedIn` | ObjectProperty | Entity | Location | Var något befinner sig |
| `createdBy` | ObjectProperty | Artifact | Entity | Vem som skapade något |
| `describedIn` | ObjectProperty | Entity | Book | Vad som beskrivs i en bok |
| `hasName` | DatatypeProperty | Entity | string | Namn på en entitet |
| `hasDescription` | DatatypeProperty | Entity | string | Beskrivning av en entitet |

---

## Exempel: Använda ontologin

### Ladda och fråga ontologin

```java
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;

// Ladda ontologin
Model model = FileManager.get().loadModel("lovecraft_mythos.ttl");

// Skriv ut alla triples
model.write(System.out, "TURTLE");
```

### Lägga till nya entiteter

```java
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

// Skapa en ny modell
Model model = ModelFactory.createDefaultModel();

// Lägg till namnrymder
model.setNsPrefix("lovecraft", "https://lovecraft.frosteby.eu/ontology#");
model.setNsPrefix("instance", "https://lovecraft.frosteby.eu/instance#");

// Lägg till en ny klass
Resource newClass = model.createResource("https://lovecraft.frosteby.eu/ontology#NewClass");
newClass.addProperty(RDF.type, OWL.Class);
newClass.addProperty(RDFS.label, model.createLiteral("New Class", "en"));

// Lägg till en ny individ
Resource newIndividual = model.createResource("https://lovecraft.frosteby.eu/instance#NewIndividual");
newIndividual.addProperty(RDF.type, OWL.NamedIndividual);
newIndividual.addProperty(RDF.type, newClass);
```

### Fråga ontologin med SPARQL

```java
import org.apache.jena.query.*;

// Ladda ontologin
Model model = FileManager.get().loadModel("lovecraft_mythos.ttl");

// Skapa en SPARQL-fråga
String queryString = 
    "PREFIX lovecraft: <https://lovecraft.frosteby.eu/ontology#> " +
    "PREFIX instance: <https://lovecraft.frosteby.eu/instance#> " +
    "SELECT ?deity WHERE { ?deity a lovecraft:GreatOldOne . }";

// Kör frågan
Query query = QueryFactory.create(queryString);
QueryExecution qexec = QueryExecutionFactory.create(query, model);
ResultSet results = qexec.execSelect();

// Skriv ut resultaten
while (results.hasNext()) {
    QuerySolution soln = results.nextSolution();
    Resource deity = soln.getResource("deity");
    System.out.println("Great Old One: " + deity.getLocalName());
}

qexec.close();
```

---

## Uppdatera beroenden

Om du vill uppdatera beroendena:

```bash
# Uppdatera Jena-versionen i pom.xml
mvn versions:use-latest-versions

# Eller manuellt ändra versionen i pom.xml
<jena.version>4.10.0</jena.version>
```

---

## Maven Kommandon

| Kommando | Beskrivning |
|----------|--------------|
| `mvn clean` | Rensa byggmappen |
| `mvn compile` | Kompilera källkoden |
| `mvn test` | Kör enhetstester |
| `mvn package` | Bygg JAR-filen |
| `mvn install` | Installera i lokal Maven-repository |
| `mvn dependency:tree` | Visa beroendeträd |
| `mvn exec:java` | Kör huvudklassen |

---

## Licens

Detta projekt är öppen källkod och tillgängligt under [MIT-licensen](../LICENSE).

---

## Bidrag

Bidrag är välkomna! Förslag på förbättringar:
- Lägga till fler Lovecraft-entiteter
- Förbättra testtäckningen
- Optimeringsförslag
- Dokumentationsförbättringar
- SPARQL-frågeexempel

---

## Support

För frågor om Java-implementationen, se [huvud-dokumentationen](../README.md).

---

## Länkar

- [Apache Jena Dokumentation](https://jena.apache.org/documentation/)
- [JUnit 5 Dokumentation](https://junit.org/junit5/docs/current/user-guide/)
- [OWL 2 Specification](https://www.w3.org/TR/owl2-overview/)
- [RDF 1.1 Specification](https://www.w3.org/TR/rdf11-primer/)
- [Turtle Syntax](https://www.w3.org/TeamSubmission/turtle/)
