package main.java.queryTemplate;

import java.util.ArrayList;
import java.util.List;

public class SpeRAtom extends SpeAtom{
	
	private String roleName;
	private String var1Name;
	private String var2Name;
	public SpeRAtom(String roleName, String var1Name, String var2Name) {
		super();
		this.roleName = roleName;
		this.var1Name = var1Name;
		this.var2Name = var2Name;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getVar1Name() {
		return var1Name;
	}
	public void setVar1Name(String var1Name) {
		this.var1Name = var1Name;
	}
	public String getVar2Name() {
		return var2Name;
	}
	public void setVar2Name(String var2Name) {
		this.var2Name = var2Name;
	}
	
	
	@Override
	public String toDatalog(String tau) {
		
		String facts = DatalogBuiltIns.spe+"("+ tau + "," + roleName +","+ DatalogBuiltIns.varPrefix+var1Name+","+DatalogBuiltIns.varPrefix+var2Name+ "). \n";
		facts += DatalogBuiltIns.role+"("+roleName+") . \n "+DatalogBuiltIns.adom+"("+roleName+") . \n";
		return facts;		//DatalogBuiltIns.deriv+ "("+tau+ Constants.get(conceptName)+")."+"\n";
	}
	
	public static SpeRAtom parseExpression(String expression) throws ParseError {
		int pos1 = expression.indexOf("SPE");
		int pos2 = expression.indexOf("{");
		if (pos1==-1 || pos2==-1)
		{
			throw new ParseError("SpeRAtom: not a specialising atom");
		}
		
		int pos3 = expression.indexOf("}");
		if (pos3==-1)
		{
			throw new ParseError("SpeRAtom: not matching { } ")  ;
		}
		String predName = expression.substring(pos2+1, pos3).trim();
		
		int pos4 = expression.indexOf("(");
		int pos5 = expression.indexOf(")");
		
		if (pos4==-1 ||  pos5==-1)
		{
			throw new ParseError("SpeRAtom: not matching paranthesis");
		}
		
		String varString = expression.substring(pos4+1,pos5);
		int pos6 = varString.indexOf(",");
		
		if (pos6==-1)
		{
			throw new ParseError("SpeRAtom: not binary");
		}
		String var1 = varString.substring(0,pos6).trim();
		String var2 = varString.substring(pos6+1).trim();
		
		return new SpeRAtom(predName, var1, var2);
		
	}
	
	
	public List<String> getVariables()
	{
		List<String> vars= new ArrayList<String>();
		vars.add(var1Name);
		vars.add(var2Name);
		return vars;
	}
	
	@Override
	public String toString() {
		return "SpeRAtom [roleName=" + roleName + ", var1Name=" + var1Name + ", var2Name=" + var2Name + "] \n";
	}

	@Override
	public String getResource() {
		return this.roleName;
	}
}
