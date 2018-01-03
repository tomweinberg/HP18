package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DynamiclyConfGui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4518732155358732040L;
	private String confFileName;
	private Condition waitForGui;
	private Lock lock;
	private Properties properties;
	/**
	 * will save a Jtext for every key in the properties file
	 */
	private ArrayList<KeyValcontainer> keyAndJtexts;

	/**
	 * creates a new GUI configuration editor
	 * 
	 * @param conffileName
	 *            the name (and path) of the configuration file
	 * @param waitForGui
	 *            the condition to continue running in our case we will signal
	 *            when we finish editing and the program can continue running
	 * @param lock
	 *            the semaphore lock of the program (we will need it to lock
	 *            before signal)
	 */
	public DynamiclyConfGui(String conffileName, Condition waitForGui, Lock lock) {
		keyAndJtexts = new ArrayList<>();
		this.confFileName = conffileName;
		this.waitForGui = waitForGui;
		this.lock = lock;
		this.properties = new Properties();

		try {
			properties.load(new FileInputStream(confFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Container c = getContentPane();
		// c.setLayout( new BorderLayout( 30, 30 ) );

		Enumeration<Object> e = properties.keys();

		Box boxes[] = new Box[3];

		boxes[0] = Box.createHorizontalBox();
		boxes[1] = Box.createVerticalBox();
		boxes[2] = Box.createVerticalBox();
		while (e.hasMoreElements()) {
			String nextOne = (String) e.nextElement();
			JPanel jp1 = new JPanel();
			JPanel jp2 = new JPanel();

			jp2.add(new JLabel(nextOne + ":          "));
			boxes[1].add(jp2);
			JTextArea jta = new JTextArea(properties.getProperty(nextOne));

			jta.setLineWrap(true);
			KeyValcontainer kvc = new KeyValcontainer(nextOne, jta);
			keyAndJtexts.add(kvc);
			jta.setColumns(20);
			jta.setRows(5);
			JScrollPane jScrollPane1 = new JScrollPane(jta);

			jp1.add(jScrollPane1);
			boxes[2].add(jp1);

		}

		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout(30, 30));
		jp.add(boxes[1], BorderLayout.WEST);
		jp.add(boxes[2], BorderLayout.EAST);

		JButton runButton = new JButton();
		runButton.setText("Run");
		runButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				RunButtonActionPerformed(evt);
			}
		});
		runButton.setPreferredSize(new Dimension(250, 100));

		JPanel jp2 = new JPanel();
		jp2.add(runButton);
		jp.add(jp2, BorderLayout.SOUTH);
		c.add(new JScrollPane(jp));

		setSize(750, 700);

	}

	/**
	 * if the Run button was clicked
	 * 
	 */
	private void RunButtonActionPerformed(java.awt.event.ActionEvent evt) {

		try {

			for (Iterator<KeyValcontainer> iterator = keyAndJtexts.iterator(); iterator
					.hasNext();) {
				KeyValcontainer keyval = (KeyValcontainer) iterator.next();
				properties.setProperty(keyval.key, keyval.value.getText());

			}

			properties.store(new FileOutputStream(confFileName), "auto edited");
			// this.notifyAll();
			lock.lock();
			this.waitForGui.signal();
			lock.unlock();
			this.dispose();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author Manassen a continer for key,value couple
	 */
	public class KeyValcontainer {
		public String key;
		public JTextArea value;

		public KeyValcontainer(String key, JTextArea container) {
			this.key = key;
			this.value = container;
		}
	}

}
