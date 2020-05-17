/**
 * module-info
 */
module de.carne.mcd.x86decoder {
	requires transitive org.eclipse.jdt.annotation;
	requires transitive de.carne.mcd;

	requires java.xml;
	requires de.carne;

	exports de.carne.mcd.x86decoder;
}