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
	public HashMap<String, String> printed = new HashMap<>();
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
	public boolean thermo = false;
	public double temp;
}
