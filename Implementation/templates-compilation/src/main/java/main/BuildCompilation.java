package main.java.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.server.ExportException;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import org.semanticweb.rulewerk.core.model.api.PositiveLiteral;
import org.semanticweb.rulewerk.core.model.api.QueryResult;
import org.semanticweb.rulewerk.core.model.api.Term;
import org.semanticweb.rulewerk.core.reasoner.Algorithm;
import org.semanticweb.rulewerk.core.reasoner.KnowledgeBase;
import org.semanticweb.rulewerk.core.reasoner.LogLevel;
import org.semanticweb.rulewerk.core.reasoner.QueryResultIterator;
import org.semanticweb.rulewerk.core.reasoner.Reasoner;
import org.semanticweb.rulewerk.core.reasoner.RuleRewriteStrategy;
import org.semanticweb.rulewerk.core.reasoner.implementation.VLogReasoner;

import org.semanticweb.rulewerk.parser.ParsingException;
import org.semanticweb.rulewerk.parser.RuleParser;

import main.java.queryTemplate.*;

//import org.semanticweb.vlog4j.core.reasoner.*;
//import org.semanticweb.rulewerk.parser.RuleParser;

/**
 * Datalog Program to Compile the Query Space over Data
 *
 */
public class BuildCompilation {

	private static QueryTemplate template;

	private static String outputfile;

	public static String compilationRulesVLOG(String file) {
		String prefixes = readFromFile(file);
		// String rules = DatalogBuiltIns.derivationRulesVLOG;

		String rules = template.toDatalog(DatalogBuiltIns.VLOG);

		return prefixes + rules;
	}

	private static void runDLV(String out) {
		// String fileQTemplate = in;
		// QueryTemplate template = null;
		// try {
		// template = readTemplate(fileQTemplate);
		// } catch (ParseError e) {
		// e.printStackTrace();
		// }

		System.out.println(template.toString());
		System.out.println("DATALOG rules");
		System.out.println(template.toDatalog(DatalogBuiltIns.DLV));
		System.out.println(DatalogBuiltIns.derivationRulesVLOG);

		// String fileDataPatterns = args[1];
		// try {
		// patterns = transformDataPatterns(fileQTemplate);
		// } catch (ParseError e) {
		// e.printStackTrace();
		// }

		// writeDatalogProgram(args[1],
		// template.toDatalog()+DatalogBuiltIns.derivationRulesDLV+DatalogBuiltIns.mguDLV);
	}

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 *
	 * Note: The VLog C++ backend performs its own logging that is configured
	 * independently with the reasoner. It is also possible to specify a separate
	 * log file for this part of the logs.
	 *
	 * @param level the log level to be used
	 */
	static void configureLogging(Level level) {
		// Create the appender that will write log messages to the console.
		final ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		final String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(level);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}

	private static KnowledgeBase performDerivation(String file, String option) throws ParsingException, IOException {
		// System.out.println();
		System.out.println("PROGRAM FILE");
		System.out.println(readFromFile(file) + template.getSignatureFactsVLOG() + template.toDatalog(option)
				+ DatalogBuiltIns.derivationRulesVLOG);
		System.out.println("============");

		final KnowledgeBase patternsKB = RuleParser
				.parse(readFromFile(file) + template.getSignatureFactsVLOG() + template.toDatalog(option) +
				// DatalogBuiltIns.patterns(k)+
						DatalogBuiltIns.derivationRulesVLOG);

		return patternsKB;
	}

