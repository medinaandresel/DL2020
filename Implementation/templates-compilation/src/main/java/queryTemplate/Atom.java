package main.java.queryTemplate;

import java.util.List;

public interface Atom {
	
	public String toDatalog(String tau);
	
	public List<String> getVariables();
	
	public String getResource();
	
	//public TemplateAtom parseExpression(String expression) throws ParseError ;

}
