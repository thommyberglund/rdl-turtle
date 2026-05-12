package org.lovecraft.ontology;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.ontology.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Lovecraft Mythos Ontology: Referensimplementation av RDL och OWL i Turtle-format.
 * 
 * Detta program genererar en ontologi baserad på H.P. Lovecrafts mytologi
 * med hjälp av Apache Jena för att skapa RDF/OWL i Turtle-syntax.
 */
public class LovecraftOntology {

    // Namnrymder
    private static final String LOVE_NS = "https://lovecraft.example.org/ontology#";
    private static final String LOVE_INST_NS = "https://lovecraft.example.org/instance#";
    private static final String DCTERMS_NS = "http://purl.org/dc/terms/";
    private static final String SKOS_NS = "http://www.w3.org/2004/02/skos/core#";
    private static final String FOAF_NS = "http://xmlns.com/foaf/0.1/";

    public static void main(String[] args) {
        System.out.println("Creating Lovecraft Mythos Ontology in Turtle format (Java)...");
        
        // Skapa en ny RDF-modell
        Model model = ModelFactory.createDefaultModel();
        
        // Lägg till namnrymder för prefix
        model.setNsPrefix("lovecraft", LOVE_NS);
        model.setNsPrefix("instance", LOVE_INST_NS);
        model.setNsPrefix("owl", OWL.NS);
        model.setNsPrefix("rdf", RDF.uri);
        model.setNsPrefix("rdfs", RDFS.uri);
        model.setNsPrefix("xsd", XSD.NS);
        model.setNsPrefix("dcterms", DCTERMS_NS);
        model.setNsPrefix("skos", SKOS_NS);
        model.setNsPrefix("foaf", FOAF_NS);

        // Skapa ontologin
        createOntology(model);
        
        // Spara till Turtle-filer
        saveModel(model, "lovecraft_mythos.ttl");
        saveModel(model, "lovecraft_mythos_simple.ttl");
        
        System.out.println("Done! The ontology has been generated in Turtle format.");
    }

