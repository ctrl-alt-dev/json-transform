# JSON-Transform

A Java centric library to do transforms and mappings on JSON documents. 

## Examples

### Complete roundtrip

```java
//
String document = "{ \"some\":\"value\" }";
//
// Build the mapping.
//
DocumentSource mapping = JsonTransform.sequence(
    JsonTransform.move(JsonTransform.path("elsewhere"))
    ).build();
//
// Pull the output from the input.
//
Object output = mapping.getDocument(
    new ValueSource(JsonTransform.parse(document))
    );
//
// Show the output.
//
System.out.println(JsonTransform.print(output));
//
// {"elsewhere":{"some":"value"}}
//
```

### Property Mapping

```java
MappingTransform mapping = JsonTransform.mapProperties()
    .rename("id", "remoteId")
    .delete("value")
    .add("constant", Integer.valueOf(42))
    .reformat("name", new UppercaseConversion())
    .move("move", JsonTransform.path("moved.somewhere"))
    .rename("bad", "good")
    .build();
```

# Maven

To use JSON-Transform in your project, add the following Maven dependency:

```xml
<dependency>
  <groupId>nl.ctrlaltdev.jsontransform</groupId>
  <artifactId>json-transform</artifactId>
  <version>0.1.1</version>
</dependency>
```

# Introduction

Currently there is no easy way of converting Json into some other form of Json in Java. As I am currently working on a project that does lots of Json transformations
by mapping the Json to Java, the Java to some other Java using mapping code and then map that Java back to Json, I thought it could be a good idea to remove Java from
the equation and do the transformation in some generic format. This library is an implementation of that idea.  

## Features

- Property Mappings. (add, delete, rename, reformat, etc)
- Property Conversions (string, date time, etc)
- Transforms. (copy, move, map parts or the whole of the document)
- Select document nodes using JsonPath, prefab selectors or write your own.
- Describe the position in a document using Path, or a Relative Path.
- Java Binding.  
- Json Templates (build a Json document out of other documents using a template structure and selects).
- Combine multiple Json documents into one.
- Split and Join documents. 
- Combine multiple transforms together. 
- Write your own custom mappings using the Transform or ValuePathTransform interfaces.
- Built in parser and printer.
- No runtime dependencies.

## Getting Started

Look at the examples above and examine the Unit Tests, they cover the full range of the API.

Then start working with the JsonTransform API facade which exposes most of the functionality of this library.

## Alternatives

- [JOLT](https://github.com/bazaarvoice/jolt) - JSON to JSON transformation library written in Java where the "specification" for the transform is itself a JSON document.
- [Silencio](https://github.com/damianszczepanik/silencio) - Silencio is a Java library for transforming and converting JSON, Properties and other file formats.

## License

[Apache 2](http://www.apache.org/licenses/LICENSE-2.0)

# Releases

## 0.1.1 / 17 Januari 2016

- Listify and Mapify transforms.
- Added excludes to Flatten transform.
- Relaxed toJava Mapping

## 0.1.0 / 14 January 2016

First Release!


 