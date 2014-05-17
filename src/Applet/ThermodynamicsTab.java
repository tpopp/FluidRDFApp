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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import calculations.Data;

/**
 * @author Tres
 * 
 */
public class ThermodynamicsTab extends JPanel implements Serializable {

	private static final long serialVersionUID = -9151962467153851374L;
	DynamicInfo systems;
	private SystemsModel mod;
	FluidApp parent;
	private JTable table;
	private JTable information;
	private JScrollPane scrollPane_1;
	private JScrollPane scroll;
	private GridBagConstraints gbc_scroll2;
	private String[][] header = {
			{ "<html><p style=\"white-space:nowrap\">Internal Energy, <i>u/\u03B5</i></p></html>" },
			{ "<html><p style=\"white-space:nowrap\">Configurational Energy, <i>e/\u03B5</i></p></html>" },
			{ "<html><p style=\"white-space:nowrap\">Compressibility, <i>Z</i></p></html>" },
//			{ "<html><p style=\"white-space:nowrap\">Isothermal Compressibility, <i>\u03C1k</i><sub>B</sub><i>T&chi</i><sub>T</sub></p></html>" },
			{ "<html><p style=\"white-space:nowrap\">Two Body Excess Entropy, <i>s</i><sup>(2)</sup>/<i>k</i><sub>B</i></p></html>" } };
	private JButton export;

	/**
	 * 
	 */
	public ThermodynamicsTab(DynamicInfo di, FluidApp pa) {
		systems = di;
		parent = pa;
		setName("Thermodynamics");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowHeights = new int[] { 120, 20, 0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, };
		setLayout(gridBagLayout);
		String[] columns = { "Systems", "Show Properties" };
		Object[][] tableInfo = {};
		mod = new SystemsModel(tableInfo, columns);
		mod.refreshTable();

		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridx = 0;
		gbc_label.gridy = 1;
		gbc_label.anchor = GridBagConstraints.WEST;
		JLabel label = new JLabel("Thermodynamic Properties");
		add(label, gbc_label);

		GridBagConstraints gbc_export = new GridBagConstraints();
		gbc_export.gridx = 1;
		gbc_export.gridy = 1;
		gbc_export.anchor = GridBagConstraints.EAST;
		export = new JButton("Export");
		add(export, gbc_export);

		table = new JTable(mod);
		table.getTableHeader().setReorderingAllowed(false);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		gbc_scrollPane_1.gridwidth = 2;
		scrollPane_1 = new JScrollPane(table);
		add(scrollPane_1, gbc_scrollPane_1);

		gbc_scroll2 = new GridBagConstraints();
		gbc_scroll2.fill = GridBagConstraints.BOTH;
		gbc_scroll2.gridx = 0;
		gbc_scroll2.gridy = 2;
		gbc_scroll2.gridwidth = 2;
		scroll = new JScrollPane();
		add(scroll, gbc_scroll2);
		createListeners();
	}

	public void updateInfo() {
		mod.refreshTable();
		DefaultTableModel model = new DefaultTableModel(header,
				new String[] { "Properties" }) {

			private static final long serialVersionUID = 4783443266564436271L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};
		for (Data d : systems.systems) {
			Object[] arr = new Object[4];
			if (d.thermo) {
				arr[0] = d.internEnergy;
				arr[1] = d.configEnergy;
				arr[2] = d.compress;
//				arr[3] = d.isothermCompress;
				arr[3] = d.twoBodyEntropy;
				model.addColumn(d.name, arr);
			}
		}
		information = new JTable(model) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2539353368400302331L;
			decimalFormat df = new decimalFormat();
			TableCellRenderer def = new DefaultTableCellRenderer();

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if (column != 0)
					return df;
				else
					return def;
			}
		};
		information.setRowHeight(information.getRowHeight()*2);
		information.getTableHeader().setReorderingAllowed(false);
		remove(scroll);
		scroll = new JScrollPane(information);
		add(scroll, gbc_scroll2);
		ThermodynamicsTab.this.revalidate();
		ThermodynamicsTab.this.repaint();
	}

	class SystemsModel extends DefaultTableModel {

		private static final long serialVersionUID = -6603151260509369309L;

		public SystemsModel(Object[][] tableInfo, String[] columns) {
			super(tableInfo, columns);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex != 0)
				return Boolean.class;
			return super.getColumnClass(columnIndex);
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			return (col != 0);
		}

		public void refreshTable() {
			setRowCount(0);
			for (Data d : systems.systems) {
				Object[] arr = new Object[2];
				arr[0] = d.name;
				arr[1] = new Boolean(d.thermo);
				addRow(arr);
			}
		}

	}

	private void createListeners() {

		TableModelListener tml = new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent event) {
				if (event.getType() != TableModelEvent.UPDATE)
					return;
				int col = event.getColumn();
				int row = event.getLastRow();
				if (row == -1 || col == -1)
					return;
				Boolean value = (Boolean) mod.getValueAt(row, col);
				Data temp = systems.systems.get(row);
				temp.thermo = value;
				updateInfo();
			}
		};

		mod.addTableModelListener(tml);

		export.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jf = new JFileChooser();
				int n = jf.showSaveDialog(getRootPane());
				if (n == JFileChooser.APPROVE_OPTION) {
					try (BufferedWriter bw = new BufferedWriter(new FileWriter(
							jf.getSelectedFile()))) {
						StringBuilder sb = new StringBuilder();
						int i = information.getColumnCount();
						int j = information.getRowCount();
						for (int ii = 0; ii < i; ii++) {
							sb.append(information.getColumnName(ii) + "\t");
						}
						sb.append('\n');
						for (int jj = 0; jj < j; jj++) {
							for (int ii = 0; ii < i; ii++) {
								sb.append(String.format("%6s\t",
										information.getValueAt(jj, ii)));
							}
							sb.append('\n');
						}
						bw.write(sb.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		});
	}

	static class decimalFormat extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 245999243623317359L;
		NumberFormat nf = NumberFormat.getInstance();

		@Override
		public void setValue(Object value) {
			nf.setMaximumFractionDigits(5);
			setText((value == null) ? "" : nf.format(value));
		}
	}

}