    /**
     * Skapar ontologin med klasser, egenskaper och individer.
     */
    public static void createOntology(Model model) {
        // --- OWL Ontology Header ---
        Resource ontology = model.createResource(LOVE_NS + "Ontology");
        ontology.addProperty(RDF.type, OWL.Ontology);
        ontology.addProperty(
            ResourceFactory.createProperty(DCTERMS_NS + "title"), 
            model.createLiteral("Lovecraft Mythos Ontology", "en"));
        ontology.addProperty(
            ResourceFactory.createProperty(DCTERMS_NS + "description"), 
            model.createLiteral("An OWL ontology describing the entities, concepts, and relationships " +
                               "in H.P. Lovecraft's Cthulhu Mythos.", "en"));
        ontology.addProperty(
            ResourceFactory.createProperty(DCTERMS_NS + "creator"), 
            model.createLiteral("Lovecraft Ontology Project", "en"));
        ontology.addProperty(
            ResourceFactory.createProperty(DCTERMS_NS + "date"), 
            model.createTypedLiteral("2024-01-01", XSD.NS + "date"));
        ontology.addProperty(OWL.versionInfo, 
            model.createLiteral("1.0.0"));

        // --- Klasser (OWL Classes) ---
        Map<String, String> classes = new HashMap<>();
        classes.put("Entity", "Base class for all Lovecraftian entities");
        classes.put("Deity", "A cosmic deity or god-like being");
        classes.put("ElderGod", "Benevolent or neutral cosmic entities");
        classes.put("GreatOldOne", "Powerful ancient beings, often malevolent");
        classes.put("OuterGod", "Entities from outside the universe");
        classes.put("Creature", "Non-divine beings from the mythos");
        classes.put("Human", "Human characters in the mythos");
        classes.put("Location", "Places in the Lovecraft universe");
        classes.put("Artifact", "Magical or powerful objects");
        classes.put("Book", "Forbidden tomes and manuscripts");
        classes.put("Concept", "Abstract concepts in the mythos");

        for (Map.Entry<String, String> entry : classes.entrySet()) {
            String className = entry.getKey();
            String description = entry.getValue();
            Resource cls = model.createResource(LOVE_NS + className);
            cls.addProperty(RDF.type, OWL.Class);
            cls.addProperty(RDFS.label, model.createLiteral(className, "en"));
            cls.addProperty(RDFS.comment, model.createLiteral(description, "en"));
            
            // Hierarki: alla klasser är subklasser till Entity (utom Entity själv)
            if (!className.equals("Entity")) {
                cls.addProperty(RDFS.subClassOf, model.createResource(LOVE_NS + "Entity"));
            }
        }

        // Specifika subklasser
        Resource greatOldOne = model.createResource(LOVE_NS + "GreatOldOne");
        greatOldOne.addProperty(RDFS.subClassOf, model.createResource(LOVE_NS + "Deity"));
        
        Resource outerGod = model.createResource(LOVE_NS + "OuterGod");
        outerGod.addProperty(RDFS.subClassOf, model.createResource(LOVE_NS + "Deity"));
        
        Resource elderGod = model.createResource(LOVE_NS + "ElderGod");
        elderGod.addProperty(RDFS.subClassOf, model.createResource(LOVE_NS + "Deity"));

        // --- Egenskaper (OWL Properties) ---
        Map<String, Map<String, String>> properties = new HashMap<>();
        
        // Object Properties
        Map<String, String> worships = new HashMap<>();
        worships.put("label", "worships");
        worships.put("comment", "Relationship between a being and a deity they worship");
        worships.put("domain", LOVE_NS + "Entity");
        worships.put("range", LOVE_NS + "Deity");
        properties.put("worships", worships);

        Map<String, String> fears = new HashMap<>();
        fears.put("label", "fears");
        fears.put("comment", "Relationship between a being and what they fear");
        fears.put("domain", LOVE_NS + "Entity");
        fears.put("range", LOVE_NS + "Entity");
        properties.put("fears", fears);

        Map<String, String> controls = new HashMap<>();
        controls.put("label", "controls");
        controls.put("comment", "Relationship between a deity and what they control");
        controls.put("domain", LOVE_NS + "Deity");
        controls.put("range", LOVE_NS + "Entity");
        properties.put("controls", controls);

        Map<String, String> locatedIn = new HashMap<>();
        locatedIn.put("label", "located in");
        locatedIn.put("comment", "Physical location of an entity or artifact");
        locatedIn.put("domain", LOVE_NS + "Entity");
        locatedIn.put("range", LOVE_NS + "Location");
        properties.put("locatedIn", locatedIn);

        Map<String, String> hasPower = new HashMap<>();
        hasPower.put("label", "has power");
        hasPower.put("comment", "The powers or abilities of an entity");
        hasPower.put("domain", LOVE_NS + "Entity");
        hasPower.put("range", XSD.NS + "string");
        properties.put("hasPower", hasPower);

        Map<String, String> isPartOf = new HashMap<>();
        isPartOf.put("label", "is part of");
        isPartOf.put("comment", "Part-whole relationship");
        isPartOf.put("domain", LOVE_NS + "Entity");
        isPartOf.put("range", LOVE_NS + "Entity");
        properties.put("isPartOf", isPartOf);

        Map<String, String> createdBy = new HashMap<>();
        createdBy.put("label", "created by");
        createdBy.put("comment", "Relationship between an artifact and its creator");
        createdBy.put("domain", LOVE_NS + "Artifact");
        createdBy.put("range", LOVE_NS + "Entity");
        properties.put("createdBy", createdBy);

        Map<String, String> describedIn = new HashMap<>();
        describedIn.put("label", "described in");
        describedIn.put("comment", "Relationship between an entity and a book that describes it");
        describedIn.put("domain", LOVE_NS + "Entity");
        describedIn.put("range", LOVE_NS + "Book");
        properties.put("describedIn", describedIn);

        // Skapa Object Properties
        for (Map.Entry<String, Map<String, String>> entry : properties.entrySet()) {
            String propName = entry.getKey();
            Map<String, String> propInfo = entry.getValue();
            Property prop = model.createProperty(LOVE_NS + propName);
            prop.addProperty(RDF.type, OWL.ObjectProperty);
            prop.addProperty(RDFS.label, model.createLiteral(propInfo.get("label"), "en"));
            prop.addProperty(RDFS.comment, model.createLiteral(propInfo.get("comment"), "en"));
            prop.addProperty(RDFS.domain, model.createResource(propInfo.get("domain")));
            prop.addProperty(RDFS.range, model.createResource(propInfo.get("range")));
        }

        // --- Datatype Properties ---
        Map<String, String> datatypeProperties = new HashMap<>();
        datatypeProperties.put("hasName", "The name of an entity");
        datatypeProperties.put("hasDescription", "A description of an entity");
        datatypeProperties.put("hasOrigin", "The origin or place of creation");
        datatypeProperties.put("hasAge", "The age of an entity (in years)");

        for (Map.Entry<String, String> entry : datatypeProperties.entrySet()) {
            String propName = entry.getKey();
            String description = entry.getValue();
            Property prop = model.createProperty(LOVE_NS + propName);
            prop.addProperty(RDF.type, OWL.DatatypeProperty);
            prop.addProperty(RDFS.label, model.createLiteral(propName, "en"));
            prop.addProperty(RDFS.comment, model.createLiteral(description, "en"));
            prop.addProperty(RDFS.domain, model.createResource(LOVE_NS + "Entity"));
            prop.addProperty(RDFS.range, XSD.NS + "string");
        }

        // --- Individer (OWL Individuals) ---
        
        // Gudomligheter (Deities)
        createDeity(model, "Cthulhu", LOVE_NS + "GreatOldOne", 
            "The high priest of the Great Old Ones, sleeping in R'lyeh",
            new String[]{"The Sun", "Light"},
            new String[]{"Dreams", "Madness"},
            new String[]{"Cult of Cthulhu"});

        createDeity(model, "Nyarlathotep", LOVE_NS + "OuterGod",
            "The crawling chaos, messenger of the Outer Gods",
            new String[]{},
            new String[]{"Time", "Space", "Madness"},
            new String[]{"Cult of the Black Pharaoh"});

        createDeity(model, "Azathoth", LOVE_NS + "OuterGod",
            "The blind idiot god at the center of the universe",
            new String[]{},
            new String[]{"Reality", "Time", "Space"},
            new String[]{"Outer God Cults"});

        createDeity(model, "YogSothoth", LOVE_NS + "OuterGod",
            "The all-in-one and one-in-all, key and gate of the universe",
            new String[]{},
            new String[]{"Knowledge", "Time", "Space"},
            new String[]{"Witch Cult"});

        createDeity(model, "Dagon", LOVE_NS + "GreatOldOne",
            "God of the Deep Ones",
            new String[]{"Land Dwellers"},
            new String[]{"The Sea", "Deep Ones"},
            new String[]{"Deep Ones", "Essex Cult"});

        createDeity(model, "Hastur", LOVE_NS + "GreatOldOne",
            "The King in Yellow, associated with madness and art",
            new String[]{"Reality"},
            new String[]{"Madness", "Theatre", "Art"},
            new String[]{"Cult of Hastur"});

        createDeity(model, "ShubNiggurath", LOVE_NS + "GreatOldOne",
            "The black goat of the woods with a thousand young",
            new String[]{"Purity"},
            new String[]{"Fertility", "Nature", "Darkness"},
            new String[]{"Cult of the Goat"});

        createDeity(model, "Yig", LOVE_NS + "ElderGod",
            "The serpent god, father of all snakes",
            new String[]{"Snake Hatred"},
            new String[]{"Snakes", "Venom"},
            new String[]{"Snake Cults"});

        // --- Varelser (Creatures) ---
        createCreature(model, "DeepOne", LOVE_NS + "Creature",
            "Fish-frog hybrids that serve Dagon",
            "Dagon", "The Sea", "Light", null);

        createCreature(model, "Nightgaunt", LOVE_NS + "Creature",
            "Faceless, winged creatures that carry victims to unknown fates",
            null, "The Night", "Light", null);

        createCreature(model, "Shoggoth", LOVE_NS + "Creature",
            "Amorphous, intelligent protoplasmic masses",
            null, null, "Elder Sign", "Shape-shifting");

        createCreature(model, "Ghoul", LOVE_NS + "Creature",
            "Subterranean humanoids that feed on corpses",
            null, "Dreamlands", "Fire", null);

        // --- Platser (Locations) ---
        createLocation(model, "Rlyeh", 
            "The sunken city where Cthulhu dreams", "The Pacific Ocean");
        createLocation(model, "Innsmouth", 
            "A decaying coastal town inhabited by Deep One hybrids", "Massachusetts");
        createLocation(model, "Arkham", 
            "A fictional town in Massachusetts, home to Miskatonic University", "Massachusetts");
        createLocation(model, "MiskatonicUniversity", 
            "A prestigious university with a dark history", "Arkham");
        createLocation(model, "Dreamlands", 
            "A parallel dimension accessible through dreams", "The Astral Plane");
        createLocation(model, "PlateauOfLeng", 
            "A high plateau in Central Asia, home to ancient horrors", "Asia");

        // --- Artefakter (Artifacts) ---
        createArtifact(model, "Necronomicon", LOVE_NS + "Book",
            "The mad Arab Abdul Alhazred's infamous grimoire",
            "AbdulAlhazred",
            new String[]{"Cthulhu", "Nyarlathotep", "YogSothoth", "ShubNiggurath"});

        createArtifact(model, "ElderSign", LOVE_NS + "Artifact",
            "A protective symbol against the Great Old Ones",
            "ElderGods", new String[]{});

        createArtifact(model, "SilverKey", LOVE_NS + "Artifact",
            "A key that can open gates to other dimensions",
            "YogSothoth", new String[]{});

        createArtifact(model, "BookOfEibon", LOVE_NS + "Book",
            "A grimoire of Hyperborean origin",
            "Eibon", new String[]{"Tsathoggua", "UbboSathla"});

        // --- Människor (Humans) ---
        createHuman(model, "HPLovecraft",
            "Howard Phillips Lovecraft, the creator of the mythos",
            new String[]{"The Unknown", "Madness"}, "Providence");

        createHuman(model, "AbdulAlhazred",
            "The mad Arab, author of the Necronomicon",
            new String[]{"His Own Creations"}, "Damascus");

        createHuman(model, "RandolphCarter",
            "A recurring character in Lovecraft's stories, explorer of the Dreamlands",
            new String[]{"The Unknown"}, "Arkham");

        // --- RDL-specifika element ---
        // RDL Class for "Resource"
        Resource resourceClass = model.createResource(LOVE_NS + "Resource");
        resourceClass.addProperty(RDF.type, OWL.Class);
        resourceClass.addProperty(RDFS.label, model.createLiteral("Resource", "en"));
        resourceClass.addProperty(RDFS.comment, 
            model.createLiteral("A generic resource in the Lovecraft universe", "en"));

        // RDL Property for "hasResourceType"
        Property hasResourceType = model.createProperty(LOVE_NS + "hasResourceType");
        hasResourceType.addProperty(RDF.type, OWL.DatatypeProperty);
        hasResourceType.addProperty(RDFS.label, model.createLiteral("has resource type", "en"));
        hasResourceType.addProperty(RDFS.comment, 
            model.createLiteral("The type of a resource", "en"));
        hasResourceType.addProperty(RDFS.domain, resourceClass);
        hasResourceType.addProperty(RDFS.range, XSD.NS + "string");

        // RDL Property for "hasResourceValue"
        Property hasResourceValue = model.createProperty(LOVE_NS + "hasResourceValue");
        hasResourceValue.addProperty(RDF.type, OWL.DatatypeProperty);
        hasResourceValue.addProperty(RDFS.label, model.createLiteral("has resource value", "en"));
        hasResourceValue.addProperty(RDFS.comment, 
            model.createLiteral("The value of a resource", "en"));
        hasResourceValue.addProperty(RDFS.domain, resourceClass);
        hasResourceValue.addProperty(RDFS.range, XSD.NS + "string");

        // Exempel på RDL-resurser
        Resource necronomicon = model.createResource(LOVE_INST_NS + "Necronomicon");
        necronomicon.addProperty(RDF.type, resourceClass);
        necronomicon.addProperty(hasResourceType, model.createLiteral("Grimoire", "en"));
        necronomicon.addProperty(hasResourceValue, 
            model.createLiteral("The most dangerous book in existence", "en"));

        Resource elderSign = model.createResource(LOVE_INST_NS + "ElderSign");
        elderSign.addProperty(RDF.type, resourceClass);
        elderSign.addProperty(hasResourceType, model.createLiteral("Symbol", "en"));
        elderSign.addProperty(hasResourceValue, 
            model.createLiteral("A protective sigil against evil", "en"));
    }

