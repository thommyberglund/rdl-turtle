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
        model.setNsPrefix("lovecraft", "https://lovecraft.frosteby.eu/ontology#");
        model.setNsPrefix("instance", "https://lovecraft.frosteby.eu/instance#");
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
        assertEquals("https://lovecraft.frosteby.eu/ontology#", 
            model.getNsPrefixURI("lovecraft"));
        assertEquals("https://lovecraft.frosteby.eu/instance#", 
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
        Resource ontology = model.getResource("https://lovecraft.frosteby.eu/ontology#Ontology");
        
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
            Resource cls = model.getResource("https://lovecraft.frosteby.eu/ontology#" + className);
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
        
        Resource entity = model.getResource("https://lovecraft.frosteby.eu/ontology#Entity");
        Resource deity = model.getResource("https://lovecraft.frosteby.eu/ontology#Deity");
        Resource greatOldOne = model.getResource("https://lovecraft.frosteby.eu/ontology#GreatOldOne");
        Resource outerGod = model.getResource("https://lovecraft.frosteby.eu/ontology#OuterGod");
        Resource elderGod = model.getResource("https://lovecraft.frosteby.eu/ontology#ElderGod");
        
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
            Property prop = model.getProperty("https://lovecraft.frosteby.eu/ontology#" + propName);
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
            Property prop = model.getProperty("https://lovecraft.frosteby.eu/ontology#" + propName);
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
            Resource individual = model.getResource("https://lovecraft.frosteby.eu/instance#" + individualName);
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
        
        Resource cthulhu = model.getResource("https://lovecraft.frosteby.eu/instance#Cthulhu");
        
        // Cthulhu ska vara en GreatOldOne
        Resource greatOldOne = model.getResource("https://lovecraft.frosteby.eu/ontology#GreatOldOne");
        assertTrue(cthulhu.hasProperty(RDF.type, greatOldOne));
        
        // Cthulhu ska ha ett namn
        Property hasName = model.getProperty("https://lovecraft.frosteby.eu/ontology#hasName");
        assertTrue(cthulhu.hasProperty(hasName));
        
        // Cthulhu ska ha en beskrivning
        Property hasDescription = model.getProperty("https://lovecraft.frosteby.eu/ontology#hasDescription");
        assertTrue(cthulhu.hasProperty(hasDescription));
        
        // Cthulhu ska kontrollera Dreams och Madness
        Property controls = model.getProperty("https://lovecraft.frosteby.eu/ontology#controls");
        Resource dreams = model.getResource("https://lovecraft.frosteby.eu/instance#Dreams");
        Resource madness = model.getResource("https://lovecraft.frosteby.eu/instance#Madness");
        assertTrue(cthulhu.hasProperty(controls, dreams));
        assertTrue(cthulhu.hasProperty(controls, madness));
        
        // Cthulhu ska frukta The Sun och Light
        Property fears = model.getProperty("https://lovecraft.frosteby.eu/ontology#fears");
        Resource theSun = model.getResource("https://lovecraft.frosteby.eu/instance#TheSun");
        Resource light = model.getResource("https://lovecraft.frosteby.eu/instance#Light");
        assertTrue(cthulhu.hasProperty(fears, theSun));
        assertTrue(cthulhu.hasProperty(fears, light));
    }

    /**
     * Testar att Necronomicon har korrekta relationer.
     */
    @Test
    public void testNecronomiconRelations() {
        LovecraftOntology.createOntology(model);
        
        Resource necronomicon = model.getResource("https://lovecraft.frosteby.eu/instance#Necronomicon");
        
        // Necronomicon ska vara en Book
        Resource book = model.getResource("https://lovecraft.frosteby.eu/ontology#Book");
        assertTrue(necronomicon.hasProperty(RDF.type, book));
        
        // Necronomicon ska vara skapad av AbdulAlhazred
        Property createdBy = model.getProperty("https://lovecraft.frosteby.eu/ontology#createdBy");
        Resource abdulAlhazred = model.getResource("https://lovecraft.frosteby.eu/instance#AbdulAlhazred");
        assertTrue(necronomicon.hasProperty(createdBy, abdulAlhazred));
        
        // Necronomicon ska beskriva flera entiteter
        Property describedIn = model.getProperty("https://lovecraft.frosteby.eu/ontology#describedIn");
        Resource cthulhu = model.getResource("https://lovecraft.frosteby.eu/instance#Cthulhu");
        assertTrue(cthulhu.hasProperty(describedIn, necronomicon));
    }

    /**
     * Testar att RDL-resurser skapas korrekt.
     */
    @Test
    public void testRDLResources() {
        LovecraftOntology.createOntology(model);
        
        // RDL Resource-klass
        Resource resourceClass = model.getResource("https://lovecraft.frosteby.eu/ontology#Resource");
        assertNotNull(resourceClass);
        assertTrue(resourceClass.hasProperty(RDF.type, OWL.Class));
        
        // RDL egenskaper
        Property hasResourceType = model.getProperty("https://lovecraft.frosteby.eu/ontology#hasResourceType");
        Property hasResourceValue = model.getProperty("https://lovecraft.frosteby.eu/ontology#hasResourceValue");
        
        assertNotNull(hasResourceType);
        assertNotNull(hasResourceValue);
        assertTrue(hasResourceType.hasProperty(RDF.type, OWL.DatatypeProperty));
        assertTrue(hasResourceValue.hasProperty(RDF.type, OWL.DatatypeProperty));
        
        // Necronomicon som RDL-resurs
        Resource necronomicon = model.getResource("https://lovecraft.frosteby.eu/instance#Necronomicon");
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
        
        Resource locationClass = model.getResource("https://lovecraft.frosteby.eu/ontology#Location");
        
        for (String locationName : locationNames) {
            Resource location = model.getResource("https://lovecraft.frosteby.eu/instance#" + locationName);
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
            Resource artifact = model.getResource("https://lovecraft.frosteby.eu/instance#" + artifactName);
            assertNotNull(artifact, "Artifact " + artifactName + " should exist");
        }
    }

    /**
     * Testar att URI:erna använder frosteby.eu domän.
     */
    @Test
    public void testUriDomain() {
        LovecraftOntology.createOntology(model);
        
        // Hämta en resurs och verifiera dess URI
        Resource entity = model.getResource("https://lovecraft.frosteby.eu/ontology#Entity");
        assertNotNull(entity, "Entity should exist");
        assertTrue(entity.getURI().contains("lovecraft.frosteby.eu"), 
            "URI should use frosteby.eu domain");
        
        Resource cthulhu = model.getResource("https://lovecraft.frosteby.eu/instance#Cthulhu");
        assertNotNull(cthulhu, "Cthulhu should exist");
        assertTrue(cthulhu.getURI().contains("lovecraft.frosteby.eu"), 
            "Instance URI should use frosteby.eu domain");
    }

    /**
     * Testar att gamla URI:er inte används.
     */
    @Test
    public void testOldUriNotUsed() {
        LovecraftOntology.createOntology(model);
        
        // Hämta alla resurser i modellen
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt = it.next();
            
            // Kolla subjekt
            if (stmt.getSubject().isResource()) {
                String uri = stmt.getSubject().asResource().getURI();
                if (uri != null) {
                    assertFalse(uri.contains("lovecraft.example.org"), 
                        "Should not use old example.org domain: " + uri);
                    assertFalse(uri.contains("lovecraft.example.com"), 
                        "Should not use old example.com domain: " + uri);
                }
            }
            
            // Kolla objekt
            if (stmt.getObject().isResource()) {
                String uri = stmt.getObject().asResource().getURI();
                if (uri != null) {
                    assertFalse(uri.contains("lovecraft.example.org"), 
                        "Should not use old example.org domain in object: " + uri);
                    assertFalse(uri.contains("lovecraft.example.com"), 
                        "Should not use old example.com domain in object: " + uri);
                }
            }
        }
    }
}
