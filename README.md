# Description 

This is an implementation for an exploratory query answering framework using VLog. It takes a template and a set of reformulation axioms and produces the translation of the template-generated query space into Rulewerk syntax and the compilation of answers for such queries over the chosen data. 





# Template Syntax



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

