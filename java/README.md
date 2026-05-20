# Lovecraft Mythos Ontology - Java Implementation

> **Java-implementation av RDL/OWL-ontologi i Turtle- och JSON-LD-format**

[![Java CI](https://github.com/thommyberglund/rdl-turtle/actions/workflows/java-ci.yml/badge.svg)](https://github.com/thommyberglund/rdl-turtle/actions/workflows/java-ci.yml)

Denna katalog innehåller en **Java-implementation** som genererar en **OWL-ontologi** med **RDL** (Resource Description Language) i **Turtle-format** och **JSON-LD-format**, baserad på **H.P. Lovecrafts Cthulhu-mytologi**. Implementationen använder **Apache Jena** för att hantera RDF-data.

---

## Innehåll

```
java/
├── README.md                    # Denna fil
├── GENERATOR_README.md         # Dokumentation för OntologyToJavaGenerator
├── .gitignore                   # Git-ignorera regler
├── pom.xml                     # Maven-konfiguration
└── src/
    ├── main/java/eu/frosteby/ontology/
    │   ├── Main.java                      # Huvudklass för att starta programmet
    │   ├── LovecraftOntology.java         # Huvudimplementering (OWL → Turtle)
    │   ├── LovecraftOntologyJsonLD.java    # JSON-LD-implementering (OWL → JSON-LD)
    │   ├── ConsumeJsonLD.java             # JSON-LD-konsumtion (JSON-LD → RDF)
    │   └── OntologyToJavaGenerator.java    # Generator (Turtle → Java klasser)
    └── test/java/eu/frosteby/ontology/
        ├── LovecraftOntologyTest.java       # Enhetstester (Turtle)
        ├── LovecraftOntologyJsonLDTest.java # Enhetstester (JSON-LD)
        └── ConsumeJsonLDTest.java           # Enhetstester (konsumtion)
```

> **Obs!** De genererade Turtle- och JSON-LD-filerna sparas i repository-roten:
> - `lovecraft_mythos.ttl`
> - `lovecraft_mythos_simple.ttl`
> - `lovecraft_mythos.jsonld`
> - `lovecraft_mythos_simple.jsonld`

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
mvn clean compile exec:java -Dexec.mainClass="eu.frosteby.ontology.Main"
```

### Bygga JAR-filen

```bash
# Bygga en exekverbar JAR
mvn clean package

# Kör JAR-filen
java -jar target/ontology-1.0.0.jar
```

### Kör med Maven

```bash
# Kör direkt med Maven (Turtle)
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.LovecraftOntology"
```

Detta kommer att generera två Turtle-filer:
- `lovecraft_mythos.ttl` - Fullständig ontologi
- `lovecraft_mythos_simple.ttl` - Kopia för läsbarhet

### Generera JSON-LD

```bash
# Kör JSON-LD-generering
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.LovecraftOntologyJsonLD"
```

Detta kommer att generera två JSON-LD-filer:
- `lovecraft_mythos.jsonld` - Fullständig ontologi
- `lovecraft_mythos_simple.jsonld` - Kopia för läsbarhet

### Konsumera JSON-LD

```bash
# Läs JSON-LD och omvandla till RDF
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.ConsumeJsonLD"
```

Detta kommer att:
- Ladda `lovecraft_mythos.jsonld`
- Omvandla den till en RDF-modell
- Spara som `consumed_ontology.ttl` för verifiering
- Visa exempel-frågor på modellen

---

## Kör tester

### Kör alla tester

```bash
mvn test
```

### Kör specifika tester

```bash
# Kör en specifik testklass (Turtle)
mvn test -Dtest=LovecraftOntologyTest

# Kör en specifik testklass (JSON-LD)
mvn test -Dtest=LovecraftOntologyJsonLDTest

# Kör en specifik testklass (konsumtion)
mvn test -Dtest=ConsumeJsonLDTest

# Kör en specifik testmetod
mvn test -Dtest=LovecraftOntologyTest#testCthulhuProperties
```

### Kör tester med verbose-utdata

```bash
mvn test -X
```

---

## Tester som inkluderas

### Turtle-tester

| Testklass | Beskrivning |
|-----------|--------------|
| `LovecraftOntologyTest` | Tester för ontologins struktur och innehåll |

### JSON-LD-tester

| Testklass | Beskrivning |
|-----------|--------------|
| `LovecraftOntologyJsonLDTest` | Tester för JSON-LD-generering |
| `ConsumeJsonLDTest` | Tester för JSON-LD-konsumtion |

### Testtäckning

- Ontologiheader och metadata
- Klasshierarki (OWL Classes)
- Objekt- och Datatype Properties
- Individer (Cthulhu, Necronomicon, etc.)
- RDL-resurser
- Relationer mellan entiteter
- Serialisering till Turtle-format
- Serialisering till JSON-LD-format
- JSON-LD konsumtion och omvandling
- Roundtrip-test (JSON-LD ↔ Turtle)
- Platser och artefakter

---

## API-Referens

### Turtle API

#### `LovecraftOntology`

Huvudklassen som skapar ontologin i Turtle-format.

##### Metoder

###### `main(String[] args)`

Huvudmetod som skapar ontologin och sparar den till Turtle-filer.

**Exempel:**
```bash
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.LovecraftOntology"
```

###### `createOntology(Model model)`

Skapar ontologin i en given RDF-modell.

**Parametrar:**
- `model` (`Model`) - Apache Jena RDF-modell

**Exempel:**
```java
Model model = ModelFactory.createDefaultModel();
LovecraftOntology.createOntology(model);
```

###### `saveModel(Model model, String filename)`

Sparar en RDF-modell till en Turtle-fil.

**Parametrar:**
- `model` (`Model`) - RDF-modellen att spara
- `filename` (`String`) - Filnamn för utdata

### JSON-LD API

#### `LovecraftOntologyJsonLD`

Huvudklassen som skapar ontologin i JSON-LD-format.

##### Metoder

###### `main(String[] args)`

Huvudmetod som skapar ontologin och sparar den till JSON-LD-filer.

**Exempel:**
```bash
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.LovecraftOntologyJsonLD"
```

###### `createOntology(Model model)`

Skapar ontologin i en given RDF-modell (samma struktur som Turtle-versionen).

**Parametrar:**
- `model` (`Model`) - Apache Jena RDF-modell

**Exempel:**
```java
Model model = ModelFactory.createDefaultModel();
LovecraftOntologyJsonLD.createOntology(model);
```

###### `saveModelAsJsonLD(Model model, String filename)`

Sparar en RDF-modell till en JSON-LD-fil.

**Parametrar:**
- `model` (`Model`) - RDF-modellen att spara
- `filename` (`String`) - Filnamn för utdata

**Exempel:**
```java
Model model = ModelFactory.createDefaultModel();
LovecraftOntologyJsonLD.createOntology(model);
LovecraftOntologyJsonLD.saveModelAsJsonLD(model, "output.jsonld");
```

#### `ConsumeJsonLD`

Klassen som läser och omvandlar JSON-LD till RDF-modell.

##### Metoder

###### `main(String[] args)`

Huvudmetod som läser JSON-LD och visar information.

**Exempel:**
```bash
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.ConsumeJsonLD"
```

###### `loadJsonLDFile(String filename)`

Laddar en JSON-LD-fil och returnerar en RDF-modell.

**Parametrar:**
- `filename` (`String`) - Filnamn för JSON-LD-filen

**Returvärde:** `Model` - RDF-modell, eller null om fel uppstår

**Exempel:**
```java
Model model = ConsumeJsonLD.loadJsonLDFile("lovecraft_mythos.jsonld");
```

###### `loadJsonLDFileWithPrefixes(String filename)`

Laddar en JSON-LD-fil med namnrymdsprefix.

**Parametrar:**
- `filename` (`String`) - Filnamn för JSON-LD-filen

**Returvärde:** `Model` - RDF-modell med prefix, eller null om fel uppstår

**Exempel:**
```java
Model model = ConsumeJsonLD.loadJsonLDFileWithPrefixes("lovecraft_mythos.jsonld");
```

###### `saveModelAsTurtle(Model model, String filename)`

Sparar en RDF-modell som Turtle-fil.

**Parametrar:**
- `model` (`Model`) - RDF-modellen att spara
- `filename` (`String`) - Filnamn för utdata

**Exempel:**
```java
Model model = ConsumeJsonLD.loadJsonLDFile("input.jsonld");
ConsumeJsonLD.saveModelAsTurtle(model, "output.ttl");
```

###### `saveModelAsJsonLD(Model model, String filename)`

Sparar en RDF-modell som JSON-LD-fil.

**Parametrar:**
- `model` (`Model`) - RDF-modellen att spara
- `filename` (`String`) - Filnamn för utdata

###### `convertJsonLDToTurtle(String inputJsonLDFile, String outputTurtleFile)`

Omvandlar en JSON-LD-fil till en Turtle-fil.

**Parametrar:**
- `inputJsonLDFile` (`String`) - Inmatningsfil (JSON-LD)
- `outputTurtleFile` (`String`) - Utmatningsfil (Turtle)

**Exempel:**
```java
ConsumeJsonLD.convertJsonLDToTurtle("input.jsonld", "output.ttl");
```

###### `convertTurtleToJsonLD(String inputTurtleFile, String outputJsonLDFile)`

Omvandlar en Turtle-fil till en JSON-LD-fil.

**Parametrar:**
- `inputTurtleFile` (`String`) - Inmatningsfil (Turtle)
- `outputJsonLDFile` (`String`) - Utmatningsfil (JSON-LD)

**Exempel:**
```java
ConsumeJsonLD.convertTurtleToJsonLD("input.ttl", "output.jsonld");
```

---

## Omvänd Generator: Turtle → Java

> **Nytt!** Generator som skapar Java-klasser från Turtle-filer

Projektet inkluderar nu `OntologyToJavaGenerator` som gör det omvända: läser en Turtle/OWL-ontologi och genererar Java-klasser.

### Snabbstart

```bash
# Generera Java-klasser från en Turtle-fil
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.OntologyToJavaGenerator" -Dexec.args="lovecraft_mythos.ttl"
```

### Vad genereras?

- **Java-klasser** för varje OWL-klass (Entity, Deity, GreatOldOne, etc.)
- **OntologyFactory** med URI-konstanter för alla individer
- Fullständig Javadoc-dokumentation
- `equals()`, `hashCode()`, `toString()` metoder

### Exempel på användning

```java
import eu.frosteby.ontology.generated.*;

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

## Användningsexempel

### Fullständig workflow: Generera → Konsumera → Analysera

```java
import eu.frosteby.ontology.*;
import org.apache.jena.rdf.model.*;

// 1. Generera JSON-LD-ontologin
Model model = ModelFactory.createDefaultModel();
LovecraftOntologyJsonLD.createOntology(model);
LovecraftOntologyJsonLD.saveModelAsJsonLD(model, "my_ontology.jsonld");

// 2. Konsumera JSON-LD och omvandla till RDF-modell
Model loadedModel = ConsumeJsonLD.loadJsonLDFileWithPrefixes("my_ontology.jsonld");

// 3. Fråga modellen
Resource cthulhu = loadedModel.createResource("https://lovecraft.frosteby.eu/instance#Cthulhu");
Property controls = loadedModel.createProperty("https://lovecraft.frosteby.eu/ontology#controls");

// Hitta vad Cthulhu kontrollera
StmtIterator iter = loadedModel.listStatements(cthulhu, controls, (RDFNode)null);
while (iter.hasNext()) {
    Statement stmt = iter.next();
    System.out.println("Cthulhu controls: " + stmt.getObject());
}

// 4. Spara som Turtle för vidare användning
ConsumeJsonLD.saveModelAsTurtle(loadedModel, "my_ontology.ttl");
```

### Exempel: Omvandling mellan format

```java
import eu.frosteby.ontology.*;

// Omvandla Turtle till JSON-LD
ConsumeJsonLD.convertTurtleToJsonLD("input.ttl", "output.jsonld");

// Omvandla JSON-LD till Turtle
ConsumeJsonLD.convertJsonLDToTurtle("input.jsonld", "output.ttl");
```

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

## JSON-LD vs Turtle

### Skillnader mellan formaten

| Aspekt | Turtle | JSON-LD |
|--------|--------|---------|
| **Format** | Textbaserat, trippel-baserat | JSON-baserat, dokument-baserat |
| **Läsbarhet** | Mänskligt läsbart | Maskinläsbart, mänskligt läsbart med formatering |
| **Struktur** | Prefix + tripplar | @context + noder med @id, @type |
| **Användning** | RDF/Linked Data-standard | Web API-er, JavaScript-applikationer |
| **Apache Jena** | Inbyggt stöd | Inbyggt stöd via RDFDataMgr |

### När ska man använda vilket format?

- **Använd Turtle** om du arbetar med RDF-verktyg, SPARQL-frågor, eller behöver fullständig RDF/OWL-support
- **Använd JSON-LD** om du integrerar med webb-API:er, JavaScript-applikationer, eller behöver ett lättviktigt JSON-format

Båda implementeringarna representerar samma ontologi med samma klasser, egenskaper och individer.

### Apache Jena och JSON-LD

Apache Jena har fullt stöd för JSON-LD via `RDFDataMgr`-klassen:

```java
// Läs JSON-LD
RDFDataMgr.read(model, inputStream, null, RDFFormat.JSONLD);

// Skriv JSON-LD
RDFDataMgr.write(outputStream, model, RDFFormat.JSONLD);
```

## Continuous Integration

Projektet använder **GitHub Actions** för automatisk byggnad och testning. CI-konfigurationen finns i [`.github/workflows/java-ci.yml`](../../.github/workflows/java-ci.yml).

### CI-Workflow

- **Trigger**: Push till `main`-branchen eller pull requests som påverkar `java/` katalogen
- **Miljö**: Ubuntu latest med Java 11
- **Steg**:
  1. Checkout repository
  2. Sätt upp JDK 11
  3. Cacha Maven-beroenden
  4. Bygg med Maven (`mvn clean compile`)
  5. Kör enhetstester (`mvn test`)
  6. Bygg JAR-paket (`mvn package -DskipTests`)
  7. Verifiera genererade filer

### Lokalt test

För att köra samma tester lokalt:
```bash
cd java
mvn clean test
```

## Länkar

- [Apache Jena Dokumentation](https://jena.apache.org/documentation/)
- [JUnit 5 Dokumentation](https://junit.org/junit5/docs/current/user-guide/)
- [OWL 2 Specification](https://www.w3.org/TR/owl2-overview/)
- [RDF 1.1 Specification](https://www.w3.org/TR/rdf11-primer/)
- [Turtle Syntax](https://www.w3.org/TeamSubmission/turtle/)
- [JSON-LD Specification](https://json-ld.org/spec/latest/json-ld/)