    /**
     * Skapar en gudom (Deity) med relationer.
     */
    private static void createDeity(Model model, String name, String typeUri, 
                                   String description, String[] fears, 
                                   String[] controls, String[] worshippedBy) {
        Resource deity = model.createResource(LOVE_INST_NS + name);
        deity.addProperty(RDF.type, model.createResource(typeUri));
        deity.addProperty(RDF.type, model.createResource(OWL.NS + "NamedIndividual"));
        deity.addProperty(model.createProperty(LOVE_NS + "hasName"), 
            model.createLiteral(name, "en"));
        deity.addProperty(model.createProperty(LOVE_NS + "hasDescription"), 
            model.createLiteral(description, "en"));

        // Lägg till fears
        Property fearsProp = model.createProperty(LOVE_NS + "fears");
        for (String fear : fears) {
            Resource fearResource = model.createResource(LOVE_INST_NS + fear.replace(" ", ""));
            fearResource.addProperty(RDF.type, model.createResource(LOVE_NS + "Concept"));
            deity.addProperty(fearsProp, fearResource);
        }

        // Lägg till controls
        Property controlsProp = model.createProperty(LOVE_NS + "controls");
        for (String control : controls) {
            Resource controlResource = model.createResource(LOVE_INST_NS + control.replace(" ", ""));
            controlResource.addProperty(RDF.type, model.createResource(LOVE_NS + "Concept"));
            deity.addProperty(controlsProp, controlResource);
        }

        // Lägg till worshipped_by
        Property worshipsProp = model.createProperty(LOVE_NS + "worships");
        for (String worshiper : worshippedBy) {
            Resource worshiperResource = model.createResource(LOVE_INST_NS + worshiper.replace(" ", ""));
            worshiperResource.addProperty(RDF.type, model.createResource(LOVE_NS + "Creature"));
            worshiperResource.addProperty(worshipsProp, deity);
        }
    }

