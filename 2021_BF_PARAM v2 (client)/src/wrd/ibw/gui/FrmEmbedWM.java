package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;

import javax.swing.JButton;

import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.Util;

import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import oracle.jdbc.OracleTypes;
import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;
import javax.swing.ButtonGroup;
import java.awt.event.ItemListener;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class FrmEmbedWM extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private DBConnection dbConnection = null;
	
	private JComboBox<String> cbTable = null;
	private JSpinner spAF = null;
	private JTextField tfFractTupl;
	private JTextField tfMSB;
	private JTextField tfLSB;
	private JTextField tfPrivateKey;
	private JCheckBox cbGuessPen;
	private JFileChooser fileChooser;
	private File imageFile = null;
	private Image imageInfo = null;
	private JTable tbFields;
	private JButton btnStart = null;
	
	private int imageWidth = 0;
	private int imageHeight = 0;
	
	private String[] fixedFilds = {"ELEVATION", "ASPECT","SLOPE","HOR_DIST_TO_HYDROLOGY","VERT_DIST_TO_HYDROLOGY","HOR_DIST_TO_ROADWAYS","HILLSHADE_9AM","HILLSHADE_NOON","HILLSHADE_3PM","HOR_DIST_TO_FIRE_POINTS"};
	
	private Vector<Integer> imageVector = new Vector<Integer>();
	private Vector<Integer> directVector = new Vector<Integer>();
	
	private int embeddedInfo[][];
	private int originalImage[][];
	private JTextField txTotalPx;
	private JTextField txEmbeddedPx;
	private JLabel lbImgEmbedded;
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txMTP;
	private JTextField txEPP;
	private JPanel pnResults;
	private JTextField txMinLSB;
	
	private JCheckBox cbMDist;
	private JCheckBox cbLsbM;
	
	/**
	 * @wbp.nonvisual location=552,209
	 */
	private final ButtonGroup bgExtOpt = new ButtonGroup();
	
	public int[][] getImageMatrix(){
		return originalImage;
	}

	public FrmEmbedWM(DBConnection pDBConnection, Vector<Integer> embStream) {
		this.dbConnection = pDBConnection;
		try {
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.setTitle("[Sardroudi & Ibrahim, 2010] Embed Watermark...");
			this.setSize(723,561);
			this.getContentPane().setLayout(null);
			
			fileChooser = new JFileChooser(System.getProperty("user.dir") + System.getProperty("file.separator") + "img");
			fileChooser.setAcceptAllFileFilterUsed(false);
			
			String[] suffices = ImageIO.getReaderFileSuffixes();
			
			for (int i = 0; i < suffices.length; i++) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File (" + suffices[i] + ")", suffices[i]);
				if(suffices[i].equals("bmp"))
					fileChooser.addChoosableFileFilter(filter);
			}
			
			JLabel lblRelationToMark = new JLabel("Relation to Mark:");
			lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
			lblRelationToMark.setBounds(10, 11, 110, 14);
			getContentPane().add(lblRelationToMark);
			getContentPane().add(getJCBTable());
		
			JLabel lblFractionOrRelations = new JLabel("Fraction of Tuples:");
			lblFractionOrRelations.setHorizontalAlignment(SwingConstants.RIGHT);
			lblFractionOrRelations.setBounds(10, 60, 110, 14);
			getContentPane().add(lblFractionOrRelations);
		
			tfFractTupl = new JTextField();
			tfFractTupl.setText("40");
			tfFractTupl.setBounds(123, 57, 47, 20);
			getContentPane().add(tfFractTupl);
			tfFractTupl.setColumns(10);
		
			tfMSB = new JTextField();
			tfMSB.setText("3");
			tfMSB.setColumns(10);
			tfMSB.setBounds(257, 35, 37, 20);
			getContentPane().add(tfMSB);
			
			tfLSB = new JTextField();
			tfLSB.setText("1");
			tfLSB.setColumns(10);
			tfLSB.setBounds(257, 58, 37, 20);
			getContentPane().add(tfLSB);
		
			JLabel lblMsb = new JLabel("MSB:");
			lblMsb.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMsb.setBounds(222, 38, 31, 14);
			getContentPane().add(lblMsb);
			
			JLabel lblLsb = new JLabel("LSB:");
			lblLsb.setHorizontalAlignment(SwingConstants.RIGHT);
			lblLsb.setBounds(222, 61, 31, 14);
			getContentPane().add(lblLsb);
			
			JLabel lblPrivateKey = new JLabel("Private Key:");
			lblPrivateKey.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPrivateKey.setBounds(10, 37, 110, 14);
			getContentPane().add(lblPrivateKey);
			
			tfPrivateKey = new JTextField();
			tfPrivateKey.setText("Secu48102304782K");
			tfPrivateKey.setColumns(10);
			tfPrivateKey.setBounds(123, 34, 89, 20);
			getContentPane().add(tfPrivateKey);
			
			JPanel pnImageSelector = new JPanel();
			pnImageSelector.setLayout(null);
			pnImageSelector.setBorder(BorderFactory.createTitledBorder(null, "  Image to Embed  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			pnImageSelector.setBounds(304, 9, 204, 232);
			getContentPane().add(pnImageSelector);
			
			JLabel lbImageViewer = new JLabel();
			lbImageViewer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			lbImageViewer.setBounds(10, 23, 184, 164);
			pnImageSelector.add(lbImageViewer);
		
			JButton btnOpen = new JButton("Load Image");
			btnOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int returnVal = fileChooser.showOpenDialog(FrmEmbedWM.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						imageFile = fileChooser.getSelectedFile();
			            try {
			            	imageVector.clear();
			            	imageInfo = ImageIO.read(imageFile);
			            	Image bImage = imageInfo.getScaledInstance(lbImageViewer.getWidth(), lbImageViewer.getHeight(), Image.SCALE_SMOOTH);
			            	ImageIcon imgIcon = new ImageIcon(bImage);
			            	lbImageViewer.setIcon(imgIcon);
			            	
			            	Util.defineImageArray(imageFile);
			            	
			            	originalImage = Util.getImageMatrix();
			            	
			            	imageWidth = Util.getImageWidth();
			            	imageHeight = Util.getImageHeight();
			            	
			            	txTotalPx.setText(String.valueOf(imageWidth * imageHeight));
			            	
			            	imageVector = Util.getImageVector(); //storing the image vector into the corresponding class field
			            	
			            	for (int i = 0; i < imageVector.size(); i++) {
			            		directVector.add(0);
							}
			            	
			            	btnStart.setEnabled(true);
	                    } 
			            catch (IOException e) {
			            	btnStart.setEnabled(false);
	                        e.printStackTrace();
	                    }
			            catch (Exception ex) {
			            	btnStart.setEnabled(false);
	                        ex.printStackTrace();
	                    }
			        } 
					else {
						btnStart.setEnabled(false);
			        }
				}
			});
			btnOpen.setBounds(10, 198, 184, 23);
			pnImageSelector.add(btnOpen);
		
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBorder(BorderFactory.createTitledBorder(null, "  Fields to Consider  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel.setBounds(10, 85, 284, 156);
			getContentPane().add(panel);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 22, 264, 123);
			panel.add(scrollPane);
		
			tbFields = new JTable( ){
	            private static final long serialVersionUID = 1L;
	            @Override
	            public Class getColumnClass(int column) {
	                switch (column) {
	                    case 0:
	                    	return Boolean.class;
	                    case 1:
	                        return String.class;
	                    case 2:
	                    	 return String.class;
	                    default:
	                        return Boolean.class;
	                }
	            }
	        };
		
			tbFields.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			DefaultTableModel model = new DefaultTableModel();
		    model.addColumn("Get");
		    model.addColumn("Name");
		    model.addColumn("Type");

		    for (int i = 0; i < fixedFilds.length; i++) {
		    	model.addRow(new Object[]{Boolean.TRUE, fixedFilds[i], "FLOAT"});
			}
	    
		this.tbFields.setModel(model);
		
		this.tbFields.getColumnModel().getColumn(0).setPreferredWidth(37);
		this.tbFields.getColumnModel().getColumn(1).setPreferredWidth(145);
		this.tbFields.getColumnModel().getColumn(2).setPreferredWidth(64);
		this.tbFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		scrollPane.setViewportView(tbFields);
		
		btnStart = new JButton("Start");
		btnStart.setEnabled(false);
		btnStart.setBounds(514, 208, 89, 23);
		btnStart.addActionListener(new ActionListener() {
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent arg0) {
		    	int passedKets = 0;
		    	passedKets = 0;
				
				Calendar cal = Calendar.getInstance();
		    	cal.getTime();
		    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		    	System.out.println("PROCESS STARTED AT: " + sdf.format(cal.getTime()) );
		    	//System.out.println("-----------------------------------------------");
		    	
				boolean guesspen_check = false; 
				if(cbGuessPen.isSelected())
					guesspen_check = true;
					
				/* VS - BLOCK FOR VERIFICATION SINAL GENERATION */
				char[] verif_sig = new char[24];
				int[] vs_track = new int[24];
				for(int k = 0; k < 24; k++) {
					verif_sig[k] = '0';
					vs_track[k] = 0;
				}
				
				int tf = Integer.valueOf(tfFractTupl.getText());
				
				verif_sig = Util.buildVerifSignal(verif_sig, 8, tf);
				verif_sig = Util.buildVerifSignal(verif_sig, 16, imageHeight);
				verif_sig = Util.buildVerifSignal(verif_sig, 24, imageWidth);
				
				
				/* VS - ENDS VERIFICATION SIGNAL GENERATION */
				//-------------------------------------------
				/*EMBEDDING THE VERIFICATION SIGNAL -- BEGIN OF BLOCK*/
				CallableStatement vs_index = null;
				ResultSet vs_rset = null;
				
				String loader_name = "ELEVATION";
				int pk, index, abs_val, real_val;
		    	Float storer;
		    	String binary_val, new_bin;
		    	boolean sgn = false;
		    	
				try {
					CallableStatement upt_vs = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
					
					vs_index = dbConnection.getConnection().prepareCall ("{ ? = call GET_INDEX (?,?,?,?)}");
					vs_index.registerOutParameter (1, OracleTypes.CURSOR);
					vs_index.setString (2,cbTable.getSelectedItem().toString());
					vs_index.setString (3,tfPrivateKey.getText());
					vs_index.setInt (4, verif_sig.length);
					vs_index.setString (5, loader_name);
					vs_index.execute ();
								
					vs_rset = (ResultSet)vs_index.getObject (1);
							    
				    while (vs_rset.next ()){
				    	pk = vs_rset.getInt ("PK");
				    	index = vs_rset.getInt ("INDX_ELEM");
				    	storer = vs_rset.getFloat ("STORE_VAL");
				    	abs_val =Math.abs(storer.intValue()); 
				    	binary_val = Integer.toBinaryString(abs_val);
				    	if(storer < 0){ sgn = true;}
				    	
				    	new_bin = binary_val.substring(0, binary_val.length() - 1) + verif_sig[index] + binary_val.substring(binary_val.length(), binary_val.length()) ;
						
				    	new_bin = Util.minimizeVariation(binary_val, new_bin, 1, 1);
				    	
				    	if(upt_vs.isClosed()){
				    		upt_vs = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
				    	}
				    	upt_vs.setString(1, cbTable.getSelectedItem().toString());
				    	upt_vs.setString (2, loader_name);
				    	real_val = Integer.parseInt(new_bin,2);
				    	
				    	if(sgn){
				    		real_val = 0-real_val;
				    		sgn = false;
						}
				    	upt_vs.setInt(3, real_val);
				    	upt_vs.setInt(4, Integer.valueOf(vs_rset.getString ("PK")));
				    	
				    	if(guesspen_check)
				    		upt_vs.execute ();
				    	
				    	vs_track[index]++;
				    	
				    }
				
				} catch (Exception e) {
		    		e.printStackTrace();
		    	}
				/*EMBEDDING THE VERIFICATION SIGNAL -- END OF BLOCK*/
				
				txEmbeddedPx.setText("0");
				lbImgEmbedded.setBackground(Color.WHITE);
				
				embeddedInfo = new int[imageHeight][imageWidth];
				for (int i = 0; i < imageWidth; i++) {
 					for (int j = 0; j < imageHeight; j++) {
 						embeddedInfo[j][i] = -1;
 					}
 				}
				
				CallableStatement ids = null;
				ResultSet rset_pk = null;
				
				CallableStatement updater = null, attr_value_cs = null, hav_value_cs = null, attr_pos_cs = null;
				
				String attr_to_mark = "", binary_main = "", new_binary = "", hav_value;   
			    Float number_value;        
			    int width_pos = 0, height_pos = 0, lsb_pos = 0, image_element = 0, temp_decimal = 0;   
	        	int msb_pos = 0, value_to_insert = 0, msb_value = 0, absolute_value = 0, real_value = 0;
	        	boolean signed = false,  embedded = false;  
        	
	        	Vector<Integer> attrToMark = new Vector<Integer>();
	        	Vector<String> havValues = new Vector<String>();
			    
			    int tot_gen_attr = 0;
			    String prevID = "";
				
				try {
					//BUILDING THE ATTRIBUTES LIST
					Vector<String> attributes = new Vector<String>();
					for (int i = 0; i < tbFields.getRowCount(); i++) {
						 if(tbFields.getModel().getValueAt(i, 0).equals(true)){
							 attributes.add(tbFields.getModel().getValueAt(i, 1).toString());
						 }
					}
			    		
					ids = dbConnection.getConnection().prepareCall ("{ ? = call GET_GENERAL_INFO (?,?,?,?,?,?,?,?)}");
					ids.registerOutParameter (1, OracleTypes.CURSOR);
					ids.setString (2,cbTable.getSelectedItem().toString());
					ids.setString (3,tfPrivateKey.getText());
					ids.setInt (4, Integer.parseInt(tfFractTupl.getText()));
					ids.setInt (5, imageHeight-1);
					ids.setInt (6, imageWidth-1);
					ids.setInt (7, attributes.size()-1);
					ids.setInt (8, Integer.valueOf(tfMSB.getText())-1);
					ids.setInt (9, Integer.valueOf(tfLSB.getText())-1);
					ids.execute ();
									
					rset_pk = (ResultSet)ids.getObject (1);
								    
					if(attr_value_cs == null){ attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}"); }
				    attr_value_cs.registerOutParameter (1, Types.FLOAT);
									    
			    	if(hav_value_cs == null){ hav_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call CREATE_HAV (?,?,?)}"); }
			    	hav_value_cs.registerOutParameter (1, Types.INTEGER);
									    	
			    	if(attr_pos_cs == null){ attr_pos_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_ATTR_POS (?,?,?,?,?,?)}"); }
			    	attr_pos_cs.registerOutParameter (1, OracleTypes.CURSOR);
									    
				    if(updater == null){ updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}"); }	
									    
				    while (rset_pk.next ()){
				    	try {
				    		if(rset_pk.getInt ("CONS_FACT")==0){
				    			passedKets++;
				        		attrToMark.clear();
				        		havValues.clear();
										        	
				        		if(attr_value_cs.isClosed()){
				        			attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}");
				        			attr_value_cs.registerOutParameter (1, Types.FLOAT);
				        		}
										        	
				        		for (int i = 0; i < attributes.size(); i++) {
				        			attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
				        			attr_value_cs.setString(3, attributes.elementAt(i));
				        			attr_value_cs.setString(4, rset_pk.getString ("ID"));
				        			attr_value_cs.execute ();
				        			number_value = attr_value_cs.getFloat(1);
						        			
				        			absolute_value = Math.abs(number_value.intValue());
				        			binary_main = Integer.toBinaryString(absolute_value);
											        	
				        			if (binary_main.length()>=(Integer.valueOf(tfLSB.getText()))+Integer.valueOf(tfMSB.getText())){
				        				new_binary = binary_main.substring(0, binary_main.length() - (new BigInteger(tfLSB.getText()).intValue())) ;
				        				real_value = Integer.parseInt(new_binary,2);
											        		
				        				if(hav_value_cs.isClosed()){
				        					hav_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call CREATE_HAV (?,?,?)}");
				        					hav_value_cs.registerOutParameter (1, Types.INTEGER);
				        				}
												        	
				        				hav_value_cs.setString(2, rset_pk.getString ("ID"));
				        				hav_value_cs.setString (3,tfPrivateKey.getText());
				        				hav_value_cs.setFloat(4, real_value);
				        				
				        				hav_value_cs.execute ();
				        				hav_value = hav_value_cs.getString(1);
											        	
				        				if(new BigInteger(hav_value).mod(new BigInteger(spAF.getValue().toString()))== BigInteger.valueOf(new Long(0))){                       
				        					attrToMark.addElement(i);
				        					havValues.addElement(hav_value);
				        				}
				        			}
				        		}
									        	
				    			for (int i = 0; i < attrToMark.size(); i++) {
				    				embedded = false;
				    				attr_to_mark = attributes.elementAt(attrToMark.elementAt(i));
				    				attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
				    				attr_value_cs.setString(3, attr_to_mark);
				    				attr_value_cs.setString(4, rset_pk.getString ("ID"));
				    				attr_value_cs.execute ();
				    				number_value = attr_value_cs.getFloat(1);
										        	
				    				absolute_value = Math.abs(number_value.intValue());
				    				if(number_value < 0){ signed = true;}
					    				
				    				binary_main = Integer.toBinaryString(absolute_value);
				    				temp_decimal = Integer.parseInt(String.valueOf(number_value).substring(String.valueOf(number_value).indexOf ( "." )+1));
				    				if(temp_decimal!=0){ /*binary_decimal = Integer.toBinaryString(temp_decimal);*/}
										        	
				    				if(Integer.parseInt(tfLSB.getText())*2 < binary_main.length()){
			    						if(attr_pos_cs.isClosed()){
			    							attr_pos_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_ATTR_POS (?,?,?,?,?,?)}");
			    							attr_pos_cs.registerOutParameter (1, OracleTypes.CURSOR);
			    						}
										        			
			    						attr_pos_cs.setString (2,havValues.elementAt(i));
			    						attr_pos_cs.setInt (3, (Integer)spAF.getValue());
			    						attr_pos_cs.setInt (4, imageHeight-1);
			    						attr_pos_cs.setInt (5, imageWidth-1);
			    						attr_pos_cs.setInt (6, Integer.valueOf(tfMSB.getText())-1);
			    						attr_pos_cs.setInt (7, Integer.valueOf(tfLSB.getText())-1);
								        			
			    						attr_pos_cs.execute ();
			    						ResultSet rset_attr_hav = (ResultSet)attr_pos_cs.getObject (1);
										        			
			    						while (rset_attr_hav.next ()){
			    							height_pos = rset_attr_hav.getInt("H"); 
			    							width_pos =  rset_attr_hav.getInt("W"); 
					    							
			    							msb_pos = rset_attr_hav.getInt("MSB_POS") + 1  ;
			    							lsb_pos = rset_attr_hav.getInt("LSB_POS") + 1  ;
			    						}
						
				    					if((msb_pos - 1) < (binary_main.length()-lsb_pos)){  
				    						msb_value = Character.getNumericValue(binary_main.charAt(msb_pos-1));
										        			
				    						if((imageWidth * (height_pos) + width_pos) > 0  ){
				    							image_element = imageVector.elementAt(imageWidth * (height_pos) + width_pos);
				    							directVector.setElementAt(1, rset_pk.getInt("DIRECT_POS"));
				    							value_to_insert = image_element ^ msb_value;
										        				
			    								new_binary = binary_main.substring(0, binary_main.length() - lsb_pos) + value_to_insert + binary_main.substring(binary_main.length() - lsb_pos+1, binary_main.length()) ;
			    								int min_pos = 1;
			    								if (cbMDist.isSelected()) {	min_pos = Integer.valueOf(txMinLSB.getText());}
			    								new_binary = Util.minimizeVariation(binary_main, new_binary, lsb_pos, min_pos);
											        				
			    								//************************************* UPDATE BLOCK ********************************************
			    								if(updater.isClosed()){	updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");}			
			    								updater.setString(1, cbTable.getSelectedItem().toString());
			    								updater.setString (2, attr_to_mark);
											        				
			    								real_value = Integer.parseInt(new_binary,2);
											        				
			    								if(signed){
			    									real_value = 0-real_value;
			    									signed = false;
			    								}
			    								updater.setInt(3, real_value);
			    								updater.setInt(4, Integer.valueOf(rset_pk.getString ("ID")));
											        				
			    								updater.execute ();
			    								embStream.add(value_to_insert);
			    								embedded = true;
											        				
			    								if(!rset_pk.getString ("ID").equals(new String(prevID))){ prevID = rset_pk.getString ("ID");}
									        				
				    							if (embedded) {	embeddedInfo[height_pos][width_pos] = image_element;}
				    						}	
				    					}
				    				}
								}
					        	tot_gen_attr = tot_gen_attr + attrToMark.size();
									        	
					        	DecimalFormat dff = new DecimalFormat("##.##");
								dff.setRoundingMode(RoundingMode.DOWN);
				    		}
				    	} catch (Exception e) {
				    		e.printStackTrace();
				    	}finally {
				    		updater.close();
				    		if(hav_value_cs!=null)
				    			hav_value_cs.close();
				    		if(attr_pos_cs!=null)
				    			attr_pos_cs.close();
				    		attr_value_cs.close();
				    	}
				    }
				    
					//RESULTS REPORT BUIL SECTION***************************
					
					int no_embedd = 0;
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(embeddedInfo[j][i] != -1)
	 							no_embedd++;
	 					}
	 				}
					
					BufferedImage orig_img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(originalImage[j][i] == 1){
								orig_img.setRGB(i, j, Color.BLACK.getRGB());
								}else	if(originalImage[j][i] == 0){
									orig_img.setRGB(i, j, Color.WHITE.getRGB());
										} else {
											orig_img.setRGB(i, j, Color.RED.getRGB());
										}
							}
						}
					
					ImageIO.write(orig_img, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"incrusted.bmp"));
					BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					
					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(embeddedInfo[j][i] == 1){
								img.setRGB(i, j, Color.BLACK.getRGB());
	 						}else	if(embeddedInfo[j][i] == 0){
	 									img.setRGB(i, j, Color.WHITE.getRGB());
	 								} else {
	 									img.setRGB(i, j, Color.RED.getRGB());
	 								}
 						}
	 				}
					
					Image scaledInstance = img.getScaledInstance(lbImgEmbedded.getWidth(), lbImgEmbedded.getHeight(), Image.SCALE_DEFAULT);
					ImageIcon imageIcon = new ImageIcon(scaledInstance);
					lbImgEmbedded.setIcon(imageIcon);
					
					txEmbeddedPx.setText(String.valueOf(no_embedd));
					txTotalTupl.setText(String.valueOf(dbConnection.getNoRows(cbTable.getSelectedItem().toString(), tfPrivateKey.getText(), Integer.valueOf(tfFractTupl.getText()))));
					
					DecimalFormat df = new DecimalFormat("##.##");
					df.setRoundingMode(RoundingMode.DOWN);
					txEPP.setText(String.valueOf(df.format(Float.valueOf(txEmbeddedPx.getText())*100/Float.valueOf(txTotalPx.getText()))));
					
					txTotalTuples.setText(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())));
					txMTP.setText(String.valueOf(df.format(Float.valueOf(txTotalTupl.getText())*100/Float.valueOf(txTotalTuples.getText()))));
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					try {
						rset_pk.close();
						ids.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			    Calendar cal1 = Calendar.getInstance();
		    	cal1.getTime();
		    	SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
		    	System.out.println ("PROCESS COMPLETED AT: "+sdf1.format(cal1.getTime()));
		    	System.out.println("-----------------------------------------------");
		    	Toolkit.getDefaultToolkit().beep();
				
		    	System.out.println ("KEYS THAT PASED: "+passedKets);
		    	
		    	int tempo = 0;
		    	for (int i = 0; i < directVector.size(); i++) {
					if (directVector.elementAt(i) == 1) {
						tempo ++;
					}
				}
		    	System.out.println ("TIME: "+tempo);
				
				System.out.println("END OF EXPERIMENT");
			}
		});
		getContentPane().add(btnStart);
		
		JButton btnExit = new JButton("Close");
		btnExit.setBounds(609, 208, 89, 23);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dispose();
				} 
				catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		getContentPane().add(btnExit);
		
		pnResults = new JPanel();
		pnResults.setLayout(null);
		pnResults.setBorder(BorderFactory.createTitledBorder(null, "  Results of the Embedding Process ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnResults.setBounds(10, 282, 688, 273);
		getContentPane().add(pnResults);
		
		JPanel pnCapacity = new JPanel();
		pnCapacity.setBounds(10, 23, 503, 208);
		pnResults.add(pnCapacity);
		pnCapacity.setLayout(null);
		pnCapacity.setBorder(BorderFactory.createTitledBorder(null, "  Embedded Pixels ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		lbImgEmbedded = new JLabel();
		lbImgEmbedded.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lbImgEmbedded.setBounds(10, 30, 184, 164);
		lbImgEmbedded.setOpaque(true);
		lbImgEmbedded.setBackground(Color.WHITE);
		pnCapacity.add(lbImgEmbedded);
		
		JLabel lblTotal = new JLabel("Total:");
		lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotal.setBounds(401, 33, 38, 14);
		pnCapacity.add(lblTotal);
		
		JLabel lblEmbeddeds = new JLabel("Embedded:");
		lblEmbeddeds.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEmbeddeds.setBounds(356, 61, 83, 14);
		pnCapacity.add(lblEmbeddeds);
		
		txTotalPx = new JTextField();
		txTotalPx.setEditable(false);
		txTotalPx.setText("0");
		txTotalPx.setColumns(10);
		txTotalPx.setBounds(442, 30, 51, 20);
		pnCapacity.add(txTotalPx);
		
		txEmbeddedPx = new JTextField();
		txEmbeddedPx.setEditable(false);
		txEmbeddedPx.setText("0");
		txEmbeddedPx.setColumns(10);
		txEmbeddedPx.setBounds(442, 58, 51, 20);
		pnCapacity.add(txEmbeddedPx);
		
		JPanel pnLegend = new JPanel();
		pnLegend.setBounds(207, 31, 162, 95);
		pnCapacity.add(pnLegend);
		pnLegend.setLayout(null);
		pnLegend.setBorder(BorderFactory.createTitledBorder(null, "  Legend ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_4.setBackground(Color.RED);
		panel_4.setBounds(14, 25, 15, 15);
		pnLegend.add(panel_4);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(14, 49, 15, 15);
		pnLegend.add(panel_5);
		
		JLabel label_3 = new JLabel("Missed Pixel");
		label_3.setHorizontalAlignment(SwingConstants.LEFT);
		label_3.setBounds(33, 26, 95, 14);
		pnLegend.add(label_3);
		
		JLabel lblEncrustedPixelval = new JLabel("Encrusted Pixel (0)");
		lblEncrustedPixelval.setHorizontalAlignment(SwingConstants.LEFT);
		lblEncrustedPixelval.setBounds(33, 49, 119, 14);
		pnLegend.add(lblEncrustedPixelval);
		
		JLabel lblEncrustedPixelval_1 = new JLabel("Encrusted Pixel (1)");
		lblEncrustedPixelval_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblEncrustedPixelval_1.setBounds(33, 67, 119, 14);
		pnLegend.add(lblEncrustedPixelval_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBackground(Color.BLACK);
		panel_1.setBounds(14, 67, 15, 15);
		pnLegend.add(panel_1);
		
		JLabel label_4 = new JLabel("___________________");
		label_4.setHorizontalAlignment(SwingConstants.LEFT);
		label_4.setBounds(14, 32, 138, 14);
		label_4.setOpaque(true);
		pnLegend.add(label_4);
		
		JLabel label_5 = new JLabel("Percent:");
		label_5.setHorizontalAlignment(SwingConstants.RIGHT);
		label_5.setBounds(356, 96, 83, 14);
		pnCapacity.add(label_5);
		
		txEPP = new JTextField();
		txEPP.setText("0");
		txEPP.setEditable(false);
		txEPP.setColumns(10);
		txEPP.setBounds(442, 93, 51, 20);
		pnCapacity.add(txEPP);
		
		JLabel label_8 = new JLabel("__________________");
		label_8.setOpaque(true);
		label_8.setHorizontalAlignment(SwingConstants.LEFT);
		label_8.setBounds(366, 73, 130, 14);
		pnCapacity.add(label_8);
		
		JPanel pnMarkedRelation = new JPanel();
		pnMarkedRelation.setBounds(523, 23, 155, 130);
		pnResults.add(pnMarkedRelation);
		pnMarkedRelation.setLayout(null);
		pnMarkedRelation.setBorder(BorderFactory.createTitledBorder(null, "  Relation Tuples ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		JLabel label_1 = new JLabel("Marked:");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(17, 58, 59, 14);
		pnMarkedRelation.add(label_1);
		
		txTotalTupl = new JTextField();
		txTotalTupl.setText("0");
		txTotalTupl.setEditable(false);
		txTotalTupl.setColumns(10);
		txTotalTupl.setBounds(79, 55, 64, 20);
		pnMarkedRelation.add(txTotalTupl);
		
		
		JLabel label = new JLabel("Total:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(12, 30, 64, 14);
		pnMarkedRelation.add(label);
		
		txTotalTuples = new JTextField();
		txTotalTuples.setText("0");
		txTotalTuples.setEditable(false);
		txTotalTuples.setColumns(10);
		txTotalTuples.setBounds(79, 27, 64, 20);
		pnMarkedRelation.add(txTotalTuples);
		
		JLabel label_2 = new JLabel("Percent:");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(17, 96, 59, 14);
		pnMarkedRelation.add(label_2);
		
		txMTP = new JTextField();
		txMTP.setText("0");
		txMTP.setEditable(false);
		txMTP.setColumns(10);
		txMTP.setBounds(79, 93, 64, 20);
		pnMarkedRelation.add(txMTP);
		
		JLabel label_9 = new JLabel("__________________");
		label_9.setOpaque(true);
		label_9.setHorizontalAlignment(SwingConstants.LEFT);
		label_9.setBounds(16, 71, 131, 14);
		pnMarkedRelation.add(label_9);
		
		JLabel label_6 = new JLabel("-------------------------------------------------------------------------------------------------------------------------");
		label_6.setOpaque(true);
		label_6.setHorizontalAlignment(SwingConstants.LEFT);
		label_6.setBounds(12, 255, 496, 14);
		getContentPane().add(label_6);
		
		JPanel pnAlgorithms = new JPanel();
		pnAlgorithms.setLayout(null);
		pnAlgorithms.setBorder(BorderFactory.createTitledBorder(null, "  Algorithm Variations ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnAlgorithms.setBounds(513, 9, 185, 188);
		getContentPane().add(pnAlgorithms);
		
		cbMDist = new JCheckBox("Min-Distortion");
		cbMDist.setBounds(47, 33, 119, 23);
		pnAlgorithms.add(cbMDist);
		
		bgExtOpt.add(cbMDist);
		
		JLabel lblBits = new JLabel("Bits:");
		lblBits.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBits.setBounds(21, 67, 52, 14);
		pnAlgorithms.add(lblBits);
		
		txMinLSB = new JTextField();
		txMinLSB.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (Integer.valueOf(txMinLSB.getText())>Integer.valueOf(tfLSB.getText())) {
					txMinLSB.setText(tfLSB.getText());
				}
				if (cbLsbM.isSelected()) {
					txMinLSB.setText(tfLSB.getText());
				}
			}
		});
		txMinLSB.setText("0");
		txMinLSB.setColumns(10);
		txMinLSB.setBounds(77, 64, 26, 20);
		pnAlgorithms.add(txMinLSB);
		
		cbLsbM = new JCheckBox("LSB");
		cbLsbM.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				txMinLSB.setText(tfLSB.getText());
			}
		});
		cbLsbM.setBounds(105, 63, 52, 23);
		pnAlgorithms.add(cbLsbM);
		
		JLabel lblFractionOfAttributes = new JLabel("Fraction of Attributes:");
		lblFractionOfAttributes.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFractionOfAttributes.setBounds(1, 96, 119, 14);
		pnAlgorithms.add(lblFractionOfAttributes);
		
		spAF = new JSpinner();
		spAF.setModel(new SpinnerNumberModel(1, 1, 10, 1));
		spAF.setBounds(128, 93, 38, 20);
		pnAlgorithms.add(spAF);
		
		
		cbGuessPen = new JCheckBox("Guessing Penalization");
		cbGuessPen.setBounds(21, 158, 143, 23);
		pnAlgorithms.add(cbGuessPen);
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JComboBox<String> getJCBTable(){
		if(this.cbTable == null){
			try {
				this.cbTable = new JComboBox<String>();
				cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"COVERTYPE_A"}));
				cbTable.setSelectedIndex(0);
				this.cbTable.setBounds(123, 8, 171, 20);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.cbTable;
	}
	
	public int[][] getImgToEmbed(){
		return originalImage;
	}
	
	public int gettEmbedImgHeigh(){
		return imageHeight;
	}
	
	public int getEmbedImgWidth(){
		return imageWidth;
	}
	
	public String getSecretKey(){
		return tfPrivateKey.getText();
	}
	
	public String getTupleFract(){
		return tfFractTupl.getText();
	}
	
	public int getAttrFract(){
		return Integer.valueOf(spAF.getValue().toString());
	}
	
}
