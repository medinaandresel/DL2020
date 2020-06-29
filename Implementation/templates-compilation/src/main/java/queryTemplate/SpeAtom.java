package main.java.queryTemplate;



public class SpeAtom extends TemplateAtom{
	
	
	public String toDatalog(String tau) {
		return this.toDatalog(tau);
	}

	public static SpeAtom parseExpression(String expression) throws ParseError {
		
		try {
			SpeCAtom atom = SpeCAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		
		try {
			SpeRAtom atom = SpeRAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		
		
		try {
			SpeEqAtom atom = SpeEqAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		
		throw new ParseError("SpeAtom: parse error");
		
	}
	@Override
	public String getResource() {
		return this.getResource();
	}

}
