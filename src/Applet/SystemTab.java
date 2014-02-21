/**
 * 
 */
package Applet;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import calculations.Data;
import calculations.DiscretePotential;
import calculations.SmoothGofr;
import calculations.TlReadParams;

/**
 * @author Tres
 * 
 */
public class SystemTab extends JPanel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8388558360632737053L;
	private JTextField txtSystem;
	private JTable potentialsTable;
	private JTextField packingField;
	private JTextField numDensityField;
	private JTextField epsMaxField;
	private JTextField numRField;
	private JTextField deltaRField;
	private JTextField rMaxField;
	private double packingFraction = 0.4, numberDensity = packingFraction * 6
			/ Math.PI, epsMax = 0.1, deltaR = 0.01, maxR = 10.0;
	private int numR = 1_000;

	private SystemTab me;
	protected FluidApp parent;
	private int i;
	protected Data dat;
	private EmbeddedChart chartFrame;
	private String[] names = new String[] {
			Messages.getString("SystemTab.ContinuousLegend"),
			Messages.getString("SystemTab.terracedLegend") };
	private GridBagConstraints chartConstraint;
	private List<Double> listr = new ArrayList<Double>();
	private List<Double> listv = new ArrayList<Double>();
	private JButton numRHint;
	private JButton deltaRHint;
	private JButton rMaxHint;
	private JButton newWindowButton;
	private JButton btnRestoreButton;
	private JButton rdfButton;
	private JButton btnLoad;
	private JButton terraceButton;
	private JButton deltaEpsHint;
	private JButton setNameButton;
	private DefaultTableModel tableModel;
	private SerialListener tableListener;
	private String positiveWarning = Messages
			.getString("SystemTab.positiveParameters");

	/**
	 * 
	 */
	public SystemTab(final int i, final FluidApp parent, final JFileChooser fc,
			Data data) {
		dat = data;
		dat.max_deps = epsMax;
		me = this;
		this.parent = parent;
		this.i = i;
		setName("System " + (i + 1)); 

		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 109, 28, 0, 31, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
		gbl_panel.columnWeights = new double[] { 1.0, 0.0, 1.0, 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 1.0 };
		setLayout(gbl_panel);

		/*
		 * Nickname
		 */
		JLabel lblNewLabel = new JLabel("Nickname"); 
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		setNameButton = new JButton("Set"); 
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.EAST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 0;
		add(setNameButton, gbc_btnNewButton);

		txtSystem = new JTextField();
		txtSystem.setText("System " + (i + 1)); 
		dat.name = "System " + (i + 1); 
		GridBagConstraints gbc_txtSystem = new GridBagConstraints();
		gbc_txtSystem.insets = new Insets(0, 0, 5, 5);
		gbc_txtSystem.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSystem.gridx = 0;
		gbc_txtSystem.gridy = 1;
		add(txtSystem, gbc_txtSystem);
		txtSystem.setColumns(10);
		/*
		 * End Nickname
		 */

		/*
		 * Table
		 */
		JLabel lblNewLabel_1 = new JLabel("Pair Potential"); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 3;
		gbc_lblNewLabel_1.gridy = 0;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		String[] headers = {
				Messages.getString("SystemTab.distanceHeader"), Messages.getString("SystemTab.potentialHeader") }; //$NON-NLS-1$ //$NON-NLS-2$
		Object[][] vals = {};
		tableModel = new DefaultTableModel(vals, headers) {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8683053549165388483L;

			@Override
			public boolean isCellEditable(int arg0, int arg1) {
				return (arg1 != 2);
			}
		};
		tableListener = new SerialListener();
		btnLoad = new JButton("Load"); //$NON-NLS-1$
		GridBagConstraints gbc_btnLoad = new GridBagConstraints();
		gbc_btnLoad.anchor = GridBagConstraints.EAST;
		gbc_btnLoad.insets = new Insets(0, 0, 5, 0);
		gbc_btnLoad.gridx = 5;
		gbc_btnLoad.gridy = 0;
		add(btnLoad, gbc_btnLoad);

		tableModel.setColumnCount(2);
		potentialsTable = new JTable(tableModel);
		potentialsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		potentialsTable.setRowSelectionAllowed(false);
		GridBagConstraints gbc_table_1 = new GridBagConstraints();
		gbc_table_1.gridheight = 8;
		gbc_table_1.gridwidth = 3;
		gbc_table_1.insets = new Insets(0, 0, 5, 0);
		gbc_table_1.fill = GridBagConstraints.BOTH;
		gbc_table_1.gridx = 3;
		gbc_table_1.gridy = 1;
		tableModel.addRow(new Object[] {});
		JScrollPane tableContainer = new JScrollPane(potentialsTable);
		tableContainer.setPreferredSize(new Dimension(453, 128));
		add(tableContainer, gbc_table_1);
		/*
		 * End table
		 */

		/*
		 * Packing Fraction
		 */
		JLabel lblNewLabel_3 = new JLabel("Packing Fraction"); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 2;
		add(lblNewLabel_3, gbc_lblNewLabel_3);

		packingField = new JTextField();
		packingField.setText("" + packingFraction); //$NON-NLS-1$
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 2;
		add(packingField, gbc_textField);
		packingField.setColumns(10);
		/*
		 * End Packing fraction
		 */

		/*
		 * Number Density
		 */
		JLabel lblNumberDensity = new JLabel("Number Density"); //$NON-NLS-1$
		GridBagConstraints gbc_lblNumberDensity = new GridBagConstraints();
		gbc_lblNumberDensity.anchor = GridBagConstraints.WEST;
		gbc_lblNumberDensity.insets = new Insets(0, 0, 5, 5);
		gbc_lblNumberDensity.gridx = 0;
		gbc_lblNumberDensity.gridy = 3;
		add(lblNumberDensity, gbc_lblNumberDensity);

		numDensityField = new JTextField();
		numDensityField.setText(String.format("%.4f", numberDensity)); //$NON-NLS-1$
		numDensityField.setColumns(10);
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 3;
		add(numDensityField, gbc_textField_1);
		/*
		 * End Number Density
		 */

		/*
		 * Delta Eps Max
		 */
		JLabel lblDeltaEpsMax = new JLabel("Delta eps max"); //$NON-NLS-1$
		GridBagConstraints gbc_lblDeltaEpsMax = new GridBagConstraints();
		gbc_lblDeltaEpsMax.anchor = GridBagConstraints.WEST;
		gbc_lblDeltaEpsMax.insets = new Insets(0, 0, 5, 5);
		gbc_lblDeltaEpsMax.gridx = 0;
		gbc_lblDeltaEpsMax.gridy = 4;
		add(lblDeltaEpsMax, gbc_lblDeltaEpsMax);

		deltaEpsHint = new JButton("?"); //$NON-NLS-1$
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 5);
		gbc_button.gridx = 1;
		gbc_button.gridy = 4;
		add(deltaEpsHint, gbc_button);

		epsMaxField = new JTextField();
		epsMaxField.setText("" + epsMax); //$NON-NLS-1$
		epsMaxField.setColumns(10);
		GridBagConstraints gbc_textField_2 = new GridBagConstraints();
		gbc_textField_2.insets = new Insets(0, 0, 5, 5);
		gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_2.gridx = 2;
		gbc_textField_2.gridy = 4;
		add(epsMaxField, gbc_textField_2);
		/*
		 * End Delta Eps max
		 */

		/*
		 * Number of r points
		 */
		JLabel lblNewLabel_5 = new JLabel("# of r points"); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 5;
		add(lblNewLabel_5, gbc_lblNewLabel_5);

		numRHint = new JButton("?"); //$NON-NLS-1$
		GridBagConstraints gbc_button_2 = new GridBagConstraints();
		gbc_button_2.insets = new Insets(0, 0, 5, 5);
		gbc_button_2.gridx = 1;
		gbc_button_2.gridy = 5;
		add(numRHint, gbc_button_2);

		numRField = new JTextField();
		numRField.setText("" + numR); //$NON-NLS-1$
		numRField.setColumns(10);
		GridBagConstraints gbc_textField_4 = new GridBagConstraints();
		gbc_textField_4.insets = new Insets(0, 0, 5, 5);
		gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_4.gridx = 2;
		gbc_textField_4.gridy = 5;
		add(numRField, gbc_textField_4);
		/*
		 * End number of r points
		 */

		/*
		 * Delta r
		 */
		JLabel lblDeltaR = new JLabel("delta r"); //$NON-NLS-1$
		GridBagConstraints gbc_lblDeltaR = new GridBagConstraints();
		gbc_lblDeltaR.anchor = GridBagConstraints.WEST;
		gbc_lblDeltaR.insets = new Insets(0, 0, 5, 5);
		gbc_lblDeltaR.gridx = 0;
		gbc_lblDeltaR.gridy = 6;
		add(lblDeltaR, gbc_lblDeltaR);

		deltaRHint = new JButton("?"); //$NON-NLS-1$
		GridBagConstraints gbc_button_3 = new GridBagConstraints();
		gbc_button_3.insets = new Insets(0, 0, 5, 5);
		gbc_button_3.gridx = 1;
		gbc_button_3.gridy = 6;
		add(deltaRHint, gbc_button_3);

		deltaRField = new JTextField();
		deltaRField.setText("" + deltaR); //$NON-NLS-1$
		deltaRField.setColumns(10);
		GridBagConstraints gbc_textField_5 = new GridBagConstraints();
		gbc_textField_5.insets = new Insets(0, 0, 5, 5);
		gbc_textField_5.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_5.gridx = 2;
		gbc_textField_5.gridy = 6;
		add(deltaRField, gbc_textField_5);
		/*
		 * End delta r
		 */

		/*
		 * R max
		 */
		JLabel lblNewLabel_6 = new JLabel("r max"); //$NON-NLS-1$
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 7;
		add(lblNewLabel_6, gbc_lblNewLabel_6);

		rMaxHint = new JButton("?"); //$NON-NLS-1$
		GridBagConstraints gbc_button_4 = new GridBagConstraints();
		gbc_button_4.insets = new Insets(0, 0, 5, 5);
		gbc_button_4.gridx = 1;
		gbc_button_4.gridy = 7;
		add(rMaxHint, gbc_button_4);

		rMaxField = new JTextField();
		rMaxField.setText("" + maxR); //$NON-NLS-1$
		rMaxField.setColumns(10);
		GridBagConstraints gbc_textField_6 = new GridBagConstraints();
		gbc_textField_6.insets = new Insets(0, 0, 5, 5);
		gbc_textField_6.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_6.gridx = 2;
		gbc_textField_6.gridy = 7;
		add(rMaxField, gbc_textField_6);
		/*
		 * End r max
		 */

		/*
		 * Calculate Buttons
		 */

		newWindowButton = new JButton("Popout"); //$NON-NLS-1$
		GridBagConstraints gbc_btnNW = new GridBagConstraints();
		gbc_btnNW.insets = new Insets(0, 0, 5, 5);
		gbc_btnNW.gridx = 5;
		gbc_btnNW.gridy = 9;
		gbc_btnNW.anchor = GridBagConstraints.EAST;
		add(newWindowButton, gbc_btnNW);

		btnRestoreButton = new JButton("Restore"); //$NON-NLS-1$
		GridBagConstraints gbcRNewButton = new GridBagConstraints();
		gbcRNewButton.anchor = GridBagConstraints.EAST;
		gbcRNewButton.insets = new Insets(0, 0, 5, 0);
		gbcRNewButton.gridx = 4;
		gbcRNewButton.gridy = 9;
		add(btnRestoreButton, gbcRNewButton);

		terraceButton = new JButton("Terrace u(r)");
		GridBagConstraints gbc_btnT = new GridBagConstraints();
		gbc_btnT.insets = new Insets(0, 0, 5, 5);
		gbc_btnT.gridx = 0;
		gbc_btnT.gridy = 8;
		gbc_btnT.anchor = GridBagConstraints.WEST;
		add(terraceButton, gbc_btnT);

		rdfButton = new JButton("Calc RDF");
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.gridwidth = 2;
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 8;
		gbc_btnNewButton_1.anchor = GridBagConstraints.EAST;
		add(rdfButton, gbc_btnNewButton_1);

		/*
		 * End calculate buttons
		 */

		/*
		 * Chart
		 */
		JLabel chartTitle = new JLabel("Pair Potential u(r)");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_7.gridx = 0;
		gbc_lblNewLabel_7.gridy = 9;
		add(chartTitle, gbc_lblNewLabel_7);
		ArrayList<double[][]> RYVals = new ArrayList<>();
		double[][] disc = { dat.r, dat.disc_v };
		double[][] cont = { dat.r, dat.cont_v };
		RYVals.add(cont);
		RYVals.add(disc);

		chartFrame = new EmbeddedChart("Pair Potential, u(r)", names, RYVals, //$NON-NLS-1$
				false);
		chartConstraint = new GridBagConstraints();
		chartConstraint.weighty = 1.0;
		chartConstraint.gridwidth = 6;
		chartConstraint.fill = GridBagConstraints.BOTH;
		chartConstraint.gridx = 0;
		chartConstraint.gridy = 10;
		add(chartFrame.getContentPane(), chartConstraint);
		/*
		 * End chart
		 */

		createListeners();
	}

	public void tableUpdate() {
		try {
			me.remove(chartFrame.getContentPane());
			ArrayList<double[][]> RYVals = new ArrayList<>();
			double[] disc_arr = new double[dat.disc_v.length * 2 - 1];
			double[] disc_r = new double[dat.disc_v.length * 2 - 1];
			int ii;
			for (ii = 0; ii < disc_arr.length; ii++) {
				disc_arr[ii] = dat.disc_v[(ii + 1) / 2];
				disc_r[ii] = dat.r[ii / 2];
			}
			double[][] disc = { disc_r, disc_arr };
			double[][] cont = { dat.r, dat.cont_v };
			RYVals.add(cont);
			RYVals.add(disc);
			chartFrame = new EmbeddedChart("Pair Potential, u(r)", names,
					RYVals, false);
			me.add(chartFrame.getContentPane(), chartConstraint);
			parent.somethingChanged();
		} catch (Exception E) {
			ArrayList<double[][]> RYVals = new ArrayList<>();
			double[] disc_r = {};
			double[] disc_arr = {};
			double[][] disc = { disc_r, disc_arr };
			double[][] cont = { dat.r, dat.cont_v };
			RYVals.add(cont);
			RYVals.add(disc);
			chartFrame = new EmbeddedChart("Pair Potential, u(r)", names,
					RYVals, false);
			me.add(chartFrame.getContentPane(), chartConstraint);
			parent.somethingChanged();
		}
	}

	class DiscreteThread extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			List<Double> radii = new LinkedList<>();
			List<Double> potentials = new LinkedList<>();
			PriorityQueue<Pair> pq = new PriorityQueue<>();
			for (int i = 0; i < listr.size(); i++) {
				pq.add(new Pair(listr.get(i), listv.get(i)));
			}
			if (pq.peek().radius > 1) {
				Pair p1 = pq.poll();
				Pair p2 = pq.poll();
				double slope = (p2.potential - p1.potential)
						/ (p2.radius - p1.radius);
				double intercept = p2.potential - slope * p2.radius;
				double v = slope + intercept;
				pq.add(new Pair(1.0, v));
			}
			while (!pq.isEmpty()) {
				Pair p = pq.poll();
				radii.add(p.radius);
				potentials.add(p.potential);
			}
			int size = potentials.size() - 1;
			if (potentials.get(size) > 0) {
				double r1 = radii.get(size - 1);
				double r2 = radii.get(size);
				double v1 = potentials.get(size - 1);
				double v2 = potentials.get(size);
				double slope = (v2 - v1) / (r2 - r1);
				double intercept = v2 - slope * r2;
				double r = -intercept / slope;
				radii.add(r);
				potentials.add(0.0);
				size++;
			}
			size++;
			dat.given_r = new double[size];
			dat.given_v = new double[size];
			for (int i = 0; i < size; i++) {
				dat.given_r[i] = radii.get(i);
				dat.given_v[i] = potentials.get(i);
			}
			new DiscretePotential().discretePotential(dat, numR, deltaR);
			return null;
		}

		@Override
		protected void done() {
			tableUpdate();
		}
	}

	private void readObject(ObjectInputStream in) throws Exception {
		in.defaultReadObject();
		createListeners();
	}

	private void createListeners() {
		final JFileChooser jf = new JFileChooser();
		tableModel.addTableModelListener(tableListener);
		setNameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				me.setName(txtSystem.getText());
				dat.name = me.getName();
				parent.changeName(me, i);

			}
		});
		packingField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				double n = 0.0;
				try {
					n = Double.parseDouble(packingField.getText());
					if (n <= 0) {
						JOptionPane.showMessageDialog(getRootPane(),
								positiveWarning);
						packingField.setText("" + packingFraction);
						return;
					}
					if (n >= 0.74) {
						JOptionPane.showMessageDialog(getRootPane(),
								"Packing fraction should be less than 0.74");
						packingField.setText("" + packingFraction);
						return;
					}
				} catch (Exception e) {

				}
				packingFraction = n;
				numberDensity = n * 6.0 / Math.PI;
				numDensityField.setText(String.format("%.4f", numberDensity));

			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		numDensityField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				double n = 0.0;
				try {
					n = Double.parseDouble(numDensityField.getText());
					if (n <= 0) {
						JOptionPane.showMessageDialog(getRootPane(),
								positiveWarning);
						numDensityField.setText("" + numberDensity);
						return;
					}
				} catch (Exception e) {

				}
				numberDensity = n;
				packingFraction = n / 6.0 * Math.PI;
				packingField.setText(String.format("%.4f", packingFraction));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		deltaEpsHint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getRootPane(),
						Messages.getString("SystemTab.deltaEpsHint"));

			}
		});
		numRHint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getRootPane(),
						Messages.getString("SystemTab.numRHint"));

			}
		});
		numRField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				int n = 0;
				try {
					n = Integer.parseInt(numRField.getText());
					if (n <= 0) {
						JOptionPane.showMessageDialog(getRootPane(),
								positiveWarning);
						numRField.setText("" + numR);
						return;
					}
				} catch (Exception e) {

				}
				numR = n;
				deltaR = maxR / numR;
				deltaRField.setText(String.format("%.4f", deltaR));

			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		deltaRHint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getRootPane(),
						Messages.getString("SystemTab.deltaRHint"));

			}
		});
		deltaRField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				double n = 0;
				try {
					n = Double.parseDouble(deltaRField.getText());
					if (n <= 0) {
						JOptionPane.showMessageDialog(getRootPane(),
								positiveWarning);
						deltaRField.setText("" + deltaR);
						return;
					}
				} catch (Exception e) {

				}
				deltaR = n;
				numR = (int) (maxR / n);
				numRField.setText(String.format("%d", numR));
				deltaR = maxR / numR;
				deltaRField.setText(String.format("%.4f", deltaR));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		rMaxHint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getRootPane(),
						Messages.getString("SystemTab.maxRHint"));

			}
		});
		rMaxField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				double n = 0;
				try {
					n = Double.parseDouble(rMaxField.getText());
					if (n <= 0) {
						JOptionPane.showMessageDialog(getRootPane(),
								positiveWarning);
						rMaxField.setText("" + maxR);
						return;
					}
				} catch (Exception e) {

				}
				maxR = n;
				deltaR = maxR / numR;
				deltaRField.setText(String.format("%.4f", deltaR)); 

			}

			@Override
			public void focusGained(FocusEvent arg0) {
			}
		});
		newWindowButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame();
				frame.setTitle(chartFrame.title);
				frame.add(chartFrame.copy().getContentPane());
				frame.setSize(400, 300);
				frame.setVisible(true);
			}
		});
		btnRestoreButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				chartFrame.restore();

			}
		});
		terraceButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new DiscreteThread().execute();
			}
		});
		rdfButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dat.packingFraction = packingFraction;
				SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
					boolean flag = true;

					@Override
					protected Void doInBackground() throws Exception {
						rdfButton.setEnabled(false);
						List<Double> radii = new LinkedList<>();
						List<Double> potentials = new LinkedList<>();
						PriorityQueue<Pair> pq = new PriorityQueue<>();
						for (int i = 0; i < listr.size(); i++) {
							pq.add(new Pair(listr.get(i), listv.get(i)));
						}
						if (Math.abs(pq.peek().radius - 1.0) > 0.0001) {
							int abc = JOptionPane.showOptionDialog(
									getRootPane(),
									Messages.getString("SystemTab.rLessThan"), //$NON-NLS-1$
									"R > 1", JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
									JOptionPane.WARNING_MESSAGE, null, null,
									null);
							if (abc == JOptionPane.NO_OPTION) {
								flag = false;
								return null;
							}
							Pair p1 = pq.poll();
							Pair p2 = pq.poll();
							double slope = (p2.potential - p1.potential)
									/ (p2.radius - p1.radius);
							double intercept = p2.potential - slope * p2.radius;
							double v = slope + intercept;
							pq.add(new Pair(1.0, v));
						}
						while (!pq.isEmpty()) {
							Pair p = pq.poll();
							radii.add(p.radius);
							potentials.add(p.potential);
						}
						int size = potentials.size() - 1;
						if (Math.abs(radii.get(size) - 2.0) > 0.001) {
							int abc = JOptionPane.showOptionDialog(
									getRootPane(),
									Messages.getString("SystemTab.rGreaterThan"), //$NON-NLS-1$
									"R != 2", JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
									JOptionPane.WARNING_MESSAGE, null, null,
									null);
							if (abc == JOptionPane.NO_OPTION) {
								flag = false;
								return null;
							}
						}
						if (Math.abs(potentials.get(size)) > 0.0001) {
							int abc = JOptionPane.showOptionDialog(
									getRootPane(),
									Messages.getString("SystemTab.uGreaterThan"), //$NON-NLS-1$
									"U != 0", JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
									JOptionPane.WARNING_MESSAGE, null, null,
									null);
							if (abc == JOptionPane.NO_OPTION) {
								flag = false;
								return null;
							}
							double r1 = radii.get(size - 1);
							double r2 = radii.get(size);
							double v1 = potentials.get(size - 1);
							double v2 = potentials.get(size);
							double slope = (v2 - v1) / (r2 - r1);
							double intercept = v2 - slope * r2;
							double r = -intercept / slope;
							radii.add(r);
							potentials.add(0.0);
							size++;
						}
						size++;
						dat.given_r = new double[size];
						dat.given_v = new double[size];
						for (int i = 0; i < size; i++) {
							dat.given_r[i] = radii.get(i);
							dat.given_v[i] = potentials.get(i);
						}
						new DiscretePotential().discretePotential(dat, numR,
								deltaR);
						new TlReadParams().tlReadParams(packingFraction,
								dat.epsilon, dat.lambda, dat.r, dat);
						new SmoothGofr().smoothGofr(dat.lambda, dat);
						return null;
					}

					@Override
					protected void done() {
						if (flag) {
							tableUpdate();
							parent.somethingChanged();
						}
						rdfButton.setEnabled(true);
					}

				};
				sw.execute();
			}
		});
		btnLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (TableModelListener lmt : tableModel
						.getTableModelListeners())
					tableModel.removeTableModelListener(lmt);
				int option = jf.showOpenDialog(SystemTab.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					tableModel.setRowCount(0);
					try (Scanner sc = new Scanner(jf.getSelectedFile())
							.useDelimiter("[\\s,]+")) {
						listv.clear();
						listr.clear();
						Double[] temp = new Double[2];
						while (sc.hasNext()) {
							temp[0] = sc.nextDouble();
							temp[1] = sc.nextDouble();
							tableModel.insertRow(tableModel.getRowCount(), temp);
							listr.add(temp[0]);
							listv.add(temp[1]);
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(getRootPane(),
								Messages.getString("SystemTab.invalidInput"));
						return;
					}
					SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

						@Override
						protected Void doInBackground() throws Exception {
							dat.given_r = new double[listr.size()];
							dat.given_v = new double[listr.size()];
							for (int i = 0; i < listr.size(); i++) {
								dat.given_r[i] = listr.get(i);
								dat.given_v[i] = listv.get(i);
							}
							new DiscretePotential().discretePotential(dat,
									numR, deltaR);
							return null;
						}

						@Override
						protected void done() {
							Object[] ehh = {};
							tableModel.insertRow(tableModel.getRowCount(), ehh);
							tableModel.addTableModelListener(tableListener);
							potentialsTable.revalidate();
							potentialsTable.repaint();
							tableUpdate();
						}
					};
					sw.execute();
				}
			}
		});
		epsMaxField.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				dat.max_deps = Double.parseDouble(epsMaxField.getText());
				if (dat.max_deps < 0) {
					JOptionPane.showMessageDialog(getRootPane(),
							positiveWarning);
					epsMaxField.setText("" + epsMax); //$NON-NLS-1$
					dat.max_deps = epsMax;
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
	}

	class SerialListener implements TableModelListener, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2181777456303295741L;

		@Override
		public void tableChanged(TableModelEvent e) {
			int n = e.getFirstRow();
			if (("" + tableModel.getValueAt(n, 0)).isEmpty() //$NON-NLS-1$
					&& (("" + tableModel.getValueAt(n, 1)).isEmpty())) { //$NON-NLS-1$
				if (n + 1 == tableModel.getRowCount())
					return;
				tableModel.removeRow(n);
				listr.remove(n);
				listv.remove(n);
			} else if ((tableModel.getValueAt(n, 0)) == null
					|| (tableModel.getValueAt(n, 1)) == null) {
			} else {
				if (n != listr.size()) {
					listr.remove(n);
					listv.remove(n);
				}
				try {
					listr.add(n, Double.parseDouble("" //$NON-NLS-1$
							+ tableModel.getValueAt(n, 0)));
				} catch (Exception E) {
					listr.add(n, 0.0);
				}
				try {
					listv.add(n, Double.parseDouble("" //$NON-NLS-1$
							+ tableModel.getValueAt(n, 1)));
				} catch (Exception E) {
					listv.add(n, 0.0);
				}
			}
			n = tableModel.getRowCount() - 1;
			if (tableModel.getValueAt(n, 1) != null
					&& tableModel.getValueAt(n, 0) != null) {
				tableModel.addRow(new Object[0]);
			}
			me.remove(chartFrame.getContentPane());
			SwingWorker<Void, Void> work = new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					dat.given_r = new double[listr.size()];
					dat.given_v = new double[listr.size()];
					for (int i = 0; i < listr.size(); i++) {
						dat.given_r[i] = listr.get(i);
						dat.given_v[i] = listv.get(i);
					}
					ArrayList<double[][]> RYVals = new ArrayList<>();
					double[][] cont = { dat.given_r, dat.given_v };
					String[] name = { "Smooth" }; //$NON-NLS-1$
					RYVals.add(cont);
					chartFrame = new EmbeddedChart("Pair Potential, u(r)", //$NON-NLS-1$
							name, RYVals, false);
					return null;
				}

				@Override
				protected void done() {
					me.add(chartFrame.getContentPane(), chartConstraint);
					me.revalidate();
					me.repaint();
					parent.revalidate();
					parent.repaint();
				}
			};
			work.execute();
		}
	};
}

class Pair implements Comparable<Pair> {
	double radius, potential;

	public Pair(double r, double v) {
		radius = r;
		potential = v;
	}

	@Override
	public int compareTo(Pair arg0) {
		Pair comp = arg0;
		return radius - comp.radius > 0 ? 1 : -1;
	}
}
