package eu.frosteby.ontology;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.ontology.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.Lang;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Enhetstester för LovecraftOntologyJsonLD.
 * 
 * Dessa tester verifierar att JSON-LD-ontologin skapas korrekt.
 */
public class LovecraftOntologyJsonLDTest {

    private static final String LOVE_NS = "https://lovecraft.frosteby.eu/ontology#";
    private static final String LOVE_INST_NS = "https://lovecraft.frosteby.eu/instance#";
    private static final String DCTERMS_NS = "http://purl.org/dc/terms/";

    private Model model;

    @BeforeEach
    public void setUp() {
        // Skapa en ny modell för varje test
        model = ModelFactory.createDefaultModel();
        LovecraftOntologyJsonLD.createOntology(model);
    }

    @Test
    public void testOntologyHeader() {
        Resource ontology = model.createResource(LOVE_NS + "Ontology");
        
        // Verifiera att ontologin finns och har korrekt typ
        assertTrue(model.contains(ontology, RDF.type, OWL.Ontology));
        
        // Verifiera metadata
        Property titleProp = ResourceFactory.createProperty(DCTERMS_NS + "title");
        assertTrue(model.contains(ontology, titleProp, 
            model.createLiteral("Lovecraft Mythos Ontology", "en")));
        
        Property descProp = ResourceFactory.createProperty(DCTERMS_NS + "description");
        assertTrue(model.contains(ontology, descProp, 
            model.createLiteral("An OWL ontology describing the entities, concepts, and relationships in H.P. Lovecraft's Cthulhu Mythos.", "en")));
        
        Property creatorProp = ResourceFactory.createProperty(DCTERMS_NS + "creator");
        assertTrue(model.contains(ontology, creatorProp, 
            model.createLiteral("Lovecraft Ontology Project", "en")));
        
        assertTrue(model.contains(ontology, OWL.versionInfo, 
            model.createLiteral("1.0.0")));
    }

    @Test
    public void testMainClassesExist() {
        String[] classNames = {
            "Entity", "Deity", "ElderGod", "GreatOldOne", "OuterGod",
            "Creature", "Human", "Location", "Artifact", "Book", "Concept"
        };
        
        for (String className : classNames) {
            Resource cls = model.createResource(LOVE_NS + className);
            assertTrue(model.contains(cls, RDF.type, OWL.Class),
                "Class " + className + " should exist");
            assertTrue(model.contains(cls, RDFS.label, 
                model.createLiteral(className, "en")),
                "Class " + className + " should have label");
        }
    }

    @Test
    public void testClassHierarchy() {
        Resource entity = model.createResource(LOVE_NS + "Entity");
        Resource deity = model.createResource(LOVE_NS + "Deity");
        Resource greatOldOne = model.createResource(LOVE_NS + "GreatOldOne");
        Resource outerGod = model.createResource(LOVE_NS + "OuterGod");
        Resource elderGod = model.createResource(LOVE_NS + "ElderGod");
        
        // Deity är subklass till Entity
        assertTrue(model.contains(deity, RDFS.subClassOf, entity));
        
        // GreatOldOne, OuterGod, ElderGod är subklasser till Deity
        assertTrue(model.contains(greatOldOne, RDFS.subClassOf, deity));
        assertTrue(model.contains(outerGod, RDFS.subClassOf, deity));
        assertTrue(model.contains(elderGod, RDFS.subClassOf, deity));
    }

    @Test
    public void testObjectPropertiesExist() {
        String[] propertyNames = {
            "worships", "fears", "controls", "locatedIn",
            "hasPower", "isPartOf", "createdBy", "describedIn"
        };
        
        for (String propName : propertyNames) {
            Property prop = model.createProperty(LOVE_NS + propName);
            assertTrue(model.contains(prop, RDF.type, OWL.ObjectProperty),
                "Property " + propName + " should exist");
        }
    }

    @Test
    public void testDatatypePropertiesExist() {
        String[] propertyNames = {"hasName", "hasDescription", "hasOrigin", "hasAge"};
        
        for (String propName : propertyNames) {
            Property prop = model.createProperty(LOVE_NS + propName);
            assertTrue(model.contains(prop, RDF.type, OWL.DatatypeProperty),
                "Datatype property " + propName + " should exist");
        }
    }

