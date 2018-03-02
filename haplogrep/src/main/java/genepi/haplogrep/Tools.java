package genepi.haplogrep;

import java.lang.reflect.InvocationTargetException;

import genepi.base.Toolbox;

public class Tools extends Toolbox {

	public Tools(String command, String[] args) {
		super(command, args);
	}

	public static void main(String[] args) {

		Tools tools = new Tools("java -jar toolbox-1.0.jar", args);
		
		tools.addTool("haplogrep", HaplogrepCMD.class);

		try {
			tools.start();
		} catch (InstantiationException | IllegalAccessException | SecurityException | NoSuchMethodException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}
