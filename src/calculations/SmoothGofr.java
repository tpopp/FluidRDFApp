package calculations;

import java.io.IOException;

public class SmoothGofr {

	/**
	 * @param lambda
	 *            array of radii
	 * @param dat
	 *            contains arrays for g(r)
	 * @throws IOException
	 */
	public void smoothGofr(double[] lambda, Data dat) throws IOException {
		// TODO USER INPUT
		int maxsteps = 1000; // upper limit for declaring arrays.
		int nsteps = lambda.length;

		int maxnr = 100000;
		double[] r = dat.r;
		double[] g_corr = new double[maxnr];
		double[] deltag = new double[maxnr];
		double[] g_smooth = new double[maxnr];

		// step boundaries.
		double[] g_plus = new double[maxsteps];
		double[] g_minus = new double[maxsteps];
		double[] g_avg = new double[maxsteps];
		double[] lambda_minus = new double[maxsteps];
		double[] lambda_plus = new double[maxsteps];

		int[] index_minus = new int[maxsteps], index_plus = new int[maxsteps];

		int i, j; // loop control variables.

		g_corr = dat.gofr;

		// FIND VALUES OF R and G(R) WHICH BRACKET EACH LAMBDA, AND CALCULATE
		// G_AVG:
		for (i = 0; i < lambda.length; i++) {
			j = 0;
			while (r[j] < lambda[i]) // Skip until r > current lambda
			{
				j++;
				continue;
			}

			index_plus[i] = j;
			index_minus[i] = j - 1;

			lambda_plus[i] = r[j];
			lambda_minus[i] = r[j - 1];

			g_plus[i] = g_corr[j] + (g_corr[j + 1] - g_corr[j])
					/ (r[j + 1] - r[j]) * (lambda[i] - r[j]);
			g_minus[i] = g_corr[j - 2] + (g_corr[j - 1] - g_corr[j - 2])
					/ (r[j - 1] - r[j - 2]) * (lambda[i] - r[j - 2]);

			g_avg[i] = 0.5 * (g_plus[i] + g_minus[i]);
		}

		// FOR EACH WELL (LAMBDA_I-1 < R < LAMBDA_I) FIT A LINE BETWEEN
		// [LAMBDA_I-1, DELTAG(LAMBDA_I-1)] AND [LAMBDA_I, DELTAG(LAMBDA_I)],
		// AND POPULATE DELTAG(NR).
		// First well:
		for (j = 0; j <= index_minus[0]; j++) {
			if (r[j] < 1.0000)
				deltag[j] = 0.0;
			else
				deltag[j] = 2. * (g_avg[0] - g_minus[0]) / (lambda[0] - 1.)
						* (r[j] - (lambda[0] + 1.) / 2.);
		}

		// Middle wells:
		for (i = 1; i < nsteps - 1; i++) {
			for (j = index_plus[i - 1]; j <= index_minus[i]; j++) {
				deltag[j] = (g_avg[i - 1] - g_plus[i - 1])
						+ ((g_avg[i] - g_minus[i]) - (g_avg[i - 1] - g_plus[i - 1]))
						/ (lambda[i] - lambda[i - 1]) * (r[j] - lambda[i - 1]);
			}
		}

		// Last well:
		for (j = index_plus[nsteps - 2]; j <= index_minus[nsteps - 1]; j++) {
			deltag[j] = ((g_plus[nsteps - 1] - g_minus[nsteps - 1]) - (g_avg[nsteps - 2] - g_plus[nsteps - 2]))
					/ (lambda[nsteps - 1] - lambda[nsteps - 2])
					* (r[j] - lambda[nsteps - 2])
					+ (g_avg[nsteps - 2] - g_plus[nsteps - 2]);
		}

		// store data
		String smoothfp = "";

		smoothfp = smoothfp
				.concat(String.format(
						Messages.getString("SmoothGofr.GofrHeaderFormat"),
						Messages.getString("SmoothGofr.RadiusColumnHeader"), Messages.getString("SmoothGofr.GofrColumnHeader"), Messages.getString("SmoothGofr.ChangeInGofrColumnHeader"), Messages.getString("SmoothGofr.SmoothedGofrColumnHeader"))); //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		for (j = 0; j < g_corr.length; j++) {
			g_smooth[j] = g_corr[j] + deltag[j];

			smoothfp = smoothfp.concat(String.format(
					Messages.getString("SmoothGofr.GofrDataFormat"), r[j],
					g_corr[j], deltag[j], g_smooth[j]));
		}

		dat.printed
				.put(Messages.getString("SmoothGofr.RDFStringKey"), smoothfp);
		dat.gofr = g_smooth;
	}

}
