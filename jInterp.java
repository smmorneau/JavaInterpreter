import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class jInterp {

	public static String imports = "import java.io.*;\n"
			+ "import java.util.*;\n\n";

	public static URLClassLoader classLoader;
	
	/* Create a diagnostic controller, which holds the compilation problems */
	public static DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

	public static void main(String[] args) throws SecurityException,
			NoSuchMethodException, ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, MalformedURLException {

		loadClass();

		int counter = 0;

		// input from user
		Scanner scan = new Scanner(System.in);
		System.out.print("> ");

		do {
			String input = scan.nextLine();

			String fileName = "Bogus";

			// see if input is declaration

			String content;

			if (counter > 0) {
				content = imports + "public class " + fileName
						+ " extends Interp_" + (counter - 1)
						+ " {\n\tpublic static " + input
						+ " \n\tpublic static void exec() {}" + "\n}";
			} else {
				content = imports + "public class " + fileName
						+ " {\n\tpublic static " + input
						+ " \n\tpublic static void exec() {}" + "\n}";
			}

			// compile test file
			boolean noError = compile(fileName, content);

			fileName = "Interp_" + counter;

			if (noError) {
				// input was a declaration
				
				if (counter > 0) {
					content = imports + "public class Interp_" + counter
							+ " extends Interp_" + (counter - 1)
							+ " {\n\tpublic static " + input
							+ " \n\tpublic static void exec() {}" + "\n}";
				} else {
					content = imports + "public class Interp_" + counter
							+ " {\n\tpublic static " + input
							+ " \n\tpublic static void exec() {}" + "\n}";
				}
			} else {
				// if error occurred, see if input is statement
				
				if (counter > 0) {
					content = imports + "public class Interp_" + counter
							+ " extends Interp_" + (counter - 1)
							+ " {\n\tpublic static void exec() {\n\t\t" + input
							+ "\n\t}" + "\n}";
				} else {
					content = imports + "public class Interp_" + counter
							+ " {\n\tpublic static void exec() {\n\t\t" + input
							+ "\n\t}" + "\n}";
				}
				
			}

//			System.out.println(content);
			noError = compile(fileName, content);

			if (!noError) {
				// input is not valid Java
				System.out.println("Invalid input:");
				
				List<Diagnostic<? extends JavaFileObject>> diagList = diagnostics.getDiagnostics();
				for(Diagnostic<? extends JavaFileObject> diag : diagList) {
					System.out.println(diag);
				}
				
				counter--;

			} else {
				// run the statement
				
				Method exec = Class.forName(fileName, true, classLoader)
						.getDeclaredMethod("exec", (Class[]) null);

				exec.invoke(null, (Object[]) null);
				
			}
			
			counter++;
			System.out.print("> ");

		} while (scan.hasNext());

	}

	/*
	 * Resources:
	 * http://www.java2s.com/Code/Java/JDK-6/CompilingfromMemory.htm
	 * http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
	 */
	public static boolean compile(String fileName, String content) {
		/* Instantiating the java compiler */
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		JavaFileObject file = new JavaSourceFromString(fileName, content);

		/* Prepare any compilation options to be used during compilation */
		// Place the output files under tmp folder.
		String[] compileOptions = new String[] { "-d", "tmp", "-cp", "tmp" };
		Iterable<String> compilationOptionss = Arrays.asList(compileOptions);

		Iterable<? extends JavaFileObject> compilationUnits = Arrays
				.asList(file);
		CompilationTask task = compiler.getTask(null, null, diagnostics,
				compilationOptionss, null, compilationUnits);

		return task.call();
	}

	public static void loadClass() throws MalformedURLException {
		File file = new File("tmp");
		file.mkdir();

		URL url = file.toURI().toURL();
		URL[] urls = { url };
		classLoader = new URLClassLoader(urls);

	}
	
}


