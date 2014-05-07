/**
 * 
 */
package calculations;

/**
 * @author Tres
 *
 */
public class ThermodynamicProperties {
	
	public static double configurationalEnergy(double[] u, double[] g, double[] r, double dr){
		if(r.length<2 || u.length < 2 || g.length < 2)
			return 0.0;
		double sum = 0;
		int n = 0;
		while(r[n] < 1.0)
			n++;
		double temp = r[n]*r[n]*u[n]*g[n];
		n++;
		while(r[n] < 2.0){
			sum += r[n]*r[n]*u[n]*g[n];
			n++;
		}
		n--;
		sum -= r[n]*r[n]*u[n]*g[n];
		temp += r[n]*r[n]*u[n]*g[n];
		return (sum*2+temp)*dr/2.0;
	}
	
	public static double internalEnergy(double energy){
		return 1.5 + energy;
	}
	
	public static double isothermalCompressibility(double dens, double[] g, double[] r, double dr){
		if(r.length<2 || g.length < 2)
			return 0.0;
		double sum = 0.0;
		for(int i = 1; i < r.length-1; i++){
			sum += (g[i] - 1.0)*r[i]*r[i];
		}
		sum *= 2;
		sum += (g[0] - 1.0)*r[0]*r[0] + (g[r.length-1] - 1.0)*r[r.length-1]*r[r.length-1];
		sum *= 2 * Math.PI*dr;
		return sum + 1/dens;
	}
	
	public static double twoBodyExcessEntropy(double dens, double[] g, double[] r, double dr){
		if(r.length<2 || g.length < 2)
			return 0.0;
		double sum = 0;
		for(int i = 1; i < r.length-1; i++){
			if(g[i]< 0.0000001)
				sum += r[i]*r[i];
			else
				sum += r[i]*r[i]*(g[i]*Math.log(g[i])-g[i]+1);
		}
		sum *= 2;
		if(g[0] < 0.0000001)
			sum += r[0]*r[0];
		else
			sum += r[0]*r[0]*(g[0]*Math.log(g[0])-g[0]+1);
		if(g[r.length-1] < 0.0000001)
			sum += r[r.length-1]*r[r.length-1];
		else
			sum += r[r.length-1]*r[r.length-1]*(g[r.length-1]*Math.log(g[r.length-1])-g[r.length-1]+1);
		return -sum*dens*Math.PI*dr;
	}
	
	public static double compressibility(double dens, double[] lambda, double[] saw_g, double[] r, double dr){
		double sum = 0.0;
		for(int i = 0, n = 0; n < lambda.length ; n++){
			while(r[i]<lambda[n])
				i++;
			sum += Math.pow(lambda[n], 3) * (saw_g[i]-saw_g[i-1]);
		}
		return sum*2.0*dens*Math.PI/3.0*dr;
	}
	
	public static void calculate(Data dat){
		double dr = dat.dr;
		dat.configEnergy = configurationalEnergy(dat.cont_v, dat.gofr, dat.r, dr);
		dat.internEnergy = dat.configEnergy + 1.5;
		dat.isothermCompress = isothermalCompressibility(dat.density, dat.gofr, dat.r, dr);
		dat.twoBodyEntropy = twoBodyExcessEntropy(dat.density, dat.gofr, dat.r, dr);
		dat.compress = compressibility(dat.density, dat.lambda, dat.rough_gofr, dat.r, dr);
	}
	
}
