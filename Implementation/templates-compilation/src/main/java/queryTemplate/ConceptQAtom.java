package main.java.queryTemplate;

import java.util.ArrayList;
import java.util.List;

public class ConceptQAtom extends QueryAtom  {
	
	


	private String conceptName;
	private String variableName;
	
	
	public String getConceptName() {
		return conceptName;
	}


	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}


	public String getVariableName() {
		return variableName;
	}


	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}


	public ConceptQAtom(String conceptName, String variableName) {
		super();
		this.conceptName = conceptName;
		this.variableName = variableName;
	}


	@Override
	public String toDatalog(String tau) {
		
		String facts = DatalogBuiltIns.deriv+ "("+tau+","+conceptName+","+DatalogBuiltIns.varPrefix+variableName+","+DatalogBuiltIns.nil+")."+"\n";
		facts += DatalogBuiltIns.conc+"("+conceptName+"). \n "+DatalogBuiltIns.adom+"("+conceptName+") .\n";
		return facts;
	}
	
	
	public static ConceptQAtom parseExpression(String expression) throws ParseError {
		int pos1 = expression.indexOf("(");
		int pos2 = expression.indexOf(")");
		
		if (pos1==-1 ||  pos2==-1)
		{
			throw new ParseError("CAtom: not matching paranthesis");
		}
		String predName = expression.substring(0, pos1).trim();
		if (predName.contains("}")) {
			throw new ParseError("CAtom: not a concept name");
		}
		
		String varString = expression.substring(pos1+1,pos2);
		if (varString.contains(",")) {
			throw new ParseError("CAtom: not unary");
		}
		
		//conceptName = predName;
		//variableName = varString;
		
		return new ConceptQAtom(predName, varString);
		
		
		
	}
	
	public List<String> getVariables()
	{
		List<String> vars= new ArrayList<String>();
		vars.add(variableName);
		return vars;
	}

	@Override
	public String toString() {
		return "ConceptQAtom [conceptName=" + conceptName + ", variableName=" + variableName + "] \n";
	}

	
	@Override
	public String getResource() {
		return conceptName;
	}
	

}
