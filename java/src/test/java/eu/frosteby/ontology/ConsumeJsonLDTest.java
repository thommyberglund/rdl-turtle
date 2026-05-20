package eu.frosteby.ontology;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.ontology.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.Lang;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Enhetstester för ConsumeJsonLD.
 * 
 * Dessa tester verifierar att JSON-LD-filer kan läsas och omvandlas
 * korrekt till RDF-modeller.
 */
public class ConsumeJsonLDTest {

    private static final String LOVE_NS = "https://lovecraft.frosteby.eu/ontology#";
    private static final String LOVE_INST_NS = "https://lovecraft.frosteby.eu/instance#";
    private static final String DCTERMS_NS = "http://purl.org/dc/terms/";

    @Test
    public void testLoadJsonLDFile() {
        // Först generera en JSON-LD-fil
        Model model = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(model);
        
        String filename = "test_consume.jsonld";
        try (FileOutputStream out = new FileOutputStream(filename)) {
            model.write(out, "JSON-LD");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        
        try {
            // Ladda JSON-LD-filen
            Model loadedModel = ConsumeJsonLD.loadJsonLDFile(filename);
            
            assertNotNull(loadedModel, "Loaded model should not be null");
            assertTrue(loadedModel.size() > 0, "Loaded model should not be empty");
            
        } finally {
            // Rensa upp
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                // Ignorera
            }
        }
    }

    @Test
    public void testLoadJsonLDFileWithPrefixes() {
        // Först generera en JSON-LD-fil
        Model model = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(model);
        
        String filename = "test_consume_prefixes.jsonld";
        try (FileOutputStream out = new FileOutputStream(filename)) {
            model.write(out, "JSON-LD");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        
        try {
            // Ladda JSON-LD-filen med prefix
            Model loadedModel = ConsumeJsonLD.loadJsonLDFileWithPrefixes(filename);
            
            assertNotNull(loadedModel, "Loaded model should not be null");
            assertTrue(loadedModel.size() > 0, "Loaded model should not be empty");
            
            // Verifiera att prefix finns
            assertEquals(LOVE_NS, loadedModel.getNsPrefixURI("lovecraft"));
            assertEquals(LOVE_INST_NS, loadedModel.getNsPrefixURI("instance"));
            
        } finally {
            // Rensa upp
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                // Ignorera
            }
        }
    }

    @Test
    public void testOntologyStructurePreserved() {
        // Först generera en JSON-LD-fil
        Model originalModel = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(originalModel);
        
        String filename = "test_structure.jsonld";
        try (FileOutputStream out = new FileOutputStream(filename)) {
            originalModel.write(out, "JSON-LD");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        
        try {
            // Ladda JSON-LD-filen
            Model loadedModel = ConsumeJsonLD.loadJsonLDFileWithPrefixes(filename);
            
            // Kontrollera att ontologin finns
            Resource ontology = loadedModel.createResource(LOVE_NS + "Ontology");
            assertTrue(loadedModel.contains(ontology, RDF.type, OWL.Ontology));
            
            // Kontrollera metadata
            Property titleProp = ResourceFactory.createProperty(DCTERMS_NS + "title");
            assertTrue(loadedModel.contains(ontology, titleProp, 
                loadedModel.createLiteral("Lovecraft Mythos Ontology", "en")));
            
        } finally {
            // Rensa upp
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                // Ignorera
            }
        }
    }

    @Test
    public void testClassesPreserved() {
        // Först generera en JSON-LD-fil
        Model originalModel = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(originalModel);
        
        String filename = "test_classes.jsonld";
        try (FileOutputStream out = new FileOutputStream(filename)) {
            originalModel.write(out, "JSON-LD");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        
        try {
            // Ladda JSON-LD-filen
            Model loadedModel = ConsumeJsonLD.loadJsonLDFileWithPrefixes(filename);
            
            // Kontrollera att huvudklasser finns
            String[] classNames = {
                "Entity", "Deity", "GreatOldOne", "OuterGod", "ElderGod",
                "Creature", "Human", "Location", "Artifact", "Book", "Concept"
            };
            
            for (String className : classNames) {
                Resource cls = loadedModel.createResource(LOVE_NS + className);
                assertTrue(loadedModel.contains(cls, RDF.type, OWL.Class),
                    "Class " + className + " should exist");
            }
            
        } finally {
            // Rensa upp
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                // Ignorera
            }
        }
    }

    @Test
    public void testIndividualsPreserved() {
        // Först generera en JSON-LD-fil
        Model originalModel = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(originalModel);
        
        String filename = "test_individuals.jsonld";
        try (FileOutputStream out = new FileOutputStream(filename)) {
            originalModel.write(out, "JSON-LD");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        
        try {
            // Ladda JSON-LD-filen
            Model loadedModel = ConsumeJsonLD.loadJsonLDFileWithPrefixes(filename);
            
            // Kontrollera specifika individer
            String[] individualNames = {
                "Cthulhu", "Nyarlathotep", "Azathoth", "Necronomicon", 
                "ElderSign", "Rlyeh", "Innsmouth", "HPLovecraft", "AbdulAlhazred"
            };
            
            for (String individualName : individualNames) {
                Resource individual = loadedModel.createResource(LOVE_INST_NS + individualName);
                assertTrue(loadedModel.contains(individual, RDF.type, loadedModel.createResource(OWL.NS + "NamedIndividual")),
                    "Individual " + individualName + " should exist");
            }
            
        } finally {
            // Rensa upp
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                // Ignorera
            }
        }
    }

