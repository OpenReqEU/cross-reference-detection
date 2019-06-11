# Cross Reference Detection

_This service was created as a result of the OpenReq project funded by the European Union Horizon 2020 Research and Innovation programme under grant agreement No 732463._

## Introduction

The **cross-reference detection** tool is a Requirement Engineering (RE) tool used to identify cross-references from requirements. These requirements can be in a database, or in an HTML document. Cross-references are dependencies explicitly stated, either internal (between requirements of the same project), or external (between a requirement and an external source).

## Technical description

Next sections provide a general overview of the technical details of the dependency detection tool.

### Functionalities

#### 1. Cross-reference detection: HTML file

Uploads one document in HTML format to the server, extracts the cross-references of all the requirements in the document and finally removes the uploaded file.

- **Indexed detection** -> the component allows to extract the cross-references of the requirements between N-M indexes in the document.

#### 2. Cross-reference detection: JSON project

Extracts the cross-references of all bugs and requirements of a project stored in an input JSON.

- **Indexed detection** -> the component allows to extract the cross-references between N-M indexes of all bugs and requirements of the input JSON.

### Used technologies

* Swagger (&rarr; [https://swagger.io/](https://swagger.io/) )
* Maven (&rarr; [https://maven.apache.org/](https://maven.apache.org/) )
* jsoup: Java HTML Parser (&rarr; [https://jsoup.org/](https://jsoup.org/) )

### How to install

The project does not require the addition of external dependencies or additional configuration. It can be built by running:

```
mvn clean install package
```

This generates a .jar file.

### How to use it

You can take a look at the Swagger documentation [here](https://api.openreq.eu/#/services/cross-reference-detection), which includes specific, technical details of the REST API to communicate to the service.

### Notes for developers

### Sources

* "Jsoup Java HTML Parser, with Best of DOM, CSS, and Jquery." Jsoup Java HTML Parser, with Best of DOM, CSS, and Jquery. Accessed March 11, 2019. https://jsoup.org/.

## How to contribute

See OpenReq project contribution [guidelines](https://github.com/OpenReqEU/OpenReq/blob/master/CONTRIBUTING.md)

## License

Free use of this software is granted under the terms of the [EPL version 2 (EPL2.0)](https://www.eclipse.org/legal/epl-2.0/)