    @Test
    public void testIndividualsExist() {
        String[] individualNames = {
            "Cthulhu", "Nyarlathotep", "Azathoth", "YogSothoth",
            "Necronomicon", "ElderSign", "Rlyeh", "Innsmouth",
            "HPLovecraft", "AbdulAlhazred"
        };
        
        for (String individualName : individualNames) {
            Resource individual = model.createResource(LOVE_INST_NS + individualName);
            Resource namedIndividual = model.createResource(OWL.NS + "NamedIndividual");
            assertTrue(model.contains(individual, RDF.type, namedIndividual),
                "Individual " + individualName + " should exist");
        }
    }

    @Test
    public void testCthulhuProperties() {
        Resource cthulhu = model.createResource(LOVE_INST_NS + "Cthulhu");
        Resource greatOldOne = model.createResource(LOVE_NS + "GreatOldOne");
        
        // Cthulhu ska vara en GreatOldOne
        assertTrue(model.contains(cthulhu, RDF.type, greatOldOne));
        
        // Cthulhu ska ha ett namn
        Property hasName = model.createProperty(LOVE_NS + "hasName");
        assertTrue(model.contains(cthulhu, hasName, model.createLiteral("Cthulhu", "en")));
        
        // Cthulhu ska kontrollera Dreams och Madness
        Property controls = model.createProperty(LOVE_NS + "controls");
        Resource dreams = model.createResource(LOVE_INST_NS + "Dreams");
        Resource madness = model.createResource(LOVE_INST_NS + "Madness");
        assertTrue(model.contains(cthulhu, controls, dreams));
        assertTrue(model.contains(cthulhu, controls, madness));
    }

    @Test
    public void testNecronomiconRelations() {
        Resource necronomicon = model.createResource(LOVE_INST_NS + "Necronomicon");
        Resource book = model.createResource(LOVE_NS + "Book");
        Resource abdulAlhazred = model.createResource(LOVE_INST_NS + "AbdulAlhazred");
        
        // Necronomicon ska vara en Book
        assertTrue(model.contains(necronomicon, RDF.type, book));
        
        // Necronomicon ska vara skapad av AbdulAlhazred
        Property createdBy = model.createProperty(LOVE_NS + "createdBy");
        assertTrue(model.contains(necronomicon, createdBy, abdulAlhazred));
    }

    @Test
    public void testRdlResources() {
        Resource resourceClass = model.createResource(LOVE_NS + "Resource");
        Property hasResourceType = model.createProperty(LOVE_NS + "hasResourceType");
        Property hasResourceValue = model.createProperty(LOVE_NS + "hasResourceValue");
        
        // RDL Resource-klass
        assertTrue(model.contains(resourceClass, RDF.type, OWL.Class));
        
        // RDL egenskaper
        assertTrue(model.contains(hasResourceType, RDF.type, OWL.DatatypeProperty));
        assertTrue(model.contains(hasResourceValue, RDF.type, OWL.DatatypeProperty));
        
        // Necronomicon som RDL-resurs
        Resource necronomicon = model.createResource(LOVE_INST_NS + "Necronomicon");
        assertTrue(model.contains(necronomicon, RDF.type, resourceClass));
        assertTrue(model.contains(necronomicon, hasResourceType, 
            model.createLiteral("Grimoire", "en")));
    }

    @Test
    public void testModelNotEmpty() {
        assertTrue(model.size() > 0, "Model should not be empty");
    }

    @Test
    public void testSaveAndLoadJsonLD() throws IOException {
        String filename = "test_ontology.jsonld";
        
        try {
            // Spara modellen som JSON-LD
            try (FileOutputStream out = new FileOutputStream(filename)) {
                RDFDataMgr.write(out, model, Lang.JSONLD);
            }
            
            // Verifiera att filen existerar
            assertTrue(Files.exists(Paths.get(filename)));
            
            // Ladda filen och verifiera innehållet
            Model loadedModel = ModelFactory.createDefaultModel();
            try (FileInputStream in = new FileInputStream(filename)) {
                RDFDataMgr.read(loadedModel, in, null, Lang.JSONLD);
            }
            
            // Verifiera att den laddade modellen har samma storlek
            assertEquals(model.size(), loadedModel.size());
            
        } finally {
            // Rensa upp
            Files.deleteIfExists(Paths.get(filename));
        }
    }
}
