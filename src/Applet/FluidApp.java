/**
 * 
 */
package Applet;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.Timer;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import calculations.Data;

/*
 * <applet code="ComplexFluidsComputationApplet" width=900 height=600></applet>
 */
/**
 * @author Tres
 * 
 */
public class FluidApp extends JApplet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6574435618466046038L;
	private JTabbedPane calculations;
	private JTabbedPane systems;
	private int i = 0;
	private DynamicInfo dats = new DynamicInfo();
	private ComparisonTab compare;
	private ExportTable table;
	private JMenuItem newSystem;
	private JMenuItem store;
	private JMenuItem load;
	private JMenuItem removeSys;
	private JMenuItem loadSys;
	private JMenuItem storeSys;
	private ThermodynamicsTab thermo;

	/** 
	 * 
	 */
	public void init() {

		final JFileChooser jf = new JFileChooser();
		// menubar
		JMenuBar menu = new JMenuBar();
		JMenu options = new JMenu("Systems");
		newSystem = new JMenuItem(Messages.getString("FluidApp.newSystem"));
		removeSys = new JMenuItem(Messages.getString("FluidApp.removeSystem"));
		store = new JMenuItem(Messages.getString("FluidApp.storeState"));
		load = new JMenuItem(Messages.getString("FluidApp.loadState"));
		storeSys = new JMenuItem("Store System");
		loadSys = new JMenuItem("Load System");
		JMenu saveLoad = new JMenu("Store/Load");

		options.add(newSystem);
		options.add(removeSys);
		saveLoad.add(store);
		saveLoad.add(load);
		saveLoad.add(storeSys);
		saveLoad.add(loadSys);
		menu.add(options);
		menu.add(saveLoad);
		setJMenuBar(menu);

		// Initial Layout
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 450, 450 };
		gridBagLayout.rowHeights = new int[] { 0, 200 };
		gridBagLayout.columnWeights = new double[] { 0.5, 0.5 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0 };
		setLayout(gridBagLayout);
		systems = new JTabbedPane(JTabbedPane.NORTH,
				JTabbedPane.WRAP_TAB_LAYOUT);
		calculations = new JTabbedPane(JTabbedPane.NORTH,
				JTabbedPane.WRAP_TAB_LAYOUT);
		dats.systems.add(new Data());

		// Add initial windows
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		systems.add(new SystemTab(i++, this, jf, dats.systems.get(0)));
		compare = new ComparisonTab(FluidApp.this, dats, jf);
		calculations.add(compare);
		thermo = new ThermodynamicsTab(dats, this);
		calculations.add(thermo);
		add(systems, gbc);

		gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		add(calculations, gbc);
		table = new ExportTable(dats, this);

		gbc = new GridBagConstraints();
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(table, gbc);
		createListeners();
		setPreferredSize(new Dimension(900, 600));
		resize(900, 600);
	}

	public void changeName(JPanel panel, int i) {
		systems.remove(i);
		systems.add(panel, i);
		systems.setSelectedIndex(i);
		somethingChanged();
	}

	public void somethingChanged() {
		compare.systemUpdate();
		table.updateChoices();
		thermo.updateInfo();
	}

	public void updateSystems() {
		table.updateChoices();
	}

	public void newStuff() {
		thermo.updateInfo();
		compare.revalidate();
		compare.repaint();
		systems.getSelectedComponent().revalidate();
		systems.getSelectedComponent().repaint();
		table.revalidate();
		table.repaint();
		revalidate();
		repaint();
	}

	/**
	 * @param args
	 *            Doesn't use any arguments
	 */
	public static void main(String[] args) {
		// Create and start app
		FluidApp app = new FluidApp();
		app.init();
		app.start();

		javax.swing.JFrame window = new javax.swing.JFrame("Fluid Info"); // Title //$NON-NLS-1$
		window.setContentPane(app);
		window.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		window.pack(); // Arrange the components.
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true); // Make the window visible.
	}

	private void readObject(ObjectInputStream in) throws Exception {
		in.defaultReadObject();
		createListeners();
	}

	private void createListeners() {
		final JFileChooser jf = new JFileChooser();

		removeSys.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int j = systems.getSelectedIndex();
				systems.remove(j);
				dats.systems.remove(j);
				compare.systemUpdate();
				somethingChanged();
			}
		});
		newSystem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dats.systems.add(new Data());
				systems.add(new SystemTab(i++, FluidApp.this, jf, dats.systems
						.get(dats.systems.size() - 1)));
				compare.systemUpdate();
				somethingChanged();

			}
		});
		store.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int choice = jf.showSaveDialog(FluidApp.this);
				if (choice == JFileChooser.APPROVE_OPTION) {
					try (ObjectOutputStream os = new ObjectOutputStream(
							new FileOutputStream(jf.getSelectedFile()));) {
						FluidApp.this.stop();
						os.writeObject(FluidApp.this);
						FluidApp.this.start();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(getRootPane(),
								"Error: \n\n" + e1); //$NON-NLS-1$
					}
				}

			}
		});

		load.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int choice = jf.showOpenDialog(FluidApp.this);
				if (choice == JFileChooser.APPROVE_OPTION) {
					try (ObjectInputStream is = new ObjectInputStream(
							new FileInputStream(jf.getSelectedFile()));) {
						FluidApp app = (FluidApp) is.readObject();
						app.start();
						javax.swing.JFrame window = new javax.swing.JFrame(
								"Fluid Info"); // Title //$NON-NLS-1$
						window.setContentPane(app);
						window.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
						window.pack(); // Arrange the components.
						window.setVisible(true); // Make the window visible.
						window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					} catch (IOException | ClassNotFoundException e1) {
						JOptionPane.showMessageDialog(getRootPane(),
								"Error: \n\n" + e1);
					}
				}
			}
		});

		storeSys.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				int choice = jf.showSaveDialog(FluidApp.this);
				if (choice == JFileChooser.APPROVE_OPTION) {
					try (ObjectOutputStream os = new ObjectOutputStream(
							new FileOutputStream(jf.getSelectedFile()));) {
						FluidApp.this.stop();
						os.writeObject(systems.getSelectedComponent());
						FluidApp.this.start();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(getRootPane(),
								"Error: \n\n" + e1);
					}
				}

			}
		});

		loadSys.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = jf.showOpenDialog(FluidApp.this);
				if (choice == JFileChooser.APPROVE_OPTION) {
					try (ObjectInputStream is = new ObjectInputStream(
							new FileInputStream(jf.getSelectedFile()));) {
						SystemTab tab = (SystemTab) is.readObject();
						tab.parent = FluidApp.this;
						if (tab.getName().matches("System \\d+")) {
							tab.dat.name = "System " + i++;
							tab.setName(tab.dat.name);
						}
						dats.systems.add(tab.dat);
						systems.add(tab);
						compare.systemUpdate();
						somethingChanged();
					} catch (IOException | ClassNotFoundException e1) {
						JOptionPane.showMessageDialog(getRootPane(),
								"Error: \n\n" + e1);
					}
				}

			}
		});

		new Timer(2000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				newStuff();
			}
		}).start();
	}
}
