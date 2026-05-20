#!/usr/bin/env python3
"""
Enhetstester för JSON-LD Consumer.

Dessa tester verifierar att JSON-LD-filer kan läsas och omvandlas
korrekt till RDF-grafer.
"""

import unittest
import os
import tempfile
from rdflib import Graph, Literal, Namespace, URIRef
from rdflib.namespace import RDF, RDFS, OWL, XSD

from consume_jsonld import (
    load_jsonld_file,
    jsonld_to_rdf_graph,
    consume_jsonld,
    save_as_turtle,
    expand_uri
)
from lovecraft_ontology import create_lovecraft_ontology


class TestConsumeJsonLD(unittest.TestCase):
    """Testklass för JSON-LD-konsumtion."""

    def setUp(self):
        """Skapar testdata för varje test."""
        self.LOVE = Namespace("https://lovecraft.frosteby.eu/ontology#")
        self.LOVE_INST = Namespace("https://lovecraft.frosteby.eu/instance#")
        self.DCTERMS = Namespace("http://purl.org/dc/terms/")
        
        # Skapa en referens-graf från Turtle-implementeringen
        self.reference_graph = create_lovecraft_ontology()

    def test_load_jsonld_file(self):
        """Testar att JSON-LD-filer kan laddas."""
        # Använd den befintliga JSON-LD-filen
        doc = load_jsonld_file("lovecraft_mythos.jsonld")
        
        self.assertIsNotNone(doc)
        self.assertIn("@context", doc)
        self.assertIn("@graph", doc)

    def test_jsonld_to_rdf_graph(self):
        """Testar omvandling från JSON-LD till RDF-graf."""
        # Ladda JSON-LD-fil
        doc = load_jsonld_file("lovecraft_mythos.jsonld")
        
        # Omvandla till RDF-graf
        graph = jsonld_to_rdf_graph(doc)
        
        self.assertIsNotNone(graph)
        self.assertIsInstance(graph, Graph)
        self.assertGreater(len(graph), 0)

    def test_consume_jsonld(self):
        """Testar fullständig konsumtion av JSON-LD-fil."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        self.assertIsNotNone(graph)
        self.assertIsInstance(graph, Graph)
        self.assertGreater(len(graph), 0)

    def test_ontology_structure_preserved(self):
        """Testar att ontologins struktur bevaras vid omvandling."""
        # Ladda JSON-LD och omvandla
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        # Kontrollera att ontologin finns
        ontology = self.LOVE.Ontology
        self.assertTrue((ontology, RDF.type, OWL.Ontology) in graph)
        
        # Kontrollera metadata
        self.assertTrue((ontology, self.DCTERMS.title, 
                        Literal("Lovecraft Mythos Ontology", lang="en")) in graph)

    def test_classes_preserved(self):
        """Testar att klasser bevaras korrekt."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        # Kontrollera att huvudklasser finns
        classes = ["Entity", "Deity", "GreatOldOne", "OuterGod", "ElderGod",
                  "Creature", "Human", "Location", "Artifact", "Book", "Concept"]
        
        for class_name in classes:
            cls = self.LOVE[class_name]
            self.assertTrue((cls, RDF.type, OWL.Class) in graph,
                          f"Class {class_name} should exist")

    def test_class_hierarchy_preserved(self):
        """Testar att klasshierarkin bevaras."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        # Deity är subklass till Entity
        self.assertTrue((self.LOVE.Deity, RDFS.subClassOf, self.LOVE.Entity) in graph)
        
        # GreatOldOne, OuterGod, ElderGod är subklasser till Deity
        self.assertTrue((self.LOVE.GreatOldOne, RDFS.subClassOf, self.LOVE.Deity) in graph)
        self.assertTrue((self.LOVE.OuterGod, RDFS.subClassOf, self.LOVE.Deity) in graph)
        self.assertTrue((self.LOVE.ElderGod, RDFS.subClassOf, self.LOVE.Deity) in graph)

    def test_properties_preserved(self):
        """Testar att egenskaper bevaras korrekt."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        # Kontrollera Object Properties
        object_props = ["worships", "fears", "controls", "locatedIn", 
                       "hasPower", "isPartOf", "createdBy", "describedIn"]
        
        for prop_name in object_props:
            prop = self.LOVE[prop_name]
            self.assertTrue((prop, RDF.type, OWL.ObjectProperty) in graph,
                          f"Object property {prop_name} should exist")
        
        # Kontrollera Datatype Properties
        datatype_props = ["hasName", "hasDescription", "hasOrigin", "hasAge"]
        
        for prop_name in datatype_props:
            prop = self.LOVE[prop_name]
            self.assertTrue((prop, RDF.type, OWL.DatatypeProperty) in graph,
                          f"Datatype property {prop_name} should exist")

    def test_individuals_preserved(self):
        """Testar att individer bevaras korrekt."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        # Kontrollera specifika individer
        individuals = ["Cthulhu", "Nyarlathotep", "Azathoth", "Necronomicon", 
                      "ElderSign", "Rlyeh", "Innsmouth", "HPLovecraft", "AbdulAlhazred"]
        
        for individual_name in individuals:
            individual = self.LOVE_INST[individual_name]
            self.assertTrue((individual, RDF.type, OWL.NamedIndividual) in graph,
                          f"Individual {individual_name} should exist")

    def test_cthulhu_properties_preserved(self):
        """Testar att Cthulhus egenskaper bevaras."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        cthulhu = self.LOVE_INST.Cthulhu
        
        # Cthulhu ska vara en GreatOldOne
        self.assertTrue((cthulhu, RDF.type, self.LOVE.GreatOldOne) in graph)
        
        # Cthulhu ska ha namn och beskrivning
        self.assertTrue((cthulhu, self.LOVE.hasName, 
                        Literal("Cthulhu", lang="en")) in graph)
        
        # Cthulhu ska kontrollera Dreams och Madness
        self.assertTrue((cthulhu, self.LOVE.controls, self.LOVE_INST.Dreams) in graph)
        self.assertTrue((cthulhu, self.LOVE.controls, self.LOVE_INST.Madness) in graph)

    def test_necronomicon_relations_preserved(self):
        """Testar att Necronomicons relationer bevaras."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        necronomicon = self.LOVE_INST.Necronomicon
        
        # Necronomicon ska vara en Book
        self.assertTrue((necronomicon, RDF.type, self.LOVE.Book) in graph)
        
        # Necronomicon ska vara skapad av AbdulAlhazred
        self.assertTrue((necronomicon, self.LOVE.createdBy, 
                        self.LOVE_INST.AbdulAlhazred) in graph)

    def test_rdl_resources_preserved(self):
        """Testar att RDL-resurser bevaras."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        # RDL Resource-klass
        self.assertTrue((self.LOVE.Resource, RDF.type, OWL.Class) in graph)
        
        # RDL egenskaper
        self.assertTrue((self.LOVE.hasResourceType, RDF.type, OWL.DatatypeProperty) in graph)
        self.assertTrue((self.LOVE.hasResourceValue, RDF.type, OWL.DatatypeProperty) in graph)

    def test_save_as_turtle(self):
        """Testar att grafen kan sparas som Turtle."""
        graph = consume_jsonld("lovecraft_mythos.jsonld")
        
        with tempfile.NamedTemporaryFile(mode='w', suffix='.ttl', delete=False) as f:
            temp_filename = f.name
        
        try:
            # Spara som Turtle
            save_as_turtle(graph, temp_filename)
            
            # Verifiera att filen existerar
            self.assertTrue(os.path.exists(temp_filename))
            self.assertGreater(os.path.getsize(temp_filename), 0)
            
            # Läs och verifiera innehållet
            with open(temp_filename, 'r', encoding='utf-8') as f:
                content = f.read()
                self.assertIn("Cthulhu", content)
                self.assertIn("Necronomicon", content)
        finally:
            if os.path.exists(temp_filename):
                os.unlink(temp_filename)

    def test_expand_uri(self):
        """Testar URI-expansion med kontext."""
        context = {
            "lovecraft": "https://lovecraft.frosteby.eu/ontology#",
            "instance": "https://lovecraft.frosteby.eu/instance#",
            "owl": "http://www.w3.org/2002/07/owl#",
        }
        
        # Test full URI
        self.assertEqual(
            expand_uri("http://example.com/test", context),
            "http://example.com/test"
        )
        
        # Test kompakt URI
        self.assertEqual(
            expand_uri("lovecraft:Entity", context),
            "https://lovecraft.frosteby.eu/ontology#Entity"
        )
        
        self.assertEqual(
            expand_uri("owl:Class", context),
            "http://www.w3.org/2002/07/owl#Class"
        )
        
        # Test okänt prefix
        self.assertEqual(
            expand_uri("unknown:test", context),
            "unknown:test"
        )

    def test_roundtrip_conversion(self):
        """Testar att omvandling JSON-LD -> RDF -> Turtle bevarar data."""
        # Ladda JSON-LD
        graph_from_jsonld = consume_jsonld("lovecraft_mythos.jsonld")
        
        # Jämför med referens-grafen (från Turtle)
        # Båda bör ha samma grundläggande struktur
        
        # Kontrollera att antalet klasser är liknande
        jsonld_classes = len(list(graph_from_jsonld.subjects(RDF.type, OWL.Class)))
        ref_classes = len(list(self.reference_graph.subjects(RDF.type, OWL.Class)))
        self.assertEqual(jsonld_classes, ref_classes)
        
        # Kontrollera att antalet individer är liknande
        jsonld_individuals = len(list(graph_from_jsonld.subjects(RDF.type, OWL.NamedIndividual)))
        ref_individuals = len(list(self.reference_graph.subjects(RDF.type, OWL.NamedIndividual)))
        self.assertEqual(jsonld_individuals, ref_individuals)


