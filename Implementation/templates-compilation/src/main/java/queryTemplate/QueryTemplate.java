package main.java.queryTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryTemplate {
	
	private Set<String> ansVars;
	private List<TemplateAtom> templateList;
	private Set<String> joinVars;
	private String vlogQuery;
	private String views = "";
	private String facts = "";
	private String reformArity = "";
	private String queryViewNoAnsVar = "";
	
	private QueryTemplate(List<TemplateAtom> atoms, Set<String> ansVars) {
		this.templateList = atoms;
		this.ansVars = ansVars;
		this.joinVars = new HashSet<String>();
		//templateList.forEach(atom -> {atom.getVariables()});
	}


	public static QueryTemplate parseExpression(String expression) throws ParseError {
		List <TemplateAtom> atoms = new ArrayList<TemplateAtom>();
		
		int pos1 = expression.indexOf(":-");
		if (pos1 == -1)
		{
			throw new ParseError(":- is required");
		}
		String headString = expression.substring(0, pos1);
		Set<String> vars = new HashSet<String>();
		for (String v : headString.split(","))
		{
			vars.add(v.trim());
		}
		
		
		String bodyString = expression.substring(pos1+2).trim();
		
		
		String[] atomsStrings = bodyString.split("AND");
		int  i = 1;
		for (String atomString : atomsStrings)
		{
			
				TemplateAtom atom = TemplateAtom.parseExpression(atomString.trim());
				atom.setIdentifier(DatalogBuiltIns.tauPrefix+i);
				i++;
				atoms.add(atom);
			
		}

		return new QueryTemplate(atoms, vars);
		
	}


	@Override
	public String toString() {
		return "QueryTemplate [templateList=" + templateList + "]";
	}


	public List<TemplateAtom> getTemplateList() {
		return templateList;
	}	
	
	
	public String toDatalog(String reasoner)
	{
		//System.out.println("**** "+reasoner);
		if (reasoner.equals(DatalogBuiltIns.DLV))
		{
			return dlvRules();
		}
		if (reasoner.equals(DatalogBuiltIns.VLOG))
			return vlogRules();
		
		if (reasoner.equals(DatalogBuiltIns.OPTIM))
			return vlogOptimizRules();
		if (reasoner.contentEquals(DatalogBuiltIns.NO_OPTIM))
			return vlogRules();
		return null;
	}
	
	


	private String vlogOptimizRules() {
		String programString = "atom(?U, ?X, null) :- unat(?U,?X), conc(?U).\n" + 
				"atom(?U, ?U, null) :- cst(?U).\n" + 
				"atom(?U, ?X, ?Y) :- binat(?U, ?X, ?Y), role(?U). \n"+
		        "atom(?U, ?X, null) :- binat(?U, ?X, ?Y), role(?U). \n"+
		        "atom(?U, null, ?Y) :- binat(?U, ?X, ?Y), role(?U). \n";
		

		programString += "succAt(?Vtau, ?U, ?V) :- deriv(?Vtau , ?U, ?X, ?Y), deriv(?Vtau, ?V, ?X2, ?Y2), op(?V, ?U). \n";
		String joinFacts = "";
		
		String encodesQueryHead = "encodesQuery(";
		String encodesQueryBody = "";
		String queryView = "query(";

		String queryViewBody = "";
		String vars = "";
		
		for (TemplateAtom atom : templateList) {
			
			String succQueryHead = "succQuery(";
			String succQueryBody = "";
			String succQueryVars1 = "";
			
			
			for (String var : atom.getVariables())
			{
				
				for (TemplateAtom a : templateList)
				{
					if (!a.equals(atom))
					{
						if (a.getVariables().contains(var))
						{
							joinFacts += DatalogBuiltIns.join+ "("+atom.getIdentifier()+","+a.getIdentifier()+","+DatalogBuiltIns.varPrefix+var+"). \n";
							joinVars.add(var);
							//joinFacts = joinFacts + fact;
						}
					}
					
				}

			}
			for (TemplateAtom a : templateList)
			{
				succQueryVars1+="?U"+a.getIdentifier();
				if (templateList.lastIndexOf(a)<templateList.size()-1)
				{
					succQueryVars1 += ",";
				}
			}
			// succQueryHead 
			String succQueryVars = succQueryVars1 +","+succQueryVars1.replace("?U"+atom.getIdentifier(), "?V"+atom.getIdentifier());
			succQueryHead += succQueryVars+") :-";
			
			// succQueryBody
			succQueryBody += "encodesQuery("+succQueryVars1+"), succAt("+atom.getIdentifier()+","+"?U"+atom.getIdentifier()+","+"?V"+atom.getIdentifier()+"). \n";
			
			// add succQuery rule
			programString += succQueryHead + succQueryBody;
			
			encodesQueryBody += DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?U"+atom.getIdentifier()+",?X"+atom.getIdentifier()+",?Y"+atom.getIdentifier()+")";
			encodesQueryHead +="?U"+atom.getIdentifier();
			if (templateList.indexOf(atom)<templateList.size()-1)
			{
				encodesQueryHead+=",";
				encodesQueryBody += ",";
				
			}
			else {
				encodesQueryBody += ". \n";
			}
			
			// define views
			
			facts += atom.toDatalog()+" \n";
			facts += "adom("+atom.getIdentifier()+"). \n";
			
			queryViewBody += DatalogBuiltIns.atom+"(?U"+atom.getIdentifier()+",";
			
			
			// add variables to atom_tau
			int varsNumber = 0;
			for (String v : atom.getVariables()) {
				
					
					varsNumber++;
					//if (joinVars.contains(v) || ansVars.contains(v)) 
						queryViewBody += "?"+v;
					//else 
					//	queryViewBody += DatalogBuiltIns.nilVLOG;
					if (atom.getVariables().lastIndexOf(v)!=atom.getVariables().size()-1)
						queryViewBody +=",";
				}
				if (varsNumber==1)
					queryViewBody += ","+DatalogBuiltIns.nilVLOG;
				queryViewBody += ")";
				
					
				vars += "?U"+atom.getIdentifier();
				
					if (templateList.lastIndexOf(atom)!=templateList.size()-1)
					{
						queryViewBody +=",";
						vars += ",";
						
					}
					
				 
		}
	
		for (String v : ansVars)
		{
			vars += ",?"+v;
			//if (ansVars.lastIndexOf(v)!=ansVars.size()-1)
			//	vars += ",";
		}
		// define query view 
		queryView += vars+") :- "+queryViewBody + ","+encodesQueryHead+") .\n";
		vlogQuery = "query("+vars+")";
		
		for (String v : this.joinVars) {
			if (!ansVars.contains(v))
				facts += DatalogBuiltIns.nonAns + "("+DatalogBuiltIns.varPrefix+v+"). \n";
		}
		
		
		
		
		facts += joinFacts;
		
		for (String var : ansVars)
		{
			facts += "adom("+DatalogBuiltIns.varPrefix+var+"). \n";
		}
		
		// add encodesQueryRule
		String encodesQueryRule = encodesQueryHead + ") :- "+encodesQueryBody+"\n";
		
		views = programString + encodesQueryRule + queryView +"\n";
		
		for (String var : joinVars)
		{
			facts += "adom("+DatalogBuiltIns.varPrefix+var+"). \n";
		}
		//programString += inputFacts;
		//System.out.println(inputFacts);
		
		System.out.println("=====Input facts===");
		System.out.println(facts);
		System.out.println("--------------------");
		return facts;
	}


	private String vlogRules() {
		String programString = "";
		String inputFacts = "";
		
		
		
		String joinFacts = "";
		
		
		String queryView = "query(";
		
		
			
		
		String queryViewBody = "";
		String vars = "";
		
		for (TemplateAtom atom : templateList) {
			for (String var : atom.getVariables())
			{
				
				for (TemplateAtom a : templateList)
				{
					if (!a.equals(atom))
					{
						if (a.getVariables().contains(var))
						{
							joinFacts += DatalogBuiltIns.join+ "("+atom.getIdentifier()+","+a.getIdentifier()+","+DatalogBuiltIns.varPrefix+var+"). \n";
							joinVars.add(var);
							//joinFacts = joinFacts + fact;
						}
					}
					
				}
				
			}
			// define views
			facts += atom.toDatalog()+" \n";
			facts += "adom("+atom.getIdentifier()+"). \n";
			programString += DatalogBuiltIns.atom+atom.getIdentifier()+"(?U,"+DatalogBuiltIns.nilVLOG+","+ DatalogBuiltIns.nilVLOG+ ") :- "+
					 		 DatalogBuiltIns.unat+"(?U,?X),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?U,"+DatalogBuiltIns.nilVLOG+","+DatalogBuiltIns.nilVLOG+"),"+
					 		 DatalogBuiltIns.conc+"(?U).\n"+
							 DatalogBuiltIns.atom+atom.getIdentifier()+"(?U,?X,"+ DatalogBuiltIns.nilVLOG+ ") :- "+
							 DatalogBuiltIns.unat+"(?U,?X),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?U,?X1,"+DatalogBuiltIns.nilVLOG+"),"+
					          DatalogBuiltIns.conc+"(?U).\n"+
							 
							DatalogBuiltIns.atom+atom.getIdentifier()+"(?X,"+DatalogBuiltIns.nilVLOG+","+ DatalogBuiltIns.nilVLOG+ ") :- "+
							 DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?X,"+DatalogBuiltIns.nilVLOG+","+DatalogBuiltIns.nilVLOG+"),"+
							DatalogBuiltIns.cst+"(?X).\n"+
	 						DatalogBuiltIns.atom+atom.getIdentifier()+"(?X,?X,"+ DatalogBuiltIns.nilVLOG+ ") :- "+
	 						 DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?X,?V,"+DatalogBuiltIns.nilVLOG+"),"+
	 						DatalogBuiltIns.cst+"(?X).\n"+
					          
					          DatalogBuiltIns.atom+atom.getIdentifier()+"(?U,"+DatalogBuiltIns.nilVLOG+","+ DatalogBuiltIns.nilVLOG+ ") :- "+
							  DatalogBuiltIns.binat+"(?U,?X,?Y),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?U,"+DatalogBuiltIns.nilVLOG+","+DatalogBuiltIns.nilVLOG+"),"+
						      DatalogBuiltIns.role+"(?U).\n"+
					         DatalogBuiltIns.atom+atom.getIdentifier()+"(?U,?X,"+ DatalogBuiltIns.nilVLOG+ ") :- "+
							 DatalogBuiltIns.binat+"(?U,?X,?Y),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?U,?X1,"+DatalogBuiltIns.nilVLOG+"),"+
					          DatalogBuiltIns.role+"(?U).\n" +
					         DatalogBuiltIns.atom+atom.getIdentifier()+"(?U,"+ DatalogBuiltIns.nilVLOG+ ",?X) :- "+
							 DatalogBuiltIns.binat+"(?U,?Y,?X),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?U,"+DatalogBuiltIns.nilVLOG+",?X1),"+
					          DatalogBuiltIns.role+"(?U).\n" +
					         DatalogBuiltIns.atom+atom.getIdentifier()+"(?U,?X,?Y) :- "+
							 DatalogBuiltIns.binat+"(?U,?X,?Y),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",?U,?X1,?Y1), "+
					          DatalogBuiltIns.role+"(?U).\n";
			queryViewBody += DatalogBuiltIns.atom+atom.getIdentifier()+"(?U"+atom.getIdentifier()+",";
			
			
			// add variables to atom_tau
			int varsNumber = 0;
			for (String v : atom.getVariables()) {
				
					
					varsNumber++;
					//if (joinVars.contains(v) || ansVars.contains(v)) 
						queryViewBody += "?"+v;
					//else 
					//	queryViewBody += DatalogBuiltIns.nilVLOG;
					if (atom.getVariables().lastIndexOf(v)!=atom.getVariables().size()-1)
						queryViewBody +=",";
				}
				if (varsNumber==1)
					queryViewBody += ","+DatalogBuiltIns.nilVLOG;
				queryViewBody += ")";
				
					
				vars += "?U"+atom.getIdentifier();
				
					if (templateList.lastIndexOf(atom)!=templateList.size()-1)
					{
						queryViewBody +=",";
						vars += ",";
						
					}
					else {
						for (String v : ansVars)
						{
							//queryViewBody += ", neq(?"+v+","+DatalogBuiltIns.nilVLOG+")";
						}
						queryViewBody+=".";
						
					}
				 
		}
	
		for (String v : ansVars)
		{
			vars += ",?"+v;
			//if (ansVars.lastIndexOf(v)!=ansVars.size()-1)
			//	vars += ",";
		}
		// define query view 
		queryView += vars+") :- "+queryViewBody;
		vlogQuery = "query("+vars+")";
		
		for (String v : this.joinVars) {
			if (!ansVars.contains(v))
				facts += DatalogBuiltIns.nonAns + "("+DatalogBuiltIns.varPrefix+v+"). \n";
		}
		
		views = programString + queryView +"\n";
		
		
		facts += joinFacts;
		
		for (String var : ansVars)
		{
			facts += "adom("+DatalogBuiltIns.varPrefix+var+"). \n";
		}
		
		for (String var : joinVars)
		{
			facts += "adom("+DatalogBuiltIns.varPrefix+var+"). \n";
		}
		//programString += inputFacts;
		//System.out.println(inputFacts);
		
		System.out.println("=====Input facts===");
		System.out.println(facts);
		System.out.println("--------------------");
		return facts;
	}


	private String dlvRules() {
		String programString = "";
		String inputFacts = "";
		for (String v : this.joinVars) {
			if (!ansVars.contains(v))
				inputFacts += DatalogBuiltIns.nonAns + "(v_"+v+"). \n";
		}
		
		
		String joinFacts = "";
		
		
		String queryView = "query(";
		
		
			
		
		String queryViewBody = "";
		String vars = "";
		
		for (TemplateAtom atom : templateList) {
			for (String var : atom.getVariables())
			{
				
				for (TemplateAtom a : templateList)
				{
					if (!a.equals(atom))
					{
						if (a.getVariables().contains(var))
						{
							joinFacts += DatalogBuiltIns.join+ "("+atom.getIdentifier()+","+a.getIdentifier()+","+DatalogBuiltIns.varPrefix+var+").";
							joinVars.add(var);
							//joinFacts = joinFacts + fact;
						}
					}
					
				}
				
			}
			// define views
			inputFacts += atom.toDatalog()+" \n";
			programString += DatalogBuiltIns.atom+atom.getIdentifier()+"(U,"+DatalogBuiltIns.nil+","+ DatalogBuiltIns.nil+ ") :- "+
					 		 DatalogBuiltIns.unat+"(U,X),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",U,"+DatalogBuiltIns.nil+","+DatalogBuiltIns.nil+"),"+
					 		 DatalogBuiltIns.conc+"(U).\n"+
							 DatalogBuiltIns.atom+atom.getIdentifier()+"(U,X,"+ DatalogBuiltIns.nil+ ") :- "+
							 DatalogBuiltIns.unat+"(U,X),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",U,X1,"+DatalogBuiltIns.nil+"),"+
					          DatalogBuiltIns.conc+"(U).\n"+
					          DatalogBuiltIns.atom+atom.getIdentifier()+"(U,"+DatalogBuiltIns.nil+","+ DatalogBuiltIns.nil+ ") :- "+
							  DatalogBuiltIns.binat+"(U,X,Y),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",U,"+DatalogBuiltIns.nil+","+DatalogBuiltIns.nil+"),"+
						      DatalogBuiltIns.role+"(U).\n"+
					         DatalogBuiltIns.atom+atom.getIdentifier()+"(U,X,"+ DatalogBuiltIns.nil+ ") :- "+
							 DatalogBuiltIns.binat+"(U,X,Y),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",U,X1,"+DatalogBuiltIns.nil+"),"+
					          DatalogBuiltIns.role+"(U).\n" +
					         DatalogBuiltIns.atom+atom.getIdentifier()+"(U,"+ DatalogBuiltIns.nil+ ",X) :- "+
							 DatalogBuiltIns.binat+"(U,Y,X),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",U,"+DatalogBuiltIns.nil+",X1),"+
					          DatalogBuiltIns.role+"(U).\n" +
					         DatalogBuiltIns.atom+atom.getIdentifier()+"(U,X,Y) :- "+
							 DatalogBuiltIns.binat+"(U,X,Y),"+ DatalogBuiltIns.deriv+"("+atom.getIdentifier()+",U,X1,Y1), "+
							 		//+ "X1 != "+DatalogBuiltIns.nil  
							       // +", Y1 != "+DatalogBuiltIns.nil+","+
					          DatalogBuiltIns.role+"(U).\n";
			queryViewBody += DatalogBuiltIns.atom+atom.getIdentifier()+"(U"+atom.getIdentifier()+",";
			
			
			// add variables to atom_tau
			int varsNumber = 0;
			for (String v : atom.getVariables()) {
				
					
					varsNumber++;
					if (joinVars.contains(v) || ansVars.contains(v)) 
						queryViewBody += v;
					else 
						queryViewBody += DatalogBuiltIns.nil;
					if (atom.getVariables().lastIndexOf(v)!=atom.getVariables().size()-1)
						queryViewBody +=",";
				}
				if (varsNumber==1)
					queryViewBody += ","+DatalogBuiltIns.nil;
				queryViewBody += ")";
					
				vars += "U"+atom.getIdentifier();
				
					if (templateList.lastIndexOf(atom)!=templateList.size()-1)
					{
						queryViewBody +=",";
						vars += ",";
						
					}
					else {
						for (String v : ansVars)
						{
							queryViewBody += ", "+v+"!="+DatalogBuiltIns.nil;
						}
						queryViewBody+=".";
						
					}
					
					
					
							 
		}
		
		
		
		for (String v : ansVars)
		{
			vars += ","+v;
			//if (ansVars.lastIndexOf(v)!=ansVars.size()-1)
			//	vars += ",";
		}
		// define query view 
		queryView += vars+") :- "+queryViewBody;
		
		programString += queryView +"\n";
		
		
		programString += joinFacts;
		
		
		programString += inputFacts;
		//System.out.println(inputFacts);
		
		
		return programString;
	}


	public String getVLOGQuery() {
		return vlogQuery;
	}


	public String getSignatureFactsVLOG() {
		String signatureRules = "";
		for (TemplateAtom templAtom : templateList) {
			if (templAtom instanceof SpeAtom  ) {
				signatureRules += "signS("+templAtom.getResource()+"). ";
			}
			if (templAtom instanceof GenAtom  ) {
				signatureRules += "signG("+templAtom.getResource()+"). ";
			}
			
		}
		
		return signatureRules;
	}


	public String getViews() {
		return views;
	}
	
	
	
	
	public String getReformulationsRules()
	{
		String strictRules = "";
		String neutralRules = "";
		String notMax = "";
		String sMax = "";
		String gMax = "";
		String maxGen = "maxGen";
		String maxSpe = "maxSpe";
		String minSpe = "";
		String minGen = "";
		
		/// auxiliary
		String arity = "(";
		String arityPart1 = "";
		String arityPart2 = "";
		
		String bodyMax = vlogQuery+",";
		String newAnsVar = "";
		String varsToBeReplaced = "";
		
		for (int i=0; i< ansVars.size();i++) {
			varsToBeReplaced += "?"+((String)ansVars.toArray()[i]).toString();
			newAnsVar += "?"+((String)ansVars.toArray()[i]).toString()+i;
			if (i<ansVars.size()-1) {
				newAnsVar += ",";
				varsToBeReplaced += ",";
			}
				
		}
		String otherAnsVarQuery = this.getVLOGQuery().replace(varsToBeReplaced, newAnsVar);
		//System.out.println(varsToBeReplaced);
		//System.out.println("!!!!! "+this.getVLOGQuery()+" -> "+otherAnsVarQuery);
		
		String auxEncoding = projectAwayAnsVar();
		for (TemplateAtom atom : templateList)
		{
		
			String tau = atom.getIdentifier();
			arityPart1 += "?U"+tau+",";
			arityPart2 += "?V"+tau;
			String strictTau = "strict("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2) :- op(?V1,?V2), deriv("+tau+",?V1,?G,?H), "+
								"deriv("+tau+",?V2,?Q,?W),"+vlogQuery.replace("?U"+tau, "?V1")+
								//","+otherAnsVarQuery.replace("?U"+tau, "?V2")+
								//","+vlogQuery.replace("?U"+tau, "?V2")+
								", ~ "+vlogQuery.replace("?U"+tau, "?V2")+". \n";
			strictRules += strictTau;
			
			String neutralTau = "neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2) :- op(?V1,?V2), deriv("+tau+",?V1,?G,?H), deriv("+tau+ ",?V2,?Q,?W), "+
					vlogQuery.replace("?U"+tau, "?V1")+", ~ strict("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2) . \n";
			neutralRules += neutralTau;
			
			//neutralRules += "neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+","+" ?V3) :- neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2), neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")
		    //            +",?V3) . \n";
			
			
			//notMax += "notMaxS("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2) :- neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+", ?V2), neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+", ?V3), neq(?V2, ?V3) . \n";
			//notMax += "notMaxG("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V3")+",?V2) :- neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+", ?V2), neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+",?V3), neq(?V1, ?V2) . \n";
			
			//String sMaxTau = "smax("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2) :- neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+", ?V2), ~ notMaxS("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2) . \n";
			//sMax += sMaxTau;
			
			//String gMaxTau = "gmax("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+",?V1) :- neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+", ?V2), ~ notMaxG("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+",?V1) . \n";
			//gMax += gMaxTau;

			
			// new rules
			String sMaxTau = "smax("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2) :- neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+", ?V2), ~ neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+",?V3), neq(?V2, ?V3) . \n";
			sMax += sMaxTau;
			
			//String gMaxTau = "gmax("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+",?V1) :- neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+", ?V2), ~ neutral("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+",?V3),  . \n";
			String gMaxTau = "gmax("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V2")+",?V1) :- smax("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+", ?V2), ~ strict("+tau+","+projectAwayAnsVar().replace("?U"+tau, "?V1")+",?V2). \n";
			gMax += gMaxTau;
			
			
			bodyMax += "Gamma("+tau+","+auxEncoding+",?V"+tau+")";
			auxEncoding = auxEncoding.replace("?U"+tau, "?V"+tau);
			//System.out.println("###### "+auxEncoding);
			if (templateList.indexOf(atom)!=templateList.size()-1)
			{
				bodyMax += ",";
				arityPart2 +=",";
			}
			else bodyMax += ". \n";

		}
		arity += arityPart1 + arityPart2+")";
		maxSpe += arity +":-"+bodyMax.replace("Gamma", "smax");
		maxGen += arity +":-"+bodyMax.replace("Gamma", "gmax");
		//System.out.println("§§§§§ "+bodyMax);
		for (TemplateAtom atom : templateList) {
			String tau = atom.getIdentifier();
			minSpe +="minSpe"+ arity.replace("?V"+tau, "?X") +":- maxSpe"+arity+", strict("+tau+","+arityPart2+", ?X) . \n";
			minGen +="minGen"+ arity.replace("?V"+tau, "?X") +":- maxGen"+arity+", strict("+tau+","+arityPart2.replace("?V"+tau, "?X")+",?V"+tau+") . \n";
			
		}
		
		this.reformArity =arity;
		return strictRules + neutralRules +
				// notMax +
				 sMax + gMax +
				 maxGen + maxSpe + minSpe + minGen;
		
	}

	public String getReformArity() {
		return reformArity;
	}


	public void setReformArity(String reformArity) {
		this.reformArity = reformArity;
	}


	public String projectAwayAnsVar() {
		if (queryViewNoAnsVar=="") {
			for (TemplateAtom atom : templateList)
			{
				queryViewNoAnsVar += "?U"+atom.getIdentifier();
				if (templateList.indexOf(atom)!=templateList.size()-1)
					queryViewNoAnsVar += ",";
			}
		}
		
		return queryViewNoAnsVar;
	}


	public String addAnsVars() {
		String s = "";
		int i=0;
		for (String var : ansVars)
		{
			i++;
			s += "?"+var;
			if (i<ansVars.size()) {
				s += ",";
			}
		}
		return s;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
