# Lovecraft Mythos Ontology

> **Referensimplementation av RDL och OWL i Turtle- och JSON-LD-format med Lovecraft-tema**

[![Java CI](https://github.com/thommyberglund/rdl-turtle/actions/workflows/java-ci.yml/badge.svg)](https://github.com/thommyberglund/rdl-turtle/actions/workflows/java-ci.yml)
[![Python CI](https://github.com/thommyberglund/rdl-turtle/actions/workflows/python-ci.yml/badge.svg)](https://github.com/thommyberglund/rdl-turtle/actions/workflows/python-ci.yml)

Detta repository innehåller implementeringar i **Python** och **Java** som genererar en **OWL-ontologi** (Web Ontology Language) med **RDL** (Resource Description Language) i **Turtle-format** och **JSON-LD-format**. Ontologin beskriver entiteter, relationer och koncept från **H.P. Lovecrafts Cthulhu-mytologi**.

---

## Struktur

```
rdl-turtle/
├── README.md                    # Denna fil
├── python/                      # Python-implementation
│   ├── README.md                # Python-specifik dokumentation
│   ├── lovecraft_ontology.py    # Huvudimplementering (Turtle)
│   ├── lovecraft_ontology_jsonld.py # JSON-LD-implementering
│   ├── consume_jsonld.py        # JSON-LD-konsumtion
│   ├── test_lovecraft_ontology.py # Enhetstester (Turtle)
│   ├── test_lovecraft_ontology_jsonld.py # Enhetstester (JSON-LD)
│   ├── test_consume_jsonld.py   # Enhetstester (konsumtion)
│   ├── requirements.txt         # Beroenden
│   └── *.ttl / *.jsonld         # Genererade Turtle- och JSON-LD-filer
└── java/                       # Java-implementation
    ├── README.md                # Java-specifik dokumentation
    ├── pom.xml                  # Maven-konfiguration
    └── src/                     # Java-källkod och tester
```

---

## Syfte

Projektet syftar till att:
- Demonstrera hur man skapar **semantiska ontologier** med **OWL** och **RDL**
- Använda **Turtle-syntax** och **JSON-LD-format** för RDF-data
- Visa skillnader och likheter mellan **Python** (med `rdflib`) och **Java** (med Apache Jena)
- Ge en praktisk referens för arbete med **Linked Data** och **Semantic Web**
- Visa hur man kan serialisera och konsumera ontologier i båda formaten (Turtle ↔ JSON-LD)

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
| **Turtle** | Terse RDF Triple Language | Serialiseringsformat (textbaserat) |
| **JSON-LD** | JSON for Linked Data | Serialiseringsformat (JSON-baserat) |
| **Python + rdflib** | RDF-bibliotek för Python | Python-implementation (Turtle & JSON-LD) |
| **Java + Apache Jena** | RDF-bibliotek för Java | Java-implementation (Turtle & JSON-LD) |
| **JUnit 5** | Testramverk för Java | Java-tester |
| **unittest** | Testramverk för Python | Python-tester |

---

## Dokumentation

- [Python-implementation](./python/README.md) - Detaljer om Python-koden (Turtle & JSON-LD)
- [Java-implementation](./java/README.md) - Detaljer om Java-koden (Turtle & JSON-LD)
- [Ontology to Java Generator](./java/GENERATOR_README.md) - Dokumentation för generatorn som skapar Java-klasser från Turtle-filer

---

## Snabbstart

### För båda implementationerna

1. **Klona repositoryt**
   ```bash
   git clone https://github.com/thommyberglund/rdl-turtle.git
   cd rdl-turtle
   ```

2. **Välj implementation**
   - [Python](./python/README.md) - Enklare att komma igång (Turtle & JSON-LD)
   - [Java](./java/README.md) - Mer enterprise-inriktad (Turtle & JSON-LD)

### Omvänd generator (Turtle → Java)

Java-implementationen inkluderar också en **OntologyToJavaGenerator** som läser en Turtle-fil och genererar Java-klasser:

```bash
cd java
mvn exec:java -Dexec.mainClass="org.lovecraft.ontology.OntologyToJavaGenerator" -Dexec.args="lovecraft_mythos.ttl"
```

Se [Java-dokumentationen](./java/README.md#omvänd-generator-turtle--java) för mer information.

### JSON-LD-generering

Båda implementationerna stödjer generering av ontologin i **JSON-LD-format**:

**Python:**
```bash
cd python
python lovecraft_ontology_jsonld.py
```

**Java:**
```bash
cd java
mvn exec:java -Dexec.mainClass="eu.frosteby.ontology.LovecraftOntologyJsonLD"
```

Detta genererar JSON-LD-filer (`lovecraft_mythos.jsonld` och `lovecraft_mythos_simple.jsonld`).

---

## Exempel på utdata

### Turtle-format

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

### JSON-LD-format

```json
{
  "@context": {
    "lovecraft": "https://lovecraft.frosteby.eu/ontology#",
    "instance": "https://lovecraft.frosteby.eu/instance#",
    "owl": "http://www.w3.org/2002/07/owl#",
    "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    "rdfs": "http://www.w3.org/2000/01/rdf-schema#"
  },
  "@graph": [
    {
      "@id": "instance:Cthulhu",
      "@type": ["lovecraft:GreatOldOne", "owl:NamedIndividual"],
      "lovecraft:hasName": {"@value": "Cthulhu", "@language": "en"},
      "lovecraft:hasDescription": {"@value": "The high priest of the Great Old Ones, sleeping in R'lyeh", "@language": "en"},
      "lovecraft:controls": [{"@id": "instance:Dreams"}, {"@id": "instance:Madness"}],
      "lovecraft:fears": [{"@id": "instance:TheSun"}, {"@id": "instance:Light"}]
    },
    {
      "@id": "instance:Necronomicon",
      "@type": ["lovecraft:Book", "owl:NamedIndividual"],
      "lovecraft:hasName": {"@value": "Necronomicon", "@language": "en"},
      "lovecraft:createdBy": {"@id": "instance:AbdulAlhazred"}
    }
  ]
}
```

Båda formaten representerar samma ontologi med samma klasser, egenskaper och individer. JSON-LD använder en JSON-baserad struktur med `@context` för namnrymdshantering och `@graph` för att definiera entiteterna.

---

## JSON-LD Implementering

### Översikt

Projektet inkluderar fullständig support för **JSON-LD** (JSON for Linked Data) i båda implementationerna. JSON-LD är ett JSON-baserat format för att representera Linked Data, vilket gör det särskilt lämpat för webb-API:er och JavaScript-applikationer.

### Implementeringsdetaljer

#### Python
- **`lovecraft_ontology_jsonld.py`** - Genererar ontologin direkt som JSON-LD-dokument
- **`consume_jsonld.py`** - Läser JSON-LD-filer och omvandlar till RDF-grafer
- Fullständig roundtrip-support: JSON-LD ↔ RDF ↔ Turtle

#### Java
- **`LovecraftOntologyJsonLD.java`** - Genererar ontologin i JSON-LD-format med Apache Jena
- **`ConsumeJsonLD.java`** - Läser och omvandlar JSON-LD-filer till RDF-modeller
- Stöd för konvertering mellan Turtle och JSON-LD

### Funktioner

- ✅ Generering av fullständiga JSON-LD-ontologier
- ✅ Konsumtion och omvandling av JSON-LD till RDF
- ✅ Roundtrip-testning (JSON-LD → RDF → Turtle → JSON-LD)
- ✅ Enhetstester för alla JSON-LD-funktioner
- ✅ Kontext-hantering för namnrymder
- ✅ Stöd för alla ontologi-entiteter (klasser, egenskaper, individer)

### När ska man använda JSON-LD?

| Användningsfall | Rekommenderat format |
|----------------|---------------------|
| RDF/Linked Data-verktyg | Turtle |
| SPARQL-frågor | Turtle |
| Webb-API:er | JSON-LD |
| JavaScript-applikationer | JSON-LD |
| Lättviktiga integreringar | JSON-LD |
| Fullständig RDF/OWL-support | Turtle |

---

## Continuous Integration

Projektet använder **GitHub Actions** för automatisk byggnad och testning av både Java- och Python-implementationerna.

### CI-Workflows

- **[Java CI](.github/workflows/java-ci.yml)**: Bygg och test Java-implementationen
  - Trigger: Push/pull requests till `java/` katalogen
  - Miljö: Ubuntu latest, Java 11, Maven
  - Kör: Kompilering, enhetstester, paketering

- **[Python CI](.github/workflows/python-ci.yml)**: Test Python-implementationen
  - Trigger: Push/pull requests till `python/` katalogen
  - Miljö: Ubuntu latest, Python 3.11
  - Kör: Installera beroenden, enhetstester, filgenerering

### Lokalt test

För att köra alla tester lokalt:
```bash
# Java-tester
cd java && mvn clean test

# Python-tester
cd python && python -m unittest discover -v -s . -p 'test_*.py'
```

---

## Turtle vs JSON-LD

### Jämförelse

| Aspekt | Turtle | JSON-LD |
|--------|--------|---------|
| **Format** | Textbaserat, trippel-baserat | JSON-baserat, dokument-baserat |
| **Läsbarhet (mänsklig)** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ (med formatering) |
| **Läsbarhet (maskin)** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Struktur** | Prefix + tripplar | @context + @graph med noder |
| **Storlek** | Kompakt | Något större (JSON-overhead) |
| **Användning** | RDF/Linked Data-standard, SPARQL | Webb-API:er, JavaScript |
| **Beroenden** | Kräver RDF-bibliotek | Inga externa beroenden (endast JSON-parser) |
| **Integration** | RDF-verktyg | Webb-applikationer |

### Komplementaritet

De två formaten är **fullständigt kompatibla** och representerar samma data. Projektet visar hur man kan:
1. Generera ontologin i båda formaten
2. Konvertera mellan formaten (Turtle ↔ JSON-LD)
3. Använda det format som bäst passar ditt användningsfall

Båda implementationerna (Python och Java) stödjer fullständig roundtrip-konvertering mellan formaten.

---

## Licens

Detta projekt är öppen källkod och tillgängligt under [MIT-licensen](LICENSE).

---

## Resurser och Länkar

### Specifikationer
- [RDF 1.1 Specification](https://www.w3.org/TR/rdf11-primer/)
- [OWL 2 Web Ontology Language](https://www.w3.org/TR/owl2-overview/)
- [JSON-LD 1.1 Specification](https://www.w3.org/TR/json-ld11/)
- [Turtle - Terse RDF Triple Language](https://www.w3.org/TR/turtle/)

### Verktyg och Bibliotek
- [rdflib (Python)](https://rdflib.readthedocs.io/) - RDF-bibliotek för Python
- [Apache Jena (Java)](https://jena.apache.org/) - RDF-bibliotek för Java
- [JSON-LD Playground](https://json-ld.org/playground/) - Testa och validera JSON-LD

---

## Bidrag

Bidrag är välkomna! Öppna gärna **issues** eller **pull requests** för:
- Förbättringar av ontologin
- Ny funktionalitet
- Buggfixar
- Dokumentationsförbättringar
- Förbättringar av JSON-LD-implementeringen

---

## Kontakt

För frågor om projektet, kontakta repository-ägaren.
