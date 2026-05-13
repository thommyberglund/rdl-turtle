package org.lovecraft.ontology;

/**
 * Main-klass för att starta Lovecraft Ontology-programmet.
 * 
 * Denna klass ger ett enkelt sätt att köra ontologin från kommandoraden.
 * 
 * Användning:
 *   - Inga argument: Genererar OWL-ontologi till Turtle-filer
 *   - --generate-java <turtle-file>: Genererar Java-klasser från en Turtle-fil
 */
public class Main {

    public static void main(String[] args) {
        if (args.length >= 2 && "--generate-java".equals(args[0])) {
            // Läge: Generera Java-klasser från Turtle-fil
            System.out.println("=== Ontology to Java Generator ===");
            System.out.println("Input: " + args[1]);
            System.out.println();
            
            try {
                OntologyToJavaGenerator generator = new OntologyToJavaGenerator();
                String outputDir = args.length > 2 ? args[2] : "src/main/java";
                generator.generateFromTurtle(args[1], outputDir);
                System.out.println("\n=== Java Class Generation Complete ===");
            } catch (Exception e) {
                System.err.println("Error generating Java classes: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            // Standardläge: Generera OWL-ontologi till Turtle-filer
            System.out.println("=== Lovecraft Mythos Ontology Generator ===");
            System.out.println("Generating OWL/RDL ontology in Turtle format...\n");
            
            try {
                // Anropa main-metoden i LovecraftOntology direkt
                LovecraftOntology.main(args);
                
                System.out.println("\n=== Ontology Generation Complete ===");
                System.out.println("Files generated:");
                System.out.println("  - lovecraft_mythos.ttl");
                System.out.println("  - lovecraft_mythos_simple.ttl");
            } catch (Exception e) {
                System.err.println("Error generating ontology: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
