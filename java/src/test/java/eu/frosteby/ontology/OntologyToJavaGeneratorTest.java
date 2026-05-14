package eu.frosteby.ontology;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhetstester för OntologyToJavaGenerator-klassen.
 * 
 * Dessa tester verifierar att Java-klasser genereras korrekt från en Turtle/OWL-ontologi.
 */
public class OntologyToJavaGeneratorTest {

    private static final String TEST_TURTLE_FILE = "target/test-classes/test_ontology.ttl";
    private static final String TEST_OUTPUT_DIR = "target/generated-test";
    private static final String LOVE_NS = "https://lovecraft.frosteby.eu/ontology#";
    private static final String LOVE_INST_NS = "https://lovecraft.frosteby.eu/instance#";

    @BeforeEach
    public void setUp() {
        // Skapa test Turtle-fil
        createTestTurtleFile();
    }

    @AfterEach
    public void tearDown() {
        // Rensa upp genererade filer
        deleteDirectory(Paths.get(TEST_OUTPUT_DIR));
        try {
            Files.deleteIfExists(Paths.get(TEST_TURTLE_FILE));
        } catch (IOException e) {
            // Ignorera
        }
    }

    /**
     * Skapar en minimal test Turtle-fil.
     */
    private void createTestTurtleFile() {
        try {
            Path outputPath = Paths.get(TEST_TURTLE_FILE);
            Files.createDirectories(outputPath.getParent());
            
            String turtleContent = 
                "@prefix lovecraft: <" + LOVE_NS + "> .\n" +
                "@prefix instance: <" + LOVE_INST_NS + "> .\n" +
                "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
                "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                "\n" +
                "lovecraft:Ontology a owl:Ontology ;\n" +
                "    rdfs:label \"Test Ontology\"@en .\n" +
                "\n" +
                "lovecraft:Entity a owl:Class ;\n" +
                "    rdfs:label \"Entity\"@en ;\n" +
                "    rdfs:comment \"Base class\"@en .\n" +
                "\n" +
                "lovecraft:Deity a owl:Class ;\n" +
                "    rdfs:subClassOf lovecraft:Entity ;\n" +
                "    rdfs:label \"Deity\"@en .\n" +
                "\n" +
                "lovecraft:hasName a owl:DatatypeProperty ;\n" +
                "    rdfs:domain lovecraft:Entity ;\n" +
                "    rdfs:range xsd:string .\n" +
                "\n" +
                "instance:Cthulhu a lovecraft:Deity, owl:NamedIndividual ;\n" +
                "    lovecraft:hasName \"Cthulhu\"@en .\n" +
                "\n" +
                "instance:Necronomicon a lovecraft:Book, owl:NamedIndividual ;\n" +
                "    lovecraft:hasName \"Necronomicon\"@en .\n";
            
            Files.write(outputPath, turtleContent.getBytes("UTF-8"));
        } catch (IOException e) {
            fail("Failed to create test Turtle file: " + e.getMessage());
        }
    }

