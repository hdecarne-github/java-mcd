/**
 * module-info
 */
module de.carne.mcd.jvmdecoder {
	requires transitive org.eclipse.jdt.annotation;
	requires transitive de.carne.mcd;

	requires de.carne;

	exports de.carne.mcd.jvmdecoder;
}