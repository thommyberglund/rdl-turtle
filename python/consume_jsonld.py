#!/usr/bin/env python3
"""
JSON-LD Consumer: Läs och omvandla JSON-LD till RDF Graph.

Detta script läser en JSON-LD-fil och omvandlar den till en RDF-graf
som kan användas med rdflib, det vill säga det omvända av
lovecraft_ontology_jsonld.py.
"""

import json
from rdflib import Graph, URIRef, Literal, Namespace
from rdflib.namespace import RDF, RDFS, OWL, XSD


# Definiera namnrymder (samma som i original-implementeringen)
LOVE = Namespace("https://lovecraft.frosteby.eu/ontology#")
LOVE_INST = Namespace("https://lovecraft.frosteby.eu/instance#")
DCTERMS = Namespace("http://purl.org/dc/terms/")
SKOS = Namespace("http://www.w3.org/2004/02/skos/core#")
FOAF = Namespace("http://xmlns.com/foaf/0.1/")


def load_jsonld_file(filename: str) -> dict:
    """Laddar en JSON-LD-fil och returnerar dokumentet som en dict."""
    with open(filename, 'r', encoding='utf-8') as f:
        return json.load(f)


def jsonld_to_rdf_graph(jsonld_doc: dict) -> Graph:
    """
    Omvandlar ett JSON-LD-dokument till en RDF Graph.
    
    Args:
        jsonld_doc: JSON-LD-dokument som en Python-dict
        
    Returns:
        rdflib.Graph: RDF-graf med alla statements från JSON-LD
    """
    g = Graph()
    
    # Bind namnrymder för lättare läsning
    g.bind("lovecraft", LOVE)
    g.bind("instance", LOVE_INST)
    g.bind("owl", OWL)
    g.bind("rdf", RDF)
    g.bind("rdfs", RDFS)
    g.bind("xsd", XSD)
    g.bind("foaf", FOAF)
    g.bind("dcterms", DCTERMS)
    g.bind("skos", SKOS)
    
    # Hämta kontext och graf från JSON-LD
    context = jsonld_doc.get("@context", {})
    graph_nodes = jsonld_doc.get("@graph", [])
    
    # Om det inte finns en @graph, förvänta oss att hela dokumentet är en nod
    if not graph_nodes:
        graph_nodes = [jsonld_doc]
    
    # Processa varje nod i grafen
    for node in graph_nodes:
        process_jsonld_node(node, context, g)
    
    return g


def process_jsonld_node(node: dict, context: dict, g: Graph) -> None:
    """
    Processar en JSON-LD-nod och lägger till dess statements i RDF-grafen.
    
    Args:
        node: JSON-LD-nod
        context: JSON-LD-kontext
        g: RDF-graf att lägga till statements i
    """
    # Hämta node ID
    node_id = node.get("@id", "")
    if not node_id:
        return
    
    # Omvandla node_id till URIRef
    subject = URIRef(node_id)
    
    # Processa @type
    node_type = node.get("@type")
    if node_type:
        if isinstance(node_type, list):
            for type_val in node_type:
                if isinstance(type_val, str):
                    # Expandera typen med kontext
                    type_uri = expand_uri(type_val, context)
                    g.add((subject, RDF.type, URIRef(type_uri)))
                elif isinstance(type_val, dict):
                    # Hantera typ som en nod-referens
                    type_uri = type_val.get("@id", "")
                    if type_uri:
                        g.add((subject, RDF.type, URIRef(type_uri)))
        else:
            # Enskild typ
            if isinstance(node_type, str):
                type_uri = expand_uri(node_type, context)
                g.add((subject, RDF.type, URIRef(type_uri)))
            elif isinstance(node_type, dict):
                type_uri = node_type.get("@id", "")
                if type_uri:
                    g.add((subject, RDF.type, URIRef(type_uri)))
    
    # Processa alla andra egenskaper
    for key, value in node.items():
        if key in ["@id", "@type"]:
            continue
        
        # Expandera egenskapsnamn med kontext
        predicate_uri = expand_uri(key, context)
        
        # Processa värdet/värdena
        if isinstance(value, list):
            # Flera värden
            for item in value:
                process_jsonld_value(item, predicate_uri, subject, context, g)
        else:
            # Enskilt värde
            process_jsonld_value(value, predicate_uri, subject, context, g)


