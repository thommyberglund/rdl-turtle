package org.lovecraft.ontology;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.ontology.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Enhetstester för LovecraftOntology-klassen.
 * 
 * Dessa tester verifierar att ontologin skapas korrekt med alla
 * nödvändiga klasser, egenskaper och individer.
 */
public class LovecraftOntologyTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        // Skapa en ny modell för varje test
        model = ModelFactory.createDefaultModel();
        
        // Lägg till namnrymder
        model.setNsPrefix("lovecraft", "https://lovecraft.example.org/ontology#");
        model.setNsPrefix("instance", "https://lovecraft.example.org/instance#");
        model.setNsPrefix("owl", OWL.NS);
        model.setNsPrefix("rdf", RDF.uri);
        model.setNsPrefix("rdfs", RDFS.uri);
        model.setNsPrefix("xsd", XSD.NS);
    }

    @AfterEach
    public void tearDown() {
        if (model != null) {
            model.close();
        }
    }

    /**
     * Testar att ontologin skapas med korrekta namnrymder.
     */
    @Test
    public void testOntologyNamespacePrefixes() {
        // Anropa metoden som skapar ontologin
        LovecraftOntology.createOntology(model);
        
        // Verifiera att namnrymder är korrekt inställda
        assertEquals("https://lovecraft.example.org/ontology#", 
            model.getNsPrefixURI("lovecraft"));
        assertEquals("https://lovecraft.example.org/instance#", 
            model.getNsPrefixURI("instance"));
        assertEquals(OWL.NS, model.getNsPrefixURI("owl"));
        assertEquals(RDF.uri, model.getNsPrefixURI("rdf"));
        assertEquals(RDFS.uri, model.getNsPrefixURI("rdfs"));
    }

    /**
     * Testar att OWL-ontologin skapas med korrekt metadata.
     */
    @Test
    public void testOntologyMetadata() {
        LovecraftOntology.createOntology(model);
        
        // Hämta ontologin
        Resource ontology = model.getResource("https://lovecraft.example.org/ontology#Ontology");
        
        // Verifiera att ontologin finns och har korrekt typ
        assertNotNull(ontology);
        assertTrue(ontology.hasProperty(RDF.type, OWL.Ontology));
        
        // Verifiera metadata
        Property titleProp = model.getProperty("http://purl.org/dc/terms/title");
        assertTrue(ontology.hasProperty(titleProp));
        
        Property descProp = model.getProperty("http://purl.org/dc/terms/description");
        assertTrue(ontology.hasProperty(descProp));
        
        Property creatorProp = model.getProperty("http://purl.org/dc/terms/creator");
        assertTrue(ontology.hasProperty(creatorProp));
        
        assertTrue(ontology.hasProperty(OWL.versionInfo));
    }

    /**
     * Testar att alla huvudklasser skapas korrekt.
     */
    @Test
    public void testMainClassesExist() {
        LovecraftOntology.createOntology(model);
        
        String[] classNames = {
            "Entity", "Deity", "ElderGod", "GreatOldOne", "OuterGod",
            "Creature", "Human", "Location", "Artifact", "Book", "Concept"
        };
        
        for (String className : classNames) {
            Resource cls = model.getResource("https://lovecraft.example.org/ontology#" + className);
            assertNotNull(cls, "Class " + className + " should exist");
            assertTrue(cls.hasProperty(RDF.type, OWL.Class), 
                className + " should be an OWL Class");
        }
    }

    /**
     * Testar klasshierarkin (subClassOf-relationer).
     */
    @Test
    public void testClassHierarchy() {
        LovecraftOntology.createOntology(model);
        
        Resource entity = model.getResource("https://lovecraft.example.org/ontology#Entity");
        Resource deity = model.getResource("https://lovecraft.example.org/ontology#Deity");
        Resource greatOldOne = model.getResource("https://lovecraft.example.org/ontology#GreatOldOne");
        Resource outerGod = model.getResource("https://lovecraft.example.org/ontology#OuterGod");
        Resource elderGod = model.getResource("https://lovecraft.example.org/ontology#ElderGod");
        
        // Deity är subklass till Entity
        assertTrue(deity.hasProperty(RDFS.subClassOf, entity));
        
        // GreatOldOne, OuterGod, ElderGod är subklasser till Deity
        assertTrue(greatOldOne.hasProperty(RDFS.subClassOf, deity));
        assertTrue(outerGod.hasProperty(RDFS.subClassOf, deity));
        assertTrue(elderGod.hasProperty(RDFS.subClassOf, deity));
    }

    /**
     * Testar att Object Properties skapas korrekt.
     */
    @Test
    public void testObjectPropertiesExist() {
        LovecraftOntology.createOntology(model);
        
        String[] propertyNames = {
            "worships", "fears", "controls", "locatedIn", 
            "hasPower", "isPartOf", "createdBy", "describedIn"
        };
        
        for (String propName : propertyNames) {
            Property prop = model.getProperty("https://lovecraft.example.org/ontology#" + propName);
            assertNotNull(prop, "Property " + propName + " should exist");
            assertTrue(prop.hasProperty(RDF.type, OWL.ObjectProperty), 
                propName + " should be an ObjectProperty");
        }
    }

    /**
     * Testar att Datatype Properties skapas korrekt.
     */
    @Test
    public void testDatatypePropertiesExist() {
        LovecraftOntology.createOntology(model);
        
        String[] propertyNames = {
            "hasName", "hasDescription", "hasOrigin", "hasAge"
        };
        
        for (String propName : propertyNames) {
            Property prop = model.getProperty("https://lovecraft.example.org/ontology#" + propName);
            assertNotNull(prop, "Datatype property " + propName + " should exist");
            assertTrue(prop.hasProperty(RDF.type, OWL.DatatypeProperty), 
                propName + " should be a DatatypeProperty");
        }
    }

    /**
     * Testar att specifika individer skapas korrekt.
     */
    @Test
    public void testIndividualsExist() {
        LovecraftOntology.createOntology(model);
        
        // Testar några viktiga individer
        String[] individualNames = {
            "Cthulhu", "Nyarlathotep", "Azathoth", "YogSothoth",
            "Necronomicon", "ElderSign", "Rlyeh", "Innsmouth",
            "HPLovecraft", "AbdulAlhazred"
        };
        
        for (String individualName : individualNames) {
            Resource individual = model.getResource("https://lovecraft.example.org/instance#" + individualName);
            assertNotNull(individual, "Individual " + individualName + " should exist");
            assertTrue(individual.hasProperty(RDF.type, model.createResource(OWL.NS + "NamedIndividual")), 
                individualName + " should be a NamedIndividual");
        }
    }

    /**
     * Testar att Cthulhu har korrekta egenskaper.
     */
    @Test
    public void testCthulhuProperties() {
        LovecraftOntology.createOntology(model);
        
        Resource cthulhu = model.getResource("https://lovecraft.example.org/instance#Cthulhu");
        
        // Cthulhu ska vara en GreatOldOne
        Resource greatOldOne = model.getResource("https://lovecraft.example.org/ontology#GreatOldOne");
        assertTrue(cthulhu.hasProperty(RDF.type, greatOldOne));
        
        // Cthulhu ska ha ett namn
        Property hasName = model.getProperty("https://lovecraft.example.org/ontology#hasName");
        assertTrue(cthulhu.hasProperty(hasName));
        
        // Cthulhu ska ha en beskrivning
        Property hasDescription = model.getProperty("https://lovecraft.example.org/ontology#hasDescription");
        assertTrue(cthulhu.hasProperty(hasDescription));
        
        // Cthulhu ska kontrollera Dreams och Madness
        Property controls = model.getProperty("https://lovecraft.example.org/ontology#controls");
        Resource dreams = model.getResource("https://lovecraft.example.org/instance#Dreams");
        Resource madness = model.getResource("https://lovecraft.example.org/instance#Madness");
        assertTrue(cthulhu.hasProperty(controls, dreams));
        assertTrue(cthulhu.hasProperty(controls, madness));
        
        // Cthulhu ska frukta The Sun och Light
        Property fears = model.getProperty("https://lovecraft.example.org/ontology#fears");
        Resource theSun = model.getResource("https://lovecraft.example.org/instance#TheSun");
        Resource light = model.getResource("https://lovecraft.example.org/instance#Light");
        assertTrue(cthulhu.hasProperty(fears, theSun));
        assertTrue(cthulhu.hasProperty(fears, light));
    }

    /**
     * Testar att Necronomicon har korrekta relationer.
     */
    @Test
    public void testNecronomiconRelations() {
        LovecraftOntology.createOntology(model);
        
        Resource necronomicon = model.getResource("https://lovecraft.example.org/instance#Necronomicon");
        
        // Necronomicon ska vara en Book
        Resource book = model.getResource("https://lovecraft.example.org/ontology#Book");
        assertTrue(necronomicon.hasProperty(RDF.type, book));
        
        // Necronomicon ska vara skapad av AbdulAlhazred
        Property createdBy = model.getProperty("https://lovecraft.example.org/ontology#createdBy");
        Resource abdulAlhazred = model.getResource("https://lovecraft.example.org/instance#AbdulAlhazred");
        assertTrue(necronomicon.hasProperty(createdBy, abdulAlhazred));
        
        // Necronomicon ska beskriva flera entiteter
        Property describedIn = model.getProperty("https://lovecraft.example.org/ontology#describedIn");
        Resource cthulhu = model.getResource("https://lovecraft.example.org/instance#Cthulhu");
        assertTrue(cthulhu.hasProperty(describedIn, necronomicon));
    }

    /**
     * Testar att RDL-resurser skapas korrekt.
     */
    @Test
    public void testRDLResources() {
        LovecraftOntology.createOntology(model);
        
        // RDL Resource-klass
        Resource resourceClass = model.getResource("https://lovecraft.example.org/ontology#Resource");
        assertNotNull(resourceClass);
        assertTrue(resourceClass.hasProperty(RDF.type, OWL.Class));
        
        // RDL egenskaper
        Property hasResourceType = model.getProperty("https://lovecraft.example.org/ontology#hasResourceType");
        Property hasResourceValue = model.getProperty("https://lovecraft.example.org/ontology#hasResourceValue");
        
        assertNotNull(hasResourceType);
        assertNotNull(hasResourceValue);
        assertTrue(hasResourceType.hasProperty(RDF.type, OWL.DatatypeProperty));
        assertTrue(hasResourceValue.hasProperty(RDF.type, OWL.DatatypeProperty));
        
        // Necronomicon som RDL-resurs
        Resource necronomicon = model.getResource("https://lovecraft.example.org/instance#Necronomicon");
        assertTrue(necronomicon.hasProperty(RDF.type, resourceClass));
        assertTrue(necronomicon.hasProperty(hasResourceType));
        assertTrue(necronomicon.hasProperty(hasResourceValue));
    }

    /**
     * Testar att modellen kan serialiseras till Turtle-format.
     */
    @Test
    public void testModelSerialization() {
        LovecraftOntology.createOntology(model);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            model.write(out, "TURTLE");
            String turtleOutput = out.toString("UTF-8");
            
            // Verifiera att output inte är tom
            assertNotNull(turtleOutput);
            assertTrue(turtleOutput.length() > 0);
            
            // Verifiera att output innehåller förväntade strängar
            assertTrue(turtleOutput.contains("@prefix lovecraft:"));
            assertTrue(turtleOutput.contains("@prefix instance:"));
            assertTrue(turtleOutput.contains("Cthulhu"));
            assertTrue(turtleOutput.contains("Necronomicon"));
            
        } catch (IOException e) {
            fail("Failed to serialize model: " + e.getMessage());
        }
    }

    /**
     * Testar att alla förväntade platser skapas.
     */
    @Test
    public void testLocationsExist() {
        LovecraftOntology.createOntology(model);
        
        String[] locationNames = {
            "Rlyeh", "Innsmouth", "Arkham", "MiskatonicUniversity",
            "Dreamlands", "PlateauOfLeng"
        };
        
        Resource locationClass = model.getResource("https://lovecraft.example.org/ontology#Location");
        
        for (String locationName : locationNames) {
            Resource location = model.getResource("https://lovecraft.example.org/instance#" + locationName);
            assertNotNull(location, "Location " + locationName + " should exist");
            assertTrue(location.hasProperty(RDF.type, locationClass), 
                locationName + " should be a Location");
        }
    }

    /**
     * Testar att alla förväntade artefakter skapas.
     */
    @Test
    public void testArtifactsExist() {
        LovecraftOntology.createOntology(model);
        
        String[] artifactNames = {
            "Necronomicon", "ElderSign", "SilverKey", "BookOfEibon"
        };
        
        for (String artifactName : artifactNames) {
            Resource artifact = model.getResource("https://lovecraft.example.org/instance#" + artifactName);
            assertNotNull(artifact, "Artifact " + artifactName + " should exist");
        }
    }
}
