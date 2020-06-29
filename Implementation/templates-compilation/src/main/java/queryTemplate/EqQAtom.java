package main.java.queryTemplate;

import java.util.ArrayList;
import java.util.List;

public class EqQAtom extends QueryAtom{
	
	private String individualName;
	private String variableName;
	
	
	public EqQAtom(String individualName, String variableName) {
		super();
		this.individualName = individualName;
		this.variableName = variableName;
	}


	public String getIndividualName() {
		return individualName;
	}


	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}


	public String getVariableName() {
		return variableName;
	}


	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}


	@Override
	public String toDatalog(String tau) {
		
		String facts = DatalogBuiltIns.deriv+ "("+tau+","+ individualName+","+DatalogBuiltIns.varPrefix+variableName+DatalogBuiltIns.nil+")."+"\n";
		facts += DatalogBuiltIns.cst+"("+individualName+") .\n"+ DatalogBuiltIns.adom+"("+individualName+") . \n";
		return facts;
	}

	/*
	 * assumes variable on position 1
	 */
	public static EqQAtom parseExpression(String expression) throws ParseError {
		if (expression.contains("GEN")) {
			throw new ParseError("EqAtom: not an equality atom");
		}
		
		int pos1 = expression.indexOf("=");
		if (pos1==-1) {
			throw new ParseError("EqAtom: not an equality atom");
		
		}
		String var = expression.substring(0,pos1).trim();
		String ind = expression.substring(pos1+1).trim();
		
		return new EqQAtom(ind, var);
		
	}
	public List<String> getVariables()
	{
		List<String> vars= new ArrayList<String>();
		vars.add(variableName);
		return vars;
	}


	@Override
	public String toString() {
		return "EqQAtom [individualName=" + individualName + ", variableName=" + variableName + "]\n";
	}
	
	@Override
	public String getResource() {
		return individualName;
	}
}
