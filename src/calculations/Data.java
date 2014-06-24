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

import java.io.Serializable;
import java.util.HashMap;

public class Data implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2940432500663393279L;
	public String name;
	public double dr;
	public double[] epsilon = {};
	public double[] lambda = {};
	public double[] r = {};
	public HashMap<String, String> printed = new HashMap<String, String>();
	public double[] gofr = {};
	public double[] rough_gofr = {};
	public double[] disc_v = {};
	public double[] cont_v = {};
	public double[] given_r = {};
	public double[] given_v = {};
	public double packingFraction;
	public int nr = 1000;
	public double max_deps = 0.50;
	public boolean[] show = {false, false};
	public double configEnergy;
	public double internEnergy;
	public double isothermCompress;
	public double twoBodyEntropy;
	public double compress;
	public double density;
	public boolean thermo = true;
	public double temp;
	public float[][] g0;
	public float[][][] g1;
}