	private static void runVLOG(String in, String option) throws ParsingException, IOException {
		configureLogging(Level.INFO);
		
		System.out.println("SIGNATURE FACTS");
		System.out.println(template.getSignatureFactsVLOG());


		long t1 = 0;
		long t2 = 0;
		final KnowledgeBase kb = performDerivation(in, option);

		System.out.println("----Derivation rules----");
		System.out.println(DatalogBuiltIns.derivationRulesVLOG);
		System.out.println("---------------");
		System.out.println("----Views rules-----");
		System.out.println(template.getViews());
		System.out.println("--------------------");
		System.out.println("Reformulation rules");
		System.out.println(template.getReformulationsRules());
		System.out.println("--------------------");

	
		RuleParser.parseInto(kb, template.getViews());

		RuleParser.parseInto(kb, template.getReformulationsRules());
		RuleParser.parseInto(kb, "q(" + template.projectAwayAnsVar() + ") :- " + template.getVLOGQuery() + " .");
		RuleParser.parseInto(kb, "ans(" + template.addAnsVars() + ") :- " + template.getVLOGQuery() + " .");

		try (final Reasoner reasoner = new VLogReasoner(kb)) { //
			reasoner.setLogLevel(LogLevel.INFO); // < uncomment for reasoning details
			reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES);
			reasoner.setAlgorithm(Algorithm.SKOLEM_CHASE);
			System.out.println("Running the solver:");
			t1 = System.nanoTime();
			reasoner.reason();
			t2 = System.nanoTime() - t1;
			System.out.println("Time to materialize (ms):" + (t2 / 1000000.0));
			writeInFile(outputfile, "Time to materialize whole program (ms):" + (t2 / 1000000.0) + "\n");

			System.out.println(template.getVLOGQuery());
			PositiveLiteral query = RuleParser.parsePositiveLiteral(template.getVLOGQuery());
			reasoner.exportQueryAnswersToCsv(query, "compilation.csv", true);

			PositiveLiteral strict = RuleParser.parsePositiveLiteral("strict(?t1,?V1,?Ut2,?Ut3,?V2)");

			reasoner.exportQueryAnswersToCsv(strict, "strict.csv", true);

			PositiveLiteral neutral = RuleParser.parsePositiveLiteral("neutral(?t1,?V1,?Ut2,?Ut3,?Ut4,?Ut5,?V2)");

			reasoner.exportQueryAnswersToCsv(neutral, "neutral.csv", true);

			PositiveLiteral notMax = RuleParser.parsePositiveLiteral("notMaxG(?t1,?V1,?Ut2,?Ut3,?V2)");

			reasoner.exportQueryAnswersToCsv(notMax, "notMaxG.csv", true);

			PositiveLiteral smax = RuleParser.parsePositiveLiteral("smax(?t1,?V1,?Ut2,?Ut3,?V2)");

			reasoner.exportQueryAnswersToCsv(smax, "smax.csv", true);

			PositiveLiteral gmax = RuleParser.parsePositiveLiteral("gmax(?t1,?V1,?Ut2,?Ut3,?V2)");

			reasoner.exportQueryAnswersToCsv(gmax, "gmax.csv", true);

			PositiveLiteral ans = RuleParser.parsePositiveLiteral("ans(" + template.addAnsVars() + ")");

			writeInFile(outputfile,
					"Number of total answers in the space (#Ans):" + reasoner.countQueryAnswers(ans, false) + "\n");
			query = RuleParser.parsePositiveLiteral("q(" + template.projectAwayAnsVar() + ")");
			long nbOfUniqueQueries = reasoner.countQueryAnswers(query).getCount();

			writeInFile(outputfile, "Number of unique queries in the space (#Q):" + nbOfUniqueQueries + "\n");

			// size of data patterns

			long nbOfCisas = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("cisa(?X,?Y)")).getCount();
			long nbOfRisas = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("risa(?X,?Y)")).getCount();
			long nbOfUnAt = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("unat2(?X,?Y)")).getCount();
			long nbOfBinAt = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("binat2(?X,?Y)")).getCount();
			long nbOfDdn = reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("ddn(?X,?Y,?Z)")).getCount();
			// long nbOfRup =
			// reasoner.countQueryAnswers(RuleParser.parsePositiveLiteral("rup(?X,?Y,?Z)")).getCount();
			long totalNb = nbOfCisas + nbOfRisas + nbOfUnAt + nbOfBinAt + nbOfDdn;
			writeInFile(outputfile, "Number of data patterns (#P): " + totalNb + "\n");

			// test

			// avg time to compute answers and reformulations over the compilation
			long avgAnsTime = 0;
			long avgMaxSTime = 0;
			long avgMaxGTime = 0;
			long avgMinSTime = 0;
			long avgMinGTime = 0;
			// for each query in the space get answers, reformulations \Delta_s and \Delta_g

			QueryResultIterator iterQueries = reasoner.answerQuery(query, true);
			double deltaPsiS = 0;
			double deltaPsiG = 0;
			while (iterQueries.hasNext()) {
				QueryResult encoding = iterQueries.next();
				String terms = "";
				int i = 0;
				for (Term t : encoding.getTerms()) {
					terms += t;
					if (i < encoding.getTerms().size() - 1) {
						terms += ",";
					}
					i++;
				}
				// System.out.println("++++++++ query("+terms+","+template.addAnsVars()+")");
				t1 = System.nanoTime();
				PositiveLiteral queryQ = RuleParser
						.parsePositiveLiteral("query(" + terms + "," + template.addAnsVars() + ")");
				t2 = System.nanoTime();
				avgAnsTime += (t2 - t1);

				t1 = System.nanoTime();
				PositiveLiteral maxSpe = RuleParser
						.parsePositiveLiteral("maxSpe(" + terms + "," + template.projectAwayAnsVar() + ")");
				t2 = System.nanoTime();
				avgMaxSTime += (t2 - t1);

				t1 = System.nanoTime();
				PositiveLiteral maxGen = RuleParser
						.parsePositiveLiteral("maxGen(" + terms + "," + template.projectAwayAnsVar() + ")");
				t2 = System.nanoTime();
				avgMaxGTime += (t2 - t1);

				t1 = System.nanoTime();
				PositiveLiteral minSpe = RuleParser
						.parsePositiveLiteral("minSpe(" + terms + "," + template.projectAwayAnsVar() + ")");
				t2 = System.nanoTime();
				avgMinSTime += (t2 - t1);

				t1 = System.nanoTime();
				PositiveLiteral minGen = RuleParser
						.parsePositiveLiteral("minGen(" + terms + "," + template.projectAwayAnsVar() + ")");
				t2 = System.nanoTime();
				avgMinGTime += (t2 - t1);

				long nbOfAnsQ = reasoner.countQueryAnswers(queryQ).getCount();
				long nbOfMaxSpe = reasoner.countQueryAnswers(maxSpe).getCount();
				long nbOfMaxGen = reasoner.countQueryAnswers(maxGen).getCount();
				// long nbOfMinSpe = reasoner.countQueryAnswers(minSpe).getCount();
				// long nbOfMinGen = reasoner.countQueryAnswers(minGen).getCount();

				long nbOfMinSpe = 0;
				long nbOfMinGen = 0;

				/**
				 * TO BE REMOVED AFTER TEST
				 * 
				 */
				/*
				 * QueryResultIterator iterMaxGen = reasoner.answerQuery(maxGen, true); while
				 * (iterMaxGen.hasNext()) { QueryResult genEnc = iterMaxGen.next(); String
				 * genTerms = ""; List<Term> lastTerms =
				 * genEnc.getTerms().subList(genEnc.getTerms().size()/2,
				 * genEnc.getTerms().size()); for (Term t : lastTerms) { genTerms += t; //if
				 * (lastTerms.indexOf(t)<lastTerms.size()-1) { genTerms += ","; } }
				 * 
				 * PositiveLiteral queryGen =
				 * RuleParser.parsePositiveLiteral("query("+genTerms+template.addAnsVars()+")");
				 * long maxGenAns = reasoner.countQueryAnswers(queryGen).getCount();
				 * System.out.println(" **** MAX GEN: "+queryGen);
				 * System.out.println(" §§§§ Query Answers: "+nbOfAnsQ+" vs. maxGEN answers: "
				 * +maxGenAns); }
				 * 
				 * QueryResultIterator iterMaxSpe = reasoner.answerQuery(maxSpe, true);
				 * 
				 * while (iterMaxSpe.hasNext()) {
				 * 
				 * QueryResult speEnc = iterMaxSpe.next(); String speTerms = ""; List<Term>
				 * lastTerms = speEnc.getTerms().subList(speEnc.getTerms().size()/2,
				 * speEnc.getTerms().size()); for (Term t : lastTerms) { speTerms += t; {
				 * speTerms += ","; } }
				 * 
				 * PositiveLiteral querySpe =
				 * RuleParser.parsePositiveLiteral("query("+speTerms+template.addAnsVars()+")");
				 * long maxSpeAns = reasoner.countQueryAnswers(querySpe).getCount(); if
				 * (maxSpeAns > 0) { System.out.println(" ++++ MAX SPE: "+querySpe);
				 * System.out.println(" §§§§ Query Answers: "+nbOfAnsQ+" vs. maxSPE answers: "
				 * +maxSpeAns); } }
				 * 
				 * 
				 * 
				 *//**
					 * ---------
					 */

				// compute Delta_s
				long sumOfDelta = 0;
				QueryResultIterator iterMinSpe = reasoner.answerQuery(minSpe, true);

				while (iterMinSpe.hasNext()) {

					QueryResult speEnc = iterMinSpe.next();
					String speTerms = "";
					List<Term> lastTerms = speEnc.getTerms().subList(speEnc.getTerms().size() / 2,
							speEnc.getTerms().size());
					for (Term t : lastTerms) {
						speTerms += t;
						// if (lastTerms.indexOf(t)<lastTerms.size()-1)
						{
							speTerms += ",";
						}
					}

					// System.out.println("$$$$$$ "+"query("+speTerms+template.addAnsVars()+")");
					PositiveLiteral querySpe = RuleParser
							.parsePositiveLiteral("query(" + speTerms + template.addAnsVars() + ")");
					long minSpeAns = reasoner.countQueryAnswers(querySpe).getCount();
					if (minSpeAns > 0) {
						//System.out.println(" #### MIN SPE: " + querySpe);
						//System.out.println(" §§§§ Query Answers: " + nbOfAnsQ + " vs. minSpe answers: " + minSpeAns);
						sumOfDelta += nbOfAnsQ - minSpeAns;
						nbOfMinSpe++;
					}
				}

				double deltaS = 0;
				if (sumOfDelta > 0) {
					deltaS = nbOfMinSpe / sumOfDelta;
				}

				long avgSel = 0;
				if (nbOfMinSpe > 0) {
					avgSel = sumOfDelta / nbOfMinSpe;
				}
				//System.out.println("    Avg. selectivity: " + avgSel);
				// compute Delta_g
				sumOfDelta = 0;
				QueryResultIterator iterMinGen = reasoner.answerQuery(minGen, true);
				while (iterMinGen.hasNext()) {
					nbOfMinGen++;
					QueryResult genEnc = iterMinGen.next();
					String genTerms = "";
					List<Term> lastTerms = genEnc.getTerms().subList(genEnc.getTerms().size() / 2,
							genEnc.getTerms().size());
					for (Term t : lastTerms) {
						genTerms += t;
							{
							genTerms += ",";
						}
					}

					// System.out.println("##### "+"query("+genTerms+template.addAnsVars()+")");
					PositiveLiteral queryGen = RuleParser
							.parsePositiveLiteral("query(" + genTerms + template.addAnsVars() + ")");
					
					long minGenAns = reasoner.countQueryAnswers(queryGen).getCount();

					if (minGenAns > 0) {
						//System.out.println(" #### MIN GEN: " + queryGen);
						//System.out.println(" !!!! Query Answers: " + nbOfAnsQ + " vs. minGen answers: " + minGenAns);
						sumOfDelta += reasoner.countQueryAnswers(queryGen).getCount() - nbOfAnsQ;
						nbOfMinGen++;
					}
				}

				double deltaG = 0;
				if (sumOfDelta > 0) {
					deltaG = nbOfMinGen / sumOfDelta;
				}

				long avgInc = 0;
				if (nbOfMinGen > 0) {
					avgInc = sumOfDelta / nbOfMinGen;
				}
				//System.out.println("    Avg. inclusivity: " + avgInc);
				deltaPsiS += avgSel;
				deltaPsiG += avgInc;
				// Math.max(avgInc, avgSel);
				writeInFile(outputfile,
						"-- Query: " + encoding.toString() + ": #ans=" + nbOfAnsQ + " #maxSpe= " + nbOfMaxSpe
								+ " #maxGen=" + nbOfMaxGen + " #minSpe=" + nbOfMinSpe + " #minGen=" + nbOfMinGen
								+ " avg. sel=" + avgSel + " avg. incl=" + avgInc + " Delta_s=" + deltaS + " Delta_g="
								+ deltaG + "\n");

			}
			// write Delta_Psi
			writeInFile(outputfile, "-- Avg. inclusivity (Delta_Psi_G): " + deltaPsiG / nbOfUniqueQueries + "\n");
			writeInFile(outputfile, "-- Avg. selectivity (Delta_Psi_S): " + deltaPsiS / nbOfUniqueQueries + "\n");
			avgAnsTime = avgAnsTime / nbOfUniqueQueries;
			avgMaxGTime = avgMaxGTime / nbOfUniqueQueries;
			avgMaxSTime = avgMaxSTime / nbOfUniqueQueries;
			avgMinGTime = avgMinGTime / nbOfUniqueQueries;
			avgMinSTime = avgMinSTime / nbOfUniqueQueries;

			writeInFile(outputfile,
					"Avg. computing times (ms): ansQ=" + (avgAnsTime / 1000000.0) + " maxS=" + (avgMaxSTime / 1000000.0)
							+ " maxG=" + (avgMaxGTime / 1000000.0) + " minS=" + (avgMinSTime / 1000000.0) + " minG= "
							+ (avgMinGTime / 1000000.0) + " \n");

			

			t1 = System.nanoTime();
			reasoner.exportQueryAnswersToCsv(RuleParser.parsePositiveLiteral("minGen" + template.getReformArity()),
					"minGen.csv", true);
			t2 = System.nanoTime() - t1;

			writeInFile(outputfile, "Time to export all minGen (ms): " + (t2 / 1000000.0) + "\n");
			t1 = System.nanoTime();
			reasoner.exportQueryAnswersToCsv(RuleParser.parsePositiveLiteral("minSpe" + template.getReformArity()),
					"minSpe.csv", true);
			t2 = System.nanoTime() - t1;

			writeInFile(outputfile, "Time to export all minSpe (ms): " + (t2 / 1000000.0) + "\n");

			t1 = System.nanoTime();
			reasoner.exportQueryAnswersToCsv(RuleParser.parsePositiveLiteral("maxGen" + template.getReformArity()),
					"maxGen.csv", true);
			t2 = System.nanoTime() - t1;
			writeInFile(outputfile, "Time to export all maxGen (ms): " + (t2 / 1000000.0) + "\n");

			t1 = System.nanoTime();
			reasoner.exportQueryAnswersToCsv(RuleParser.parsePositiveLiteral("maxSpe" + template.getReformArity()),
					"maxSpe.csv", true);
			t2 = System.nanoTime() - t1;
			writeInFile(outputfile, "Time to export all maxSpe (ms): " + (t2 / 1000000.0) + "\n");
			reasoner.close();
		}

	}

	private static void writeInFile(String path, String program) {
		try {
			Files.write(Paths.get(path), program.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static QueryTemplate readTemplate(String fileQTemplate) throws ParseError {

		String fileContent = readFromFile(fileQTemplate);

		return QueryTemplate.parseExpression(fileContent);
	}

	private static String readFromFile(String fileQTemplate) {
		String contents = "";
		try {
			contents = new String(Files.readAllBytes(Paths.get(fileQTemplate)));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contents;
	}

	public static void main(String[] args) {
		String option = args[0];

		String fileQTemplate = args[1];

		if (option.equals("-extract")) {
			try {
				final KnowledgeBase patternsKB = RuleParser.parse(readFromFile(fileQTemplate));
				try (final Reasoner reasoner = new VLogReasoner(patternsKB)) { //
					reasoner.setLogLevel(LogLevel.INFO); // < uncomment for reasoning details
					// reasoner.setRuleRewriteStrategy(RuleRewriteStrategy.SPLIT_HEAD_PIECES);
					reasoner.setAlgorithm(Algorithm.RESTRICTED_CHASE);
					System.out.println("Running the solver:");
					long t1 = System.nanoTime();
					reasoner.reason();
					long t2 = System.nanoTime() - t1;
					System.out.println("Time to extract data (ms):" + (t2 / 1000000.0));

					PositiveLiteral unatT = RuleParser.parsePositiveLiteral("unatT(?U,?X)");
					PositiveLiteral binatT = RuleParser.parsePositiveLiteral("binatT(?U,?X,?Y)");

					reasoner.exportQueryAnswersToCsv(unatT, "dbpedia/complExtension/unatExt.csv", true);
					reasoner.exportQueryAnswersToCsv(binatT, "dbpedia/complExtension/binatExt.csv", true);
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (ParsingException e) {
				e.printStackTrace();
			}
			return;

		}

		try {
			template = readTemplate(fileQTemplate);
		} catch (ParseError e) {
			e.printStackTrace();
		}
		System.out.println(template.toString());

		
		outputfile = "results/" + fileQTemplate + ".out";

		writeInFile(outputfile, template.toString() + "\n");
		writeInFile(outputfile, "Size of template (#Psi):" + template.getTemplateList().size() + "\n");

		
		String extractFile = args[2];
		
		if (option.equals(DatalogBuiltIns.OPTIM))
		{

			try {
				runVLOG(extractFile, option);
			} catch (ParsingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				runVLOG(extractFile, DatalogBuiltIns.NO_OPTIM);
			} catch (ParsingException | IOException e) {
				e.printStackTrace();
			}
		}
		

	}

}
