# XML Processing in Spring

## Learning Objectives

- Understand XML as a structured data format and its role in enterprise Java applications.
- Learn how Spring's Object-XML Mapping (OXM) module bridges Java objects and XML documents.
- Use the Marshaller and Unmarshaller interfaces to convert between objects and XML.
- Handle common XML mapping exceptions with XmlMappingException.

## Why This Matters

XML remains a dominant data exchange format in enterprise systems, particularly in legacy integrations, SOAP-based web services, and configuration files. This week focuses on document processing within Spring Boot applications. Mastering XML processing is the first step in that journey -- it establishes the patterns for data serialization and deserialization that you will see again with JSON.

## The Concept

### XML Overview

XML (eXtensible Markup Language) is a text-based format for representing structured data. It uses a tree of nested elements defined by opening and closing tags:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<employee>
    <id>101</id>
    <name>Alice Johnson</name>
    <department>Engineering</department>
</employee>
```

Key characteristics of XML:

- **Self-describing** -- Tags provide context about the data they contain.
- **Hierarchical** -- Data is organized in a parent-child tree structure.
- **Schema-enforceable** -- Can be validated against an XSD (XML Schema Definition).
- **Verbose** -- More characters per datum compared to formats like JSON.

### Spring OXM Module

Spring provides the `spring-oxm` module (Object-XML Mapping) as an abstraction layer over various XML binding frameworks such as JAXB, Castor, and XStream. The module defines two core interfaces:

| Interface       | Responsibility                                      |
|-----------------|-----------------------------------------------------|
| `Marshaller`    | Converts a Java object into an XML representation.  |
| `Unmarshaller`  | Converts an XML representation back into a Java object. |

By programming against these interfaces, your application code remains decoupled from the specific XML binding technology.

### Marshaller Interface

The `Marshaller` interface defines a single primary method:

```java
void marshal(Object graph, Result result) throws XmlMappingException, IOException;
```

- `graph` -- The Java object to serialize.
- `result` -- The output target, typically a `StreamResult` wrapping an `OutputStream` or `Writer`.

### Unmarshaller Interface

The `Unmarshaller` interface mirrors the marshaller:

```java
Object unmarshal(Source source) throws XmlMappingException, IOException;
```

- `source` -- The XML input, typically a `StreamSource` wrapping an `InputStream` or `Reader`.

### JAXB Integration

JAXB (Java Architecture for XML Binding) is the most common implementation used with Spring OXM. JAXB uses annotations on your Java classes to define the XML mapping:

```java
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "employee")
public class Employee {

    private int id;
    private String name;
    private String department;

    @XmlElement
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @XmlElement
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @XmlElement
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
```

Key JAXB annotations:

| Annotation          | Purpose                                                         |
|---------------------|-----------------------------------------------------------------|
| `@XmlRootElement`   | Marks the class as the root element of the XML document.        |
| `@XmlElement`       | Maps a property to an XML child element.                        |
| `@XmlAttribute`     | Maps a property to an XML attribute on the element.             |
| `@XmlTransient`     | Excludes a property from XML mapping.                           |
| `@XmlType`          | Controls the ordering of child elements via `propOrder`.        |

### Configuring the Marshaller Bean

In a Spring Boot configuration class, you define a `Jaxb2Marshaller` bean:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class OxmConfig {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Employee.class);
        return marshaller;
    }
}
```

The `Jaxb2Marshaller` implements both `Marshaller` and `Unmarshaller`, so a single bean handles both directions.

### Marshalling Example

```java
import org.springframework.oxm.Marshaller;

import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;

public class XmlWriter {

    private final Marshaller marshaller;

    public XmlWriter(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void writeEmployeeToXml(Employee employee, String filePath) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            marshaller.marshal(employee, new StreamResult(fos));
        }
    }
}
```

### Unmarshalling Example

```java
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.stream.StreamSource;
import java.io.FileInputStream;

public class XmlReader {

    private final Unmarshaller unmarshaller;

    public XmlReader(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    public Employee readEmployeeFromXml(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            return (Employee) unmarshaller.unmarshal(new StreamSource(fis));
        }
    }
}
```

### XmlMappingException

`XmlMappingException` is Spring's abstraction over the various exceptions thrown by underlying XML frameworks. It is an unchecked exception (extends `RuntimeException`) and serves as the common base for:

| Exception                          | Cause                                                   |
|------------------------------------|---------------------------------------------------------|
| `MarshallingFailureException`      | Error during object-to-XML conversion (e.g., missing required element). |
| `UnmarshallingFailureException`    | Error during XML-to-object conversion (e.g., malformed XML). |
| `ValidationFailureException`       | XML does not conform to the expected schema.            |

Handling example:

```java
try {
    Employee emp = xmlReader.readEmployeeFromXml("data/employee.xml");
} catch (UnmarshallingFailureException e) {
    System.err.println("Failed to parse XML: " + e.getMessage());
} catch (XmlMappingException e) {
    System.err.println("XML mapping error: " + e.getMessage());
}
```

Because these are unchecked exceptions, you are not forced to catch them, but doing so is recommended in production code to provide meaningful error messages.

## Summary

- XML is a verbose but widely used data format, especially in enterprise and legacy systems.
- Spring OXM provides `Marshaller` and `Unmarshaller` interfaces that abstract over binding frameworks like JAXB.
- JAXB annotations (`@XmlRootElement`, `@XmlElement`, etc.) define how Java objects map to XML elements.
- The `Jaxb2Marshaller` bean handles both marshalling and unmarshalling in a single configuration.
- `XmlMappingException` is the common base exception for all XML processing errors in Spring OXM.
- Tomorrow, we will apply these same serialization patterns to JSON using Jackson and Gson.

## Additional Resources

- [Spring OXM Reference Documentation](https://docs.spring.io/spring-framework/reference/data-access/oxm.html)
- [Baeldung -- Spring OXM with JAXB](https://www.baeldung.com/spring-oxm)
- [Jakarta XML Binding (JAXB) Specification](https://jakarta.ee/specifications/xml-binding/)
