/**
 *  This file is part of FluidInfo.

    FluidInfo is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FluidInfo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FluidInfo.  If not, see <http://www.gnu.org/licenses/>.
 */
package calculations;

import java.util.Arrays;

public class DiscretePotential {

	/** Max difference in potentials for each step when computing discrete steps */
	private double MAX_DEPS = 0.50;
	/** Radii that will be given through the field in dat */
	private double[] given_r;
	/**
	 * Potentials corresponding to radii that will be given through the field in
	 * dat
	 */
	private double[] given_v;
	/**
	 * Dynamic programming: stores integrals for calculating Integral
	 * potentials, so they aren't repeated
	 */
	private double[] integrals;

	/**
	 * Convert from continuous potentials to discrete. Store discrete and
	 * continuous potentials.
	 * 
	 * 
	 * @param dat
	 *            collection of all necessary information to be used and stored
	 */
	public void discretePotential(Data dat, int numr, double deltar) {
		System.out.println(Arrays.toString(dat.given_r));
		System.out.println(Arrays.toString(dat.given_v));
		MAX_DEPS = dat.max_deps;
		given_r = dat.given_r;
		given_v = dat.given_v;

		int nsteps = 100; // initial number of steps in discretized potential;
						// will be adjusted if necessary by the program.
		int maxsteps = 1000; // upper limit for declaring arrays.

		int nr = numr;
		double dr = deltar;

		integrals = new double[given_r.length];
		double[] epsilon = new double[100]; // discrete potential
		double[] lambda = new double[100]; // discrete distance
		double[] r = new double[nr]; // radii to plot against
		double[] v_cont = new double[nr]; // continuous potentials
		double[] v_disc = new double[nr]; // discrete potentials

		// give initial values
		// discrete steps will be for radii <= 2
		for (int i = 0; i < 100; i++) {
			epsilon[i] = 0.0;
			lambda[i] = 2.0;
		}

		// Calculate values for integrals
		integrals[0] = 0.0;
		for (int i = 1; i < given_r.length; i++) {

			integrals[i] = calc_int(given_r[i], given_r[i - 1], given_v[i],
					given_v[i - 1]) + integrals[i - 1];
		}

		// initialize r and continuous potential.
		for (int i = 0; i < nr; i++) {
			r[i] = dr * (double) (i + 1);
			v_cont[i] = Calc_Potential(r[i]);
		}

		// Calculate width and height of last step.
		int jj_max = 20000;
		double aa, bb, cc = 0;
		double eps_a, eps_c;
		double f_a, f_c;
		double lam;
		int i;
		for(i = 0, lam = 1.01; i < 100; i++, lam+=0.01){
			lambda[i] = lam;
			epsilon[i] = (Calc_Integral(lam) - Calc_Integral(lam-0.01))
					/ 0.01;
		}
		
		// Calculate lambda and epsilon values
//		for (int i = maxsteps - 1; i > 0; i--) {
//			aa = 0.5;
//			bb = lambda[i];
//
//			// Zero in on root
//			for (int j = 0; j < jj_max; j++) {
//				cc = (aa + bb) / 2.;
//
//				eps_a = Calc_Potential(aa) - MAX_DEPS / 2.;
//				eps_c = Calc_Potential(cc) - MAX_DEPS / 2.;
//
//				f_a = Root_Func(aa, lambda[i], eps_a);
//				f_c = Root_Func(cc, lambda[i], eps_c);
//
//				if (Math.abs(f_c) < 0.000001)
//					break;
//
//				else {
//					if (f_a * f_c > 0.0)
//						aa = cc;
//					else
//						bb = cc;
//				}
//
//			}
//			
//			double j, eps;
//			for(j = bb - dr/10.0; j > 1.0; j -= dr/10.0){
//				eps = Math.abs(Calc_Potential(j)) - Math.abs(Calc_Potential(bb)) > 0 ? MAX_DEPS/2.0 : -MAX_DEPS/2.0;
//				if(Math.abs(Calc_Potential(j)) - Math.abs(Calc_Potential(bb)) > 0 && Root_Func(j, bb, Math.abs(Calc_Potential(j))-eps) < 0 ||
//						Math.abs(Calc_Potential(j)) - Math.abs(Calc_Potential(bb)) < 0 && Root_Func(j, bb, Math.abs(Calc_Potential(j))-eps) > 0){
//					break;
//				}
//			}
//
//			j = ((int)(j*100))/100.;
//			// store lambda and epsilon values for decided distance
//			lambda[i - 1] = j;
//
//			if (lambda[i - 1] < 1.0)
//				lambda[i - 1] = 1.0;
//
//			epsilon[i] = (Calc_Integral(lambda[i]) - Calc_Integral(lambda[i - 1]))
//					/ (lambda[i] - lambda[i - 1]);
//
//			nsteps++;
//
//			//if lambda is as one, stop
//			if (Math.abs(lambda[i - 1] - 1.0) < 0.00001) {
//				break;
//			}
//		}
//		System.out.println("Nsteps:  " + nsteps);

		// reassign lambdas and epsilons to the front of their arrays.
//		for (int i = 0; i < nsteps; i++) {
//			lambda[i] = lambda[maxsteps - nsteps + i];
//			epsilon[i] = epsilon[maxsteps - nsteps + i];
//		}
//
//		// clear remainder of arrays.
//		for (int i = nsteps; i < maxsteps; i++) {
//			lambda[i] = 0.0;
//			epsilon[i] = 0.0;
//		}

		// assign energy values to v_disc.
		for (i = 0; i < nr; i++) {
			if ((r[i] > 0.0) && (r[i] < 1.0)) {
				v_disc[i] = 1.0 / 0.0;
			}

			else if ((r[i] >= 1.0) && (r[i] <= 2.0)) {
				if ((r[i] >= 1.0) && (r[i] < lambda[0])) // check first well.
				{
					v_disc[i] = epsilon[0];
				}

				else // check other wells.
				{
					for (int j = 1; j < nsteps; j++) {
						if ((r[i] >= lambda[j - 1]) && (r[i] < lambda[j])) {
							v_disc[i] = epsilon[j];
						}
					}
				}
			}

			else if (r[i] > 2.0) {
				v_disc[i] = 0.0;
			}
		}
		System.out.println("Got here");

		// Store data in textual format and store arrays for other calculations
		String paramsfp = Messages.getString("DiscretePotential.StepTitle");
		paramsfp = paramsfp
				.concat(String.format(
						Messages.getString("DiscretePotential.StepHeaderFormat"),
						Messages.getString("DiscretePotential.StepColumnHeader"), Messages.getString("DiscretePotential.LambdaColumnHeader"), Messages.getString("DiscretePotential.EpsilonColumnHeader"), Messages.getString("DiscretePotential.DeltaEpsilonColumnHeader"))); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		for (i = 0; i < nsteps-1; i++) {
			paramsfp = paramsfp.concat(String.format(
					Messages.getString("DiscretePotential.StepValuesFormat"),
					i, lambda[i], epsilon[i], epsilon[i + 1] - epsilon[i]));
		}
		paramsfp = paramsfp.concat(String.format(
				Messages.getString("DiscretePotential.StepValuesFormat"),
				i, lambda[i], epsilon[i], epsilon[i]));

		String potfp = String
				.format(Messages
						.getString("DiscretePotential.PotentialsHeaderFormat"), Messages.getString("DiscretePotential.RadiusColumnHeader"), Messages.getString("DiscretePotential.ContinuousPotentialColumnHeader"), //$NON-NLS-2$ //$NON-NLS-3$
						Messages.getString("DiscretePotential.DiscretePotentialColumnHeader"));
		for (i = 0; i < nr; i++) {
			potfp = potfp.concat(String.format(Messages
					.getString("DiscretePotential.PotentialValuesFormat"),
					r[i], v_cont[i], v_disc[i]));
		}
		dat.cont_v = v_cont;
		dat.disc_v = v_disc;
		dat.printed.put(Messages.getString("DiscretePotential.StepsStringKey"),
				paramsfp);
		dat.printed.put(
				Messages.getString("DiscretePotential.PotentialsStringKey"),
				potfp);
		dat.epsilon = epsilon;
		dat.lambda = lambda;
		dat.r = r;
		System.err.println(Arrays.toString(dat.lambda));
		System.err.println(Arrays.toString(dat.epsilon));
	}

