#!/usr/bin/env python3
"""
Enhetstester för Lovecraft Ontology (Python-implementation).

Dessa tester verifierar att ontologin skapas korrekt med alla
nödvändiga klasser, egenskaper och individer.
"""

import unittest
import os
import tempfile
from rdflib import Graph, Literal, Namespace, URIRef
from rdflib.namespace import RDF, RDFS, OWL, XSD

from lovecraft_ontology import create_lovecraft_ontology


class TestLovecraftOntology(unittest.TestCase):
    """Testklass för Lovecraft-ontologin."""

    def setUp(self):
        """Skapar en ny ontologi för varje test."""
        self.graph = create_lovecraft_ontology()
        self.LOVE = Namespace("https://lovecraft.example.org/ontology#")
        self.LOVE_INST = Namespace("https://lovecraft.example.org/instance#")

    def test_ontology_header(self):
        """Testar att ontologin har korrekt header."""
        ontology = self.LOVE.Ontology
        
        # Verifiera att ontologin finns och har korrekt typ
        self.assertTrue((ontology, RDF.type, OWL.Ontology) in self.graph)
        
        # Verifiera metadata
        self.assertTrue((ontology, URIRef("http://purl.org/dc/terms/title"), 
                        Literal("Lovecraft Mythos Ontology", lang="en")) in self.graph)
        
        self.assertTrue((ontology, URIRef("http://purl.org/dc/terms/description"), 
                        Literal("An OWL ontology describing the entities, concepts, and relationships in H.P. Lovecraft's Cthulhu Mythos.", lang="en")) in self.graph)
        
        self.assertTrue((ontology, URIRef("http://purl.org/dc/terms/creator"), 
                        Literal("Lovecraft Ontology Project", lang="en")) in self.graph)
        
        self.assertTrue((ontology, OWL.versionInfo, Literal("1.0.0")) in self.graph)

    def test_main_classes_exist(self):
        """Testar att alla huvudklasser skapas korrekt."""
        class_names = [
            "Entity", "Deity", "ElderGod", "GreatOldOne", "OuterGod",
            "Creature", "Human", "Location", "Artifact", "Book", "Concept"
        ]
        
        for class_name in class_names:
            cls = self.LOVE[class_name]
            self.assertTrue((cls, RDF.type, OWL.Class) in self.graph, 
                          f"Class {class_name} should exist")
            self.assertTrue((cls, RDFS.label, Literal(class_name, lang="en")) in self.graph, 
                          f"Class {class_name} should have label")

    def test_class_hierarchy(self):
        """Testar klasshierarkin (subClassOf-relationer)."""
        entity = self.LOVE.Entity
        deity = self.LOVE.Deity
        great_old_one = self.LOVE.GreatOldOne
        outer_god = self.LOVE.OuterGod
        elder_god = self.LOVE.ElderGod
        
        # Deity är subklass till Entity
        self.assertTrue((deity, RDFS.subClassOf, entity) in self.graph)
        
        # GreatOldOne, OuterGod, ElderGod är subklasser till Deity
        self.assertTrue((great_old_one, RDFS.subClassOf, deity) in self.graph)
        self.assertTrue((outer_god, RDFS.subClassOf, deity) in self.graph)
        self.assertTrue((elder_god, RDFS.subClassOf, deity) in self.graph)

    def test_object_properties_exist(self):
        """Testar att Object Properties skapas korrekt."""
        property_names = [
            "worships", "fears", "controls", "locatedIn", 
            "hasPower", "isPartOf", "createdBy", "describedIn"
        ]
        
        for prop_name in property_names:
            prop = self.LOVE[prop_name]
            self.assertTrue((prop, RDF.type, OWL.ObjectProperty) in self.graph, 
                          f"Property {prop_name} should exist")
            self.assertTrue((prop, RDFS.label, Literal(prop_name, lang="en")) in self.graph, 
                          f"Property {prop_name} should have label")

    def test_datatype_properties_exist(self):
        """Testar att Datatype Properties skapas korrekt."""
        property_names = ["hasName", "hasDescription", "hasOrigin", "hasAge"]
        
        for prop_name in property_names:
            prop = self.LOVE[prop_name]
            self.assertTrue((prop, RDF.type, OWL.DatatypeProperty) in self.graph, 
                          f"Datatype property {prop_name} should exist")
            self.assertTrue((prop, RDFS.label, Literal(prop_name, lang="en")) in self.graph, 
                          f"Datatype property {prop_name} should have label")

    def test_individuals_exist(self):
        """Testar att specifika individer skapas korrekt."""
        individual_names = [
            "Cthulhu", "Nyarlathotep", "Azathoth", "YogSothoth",
            "Necronomicon", "ElderSign", "Rlyeh", "Innsmouth",
            "HPLovecraft", "AbdulAlhazred"
        ]
        
        for individual_name in individual_names:
            individual = self.LOVE_INST[individual_name]
            self.assertTrue((individual, RDF.type, OWL.NamedIndividual) in self.graph, 
                          f"Individual {individual_name} should exist")

    def test_cthulhu_properties(self):
        """Testar att Cthulhu har korrekta egenskaper."""
        cthulhu = self.LOVE_INST.Cthulhu
        
        # Cthulhu ska vara en GreatOldOne
        self.assertTrue((cthulhu, RDF.type, self.LOVE.GreatOldOne) in self.graph)
        
        # Cthulhu ska ha ett namn
        self.assertTrue((cthulhu, self.LOVE.hasName, Literal("Cthulhu", lang="en")) in self.graph)
        
        # Cthulhu ska ha en beskrivning
        self.assertTrue((cthulhu, self.LOVE.hasDescription, 
                        Literal("The high priest of the Great Old Ones, sleeping in R'lyeh", lang="en")) in self.graph)
        
        # Cthulhu ska kontrollera Dreams och Madness
        self.assertTrue((cthulhu, self.LOVE.controls, self.LOVE_INST.Dreams) in self.graph)
        self.assertTrue((cthulhu, self.LOVE.controls, self.LOVE_INST.Madness) in self.graph)
        
        # Cthulhu ska frukta The Sun och Light
        self.assertTrue((cthulhu, self.LOVE.fears, self.LOVE_INST.TheSun) in self.graph)
        self.assertTrue((cthulhu, self.LOVE.fears, self.LOVE_INST.Light) in self.graph)

    def test_necronomicon_relations(self):
        """Testar att Necronomicon har korrekta relationer."""
        necronomicon = self.LOVE_INST.Necronomicon
        
        # Necronomicon ska vara en Book
        self.assertTrue((necronomicon, RDF.type, self.LOVE.Book) in self.graph)
        
        # Necronomicon ska vara skapad av AbdulAlhazred
        self.assertTrue((necronomicon, self.LOVE.createdBy, self.LOVE_INST.AbdulAlhazred) in self.graph)
        
        # Necronomicon ska beskriva flera entiteter
        self.assertTrue((self.LOVE_INST.Cthulhu, self.LOVE.describedIn, necronomicon) in self.graph)
        self.assertTrue((self.LOVE_INST.Nyarlathotep, self.LOVE.describedIn, necronomicon) in self.graph)

    def test_rdl_resources(self):
        """Testar att RDL-resurser skapas korrekt."""
        # RDL Resource-klass
        resource_class = self.LOVE.Resource
        self.assertTrue((resource_class, RDF.type, OWL.Class) in self.graph)
        
        # RDL egenskaper
        has_resource_type = self.LOVE.hasResourceType
        has_resource_value = self.LOVE.hasResourceValue
        
        self.assertTrue((has_resource_type, RDF.type, OWL.DatatypeProperty) in self.graph)
        self.assertTrue((has_resource_value, RDF.type, OWL.DatatypeProperty) in self.graph)
        
        # Necronomicon som RDL-resurs
        necronomicon = self.LOVE_INST.Necronomicon
        self.assertTrue((necronomicon, RDF.type, resource_class) in self.graph)
        self.assertTrue((necronomicon, has_resource_type, Literal("Grimoire", lang="en")) in self.graph)
        self.assertTrue((necronomicon, has_resource_value, 
                        Literal("The most dangerous book in existence", lang="en")) in self.graph)

    def test_locations_exist(self):
        """Testar att alla förväntade platser skapas."""
        location_names = [
            "Rlyeh", "Innsmouth", "Arkham", "MiskatonicUniversity",
            "Dreamlands", "PlateauOfLeng"
        ]
        
        for location_name in location_names:
            location = self.LOVE_INST[location_name]
            self.assertTrue((location, RDF.type, self.LOVE.Location) in self.graph, 
                          f"Location {location_name} should exist")

    def test_artifacts_exist(self):
        """Testar att alla förväntade artefakter skapas."""
        artifact_names = ["Necronomicon", "ElderSign", "SilverKey", "BookOfEibon"]
        
        for artifact_name in artifact_names:
            artifact = self.LOVE_INST[artifact_name]
            self.assertTrue((artifact, RDF.type, OWL.NamedIndividual) in self.graph, 
                          f"Artifact {artifact_name} should exist")

    def test_deities_exist(self):
        """Testar att alla förväntade gudomligheter skapas."""
        deity_names = [
            "Cthulhu", "Nyarlathotep", "Azathoth", "YogSothoth",
            "Dagon", "Hastur", "ShubNiggurath", "Yig"
        ]
        
        for deity_name in deity_names:
            deity = self.LOVE_INST[deity_name]
            self.assertTrue((deity, RDF.type, OWL.NamedIndividual) in self.graph, 
                          f"Deity {deity_name} should exist")

    def test_creatures_exist(self):
        """Testar att alla förväntade varelser skapas."""
        creature_names = ["DeepOne", "Nightgaunt", "Shoggoth", "Ghoul"]
        
        for creature_name in creature_names:
            creature = self.LOVE_INST[creature_name]
            self.assertTrue((creature, RDF.type, self.LOVE.Creature) in self.graph, 
                          f"Creature {creature_name} should exist")

    def test_humans_exist(self):
        """Testar att alla förväntade människor skapas."""
        human_names = ["HPLovecraft", "AbdulAlhazred", "RandolphCarter"]
        
        for human_name in human_names:
            human = self.LOVE_INST[human_name]
            self.assertTrue((human, RDF.type, self.LOVE.Human) in self.graph, 
                          f"Human {human_name} should exist")

    def test_turtle_serialization(self):
        """Testar att grafen kan serialiseras till Turtle-format."""
        turtle_output = self.graph.serialize(format="turtle")
        
        if isinstance(turtle_output, bytes):
            turtle_output = turtle_output.decode("utf-8")
        
        # Verifiera att output inte är tom
        self.assertIsNotNone(turtle_output)
        self.assertGreater(len(turtle_output), 0)
        
        # Verifiera att output innehåller förväntade strängar
        self.assertIn("@prefix lovecraft:", turtle_output)
        self.assertIn("@prefix instance:", turtle_output)
        self.assertIn("Cthulhu", turtle_output)
        self.assertIn("Necronomicon", turtle_output)

    def test_save_ontology(self):
        """Testar att ontologin kan sparas till en fil."""
        with tempfile.NamedTemporaryFile(mode='w', suffix='.ttl', delete=False) as f:
            temp_filename = f.name
        
        try:
            # Spara ontologin till en temporär fil
            with open(temp_filename, 'w', encoding='utf-8') as f:
                turtle_data = self.graph.serialize(format="turtle")
                if isinstance(turtle_data, bytes):
                    turtle_data = turtle_data.decode("utf-8")
                f.write(turtle_data)
            
            # Verifiera att filen existerar och inte är tom
            self.assertTrue(os.path.exists(temp_filename))
            self.assertGreater(os.path.getsize(temp_filename), 0)
            
            # Läs och verifiera innehållet
            with open(temp_filename, 'r', encoding='utf-8') as f:
                content = f.read()
                self.assertIn("Cthulhu", content)
                self.assertIn("Necronomicon", content)
        finally:
            # Rensa upp
            if os.path.exists(temp_filename):
                os.unlink(temp_filename)


class TestLovecraftOntologyFileGeneration(unittest.TestCase):
    """Testklass för filgenerering."""

    def test_generate_files(self):
        """Testar att filer genereras korrekt."""
        # Skapa ontologin
        graph = create_lovecraft_ontology()
        
        # Spara till temporära filer
        with tempfile.TemporaryDirectory() as temp_dir:
            ttl_file = os.path.join(temp_dir, "test_ontology.ttl")
            
            # Spara ontologin
            turtle_data = graph.serialize(format="turtle")
            if isinstance(turtle_data, bytes):
                turtle_data = turtle_data.decode("utf-8")
            
            with open(ttl_file, 'w', encoding='utf-8') as f:
                f.write(turtle_data)
            
            # Verifiera att filen existerar
            self.assertTrue(os.path.exists(ttl_file))
            
            # Läs filen och verifiera innehållet
            with open(ttl_file, 'r', encoding='utf-8') as f:
                content = f.read()
                self.assertIn("@prefix lovecraft:", content)
                self.assertIn("Cthulhu", content)
                self.assertIn("Nyarlathotep", content)
                self.assertIn("Necronomicon", content)


if __name__ == '__main__':
    unittest.main()
