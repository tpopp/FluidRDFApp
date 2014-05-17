/**
 *  This file is part of FluidRDFApp.

    FluidRDFApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FluidRDFApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FluidRDFApp.  If not, see <http://www.gnu.org/licenses/>.
 */
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
