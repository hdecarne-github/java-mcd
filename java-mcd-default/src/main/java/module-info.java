/**
 * module-info
 */
module de.carne.mcd {

	requires org.eclipse.jdt.annotation;
	requires de.carne;

	exports de.carne.mcd;

	exports de.carne.mcd.bootstrap to de.carne.mcd.jvm, de.carne.mcd.x86;
	exports de.carne.mcd.instruction to de.carne.mcd.jvm, de.carne.mcd.x86;
	exports de.carne.mcd.io to de.carne.mcd.jvm, de.carne.mcd.x86;

}