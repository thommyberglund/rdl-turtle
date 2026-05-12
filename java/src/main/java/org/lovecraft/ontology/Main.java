package org.lovecraft.ontology;

/**
 * Main-klass för att starta Lovecraft Ontology-programmet.
 * 
 * Denna klass ger ett enkelt sätt att köra ontologin från kommandoraden.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Lovecraft Mythos Ontology Generator ===");
        System.out.println("Generating OWL/RDL ontology in Turtle format...\n");
        
        try {
            // Skapa och köra ontologin
            LovecraftOntology ontology = new LovecraftOntology();
            
            // Anropa main-metoden i LovecraftOntology
            // (Vi skapar en ny main-metod i LovecraftOntology som kan anropas)
            LovecraftOntology.main(args);
            
            System.out.println("\n=== Ontology Generation Complete ===");
            System.out.println("Files generated:");
            System.out.println("  - java/lovecraft_mythos.ttl");
            System.out.println("  - java/lovecraft_mythos_simple.ttl");
        } catch (Exception e) {
            System.err.println("Error generating ontology: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
