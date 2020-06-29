package main.java.queryTemplate;

public class DatalogBuiltIns {
	
	static final String nonAns = "nonAns";
	static final String deriv = "deriv";
	static final String predPrefix = "p";
	static final String varPrefix = "v";
	static final String spe="spe";
	static final String gen="gen";
	static final String tauPrefix = "t";
	static final String risa = "risa";
	static final String inv = "inv";
	static final String cisa = "cisa";	
	static final String conc = "conc";
	static final String role = "role";
	static final String nil = "null";
	static final String ddn = "ddn";
	static final String rup = "rup";
	static final String neq = "neq";
	static final String unat = "unat";
	static final String binat = "binat";
	static final String unat2 = "unat2";
	static final String binat2 = "binat2";
	static final String mgu = "mgu";
	static final String subst = "subst";
	static final String nosubst = "nosubst";
	static final String join = "join";
	static final String atom = "atom";
	static final String owlPrefix = "owl:";
	public static final String cst = "cst";
	public static final String nilVLOG=nil;
	
	public static final String VLOG="-VLOG";
	public static final String DLV="-DLV";

	/*
	 * TODO: variable derivations
	 */
	public static final String derivationRulesVLOG = spe+"(?V,?U,?X,?Y) :- " + spe+ "(?V,?U1,?X,?Y),"+risa+"(?U,?U1). \n"+
									      spe+"(?V,?U,?X,?Y) :- " + spe+ "(?V,?U1,?X,?Y),"+inv+"(?U,?U1). \n"+
									      spe+"(?V,?U,?X,?Y) :- " + spe+ "(?V,?U1,?X,?Y),"+cisa+"(?U,?U1),"+conc+"(?U1). \n"+
									      spe+"(?V,?U,?X,"+nil+") :- " + spe+ "(?V,?U1,?X,"+nil+"),"+cisa+"(?U,?U1),"+role+"(?U1),"+conc+"(?U). \n"+
									      spe+"(?V,?A1,?X,"+nil+") :- " + spe+ "(?V,?A2,?X,"+nil+"), "+ ddn +"(?A2,?A1,?R),"+deriv+"(?V2,?R,?Y,?X), "+ neq+"(?V,?V2), "+nonAns+"(?X). \n"+
									      spe+"(?V,?a,?X,"+nil+") :- " + spe+ "(?V,?A,?X,"+nil+"), "+ unat2 +"(?A,?a). \n"+
									      spe+"(?V,?a,?X,"+nil+") :- " + spe+ "(?V,?R,?X,"+nil+"), "+ binat2 +"(?R,?a,?b), "+neq+" (?X,"+nil+"). \n"+
									      spe+"(?V,?b,"+nil+",?X) :- " + spe+ "(?V,?R,"+nil+",?X), "+ binat2 +"(?R,?a,?b), "+neq+" (?X,"+nil+"). \n"+
									      deriv+"(?V,?U,?X,?Y):-"+spe+"(?V,?U,?X,?Y). \n"+
									      
