package eu.frosteby.ontology;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;
import org.apache.jena.ontology.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Generator som konverterar en Turtle/OWL-ontologi till Java-klasser.
 * 
 * Denna klass läser en RDF/OWL-ontologi i Turtle-format och genererar
 * motsvarande Java-klasser med instansfält och relationer.
 */
public class OntologyToJavaGenerator {

    private static final String BASE_PACKAGE = "eu.frosteby.ontology.generated";
    private static final String OUTPUT_DIR = "src/main/java";
    private static final String LOVE_NS = "https://lovecraft.frosteby.eu/ontology#";
    private static final String LOVE_INST_NS = "https://lovecraft.frosteby.eu/instance#";

    private Model model;
    private OntModel ontModel;
    private String outputDirectory;

    /**
     * Skapar en ny generator.
     */
    public OntologyToJavaGenerator() {
        this.model = ModelFactory.createDefaultModel();
        this.ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
        this.outputDirectory = OUTPUT_DIR;
    }

    /**
     * Läser in en Turtle-fil och genererar Java-klasser.
     * 
     * @param turtleFilePath Sökväg till Turtle-filen
     * @param outputDir Utdata-katalog för genererade filer
     */
    public void generateFromTurtle(String turtleFilePath, String outputDir) {
        this.outputDirectory = outputDir;
        
        System.out.println("Reading Turtle file: " + turtleFilePath);
        
        // Läs in modellen från Turtle-fil
        InputStream in = FileManager.get().open(turtleFilePath);
        if (in == null) {
            throw new IllegalArgumentException("File not found: " + turtleFilePath);
        }
        
        model.read(in, null, "TURTLE");
        ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, model);
        
        try {
            in.close();
        } catch (IOException e) {
            System.err.println("Warning: Could not close input stream: " + e.getMessage());
        }
        
        System.out.println("Loaded ontology with " + model.size() + " triples");
        
        // Skapa utdata-kataloger
        createOutputDirectories();
        
        // Generera klasser
        generateOntologyClasses();
        
