package fdr_synthesizer;

import java.io.File;

import uk.ac.ox.cs.fdr.*;

public class FDR {

	static String outputFDR = "";

	public static void main(String[] argv)
	{
		String fileName = "projeto_synthesizer.csp";
		 
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		 
		File file = new File(classLoader.getResource(fileName).getPath());

		runFDR(file.getPath());
	}

	public static String runFDR(String pathFile) {

		outputFDR = "";
				
		try {
			Session session = new Session();
			session.loadFile(pathFile);
			for (Assertion assertion : session.assertions()) {
				assertion.execute(null);
				System.out.println(assertion.toString()+" "+
						(assertion.passed() ? "Passed" : "Failed"));

				outputFDR +=
						(assertion.passed() ? "Passed" : "Failed")
						+", found "+(assertion.counterexamples().size()
						+" counterexamples \n");

				// Pretty print the counterexamples
				for (Counterexample counterexample : assertion.counterexamples()) {
					describeCounterexample(session, counterexample);
				}

			}
		}
		catch (InputFileError error) {
			System.out.println(error);
		}
		catch (FileLoadError error) {
			System.out.println(error);
		}

		fdr.libraryExit();
		System.out.println(outputFDR);
		
		return outputFDR;

	}


	/**
	 * Pretty prints the specified counterexample to out.
	 */
	private static void describeCounterexample(Session session,
			Counterexample counterexample){
		// Firstly, just print a simple description of the counterexample
		//
		// This uses dynamic casting to check the assertion type.
		outputFDR += ("Counterexample type: ");
		if (counterexample instanceof DeadlockCounterexample)
			outputFDR += ("deadlock\n");
		else if (counterexample instanceof DeterminismCounterexample)
			outputFDR += ("determinism\n");
		else if (counterexample instanceof DivergenceCounterexample)
			outputFDR += ("divergence\n");
		else if (counterexample instanceof MinAcceptanceCounterexample)
		{
			MinAcceptanceCounterexample minAcceptance =
					(MinAcceptanceCounterexample) counterexample;
			outputFDR += ("minimal acceptance refusing {");
			for (Long event : minAcceptance.minAcceptance())
				outputFDR += (session.uncompileEvent(event).toString() + ", ");
			outputFDR += ("}\n");
		}
		else if (counterexample instanceof TraceCounterexample)
		{
			TraceCounterexample trace = (TraceCounterexample) counterexample;
			outputFDR += ("trace with event "+ session.uncompileEvent(
					trace.errorEvent()).toString() + "\n");
		}
		else
			outputFDR += ("unknown \n");

		outputFDR += ("Children: \n");

		// In order to print the children we use a DebugContext. This allows for
		// division of behaviours into their component behaviours, and also ensures
		// proper alignment amongst the child components.
		DebugContext debugContext = null;

		if (counterexample instanceof RefinementCounterexample)
			debugContext = new DebugContext((RefinementCounterexample) counterexample, false);
		else if (counterexample instanceof PropertyCounterexample)
			debugContext = new DebugContext((PropertyCounterexample) counterexample, false);

		debugContext.initialise(null);
		for (Behaviour root : debugContext.rootBehaviours())
			describeBehaviour(session, debugContext, root, 2, true);
	}

	/**
	 * Prints a vaguely human readable description of the given behaviour to out.
	 */
	private static void describeBehaviour(Session session,
			DebugContext debugContext, Behaviour behaviour, int indent,
			boolean recurse)
	{
		// Describe the behaviour type
		printIndent(indent); outputFDR += ("behaviour type: ");
		indent += 2;
		if (behaviour instanceof ExplicitDivergenceBehaviour)
			outputFDR += ("explicit divergence after trace \n");
		else if (behaviour instanceof IrrelevantBehaviour)
			outputFDR += ("irrelevant \n");
		else if (behaviour instanceof LoopBehaviour)
		{
			LoopBehaviour loop = (LoopBehaviour) behaviour;
			outputFDR += ("loops after index " + loop.loopIndex()+ "\n");
		}
		else if (behaviour instanceof MinAcceptanceBehaviour)
		{
			MinAcceptanceBehaviour minAcceptance = (MinAcceptanceBehaviour) behaviour;
			outputFDR += ("minimal acceptance refusing {");
			for (Long event : minAcceptance.minAcceptance())
				outputFDR += (session.uncompileEvent(event).toString() + ", ");
			outputFDR += ("} \n");
		}
		else if (behaviour instanceof SegmentedBehaviour)
		{
			SegmentedBehaviour segmented = (SegmentedBehaviour) behaviour;
			outputFDR += ("Segmented behaviour consisting of: \n");
			// Describe the sections of this behaviour. Note that it is very
			// important that false is passed to the the descibe methods below
			// because segments themselves cannot be divded via the DebugContext.
			// That is, asking for behaviourChildren for a behaviour of a
			// SegmentedBehaviour is not allowed.
			for (TraceBehaviour child : segmented.priorSections())
				describeBehaviour(session, debugContext, child, indent + 2, false);
			describeBehaviour(session, debugContext, segmented.last(),
					indent + 2, false);
		}
		else if (behaviour instanceof TraceBehaviour)
		{
			TraceBehaviour trace = (TraceBehaviour) behaviour;
			outputFDR += ("performs event " +
					session.uncompileEvent(trace.errorEvent()).toString()+" \n");
		}

		// Describe the trace of the behaviour
		printIndent(indent); outputFDR += ("Trace: ");
		for (Long event : behaviour.trace())
		{
			// INVALIDEVENT indiciates that this machine did not perform an event at
			// the specified index (i.e. it was not synchronised with the machines
			// that actually did perform the event).
			if (event == fdr.INVALIDEVENT)
				outputFDR += ("-, ");
			else
				outputFDR += (session.uncompileEvent(event).toString() + ", ");
		}
		outputFDR += "\n";

		// Describe any named states of the behaviour
		printIndent(indent); outputFDR += ("States: ");
		for (Node node : behaviour.nodePath())
		{
			if (node == null)
				outputFDR +=("-, ");
			else
			{
				ProcessName processName = session.machineNodeName(behaviour.machine(), node);
				if (processName == null)
					outputFDR += ("(unknown), ");
				else
					outputFDR += (processName.toString()+", ");
			}
		}
		outputFDR += "\n";

		// Describe our own children recursively
		if (recurse) {
			for (Behaviour child : debugContext.behaviourChildren(behaviour))
				describeBehaviour(session, debugContext, child, indent + 2, true);
		}
	}

	/**
	 * Prints a number of spaces to out.
	 */
	private static void printIndent(int indent) {
		for (int i = 0; i < indent; ++i)
			outputFDR += (' ');
	}


}
