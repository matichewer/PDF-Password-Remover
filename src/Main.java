import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.operator.SetHorizontalTextScaling;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JProgressBar;


public class Main {


	private static LinkedList<File> lista;
	private static String clave;
	
	private JFrame frame;
	private JPanel panelOpciones;
	private JScrollPane panelTabla;
	private Tablamodel tablaModel;
	private JTable tabla;
	private JButton btnRuta;
	private JTextField txtRuta;
	private JLabel lblClave;
	private JTextField txtClave;
	private JButton btnDesencriptar;
	private JProgressBar progressBar;
	

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		lista = new LinkedList<File>();
		crearFramePrincipal();		
		crearPanelOpciones();
		crearPanelTabla();
	}

	private void crearFramePrincipal() {
		frame = new JFrame();
		frame.setBounds(100, 100, 546, 268);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void crearPanelOpciones() {
		panelOpciones = new JPanel();
		panelOpciones.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		frame.getContentPane().add(panelOpciones, BorderLayout.NORTH);		
		{
			GridBagLayout gbl_panelOpciones = new GridBagLayout();
			gbl_panelOpciones.columnWidths = new int[]{164, 279, 0};
			gbl_panelOpciones.rowHeights = new int[] {30, 30, 0, 0};
			gbl_panelOpciones.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			gbl_panelOpciones.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			panelOpciones.setLayout(gbl_panelOpciones);
		}
		btnRuta = new JButton("Seleccionar archivo / carpeta");
		btnRuta.setHorizontalAlignment(SwingConstants.LEFT);
		btnRuta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFrame ventanaChooser = new JFrame();
				ventanaChooser.setBounds(100, 100, 450, 300);
				ventanaChooser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				ventanaChooser.getContentPane().setLayout(null);
				
				JFileChooser chooser = new JFileChooser();
			    chooser.setDialogTitle("Elige carpeta");
			    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			    chooser.setAcceptAllFileFilterUsed(false);			    
			    if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) { 
			    	System.out.println("getSelectedFile() : "+chooser.getSelectedFile());
			    	txtRuta.setText(chooser.getSelectedFile().toString());			    	
			    }
			    else 
			    	System.out.println("No Selection ");			      
			    ventanaChooser.getContentPane().add(chooser);
			    
			    
			}
		});
		GridBagConstraints gbc_btnRuta = new GridBagConstraints();
		gbc_btnRuta.insets = new Insets(0, 0, 5, 5);
		gbc_btnRuta.gridx = 0;
		gbc_btnRuta.gridy = 0;
		panelOpciones.add(btnRuta, gbc_btnRuta);
		{
			txtRuta = new JTextField();
			txtRuta.setEditable(false);
			txtRuta.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_txtRuta = new GridBagConstraints();
			gbc_txtRuta.insets = new Insets(0, 0, 5, 0);
			gbc_txtRuta.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtRuta.gridx = 1;
			gbc_txtRuta.gridy = 0;
			panelOpciones.add(txtRuta, gbc_txtRuta);
			txtRuta.setColumns(8);
		}
		{
			lblClave = new JLabel("Clave");
			GridBagConstraints gbc_lblClave = new GridBagConstraints();
			gbc_lblClave.insets = new Insets(0, 0, 5, 5);
			gbc_lblClave.gridx = 0;
			gbc_lblClave.gridy = 1;
			panelOpciones.add(lblClave, gbc_lblClave);
		}
		{
			txtClave = new JTextField();
			GridBagConstraints gbc_txtClave = new GridBagConstraints();
			gbc_txtClave.insets = new Insets(0, 0, 5, 0);
			gbc_txtClave.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtClave.gridx = 1;
			gbc_txtClave.gridy = 1;
			panelOpciones.add(txtClave, gbc_txtClave);
			txtClave.setColumns(10);
		}
		{
			btnDesencriptar = new JButton("Desencriptar");
			btnDesencriptar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new Thread() {
						public void run() {
							String ruta = txtRuta.getText();
							clave = txtClave.getText();							
							try {
								File file = new File(txtRuta.getText());
								if(file.isDirectory())
									recorrerCarpetas(new File(txtRuta.getText()));
								else
									try {
										removerClave(file);
									} catch (COSVisitorException | CryptographyException | ArchivoSinClaveException e) {
										e.printStackTrace();
						            	agregarFila(file.getName(), "Incorrecta");
									}
							} catch (IOException e) {
								e.printStackTrace();
							}		
						}					
				}.start();				
			}});
			GridBagConstraints gbc_btnDesencriptar = new GridBagConstraints();
			gbc_btnDesencriptar.gridwidth = 2;
			gbc_btnDesencriptar.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnDesencriptar.gridx = 0;
			gbc_btnDesencriptar.gridy = 2;
			panelOpciones.add(btnDesencriptar, gbc_btnDesencriptar);
		}
	}	
	


	public void crearPanelTabla() {
		panelTabla = new JScrollPane();
		frame.getContentPane().add(panelTabla, BorderLayout.CENTER);
	    tablaModel = new Tablamodel();        
	    tabla = new JTable();
	    panelTabla.setViewportView(tabla);              
	    tabla.setModel(tablaModel); 
	    tabla.setAutoCreateRowSorter(true);	    
	    {
	    	progressBar = new JProgressBar();
	    	frame.getContentPane().add(progressBar, BorderLayout.SOUTH);	    
	    }
	    
	    // centrar todos los datos de la tabla
	    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
	    centerRenderer.setHorizontalAlignment( JLabel.CENTER );
	    tabla.setDefaultRenderer(String.class, centerRenderer);  
	    tabla.setDefaultRenderer(Integer.class, centerRenderer);  
	    
	    // seteo tamanio de cada columna
	    tabla.getColumnModel().getColumn(0).setPreferredWidth(30);
	    tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
	    tabla.getColumnModel().getColumn(2).setPreferredWidth(200);
	}	
	

		    final class Tablamodel extends DefaultTableModel{
		    	private Class[] types;
		        private boolean[] canEdit;
		        
		        Tablamodel(){
		        	super(new String[][] {},
		        		  new String[]{"N", "Archivo", "Estado de clave"});
		        	types = new Class[] {java.lang.Integer.class,
		        	                     java.lang.String.class, 
		        	                     java.lang.String.class         	                     
		        	};
		        	canEdit= new boolean[] { false, false, false};
		        };             	
		    		             
		        public Class getColumnClass(int columnIndex){
		           return types[columnIndex];
		        }
		        
		        public boolean isCellEditable(int rowIndex, int columnIndex){
		           return canEdit[columnIndex];
		        }         	          	            	
		    };   

		    
	public void agregarFila(String archivo, String estado) {
		int fila = tabla.getRowCount();
	    ((DefaultTableModel) tabla.getModel()).setRowCount(fila+1); // creo nueva fila
	    tabla.setValueAt(fila+1, fila, 0);
	    tabla.setValueAt(archivo, fila, 1);
	    tabla.setValueAt(estado, fila, 2);
	}
	