    @Test
    public void testCthulhuPropertiesPreserved() {
        // Först generera en JSON-LD-fil
        Model originalModel = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(originalModel);
        
        String filename = "test_cthulhu.jsonld";
        try (FileOutputStream out = new FileOutputStream(filename)) {
            originalModel.write(out, "JSON-LD");
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
        }
        
        try {
            // Ladda JSON-LD-filen
            Model loadedModel = ConsumeJsonLD.loadJsonLDFileWithPrefixes(filename);
            
            Resource cthulhu = loadedModel.createResource(LOVE_INST_NS + "Cthulhu");
            Resource greatOldOne = loadedModel.createResource(LOVE_NS + "GreatOldOne");
            
            // Cthulhu ska vara en GreatOldOne
            assertTrue(loadedModel.contains(cthulhu, RDF.type, greatOldOne));
            
            // Cthulhu ska ha ett namn
            Property hasName = loadedModel.createProperty(LOVE_NS + "hasName");
            assertTrue(loadedModel.contains(cthulhu, hasName, 
                loadedModel.createLiteral("Cthulhu", "en")));
            
            // Cthulhu ska kontrollera Dreams och Madness
            Property controls = loadedModel.createProperty(LOVE_NS + "controls");
            Resource dreams = loadedModel.createResource(LOVE_INST_NS + "Dreams");
            Resource madness = loadedModel.createResource(LOVE_INST_NS + "Madness");
            assertTrue(loadedModel.contains(cthulhu, controls, dreams));
            assertTrue(loadedModel.contains(cthulhu, controls, madness));
            
        } finally {
            // Rensa upp
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                // Ignorera
            }
        }
    }

    @Test
    public void testConvertJsonLDToTurtle() throws IOException {
        // Först generera en JSON-LD-fil
        Model originalModel = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(originalModel);
        
        String jsonldFile = "test_convert.jsonld";
        String turtleFile = "test_convert.ttl";
        
        try (FileOutputStream out = new FileOutputStream(jsonldFile)) {
            originalModel.write(out, "JSON-LD");
        }
        
        try {
            // Omvandla JSON-LD till Turtle
            ConsumeJsonLD.convertJsonLDToTurtle(jsonldFile, turtleFile);
            
            // Verifiera att Turtle-filen existerar
            assertTrue(Files.exists(Paths.get(turtleFile)));
            
            // Ladda Turtle-filen och verifiera innehållet
            Model loadedModel = ModelFactory.createDefaultModel();
            loadedModel.read(turtleFile);
            
            assertTrue(loadedModel.size() > 0, "Loaded Turtle model should not be empty");
            
            // Kontrollera att ontologin finns
            Resource ontology = loadedModel.createResource(LOVE_NS + "Ontology");
            assertTrue(loadedModel.contains(ontology, RDF.type, OWL.Ontology));
            
        } finally {
            // Rensa upp
            Files.deleteIfExists(Paths.get(jsonldFile));
            Files.deleteIfExists(Paths.get(turtleFile));
        }
    }

    @Test
    public void testConvertTurtleToJsonLD() throws IOException {
        // Först generera en Turtle-fil
        Model originalModel = ModelFactory.createDefaultModel();
        LovecraftOntology.createOntology(originalModel);
        
        String turtleFile = "test_turtle_to_jsonld.ttl";
        String jsonldFile = "test_turtle_to_jsonld_output.jsonld";
        
        try (FileOutputStream out = new FileOutputStream(turtleFile)) {
            originalModel.write(out, "TURTLE");
        }
        
        try {
            // Omvandla Turtle till JSON-LD
            ConsumeJsonLD.convertTurtleToJsonLD(turtleFile, jsonldFile);
            
            // Verifiera att JSON-LD-filen existerar
            assertTrue(Files.exists(Paths.get(jsonldFile)));
            
            // Ladda JSON-LD-filen och verifiera innehållet
            Model loadedModel = ConsumeJsonLD.loadJsonLDFile(jsonldFile);
            
            assertTrue(loadedModel.size() > 0, "Loaded JSON-LD model should not be empty");
            
            // Kontrollera att ontologin finns
            Resource ontology = loadedModel.createResource(LOVE_NS + "Ontology");
            assertTrue(loadedModel.contains(ontology, RDF.type, OWL.Ontology));
            
        } finally {
            // Rensa upp
            Files.deleteIfExists(Paths.get(turtleFile));
            Files.deleteIfExists(Paths.get(jsonldFile));
        }
    }

    @Test
    public void testSaveModelAsTurtle() throws IOException {
        // Skapa en modell
        Model model = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(model);
        
        String filename = "test_save_turtle.ttl";
        
        try {
            // Spara som Turtle
            ConsumeJsonLD.saveModelAsTurtle(model, filename);
            
            // Verifiera att filen existerar
            assertTrue(Files.exists(Paths.get(filename)));
            
            // Ladda filen och verifiera innehållet
            Model loadedModel = ModelFactory.createDefaultModel();
            loadedModel.read(filename);
            
            assertTrue(loadedModel.size() > 0, "Loaded model should not be empty");
            
        } finally {
            // Rensa upp
            Files.deleteIfExists(Paths.get(filename));
        }
    }

    @Test
    public void testSaveModelAsJsonLD() throws IOException {
        // Skapa en modell
        Model model = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(model);
        
        String filename = "test_save_jsonld.jsonld";
        
        try {
            // Spara som JSON-LD
            ConsumeJsonLD.saveModelAsJsonLD(model, filename);
            
            // Verifiera att filen existerar
            assertTrue(Files.exists(Paths.get(filename)));
            
            // Ladda filen och verifiera innehållet
            Model loadedModel = ConsumeJsonLD.loadJsonLDFile(filename);
            
            assertTrue(loadedModel.size() > 0, "Loaded model should not be empty");
            
        } finally {
            // Rensa upp
            Files.deleteIfExists(Paths.get(filename));
        }
    }
}
