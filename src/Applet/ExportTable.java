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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import calculations.Data;

public class ExportTable extends JPanel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 873100138278353002L;
	private JComboBox<String> comboBox;
	private JButton btnExport;
	private DynamicInfo dats;
	private HashMap<String, JTable> tables = new HashMap<String, JTable>();
	private JScrollPane scrollPane;
	private GridBagConstraints gbc_scrollPane;
	FluidApp parent;
	JTable tabel;

	public ExportTable(DynamicInfo dats, FluidApp fluidApp) {
		parent = fluidApp;
		this.dats = dats;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.5, 0.5, 0.5 };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0 };
		setLayout(gridBagLayout);

		/*
		 * Data table and button
		 */
		JLabel lblDataLabels = new JLabel("Data");
		GridBagConstraints gbc_lblDataLabels = new GridBagConstraints();
		gbc_lblDataLabels.anchor = GridBagConstraints.WEST;
		gbc_lblDataLabels.insets = new Insets(0, 3, 5, 5);
		gbc_lblDataLabels.gridx = 0;
		gbc_lblDataLabels.gridy = 0;
		add(lblDataLabels, gbc_lblDataLabels);

		comboBox = new JComboBox<String>();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		add(comboBox, gbc_comboBox);

		btnExport = new JButton("Export");
		GridBagConstraints gbc_btnExport = new GridBagConstraints();
		gbc_btnExport.anchor = GridBagConstraints.EAST;
		gbc_btnExport.insets = new Insets(0, 0, 5, 0);
		gbc_btnExport.gridx = 2;
		gbc_btnExport.gridy = 0;
		add(btnExport, gbc_btnExport);

		gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.weighty = 0.0;
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		scrollPane = new JScrollPane();
		add(scrollPane, gbc_scrollPane);
		/*
		 * End data table
		 */
		updateChoices();
		createListeners();
	}

	private void readObject(ObjectInputStream in) throws Exception {
		in.defaultReadObject();
		createListeners();
		updateChoices();
	}

	public void updateChoices() {
		DefaultTableModel m;
		tables.clear();
		for (ItemListener a : comboBox.getItemListeners())
			comboBox.removeItemListener(a);
		comboBox.removeAllItems();
		List<Double[]> arr = new LinkedList<Double[]>();
		List<String> compareNames = new LinkedList<String>();
		boolean first = true;
		for (Data d : dats.systems) {
			String[] headers = { "r", "continuous potential",
					"discrete potential" };
			String[] headers2 = { "r", "smooth g(r)", "sawtoothed g(r)" };
			Double[][] vals = new Double[d.r.length][3];
			Double[][] vals2 = new Double[d.r.length][3];
			for (int j = 0; j < d.r.length; j++) {
				vals[j][0] = d.r[j];
				vals[j][1] = d.cont_v[j];
				vals[j][2] = d.disc_v[j];
			}
			m = new DefaultTableModelExtension(vals, headers);
			
			JTable table = new JTable(m) {
				private static final long serialVersionUID = -2170582283224273188L;
				decimalFormat df = new decimalFormat();
				
				@Override
				public TableCellRenderer getCellRenderer(int row, int column) {
					return df;
				}
			};
			String choice = d.name + " potentials";
			table.getTableHeader().setReorderingAllowed(false);
			tables.put(choice, table);
			if (d.gofr.length != 0) {
				for (int j = 0; j < d.r.length; j++) {
					vals2[j][0] = d.r[j];
					vals2[j][1] = d.gofr[j];
					vals2[j][2] = d.rough_gofr[j];
				}
				choice = d.name + " RDF";
				m = new DefaultTableModelExtension(vals2, headers2);
				table = new JTable(m) {
					/**
					 * 
					 */
					private static final long serialVersionUID = -989367801934338962L;
					decimalFormat df = new decimalFormat();

					@Override
					public TableCellRenderer getCellRenderer(int row, int column) {
						return df;
					}
				};
				table.getTableHeader().setReorderingAllowed(false);
				tables.put(choice, table);
			}

			if (d.show[0]) {
				if (first) {
					first = false;
					compareNames.add("r");
					Double[] temp = new Double[d.r.length];
					for (int ii = 0; ii < d.r.length; ii++) {
						temp[ii] = d.r[ii];
					}
					arr.add(temp);
				}
				compareNames.add(d.name + " sawtoothed");
				Double[] temp = new Double[d.r.length];
				for (int j = 0; j < d.r.length; j++) {
					temp[j] = d.rough_gofr[j];
				}
				arr.add(temp);
			}
			if (d.show[1]) {
				if (first) {
					first = false;
					compareNames.add("r");
					Double[] temp = new Double[d.r.length];
					for (int ii = 0; ii < d.r.length; ii++) {
						temp[ii] = d.r[ii];
					}
					arr.add(temp);
				}
				compareNames.add(d.name + " Smooth");
				Double[] temp = new Double[d.r.length];
				for (int j = 0; j < d.r.length; j++) {
					temp[j] = d.gofr[j];
				}
				arr.add(temp);
			}
		}
		Double[][] values = new Double[arr.size()][];
		JTable table;
		Double[][] actual = new Double[0][0];
		for (int j = 0; j < values.length; j++)
			values[j] = arr.get(j);
		if (values.length != 0) {
			actual = new Double[values[0].length][values.length];
			for (int j = 0; j < values.length; j++)
				for (int ii = 0; ii < values[0].length; ii++)
					actual[ii][j] = values[j][ii];
			m = new DefaultTableModelExtension(actual, compareNames.toArray(new String[0]));
			table = new JTable(m) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 2539353368400302331L;
				decimalFormat df = new decimalFormat();

				@Override
				public TableCellRenderer getCellRenderer(int row, int column) {
					return df;
				}
			};
		} else {
			m = new DefaultTableModelExtension(values, compareNames.toArray(new String[0]));
			table = new JTable(m) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 4465088859684036452L;
				decimalFormat df = new decimalFormat();

				@Override
				public TableCellRenderer getCellRenderer(int row, int column) {
					return df;
				}
			};
		}
		table.getTableHeader().setReorderingAllowed(false);
		tables.put("Comparisons", table);
		for (String s : tables.keySet())
			comboBox.addItem(s);

		comboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				ExportTable.this.remove(scrollPane);
				String choice = (String) comboBox.getSelectedItem();
				tabel = tables.get(choice);
				scrollPane = new JScrollPane(tabel,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				ExportTable.this.add(scrollPane, gbc_scrollPane);
				parent.newStuff();
			}
		});

		ExportTable.this.remove(scrollPane);
		String choice = (String) comboBox.getSelectedItem();
		tabel = tables.get(choice);
		scrollPane = new JScrollPane(tabel,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ExportTable.this.add(scrollPane, gbc_scrollPane);
	}

	private void createListeners() {
		btnExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JTable table = tabel;
				JFileChooser jf = new JFileChooser();
				int n = jf.showSaveDialog(getRootPane());
				if (n == JFileChooser.APPROVE_OPTION) {
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(
								jf.getSelectedFile()));
						StringBuilder sb = new StringBuilder("#");
						int i = table.getColumnCount();
						int j = table.getRowCount();
						for (int ii = 0; ii < i; ii++) {
							sb.append(table.getColumnName(ii) + "\t");
						}
						sb.append('\n');
						for (int jj = 0; jj < j; jj++) {
							for (int ii = 0; ii < i; ii++) {
									sb.append(String.format("%6e\t",
										table.getValueAt(jj, ii)));
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

	private final class DefaultTableModelExtension extends DefaultTableModel {
		private static final long serialVersionUID = -3217427656490695027L;

		private DefaultTableModelExtension(Object[][] data, Object[] columnNames) {
			super(data, columnNames);
		}

		@Override
		public boolean isCellEditable(int row, int column){
			return false;
		}
	}

	static class decimalFormat extends DefaultTableCellRenderer {
		NumberFormat nf = NumberFormat.getInstance();

		/**
		 * 
		 */
		private static final long serialVersionUID = -3992614163245237303L;

		@Override
		public void setValue(Object value) {
			nf.setMaximumFractionDigits(6);
			setText((value == null) ? "" : nf.format(value));
		}
	}
	
}
