package Applet;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import calculations.Data;

/**
 * @author Tres
 *
 */
public class DynamicInfo implements Serializable {

	private static final long serialVersionUID = 7791030920524060243L;
	List<Data> systems = new LinkedList<>();
	
	public DynamicInfo() {
		
	}

}
