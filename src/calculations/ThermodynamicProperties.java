/**
 * 
 */
package calculations;

/**
 * @author Tres
 *
 */
public class ThermodynamicProperties {
	
	
	public static double configurationalEnergy(double[] u, double[] g, double[] r){
		double sum = 0;
		int n = 0;
		while(r[n] < 1.0)
			n++;
		while(r[n] < 2.0){
			sum += r[n]*r[n]*u[n]*g[n];
		}
		return 0;
	}
	
	public static double internalEnergy(double energy){
		return 1.5 + energy;
	}
	
	public static double isothermalCompressibility(double dens, double[] g, double[] r){
		double sum = 0.0;
		for(int i = 0; i < r.length; i++){
			sum += (g[i] - 1.0)*r[i]*r[i];
		}
		sum *= 4 * Math.PI;
		return sum + 1/dens;
	}
	
	public static double twoBodyExcessEntropy(double dens, double[] g, double[] r){
		double sum = 0;
		for(int i = 0; i < r.length; i++){
			if(g[i]< 0.0000001)
				sum += r[i]*r[i];
			else
				sum += r[i]*r[i]*(g[i]*Math.log(g[i])-g[i]+1);
		}
		return sum*2*dens*Math.PI;
	}
	
	public static double compressibility(double dens, double[] lambda, double[] saw_g, double[] r){
		double sum = 0.0;
		for(int i = 0, n = 0; n < lambda.length ; n++){
			while(r[i]<lambda[n])
				i++;
			sum += Math.pow(lambda[n], 3) * (saw_g[i]-saw_g[i-1]);
		}
		return sum*2.0*dens*Math.PI/3.0;
	}
	
}
