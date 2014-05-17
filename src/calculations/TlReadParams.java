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

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

public class TlReadParams extends SwingWorker<Void, Void> {
	private Integer progress = 0;
	private double phi, beta;
	private double[] epsilon, lambda, r_dmd;
	Data dat;

	public TlReadParams(double phi, double[] epsilon, double[] lambda,
			double[] r_dmd, Data dat) {

		this.phi = phi;
		this.epsilon = epsilon;
		this.lambda = lambda;
		this.r_dmd = r_dmd;
		this.dat = dat;
	}

	/**
	 * Set-up then calls tanglu-struct to do calculations
	 * 
	 * @param phi
	 *            packing fraction
	 * @param epsilon
	 *            discrete potential steps
	 * @param lambda
	 *            discrete radius steps
	 * @param r_dmd
	 *            radii to calculate for
	 * @param dat
	 *            various information
	 * @return changes passed arrays and objects
	 */
	public void tlReadParams(double phi, double[] epsilon, double[] lambda,
			double[] r_dmd, Data dat) throws IOException {

		double rho = phi * 6.0 / Math.PI;

		// where to store RDF
		double[] gofr = new double[r_dmd.length];

		tanglu_struct(rho, r_dmd, gofr, lambda, epsilon, dat);

		// store data
		String rdf = String.format(
				Messages.getString("TlReadParams.SawToothedGofrFormat"),
				Messages.getString("TlReadParams.RadiusColumnHeader"),
				Messages.getString("TlReadParams.GofrColumnHeader"));
		for (int i = 0; i < r_dmd.length; i++) {
			rdf = rdf.concat(String.format(
					Messages.getString("TlReadParams.RDF_Format"), r_dmd[i],
					gofr[i]));
			if (r_dmd[i] > 8.0)
				break;
		}
		dat.gofr = gofr;
		dat.rough_gofr = gofr;
	}

