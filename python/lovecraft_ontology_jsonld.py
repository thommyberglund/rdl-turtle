#!/usr/bin/env python3
"""
Lovecraft Ontology: JSON-LD implementation av RDL och OWL.

Detta script genererar en ontologi baserad på H.P. Lovecrafts mytologi
användande RDF, RDL och OWL i JSON-LD-format.
JSON-LD är en JSON-baserad serialisering av Linked Data som speglar Turtle-formatet.
"""

import json
from typing import Dict, List, Any, Optional


# Definiera namnrymder (samma som i Turtle-versionen)
LOVE = "https://lovecraft.frosteby.eu/ontology#"
LOVE_INST = "https://lovecraft.frosteby.eu/instance#"
DCTERMS = "http://purl.org/dc/terms/"
SKOS = "http://www.w3.org/2004/02/skos/core#"
OWL = "http://www.w3.org/2002/07/owl#"
RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
RDFS = "http://www.w3.org/2000/01/rdf-schema#"
XSD = "http://www.w3.org/2001/XMLSchema#"
FOAF = "http://xmlns.com/foaf/0.1/"


def create_jsonld_context() -> Dict[str, str]:
    """Skapar JSON-LD kontext med namnrymdsprefix."""
    return {
        "@context": {
            "lovecraft": LOVE,
            "instance": LOVE_INST,
            "owl": OWL,
            "rdf": RDF,
            "rdfs": RDFS,
            "xsd": XSD,
            "foaf": FOAF,
            "dcterms": DCTERMS,
            "skos": SKOS,
            # Standardprefix
            "type": "@type",
            "id": "@id",
            "value": "@value",
            "language": "@language"
        }
    }


