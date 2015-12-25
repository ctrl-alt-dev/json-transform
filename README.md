# JSON-Transform

A Java centric library to do transforms and mappings on JSON documents. 

## Note

This framework is a proof of concept to get workable JSON mappings in Java. As such, it's API may be quite unstable while I figure out how to do it.

## Example

```java
//
ObjectMapper objectMapper = new ObjectMapper();
//
String document = "{ \"some\":\"value\" }";
//
// Load JSON Document using Jackson into its basic Java form.
//
Map<String, Object> input = (Map<String, Object>) objectMapper.readValue(document, Map.class);
//
// Build the mapping.
//
DocumentSource mapping = CompositeMappingBuilder.sequence(
        MappingBuilder.move(Path.fromString("elsewhere"))
    ).build();
//
// Apply the mapping: pull the output from the input.
//
Object output = mapping.getDocument(new ValueSource(input));
//
// Show the output.
//
System.out.println(objectMapper.writeValueAsString(output));
//
// {"elsewhere":{"some":"value"}}
//
```

## Concepts

These are the mayor concepts in the framework, bottom up.

### Document

A JSON Document in its basic Java Form (i.e. a Map containing other Maps, Lists and values).

### Select

A select is an expression that selects a zero, one or more nodes in the source document.

```java
Select select = SelectBuilder.select().property("name").build();
```

### Transform

A transform applies a transformation to the source document and changes it into the target document.

```java
new IdentityTransform().apply(source, dest);
```

### Path

A Path describes the location of a node in a document.

```java
Path.fromString("some[2].property");
```

### Move Transform Select

A Move Transform Select is a combination of a Select, Transform and Move operation. 
The select selects zero, one or more nodes from the source document, which are transformed 
and moved to the specified location in the target document.

### Merge

Sometimes you have multiple documents that need to be merged into one. This is what Merge does.

There are 3 different MergeStrategies:
- Join Merge : the documents are joined.
- Array Merge: the documents are placed in an array.
- Overwrite Merge: the documents are merged, any overlapping properties are overwritten.

```java
MergeFactory.join(Path.root()).merge(someDocument);
```

### Sequence Mapping

A sequence mapping is a series of Move Transform Selects applied in reverse order.

### Parallel Mapping

A parallel mapping applies different Move Transform Selects to the same source document and merges the results into one document.

### Template

A Template allows you fill a JSON document template with values from another document. Its behavior can be customized using Handlers.

### Document Source

A document source produces a JSON document. There are two basic kinds of Document Sources:
- Inputs: these document sources provide the JSON documents that are the input of the mapping.  
- Output: this produces the output of the mapping.

### Mapping Builder

The Mapping Builder combines all of the above concepts to easily create JSON mappings. The result of the Builder is a Document Source
with which the output document can be produced. The input(s) are given as argument.

The Mapping is built from output to input, so in reverse order.

```
output &lt;-- ( Move &lt;- Transform &lt;- Select ) &lt;-- input
``` 

## Alternatives

- [JOLT](https://github.com/bazaarvoice/jolt) - JSON to JSON transformation library written in Java where the "specification" for the transform is itself a JSON document.
- [Silencio](https://github.com/damianszczepanik/silencio) - Silencio is a Java library for transforming and converting JSON, Properties and other file formats.

 