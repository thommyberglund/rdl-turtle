# Lovecraft Mythos Ontology - Python Implementation

> **Python-implementation av RDL/OWL-ontologi i Turtle- och JSON-LD-format**

[![Python CI](https://github.com/thommyberglund/rdl-turtle/actions/workflows/python-ci.yml/badge.svg)](https://github.com/thommyberglund/rdl-turtle/actions/workflows/python-ci.yml)

Denna katalog innehåller en **Python-implementation** som genererar en **OWL-ontologi** med **RDL** (Resource Description Language) i **Turtle-format** och **JSON-LD-format**, baserad på **H.P. Lovecrafts Cthulhu-mytologi**.

---

##  Innehåll

```
python/
├── README.md                        # Denna fil
├── lovecraft_ontology.py            # Huvudimplementering (Turtle)
├── lovecraft_ontology_jsonld.py     # JSON-LD-implementering (generering)
├── consume_jsonld.py                # JSON-LD-konsumtion (läsning)
├── test_lovecraft_ontology.py       # Enhetstester (Turtle)
├── test_lovecraft_ontology_jsonld.py # Enhetstester (JSON-LD)
├── test_consume_jsonld.py           # Enhetstester (konsumtion)
├── requirements.txt                 # Beroenden
├── lovecraft_mythos.ttl            # Genererad Turtle-fil (fullständig)
├── lovecraft_mythos_simple.ttl     # Genererad Turtle-fil (kopia)
├── lovecraft_mythos.jsonld         # Genererad JSON-LD-fil (fullständig)
└── lovecraft_mythos_simple.jsonld  # Genererad JSON-LD-fil (kopia)
```

---

##  Beroenden

Projektet använder följande Python-bibliotek:
- **[rdflib](https://rdflib.readthedocs.io/)** - RDF-bibliotek för Python
- **[pyparsing](https://pyparsing-docs.readthedocs.io/)** - Parser-bibliotek (beroende till rdflib)

---

##  Installation

### 1. Skapa en virtuell miljö (rekommenderat)

```bash
# Navigera till python-katalogen
cd python

# Skapa virtuell miljö (valfritt men rekommenderat)
python -m venv venv

# Aktivera virtuell miljö
# På Linux/Mac:
source venv/bin/activate
# På Windows:
venv\Scripts\activate
```

### 2. Installera beroenden

```bash
pip install -r requirements.txt
```

---

## 🏃 Kör programmet

### Generera Turtle-ontologin

```bash
python lovecraft_ontology.py
```

Detta kommer att generera två Turtle-filer:
- `lovecraft_mythos.ttl` - Fullständig ontologi
- `lovecraft_mythos_simple.ttl` - Kopia för läsbarhet

### Generera JSON-LD-ontologin

```bash
python lovecraft_ontology_jsonld.py
```

Detta kommer att generera två JSON-LD-filer:
- `lovecraft_mythos.jsonld` - Fullständig ontologi
- `lovecraft_mythos_simple.jsonld` - Kopia för läsbarhet

### Konsumera JSON-LD-ontologin

```bash
python consume_jsonld.py
```

Detta kommer att:
- Ladda `lovecraft_mythos.jsonld`
- Omvandla den till en RDF-graf
- Spara som `consumed_ontology.ttl` för verifiering
- Visa exempel-frågor på grafen

### Kör med anpassade inställningar

Du kan modifiera `lovecraft_ontology.py` för att:
- Lägga till fler entiteter
- Ändra relationer
- Anpassa namnrymder
- Lägga till nya egenskaper

---

##  Kör tester

### Kör alla tester (Turtle)

```bash
python -m unittest test_lovecraft_ontology.py
```

### Kör alla tester (JSON-LD)

```bash
python -m unittest test_lovecraft_ontology_jsonld.py
```

### Kör konsumtionstester

```bash
python -m unittest test_consume_jsonld.py
```

### Kör alla tester

```bash
python -m unittest discover -s . -p "test_*.py"
```

### Kör specifika testklasser

```bash
# Kör en specifik testklass (Turtle)
python -m unittest test_lovecraft_ontology.TestLovecraftOntology

# Kör en specifik testklass (JSON-LD)
python -m unittest test_lovecraft_ontology_jsonld.TestLovecraftOntologyJsonLD

# Kör en specifik testmetod
python -m unittest test_lovecraft_ontology.TestLovecraftOntology.test_cthulhu_properties
```

### Kör tester med verbose-utdata

```bash
python -m unittest -v test_lovecraft_ontology.py
python -m unittest -v test_lovecraft_ontology_jsonld.py
```

---

##  Tester som inkluderas

### Turtle-tester

| Testklass | Beskrivning |
|-----------|--------------|
| `TestLovecraftOntology` | Tester för ontologins struktur och innehåll |
| `TestLovecraftOntologyFileGeneration` | Tester för filgenerering |

### JSON-LD-tester

| Testklass | Beskrivning |
|-----------|--------------|
| `TestLovecraftOntologyJsonLD` | Tester för JSON-LD-ontologins struktur och innehåll |
| `TestLovecraftOntologyJsonLDFileGeneration` | Tester för JSON-LD-filgenerering |

### Testtäckning

-  Ontologiheader och metadata
-  Klasshierarki (OWL Classes)
-  Objekt- och Datatype Properties
-  Individer (Cthulhu, Necronomicon, etc.)
-  RDL-resurser
-  Relationer mellan entiteter
-  Serialisering till Turtle-format
-  Serialisering till JSON-LD-format
-  Filgenerering (både Turtle och JSON-LD)
-  JSON-LD konsumtion och omvandling till RDF
-  Roundtrip-test (JSON-LD → RDF → Turtle)

---

##  API-Referens

### Turtle API

#### `create_lovecraft_ontology()`

Skapar och returnerar en **RDF Graph** med Lovecraft-ontologin.

**Returvärde:** `rdflib.Graph` - En RDF-graf med ontologin

**Exempel:**
```python
from lovecraft_ontology import create_lovecraft_ontology

graph = create_lovecraft_ontology()
print(graph.serialize(format="turtle"))
```

#### `save_ontology(graph, filename)`

Sparar en RDF-graf till en Turtle-fil.

**Parametrar:**
- `graph` (`rdflib.Graph`) - RDF-grafen att spara
- `filename` (`str`) - Filnamn för utdata

**Exempel:**
```python
from lovecraft_ontology import create_lovecraft_ontology, save_ontology

graph = create_lovecraft_ontology()
save_ontology(graph, "my_ontology.ttl")
```

### JSON-LD API

#### Generering (skriva JSON-LD)

##### `create_lovecraft_ontology_jsonld()`

Skapar och returnerar en **JSON-LD-dokument** med Lovecraft-ontologin.

**Returvärde:** `dict` - Ett JSON-LD-dokument med ontologin

**Exempel:**
```python
from lovecraft_ontology_jsonld import create_lovecraft_ontology_jsonld
import json

doc = create_lovecraft_ontology_jsonld()
print(json.dumps(doc, indent=2))
```

##### `save_ontology_jsonld(ontology, filename)`

Sparar en JSON-LD-ontologi till en fil.

**Parametrar:**
- `ontology` (`dict`) - JSON-LD-dokumentet att spara
- `filename` (`str`) - Filnamn för utdata

**Exempel:**
```python
from lovecraft_ontology_jsonld import create_lovecraft_ontology_jsonld, save_ontology_jsonld

doc = create_lovecraft_ontology_jsonld()
save_ontology_jsonld(doc, "my_ontology.jsonld")
```

#### Konsumtion (läsa JSON-LD)

##### `consume_jsonld(filename)`

Laddar en JSON-LD-fil och returnerar en RDF-graf.

**Parametrar:**
- `filename` (`str`) - Filnamn för JSON-LD-filen

**Returvärde:** `rdflib.Graph` - RDF-graf med ontologin

**Exempel:**
```python
from consume_jsonld import consume_jsonld

graph = consume_jsonld("lovecraft_mythos.jsonld")
# Använd grafen med rdflib
for s, p, o in graph:
    print(f"{s} {p} {o}")
```

##### `jsonld_to_rdf_graph(jsonld_doc)`

Omvandlar ett JSON-LD-dokument (dict) till en RDF-graf.

**Parametrar:**
- `jsonld_doc` (`dict`) - JSON-LD-dokument som Python-dict

**Returvärde:** `rdflib.Graph` - RDF-graf

**Exempel:**
```python
from consume_jsonld import jsonld_to_rdf_graph
import json

with open("lovecraft_mythos.jsonld", "r") as f:
    doc = json.load(f)

graph = jsonld_to_rdf_graph(doc)
```

##### `save_as_turtle(graph, filename)`

Sparar en RDF-graf som Turtle-fil.

**Parametrar:**
- `graph` (`rdflib.Graph`) - RDF-grafen att spara
- `filename` (`str`) - Filnamn för utdata

**Exempel:**
```python
from consume_jsonld import consume_jsonld, save_as_turtle

graph = consume_jsonld("lovecraft_mythos.jsonld")
save_as_turtle(graph, "output.ttl")
```

---

##  Ontologins Struktur

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

##  Exempel: Använda ontologin

### Ladda och fråga ontologin

```python
from rdflib import Graph
from rdflib.namespace import RDF, OWL

# Ladda ontologin
graph = Graph()
graph.parse("lovecraft_mythos.ttl", format="turtle")

# Fråga: Hitta alla Great Old Ones
from rdflib import URIRef

LOVE = URIRef("https://lovecraft.frosteby.eu/ontology#")
LOVE_INST = URIRef("https://lovecraft.frosteby.eu/instance#")

for s, p, o in graph:
    if p == RDF.type and o == LOVE.GreatOldOne:
        print(f"Great Old One: {s}")
```

### Lägga till nya entiteter

```python
from lovecraft_ontology import create_lovecraft_ontology
from rdflib.namespace import RDF, OWL, RDFS

# Skapa ontologin
graph = create_lovecraft_ontology()

# Lägg till en ny klass
NewClass = URIRef("https://lovecraft.frosteby.eu/ontology#NewClass")
graph.add((NewClass, RDF.type, OWL.Class))
graph.add((NewClass, RDFS.label, Literal("New Class", lang="en")))

# Lägg till en ny individ
NewIndividual = URIRef("https://lovecraft.frosteby.eu/instance#NewIndividual")
graph.add((NewIndividual, RDF.type, OWL.NamedIndividual))
graph.add((NewIndividual, RDF.type, NewClass))
```

---

##  Uppdatera beroenden

Om du vill uppdatera beroendena:

```bash
pip install --upgrade rdflib pyparsing
pip freeze > requirements.txt
```

---

##  Licens

Detta projekt är öppen källkod och tillgängligt under [MIT-licensen](../LICENSE).

---

##  Bidrag

Bidrag är välkomna! Förslag på förbättringar:
- Lägga till fler Lovecraft-entiteter
- Förbättra testtäckningen
- Optimeringsförslag
- Dokumentationsförbättringar

---

##  Användningsexempel

### Fullständig workflow: Generera → Konsumera → Analysera

```python
# 1. Generera JSON-LD-ontologin
from lovecraft_ontology_jsonld import create_lovecraft_ontology_jsonld, save_ontology_jsonld

doc = create_lovecraft_ontology_jsonld()
save_ontology_jsonld(doc, "my_ontology.jsonld")

# 2. Konsumera JSON-LD och omvandla till RDF-graf
from consume_jsonld import consume_jsonld

graph = consume_jsonld("my_ontology.jsonld")

# 3. Fråga grafen med rdflib
from rdflib.namespace import RDF, OWL
from rdflib import Namespace

LOVE = Namespace("https://lovecraft.frosteby.eu/ontology#")
LOVE_INST = Namespace("https://lovecraft.frosteby.eu/instance#")

# Hitta alla Great Old Ones
for s, p, o in graph.triples((None, RDF.type, LOVE.GreatOldOne)):
    print(f"Great Old One: {s}")

# Hitta vad Cthulhu kontrollera
cthulhu = LOVE_INST.Cthulhu
for s, p, o in graph.triples((cthulhu, LOVE.controls, None)):
    print(f"Cthulhu controls: {o}")

# 4. Spara som Turtle för vidare användning
from consume_jsonld import save_as_turtle

save_as_turtle(graph, "my_ontology.ttl")
```

##  JSON-LD vs Turtle

### Skillnader mellan formaten

| Aspekt | Turtle | JSON-LD |
|--------|--------|---------|
| **Format** | Textbaserat, trippel-baserat | JSON-baserat, dokument-baserat |
| **Läsbarhet** | Mänskligt läsbart | Maskinläsbart, mänskligt läsbart med formatering |
| **Struktur** | Prefix + tripplar | @context + @graph med noder |
| **Användning** | RDF/Linked Data-standard | Web API-er, JavaScript-applikationer |
| **Beroenden** | Kräver rdflib | Inga externa beroenden (endast standardbibliotek) |

### När ska man använda vilket format?

- **Använd Turtle** om du arbetar med RDF-verktyg, SPARQL-frågor, eller behöver fullständig RDF/OWL-support
- **Använd JSON-LD** om du integrerar med webb-API:er, JavaScript-applikationer, eller behöver ett lättviktigt JSON-format

Båda implementeringarna representerar samma ontologi med samma klasser, egenskaper och individer.

## Continuous Integration

Projektet använder **GitHub Actions** för automatisk testning. CI-konfigurationen finns i [`.github/workflows/python-ci.yml`](../../.github/workflows/python-ci.yml).

### CI-Workflow

- **Trigger**: Push till `main`-branchen eller pull requests som påverkar `python/` katalogen
- **Miljö**: Ubuntu latest med Python 3.11
- **Steg**:
  1. Checkout repository
  2. Sätt upp Python 3.11
  3. Installera beroenden (`pip install -r requirements.txt`)
  4. Kör enhetstester (`python -m unittest discover -v -s . -p 'test_*.py'`)
  5. Generera ontologi-filer
  6. Verifiera genererade filer

### Lokalt test

För att köra samma tester lokalt:
```bash
cd python
python -m unittest discover -v -s . -p 'test_*.py'
```

##  Support

För frågor om Python-implementationen, se [huvud-dokumentationen](../README.md).