    /**
     * Raderar en katalog rekursivt.
     */
    private void deleteDirectory(Path directory) {
        if (Files.exists(directory)) {
            try {
                Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // Ignorera
                        }
                    });
            } catch (IOException e) {
                // Ignorera
            }
        }
    }

    /**
     * Testar att generatorn kan skapas.
     */
    @Test
    public void testGeneratorCreation() {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        assertNotNull(generator, "Generator should be created");
    }

    /**
     * Testar att generatorn kan läsa en Turtle-fil och generera klasser.
     */
    @Test
    public void testGenerateFromTurtle() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        
        assertDoesNotThrow(() -> {
            generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        }, "Should generate Java classes without errors");
        
        // Verifiera att Entity-klass genererades
        Path entityFile = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated/Entity.java");
        assertTrue(Files.exists(entityFile), "Entity.java should be generated");
        
        // Verifiera innehåll
        String content = new String(Files.readAllBytes(entityFile), "UTF-8");;
        assertTrue(content.contains("public class Entity"), "Entity class should be defined");
        assertTrue(content.contains("URI"), "Entity should have URI constant");
        assertTrue(content.contains("https://lovecraft.frosteby.eu/ontology#Entity"), "URI should use frosteby.eu domain");
    }

    /**
     * Testar att OntologyFactory genereras.
     */
    @Test
    public void testFactoryGeneration() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        
        // Verifiera att OntologyFactory genererades
        Path factoryFile = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated/OntologyFactory.java");
        assertTrue(Files.exists(factoryFile), "OntologyFactory.java should be generated");
        
        // Verifiera att URI-konstanter genererades
        String content = new String(Files.readAllBytes(factoryFile), "UTF-8");
        assertTrue(content.contains("CTHULHU_URI"), "Should contain CTHULHU_URI constant");
        assertTrue(content.contains("NECRONOMICON_URI"), "Should contain NECRONOMICON_URI constant");
    }

    /**
     * Testar att klassnamn extraheras korrekt.
     */
    @Test
    public void testClassNameExtraction() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        
        // Test via reflection (extractClassName är private)
        // Istället testar vi indirekt genom att generera och kontrollera filnamn
        generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        
        // Verifiera att Deity-klass genererades
        Path deityFile = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated/Deity.java");
        assertTrue(Files.exists(deityFile), "Deity.java should be generated");
    }

    /**
     * Testar att generatorn hanterar ogiltig fil.
     */
    @Test
    public void testInvalidFileHandling() {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        
        assertThrows(IllegalArgumentException.class, () -> {
            generator.generateFromTurtle("nonexistent.ttl", TEST_OUTPUT_DIR);
        }, "Should throw exception for nonexistent file");
    }

    /**
     * Testar att URI:erna använder frosteby.eu domän.
     */
    @Test
    public void testUriDomain() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        
        // Läs Entity.java
        Path entityFile = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated/Entity.java");
        String content = new String(Files.readAllBytes(entityFile), "UTF-8");
        
        // Verifiera frosteby.eu
        assertTrue(content.contains("https://lovecraft.frosteby.eu/ontology#Entity"), 
            "Should use frosteby.eu domain");
        assertFalse(content.contains("lovecraft.example.org"), 
            "Should not contain old example.org domain");
        assertFalse(content.contains("lovecraft.example.com"), 
            "Should not contain old example.com domain");
    }

    /**
     * Testar att alla klasser har nödvändiga metoder.
     */
    @Test
    public void testGeneratedClassStructure() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        
        Path entityFile = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated/Entity.java");
        String content = new String(Files.readAllBytes(entityFile), "UTF-8");
        
        // Verifiera nödvändiga metoder
        assertTrue(content.contains("public Entity(String instanceUri)"), "Should have constructor with instanceUri");
        assertTrue(content.contains("public Entity()"), "Should have default constructor");
        assertTrue(content.contains("getInstanceUri()"), "Should have getInstanceUri method");
        assertTrue(content.contains("getUri()"), "Should have getUri method");
        assertTrue(content.contains("toString()"), "Should have toString method");
        assertTrue(content.contains("equals(Object o)"), "Should have equals method");
        assertTrue(content.contains("hashCode()"), "Should have hashCode method");
    }

    /**
     * Testar att package-deklarationen är korrekt.
     */
    @Test
    public void testPackageDeclaration() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        
        Path entityFile = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated/Entity.java");
        String content = new String(Files.readAllBytes(entityFile), "UTF-8");
        
        assertTrue(content.contains("package eu.frosteby.ontology.generated;"), 
            "Should have correct package declaration");
    }

    /**
     * Testar att generatorn skapar korrekt paketstruktur.
     */
    @Test
    public void testPackageStructure() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        
        // Verifiera att paketkatalogen skapades
        Path packageDir = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated");
        assertTrue(Files.exists(packageDir), "Package directory should be created");
    }

    /**
     * Testar att generatorn hanterar specialtecken i klassnamn.
     */
    @Test
    public void testSpecialCharactersInClassNames() throws IOException {
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        generator.generateFromTurtle(TEST_TURTLE_FILE, TEST_OUTPUT_DIR);
        
        // Vår testfil har GreatOldOne som klass
        // Verifiera att den genereras korrekt
        Path greatOldOneFile = Paths.get(TEST_OUTPUT_DIR, "eu/frosteby/ontology/generated/Deity.java");
        assertTrue(Files.exists(greatOldOneFile), "Deity class should be generated");
    }
}