    /**
     * Skapar en varelse (Creature) med relationer.
     */
    private static void createCreature(Model model, String name, String typeUri,
                                      String description, String worships, 
                                      String locatedIn, String fears, String controls) {
        Resource creature = model.createResource(LOVE_INST_NS + name);
        creature.addProperty(RDF.type, model.createResource(typeUri));
        creature.addProperty(RDF.type, model.createResource(OWL.NS + "NamedIndividual"));
        creature.addProperty(model.createProperty(LOVE_NS + "hasName"), 
            model.createLiteral(name, "en"));
        creature.addProperty(model.createProperty(LOVE_NS + "hasDescription"), 
            model.createLiteral(description, "en"));

        Property worshipsProp = model.createProperty(LOVE_NS + "worships");
        Property locatedInProp = model.createProperty(LOVE_NS + "locatedIn");
        Property fearsProp = model.createProperty(LOVE_NS + "fears");
        Property controlsProp = model.createProperty(LOVE_NS + "controls");

        if (worships != null) {
            creature.addProperty(worshipsProp, 
                model.createResource(LOVE_INST_NS + worships));
        }

        if (locatedIn != null) {
            Resource location = model.createResource(LOVE_INST_NS + locatedIn.replace(" ", ""));
            location.addProperty(RDF.type, model.createResource(LOVE_NS + "Location"));
            creature.addProperty(locatedInProp, location);
        }

        if (fears != null) {
            Resource fearResource = model.createResource(LOVE_INST_NS + fears.replace(" ", ""));
            fearResource.addProperty(RDF.type, model.createResource(LOVE_NS + "Concept"));
            creature.addProperty(fearsProp, fearResource);
        }

        if (controls != null) {
            Resource controlResource = model.createResource(LOVE_INST_NS + controls.replace(" ", ""));
            controlResource.addProperty(RDF.type, model.createResource(LOVE_NS + "Concept"));
            creature.addProperty(controlsProp, controlResource);
        }
    }