def create_lovecraft_ontology_jsonld() -> Dict[str, Any]:
    """Skapar en OWL-ontologi för Lovecrafts mytologi i JSON-LD-format."""
    
    # Skapa JSON-LD-dokument med kontext
    doc = create_jsonld_context()
    
    # Lägg till @graph för att gruppera alla statement
    doc["@graph"] = []
    graph = doc["@graph"]
    
    # --- OWL Ontology Header ---
    ontology = {
        "@id": f"{LOVE}Ontology",
        "@type": "owl:Ontology",
        "dcterms:title": {"@value": "Lovecraft Mythos Ontology", "@language": "en"},
        "dcterms:description": {
            "@value": "An OWL ontology describing the entities, concepts, and relationships "
                      "in H.P. Lovecraft's Cthulhu Mythos.",
            "@language": "en"
        },
        "dcterms:creator": {"@value": "Lovecraft Ontology Project", "@language": "en"},
        "dcterms:date": {"@value": "2024-01-01", "@type": "xsd:date"},
        "owl:versionInfo": {"@value": "1.0.0"}
    }
    graph.append(ontology)
    
    # --- Klasser (OWL Classes) ---
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
        class_uri = f"{LOVE}{class_name}"
        class_node = {
            "@id": class_uri,
            "@type": "owl:Class",
            "rdfs:label": {"@value": class_name, "@language": "en"},
            "rdfs:comment": {"@value": description, "@language": "en"}
        }
        
        # Hierarki - alla är subklasser till Entity utom Entity själv
        if class_name != "Entity":
            class_node["rdfs:subClassOf"] = {"@id": f"{LOVE}Entity"}
        
        graph.append(class_node)
    
    # Specifika subklasser - lägg till subClassOf för Deity-subklasser
    # (Deity, GreatOldOne, OuterGod, ElderGod har redan skapats ovan)
    # Uppdatera GreatOldOne, OuterGod, ElderGod att peka på Deity istället för Entity
    # Vi gör detta genom att hitta och uppdatera de befintliga noderna
    for node in graph:
        node_id = node.get("@id", "")
        if node_id == f"{LOVE}GreatOldOne":
            node["rdfs:subClassOf"] = {"@id": f"{LOVE}Deity"}
        elif node_id == f"{LOVE}OuterGod":
            node["rdfs:subClassOf"] = {"@id": f"{LOVE}Deity"}
        elif node_id == f"{LOVE}ElderGod":
            node["rdfs:subClassOf"] = {"@id": f"{LOVE}Deity"}
    
    # --- Egenskaper (OWL Properties) ---
    properties = {
        "worships": {
            "label": "worships",
            "comment": "Relationship between a being and a deity they worship",
            "domain": f"{LOVE}Entity",
            "range": f"{LOVE}Deity",
        },
        "fears": {
            "label": "fears",
            "comment": "Relationship between a being and what they fear",
            "domain": f"{LOVE}Entity",
            "range": f"{LOVE}Entity",
        },
        "controls": {
            "label": "controls",
            "comment": "Relationship between a deity and what they control",
            "domain": f"{LOVE}Deity",
            "range": f"{LOVE}Entity",
        },
        "locatedIn": {
            "label": "located in",
            "comment": "Physical location of an entity or artifact",
            "domain": f"{LOVE}Entity",
            "range": f"{LOVE}Location",
        },
        "hasPower": {
            "label": "has power",
            "comment": "The powers or abilities of an entity",
            "domain": f"{LOVE}Entity",
            "range": "xsd:string",
        },
        "isPartOf": {
            "label": "is part of",
            "comment": "Part-whole relationship",
            "domain": f"{LOVE}Entity",
            "range": f"{LOVE}Entity",
        },
        "createdBy": {
            "label": "created by",
            "comment": "Relationship between an artifact and its creator",
            "domain": f"{LOVE}Artifact",
            "range": f"{LOVE}Entity",
        },
        "describedIn": {
            "label": "described in",
            "comment": "Relationship between an entity and a book that describes it",
            "domain": f"{LOVE}Entity",
            "range": f"{LOVE}Book",
        },
    }
    
    for prop_name, prop_info in properties.items():
        prop_uri = f"{LOVE}{prop_name}"
        prop_node = {
            "@id": prop_uri,
            "@type": "owl:ObjectProperty",
            "rdfs:label": {"@value": prop_info["label"], "@language": "en"},
            "rdfs:comment": {"@value": prop_info["comment"], "@language": "en"},
            "rdfs:domain": {"@id": prop_info["domain"]},
            "rdfs:range": {"@id": prop_info["range"]}
        }
        graph.append(prop_node)
    
    # --- Datatype Properties ---
    datatype_properties = {
        "hasName": "The name of an entity",
        "hasDescription": "A description of an entity",
        "hasOrigin": "The origin or place of creation",
        "hasAge": "The age of an entity (in years)",
    }
    
    for prop_name, description in datatype_properties.items():
        prop_uri = f"{LOVE}{prop_name}"
        prop_node = {
            "@id": prop_uri,
            "@type": "owl:DatatypeProperty",
            "rdfs:label": {"@value": prop_name, "@language": "en"},
            "rdfs:comment": {"@value": description, "@language": "en"},
            "rdfs:domain": {"@id": f"{LOVE}Entity"},
            "rdfs:range": {"@id": "xsd:string"}
        }
        graph.append(prop_node)
    
    # --- Individer (OWL Individuals) - Gudomligheter ---
    deities = {
        "Cthulhu": {
            "type": f"{LOVE}GreatOldOne",
            "description": "The high priest of the Great Old Ones, sleeping in R'lyeh",
            "fears": ["The Sun", "Light"],
            "controls": ["Dreams", "Madness"],
            "worshipped_by": ["Cult of Cthulhu"],
        },
        "Nyarlathotep": {
            "type": f"{LOVE}OuterGod",
            "description": "The crawling chaos, messenger of the Outer Gods",
            "fears": [],
            "controls": ["Time", "Space", "Madness"],
            "worshipped_by": ["Cult of the Black Pharaoh"],
        },
        "Azathoth": {
            "type": f"{LOVE}OuterGod",
            "description": "The blind idiot god at the center of the universe",
            "fears": [],
            "controls": ["Reality", "Time", "Space"],
            "worshipped_by": ["Outer God Cults"],
        },
        "YogSothoth": {
            "type": f"{LOVE}OuterGod",
            "description": "The all-in-one and one-in-all, key and gate of the universe",
            "fears": [],
            "controls": ["Knowledge", "Time", "Space"],
            "worshipped_by": ["Witch Cult"],
        },
        "Dagon": {
            "type": f"{LOVE}GreatOldOne",
            "description": "God of the Deep Ones",
            "fears": ["Land Dwellers"],
            "controls": ["The Sea", "Deep Ones"],
            "worshipped_by": ["Deep Ones", "Essex Cult"],
        },
        "Hastur": {
            "type": f"{LOVE}GreatOldOne",
            "description": "The King in Yellow, associated with madness and art",
            "fears": ["Reality"],
            "controls": ["Madness", "Theatre", "Art"],
            "worshipped_by": ["Cult of Hastur"],
        },
        "ShubNiggurath": {
            "type": f"{LOVE}GreatOldOne",
            "description": "The black goat of the woods with a thousand young",
            "fears": ["Purity"],
            "controls": ["Fertility", "Nature", "Darkness"],
            "worshipped_by": ["Cult of the Goat"],
        },
        "Yig": {
            "type": f"{LOVE}ElderGod",
            "description": "The serpent god, father of all snakes",
            "fears": ["Snake Hatred"],
            "controls": ["Snakes", "Venom"],
            "worshipped_by": ["Snake Cults"],
        },
    }
    
    for deity_name, deity_info in deities.items():
        deity_uri = f"{LOVE_INST}{deity_name}"
        deity_node = {
            "@id": deity_uri,
            "@type": ["owl:NamedIndividual", deity_info["type"]],
            "lovecraft:hasName": {"@value": deity_name, "@language": "en"},
            "lovecraft:hasDescription": {"@value": deity_info["description"], "@language": "en"}
        }
        
        # Lägg till relations
        fears_list = []
        for fear in deity_info["fears"]:
            fear_id = f"{LOVE_INST}{fear.replace(' ', '')}"
            fears_list.append({"@id": fear_id})
            # Skapa fear-koncept om det inte redan finns
            graph.append({
                "@id": fear_id,
                "@type": f"{LOVE}Concept"
            })
        if fears_list:
            deity_node["lovecraft:fears"] = fears_list if len(fears_list) > 1 else fears_list[0]
        
        controls_list = []
        for control in deity_info["controls"]:
            control_id = f"{LOVE_INST}{control.replace(' ', '')}"
            controls_list.append({"@id": control_id})
            # Skapa control-koncept
            graph.append({
                "@id": control_id,
                "@type": f"{LOVE}Concept"
            })
        if controls_list:
            deity_node["lovecraft:controls"] = controls_list if len(controls_list) > 1 else controls_list[0]
        
        worshippers_list = []
        for worshiper in deity_info["worshipped_by"]:
            worshiper_id = f"{LOVE_INST}{worshiper.replace(' ', '')}"
            worshippers_list.append({"@id": worshiper_id})
            # Skapa worshiper-creature
            graph.append({
                "@id": worshiper_id,
                "@type": f"{LOVE}Creature"
            })
        if worshippers_list:
            # Lägg till worships-relationer (omvänd relation)
            for worshiper in worshippers_list:
                graph.append({
                    "@id": worshiper["@id"],
                    "lovecraft:worships": {"@id": deity_uri}
                })
        
        graph.append(deity_node)
    
    # --- Varelser (Creatures) ---
    creatures = {
        "DeepOne": {
            "type": f"{LOVE}Creature",
            "description": "Fish-frog hybrids that serve Dagon",
            "worships": "Dagon",
            "locatedIn": "The Sea",
        },
        "Nightgaunt": {
            "type": f"{LOVE}Creature",
            "description": "Faceless, winged creatures that carry victims to unknown fates",
            "fears": "Light",
            "locatedIn": "The Night",
        },
        "Shoggoth": {
            "type": f"{LOVE}Creature",
            "description": "Amorphous, intelligent protoplasmic masses",
            "fears": "Elder Sign",
            "controls": "Shape-shifting",
        },
        "Ghoul": {
            "type": f"{LOVE}Creature",
            "description": "Subterranean humanoids that feed on corpses",
            "locatedIn": "Dreamlands",
            "fears": "Fire",
        },
    }
    
    for creature_name, creature_info in creatures.items():
        creature_uri = f"{LOVE_INST}{creature_name}"
        creature_node = {
            "@id": creature_uri,
            "@type": ["owl:NamedIndividual", creature_info["type"]],
            "lovecraft:hasName": {"@value": creature_name, "@language": "en"},
            "lovecraft:hasDescription": {"@value": creature_info["description"], "@language": "en"}
        }
        
        if "worships" in creature_info:
            worships = creature_info["worships"]
            creature_node["lovecraft:worships"] = {"@id": f"{LOVE_INST}{worships}"}
        
        if "locatedIn" in creature_info:
            location = creature_info["locatedIn"]
            location_id = f"{LOVE_INST}{location.replace(' ', '')}"
            creature_node["lovecraft:locatedIn"] = {"@id": location_id}
            # Skapa location om det inte redan finns
            graph.append({
                "@id": location_id,
                "@type": f"{LOVE}Location"
            })
        
        if "fears" in creature_info:
            fears = creature_info["fears"]
            fear_id = f"{LOVE_INST}{fears.replace(' ', '')}"
            creature_node["lovecraft:fears"] = {"@id": fear_id}
            graph.append({
                "@id": fear_id,
                "@type": f"{LOVE}Concept"
            })
        
        if "controls" in creature_info:
            controls = creature_info["controls"]
            control_id = f"{LOVE_INST}{controls.replace(' ', '')}"
            creature_node["lovecraft:controls"] = {"@id": control_id}
            graph.append({
                "@id": control_id,
                "@type": f"{LOVE}Concept"
            })
        
        graph.append(creature_node)
    
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
        location_uri = f"{LOVE_INST}{location_name}"
        location_node = {
            "@id": location_uri,
            "@type": ["owl:NamedIndividual", f"{LOVE}Location"],
            "lovecraft:hasName": {"@value": location_name, "@language": "en"},
            "lovecraft:hasDescription": {"@value": location_info["description"], "@language": "en"}
        }
        
        if "locatedIn" in location_info:
            parent_location = location_info["locatedIn"]
            parent_uri = f"{LOVE_INST}{parent_location.replace(' ', '')}"
            location_node["lovecraft:isPartOf"] = {"@id": parent_uri}
            # Skapa parent location om det inte redan finns
            graph.append({
                "@id": parent_uri,
                "@type": f"{LOVE}Location"
            })
        
        graph.append(location_node)
    
    # --- Artefakter (Artifacts) ---
    artifacts = {
        "Necronomicon": {
            "type": f"{LOVE}Book",
            "description": "The mad Arab Abdul Alhazred's infamous grimoire",
            "createdBy": "AbdulAlhazred",
            "describes": ["Cthulhu", "Nyarlathotep", "YogSothoth", "ShubNiggurath"],
        },
        "ElderSign": {
            "type": f"{LOVE}Artifact",
            "description": "A protective symbol against the Great Old Ones",
            "createdBy": "ElderGods",
        },
        "SilverKey": {
            "type": f"{LOVE}Artifact",
            "description": "A key that can open gates to other dimensions",
            "createdBy": "YogSothoth",
        },
        "BookOfEibon": {
            "type": f"{LOVE}Book",
            "description": "A grimoire of Hyperborean origin",
            "createdBy": "Eibon",
            "describes": ["Tsathoggua", "UbboSathla"],
        },
    }
    
    for artifact_name, artifact_info in artifacts.items():
        artifact_uri = f"{LOVE_INST}{artifact_name}"
        artifact_node = {
            "@id": artifact_uri,
            "@type": ["owl:NamedIndividual", artifact_info["type"]],
            "lovecraft:hasName": {"@value": artifact_name, "@language": "en"},
            "lovecraft:hasDescription": {"@value": artifact_info["description"], "@language": "en"}
        }
        
        if "createdBy" in artifact_info:
            creator = artifact_info["createdBy"]
            creator_uri = f"{LOVE_INST}{creator}"
            artifact_node["lovecraft:createdBy"] = {"@id": creator_uri}
            # Skapa creator om det inte redan finns
            creator_exists = any(node.get("@id") == creator_uri for node in graph)
            if not creator_exists:
                graph.append({
                    "@id": creator_uri,
                    "@type": f"{LOVE}Entity"
                })
        
        if "describes" in artifact_info:
            for entity in artifact_info["describes"]:
                entity_uri = f"{LOVE_INST}{entity}"
                # Lägg till describedIn-relation (omvänd)
                graph.append({
                    "@id": entity_uri,
                    "lovecraft:describedIn": {"@id": artifact_uri}
                })
        
        graph.append(artifact_node)
    
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
        human_uri = f"{LOVE_INST}{human_name}"
        # Fix för H.P. Lovecraft
        display_name = human_name.replace("HP", "H.P.")
        
        human_node = {
            "@id": human_uri,
            "@type": ["owl:NamedIndividual", f"{LOVE}Human"],
            "lovecraft:hasName": {"@value": display_name, "@language": "en"},
            "lovecraft:hasDescription": {"@value": human_info["description"], "@language": "en"}
        }
        
        if "fears" in human_info:
            fears_list = []
            for fear in human_info["fears"]:
                fear_id = f"{LOVE_INST}{fear.replace(' ', '')}"
                fears_list.append({"@id": fear_id})
                graph.append({
                    "@id": fear_id,
                    "@type": f"{LOVE}Concept"
                })
            if fears_list:
                human_node["lovecraft:fears"] = fears_list if len(fears_list) > 1 else fears_list[0]
        
        if "locatedIn" in human_info:
            location = human_info["locatedIn"]
            location_uri = f"{LOVE_INST}{location}"
            human_node["lovecraft:locatedIn"] = {"@id": location_uri}
            graph.append({
                "@id": location_uri,
                "@type": f"{LOVE}Location"
            })
        
        graph.append(human_node)
    
    # --- RDL-specifika element ---
    # RDL Resource-klass
    graph.append({
        "@id": f"{LOVE}Resource",
        "@type": "owl:Class",
        "rdfs:label": {"@value": "Resource", "@language": "en"},
        "rdfs:comment": {"@value": "A generic resource in the Lovecraft universe", "@language": "en"}
    })
    
    # RDL egenskaper
    graph.append({
        "@id": f"{LOVE}hasResourceType",
        "@type": "owl:DatatypeProperty",
        "rdfs:label": {"@value": "has resource type", "@language": "en"},
        "rdfs:comment": {"@value": "The type of a resource", "@language": "en"},
        "rdfs:domain": {"@id": f"{LOVE}Resource"},
        "rdfs:range": {"@id": "xsd:string"}
    })
    
    graph.append({
        "@id": f"{LOVE}hasResourceValue",
        "@type": "owl:DatatypeProperty",
        "rdfs:label": {"@value": "has resource value", "@language": "en"},
        "rdfs:comment": {"@value": "The value of a resource", "@language": "en"},
        "rdfs:domain": {"@id": f"{LOVE}Resource"},
        "rdfs:range": {"@id": "xsd:string"}
    })
    
    # Exempel på RDL-resurser - lägg till Resource-typ och egenskaper till befintliga noder
    # Necronomicon
    for node in graph:
        if node.get("@id") == f"{LOVE_INST}Necronomicon":
            # Lägg till Resource-typ
            current_types = node.get("@type", [])
            if isinstance(current_types, str):
                current_types = [current_types]
            if f"{LOVE}Resource" not in current_types:
                current_types.append(f"{LOVE}Resource")
            node["@type"] = current_types
            # Lägg till RDL-egenskaper
            node[f"{LOVE}hasResourceType"] = {"@value": "Grimoire", "@language": "en"}
            node[f"{LOVE}hasResourceValue"] = {"@value": "The most dangerous book in existence", "@language": "en"}
            break
    
    # ElderSign
    for node in graph:
        if node.get("@id") == f"{LOVE_INST}ElderSign":
            # Lägg till Resource-typ
            current_types = node.get("@type", [])
            if isinstance(current_types, str):
                current_types = [current_types]
            if f"{LOVE}Resource" not in current_types:
                current_types.append(f"{LOVE}Resource")
            node["@type"] = current_types
            # Lägg till RDL-egenskaper
            node[f"{LOVE}hasResourceType"] = {"@value": "Symbol", "@language": "en"}
            node[f"{LOVE}hasResourceValue"] = {"@value": "A protective sigil against evil", "@language": "en"}
            break
    
    return doc


def save_ontology_jsonld(ontology: Dict[str, Any], filename: str) -> None:
    """Sparar ontologin till en JSON-LD-fil."""
    jsonld_data = json.dumps(ontology, indent=2, ensure_ascii=False)
    with open(filename, "w", encoding="utf-8") as f:
        f.write(jsonld_data)
    print(f"Ontology saved to {filename}")


def main():
    """Huvudfunktion för att generera JSON-LD-ontologin."""
    print("Creating Lovecraft Mythos Ontology in JSON-LD format...")
    ontology = create_lovecraft_ontology_jsonld()
    
    # Spara som JSON-LD-filer
    save_ontology_jsonld(ontology, "lovecraft_mythos.jsonld")
    save_ontology_jsonld(ontology, "lovecraft_mythos_simple.jsonld")
    
    print("Done! The ontology has been generated in JSON-LD format.")


if __name__ == "__main__":
    main()
