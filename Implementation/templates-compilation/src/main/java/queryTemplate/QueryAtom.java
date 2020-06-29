package main.java.queryTemplate;
public class QueryAtom extends TemplateAtom implements Atom{

	public String toDatalog(String tau) {
		return this.toDatalog(tau);
	}

	public static QueryAtom parseExpression(String expression) throws ParseError {
		 
		try {
			ConceptQAtom atom = ConceptQAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		try {
			RoleQAtom atom = RoleQAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		try {
			EqQAtom atom = EqQAtom.parseExpression(expression);
			return atom;
		}catch (ParseError e) {}
		
		throw new ParseError("QAtom: not a query atom");
	}

	@Override
	public String getResource() {
		return this.getResource();
	}
}