										  gen+"(?V,?U,?X,?Y) :- " + gen+ "(?V,?U1,?X,?Y),"+risa+"(?U1,?U). \n"+
										  gen+"(?V,?U1,?X,?Y) :- " + gen+ "(?V,?U,?X,?Y),"+inv+"(?U,?U1). \n"+
										  gen+"(?V,?U1,?X,?Y) :- " + gen+ "(?V,?U,?X,?Y),"+cisa+"(?U,?U1),"+conc+"(?U). \n"+
										  gen+"(?V,?U1,?X,"+nil+") :- " + gen+ "(?V,?U,?X,"+nil+"),"+cisa+"(?U,?U1),"+role+"(?U),"+conc+"(?U1). \n"+
										  gen+"(?V,?A1,?X,"+nil+") :- " + gen+ "(?V,?A2,?X,"+nil+"), "+ rup +"(?A2,?A1,?R),"+deriv+"(?V2,?R,?Y,?X), "+ neq+"(?V,?V2), "+nonAns+"(?X). \n"+
										  gen+"(?V,?A,?X,"+nil+") :- " + gen+ "(?V,?a,?X,"+nil+"), "+ unat2 +"(?A,?a). \n"+
									      gen+"(?V,?R,?X,"+nil+") :- " + gen+ "(?V,?a,?X,"+nil+"), "+ binat2 +"(?R,?a,?b). \n"+
									      gen+"(?V,?R,"+nil+",?X) :- " + gen+ "(?V,?b,"+nil+",?X), "+ binat2 +"(?R,?a,?b). \n"+
									      deriv+"(?V,?U,?X,?Y):-"+gen+"(?V,?U,?X,?Y). \n "
									      		+ "adom(?X) :- cisa(?X,?Y) . \n"
									      		+" adom(?Y) :- cisa(?X,?Y) . \n"
									      		+ "adom(?X) :- risa(?X,?Y) .\n"
									      		+ "adom(?Y) :- risa(?X,?Y) .\n"
									      		+ "adom(?X) :- inv(?X,?Y) .\n"
									      		+ "adom(?Y) :- inv(?X,?Y) .\n"
									      		+ "adom(?X) :- unat2(?X,?Y) .\n"
									      		+ "adom(?Y) :- unat2(?X,?Y) .\n"
									      		+ "adom(?U) :- bin2(?U,?X,?Y) .\n"
									      		+ "adom(?X) :- bin2(?U,?X,?Y). \n"
									      		+ "adom(?Y) :- bin2(?U,?X,?Y). \n"
									      		+ "adom(?A) :- ddn(?A,?B,?Y). \n"
									      		+ "adom(?B) :- ddn(?A,?B,?Y). \n"
									      		+ "adom(?Y) :- ddn(?A,?B,?Y). \n"+
									      		/*+ "adom(?V) :- spe(?V,?U,?X,?Y). \n"
									      		+ "adom(?X) :- spe(?V,?U,?X,?Y). \n"
									      		+ "adom(?Y) :- spe(?V,?U,?X,?Y). \n"+
									      		  "adom(?V) :- gen(?V,?U,?X,?Y). \n"
									      		+ "adom(?X) :- gen(?V,?U,?X,?Y). \n"
									      		+ "adom(?Y) :- gen(?V,?U,?X,?Y). \n"+
									      		  "adom(?V) :- deriv(?V,?U,?X,?Y). \n"
									      		+ "adom(?X) :- deriv(?V,?U,?X,?Y). \n"
									      		+ "adom(?Y) :- deriv(?V,?U,?X,?Y). \n"+*/
									      		"adom(null). adom("+DatalogBuiltIns.owlPrefix+"Thing). \n"+
									      		"eq(?X,?X) :- adom(?X). \n eq(?X,?Y) :- eq(?Y,?X). \n eq(?X,?Z) :- eq(?X,?Y), eq(?Y,?Z). \n"+
									      		"neq(?X,?Y) :- adom(?X), adom(?Y), ~ eq(?X,?Y)." ;
									      	//	+ "neq(?X,null) :- unat(?A,?X). "
									      	//	+ "neq(?X,null) :- binat(?A,?X,?Y)."
									      	//	+ "neq(?Y,null) :- binat(?A,?X,?Y)."
									      	//	+ "neq(null,?X) :- neq(?X,null). \n";
									      		
									      					

