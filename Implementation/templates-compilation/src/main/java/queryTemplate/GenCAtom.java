package main.java.queryTemplate;

import java.util.ArrayList;
import java.util.List;

public class GenCAtom extends GenAtom{
	
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


	public GenCAtom(String conceptName, String variableName) {
		super();
		this.conceptName = conceptName;
		this.variableName = variableName;
	}


	@Override
	public String toDatalog(String tau) {
		
		String facts = DatalogBuiltIns.gen+"("+ tau + "," + conceptName +","+ DatalogBuiltIns.varPrefix+variableName+","+DatalogBuiltIns.nil+"). \n";
				//DatalogBuiltIns.deriv+ "("+tau+ Constants.get(conceptName)+")."+"\n";
		facts += DatalogBuiltIns.conc+"("+conceptName+") . \n "+DatalogBuiltIns.adom+"("+conceptName+") . \n";
		return facts;
	}
	
	
	@Override
	public String toString() {
		return "GenCAtom [conceptName=" + conceptName + ", variableName=" + variableName + "]\n";
	}


	public static GenCAtom parseExpression(String expression) throws ParseError {
		int pos1 = expression.indexOf("GEN");
		int pos2 = expression.indexOf("{");
		if (pos1==-1 || pos2==-1)
		{
			throw new ParseError("GenCAtom: not a specialising atom");
		}
		
		int pos3 = expression.indexOf("}");
		if (pos3==-1)
		{
			throw new ParseError("GenCAtom: not matching { } ")  ;
		}
		String predName = expression.substring(pos2+1, pos3).trim();
		
		int pos4 = expression.indexOf("(");
		int pos5 = expression.indexOf(")");
		
		if (pos4==-1 ||  pos5==-1)
		{
			throw new ParseError("GenCAtom: not matching paranthesis");
		}
		
		String varString = expression.substring(pos4+1,pos5);
		if (varString.contains(",")) {
			throw new ParseError("GenCAtom: not unary");
		}
		
		//conceptName = predName;
		//variableName = varString;
		
		return new GenCAtom(predName, varString);
		
		
		
	}
	
	public List<String> getVariables()
	{
		List<String> vars= new ArrayList<String>();
		vars.add(variableName);
		return vars;
	}
	
	@Override
	public String getResource() {
		return this.conceptName;
	}

}
