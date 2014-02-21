package calculations;

/**
 * Square Well potential related parameters
 * @author Tres
 *
 */
public class tanglu_constants {
	
	/**
	 * number density of the fluid
	 */
	protected double rho;
	
	/**
	 * packing fraction
	 */
	protected double eta;
	
	/**
	 * f = 3 + 3n - n^2
	 */
	protected double f;
	
	/**
	 * yplus = [1 + (1 + 2n^4/f^2)^(1/2)]^(1/3) 
	 */
	protected double yplus;
	
	/**
	 * yminus = [1 - (1 + 2n^4/f^2)^(1/2)]^(1/3)
	 */
	protected double yminus;
	
	/**
	 * c = e^(2pi * i / 3)
	 */
	protected Complex c;
	
	/**
	 * solutions to TlReadParams.tsubi
	 */
	protected Complex[] t = new Complex[3];
	
	/**
	 * solutions to function a1
	 */
	protected Complex[] a1 = new Complex[3];
	
	/**
	 * solutions to function a2
	 */
	protected Complex[] a2 = new Complex[3];
	
	/**
	 * solutions to function a3
	 */
	protected Complex[] a3 = new Complex[3];

}