	public static final String derivationRulesDLV = spe+"(V,U,X,Y) :- " + spe+ "(V,U1,X,Y),"+risa+"(U,U1). \n"+
		      spe+"(V,U,X,Y) :- " + spe+ "(V,U1,X,Y),"+inv+"(U,U1). \n"+
		      spe+"(V,U,X,Y) :- " + spe+ "(V,U1,X,Y),"+cisa+"(U,U1),"+conc+"(U1). \n"+
		      spe+"(V,U,X,"+nil+") :- " + spe+ "(V,U1,X,"+nil+"),"+cisa+"(U,U1),"+role+"(U1)"+conc+"(U). \n"+
		      spe+"(V,A1,X,"+nil+") :- " + spe+ "(V,A2,X,"+nil+"), "+ ddn +"(A2,A1,R),"+deriv+"(V2,R,Y,X), "+ "V != V2, "+nonAns+"(X). \n"+
		      spe+"(V,U,X,"+nil+") :- " + spe+ "(V,A,X,"+nil+"), "+ unat +"(A,U). \n"+
		      spe+"(V,U,X,"+nil+") :- " + spe+ "(V,R,X,"+nil+"), "+ binat +"(R,U,U2), "+" X !="+nil+". \n"+
		      spe+"(V,U2,"+nil+",X) :- " + spe+ "(V,R,"+nil+",X), "+ binat +"(R,U,U2), "+" X !="+nil+". \n"+
		      deriv+"(V,U,X,Y):-"+spe+"(V,U,X,Y). \n"+
		      
			  gen+"(V,U,X,Y) :- " + gen+ "(V,U1,X,Y),"+risa+"(U1,U). \n"+
			  spe+"(V,U,X,Y) :- " + spe+ "(V,U1,X,Y),"+inv+"(U,U1). \n"+
			  gen+"(V,U1,X,Y) :- " + gen+ "(V,U,X,Y),"+cisa+"(U,U1),"+conc+"(U). \n"+
			  gen+"(V,U1,X,"+nil+") :- " + gen+ "(V,U,X,"+nil+"),"+cisa+"(U,U1),"+role+"(U)"+conc+"(U1). \n"+
			  gen+"(V,A1,X,"+nil+") :- " + gen+ "(V,A2,X,"+nil+"), "+ rup +"(A2,A1,R),"+deriv+"(V2,R,Y,X), "+ "V != V2, "+nonAns+"(X). \n"+
			  gen+"(V,A,X,"+nil+") :- " + gen+ "(V,U,X,"+nil+"), "+ unat +"(A,U). \n"+
		      gen+"(V,R,X,"+nil+") :- " + gen+ "(V,U,X,"+nil+"), "+ binat +"(R,U,U2). \n"+
		      gen+"(V,R,"+nil+",X) :- " + gen+ "(V,U2,"+nil+",X), "+ binat +"(R,U,U2). \n"+
		      deriv+"(V,U,X,Y):-"+gen+"(V,U,X,Y). \n";	
	
