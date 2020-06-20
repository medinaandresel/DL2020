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

