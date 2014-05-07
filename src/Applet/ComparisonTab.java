/**
 * 
 */
package Applet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import calculations.Data;

/**
 * @author Tres
 * 
 */
public class ComparisonTab extends JPanel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1009135792118035866L;
	private JTable table;
	private FluidApp parent;
	private JScrollPane scrollPane_1;
	private DynamicInfo systems;
	private systemsModel mod;
	private EmbeddedChart panel;
	private GridBagConstraints gbc_panel;
	private JButton newWindowButton;
	private JButton btnNewButton;

	/**
	 * 
	 */
	public ComparisonTab(FluidApp fluidApp, DynamicInfo dats, JFileChooser jf) {
		parent = fluidApp;
		systems = dats;
		setName("Structure");
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 195, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 60, 0, 95 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, };
		setLayout(gridBagLayout);

		/*
		 * Selection table
		 */
		JLabel lblNewLabel = new JLabel("Show Data For...");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 3;
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 2, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		String[] columns = { "", "System 1" };
		Object[][] tableInfo = { { "Sawtoothed", new Boolean(false) },
				{ "Smoothed", new Boolean(false) } };
		mod = new systemsModel(tableInfo, columns);
		mod.refreshTable();

		table = new JTable(mod);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 3;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 1;
		scrollPane_1 = new JScrollPane(table);
		add(scrollPane_1, gbc_scrollPane_1);
		/*
		 * End selection table
		 */

		/*
		 * Chart
		 */
		JLabel lblRadialDistributionFunctions = new JLabel(
				"Radial Distribution Function(s)");
		GridBagConstraints gbc_lblRadialDistributionFunctions = new GridBagConstraints();
		gbc_lblRadialDistributionFunctions.gridwidth = 2;
		gbc_lblRadialDistributionFunctions.anchor = GridBagConstraints.WEST;
		gbc_lblRadialDistributionFunctions.insets = new Insets(0, 2, 5, 5);
		gbc_lblRadialDistributionFunctions.gridx = 0;
		gbc_lblRadialDistributionFunctions.gridy = 2;
		add(lblRadialDistributionFunctions, gbc_lblRadialDistributionFunctions);

		newWindowButton = new JButton("Popout");
		GridBagConstraints gbc_btnNWButton = new GridBagConstraints();
		gbc_btnNWButton.anchor = GridBagConstraints.EAST;
		gbc_btnNWButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNWButton.gridx = 2;
		gbc_btnNWButton.gridy = 2;
		add(newWindowButton, gbc_btnNWButton);

		String[] names = {};
		ArrayList<double[][]> RYVals = new ArrayList<>();
		panel = new EmbeddedChart("RDF, g(r)", names, RYVals, true);
		gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 0.75;
		gbc_panel.gridwidth = 3;
		gbc_panel.insets = new Insets(0, 0, 0, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		add(panel.getContentPane(), gbc_panel);
		/*
		 * End chart
		 */

		createListeners();

	}

	public void systemUpdate() {
		mod.refreshTable();
		refreshChart();
	}

	public void refreshChart() {
		List<String> leg = new LinkedList<>();
		List<double[][]> vals = new LinkedList<>();
		for (Data d : systems.systems) {
			if (d.show[0]) {
				leg.add(d.name + " sawtoothed");
				double[][] arr = new double[2][d.r.length];
				arr[0] = d.r;
				arr[1] = d.rough_gofr;
				vals.add(arr);
			}
			if (d.show[1]) {
				leg.add(d.name + " smooth");
				double[][] arr = new double[2][d.r.length];
				arr[0] = d.r;
				arr[1] = d.gofr;
				vals.add(arr);
			}
		}
		String[] legends = leg.toArray(new String[0]);
		remove(panel.getContentPane());
		panel = new EmbeddedChart("RDF g(r)", legends, vals, true);
		add(panel.getContentPane(), gbc_panel);
		ComparisonTab.this.revalidate();
		ComparisonTab.this.repaint();
		parent.revalidate();
		parent.repaint();
	}

	class systemsModel extends DefaultTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 162719221504223808L;

		public systemsModel(Object[][] tableInfo, String[] columns) {
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
			setColumnCount(1);
			for (Data d : systems.systems) {
				Boolean[] arr = new Boolean[2];
				arr[0] = d.show[0];
				arr[1] = d.show[1];
				addColumn(d.name, arr);
			}
		}

	};

	private void readObject(ObjectInputStream in) throws Exception {
		in.defaultReadObject();
		createListeners();

	}

	private void createListeners() {
		newWindowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setTitle(panel.title);
				frame.add(panel.copy().getContentPane());
				frame.setSize(400, 300);
				frame.setVisible(true);
			}
		});

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
				Data temp = systems.systems.get(col-1);
				if(temp.gofr.length <  temp.r.length){
					System.out.println(temp.r.length);
					System.out.println(temp.gofr.length);
					mod.removeTableModelListener(this);
					mod.setValueAt(new Boolean(false), row, col);
					mod.addTableModelListener(this);
				}
				else
					temp.show[row] = value;
				refreshChart();
				parent.updateSystems();
			}
		};

		
		mod.addTableModelListener(tml);
	}
	
}
