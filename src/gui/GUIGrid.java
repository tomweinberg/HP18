package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.MinMax;
import main.Monomer;
import main.MonomerType;
import main.Protein;

public class GUIGrid extends JPanel implements Runnable {

	private static final long serialVersionUID = -5165880094666105337L;
	private JButton[][] grid; // names the grid of buttons
	private int width;
	private int length;
	private boolean isBigProtain = false;

	protected Protein proteinPresented;

	public GUIGrid(int wid, int len) { // constructor

		width = Math.min(wid, 70);
		length = Math.min(len, 70);
		// if ()

		setLayout(new GridLayout(width, length)); // set layout
		this.setSize(new Dimension(1500, 1500));
		grid = new JButton[width][length]; // allocate the size of grid

		buildGrid();
	}

	/*
	 * public GUIGrid(String title,int wid, int len){ //constructor
	 * this(wid,len); this.panel.setTitle(title); this.title=title; }
	 */

	/**
	 * build grid according to the protein given
	 * 
	 * @param prot
	 *            the protein will be present in the grid
	 * @param dimensions
	 *            the dimensions of the protein
	 */
	public void buildGrid(Protein prot, int dimensions) {
		if (dimensions != 2)
			throw new RuntimeException("the gui support only two dimination");
		MinMax minMax = new MinMax(prot);
		clearGrid();
		boolean first = true;
		this.proteinPresented = prot;
		Monomer prev = null;
		int x, y;
		int prevX = '.';
		int prevY = '.';
		for (Monomer monomer : prot) {
			x = 2 * (monomer.getX() - minMax.minX);
		    y = 2 * (monomer.getY() - minMax.minY);
			prev = buildGridMonomer(monomer,prev, first, x,y,prevX,prevY);
			first = false;
			prevX = x;
			prevY = y;
		}
	}
		
    private void redFrame() {
		for (int i = 0; i < width; i++) {
			grid[i][0].setBackground(Color.RED);
			grid[i][length - 1].setBackground(Color.RED);

		}
		for (int i = 0; i < length; i++) {
			grid[0][i].setBackground(Color.RED);
			grid[width - 1][i].setBackground(Color.RED);

		}
    }

    private Monomer buildGridMonomer(Monomer monomer, Monomer prev, boolean first, int x, int y,int prevX, int prevY) {
	    if (grid.length <= x || grid[0].length <= y) {
	    	redFrame();
		    prev = null;
	    } else {
				if (monomer.type == MonomerType.H) {
					if (first) {
						grid[x][y].setBackground(Color.GREEN);
					} else {
						grid[x][y].setBackground(Color.ORANGE);
					}
					if (!isBigProtain)
						grid[x][y].setText("H");

				} else {
					if (first) {
						grid[x][y].setBackground(Color.GREEN);
					} else {
						grid[x][y].setBackground(Color.CYAN);
					}
					if (!isBigProtain)
						grid[x][y].setText("P");

				}
				if (prev != null) {
					if (prevX != x) {
						grid[(x + prevX) / 2][y]
								.setBackground(Color.LIGHT_GRAY);
					} else {
						grid[x][(y + prevY) / 2]
								.setBackground(Color.LIGHT_GRAY);
					}
				}
				prev = monomer;
			}
    	return prev;
    }

	/**
	 * this method will build the grid for the first time (clean grid)
	 */

	private void buildGrid() {
		// clear the Grid
		if (width > 30)
			this.isBigProtain = true;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < length; j++) {
				JButton btn = new JButton();
				btn.setBackground(Color.WHITE);
				if (isBigProtain)// if grid really big, smaller cubes
					btn.setPreferredSize(new Dimension(10, 10));
				btn.setBorder(null);
				// btn.setPreferredSize(new Dimension(5,5));
				// btn.setMaximumSize(new Dimension(5,5));
				grid[i][j] = btn;

				add(grid[i][j]); // adds button to grid
			}
		}
	}

	/**
	 * clears the grid
	 */
	private void clearGrid() {
		/*
		 * if (this.title!=null) { this.panel.setTitle(this.title); }
		 */
		// clear the Grid
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < length; j++) {
				grid[i][j].setBackground(Color.WHITE);
				grid[i][j].setText("");
			}
		}
	}

	/*
	 * public void disposeWindow() { this.panel.dispose(); }
	 */

	@Override
	public void run() {
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception caught");
		}
	}

	public Protein getProteinPresented() {
		return proteinPresented;
	}

}
