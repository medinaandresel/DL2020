package main.java.queryTemplate;
import java.util.ArrayList;
import java.util.List;

public class GenEqAtom extends GenAtom{
	
	private String individualName;
	private String variableName;
	
	
	public GenEqAtom(String individualName, String variableName) {
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
		
		String facts = DatalogBuiltIns.gen+"("+ tau + "," + individualName +","+ DatalogBuiltIns.varPrefix+variableName+","+DatalogBuiltIns.nil+"). \n";
				//DatalogBuiltIns.deriv+ "("+tau+ Constants.get(conceptName)+")."+"\n";
		facts += DatalogBuiltIns.cst+"("+individualName+") . \n "+DatalogBuiltIns.adom+"("+individualName+") . \n";
		return facts;
	}


	@Override
	public String toString() {
		return "GenEqAtom [individualName=" + individualName + ", variableName=" + variableName + "]";
	}
	
	public static GenEqAtom parseExpression(String expression) throws ParseError {
		
		int pos1 = expression.indexOf("=");
		if (pos1==-1) {
			throw new ParseError("GenEqAtom: not an equality atom");
		
		}
		String var = expression.substring(0,pos1).trim();
		
		String genind = expression.substring(pos1+1).trim();
		
		int pos2= genind.indexOf("GEN");
		int pos3 = genind.indexOf("{");
		if (pos2==-1 || pos3==-1)
		{
			throw new ParseError("GenEqAtom: not a generalising atom");
		}
		
		int pos4 = genind.indexOf("}");
		if (pos4==-1)
		{
			throw new ParseError("GenEqAtom: not matching { } ")  ;
		}
		String ind = genind.substring(pos3+1, pos4).trim();
		//System.out.println(ind);
		
		return new GenEqAtom(ind,var);
	}
	public List<String> getVariables()
	{
		List<String> vars= new ArrayList<String>();
		vars.add(variableName);
		return vars;
	}
	@Override
	public String getResource() {
		return this.individualName;
	}

}
