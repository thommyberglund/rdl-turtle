# Lovecraft Mythos Ontology

> **Referensimplementation av RDL och OWL i Turtle-format med Lovecraft-tema**

Detta repository innehåller implementeringar i **Python** och **Java** som genererar en **OWL-ontologi** (Web Ontology Language) med **RDL** (Resource Description Language) i **Turtle-format**. Ontologin beskriver entiteter, relationer och koncept från **H.P. Lovecrafts Cthulhu-mytologi**.

---

## Struktur

```
rdl-turtle/
├── README.md                    # Denna fil
├── python/                      # Python-implementation
│   ├── README.md                # Python-specifik dokumentation
│   ├── lovecraft_ontology.py    # Huvudimplementering
│   ├── test_lovecraft_ontology.py # Enhetstester
│   ├── requirements.txt         # Beroenden
│   └── *.ttl                   # Genererade Turtle-filer
└── java/                       # Java-implementation
    ├── README.md                # Java-specifik dokumentation
    ├── pom.xml                  # Maven-konfiguration
    └── src/                     # Java-källkod och tester
```

---

## Syfte

Projektet syftar till att:
- Demonstrera hur man skapar **semantiska ontologier** med **OWL** och **RDL**
- Använda **Turtle-syntax** för RDF-data
- Visa skillnader och likheter mellan **Python** (med `rdflib`) och **Java** (med Apache Jena)
- Ge en praktisk referens för arbete med **Linked Data** och **Semantic Web**

---

## Ontologins Innehåll

Ontologin inkluderar:

### **Klasser (OWL Classes)**
- `Entity` - Basklass för alla entiteter
- `Deity` - Kosmiska gudomligheter
  - `GreatOldOne` - Mektiga urgamla väsen (t.ex. Cthulhu, Dagon)
  - `OuterGod` - Väsener från utanför universum (t.ex. Azathoth, Nyarlathotep)
  - `ElderGod` - Neutrala eller välvilliga väsen (t.ex. Yig)
- `Creature` - Icke-gudomliga väsen (t.ex. Deep One, Shoggoth)
- `Human` - Människor (t.ex. H.P. Lovecraft, Abdul Alhazred)
- `Location` - Platser (t.ex. R'lyeh, Innsmouth, Arkham)
- `Artifact` - Magiska föremål
- `Book` - Förbjudna böcker (t.ex. Necronomicon)
- `Concept` - Abstrakt begrepp (t.ex. Madness, Dreams)
- `Resource` - RDL-resurser

### **Egenskaper (OWL Properties)**
- **Objektegenskaper**: `worships`, `fears`, `controls`, `locatedIn`, `isPartOf`, `createdBy`, `describedIn`
- **Datatypegenskaper**: `hasName`, `hasDescription`, `hasOrigin`, `hasAge`
- **RDL-egenskaper**: `hasResourceType`, `hasResourceValue`

### **Individer (OWL Individuals)**
- **Gudomligheter**: Cthulhu, Nyarlathotep, Azathoth, Yog-Sothoth, Dagon, Hastur, Shub-Niggurath, Yig
- **Varelser**: Deep One, Nightgaunt, Shoggoth, Ghoul
- **Platser**: R'lyeh, Innsmouth, Arkham, Miskatonic University, Dreamlands, Plateau of Leng
- **Artefakter**: Necronomicon, Elder Sign, Silver Key, Book of Eibon
- **Människor**: H.P. Lovecraft, Abdul Alhazred, Randolph Carter

---

## Teknologier

| Teknologi | Beskrivning | Användning |
|------------|--------------|-------------|
| **RDF** | Resource Description Framework | Standard för semantisk data |
| **OWL** | Web Ontology Language | Ontologi-beskrivning |
| **RDL** | Resource Description Language | Resursbeskrivning |
| **Turtle** | Terse RDF Triple Language | Serialiseringsformat |
| **Python + rdflib** | RDF-bibliotek för Python | Python-implementation |
| **Java + Apache Jena** | RDF-bibliotek för Java | Java-implementation |
| **JUnit 5** | Testramverk för Java | Java-tester |
| **unittest** | Testramverk för Python | Python-tester |

---

## Dokumentation

- [Python-implementation](./python/README.md) - Detaljer om Python-koden
- [Java-implementation](./java/README.md) - Detaljer om Java-koden

---

## Snabbstart

### För båda implementationerna

1. **Klona repositoryt**
   ```bash
   git clone https://github.com/thommyberglund/rdl-turtle.git
   cd rdl-turtle
   ```

2. **Välj implementation**
   - [Python](./python/README.md) - Enklare att komma igång
   - [Java](./java/README.md) - Mer enterprise-inriktad

### Omvänd generator (Turtle → Java)

Java-implementationen inkluderar också en **OntologyToJavaGenerator** som läser en Turtle-fil och genererar Java-klasser:

```bash
cd java
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.OntologyToJavaGenerator" -Dexec.args="lovecraft_mythos.ttl"
```

Se [Java-dokumentationen](./java/README.md#omvänd-generator-turtle--java) för mer information.

---

## Exempel på Turtle-utdata

```turtle
@prefix lovecraft: <https://lovecraft.frosteby.eu/ontology#> .
@prefix instance: <https://lovecraft.frosteby.eu/instance#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

instance:Cthulhu a lovecraft:GreatOldOne, owl:NamedIndividual ;
    lovecraft:hasName "Cthulhu"@en ;
    lovecraft:hasDescription "The high priest of the Great Old Ones, sleeping in R'lyeh"@en ;
    lovecraft:controls instance:Dreams, instance:Madness ;
    lovecraft:fears instance:TheSun, instance:Light .

instance:Necronomicon a lovecraft:Book, owl:NamedIndividual ;
    lovecraft:hasName "Necronomicon"@en ;
    lovecraft:createdBy instance:AbdulAlhazred .
```

---

## Licens

Detta projekt är öppen källkod och tillgängligt under [MIT-licensen](LICENSE).

---

## Bidrag

Bidrag är välkomna! Öppna gärna **issues** eller **pull requests** för:
- Förbättringar av ontologin
- Ny funktionalitet
- Buggfixar
- Dokumentationsförbättringar

---

## Kontakt

För frågor om projektet, kontakta repository-ägaren.