class TestConsumeJsonLDFileGeneration(unittest.TestCase):
    """Testklass för filgenerering och konsumtion."""

    def test_full_workflow(self):
        """Testar fullständig workflow: generera JSON-LD -> konsumera -> spara som Turtle."""
        # Skapa en temporär JSON-LD-fil
        with tempfile.TemporaryDirectory() as temp_dir:
            # Generera JSON-LD-fil
            from lovecraft_ontology_jsonld import create_lovecraft_ontology_jsonld, save_ontology_jsonld
            doc = create_lovecraft_ontology_jsonld()
            jsonld_file = os.path.join(temp_dir, "test.jsonld")
            save_ontology_jsonld(doc, jsonld_file)
            
            # Konsumera JSON-LD
            graph = consume_jsonld(jsonld_file)
            
            # Spara som Turtle
            turtle_file = os.path.join(temp_dir, "test.ttl")
            save_as_turtle(graph, turtle_file)
            
            # Verifiera att båda filerna existerar
            self.assertTrue(os.path.exists(jsonld_file))
            self.assertTrue(os.path.exists(turtle_file))
            
            # Verifiera att Turtle-filen innehåller förväntad data
            with open(turtle_file, 'r', encoding='utf-8') as f:
                content = f.read()
                self.assertIn("Cthulhu", content)
                self.assertIn("Necronomicon", content)


if __name__ == '__main__':
    unittest.main()
