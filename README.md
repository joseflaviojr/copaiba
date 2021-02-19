# Copaíba

Programação em tempo de execução remota.

Programming in remote runtime.

## Descrição

A Copaíba é um projeto composto por protocolos e ferramentas que permite a manipulação de classes/objetos Java remotos através de scripts escritos em Groovy, Python, JavaScript e outras linguagens. Grande parte da manipulação é feita através de [reflexão](https://docs.oracle.com/javase/tutorial/reflect/).

A figura a seguir ilustra o esquema geral da arquitetura Copaíba:

<img src="projeto/EsquemaGeral.png">

## Versão Atual

1.0-A16 (Fase de Nascimento)

Padrão de versionamento: [JFV](http://joseflavio.com/jfv)

## Como Usar

A Copaíba está disponível como biblioteca Java no repositório [Maven](http://search.maven.org/#artifactdetails%7Ccom.joseflavio%7Ccopaiba%7C1.0-A16%7Cjar).

Gradle:

```
implementation 'com.joseflavio:copaiba:1.0-A16'
```

Maven:

```xml
<dependency>
    <groupId>com.joseflavio</groupId>
    <artifactId>copaiba</artifactId>
    <version>1.0-A16</version>
</dependency>
```

### Requisitos para uso

* Java >= 1.8

## Documentação

A documentação da Copaíba, no formato **Javadoc**, está disponível em:

[http://joseflavio.com/copaiba/javadoc](http://joseflavio.com/copaiba/javadoc)

## Desenvolvimento

Configuração do projeto para Eclipse IDE e IntelliJ IDEA:

```sh
gradle cleanEclipse eclipse
gradle cleanIdea idea
```

### Requisitos para desenvolvimento

* Git >= 2.8
* Java >= 1.8
* Gradle >= 4.7

### Testes

Os testes [JUnit](https://junit.org/junit4/) estão localizados no pacote `com.joseflavio.copaiba.teste` da biblioteca Copaíba, sendo [com.joseflavio.copaiba.teste.CopaibaTestes](https://github.com/joseflaviojr/copaiba/blob/master/fonte/com/joseflavio/copaiba/teste/CopaibaTestes.java) a classe central dos testes.

## Compilação

Para compilar o projeto, gerando os arquivos JAR, executar no terminal:

```sh
gradle clean build
```

## Publicação

Para compilar e publicar os arquivos finais do projeto no repositório [Maven](http://search.maven.org/#artifactdetails%7Ccom.joseflavio%7Ccopaiba%7C1.0-A16%7Cjar), executar no terminal:

```sh
gradle clean publish
```
