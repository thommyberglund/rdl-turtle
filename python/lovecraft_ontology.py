#!/usr/bin/env python3
"""
Lovecraft Ontology: Referensimplementation av RDL och OWL i Turtle-format.

Detta script genererar en ontologi baserad på H.P. Lovecrafts mytologi
användande RDF, RDL och OWL i Turtle-syntax.
"""

from rdflib import Graph, Literal, Namespace, URIRef, XSD
from rdflib.namespace import RDF, RDFS, OWL, XSD, FOAF

# Definiera namnrymder
LOVE = Namespace("https://lovecraft.example.org/ontology#")
LOVE_INST = Namespace("https://lovecraft.example.org/instance#")
DCTERMS = Namespace("http://purl.org/dc/terms/")
SKOS = Namespace("http://www.w3.org/2004/02/skos/core#")


def create_lovecraft_ontology():
    """Skapar en OWL-ontologi för Lovecrafts mytologi."""
    g = Graph()
    
    # Bind namnrymder för lättare läsning
    g.bind("lovecraft", LOVE)
    g.bind("instance", LOVE_INST)
    g.bind("owl", OWL)
    g.bind("rdf", RDF)
    g.bind("rdfs", RDFS)
    g.bind("xsd", XSD)
    g.bind("foaf", FOAF)
    g.bind("dcterms", DCTERMS)
    g.bind("skos", SKOS)

    # --- OWL Ontology Header ---
    g.add((LOVE.Ontology, RDF.type, OWL.Ontology))
    g.add((LOVE.Ontology, DCTERMS.title, Literal("Lovecraft Mythos Ontology", lang="en")))
    g.add((LOVE.Ontology, DCTERMS.description, 
           Literal("An OWL ontology describing the entities, concepts, and relationships "
                   "in H.P. Lovecraft's Cthulhu Mythos.", lang="en")))
    g.add((LOVE.Ontology, DCTERMS.creator, 
           Literal("Lovecraft Ontology Project", lang="en")))
    g.add((LOVE.Ontology, DCTERMS.date, Literal("2024-01-01", datatype=XSD.date)))
    g.add((LOVE.Ontology, OWL.versionInfo, Literal("1.0.0")))

    # --- Klasser (OWL Classes) ---
    # Huvudklasser
    classes = {
        "Entity": "Base class for all Lovecraftian entities",
        "Deity": "A cosmic deity or god-like being",
        "ElderGod": "Benevolent or neutral cosmic entities",
        "GreatOldOne": "Powerful ancient beings, often malevolent",
        "OuterGod": "Entities from outside the universe",
        "Creature": "Non-divine beings from the mythos",
        "Human": "Human characters in the mythos",
        "Location": "Places in the Lovecraft universe",
        "Artifact": "Magical or powerful objects",
        "Book": "Forbidden tomes and manuscripts",
        "Concept": "Abstract concepts in the mythos",
    }

    for class_name, description in classes.items():
        uri = LOVE[class_name]
        g.add((uri, RDF.type, OWL.Class))
        g.add((uri, RDFS.label, Literal(class_name, lang="en")))
        g.add((uri, RDFS.comment, Literal(description, lang="en")))
        # Hierarki
        if class_name != "Entity":
            g.add((uri, RDFS.subClassOf, LOVE.Entity))

    # Specifika subklasser
    g.add((LOVE.GreatOldOne, RDFS.subClassOf, LOVE.Deity))
    g.add((LOVE.OuterGod, RDFS.subClassOf, LOVE.Deity))
    g.add((LOVE.ElderGod, RDFS.subClassOf, LOVE.Deity))

    # --- Egenskaper (OWL Properties) ---
    properties = {
        "worships": {
            "label": "worships",
            "comment": "Relationship between a being and a deity they worship",
            "domain": LOVE.Entity,
            "range": LOVE.Deity,
        },
        "fears": {
            "label": "fears",
            "comment": "Relationship between a being and what they fear",
            "domain": LOVE.Entity,
            "range": LOVE.Entity,
        },
        "controls": {
            "label": "controls",
            "comment": "Relationship between a deity and what they control",
            "domain": LOVE.Deity,
            "range": LOVE.Entity,
        },
        "locatedIn": {
            "label": "located in",
            "comment": "Physical location of an entity or artifact",
            "domain": LOVE.Entity,
            "range": LOVE.Location,
        },
        "hasPower": {
            "label": "has power",
            "comment": "The powers or abilities of an entity",
            "domain": LOVE.Entity,
            "range": XSD.string,
        },
        "isPartOf": {
            "label": "is part of",
            "comment": "Part-whole relationship",
            "domain": LOVE.Entity,
            "range": LOVE.Entity,
        },
        "createdBy": {
            "label": "created by",
            "comment": "Relationship between an artifact and its creator",
            "domain": LOVE.Artifact,
            "range": LOVE.Entity,
        },
        "describedIn": {
            "label": "described in",
            "comment": "Relationship between an entity and a book that describes it",
            "domain": LOVE.Entity,
            "range": LOVE.Book,
        },
    }

    for prop_name, prop_info in properties.items():
        uri = LOVE[prop_name]
        g.add((uri, RDF.type, OWL.ObjectProperty))
        g.add((uri, RDFS.label, Literal(prop_info["label"], lang="en")))
        g.add((uri, RDFS.comment, Literal(prop_info["comment"], lang="en")))
        g.add((uri, RDFS.domain, prop_info["domain"]))
        g.add((uri, RDFS.range, prop_info["range"]))

    # --- Datatype Properties ---
    datatype_properties = {
        "hasName": "The name of an entity",
        "hasDescription": "A description of an entity",
        "hasOrigin": "The origin or place of creation",
        "hasAge": "The age of an entity (in years)",
    }

    for prop_name, description in datatype_properties.items():
        uri = LOVE[prop_name]
        g.add((uri, RDF.type, OWL.DatatypeProperty))
        g.add((uri, RDFS.label, Literal(prop_name, lang="en")))
        g.add((uri, RDFS.comment, Literal(description, lang="en")))
        g.add((uri, RDFS.domain, LOVE.Entity))
        g.add((uri, RDFS.range, XSD.string))

    # --- Individer (OWL Individuals) ---
    
    # Gudomligheter (Deities)
    deities = {
        "Cthulhu": {
            "type": LOVE.GreatOldOne,
            "description": "The high priest of the Great Old Ones, sleeping in R'lyeh",
            "fears": ["The Sun", "Light"],
            "controls": ["Dreams", "Madness"],
            "worshipped_by": ["Cult of Cthulhu"],
        },
        "Nyarlathotep": {
            "type": LOVE.OuterGod,
            "description": "The crawling chaos, messenger of the Outer Gods",
            "fears": [],
            "controls": ["Time", "Space", "Madness"],
            "worshipped_by": ["Cult of the Black Pharaoh"],
        },
        "Azathoth": {
            "type": LOVE.OuterGod,
            "description": "The blind idiot god at the center of the universe",
            "fears": [],
            "controls": ["Reality", "Time", "Space"],
            "worshipped_by": ["Outer God Cults"],
        },
        "YogSothoth": {
            "type": LOVE.OuterGod,
            "description": "The all-in-one and one-in-all, key and gate of the universe",
            "fears": [],
            "controls": ["Knowledge", "Time", "Space"],
            "worshipped_by": ["Witch Cult"],
        },
        "Dagon": {
            "type": LOVE.GreatOldOne,
            "description": "God of the Deep Ones",
            "fears": ["Land Dwellers"],
            "controls": ["The Sea", "Deep Ones"],
            "worshipped_by": ["Deep Ones", "Essex Cult"],
        },
        "Hastur": {
            "type": LOVE.GreatOldOne,
            "description": "The King in Yellow, associated with madness and art",
            "fears": ["Reality"],
            "controls": ["Madness", "Theatre", "Art"],
            "worshipped_by": ["Cult of Hastur"],
        },
        "ShubNiggurath": {
            "type": LOVE.GreatOldOne,
            "description": "The black goat of the woods with a thousand young",
            "fears": ["Purity"],
            "controls": ["Fertility", "Nature", "Darkness"],
            "worshipped_by": ["Cult of the Goat"],
        },
        "Yig": {
            "type": LOVE.ElderGod,
            "description": "The serpent god, father of all snakes",
            "fears": ["Snake Hatred"],
            "controls": ["Snakes", "Venom"],
            "worshipped_by": ["Snake Cults"],
        },
    }

    # Skapa individer för gudomligheter
    for deity_name, deity_info in deities.items():
        deity_uri = LOVE_INST[deity_name]
        g.add((deity_uri, RDF.type, deity_info["type"]))
        g.add((deity_uri, RDF.type, OWL.NamedIndividual))
        g.add((deity_uri, LOVE.hasName, Literal(deity_name, lang="en")))
        g.add((deity_uri, LOVE.hasDescription, Literal(deity_info["description"], lang="en")))

        # Lägg till relations
        for fear in deity_info["fears"]:
            fear_uri = LOVE_INST[fear.replace(" ", "")]
            g.add((fear_uri, RDF.type, LOVE.Concept))
            g.add((deity_uri, LOVE.fears, fear_uri))

        for control in deity_info["controls"]:
            control_uri = LOVE_INST[control.replace(" ", "")]
            g.add((control_uri, RDF.type, LOVE.Concept))
            g.add((deity_uri, LOVE.controls, control_uri))

        for worshiper in deity_info["worshipped_by"]:
            worshiper_uri = LOVE_INST[worshiper.replace(" ", "")]
            g.add((worshiper_uri, RDF.type, LOVE.Creature))
            g.add((worshiper_uri, LOVE.worships, deity_uri))

    # --- Varelser (Creatures) ---
    creatures = {
        "DeepOne": {
            "type": LOVE.Creature,
            "description": "Fish-frog hybrids that serve Dagon",
            "worships": "Dagon",
            "locatedIn": "The Sea",
        },
        "Nightgaunt": {
            "type": LOVE.Creature,
            "description": "Faceless, winged creatures that carry victims to unknown fates",
            "fears": "Light",
            "locatedIn": "The Night",
        },
        "Shoggoth": {
            "type": LOVE.Creature,
            "description": "Amorphous, intelligent protoplasmic masses",
            "fears": "Elder Sign",
            "controls": "Shape-shifting",
        },
        "Ghoul": {
            "type": LOVE.Creature,
            "description": "Subterranean humanoids that feed on corpses",
            "locatedIn": "Dreamlands",
            "fears": "Fire",
        },
    }

    for creature_name, creature_info in creatures.items():
        creature_uri = LOVE_INST[creature_name]
        g.add((creature_uri, RDF.type, creature_info["type"]))
        g.add((creature_uri, RDF.type, OWL.NamedIndividual))
        g.add((creature_uri, LOVE.hasName, Literal(creature_name, lang="en")))
        g.add((creature_uri, LOVE.hasDescription, Literal(creature_info["description"], lang="en")))

        if "worships" in creature_info:
            worships = creature_info["worships"]
            g.add((creature_uri, LOVE.worships, LOVE_INST[worships]))

        if "locatedIn" in creature_info:
            location = creature_info["locatedIn"]
            location_uri = LOVE_INST[location.replace(" ", "")]
            g.add((location_uri, RDF.type, LOVE.Location))
            g.add((creature_uri, LOVE.locatedIn, location_uri))

        if "fears" in creature_info:
            fears = creature_info["fears"]
            fear_uri = LOVE_INST[fears.replace(" ", "")]
            g.add((fear_uri, RDF.type, LOVE.Concept))
            g.add((creature_uri, LOVE.fears, fear_uri))

        if "controls" in creature_info:
            controls = creature_info["controls"]
            control_uri = LOVE_INST[controls.replace(" ", "")]
            g.add((control_uri, RDF.type, LOVE.Concept))
            g.add((creature_uri, LOVE.controls, control_uri))

    # --- Platser (Locations) ---
    locations = {
        "Rlyeh": {
            "description": "The sunken city where Cthulhu dreams",
            "locatedIn": "The Pacific Ocean",
        },
        "Innsmouth": {
            "description": "A decaying coastal town inhabited by Deep One hybrids",
            "locatedIn": "Massachusetts",
        },
        "Arkham": {
            "description": "A fictional town in Massachusetts, home to Miskatonic University",
            "locatedIn": "Massachusetts",
        },
        "MiskatonicUniversity": {
            "description": "A prestigious university with a dark history",
            "locatedIn": "Arkham",
        },
        "Dreamlands": {
            "description": "A parallel dimension accessible through dreams",
            "locatedIn": "The Astral Plane",
        },
        "PlateauOfLeng": {
            "description": "A high plateau in Central Asia, home to ancient horrors",
            "locatedIn": "Asia",
        },
    }

    for location_name, location_info in locations.items():
        location_uri = LOVE_INST[location_name]
        g.add((location_uri, RDF.type, LOVE.Location))
        g.add((location_uri, RDF.type, OWL.NamedIndividual))
        g.add((location_uri, LOVE.hasName, Literal(location_name, lang="en")))
        g.add((location_uri, LOVE.hasDescription, Literal(location_info["description"], lang="en")))

        if "locatedIn" in location_info:
            parent_location = location_info["locatedIn"]
            parent_uri = LOVE_INST[parent_location.replace(" ", "")]
            g.add((parent_uri, RDF.type, LOVE.Location))
            g.add((location_uri, LOVE.isPartOf, parent_uri))

    # --- Artefakter (Artifacts) ---
    artifacts = {
        "Necronomicon": {
            "type": LOVE.Book,
            "description": "The mad Arab Abdul Alhazred's infamous grimoire",
            "createdBy": "AbdulAlhazred",
            "describes": ["Cthulhu", "Nyarlathotep", "YogSothoth", "ShubNiggurath"],
        },
        "ElderSign": {
            "type": LOVE.Artifact,
            "description": "A protective symbol against the Great Old Ones",
            "createdBy": "ElderGods",
        },
        "SilverKey": {
            "type": LOVE.Artifact,
            "description": "A key that can open gates to other dimensions",
            "createdBy": "YogSothoth",
        },
        "BookOfEibon": {
            "type": LOVE.Book,
            "description": "A grimoire of Hyperborean origin",
            "createdBy": "Eibon",
            "describes": ["Tsathoggua", "UbboSathla"],
        },
    }

    for artifact_name, artifact_info in artifacts.items():
        artifact_uri = LOVE_INST[artifact_name]
        g.add((artifact_uri, RDF.type, artifact_info["type"]))
        g.add((artifact_uri, RDF.type, OWL.NamedIndividual))
        g.add((artifact_uri, LOVE.hasName, Literal(artifact_name, lang="en")))
        g.add((artifact_uri, LOVE.hasDescription, Literal(artifact_info["description"], lang="en")))

        if "createdBy" in artifact_info:
            creator = artifact_info["createdBy"]
            creator_uri = LOVE_INST[creator]
            g.add((creator_uri, RDF.type, LOVE.Entity))
            g.add((artifact_uri, LOVE.createdBy, creator_uri))

        if "describes" in artifact_info:
            for entity in artifact_info["describes"]:
                entity_uri = LOVE_INST[entity]
                g.add((entity_uri, LOVE.describedIn, artifact_uri))

    # --- Människor (Humans) ---
    humans = {
        "HPLovecraft": {
            "description": "Howard Phillips Lovecraft, the creator of the mythos",
            "fears": ["The Unknown", "Madness"],
            "locatedIn": "Providence",
        },
        "AbdulAlhazred": {
            "description": "The mad Arab, author of the Necronomicon",
            "fears": ["His Own Creations"],
            "locatedIn": "Damascus",
        },
        "RandolphCarter": {
            "description": "A recurring character in Lovecraft's stories, explorer of the Dreamlands",
            "fears": ["The Unknown"],
            "locatedIn": "Arkham",
        },
    }

    for human_name, human_info in humans.items():
        human_uri = LOVE_INST[human_name]
        g.add((human_uri, RDF.type, LOVE.Human))
        g.add((human_uri, RDF.type, OWL.NamedIndividual))
        g.add((human_uri, LOVE.hasName, Literal(human_name.replace("HP", "H.P."), lang="en")))
        g.add((human_uri, LOVE.hasDescription, Literal(human_info["description"], lang="en")))

        if "fears" in human_info:
            for fear in human_info["fears"]:
                fear_uri = LOVE_INST[fear.replace(" ", "")]
                g.add((fear_uri, RDF.type, LOVE.Concept))
                g.add((human_uri, LOVE.fears, fear_uri))

        if "locatedIn" in human_info:
            location = human_info["locatedIn"]
            location_uri = LOVE_INST[location]
            g.add((location_uri, RDF.type, LOVE.Location))
            g.add((human_uri, LOVE.locatedIn, location_uri))

    # --- RDL-specifika element (Resource Description Language) ---
    # RDL används för att beskriva resurser och deras relationer
    # Vi lägger till RDL-specifika klasser och egenskaper

    # RDL Class for "Resource"
    g.add((LOVE.Resource, RDF.type, OWL.Class))
    g.add((LOVE.Resource, RDFS.label, Literal("Resource", lang="en")))
    g.add((LOVE.Resource, RDFS.comment, 
           Literal("A generic resource in the Lovecraft universe", lang="en")))

    # RDL Property for "hasResourceType"
    g.add((LOVE.hasResourceType, RDF.type, OWL.DatatypeProperty))
    g.add((LOVE.hasResourceType, RDFS.label, Literal("has resource type", lang="en")))
    g.add((LOVE.hasResourceType, RDFS.comment, 
           Literal("The type of a resource", lang="en")))
    g.add((LOVE.hasResourceType, RDFS.domain, LOVE.Resource))
    g.add((LOVE.hasResourceType, RDFS.range, XSD.string))

    # RDL Property for "hasResourceValue"
    g.add((LOVE.hasResourceValue, RDF.type, OWL.DatatypeProperty))
    g.add((LOVE.hasResourceValue, RDFS.label, Literal("has resource value", lang="en")))
    g.add((LOVE.hasResourceValue, RDFS.comment, 
           Literal("The value of a resource", lang="en")))
    g.add((LOVE.hasResourceValue, RDFS.domain, LOVE.Resource))
    g.add((LOVE.hasResourceValue, RDFS.range, XSD.string))

    # Exempel på RDL-resurser
    g.add((LOVE_INST.Necronomicon, RDF.type, LOVE.Resource))
    g.add((LOVE_INST.Necronomicon, LOVE.hasResourceType, Literal("Grimoire", lang="en")))
    g.add((LOVE_INST.Necronomicon, LOVE.hasResourceValue, 
           Literal("The most dangerous book in existence", lang="en")))

    g.add((LOVE_INST.ElderSign, RDF.type, LOVE.Resource))
    g.add((LOVE_INST.ElderSign, LOVE.hasResourceType, Literal("Symbol", lang="en")))
    g.add((LOVE_INST.ElderSign, LOVE.hasResourceValue, 
           Literal("A protective sigil against evil", lang="en")))

    return g


def save_ontology(graph, filename):
    """Sparar ontologin till en Turtle-fil."""
    turtle_data = graph.serialize(format="turtle")
    if isinstance(turtle_data, bytes):
        turtle_data = turtle_data.decode("utf-8")
    with open(filename, "w", encoding="utf-8") as f:
        f.write(turtle_data)
    print(f"Ontology saved to {filename}")


def main():
    print("Creating Lovecraft Mythos Ontology in Turtle format...")
    ontology = create_lovecraft_ontology()
    
    # Spara som Turtle-fil
    save_ontology(ontology, "/workspace/rdl-turtle/lovecraft_mythos.ttl")
    
    # Spara en förenklad version för läsbarhet
    save_ontology(ontology, "/workspace/rdl-turtle/lovecraft_mythos_simple.ttl")
    
    print("Done! The ontology has been generated in Turtle format.")


if __name__ == "__main__":
    main()