//	public int cantTotalArchivos(File carpeta, int cant) {
//		for( File f : carpeta.listFiles())
//				
//	}
	

	public void recorrerCarpetas(File carpeta) throws IOException {
	    for (File elemento : carpeta.listFiles()) {
	        if (elemento.isDirectory()) 
	            recorrerCarpetas(elemento);
	        else {
	            if(elemento.getName().endsWith(".pdf")){
	            	PDDocument pdf = PDDocument.load(elemento.getAbsolutePath());	            	
	            	if(pdf.isEncrypted())
	            		try {
							removerClave(elemento);							
						} catch (COSVisitorException | IOException | CryptographyException
								| ArchivoSinClaveException e) {
							e.printStackTrace();
							pdf.close();
			            	agregarFila(elemento.getName(), "Incorrecta");
							continue;
						}	
	            	pdf.close();
	            }
	        }
	    }
	}
	

	public void removerClave(File archivo) throws IOException, CryptographyException, COSVisitorException, ArchivoSinClaveException {
		PDDocument pdf = PDDocument.load(archivo.getAbsolutePath());
		if(!pdf.isEncrypted()) 
			throw new ArchivoSinClaveException("Error: El archivo ya no tenia clave");
		else {
			pdf.setAllSecurityToBeRemoved(true);
			System.out.println(archivo.toString());
			System.out.println(clave);
			pdf.decrypt(clave);
			File outputFile = new File(archivo.getAbsolutePath());
			pdf.save(outputFile);
        	agregarFila(archivo.getName(), "Removida");
		} 
		pdf.close();			
	}
}
	
