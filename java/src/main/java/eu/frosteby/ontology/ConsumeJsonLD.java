package eu.frosteby.ontology;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.ontology.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.Lang;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * JSON-LD Consumer: Läs och omvandla JSON-LD till RDF Model.
 * 
 * Detta program läser en JSON-LD-fil och omvandlar den till en RDF-modell
 * som kan användas med Apache Jena, det vill säga det omvända av
 * LovecraftOntologyJsonLD.java.
 */
public class ConsumeJsonLD {

    // Namnrymder (samma som i genereringsklassen)
    private static final String LOVE_NS = "https://lovecraft.frosteby.eu/ontology#";
    private static final String LOVE_INST_NS = "https://lovecraft.frosteby.eu/instance#";
    private static final String DCTERMS_NS = "http://purl.org/dc/terms/";

    public static void main(String[] args) {
        System.out.println("Loading JSON-LD file and converting to RDF Model (Java)...");
        
        // Ladda JSON-LD-filen
        Model model = loadJsonLDFile("lovecraft_mythos.jsonld");
        
        if (model != null) {
            // Visa information om modellen
            System.out.println("Model loaded with " + model.size() + " triples");
            
            // Spara som Turtle för verifiering
            saveModelAsTurtle(model, "consumed_ontology.ttl");
            
            // Exempel: Fråga modellen
            System.out.println("\n--- Example Queries ---");
            
            // Hitta alla klasser
            System.out.println("\nClasses:");
            StmtIterator classIter = model.listStatements(
                null, RDF.type, OWL.Class
            );
            while (classIter.hasNext()) {
                Statement stmt = classIter.next();
                System.out.println("  - " + stmt.getSubject());
            }
            
            // Hitta alla individer
            System.out.println("\nIndividuals:");
            Resource namedIndividual = model.createResource(OWL.NS + "NamedIndividual");
            StmtIterator individualIter = model.listStatements(
                null, RDF.type, namedIndividual
            );
            while (individualIter.hasNext()) {
                Statement stmt = individualIter.next();
                System.out.println("  - " + stmt.getSubject());
            }
            
            // Hitta Cthulhu
            System.out.println("\nCthulhu information:");
            Resource cthulhu = model.createResource(LOVE_INST_NS + "Cthulhu");
            StmtIterator cthulhuIter = model.listStatements(
                cthulhu, null, (RDFNode)null
            );
            while (cthulhuIter.hasNext()) {
                Statement stmt = cthulhuIter.next();
                System.out.println("  " + stmt.getPredicate() + ": " + stmt.getObject());
            }
            
            System.out.println("\nDone! JSON-LD successfully consumed and converted to RDF Model.");
        }
    }

    /**
     * Laddar en JSON-LD-fil och returnerar en RDF-modell.
     * 
     * @param filename Filnamn för JSON-LD-filen
     * @return RDF-modell med ontologin, eller null om fel uppstår
     */
    public static Model loadJsonLDFile(String filename) {
        try (FileInputStream in = new FileInputStream(filename)) {
            Model model = ModelFactory.createDefaultModel();
            // Läs JSON-LD och ladda in i modellen
            RDFDataMgr.read(model, in, null, Lang.JSONLD);
            return model;
        } catch (IOException e) {
            System.err.println("Error loading JSON-LD file " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Laddar en JSON-LD-fil och returnerar en RDF-modell med namnrymdsprefix.
     * 
     * @param filename Filnamn för JSON-LD-filen
     * @return RDF-modell med ontologin och namnrymdsprefix, eller null om fel uppstår
     */
    public static Model loadJsonLDFileWithPrefixes(String filename) {
        Model model = loadJsonLDFile(filename);
        if (model != null) {
            // Lägg till namnrymdsprefix
            model.setNsPrefix("lovecraft", LOVE_NS);
            model.setNsPrefix("instance", LOVE_INST_NS);
            model.setNsPrefix("owl", OWL.NS);
            model.setNsPrefix("rdf", RDF.uri);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            model.setNsPrefix("dcterms", DCTERMS_NS);
        }
        return model;
    }

    /**
     * Sparar en RDF-modell som Turtle-fil.
     * 
     * @param model RDF-modellen att spara
     * @param filename Filnamn för utdata
     */
    public static void saveModelAsTurtle(Model model, String filename) {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            RDFDataMgr.write(out, model, Lang.TURTLE);
            System.out.println("Model saved to " + filename + " in Turtle format");
        } catch (IOException e) {
            System.err.println("Error saving model to " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sparar en RDF-modell som JSON-LD-fil.
     * 
     * @param model RDF-modellen att spara
     * @param filename Filnamn för utdata
     */
    public static void saveModelAsJsonLD(Model model, String filename) {
        try (FileOutputStream out = new FileOutputStream(filename)) {
            RDFDataMgr.write(out, model, Lang.JSONLD);
            System.out.println("Model saved to " + filename + " in JSON-LD format");
        } catch (IOException e) {
            System.err.println("Error saving model to " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Omvandlar en JSON-LD-fil till en Turtle-fil.
     * 
     * @param inputJsonLDFile Inmatningsfil (JSON-LD)
     * @param outputTurtleFile Utmatningsfil (Turtle)
     */
    public static void convertJsonLDToTurtle(String inputJsonLDFile, String outputTurtleFile) {
        Model model = loadJsonLDFileWithPrefixes(inputJsonLDFile);
        if (model != null) {
            saveModelAsTurtle(model, outputTurtleFile);
        }
    }

    /**
     * Omvandlar en Turtle-fil till en JSON-LD-fil.
     * 
     * @param inputTurtleFile Inmatningsfil (Turtle)
     * @param outputJsonLDFile Utmatningsfil (JSON-LD)
     */
    public static void convertTurtleToJsonLD(String inputTurtleFile, String outputJsonLDFile) {
        try (FileInputStream in = new FileInputStream(inputTurtleFile)) {
            Model model = ModelFactory.createDefaultModel();
            RDFDataMgr.read(model, in, null, Lang.TURTLE);
            
            // Lägg till namnrymdsprefix
            model.setNsPrefix("lovecraft", LOVE_NS);
            model.setNsPrefix("instance", LOVE_INST_NS);
            model.setNsPrefix("owl", OWL.NS);
            model.setNsPrefix("rdf", RDF.uri);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            model.setNsPrefix("dcterms", DCTERMS_NS);
            
            saveModelAsJsonLD(model, outputJsonLDFile);
        } catch (IOException e) {
            System.err.println("Error converting " + inputTurtleFile + " to JSON-LD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