def process_jsonld_value(value, predicate_uri: str, subject: URIRef, context: dict, g: Graph) -> None:
    """
    Processar ett JSON-LD-värde och lägger till motsvarande statement i grafen.
    
    Args:
        value: Värdet att processa
        predicate_uri: URI för predikatet
        subject: Subjekt-nod
        context: JSON-LD-kontext
        g: RDF-graf
    """
    if isinstance(value, dict):
        # Kontrollera om det är en nod-referens
        if "@id" in value:
            # Objekt-referens
            obj = URIRef(value["@id"])
            g.add((subject, URIRef(predicate_uri), obj))
        elif "@value" in value:
            # Literal-värde
            literal_value = value["@value"]
            datatype = value.get("@type")
            language = value.get("@language")
            
            if datatype:
                # Typad literal
                datatype_uri = expand_uri(datatype, context)
                g.add((subject, URIRef(predicate_uri), Literal(literal_value, datatype=URIRef(datatype_uri))))
            elif language:
                # Språk-märkt literal
                g.add((subject, URIRef(predicate_uri), Literal(literal_value, lang=language)))
            else:
                # Enkel literal
                g.add((subject, URIRef(predicate_uri), Literal(literal_value)))
        elif "@list" in value:
            # Lista (hanteras som en sekvens)
            # För enkelhetens skull, lägg till varje element som separat statement
            for item in value["@list"]:
                process_jsonld_value(item, predicate_uri, subject, context, g)
    elif isinstance(value, str):
        # Enkel sträng (kan vara en URI-referens)
        # Försök att expandera som URI
        expanded = expand_uri(value, context)
        # Om det ser ut som en URI, behandla som objekt
        if expanded.startswith("http") or expanded.startswith("https") or ":" in expanded:
            g.add((subject, URIRef(predicate_uri), URIRef(expanded)))
        else:
            # Annars som literal
            g.add((subject, URIRef(predicate_uri), Literal(value)))
    elif isinstance(value, (int, float, bool)):
        # Numeriskt eller booleskt värde
        g.add((subject, URIRef(predicate_uri), Literal(value)))


def expand_uri(uri: str, context: dict) -> str:
    """
    Expanderar en kompakt URI med JSON-LD-kontext.
    
    Args:
        uri: URI att expandera (kan vara kompakt form som "lovecraft:Entity")
        context: JSON-LD-kontext
        
    Returns:
        Fullständig URI
    """
    if not uri:
        return uri
    
    # Om det redan är en full URI
    if uri.startswith("http://") or uri.startswith("https://"):
        return uri
    
    # Kontrollera om det är en kompakt URI med prefix
    if ":" in uri:
        prefix, suffix = uri.split(":", 1)
        # Hitta prefix i kontext
        if prefix in context:
            base = context[prefix]
            if base.endswith("#") or base.endswith("/"):
                return base + suffix
            else:
                return base + "#" + suffix
    
    # Om inget prefix hittas, returnera som är
    return uri


def consume_jsonld(filename: str) -> Graph:
    """
    Laddar en JSON-LD-fil och returnerar en RDF-graf.
    
    Args:
        filename: Filnamn för JSON-LD-filen
        
    Returns:
        rdflib.Graph: RDF-graf med ontologin
    """
    jsonld_doc = load_jsonld_file(filename)
    return jsonld_to_rdf_graph(jsonld_doc)


def save_as_turtle(graph: Graph, filename: str) -> None:
    """Sparar en RDF-graf som Turtle-fil."""
    turtle_data = graph.serialize(format="turtle")
    if isinstance(turtle_data, bytes):
        turtle_data = turtle_data.decode("utf-8")
    with open(filename, "w", encoding="utf-8") as f:
        f.write(turtle_data)
    print(f"Graph saved to {filename}")


def main():
    """Huvudfunktion för att demonstrera JSON-LD-konsumtion."""
    print("Loading JSON-LD file and converting to RDF Graph...")
    
    # Ladda JSON-LD-filen
    graph = consume_jsonld("lovecraft_mythos.jsonld")
    
    # Visa information om grafen
    print(f"Graph loaded with {len(graph)} triples")
    
    # Spara som Turtle för verifiering
    save_as_turtle(graph, "consumed_ontology.ttl")
    
    # Exempel: Fråga grafen
    print("\n--- Example Queries ---")
    
    # Hitta alla klasser
    print("\nClasses:")
    for s, p, o in graph.triples((None, RDF.type, OWL.Class)):
        print(f"  - {s}")
    
    # Hitta alla individer
    print("\nIndividuals:")
    for s, p, o in graph.triples((None, RDF.type, OWL.NamedIndividual)):
        print(f"  - {s}")
    
    # Hitta Cthulhu
    print("\nCthulhu information:")
    cthulhu = LOVE_INST.Cthulhu
    for s, p, o in graph.triples((cthulhu, None, None)):
        print(f"  {p}: {o}")
    
    print("\nDone! JSON-LD successfully consumed and converted to RDF Graph.")


if __name__ == "__main__":
    main()
