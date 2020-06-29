package main.java.queryTemplate;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	// private static int counterVars=0;
	private static int counterPred=0;

	/*
	 *  dictionaries to keep track of Datalog constants for variables and 
	 *  predicates; key=Pred value=DatalogCst
	 */
	//private static Map<String,String> varsMap;
	private static Map<String,String> predMap;
	
	private static String getNewPredCst() {
		counterPred ++;
		return DatalogBuiltIns.predPrefix+counterPred;
	}

	public static String get(String conceptName) {
		if (predMap == null)
		{
			predMap = new HashMap<String, String>();
		}
		
		if (predMap.get(conceptName)==null)
		{
			predMap.put(conceptName, getNewPredCst());
		}
		return predMap.get(conceptName);
	}
	
	


}
