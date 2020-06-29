package main.java.queryTemplate;

import java.util.ArrayList;
import java.util.List;

public class RoleQAtom extends QueryAtom{
	
	private String roleName;
	private String var1Name;
	private String var2Name;
	


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


	public RoleQAtom(String roleName, String var1Name, String var2Name) {
		super();
		this.roleName = roleName;
		this.var1Name = var1Name;
		this.var2Name = var2Name;
	}


	@Override
	public String toDatalog(String tau) {
		
		String facts = DatalogBuiltIns.deriv+ "("+tau+","+ roleName+","+DatalogBuiltIns.varPrefix+var1Name+","+DatalogBuiltIns.varPrefix+var2Name +")."+"\n";
		facts += DatalogBuiltIns.role+"("+roleName+") . \n "+DatalogBuiltIns.adom+"("+roleName+") . \n";
		return facts;
	}
	
	public static RoleQAtom parseExpression(String expression) throws ParseError {
		int pos1 = expression.indexOf("(");
		int pos2 = expression.indexOf(")");
		
		if (pos1==-1 ||  pos2==-1)
		{
			throw new ParseError("RAtom: not matching paranthesis");
		}
		String predName = expression.substring(0, pos1).trim();
		if (predName.contains("}")) {
			throw new ParseError("RAtom: not a role name");
		}
		
		String varString = expression.substring(pos1+1,pos2);
		if (!varString.contains(",")) {
			throw new ParseError("RAtom: not binary");
		}
		
		int posComma = varString.indexOf(",");
		String var1 = varString.substring(0, posComma).trim();
		String var2 = varString.substring(posComma+1).trim();
		return new RoleQAtom(predName, var1, var2);

		
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
		return "RoleQAtom [roleName=" + roleName + ", var1Name=" + var1Name + ", var2Name=" + var2Name + "]\n";
	}
	
	@Override
	public String getResource() {
		return this.roleName;
	}

}
