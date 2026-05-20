#!/usr/bin/env python3
"""
Enhetstester för Lovecraft Ontology JSON-LD implementation.

Dessa tester verifierar att JSON-LD-ontologin skapas korrekt med alla
nödvändiga klasser, egenskaper och individer.
"""

import unittest
import os
import tempfile
import json

from lovecraft_ontology_jsonld import create_lovecraft_ontology_jsonld, save_ontology_jsonld


class TestLovecraftOntologyJsonLD(unittest.TestCase):
    """Testklass för Lovecraft-ontologin i JSON-LD-format."""

    def setUp(self):
        """Skapar en ny ontologi för varje test."""
        self.doc = create_lovecraft_ontology_jsonld()
        self.graph = self.doc["@graph"]
        self.LOVE = "https://lovecraft.frosteby.eu/ontology#"
        self.LOVE_INST = "https://lovecraft.frosteby.eu/instance#"

    def find_node_by_id(self, node_id: str):
        """Hittar en nod i grafen baserat på @id."""
        for node in self.graph:
            if node.get("@id") == node_id:
                return node
        return None

    def test_jsonld_structure(self):
        """Testar att JSON-LD-dokumentet har korrekt struktur."""
        # Verifiera att @context finns
        self.assertIn("@context", self.doc)
        
        # Verifiera att @graph finns
        self.assertIn("@graph", self.doc)
        
        # Verifiera att grafen är en lista
        self.assertIsInstance(self.graph, list)
        
        # Verifiera att grafen inte är tom
        self.assertGreater(len(self.graph), 0)

    def test_jsonld_context(self):
        """Testar att JSON-LD-kontexten är korrekt."""
        context = self.doc["@context"]
        
        # Verifiera namnrymdsprefix
        expected_prefixes = {
            "lovecraft": self.LOVE,
            "instance": self.LOVE_INST,
            "owl": "http://www.w3.org/2002/07/owl#",
            "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
            "xsd": "http://www.w3.org/2001/XMLSchema#",
            "dcterms": "http://purl.org/dc/terms/",
        }
        
        for prefix, uri in expected_prefixes.items():
            self.assertIn(prefix, context)
            self.assertEqual(context[prefix], uri)

    def test_ontology_header(self):
        """Testar att ontologin har korrekt header."""
        ontology_node = self.find_node_by_id(f"{self.LOVE}Ontology")
        
        self.assertIsNotNone(ontology_node, "Ontology node should exist")
        self.assertEqual(ontology_node["@type"], "owl:Ontology")
        
        # Verifiera metadata
        self.assertIn("dcterms:title", ontology_node)
        self.assertEqual(ontology_node["dcterms:title"]["@value"], "Lovecraft Mythos Ontology")
        self.assertEqual(ontology_node["dcterms:title"]["@language"], "en")
        
        self.assertIn("dcterms:description", ontology_node)
        self.assertIn("Lovecraft's Cthulhu Mythos", ontology_node["dcterms:description"]["@value"])
        
        self.assertIn("dcterms:creator", ontology_node)
        self.assertEqual(ontology_node["dcterms:creator"]["@value"], "Lovecraft Ontology Project")
        
        self.assertIn("owl:versionInfo", ontology_node)
        self.assertEqual(ontology_node["owl:versionInfo"]["@value"], "1.0.0")

    def test_main_classes_exist(self):
        """Testar att alla huvudklasser skapas korrekt."""
        class_names = [
            "Entity", "Deity", "ElderGod", "GreatOldOne", "OuterGod",
            "Creature", "Human", "Location", "Artifact", "Book", "Concept"
        ]
        
        for class_name in class_names:
            class_uri = f"{self.LOVE}{class_name}"
            class_node = self.find_node_by_id(class_uri)
            
            self.assertIsNotNone(class_node, f"Class {class_name} should exist")
            self.assertEqual(class_node["@type"], "owl:Class")
            self.assertIn("rdfs:label", class_node)
            self.assertEqual(class_node["rdfs:label"]["@value"], class_name)

    def test_class_hierarchy(self):
        """Testar klasshierarkin (subClassOf-relationer)."""
        # Entity ska inte ha subClassOf
        entity_node = self.find_node_by_id(f"{self.LOVE}Entity")
        self.assertIsNotNone(entity_node)
        
        # Deity är subklass till Entity
        deity_node = self.find_node_by_id(f"{self.LOVE}Deity")
        self.assertIsNotNone(deity_node)
        self.assertIn("rdfs:subClassOf", deity_node)
        self.assertEqual(deity_node["rdfs:subClassOf"]["@id"], f"{self.LOVE}Entity")
        
        # GreatOldOne, OuterGod, ElderGod är subklasser till Deity
        for subclass in ["GreatOldOne", "OuterGod", "ElderGod"]:
            subclass_node = self.find_node_by_id(f"{self.LOVE}{subclass}")
            self.assertIsNotNone(subclass_node)
            self.assertIn("rdfs:subClassOf", subclass_node)
            self.assertEqual(subclass_node["rdfs:subClassOf"]["@id"], f"{self.LOVE}Deity")

    def test_object_properties_exist(self):
        """Testar att Object Properties skapas korrekt."""
        property_names = [
            "worships", "fears", "controls", "locatedIn",
            "hasPower", "isPartOf", "createdBy", "describedIn"
        ]
        
        for prop_name in property_names:
            prop_uri = f"{self.LOVE}{prop_name}"
            prop_node = self.find_node_by_id(prop_uri)
            
            self.assertIsNotNone(prop_node, f"Property {prop_name} should exist")
            self.assertEqual(prop_node["@type"], "owl:ObjectProperty")
            self.assertIn("rdfs:label", prop_node)

    def test_datatype_properties_exist(self):
        """Testar att Datatype Properties skapas korrekt."""
        property_names = ["hasName", "hasDescription", "hasOrigin", "hasAge"]
        
        for prop_name in property_names:
            prop_uri = f"{self.LOVE}{prop_name}"
            prop_node = self.find_node_by_id(prop_uri)
            
            self.assertIsNotNone(prop_node, f"Datatype property {prop_name} should exist")
            self.assertEqual(prop_node["@type"], "owl:DatatypeProperty")

    def test_individuals_exist(self):
        """Testar att specifika individer skapas korrekt."""
        individual_names = [
            "Cthulhu", "Nyarlathotep", "Azathoth", "YogSothoth",
            "Necronomicon", "ElderSign", "Rlyeh", "Innsmouth",
            "HPLovecraft", "AbdulAlhazred"
        ]
        
        for individual_name in individual_names:
            individual_uri = f"{self.LOVE_INST}{individual_name}"
            individual_node = self.find_node_by_id(individual_uri)
            
            self.assertIsNotNone(individual_node, f"Individual {individual_name} should exist")
            # Individer kan ha flera typer
            types = individual_node.get("@type", [])
            if isinstance(types, str):
                types = [types]
            # Kontrollera att det finns typer
            self.assertGreater(len(types), 0, f"Individual {individual_name} should have types")

    def test_cthulhu_properties(self):
        """Testar att Cthulhu har korrekta egenskaper."""
        cthulhu_uri = f"{self.LOVE_INST}Cthulhu"
        cthulhu_node = self.find_node_by_id(cthulhu_uri)
        
        self.assertIsNotNone(cthulhu_node)
        
        # Cthulhu ska vara en GreatOldOne
        types = cthulhu_node.get("@type", [])
        if isinstance(types, str):
            types = [types]
        self.assertIn(f"{self.LOVE}GreatOldOne", types)
        
        # Cthulhu ska ha ett namn
        self.assertIn("lovecraft:hasName", cthulhu_node)
        self.assertEqual(cthulhu_node["lovecraft:hasName"]["@value"], "Cthulhu")
        
        # Cthulhu ska ha en beskrivning
        self.assertIn("lovecraft:hasDescription", cthulhu_node)
        self.assertIn("high priest", cthulhu_node["lovecraft:hasDescription"]["@value"])
        
        # Cthulhu ska kontrollera Dreams och Madness
        self.assertIn("lovecraft:controls", cthulhu_node)
        controls = cthulhu_node["lovecraft:controls"]
        if isinstance(controls, list):
            control_ids = [c["@id"] for c in controls]
            self.assertIn(f"{self.LOVE_INST}Dreams", control_ids)
            self.assertIn(f"{self.LOVE_INST}Madness", control_ids)
        else:
            self.assertEqual(controls["@id"], f"{self.LOVE_INST}Dreams")

    def test_necronomicon_relations(self):
        """Testar att Necronomicon har korrekta relationer."""
        necronomicon_uri = f"{self.LOVE_INST}Necronomicon"
        necronomicon_node = self.find_node_by_id(necronomicon_uri)
        
        self.assertIsNotNone(necronomicon_node)
        
        # Necronomicon ska vara en Book
        types = necronomicon_node.get("@type", [])
        if isinstance(types, str):
            types = [types]
        self.assertIn(f"{self.LOVE}Book", types)
        
        # Necronomicon ska vara skapad av AbdulAlhazred
        self.assertIn("lovecraft:createdBy", necronomicon_node)
        self.assertEqual(necronomicon_node["lovecraft:createdBy"]["@id"], f"{self.LOVE_INST}AbdulAlhazred")

    def test_rdl_resources(self):
        """Testar att RDL-resurser skapas korrekt."""
        # RDL Resource-klass
        resource_class = self.find_node_by_id(f"{self.LOVE}Resource")
        self.assertIsNotNone(resource_class)
        self.assertEqual(resource_class["@type"], "owl:Class")
        
        # RDL egenskaper
        has_resource_type = self.find_node_by_id(f"{self.LOVE}hasResourceType")
        has_resource_value = self.find_node_by_id(f"{self.LOVE}hasResourceValue")
        
        self.assertIsNotNone(has_resource_type)
        self.assertEqual(has_resource_type["@type"], "owl:DatatypeProperty")
        
        self.assertIsNotNone(has_resource_value)
        self.assertEqual(has_resource_value["@type"], "owl:DatatypeProperty")
        
        # Necronomicon som RDL-resurs
        necronomicon_node = self.find_node_by_id(f"{self.LOVE_INST}Necronomicon")
        self.assertIsNotNone(necronomicon_node)
        
        # Kontrollera att Necronomicon har RDL-resurstyp
        # Sök efter hasResourceType i noden (kan vara full URI eller prefix)
        rdl_type = None
        for key, value in necronomicon_node.items():
            if "hasResourceType" in key:
                rdl_type = value
                break
        
        self.assertIsNotNone(rdl_type, "Necronomicon should have hasResourceType property")
        self.assertEqual(rdl_type["@value"], "Grimoire")
        
        # Kontrollera att Necronomicon har Resource-typ
        types = necronomicon_node.get("@type", [])
        if isinstance(types, str):
            types = [types]
        self.assertIn(f"{self.LOVE}Resource", types)

    def test_locations_exist(self):
        """Testar att alla förväntade platser skapas."""
        location_names = [
            "Rlyeh", "Innsmouth", "Arkham", "MiskatonicUniversity",
            "Dreamlands", "PlateauOfLeng"
        ]
        
        for location_name in location_names:
            location_uri = f"{self.LOVE_INST}{location_name}"
            location_node = self.find_node_by_id(location_uri)
            
            self.assertIsNotNone(location_node, f"Location {location_name} should exist")
            types = location_node.get("@type", [])
            if isinstance(types, str):
                types = [types]
            self.assertIn(f"{self.LOVE}Location", types)

    def test_artifacts_exist(self):
        """Testar att alla förväntade artefakter skapas."""
        artifact_names = ["Necronomicon", "ElderSign", "SilverKey", "BookOfEibon"]
        
        for artifact_name in artifact_names:
            artifact_uri = f"{self.LOVE_INST}{artifact_name}"
            artifact_node = self.find_node_by_id(artifact_uri)
            
            self.assertIsNotNone(artifact_node, f"Artifact {artifact_name} should exist")

    def test_deities_exist(self):
        """Testar att alla förväntade gudomligheter skapas."""
        deity_names = [
            "Cthulhu", "Nyarlathotep", "Azathoth", "YogSothoth",
            "Dagon", "Hastur", "ShubNiggurath", "Yig"
        ]
        
        for deity_name in deity_names:
            deity_uri = f"{self.LOVE_INST}{deity_name}"
            deity_node = self.find_node_by_id(deity_uri)
            
            self.assertIsNotNone(deity_node, f"Deity {deity_name} should exist")

    def test_creatures_exist(self):
        """Testar att alla förväntade varelser skapas."""
        creature_names = ["DeepOne", "Nightgaunt", "Shoggoth", "Ghoul"]
        
        for creature_name in creature_names:
            creature_uri = f"{self.LOVE_INST}{creature_name}"
            creature_node = self.find_node_by_id(creature_uri)
            
            self.assertIsNotNone(creature_node, f"Creature {creature_name} should exist")
            types = creature_node.get("@type", [])
            if isinstance(types, str):
                types = [types]
            self.assertIn(f"{self.LOVE}Creature", types)

    def test_humans_exist(self):
        """Testar att alla förväntade människor skapas."""
        human_names = ["HPLovecraft", "AbdulAlhazred", "RandolphCarter"]
        
        for human_name in human_names:
            human_uri = f"{self.LOVE_INST}{human_name}"
            # Hitta alla noder med detta ID (kan finnas flera om det är dubbletter)
            human_nodes = [n for n in self.graph if n.get("@id") == human_uri]
            
            self.assertGreater(len(human_nodes), 0, f"Human {human_name} should exist")
            
            # Kontrollera att åtminstone en nod har Human-typ
            has_human_type = False
            for human_node in human_nodes:
                types = human_node.get("@type", [])
                if isinstance(types, str):
                    types = [types]
                if any(t == f"{self.LOVE}Human" or t.endswith("Human") for t in types):
                    has_human_type = True
                    break
            
            self.assertTrue(has_human_type, f"Human {human_name} should have Human type")

    def test_jsonld_serialization(self):
        """Testar att dokumentet kan serialiseras till JSON."""
        json_output = json.dumps(self.doc, indent=2, ensure_ascii=False)
        
        # Verifiera att output inte är tom
        self.assertIsNotNone(json_output)
        self.assertGreater(len(json_output), 0)
        
        # Verifiera att output innehåller förväntade strängar
        self.assertIn("@context", json_output)
        self.assertIn("@graph", json_output)
        self.assertIn("Cthulhu", json_output)
        self.assertIn("Necronomicon", json_output)

    def test_save_ontology_jsonld(self):
        """Testar att ontologin kan sparas till en JSON-LD-fil."""
        with tempfile.NamedTemporaryFile(mode='w', suffix='.jsonld', delete=False) as f:
            temp_filename = f.name
        
        try:
            # Spara ontologin till en temporär fil
            save_ontology_jsonld(self.doc, temp_filename)
            
            # Verifiera att filen existerar och inte är tom
            self.assertTrue(os.path.exists(temp_filename))
            self.assertGreater(os.path.getsize(temp_filename), 0)
            
            # Läs och verifiera innehållet
            with open(temp_filename, 'r', encoding='utf-8') as f:
                content = f.read()
                loaded_doc = json.loads(content)
                self.assertIn("@context", loaded_doc)
                self.assertIn("@graph", loaded_doc)
                self.assertIn("Cthulhu", content)
                self.assertIn("Necronomicon", content)
        finally:
            # Rensa upp
            if os.path.exists(temp_filename):
                os.unlink(temp_filename)


class TestLovecraftOntologyJsonLDFileGeneration(unittest.TestCase):
    """Testklass för JSON-LD filgenerering."""

    def test_generate_files(self):
        """Testar att JSON-LD-filer genereras korrekt."""
        # Skapa ontologin
        doc = create_lovecraft_ontology_jsonld()
        
        # Spara till temporär katalog
        with tempfile.TemporaryDirectory() as temp_dir:
            jsonld_file = os.path.join(temp_dir, "test_ontology.jsonld")
            
            # Spara ontologin
            save_ontology_jsonld(doc, jsonld_file)
            
            # Verifiera att filen existerar
            self.assertTrue(os.path.exists(jsonld_file))
            
            # Läs filen och verifiera innehållet
            with open(jsonld_file, 'r', encoding='utf-8') as f:
                content = f.read()
                loaded_doc = json.loads(content)
                self.assertIn("@context", loaded_doc)
                self.assertIn("@graph", loaded_doc)
                self.assertIn("lovecraft:", content)
                self.assertIn("Cthulhu", content)
                self.assertIn("Nyarlathotep", content)
                self.assertIn("Necronomicon", content)


if __name__ == '__main__':
    unittest.main()