        System.out.println("Java classes generated successfully!");
    }

    /**
     * Bygger en fullständig utdata-sökväg.
     */
    private Path buildOutputPath(String outputDir, String basePackage, String fileName) {
        String packagePath = basePackage.replace(".", "/");
        Path filePath;
        
        if (new File(outputDir).isAbsolute()) {
            filePath = Paths.get(outputDir, packagePath, fileName);
        } else {
            Path currentDir = Paths.get("").toAbsolutePath();
            filePath = currentDir.resolve(outputDir).resolve(packagePath).resolve(fileName);
        }
        
        return filePath;
    }

    /**
     * Skapar nödvändiga utdata-kataloger.
     */
    private void createOutputDirectories() {
        try {
            Path basePath = buildOutputPath(outputDirectory, BASE_PACKAGE, "");
            Files.createDirectories(basePath);
            System.out.println("Created output directory: " + basePath.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create output directories: " + e.getMessage(), e);
        }
    }

    /**
     * Genererar Java-klasser från ontologin.
     */
    private void generateOntologyClasses() {
        // Hämta alla OWL-klasser
        ExtendedIterator<OntClass> classes = ontModel.listClasses();
        
        while (classes.hasNext()) {
            OntClass ontClass = classes.next();
            String uri = ontClass.getURI();
            
            // Skippa system-klasser (OWL, RDF, etc.)
            if (uri == null || 
                uri.startsWith("http://www.w3.org/") ||
                uri.startsWith("http://xmlns.com/") ||
                uri.startsWith("http://purl.org/")) {
                continue;
            }
            
            // Extrahera klassnamn från URI
            String className = extractClassName(uri);
            if (className == null || className.isEmpty()) {
                continue;
            }
            
            System.out.println("Generating class: " + className);
            generateClass(ontClass, className);
        }
        
        // Generera en Factory-klass för instanser
        generateFactoryClass();
    }

    /**
     * Extraherar klassnamn från en URI.
     */
    private String extractClassName(String uri) {
        if (uri == null) {
            return null;
        }
        
        // Ta sista delen av URIn
        int lastHash = uri.lastIndexOf('#');
        int lastSlash = uri.lastIndexOf('/');
        int start = Math.max(lastHash, lastSlash) + 1;
        
        if (start >= uri.length()) {
            return null;
        }
        
        String name = uri.substring(start);
        
        // Konvertera till giltigt Java-klassnamn
        // Ersätt specialtecken
        name = name.replace("-", "_").replace(" ", "");
        
        // Första bokstaven stor
        if (!name.isEmpty()) {
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        
        return name;
    }

    /**
     * Genererar en Java-klass för en OWL-klass.
     */
    private void generateClass(OntClass ontClass, String className) {
        StringBuilder sb = new StringBuilder();
        
        // Package-deklaration
        sb.append("package " + BASE_PACKAGE + ";\n\n");
        
        // Importer
        sb.append("import java.util.*;\n");
        sb.append("import org.apache.jena.rdf.model.*;\n\n");
        
        // Klass-javadoc
        String label = getLabel(ontClass);
        String description = getDescription(ontClass);
        sb.append("/**\n");
        sb.append(" * " + className + "\n");
        if (label != null) {
            sb.append(" * Label: " + label + "\n");
        }
        if (description != null) {
            sb.append(" * Description: " + description + "\n");
        }
        sb.append(" * \n");
        sb.append(" * Auto-generated from OWL ontology\n");
        sb.append(" */\n");
        
        // Klass-deklaration
        sb.append("public class " + className + " {\n\n");
        
        // URI-konstant
        sb.append("    public static final String URI = \"" + ontClass.getURI() + "\";\n\n");
        
        // Instans-URI
        sb.append("    private String instanceUri;\n\n");
        
        // Konstruktorer
        sb.append("    /**\n");
        sb.append("     * Creates a new " + className + " instance.\n");
        sb.append("     */\n");
        sb.append("    public " + className + "(String instanceUri) {\n");
        sb.append("        this.instanceUri = instanceUri;\n");
        sb.append("    }\n\n");
        
        sb.append("    /**\n");
        sb.append("     * Creates a new " + className + " with default instance URI.\n");
        sb.append("     */\n");
        sb.append("    public " + className + "() {\n");
        sb.append("        this.instanceUri = URI + \"Instance\";\n");
        sb.append("    }\n\n");
        
        // Getter för instanceUri
        sb.append("    public String getInstanceUri() {\n");
        sb.append("        return instanceUri;\n");
        sb.append("    }\n\n");
        
        // Getter för URI
        sb.append("    public String getUri() {\n");
        sb.append("        return URI;\n");
        sb.append("    }\n\n");
        
        // toString
        sb.append("    @Override\n");
        sb.append("    public String toString() {\n");
        sb.append("        return \"" + className + "{instanceUri='\" + instanceUri + \"'}\";\n");
        sb.append("    }\n\n");
        
        // equals och hashCode
        sb.append("    @Override\n");
        sb.append("    public boolean equals(Object o) {\n");
        sb.append("        if (this == o) return true;\n");
        sb.append("        if (o == null || getClass() != o.getClass()) return false;\n");
        sb.append("        " + className + " that = (" + className + ") o;\n");
        sb.append("        return Objects.equals(instanceUri, that.instanceUri);\n");
        sb.append("    }\n\n");
        
        sb.append("    @Override\n");
        sb.append("    public int hashCode() {\n");
        sb.append("        return Objects.hash(instanceUri);\n");
        sb.append("    }\n");
        
        // Stäng klass
        sb.append("}\n");
        
        // Skriv till fil
        String fileName = className + ".java";
        
        // Bygg fullständig sökväg
        Path filePath = buildOutputPath(outputDirectory, BASE_PACKAGE, fileName);
        
        writeFile(filePath.toString(), sb.toString());
    }

    /**
     * Genererar en Factory-klass för att skapa instanser.
     */
    private void generateFactoryClass() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("package " + BASE_PACKAGE + ";\n\n");
        sb.append("import java.util.*;\n");
        sb.append("import org.apache.jena.rdf.model.*;\n\n");
        
        sb.append("/**\n");
        sb.append(" * Factory class for creating ontology instances.\n");
        sb.append(" * Auto-generated from OWL ontology\n");
        sb.append(" */\n");
        sb.append("public class OntologyFactory {\n\n");
        
        // Metod för att skapa Resource från URI
        sb.append("    private static Model model = ModelFactory.createDefaultModel();\n\n");
        
        sb.append("    public static Model getModel() {\n");
        sb.append("        return model;\n");
        sb.append("    }\n\n");
        
        // Hämta alla individer och generera create-metoder
        Resource namedIndividual = model.getResource(OWL.NS + "NamedIndividual");
        StmtIterator it = model.listStatements(null, RDF.type, namedIndividual);
        while (it.hasNext()) {
            Statement stmt = it.next();
            Resource individual = stmt.getSubject();
            String uri = individual.getURI();
            
            if (uri == null || !uri.startsWith(LOVE_INST_NS)) {
                continue;
            }
            
            String name = uri.substring(LOVE_INST_NS.length());
            String className = extractClassName(getTypeForIndividual(individual));
            if (className == null) {
                className = "Entity";
            }
            
            // Skapa en konstant och en create-metod
            sb.append("    public static final String " + name.toUpperCase() + "_URI = \"" + uri + "\";\n");
        }
        
        sb.append("\n");
        sb.append("    /**\n");
        sb.append("     * Creates a Jena Resource for an instance URI.\n");
        sb.append("     */\n");
        sb.append("    public static org.apache.jena.rdf.model.Resource createJenaResource(String uri) {\n");
        sb.append("        return model.createResource(uri);\n");
        sb.append("    }\n");
        
        sb.append("    /**\n");
        sb.append("     * Creates a Jena Model.\n");
        sb.append("     */\n");
        sb.append("    public static Model createModel() {\n");
        sb.append("        return ModelFactory.createDefaultModel();\n");
        sb.append("    }\n");
        
        sb.append("}\n");
        
        String fileName = "OntologyFactory.java";
        
        // Bygg fullständig sökväg
        Path filePath = buildOutputPath(outputDirectory, BASE_PACKAGE, fileName);
        
        writeFile(filePath.toString(), sb.toString());
    }

    /**
     * Hämtar typen för en individ.
     */
    private String getTypeForIndividual(Resource individual) {
        StmtIterator it = model.listStatements(individual, RDF.type, (RDFNode) null);
        while (it.hasNext()) {
            Statement stmt = it.next();
            RDFNode object = stmt.getObject();
            if (object.isResource()) {
                String uri = object.asResource().getURI();
                if (uri != null && !uri.startsWith("http://www.w3.org/")) {
                    return uri;
                }
            }
        }
        return null;
    }

    /**
     * Hämtar label för en resurs.
     */
    private String getLabel(Resource resource) {
        Property labelProp = model.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
        StmtIterator it = model.listStatements(resource, labelProp, (RDFNode) null);
        if (it.hasNext()) {
            Statement stmt = it.next();
            RDFNode object = stmt.getObject();
            if (object.isLiteral()) {
                return object.asLiteral().getString();
            }
        }
        return null;
    }

    /**
     * Hämtar description för en resurs.
     */
    private String getDescription(Resource resource) {
        Property descProp = model.getProperty("http://www.w3.org/2000/01/rdf-schema#comment");
        StmtIterator it = model.listStatements(resource, descProp, (RDFNode) null);
        if (it.hasNext()) {
            Statement stmt = it.next();
            RDFNode object = stmt.getObject();
            if (object.isLiteral()) {
                return object.asLiteral().getString();
            }
        }
        return null;
    }

    /**
     * Skriver innehåll till en fil.
     */
    private void writeFile(String path, String content) {
        try {
            Path filePath = Paths.get(path);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("  Written: " + path);
        } catch (IOException e) {
            System.err.println("Error writing file " + path + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Hjälpmetod för att generera från en Turtle-fil.
     * 
     * @param args Kommandoradsargument: [turtle-file] [output-dir]
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: OntologyToJavaGenerator <turtle-file> [output-dir]");
            System.out.println("Example: OntologyToJavaGenerator lovecraft_mythos.ttl");
            System.exit(1);
        }
        
        String turtleFile = args[0];
        String outputDir = args.length > 1 ? args[1] : OUTPUT_DIR;
        
        System.out.println("=== Ontology to Java Generator ===");
        System.out.println("Input: " + turtleFile);
        System.out.println("Output directory: " + outputDir);
        System.out.println();
        
        OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
        generator.generateFromTurtle(turtleFile, outputDir);
        
        System.out.println();
        System.out.println("=== Generation Complete ===");
    }
}
