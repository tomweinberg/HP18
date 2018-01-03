package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.*;

public class RunningView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3479137017514066741L;
	public JLabel lblBest;
	public JLabel lblcurrent;

	GUIGrid bestSoFar;
	GUIGrid current_protain;

	/**
	 * Constructor make a new RunningView
	 * 
	 * @param wid
	 *            the width for both grid in the GUI
	 * @param len
	 *            the length for both grids in the GUI
	 * 
	 */
	public RunningView(int wid, int len) { // constructor

		this.bestSoFar = new GUIGrid(wid, len);
		this.current_protain = new GUIGrid(wid, len);
		this.setTitle("GUI HP");
		// if ()

		buildme();

	}

	/**
	 * Constructor make a new RunningView
	 * 
	 * @param title
	 *            the title of the frame
	 * @param wid
	 *            the width for both grid in the GUI
	 * @param len
	 *            the length for both grids in the GUI
	 */
	public RunningView(String title, int wid, int len) { // constructor
		this(wid, len);
		this.setTitle(title);

		this.setExtendedState(Frame.MAXIMIZED_BOTH);

	}

	/**
	 * build the GUI for this JFrame
	 * 
	 * will build a GUI that present both best conformation so far and best conformation
	 * in the population
	 */
	public void buildme() {
		JLabel jLabel1 = new JLabel(
				"<html><u><bold><b1>current conformation in this round:</u></bold></b1></html>");
		JLabel jLabel2 = new JLabel(
				"<html><u><bold><b1>best conformation so far:</u></bold></b1></html>");

		lblcurrent = new JLabel("current details                                                        ");
		lblBest = new JLabel("best details                                                              ");

		Container c = getContentPane();
		Box boxes[] = new Box[3];
		JPanel mainPanel = new JPanel();
		JScrollPane mainPanelScrolpane = new JScrollPane(mainPanel);
		boxes[0] = Box.createHorizontalBox();
		boxes[1] = Box.createVerticalBox();
		boxes[2] = Box.createVerticalBox();

		boxes[1].add(jLabel1);
		boxes[1].add(lblcurrent);
		boxes[1].add(this.current_protain);
		boxes[2].add(jLabel2);
		boxes[2].add(lblBest);
		boxes[2].add(this.bestSoFar);
		mainPanel.setLayout(new BorderLayout(30, 30));
		mainPanel.add(boxes[1], BorderLayout.WEST);
		mainPanel.add(boxes[2], BorderLayout.EAST);
		c.add(mainPanelScrolpane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// pack(); //sets appropriate size for frame
		setVisible(true); // makes frame visible
		setSize(750, 700);
	}

	/**
	 * set the label content above the best conformation grid to the given string
	 * 
	 * @param bestDetails
	 *            the content to be present in the label above the best conformation
	 *            grid
	 */
	public void setBestLabelDetails(String bestDetails) {
		this.lblBest.setText(bestDetails);
	}

	/**
	 * set the label content above the current protein grid to the given string
	 * 
	 * @param currentDetails
	 *            the content to be present in the label above the current
	 *            protein grid
	 */
	public void setCurrentLabelDetails(String currentDetails) {
		this.lblcurrent.setText(currentDetails);
	}

	/**
	 * will show the protein given in the best conformation grid
	 * 
	 * @param prot
	 *            the protein to be showed on best conformation grid
	 */
	public void buildBestGrid(Protein prot) {
		this.bestSoFar.buildGrid(prot, 2);
		this.bestSoFar.run();
	}

	/**
	 * will show the protein given in the current protein grid
	 * 
	 * @param prot
	 *            the protein to be showed on current protein grid
	 */
	public void buildCurrentGrid(Protein prot) {
		if (!prot.equals(current_protain.getProteinPresented())) {
			this.current_protain.buildGrid(prot, 2);
			this.current_protain.run();
		}

	}

	/**
	 * will dispose the window
	 */
	public void disposeWindow() {
		this.dispose();
	}
}
