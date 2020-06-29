package main.java.queryTemplate;

import java.util.List;

public abstract class TemplateAtom implements Atom{

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	private String identifier;

	public String toDatalog() {
		
		return this.toDatalog(identifier);
	}

	public static TemplateAtom parseExpression(String expression) throws ParseError {
		
		try {
			QueryAtom atom = QueryAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {
			
		}
		try {
			SpeAtom atom = SpeAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {
			
		}
		
		try {
			GenAtom atom = GenAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {
			e.printStackTrace();
		}
		return null;	
		
	}
	
	public List<String> getVariables(){
		return this.getVariables();
	}

	@Override
	public String getResource() {
		return this.getResource();
	}
	

}
