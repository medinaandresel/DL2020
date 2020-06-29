package main.java.queryTemplate;

import java.util.ArrayList;
import java.util.List;

public class GenAtom extends TemplateAtom{
	
	public String toDatalog(String tau) {
		return this.toDatalog(tau);
	}

	public static GenAtom parseExpression(String expression) throws ParseError {
		
		try {
			GenCAtom atom = GenCAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		
		try {
			GenRAtom atom = GenRAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		
		try {
			GenEqAtom atom = GenEqAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		
		
		throw new ParseError("SpeAtom: parse error");
		
	}
	
	public List<String> getVariables()
	{
		return this.getVariables();
	}

	@Override
	public String getResource() {
		return this.getResource();
	}

}
