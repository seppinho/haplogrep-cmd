package genepi;


import java.util.Arrays;

import genepi.commands.DistanceCheckCommand;
import genepi.commands.HaplogrepCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "haplogrep")
public class App implements Runnable {

	public static final String URL = "https://github.com/seppinho/haplogrep-cmd";

	public static final String APP = "mtDNA Haplogroup Classifiction";

	public static final String VERSION = "v2.4.0-rc2";

	public static final String COPYRIGHT = "(c) Sebastian Schönherr, Hansi Weissensteiner, Lukas Forer, Dominic Pacher";
	
	public static final String CONTACT = "sebastian.schoenherr@i-med.ac.at";

	// public static String[] ARGS = new String[0];

	public static String COMMAND;
	
	static CommandLine commandLine; 
	
	@Option(names = { "--version" }, versionHelp = true)
	boolean showVersion;

	public static void main(String[] args) {

		System.out.println();
		System.out.println(APP + " " + VERSION);
		if (URL != null && !URL.isEmpty()) {
			System.out.println(URL);
		}
		if (COPYRIGHT != null && !COPYRIGHT.isEmpty()) {
			System.out.println(COPYRIGHT);
		}
		
		if (CONTACT != null && !CONTACT.isEmpty()) {
			System.out.println(CONTACT);
			System.out.println();
		}

		COMMAND = Arrays.toString(args);
		
		System.out.println(COMMAND);

		commandLine = new CommandLine(new App());
		commandLine.addSubcommand("classify", new HaplogrepCommand());
		commandLine.addSubcommand("distance", new DistanceCheckCommand());

		commandLine.setExecutionStrategy(new CommandLine.RunLast());
		commandLine.execute(args);
		
	}

	@Override
	public void run() {
		System.out.println("haplogrep");
		commandLine.usage(System.out);

	}

}
