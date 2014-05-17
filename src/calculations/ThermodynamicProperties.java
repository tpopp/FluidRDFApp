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
 * @author Tres
 * 
 */
public class ThermodynamicProperties {

	public static double configurationalEnergy(double[] u, double[] g,
			double[] r, double dr) {
		if (r.length < 2 || u.length < 2 || g.length < 2)
			return 0.0;
		double sum = 0;
		int n = 0;
		while (r[n] < 1.0)
			n++;
		double temp = r[n] * r[n] * u[n] * g[n];
		n++;
		while (r[n] < 2.0) {
			sum += r[n] * r[n] * u[n] * g[n];
			n++;
		}
		n--;
		sum -= r[n] * r[n] * u[n] * g[n];
		temp += r[n] * r[n] * u[n] * g[n];
		return (sum * 2 + temp) * dr * Math.PI;
	}

	public static double internalEnergy(double energy) {
		return 1.5 + energy;
	}

	// public static double isothermalCompressibility(double dens, double[] g,
	// double[] r, double dr) {
	// if (r.length < 2 || g.length < 2)
	// return 0.0;
	// double sum = 0.0;
	// for (int i = 1; i < r.length - 1; i++) {
	// sum += (g[i] - 1.0) * r[i] * r[i];
	// }
	// sum *= 2;
	// sum += (g[0] - 1.0) * r[0] * r[0] + (g[r.length - 1] - 1.0)
	// * r[r.length - 1] * r[r.length - 1];
	// sum *= 2 * Math.PI * dr * dens;
	// return sum + 1;
	// }

	public static double twoBodyExcessEntropy(double dens, double[] g,
			double[] r, double dr) {
		if (r.length < 2 || g.length < 2)
			return 0.0;
		double sum = 0;
		for (int i = 1; i < r.length - 1; i++) {
			if (g[i] < 0.0000001)
				sum += r[i] * r[i];
			else
				sum += r[i] * r[i] * (g[i] * Math.log(g[i]) - g[i] + 1);
		}
		sum *= 2;
		if (g[0] < 0.0000001)
			sum += r[0] * r[0];
		else
			sum += r[0] * r[0] * (g[0] * Math.log(g[0]) - g[0] + 1);
		if (g[r.length - 1] < 0.0000001)
			sum += r[r.length - 1] * r[r.length - 1];
		else
			sum += r[r.length - 1]
					* r[r.length - 1]
					* (g[r.length - 1] * Math.log(g[r.length - 1])
							- g[r.length - 1] + 1);
		return -sum * dens * Math.PI * dr;
	}

	public static double compressibility(double dens, double[] lambda,
			double[] saw_g, double[] r, double dr, double[] epsilon) {
		double sum = 0.0;
		int i = 0;
		while (r[i] - 1 < 0.0000001)
			i++;
		sum += saw_g[i];
		for (int n = 0; n < lambda.length; n++) {
			while (r[i] - lambda[n] < 0.0000001)
				i++;
			if (n == lambda.length - 1 && epsilon[n] < 0.0000001)
				continue;
			if (Math.abs(epsilon[n] - epsilon[n + 1]) < 0.000001)
				continue;
			sum += Math.pow(lambda[n], 3) * (saw_g[i] - saw_g[i - 1]);
		}
		return sum * 2.0 * dens * Math.PI / 3.0 + 1;
	}

	public static void calculate(Data dat) {
		double dr = dat.dr;
		dat.configEnergy = configurationalEnergy(dat.cont_v, dat.gofr, dat.r,
				dr) * dat.density;
		dat.internEnergy = dat.configEnergy + 1.5 * dat.temp;
		// dat.isothermCompress = isothermalCompressibility(dat.density,
		// dat.gofr,
		// dat.r, dr);
		dat.twoBodyEntropy = twoBodyExcessEntropy(dat.density, dat.gofr, dat.r,
				dr);
		dat.compress = compressibility(dat.density, dat.lambda, dat.rough_gofr,
				dat.r, dr, dat.epsilon);
	}

}
