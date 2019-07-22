package fdr_synthesizer;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.nio.file.Path;

import uk.ac.ox.cs.fdr.*;

public class FDRTest {

	static PrintStream out = System.out;

	public static void main(String[] argv)
	{

		String fileName = "projeto_synthesizer_test.csp";
		 
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		 
		File file = new File(classLoader.getResource(fileName).getPath());

		runFDR(file.getPath());
		
	}

	public static String runFDR(String pathFile) {

		try {
			Session session = new Session();
			session.loadFile(pathFile);
			for (Assertion assertion : session.assertions()) {
				assertion.execute(null);
				System.out.println(assertion.toString()+" "+
						(assertion.passed() ? "Passed" : "Failed"));

				out.println(
						(assertion.passed() ? "Passed" : "Failed")
						+", found "+(assertion.counterexamples().size())
						+" counterexamples");

				// Pretty print the counterexamples
				for (Counterexample counterexample : assertion.counterexamples()) {
					describeCounterexample(out, session, counterexample);
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
		return null;

	}


	/**
	 * Pretty prints the specified counterexample to out.
	 */
	private static void describeCounterexample(PrintStream out, Session session,
			Counterexample counterexample){
		// Firstly, just print a simple description of the counterexample
		//
		// This uses dynamic casting to check the assertion type.
		out.print("Counterexample type: ");
		if (counterexample instanceof DeadlockCounterexample)
			out.println("deadlock");
		else if (counterexample instanceof DeterminismCounterexample)
			out.println("determinism");
		else if (counterexample instanceof DivergenceCounterexample)
			out.println("divergence");
		else if (counterexample instanceof MinAcceptanceCounterexample)
		{
			MinAcceptanceCounterexample minAcceptance =
					(MinAcceptanceCounterexample) counterexample;
			out.print("minimal acceptance refusing {");
			for (Long event : minAcceptance.minAcceptance())
				out.print(session.uncompileEvent(event).toString() + ", ");
			out.println("}");
		}
		else if (counterexample instanceof TraceCounterexample)
		{
			TraceCounterexample trace = (TraceCounterexample) counterexample;
			out.println("trace with event "+ session.uncompileEvent(
					trace.errorEvent()).toString());
		}
		else
			out.println("unknown");

		out.println("Children:");

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
			describeBehaviour(out, session, debugContext, root, 2, true);
	}

	/**
	 * Prints a vaguely human readable description of the given behaviour to out.
	 */
	private static void describeBehaviour(PrintStream out, Session session,
			DebugContext debugContext, Behaviour behaviour, int indent,
			boolean recurse)
	{
		// Describe the behaviour type
		printIndent(out, indent); out.print("behaviour type: ");
		indent += 2;
		if (behaviour instanceof ExplicitDivergenceBehaviour)
			out.println("explicit divergence after trace");
		else if (behaviour instanceof IrrelevantBehaviour)
			out.println("irrelevant");
		else if (behaviour instanceof LoopBehaviour)
		{
			LoopBehaviour loop = (LoopBehaviour) behaviour;
			out.println("loops after index " + loop.loopIndex());
		}
		else if (behaviour instanceof MinAcceptanceBehaviour)
		{
			MinAcceptanceBehaviour minAcceptance = (MinAcceptanceBehaviour) behaviour;
			out.print("minimal acceptance refusing {");
			for (Long event : minAcceptance.minAcceptance())
				out.print(session.uncompileEvent(event).toString() + ", ");
			out.println("}");
		}
		else if (behaviour instanceof SegmentedBehaviour)
		{
			SegmentedBehaviour segmented = (SegmentedBehaviour) behaviour;
			out.println("Segmented behaviour consisting of:");
			// Describe the sections of this behaviour. Note that it is very
			// important that false is passed to the the descibe methods below
			// because segments themselves cannot be divded via the DebugContext.
			// That is, asking for behaviourChildren for a behaviour of a
			// SegmentedBehaviour is not allowed.
			for (TraceBehaviour child : segmented.priorSections())
				describeBehaviour(out, session, debugContext, child, indent + 2, false);
			describeBehaviour(out, session, debugContext, segmented.last(),
					indent + 2, false);
		}
		else if (behaviour instanceof TraceBehaviour)
		{
			TraceBehaviour trace = (TraceBehaviour) behaviour;
			out.println("performs event " +
					session.uncompileEvent(trace.errorEvent()).toString());
		}

		// Describe the trace of the behaviour
		printIndent(out, indent); out.print("Trace: ");
		for (Long event : behaviour.trace())
		{
			// INVALIDEVENT indiciates that this machine did not perform an event at
			// the specified index (i.e. it was not synchronised with the machines
			// that actually did perform the event).
			if (event == fdr.INVALIDEVENT)
				out.print("-, ");
			else
				out.print(session.uncompileEvent(event).toString() + ", ");
		}
		out.println();

		// Describe any named states of the behaviour
		printIndent(out, indent); out.print("States: ");
		for (Node node : behaviour.nodePath())
		{
			if (node == null)
				out.print("-, ");
			else
			{
				ProcessName processName = session.machineNodeName(behaviour.machine(), node);
				if (processName == null)
					out.print("(unknown), ");
				else
					out.print(processName.toString()+", ");
			}
		}
		out.println();

		// Describe our own children recursively
		if (recurse) {
			for (Behaviour child : debugContext.behaviourChildren(behaviour))
				describeBehaviour(out, session, debugContext, child, indent + 2, true);
		}
	}

	/**
	 * Prints a number of spaces to out.
	 */
	private static void printIndent(PrintStream out, int indent) {
		for (int i = 0; i < indent; ++i)
			out.print(' ');
	}


}
