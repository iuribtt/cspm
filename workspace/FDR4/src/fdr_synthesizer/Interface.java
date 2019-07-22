package fdr_synthesizer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.DocumentFilter;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Label;
import java.awt.Window;

import javax.swing.ScrollPaneConstants;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextPane;

public class Interface extends JFrame {

	private JPanel contentPane;
	private JTable tableInput;
	private JTable tableOutput;
	private DefaultTableModel modelInput;
	private DefaultTableModel modelOutput;
	private JTextArea txtOutputFDR;
	private JLabel lblGif;
	private JScrollPane scrollPaneFdrOutput;
	private JComboBox comboBox;
	private JTextPane txtTraceToCode;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"The application could not find graphic components properly",
					"erro", JOptionPane.ERROR_MESSAGE);
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface frame = new Interface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Interface() {
		getContentPane().setLayout(null);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 599);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu aboutMenu = new JMenu("About");
		aboutMenu.setMnemonic('A');
		aboutMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {

				JOptionPane.showMessageDialog(null,"\nUniversidade Federal de Pernambuco\n" +
						"Course: Specification of competing and distributed systems\n" +
						"Title: Program Synthesizer\n"+
						"Instructor: Alexandre Cabral Mota\n" +
						"Student : Iúri Batista Teles\n" +
						"                                                                 Date: 2019-07-21\n"+
						"Author: Iúri Batista Teles\n"
						,"About",JOptionPane.WARNING_MESSAGE);
			}
		});

		menuBar.add(aboutMenu);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblGif = new JLabel((new ImageIcon(getClass().getResource("/images/cat_no_time.gif"))));
		lblGif.setBounds(320, 370, 220, 160);
		lblGif.setVisible(false);
		contentPane.add(lblGif);

		JLabel lblSin = new JLabel("<html><h3>Specification of competing and distributed systems<hr></h3></html>");
		lblSin.setBounds(81, 0, 381, 50);
		contentPane.add(lblSin);

		//table1
		String[] col = new String[] {"Variable","Value"};

		String[][] data =  null;
		tableInput = new JTable();
		tableInput.setModel(new DefaultTableModel(data,col) {  
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {   
				return true;  
			}
		});
		tableInput.getColumnModel().getColumn(0).setPreferredWidth(40);
		tableInput.getColumnModel().getColumn(1).setPreferredWidth(40);	

		modelInput = (DefaultTableModel) tableInput.getModel();
		modelInput.addRow(new Object[]{"var1", "15"});
		modelInput.addRow(new Object[]{"var2", "2"});
		modelInput.addRow(new Object[]{"var3", "0"});

		JScrollPane scrollPaneInput = new JScrollPane();
		scrollPaneInput.setBounds(36, 66, 143, 108);
		scrollPaneInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPaneInput);

		scrollPaneInput.setViewportView(tableInput);

		tableOutput = new JTable();
		tableOutput.setModel(new DefaultTableModel(data,col) {  
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {   
				return true;  
			}
		});
		tableOutput.getColumnModel().getColumn(0).setPreferredWidth(40);
		tableOutput.getColumnModel().getColumn(1).setPreferredWidth(40);	

		modelOutput = (DefaultTableModel) tableOutput.getModel();
		modelOutput.addRow(new Object[]{"var1", "15"});
		modelOutput.addRow(new Object[]{"var2", "2"});
		modelOutput.addRow(new Object[]{"var3", "47"});

		JScrollPane scrollPaneOUtput = new JScrollPane();
		scrollPaneOUtput.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPaneOUtput.setBounds(36, 200, 143, 108);
		contentPane.add(scrollPaneOUtput);

		scrollPaneOUtput.setViewportView(tableOutput);

		JButton btnAddVariable = new JButton("Add Variable");
		btnAddVariable	.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelInput.addRow(new Object[]{"", ""});
				modelOutput.addRow(new Object[]{"", ""});
				
			}
		});
		btnAddVariable.setBounds(10, 319, 105, 35);
		contentPane.add(btnAddVariable);

		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modelInput.removeRow(modelInput.getRowCount()-1);
				modelOutput.removeRow(modelOutput.getRowCount()-1);
			}
		});
		btnRemove.setBounds(125, 319, 105, 35);
		contentPane.add(btnRemove);

		JButton btnSynthesizer = new JButton("Synthesizer");
		btnSynthesizer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, Integer> inputVariables = new LinkedHashMap<String, Integer>();
				Map<String, Integer> outputExpected = new LinkedHashMap<String, Integer>();

				boolean errorVariable = false;
				for (int i = 0; i < modelInput.getRowCount() && !errorVariable; i++) {

					String variableInput = modelInput.getValueAt(i, 0).toString().trim();
					String valueInput = modelInput.getValueAt(i, 1).toString();

					String variableOutput = modelOutput.getValueAt(i, 0).toString().trim();
					String valueOutput = modelOutput.getValueAt(i, 1).toString();

					if(!variableInput.isEmpty() && 	!variableOutput.isEmpty() &&
							isStringInt(valueInput) &&	isStringInt(valueOutput)) {

						inputVariables.put(variableInput, Integer.parseInt(valueInput));
						outputExpected.put(variableOutput, Integer.parseInt(valueOutput));

					}else {
						JLabel label = new JLabel("<html>Please verify the Variable input and output at line " + (i + 1) + ".\n" + 
								"The <u>Variable</u> must be a <b>String</b> and the <u>Value</u> a <b>Integer</b></html>");
						JOptionPane.showMessageDialog(null,
								label, "Variable error", JOptionPane.ERROR_MESSAGE);

						errorVariable = true;
					}
				}

				//call FDR
				if(!errorVariable) {
					callFDR(inputVariables, outputExpected);
				}
			}
		});
		btnSynthesizer.setBounds(189, 185, 112, 35);
		contentPane.add(btnSynthesizer);

		JLabel lblIcontip =new JLabel(new ImageIcon(getClass().getResource("/images/icon-tip-off.gif")));
		lblIcontip.setBounds(445, 11, 20, 25);
		lblIcontip.setToolTipText("<html><body bgcolor=\"#ffffff\">Project uses CSPM and FDR to synthetize a simple operational program.</body></html>");
		lblIcontip.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent arg0) {
				lblIcontip.setIcon(new ImageIcon(getClass().getResource("/images/icon-tip-off.gif")));
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				lblIcontip.setIcon(new ImageIcon(getClass().getResource("/images/icon-tip-on.gif")));
			}
		});
		contentPane.add(lblIcontip);

		JLabel lblMaxDeep = new JLabel("<html><b>Max. deep:</b></h3></html>");
		lblMaxDeep.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblMaxDeep.setBounds(208, 110, 68, 14);
		contentPane.add(lblMaxDeep);

		contentPane.setBackground(new Color(230,230,230));

		JLabel lbloutput = new JLabel("<html><b>Output:</b></h3></html>");
		lbloutput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lbloutput.setBounds(39, 185, 68, 14);
		contentPane.add(lbloutput);

		JLabel lblinput = new JLabel("<html><b>Input:</b></h3></html>");
		lblinput.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblinput.setBounds(39, 50, 68, 14);
		contentPane.add(lblinput);

		Integer[] choices = { 1, 2, 3, 4 , 5, 6};

		comboBox = new JComboBox(choices);
		comboBox.setBounds(215, 135, 49, 25);
		comboBox.setSelectedItem(4);
		contentPane.add(comboBox);

		scrollPaneFdrOutput = new JScrollPane();
		scrollPaneFdrOutput.setBounds(10, 365, 534, 169);
		contentPane.add(scrollPaneFdrOutput);

		txtOutputFDR = new JTextArea();
		scrollPaneFdrOutput.setViewportView(txtOutputFDR);
		txtOutputFDR.setWrapStyleWord(true);
		txtOutputFDR.setEditable(false);
		txtOutputFDR.setToolTipText("Output FDR4:");
		txtOutputFDR.setText("Output FDR4");

		JScrollPane scrollPaneCode = new JScrollPane();
		scrollPaneCode.setBounds(311, 61, 233, 293);
		contentPane.add(scrollPaneCode);
		
		txtTraceToCode = new JTextPane();
		txtTraceToCode.setToolTipText("The code will be displayed here");
		txtTraceToCode.setContentType("text/html");
		txtTraceToCode.setText("<html><br /><br /><h2 align='center'>The code will be displayed <br /> here<br />...</h2></html>");
		scrollPaneCode.setViewportView(txtTraceToCode);


	}

	/*
	 * Verify it is a Interger
	 */
	public boolean isStringInt(String s)
	{
		try
		{
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException ex)
		{
			return false;
		}
	}

	public static String readFile(String filePath) {


		String content = "";
		try
		{
			content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
		}
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File did not found!\nPlease try again.", "Error", JOptionPane.ERROR_MESSAGE);
		} 
		catch (Exception e) {
			System.out.println ("Mismatch exception:" + e );
		}

		return content;
	}

	public static File createTemFile(String content, String prefix, String extension) {

		File tempFile = null;

		try{
			//create a temp file
			tempFile = File.createTempFile(prefix, extension); 

			//write it
			BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
			bw.write(content);
			bw.close();

			System.out.println("Done");

		}catch(IOException e){
			e.printStackTrace();
		}

		return tempFile;
	}

	protected void callFDR(Map<String, Integer> inputVariables, Map<String, Integer> outputExpected) {

		lblGif.setVisible(true);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				String fileName = "projeto_synthesizer.csp";

				ClassLoader classLoader = ClassLoader.getSystemClassLoader();

				File file = new File(classLoader.getResource(fileName).getPath());

				String strFileSynthesizer = readFile(file.getPath());

				String datatypeFDR = "";
				for(Map.Entry<String, Integer> map : inputVariables.entrySet()){
					String key = map.getKey();
					datatypeFDR = datatypeFDR + " | " + key;
				}

				datatypeFDR = datatypeFDR.replaceFirst(" \\| ", "");

				//Add the variables
				strFileSynthesizer = strFileSynthesizer.replaceFirst("\\{USER_VARIABLES\\}", datatypeFDR);

				//USER INPUT
				String userInput = "{";
				for(Map.Entry<String, Integer> map : inputVariables.entrySet()){
					String key = map.getKey();
					Integer value = map.getValue();
					userInput = userInput + ", (" + key + ", " + value + ")" ;
				}
				userInput = userInput.replaceFirst(",", "").concat(" }");

//				System.out.println(userInput);

				strFileSynthesizer = strFileSynthesizer.replaceFirst("\\{USER_INPUT\\}", userInput);

				//USER OUTPUT
				String userExpectedOutput = "{";
				for(Map.Entry<String, Integer> map : outputExpected.entrySet()){
					String key = map.getKey();
					Integer value = map.getValue();
					userExpectedOutput = userExpectedOutput + ", (" + key + ", " + value + ")" ;
				}
				userExpectedOutput = userExpectedOutput.replaceFirst(",", "").concat(" }");

//				System.out.println(userExpectedOutput);

				strFileSynthesizer = strFileSynthesizer.replaceFirst("\\{USER_EXPECTED_OUTPUT\\}", userExpectedOutput);

				//DEEP
				strFileSynthesizer = strFileSynthesizer.replaceFirst("\\{DEEP\\}", 	String.valueOf(comboBox.getSelectedItem()));

//				System.out.println(strFileSynthesizer);
				File fileCreated = createTemFile(strFileSynthesizer, "csp_sinthesize_file_temp", ".csp");


				String fdrOutput = FDR.runFDR(fileCreated.getPath());

//				System.out.println(fdrOutput);

				txtOutputFDR.setText(fdrOutput);

				txtOutputFDR.setVisible(true);
				scrollPaneFdrOutput.setVisible(true);
				
				txtTraceToCode.setText(Utils.traceToCode(fdrOutput.substring(fdrOutput.indexOf("Trace: ")+7, fdrOutput.indexOf("_FOUND,") + 6), inputVariables, outputExpected));
				
				lblGif.setVisible(false);

			}
		}).start();

	}
}
