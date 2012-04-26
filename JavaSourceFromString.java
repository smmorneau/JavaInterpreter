import java.net.URI;

/* 
 * Resource: http://www.java2s.com/Code/Java/JDK-6/CompilingfromMemory.htm
 */

import javax.tools.SimpleJavaFileObject;

	public class JavaSourceFromString extends SimpleJavaFileObject {
		final String code;

		JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}