	/**
	 * Calculations for computing radial distribution function
	 * 
	 * @param rho
	 *            number density
	 * @param r
	 *            array of radii to calculate for
	 * @param gofr
	 *            array to store g(r) in
	 * @param lambda
	 *            radii for discrete steps
	 * @param epsilon
	 *            discrete potentials
	 * @param dat
	 *            to store data in
	 */
	private void tanglu_struct(double rho, final double r[],
			final double gofr[], final double[] lambda, double[] epsilon,
			Data dat) {
		progress = 0;
		setProgress(0);

		beta = 1 / dat.temp;

		// final tanglu_constants tlc = new tanglu_constants();
		//
		final double[] depsilon = new double[epsilon.length];
		//
		depsilon[epsilon.length - 1] = epsilon[epsilon.length - 1];
		//
		// find delta epsilon
		for (int i = 0; i < epsilon.length - 1; i++)
			depsilon[i] = epsilon[i] - epsilon[i + 1];

		epsilon = depsilon;
		//
		// tlc.rho = rho;
		// tlc.eta = rho * Math.PI / 6.; // packing fraction
		// tlc.f = 3. + 3. * tlc.eta - Math.pow(tlc.eta, 2.);
		// tlc.yplus = Math.pow(1. + (Math.pow((1. + 2. * Math.pow(tlc.eta, 4.)
		// / Math.pow(tlc.f, 2.)), 0.5)), (1. / 3.));
		// tlc.yminus = Math
		// .pow(Math.abs(1. - (Math.pow((1. + 2. * Math.pow(tlc.eta, 4.)
		// / Math.pow(tlc.f, 2.)), 0.5))), (1. / 3.))
		// * -1.0;
		//
		// tlc.c = new Complex(0, 2. * Math.PI / 3.).exp();
		//
		// for (int i = 0; i <= 2; i++) {
		// tlc.t[i] = tsubi(i, tlc);
		// }

		for (int i = 0; i < r.length; i++) {
			if (r[i] < 1.0)
				gofr[i] = 0.0;
			else if (r[i] >= 10.0) {
				gofr[i] = 1.0;
			} else {
				gofr[i] = g0gmsa(r[i]);
			}
		}

		int threads = (Runtime.getRuntime().availableProcessors() + 1) / 2;
		ExecutorService e = Executors.newFixedThreadPool(threads);
		// for (int i = 0; i < r.length; i++) {
		// if (r[i] < 1.0)
		// gofr[i] = 0.0;
		// else {
		// final int ii = i;
		// e.execute(new Runnable() {
		//
		// public void run() {
		// gofr[ii] = g0gmsa(r[ii], tlc).real();
		// }
		// });
		// }
		// }
		// try {
		// e.shutdown();
		// e.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
		// } catch (InterruptedException e1) {
		// e1.printStackTrace();
		// }
		//
		// e = Executors
		// .newFixedThreadPool(threads > lambda.length ? lambda.length
		// : threads);
		for (int ja = 0; ja < lambda.length; ja++) {
			// final tanglu_constants tlcN = new tanglu_constants();
			// tlcN.c = tlc.c;
			// tlcN.eta = tlc.eta;
			// tlcN.f = tlc.f;
			// tlcN.rho = tlc.rho;
			// tlcN.t = tlc.t;
			// tlcN.yminus = tlc.yminus;
			// tlcN.yplus = tlc.yplus;
			final int j = ja;
			e.execute(new Runnable() {
				//
				@Override
				public void run() {
					double t;
					// for (int i = 0; i <= 2; i++) {
					// tlcN.a1[i] = a1(i, lambda[j], tlcN);
					// tlcN.a2[i] = a2(i, lambda[j], tlcN);
					// tlcN.a3[i] = a3(i, lambda[j], tlcN);
					//
					// }

					for (int i = 0; i < r.length; i++) {
						if (r[i] < 1.0)
							continue;
						t = Math.exp(g1sw(r[i], depsilon[j], lambda[j]));
						synchronized (gofr) {
							gofr[i] *= t;
						}
						if (r[i] >= 10.0)
							break;
					}
					synchronized (progress) {
						setProgress((int) ((double) (++progress)
								/ lambda.length * 100));
					}
				}

			});
		}
		try {
			e.shutdown();
			e.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		return;
	}

	/**
	 * Calculates roots of equation S(t) = 0
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 43
	 * 
	 * @param i
	 *            index for calculation
	 * @param tlc
	 *            constants
	 * @return one f the roots
	 */
	private static Complex tsubi(int i, tanglu_constants tlc) {

		Complex tval = (tlc.c.pow(i).times(tlc.yplus)
				.plus(tlc.c.pow(-1. * i).times(tlc.yminus))
				.times(Math.pow(2. * tlc.eta * tlc.f, 1. / 3.)).plus(-2.
				* tlc.eta)).div(1 - tlc.eta);

		return tval;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 41
	 * 
	 * @param i
	 *            location in tlc array
	 * @param lambda
	 *            range of function
	 * @param tlc
	 *            constants
	 * @return value to be stored in tlc.a1[i]
	 */
	private static Complex a1(int i, double lambda, tanglu_constants tlc) {
		Complex a1val = new Complex(0, 0);

		a1val = tlc.t[i].pow(5.0).times(Math.pow(lambda, 2.) - lambda)
				.plus(tlc.t[i].pow(4.0).times(1.0 + 4.0 * lambda))
				.minus(tlc.t[i].pow(3.0).times(4.0));
		a1val = a1val.div(S1(tlc.t[i], tlc.eta).pow(2.0));
		a1val = a1val.plus(tlc.t[i].pow(4.0)
				.minus(tlc.t[i].pow(5).times(lambda))
				.times(S2(tlc.t[i], tlc.eta))
				.div(S1(tlc.t[i], tlc.eta).pow(3.0)));

		return a1val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 41
	 * 
	 * @param i
	 *            location in tlc array
	 * @param lambda
	 *            range of function
	 * @param tlc
	 *            constants
	 * @return value to be stored in tlc.a2[i]
	 */
	private static Complex a2(int i, double lambda, tanglu_constants tlc) {
		Complex a2val = new Complex(0, 0);

		a2val = tlc.t[i].pow(4.0).minus(tlc.t[i].pow(5.0).times(lambda));

		a2val = a2val.div(S1(tlc.t[i], tlc.eta).pow(2.0));

		return a2val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 41
	 * 
	 * @param i
	 * @param lambda
	 *            range of function
	 * @param tlc
	 *            constans
	 * @return value to be stored in tlc.a3[i]
	 */
	private static Complex a3(int i, double lambda, tanglu_constants tlc) {
		Complex a3val = new Complex(0, 0);

		a3val = tlc.t[i].pow(4.0).times(lambda * 5.0);
		a3val = a3val.minus(tlc.t[i].pow(3.0).times(4.0));
		a3val = a3val.div(S1(tlc.t[i], tlc.eta).pow(2.0));
		Complex temp = tlc.t[i].pow(4.0).minus(tlc.t[i].pow(5.0).times(lambda));
		temp = temp.times(S2(tlc.t[i], tlc.eta)).div(
				S1(tlc.t[i], tlc.eta).pow(3.0));
		a3val = a3val.plus(temp);
		return a3val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 9 first
	 * derivative
	 */
	private static Complex S1(Complex t, double eta) {
		Complex S1val = new Complex(0, 0);

		S1val = t.pow(2.0).times(Math.pow(1.0 - eta, 2.0) * 3.0);
		S1val = S1val.plus(t.times(12.0 * eta * (1.0 - eta))).plus(
				18.0 * eta * eta);

		return S1val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 9
	 * second derivative
	 */
	private static Complex S2(Complex t, double eta) {
		Complex S2val = new Complex(0, 0);

		S2val = t.times(6.0 * Math.pow(1.0 - eta, 2.0)).plus(
				12.0 * eta * (1.0 - eta));

		return S2val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 73
	 * Square Well potential summation step First order RDF
	 * 
	 * @param r
	 *            radius
	 * @param epsilon
	 *            potential to relate to
	 * @param lambda
	 *            radius to relate to
	 * @param tlc
	 *            constants
	 * @return square well RDF calculation
	 */
	private double g1sw(double r, double epsilon, double lambda) {
		// int n = 0;
		// boolean nloop = true;
		//
		// Complex g1sw_val, g1sw_insidesum, g1sw_inc;
		//
		// g1sw_val = new Complex(0, 0);
		//
		// while (nloop) {
		// g1sw_insidesum = new Complex(0, 0);
		// for (int i = 0; i <= 2; i++) {
		// g1sw_inc = ((tlc.a1[i].times(Dfunc(6, n, n + 2, tlc.t[i], r - n
		// - 1., tlc)).plus(tlc.a2[i].times(Efunc(6, n, n + 2,
		// tlc.t[i], r - n - 1., tlc)))).times(tlc.t[i].times(
		// lambda - 1.0).exp())
		// .minus((tlc.a3[i].times(Dfunc(6, n, n + 2, tlc.t[i], r
		// - n - lambda, tlc)).plus(tlc.a2[i].times(Efunc(
		// 6, n, n + 2, tlc.t[i], r - n - lambda, tlc))))))
		// .times((1. + n) * Math.pow((-12. * tlc.eta), n));
		// g1sw_insidesum = g1sw_insidesum.plus(g1sw_inc);
		// }
		//
		// g1sw_val = g1sw_val.plus(g1sw_insidesum);
		// n++;
		//
		// if (g1sw_insidesum.div(g1sw_val).abs() < 0.0001)
		// nloop = false;
		// }
		//
		// g1sw_val = g1sw_val.times(beta * -epsilon * Math.pow(1. - tlc.eta,
		// 8.)
		// / r);
		//
		//
		// return g1sw_val.real();
		int x2 = (int) Math.ceil((dat.packingFraction - 0.0001) * 100);
		int x1 = x2 - 1;
		int y2 = (int) Math.ceil((r - 0.0001) * 100);
		int y1 = y2 - 1;
		int lam = (int) Math.round(lambda * 100) - 100;
		double x = dat.packingFraction % 0.01 * 100;
		x = x < 0.000001 ? 1 : x;
		double y = r % 0.01 * 100;
		y = y < 0.000001 ? 1 : y;

		if (r - lambda < 0.0099 && r - lambda > 0) {
			y1++;
			y2++;
			y -= 1;
		} else if (r - lambda > -0.0099 && r - lambda < 0) {
			y1--;
			y2--;
			y += 1;
		}

		double a = dat.g1[x1][y1][lam];
		double b = dat.g1[x2][y1][lam];
		double c = dat.g1[x1][y2][lam];
		double d = dat.g1[x2][y2][lam];

		return interpolate(a, b, c, d, x, y) * beta * -epsilon;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 70 Hard
	 * spheres for PY approximation
	 * 
	 * @param r
	 *            radius
	 * @return complex number representing hard spheres RDF
	 */
	private double g0gmsa(double r) {
		int x2 = (int) Math.ceil((dat.packingFraction - 0.00001) * 100);
		int x1 = x2 - 1;
		int y2 = (int) Math.ceil((r - 0.00001) * 100);
		int y1 = y2 - 1;

		double a = dat.g0[x1][y1];
		double b = dat.g0[x2][y1];
		double c = dat.g0[x1][y2];
		double d = dat.g0[x2][y2];

		double x = dat.packingFraction % 0.01 * 100;
		x = x < 0.000001 ? 1 : x;
		double y = r % 0.01 * 100;
		y = y < 0.000001 ? 1 : y;

		return interpolate(a, b, c, d, x, y);

		/*
		 * if (dat.g0 != null) { if ((dat.density % 0.01 < 0.0001 || dat.density
		 * % 0.01 > 0.0099) && (r % 0.01 < 0.0001 || r % 0.01 > 0.0099)) return
		 * dat.g0[(int) Math.round(dat.density * 100)][(int) Math .round(r *
		 * 100)]; else { double x1, x2, y1, y2; x1 = dat.g0[(int)
		 * Math.floor(dat.density) * 100][0]; return interpolate(x1, y1, x2, y2,
		 * dat.density, r); } } else { int n = 0; boolean nloop = true;
		 * 
		 * Complex g0gmsa_val, g0gmsa_inc;
		 * 
		 * double z0 = 2. (1. + 2. * tlc.eta) / (4. - tlc.eta) / Math.pow(1. -
		 * tlc.eta, 2.) (3. + Math.pow(21. - 15. * tlc.eta + 3. * tlc.eta
		 * tlc.eta, 0.5));
		 * 
		 * g0gmsa_val = new Complex(0, 0);
		 * 
		 * // until sufficiently converged while (nloop) { g0gmsa_inc =
		 * (Cfunc(1, n + 1, n + 1, r - n - 1, tlc) .plus(Dfunc(6, n, n + 2, new
		 * Complex(z0, 0), r - n - 1., tlc).times( 0.5 * tlc.eta * tlc.eta * (1.
		 * - tlc.eta) (1. + n)))).times(Math.pow( (-12. * tlc.eta), n));
		 * g0gmsa_val = g0gmsa_val.plus(g0gmsa_inc);
		 * 
		 * n++;
		 * 
		 * if (g0gmsa_inc.div(g0gmsa_val).abs() < 0.000001) nloop = false; }
		 * 
		 * return g0gmsa_val.div(r).real(); }
		 */
	}

	private double interpolate(double a, double b, double c, double d,
			double x1, double x2) {
		double b1, b2, b3, b4;
		b1 = a;
		b2 = b - a;
		b3 = c - a;
		b4 = a - b - c + d;
		return b1 + b2 * x1 + b3 * x2 + b4 * x1 * x2;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 63s
	 */
	private static Complex Afunc(int n1, int n2, int k1, int alpha,
			tanglu_constants tlc) {
		int istart;

		Complex Afunc_val = new Complex(0, 0);

		if ((k1 - n1) > 0)
			istart = k1 - n1;
		else
			istart = 0;

		for (int i = istart; i <= n2; i++) {
			Afunc_val = Afunc_val.plus(tlc.t[alpha].pow(n1 + i - k1).times(
					factorial(n2) * factorial(i + n1) / factorial(i)
							/ factorial(n2 - i) / factorial(i + n1 - k1)
							* Math.pow(1. + tlc.eta / 2., i)
							* Math.pow(1. + 2. * tlc.eta, n2 - i)));
		}

		return Afunc_val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 61
	 */
	private static Complex Bfunc(int n1, int n2, int n3, int i, int alpha,
			tanglu_constants tlc) {
		int beta = -1, gamma = -1;

		if (alpha == 0) {
			beta = 1;
			gamma = 2;
		} else if (alpha == 1) {
			beta = 2;
			gamma = 0;
		} else if (alpha == 2) {
			beta = 0;
			gamma = 1;
		}

		Complex Bfunc_val = new Complex(0.0, 0.0);

		for (int k1 = 0; k1 <= n3 - i; k1++) {
			for (int k2 = 0; k2 <= n3 - i - k1; k2++) {
				Bfunc_val = Bfunc_val.plus(new Complex(Math.pow(-1., n3 - i
						- k1)
						* factorial(n3 - 1 + k2)
						* factorial(2 * n3 - 1 - i - k1 - k2)
						/ factorial(k1)
						/ factorial(k2)
						/ factorial(n3 - i - k1 - k2)
						/ Math.pow(factorial(n3 - 1), 2.), 0)
						.div(tlc.t[alpha].minus(tlc.t[beta]).pow(n3 + k2))
						.div(tlc.t[alpha].minus(tlc.t[gamma]).pow(
								2. * n3 - i - k1 - k2))
						.times(Afunc(n1, n2, k1, alpha, tlc)));
			}
		}

		Bfunc_val = Bfunc_val.times(Math.pow(1. - tlc.eta, -2. * n3));

		return Bfunc_val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 64
	 */
	private static Complex Cfunc(int n1, int n2, int n3, double r,
			tanglu_constants tlc) {
		Complex Cfunc_val = new Complex(0, 0);

		if (r < 0.)
			return new Complex(0, 0);

		for (int alpha = 0; alpha <= 2; alpha++) {
			for (int i = 1; i <= n3; i++) {
				Cfunc_val = Cfunc_val.plus(tlc.t[alpha]
						.times(r)
						.exp()
						.times(Bfunc(n1, n2, n3, i, alpha, tlc).div(
								factorial(i - 1)).times(Math.pow(r, i - 1))));
			}
		}

		// in case where n1 + n2 = 3*n3 and r = 0, add additional term from Eq.
		// (64):
		if ((n2 < 0.00001) && (Math.abs(r) < 0.00001)) {
			Cfunc_val = Cfunc_val.plus(Math.pow(1. + tlc.eta / 2., n2)
					/ Math.pow(1. - tlc.eta, 2. * n3));
		}

		return Cfunc_val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 67
	 */
	private static Complex Dfunc(int n1, int n2, int n3, Complex z, double r,
			tanglu_constants tlc) {

		Complex Dfunc_val = new Complex(0.0, 0.0), Dfunc_insidesum;

		if (r < 0.)
			return new Complex(0, 0);

		for (int alpha = 0; alpha <= 2; alpha++) {
			for (int i = 1; i <= n3; i++) {
				Dfunc_insidesum = new Complex(0., 0.);
				for (int j = 0; j <= i - 1; j++) {
					Dfunc_insidesum = Dfunc_insidesum.plus(tlc.t[alpha]
							.plus(z)
							.pow(j)
							.times(Math.pow(r, j) * Math.pow(-1., j)
									/ factorial(j)));
				}

				Dfunc_val = Dfunc_val.plus(Bfunc(n1, n2, n3, i, alpha, tlc)
						.times(Math.pow(-1., i))
						.div(tlc.t[alpha].plus(z).pow(i))
						.times(z.times(-r)
								.exp()
								.minus(tlc.t[alpha].times(r).exp()
										.times(Dfunc_insidesum))));
			}
		}

		// in case where n1 + n2 = 3*n3, add additional term from Eq. (67):
		if (n2 < 0.00001) {
			Dfunc_val = Dfunc_val.plus(z
					.times(-r)
					.exp()
					.times(Math.pow(1. + tlc.eta / 2., n2)
							/ Math.pow(1. - tlc.eta, 2 * n3)));

		}

		return Dfunc_val;
	}

	/**
	 * http://www.tandfonline.com/doi/abs/10.1080/002689797172697 -- eq. 68
	 */
	private static Complex Efunc(int n1, int n2, int n3, Complex z, double r,
			tanglu_constants tlc) {

		Complex Efunc_val = new Complex(0.0, 0.0), Efunc_insidesum;

		if (r < 0.)
			return new Complex(0.0, 0.0);

		for (int alpha = 0; alpha <= 2; alpha++) {
			for (int i = 1; i <= n3; i++) {
				Efunc_insidesum = new Complex(0.0, 0.0);
				for (int j = 1; j <= i - 1; j++) {
					Efunc_insidesum = Efunc_insidesum.plus(tlc.t[alpha]
							.plus(z)
							.pow(j - 1.)
							.times((i - j) * Math.pow(-1., j) * Math.pow(r, j)
									/ factorial(j)));
				}

				Efunc_val = Efunc_val.plus(Bfunc(n1, n2, n3, i, alpha, tlc)
						.times(Math.pow(-1., i))
						.div(tlc.t[alpha].plus(z).pow(i))
						.times(z.times(-r)
								.exp()
								.times(r)
								.plus((z.times(-r).exp().minus(tlc.t[alpha]
										.times(r).exp()))
										.times(i)
										.div(tlc.t[alpha].plus(z))
										.minus(tlc.t[alpha].times(r).exp()
												.times(Efunc_insidesum)))));
			}
		}

		// in case where n1 + n2 = 3*n3, add additional term from Eq. (68):
		if (n2 < 0.00001) {
			Efunc_val = Efunc_val.plus(z
					.times(-r)
					.exp()
					.times(Math.pow(1. + tlc.eta / 2., n2)
							/ Math.pow(1. - tlc.eta, 2 * n3) * r));
		}

		return Efunc_val;
	}

	/**
	 * As name implies
	 * 
	 * @param n
	 *            to compute n!
	 * @return n!
	 */
	private static double factorial(int n) {
		if (n <= 0)
			return 1;

		double res = n;

		while (--n > 1)
			res *= n;

		return res;
	}

	@Override
	protected Void doInBackground() throws Exception {
		tlReadParams(phi, epsilon, lambda, r_dmd, dat);
		return null;
	}
}
