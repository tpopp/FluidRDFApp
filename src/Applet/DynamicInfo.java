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
	List<Data> systems = new LinkedList<Data>();
	
	public DynamicInfo() {
		
	}

}