	/**
	 * Trapezoidal rule to calculate integral between two points.
	 * 
	 * @param first
	 *            lower x
	 * @param second
	 *            higher x
	 * @param given_v2
	 *            potential corresponding to first
	 * @param given_v3
	 *            potential corresponding to second
	 * @return returns area of trapezoid
	 */
	private static double calc_int(double first, double second,
			double given_v2, double given_v3) {
		return 0.5 * (given_v3 + given_v2) * (first - second);
	}

	/**
	 * Calulate integral from a to lambda_n and subtract the desired value to
	 * obtain variation from allowable difference.
	 * 
	 * @param a
	 *            lower bound
	 * @param lambda_n
	 *            upper bound
	 * @param eps
	 *            desired average value. Subtracted in attempt to get near zero.
	 * @return
	 */
	private double Root_Func(double a, double lambda_n, double eps) {
		double return_val;

		return_val = Math.abs(Calc_Integral(lambda_n) - Calc_Integral(a)) - eps
				* (lambda_n - a);

		return return_val;
	}

	/**
	 * Calculates potential at given r, by computing a line between the radii
	 * and potentials directly above and below it. The potential is the location
	 * on the line at radius r.
	 * 
	 * @param r
	 *            radius to calculate potential at
	 * @return returns calculated potential
	 */
	private double Calc_Potential(double r) {
		int cur_r = 0;
		double slope;
		double intercept;
		if (r < given_r[0])
			return 1.0 / 0.0;
		while (cur_r < given_r.length && given_r[cur_r] <= r)
			cur_r++;
		if (cur_r == given_r.length)
			return 0;
		slope = (given_v[cur_r] - given_v[cur_r - 1])
				/ (given_r[cur_r] - given_r[cur_r - 1]);
		intercept = given_v[cur_r] - slope * given_r[cur_r];
		return slope * r + intercept;
	}

	/**
	 * Computes trapezoid from nearest given_r that is less and adds to the
	 * pre-computed integral below that given_r.
	 * 
	 * @param r
	 *            radius to calculate integral for
	 * @return returns calculated integral from some constant x to r.
	 */
	private double Calc_Integral(double r) {
		int cur_r = 0;
		double slope;
		double intercept;
		if (r < given_r[0])
			return 0;
		while (cur_r < given_r.length && given_r[cur_r] <= r)
			cur_r++;
		if (cur_r == given_r.length)
			return integrals[cur_r - 1];
		slope = (given_v[cur_r] - given_v[cur_r - 1])
				/ (given_r[cur_r] - given_r[cur_r - 1]);
		intercept = given_v[cur_r] - slope * given_r[cur_r];
		double value = slope * r + intercept;
		double trap = (r - given_r[cur_r - 1]) * 0.5
				* (value + (slope * given_r[cur_r - 1] + intercept));
		return trap + integrals[cur_r - 1];

	}

}