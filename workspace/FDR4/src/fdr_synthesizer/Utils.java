package fdr_synthesizer;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
	
	/**
	 * Convert the track to Java format code
	 * @param trace Frd4 log output
	 * @param inputUser user input variable and values
	 * @param expectedOutput variable and values expected
	 * @return return Java code formating with html tags
	 */
	public static String traceToCode(String trace, int deepLevel, Map<String, Integer> inputUser, Map<String, Integer> expectedOutput ) {
		String code = "";
		
		System.out.println(trace);
		String variables = "";
		for(Map.Entry<String, Integer> map : inputUser.entrySet()){
			String key = map.getKey();
			Integer value = map.getValue();
			variables = variables + "&#160;<font color='#950054'><b>static int</b></font> <font color='#2b00ff'>" + key + "</font> = " + value +";<br/>";
		}

		
		String expetedOutput = "  /* ";
		String expetedOutputNotFound = "";
		for(Map.Entry<String, Integer> map : expectedOutput.entrySet()){
			String key = map.getKey();
			Integer value = map.getValue();
			expetedOutput = expetedOutput + "<b>" + key + "</b> = " + value +", ";
			expetedOutputNotFound = expetedOutputNotFound + "&#160;<font color='#950054'><b>int</b></font> <font color='#2b00ff'>" + key + "</font> = " + value +";<br/>";
			
		}
	
		expetedOutput = expetedOutput + " */";

		if(trace.contains("PROGRAM_NOT_FOUND")) {
			code = "<h2 align='center'>Program not found</h2>"
					+ "<font align='center'>It <b>wasn't found a program</b> that satisfy the <b>input</b> and <b>output</b>.<br/>"
					+ "Try to change the <b>Deep</b> level, or the variables.</font><br/>"
					+ "<b>Deep:</b> <font color='#950054'><b>" + deepLevel + "</b></font><br/><br/>"
					+ "<b>Input:</b> <br/>" + variables+ "<br/>"
					+ "<b>Output:</b> <br/>" + expetedOutputNotFound;

		}else {

			code = trace.split("memory_variables_values")[0];
			if (code.trim().isEmpty()){
				
				code = "<h3 align='center'>We don't need to do anything <br /> to achieve the result.</h3>"
						+ "<b><h2 align='center'>XD</h2></b>";
			}else {
				
				code = code.replaceAll("operation\\.(.*?)\\.(.*?)\\.", "&#160;&#160;&#160;&#160;&#160;&#160;<font color='#2b00ff'>$2</font> =");
				code = code.replaceAll("add\\.(.*?)\\.([-,+,])?(\\d+)\\.", " + <font color='#2b00ff'>$1</font>");//($2$3)
				code = code.replaceAll("sub\\.(.*?)\\.([-,+,])?(\\d+)\\.", " - <font color='#2b00ff'>$1</font>");//($2$3)
				code = code.replaceAll("mult\\.(.*?)\\.([-,+])?(\\d+)	\\.", " * <font color='#2b00ff'>$1</font>");//($2$3)
				code = code.replaceAll("div\\.(.*?)\\.([-,+])?(\\d+)\\.", " \\ <font color='#2b00ff'>$1</font>");//($2$3)

				code = code.replaceAll("semicolon, ", ";<br/>");
				
			}
			
			code = "<font color='#950054'><b>public class</b></font> Main {<br/><br/>"
					+ "" + variables + "<br/>"
					+ "&#160;<font color='#950054'><b>public static void </b></font> main(String[] <font color='#6a3e3e'>args</font>) {<br/><br/>"
					+ ""+ code + "<br/>"
					+ "&#160;&#160;&#160;&#160;&#160;&#160;<font color='#3f7f5f'>"+ expetedOutput + "</font><br/>"
					+ "&#160;}<br/>"
					+ "}";
		}

		return code;

	}

	//	int var = 1;
	//	public static void main(String[] args) {
	//		Map input = new LinkedHashMap<String, Integer>();
	//		input.put("V1", 1);
	//		input.put("V2", 1);
	//
	//		Map output = new LinkedHashMap<String, Integer>();
	//		output.put("V5", -1);
	//		output.put("V6", 1);
	//
	//		String trace = "operation.equal.var3.add.var1.-15.semicolon, operation.equal.var3.add.var1.15.add.var3.15.semicolon, operation.equal.var3.add.var1.15.add.var2.2.add.var3.30.semicolon, memory_variables_values.memory.var1.15.memory.var2.2.memory.var3.47.end_memoria, FIND_PROGRAM,";
	//
	//		System.out.println(traceToCode(trace, 2, input, output));
	//	}

}