	public static final String mguDLV = mgu + "(V1,V2,X1,X2,Y1,Y2) :-"+spe+"(V1,U,X1,Y1), "+spe+"(V2,U,X2,Y2), V1 != V2, "+nonAns+"(X1),"+nonAns+"(Y1). \n"+
								 mgu + "(V1,V2,X1,X2,Y2,Y1) :-"+spe+"(V1,U,X1,Y1), "+spe+"(V2,U,X2,Y2), V1 != V2, "+nonAns+"(X1),"+nonAns+"(Y2). \n"+
			 					 mgu + "(V1,V2,X2,X1,Y1,Y2) :-"+spe+"(V1,U,X1,Y1), "+spe+"(V2,U,X2,Y2), V1 != V2, "+nonAns+"(X2),"+nonAns+"(Y1). \n"+
			 					 mgu + "(V1,V2,X2,X1,Y2,Y1) :-"+spe+"(V1,U,X1,Y1), "+spe+"(V2,U,X2,Y2), V1 != V2, "+nonAns+"(X2),"+nonAns+"(Y2). \n"+
			 					 nosubst +"(X1,"+nil+") :- "+ mgu+"(V1,V2,X1,"+nil+",Y1,Y2), "+join+"(V1,V3,X1), V3 != V2. \n"+
			 					 nosubst +"(Y1,"+nil+") :- "+ mgu+"(V1,V2,X1,X2,Y1,"+nil+"), "+join+"(V1,V3,X1), V3 != V2. \n"+
			 					 subst + "(X1,X2):- "+mgu+ "(V1,V2,X1,X2,Y1,Y2), not "+ nosubst+"(X1,X2). \n"+
			 					 subst + "(Y1,Y2):- "+mgu+ "(V1,V2,X1,X2,Y1,Y2), not "+ nosubst+"(Y1,Y2). \n"+
			 					 spe+"(V,U,X2,Y1) :- "+spe+"(V,U,X1,Y1),"+subst + "(X1,X2). \n"+
			 					 spe+"(V,U,X1,Y2) :- "+spe+"(V,U,X1,Y1),"+subst + "(Y1,Y2). \n"+
			 					 "nonjoin(Y) :- mgu(V1,V2,X,Y,X1,Y2), spe(V3,U,Y,Z), V3 != V2, V3 != V1.\n" + 
			 					 "nonjoin(Y) :- mgu(V1,V2,X,Y1,Y2,Y), spe(V3,U,Z,Y), V3 != V2, V3 != V1.\n" + 
			 					 "subst(Y,null) :- mgu(V1,V2,X,Y,Y1,Y2), not nonjoin(Y), nonAns(Y), Y != null.\n" + 
			 					 "subst(Y,null) :- mgu(V1,V2,X,Y1,Y2,Y),  not nonjoin(Y), nonAns(Y), Y != null. \n";
	public static String ontPrefix;
	public static final String adom = "adom";
	public static final String OPTIM = "-opt";
	public static final String NO_OPTIM = "-run";
	
	public static void setOntPrefix(String ontPrefix) {
		DatalogBuiltIns.ontPrefix = ontPrefix;
	}

	
	
	
	public static String patterns(String string) {
		Integer k = Integer.valueOf(string);
		String patternRules = "cisa1G(?X,?Y) :- signG(?X),subclassOf(?X,?Y). \n" +
				  "cisa1S(?X,?Y) :- signS(?Y),subclassOf(?X,?Y). \n" +
				  "risa1S(?X,?Y) :- signS(?Y), subpropOf(?X,?Y). \n" +
				  "risa1G(?X,?Y) :- signG(?X), subpropOf(?X,?Y). \n" +
				  "cisa(?X,?Y) :- cisa1G(?X,?Y) . \n"+
				  "cisa(?X,?Y) :- cisa1S(?X,?Y) . \n"+
				  "risa(?X,?Y) :- risa1G(?X,?Y) . \n"+
				  "risa(?X,?Y) :- risa1S(?X,?Y) . \n";
		
		for (int i = 2; i<=k ; i++)
		{
			String ruleCISA = "cisa"+k+"G(?Z,?Y) :- cisa"+(k-1)+"G(?X,?Z), subclassOf(?Z,?Y) . \n"+
					"cisa"+k+"S(?X,?Z) :- subclassOf(?X,?Z), cisa"+(k-1)+"S(?Z,?Y). \n"+
							   "cisa(?X,?Y) :- cisa"+k+"G(?X,?Y) . \n"+
							   "cisa(?X,?Y) :- cisa"+k+"S(?X,?Y) . \n";
			patternRules += ruleCISA;
			
			String ruleRISA = "risa"+k+"G(?Y,?Z) :- risa"+(k-1)+"G(?X,?Y), subpropOf(?Y,?Z).\n" + 
							  "risa"+k+"S(?X,?Y) :- subpropOf(?X,?Y), risa"+(k-1)+"S(?Y,?Z). \n"+
							  "risa(?X,?Y) :- risa"+k+"S(?X,?Y). \n"+
							  "risa(?X,?Y) :- risa"+k+"G(?X,?Y). \n";
							   
			patternRules += ruleRISA;
			
		}
		return patternRules;
	}
	
	

	
	
}
