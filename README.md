# Description 

This is the prototype implementation for an exploratory query answering framework using [VLog Datalog engine](https://github.com/karmaresearch/vlog).
It takes a query template and a set of reformulation axioms and produces the translation of the template-generated query space into Rulewerk syntax and the compilation of answers for such queries over the chosen data.

# Build

In order to build, you need to follow the instructions for using [Rulewerk API](https://github.com/knowsys/rulewerk) for VLog Datalog reasoner. 


# Usage

The ```templates-compilation.jar``` can be used to: 

1. *extract and complete* the data w.r.t. the relevant part of the ontology: e.g.,

```java -jar templates-compilation.jar -extract <vlogInputFile>``` 

2. *run* the Datalog encoding of the input template and the input file for VLog according to the [Rulewerk sytex] (https://github.com/knowsys/rulewerk/wiki/Rule-syntax-grammar): e.g.,

```java -jar templates-compilation.jar -opt <templateFile>  <vlogInputFile>```

3. *optimized* Datalog encoding of the input template when the data is not materialized w.r.t. the ontology: e.g.,

```java -jar templates-compilation.jar -run <templateFile>  <vlogInputFile>```


# VLog Input File 

In the VLog input file the following declarations are needed:

1. For option ```-extract```, file ```extractExtensions``` can be used as an example. 
2. For option ```-opt```, file ``` dbpedia_remote ``` is an example. 
3. For option ```-run```, file ```dbpedia_local_ext``` is an example.

In all cases, only the ```@source``` declarations must be updated according to the method of data access (local or remotely)
and according to the chosen dataset.



# Template Syntax

The template input file contains the considered query template and it must obey the following syntax:

```TEMPLATE  ::= VarSeq :- Atom (AND Atom)* ```

```
VarSeq  	::= Var (',' Var)*
Var 		::= [A-Z]+
IRI 		::= IRIREF | PrefixedName
```

``` 
Atom 		::= 'SPE{' IRI '}' '('Var')' | 'GEN{' IRI '}' '('Var')' |
		    'SPE{' IRI '}' '(' Var ',' Var ')' | 'GEN{' IRI '}' '(' Var ',' Var ')' |
		     Var'=' 'SPE{' IRI '}' | Var'=' 'GEN{' IRI '}' 
	
```