    /**
     * Skapar en plats (Location) med relationer.
     */
    private static void createLocation(Model model, String name, 
                                       String description, String locatedIn) {
        Resource location = model.createResource(LOVE_INST_NS + name);
        location.addProperty(RDF.type, model.createResource(LOVE_NS + "Location"));
        location.addProperty(RDF.type, model.createResource(OWL.NS + "NamedIndividual"));
        location.addProperty(model.createProperty(LOVE_NS + "hasName"), 
            model.createLiteral(name, "en"));
        location.addProperty(model.createProperty(LOVE_NS + "hasDescription"), 
            model.createLiteral(description, "en"));

        if (locatedIn != null) {
            Property isPartOfProp = model.createProperty(LOVE_NS + "isPartOf");
            Resource parentLocation = model.createResource(LOVE_INST_NS + locatedIn.replace(" ", ""));
            parentLocation.addProperty(RDF.type, model.createResource(LOVE_NS + "Location"));
            location.addProperty(isPartOfProp, parentLocation);
        }
    }

    /**
     * Skapar en artefakt (Artifact) med relationer.
     */
    private static void createArtifact(Model model, String name, String typeUri,
                                      String description, String createdBy, 
                                      String[] describes) {
        Resource artifact = model.createResource(LOVE_INST_NS + name);
        artifact.addProperty(RDF.type, model.createResource(typeUri));
        artifact.addProperty(RDF.type, model.createResource(OWL.NS + "NamedIndividual"));
        artifact.addProperty(model.createProperty(LOVE_NS + "hasName"), 
            model.createLiteral(name, "en"));
        artifact.addProperty(model.createProperty(LOVE_NS + "hasDescription"), 
            model.createLiteral(description, "en"));

        Property createdByProp = model.createProperty(LOVE_NS + "createdBy");
        Property describedInProp = model.createProperty(LOVE_NS + "describedIn");

        if (createdBy != null) {
            Resource creator = model.createResource(LOVE_INST_NS + createdBy);
            creator.addProperty(RDF.type, model.createResource(LOVE_NS + "Entity"));
            artifact.addProperty(createdByProp, creator);
        }

        for (String entity : describes) {
            Resource entityResource = model.createResource(LOVE_INST_NS + entity);
            entityResource.addProperty(describedInProp, artifact);
        }
    }

