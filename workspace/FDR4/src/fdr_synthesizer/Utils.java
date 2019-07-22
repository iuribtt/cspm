package fdr_synthesizer;

import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {
	
	/**
	 * 
	 * @param trace
	 * @param inputUser
	 * @param expectedOutput
	 * @return
	 */
	public static String traceToCode(String trace, Map<String, Integer> inputUser, Map<String, Integer> expectedOutput ) {
		String code = "";

		System.out.println(trace);
		String variables = "";
		for(Map.Entry<String, Integer> map : inputUser.entrySet()){
			String key = map.getKey();
			Integer value = map.getValue();
			variables = variables + "&nbsp;<font color='#950054'><b>int</b></font> <font color='#2b00ff'>" + key + "</font> = " + value +";<br/>";
		}

		String expetedOutput = "  // ";
		for(Map.Entry<String, Integer> map : expectedOutput.entrySet()){
			String key = map.getKey();
			Integer value = map.getValue();
			expetedOutput = expetedOutput + key + " = " + value +" ";
		}

		if(trace.contains("PROGRAM_NOT_FOUND")) {
			code = "It wasn't found a program that satisfay the input and output.<br/>"
					+ "Try to change the Deep level, or the variables.<br/>"
					+ "Input: " + variables+ "<br/>"
					+ "Output: " + expetedOutput;

		}else {

			code = trace.split("memory_variables_values")[0];
			if (code.trim().isEmpty()){
				
				code = "<h3 align='center'>We don't need to do anything <br /> to achieve the result.</h3>"
						+ "<b><h2 align='center'>XD</h2></b>";
			}else {
				
				code = code.replaceAll("operation\\.(.*?)\\.(.*?)\\.(.*?)", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='#2b00ff'>$2</font> =");
				code = code.replaceAll("add\\.(.*?)\\.([-,+]?\\d+)\\.", " + <font color='#2b00ff'>$1</font>");
				code = code.replaceAll("sub\\.(.*?)\\.([-,+]?\\d+)\\.", " - <font color='#2b00ff'>$1</font>");
				code = code.replaceAll("mult\\.(.*?)\\.([-,+]?\\d+)\\.", " * <font color='#2b00ff'>$1</font>");
				code = code.replaceAll("div\\.(.*?)\\.([-,+]?\\d+)\\.", " \\ <font color='#2b00ff'>$1</font>");

				code = code.replaceAll("semicolon, ", ";<br/>");
				
			}
			
			code = "<font color='#950054'><b>public class</b></font> Main {<br/><br/>"
					+ "" + variables + "<br/>"
					+ "&nbsp;<font color='#950054'><b>public static void </b></font> main(String[] <font color='#6a3e3e'>args</font>) {<br/><br/>"
					+ ""+ code + "<br/>"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='#3f7f5f'>"+ expetedOutput + "</font><br/>"
					+ "&nbsp;}<br/>"
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
	//		System.out.println(traceToCode(trace, input, output));
	//	}

}
