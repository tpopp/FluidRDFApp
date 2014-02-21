/**
 * 
 */
package Applet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tres
 * 
 */
public final class SetupInfo  implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -124980253850060156L;
	public List<Parameter> params;

	public SetupInfo() {
		params = new ArrayList<>();
		params.add(new Parameter("Packing Fraction", false, "", 0.40));
		params.add(new Parameter("Number Density", false, "", 0.763944));
		params.add(new Parameter("Delta eps max", true, "Max potential change",
				0.10));
		params.add(new Parameter("# of steps", true,
				"I'm not sure what this is for", 20));
		params.add(new Parameter("# of r points", true,
				"How many distances to calculate values at", 1000));
		params.add(new Parameter("delta r", true,
				"Distance between each r value", 0.01));
		params.add(new Parameter("r-max", true, "Farthest r value calculated",
				10.0));
	}

	class Parameter {
		String parameter, hintText;
		double init_value;
		boolean hintProvided;

		Parameter(String parameter, boolean hintProvided, String hintText,
				double init_value) {
			this.parameter = parameter;
			this.hintProvided = hintProvided;
			this.hintText = hintText;
			this.init_value = init_value;
		}
	}

}