    /**
     * Skapar en människa (Human) med relationer.
     */
    private static void createHuman(Model model, String name, 
                                    String description, String[] fears, 
                                    String locatedIn) {
        Resource human = model.createResource(LOVE_INST_NS + name);
        human.addProperty(RDF.type, model.createResource(LOVE_NS + "Human"));
        human.addProperty(RDF.type, model.createResource(OWL.NS + "NamedIndividual"));
        
        // Fix för H.P. Lovecraft
        String displayName = name.equals("HPLovecraft") ? "H.P.Lovecraft" : name;
        human.addProperty(model.createProperty(LOVE_NS + "hasName"), 
            model.createLiteral(displayName, "en"));
        human.addProperty(model.createProperty(LOVE_NS + "hasDescription"), 
            model.createLiteral(description, "en"));

        Property fearsProp = model.createProperty(LOVE_NS + "fears");
        Property locatedInProp = model.createProperty(LOVE_NS + "locatedIn");

        for (String fear : fears) {
            Resource fearResource = model.createResource(LOVE_INST_NS + fear.replace(" ", ""));
            fearResource.addProperty(RDF.type, model.createResource(LOVE_NS + "Concept"));
            human.addProperty(fearsProp, fearResource);
        }

        if (locatedIn != null) {
            Resource location = model.createResource(LOVE_INST_NS + locatedIn);
            location.addProperty(RDF.type, model.createResource(LOVE_NS + "Location"));
            human.addProperty(locatedInProp, location);
        }
    }

    /**
     * Sparar modellen till en Turtle-fil.
     */
    private static void saveModel(Model model, String filename) {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            RDFDataMgr.write(out, model, RDFFormat.TURTLE);
            System.out.println("Ontology saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving ontology to " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
