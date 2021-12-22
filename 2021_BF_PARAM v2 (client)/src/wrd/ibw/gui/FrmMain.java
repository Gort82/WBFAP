package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;
import java.awt.event.ActionEvent;

import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.jssim.SsimCalculator;

import javax.swing.SwingConstants;

public class FrmMain extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	private JLabel lblStatus = null;
	private JTextField tfServer;
	private JTextField tfSID;
	private JTextField tfUser;
	private JPasswordField tfPassword;
	private DBConnection dbConnection = null;
	private Vector<Integer> embStream = new Vector<Integer>();
	private Vector<Integer> extStream = new Vector<Integer>();
	
	private JButton btnEmbedWM = null;
	private JButton btnExtractWM = null;
	private JButton btnMetrics;
	private JButton btnFixedUpdate = null;
	private JButton btnRandomUpdate = null;
	
	private Vector<Integer>signalRest = new Vector<Integer>();
	private Vector<Integer>acumRest = new Vector<Integer>();
	
	private static FrmEmbedWM frmEmbedWM = null;
	private static FrmExtractWM frmExtractWM = null;
	
	private int originalImage[][];
	private int recoveredImage[][];
	private int originalImgHeight;
	private int originalImgWidth;
	
	private SsimCalculator mySSIMCalc = null;
	private JTextField txCF;
	private JTextField txSSIM_Ext;
	private JTextField txStart;
	private JTextField txEnd;
	
	public FrmMain() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("2021. BF_R");
		this.setSize(546,298);
		this.getContentPane().setLayout(null);
		
		JPanel pnConnection = new JPanel();
		pnConnection.setBounds(10, 11, 284, 167);
		getContentPane().add(pnConnection);
		pnConnection.setBorder(BorderFactory.createTitledBorder(null, "  Database Connection  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnConnection.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server:");
		lblNewLabel.setBounds(10, 32, 77, 14);
		pnConnection.add(lblNewLabel);
		
		JLabel lblDatabase = new JLabel("SID:");
		lblDatabase.setBounds(10, 57, 77, 14);
		pnConnection.add(lblDatabase);
		
		JLabel lblUser = new JLabel("User:");
		lblUser.setBounds(10, 82, 77, 14);
		pnConnection.add(lblUser);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 107, 77, 14);
		pnConnection.add(lblPassword);
		
		tfServer = new JTextField();
		tfServer.setText("localhost");
		tfServer.setBounds(77, 29, 197, 20);
		pnConnection.add(tfServer);
		tfServer.setColumns(10);
		
		tfSID = new JTextField();
		tfSID.setText("orcl");
		tfSID.setColumns(10);
		tfSID.setBounds(77, 54, 197, 20);
		pnConnection.add(tfSID);
		
		tfUser = new JTextField();
		tfUser.setText("system");
		tfUser.setColumns(10);
		tfUser.setBounds(77, 79, 197, 20);
		pnConnection.add(tfUser);
		
		tfPassword = new JPasswordField();
		tfPassword.setText("gort");
		tfPassword.setColumns(10);
		tfPassword.setBounds(77, 104, 197, 20);
		pnConnection.add(tfPassword);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(btnConnect.getText().equals("Connect")){
					if(dbConnection == null){
						dbConnection = new DBConnection(tfServer.getText(), tfSID.getText(), tfUser.getText(), String.copyValueOf(tfPassword.getPassword()));
						if(dbConnection.getConnection() != null){
							getBtnEmbedWM().setEnabled(true);
							getBtnExtractWM().setEnabled(true);
							btnMetrics.setEnabled(true);
							btnConnect.setText("Disconnect");
							lblStatus.setForeground(new Color(34, 139, 34));
							lblStatus.setText("Connected...");
						}else{
							JOptionPane.showMessageDialog(null, "Connection failure!!!");
							btnConnect.setText("Connect");
							lblStatus.setForeground(Color.RED);
							lblStatus.setText("Disconnected...");
							getBtnEmbedWM().setEnabled(false);
							getBtnExtractWM().setEnabled(false);
							btnMetrics.setEnabled(false);
						}
					}
				}else{
					if(dbConnection.getConnection() != null){
						try {
							dbConnection.getConnection().close();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Some problem!!!");
							lblStatus.setForeground(Color.RED);
							lblStatus.setText("Disconnected...");
						}
					} 
					dbConnection = null;
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Disconnected...");
					getBtnEmbedWM().setEnabled(false);
					getBtnExtractWM().setEnabled(false);
					btnConnect.setText("Connect");
					
					btnFixedUpdate.setEnabled(false);
					btnRandomUpdate.setEnabled(false);
					btnMetrics.setEnabled(false);
				}
			}
		});
		
		btnConnect.setBounds(162, 135, 112, 23);
		pnConnection.add(btnConnect);
		
		lblStatus = new JLabel("Disconnected...");
		lblStatus.setBounds(54, 142, 95, 14);
		pnConnection.add(lblStatus);
		lblStatus.setForeground(Color.RED);
		
		JLabel lblCaption = new JLabel("Status: ");
		lblCaption.setBounds(10, 142, 46, 14);
		pnConnection.add(lblCaption);
		
		JPanel pnWatermark = new JPanel();
		pnWatermark.setLayout(null);
		pnWatermark.setBorder(BorderFactory.createTitledBorder(null, "  Watermarking Processes  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnWatermark.setBounds(10, 189, 284, 63);
		getContentPane().add(pnWatermark);
		pnWatermark.add(getBtnExtractWM());
		pnWatermark.add(getBtnEmbedWM());
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder(null, "  Evaluation Metrics  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel.setBounds(306, 11, 215, 89);
		getContentPane().add(panel);
		
		btnMetrics = new JButton("Calculate");
		btnMetrics.setEnabled(false);
		btnMetrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				originalImgWidth = frmEmbedWM.getEmbedImgWidth();
				originalImgHeight = frmEmbedWM.gettEmbedImgHeigh();
				
				originalImage = new int[originalImgHeight][originalImgWidth]; 
				originalImage = frmEmbedWM.getImgToEmbed();
				
				if((originalImgWidth == frmExtractWM.getExtImgWidth())&&(originalImgHeight == frmExtractWM.getExtImgHeigh())){
					recoveredImage = new int[frmExtractWM.getExtImgHeigh()][frmExtractWM.getExtImgWidth()]; 
					recoveredImage = frmExtractWM.getExtractedImg();
					
					float cumul = 0;		float mse1 = 0;
					float cf = 0;        
					try {
						for (int i = 0; i < originalImgWidth; i++) {
		 					for (int j = 0; j < originalImgHeight; j++) {
		 						if(recoveredImage[j][i] != -1){
		 							cumul = cumul + (originalImage[j][i] ^ (-1*(recoveredImage[j][i]-1)));
		 							mse1 = mse1 + (float)Math.pow((originalImage[j][i] - recoveredImage[j][i]),2);
		 						}
		 					}
		 				}
						
						cf = 100*cumul/(originalImgWidth*originalImgHeight);
						
						DecimalFormat df = new DecimalFormat("##.##");
						df.setRoundingMode(RoundingMode.DOWN);
						
						System.out.println("Correction Factor: " + cf);
						
						txCF.setText(String.valueOf(df.format(cf)));
						
						DecimalFormat df1 = new DecimalFormat("##.######");
						df1.setRoundingMode(RoundingMode.DOWN);
						
						DecimalFormat df2 = new DecimalFormat("###.####");
						df2.setRoundingMode(RoundingMode.DOWN);
						
						
						File fileEmb = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"incrusted.bmp");
						mySSIMCalc = new SsimCalculator(fileEmb);
						
						File fileExt =  new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"extracted.bmp");

						Double tempSSIM_A = Double.valueOf(0);
						tempSSIM_A = mySSIMCalc.compareTo(fileExt);
						
						txSSIM_Ext.setText(String.valueOf(df.format(tempSSIM_A)));
					} 
					catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}else{
					JOptionPane.showMessageDialog(null, "The dimension of the extracted image is different to the embedded image...");
				}
				
				
			}
		});
		btnMetrics.setBounds(110, 56, 95, 23);
		panel.add(btnMetrics);
		
		txCF = new JTextField();
		txCF.setText("0");
		txCF.setEditable(false);
		txCF.setColumns(10);
		txCF.setBounds(64, 25, 41, 20);
		panel.add(txCF);
		
		JLabel lblCf = new JLabel("CF:");
		lblCf.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCf.setBounds(21, 29, 41, 14);
		panel.add(lblCf);
		
		JLabel lblSsim = new JLabel("SSIM:");
		lblSsim.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSsim.setBounds(115, 28, 41, 14);
		panel.add(lblSsim);
		
		txSSIM_Ext = new JTextField();
		txSSIM_Ext.setText("0");
		txSSIM_Ext.setEditable(false);
		txSSIM_Ext.setColumns(10);
		txSSIM_Ext.setBounds(158, 25, 41, 20);
		panel.add(txSSIM_Ext);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setBounds(396, 229, 125, 23);
		getContentPane().add(btnExit);
		
		JButton btnStreams = new JButton("STREAMS");
		btnStreams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				signalRest.clear();
				acumRest.clear();
				if(extStream.size()!=embStream.size())
					if(extStream.size()>embStream.size())
						for (int i = embStream.size(); i < extStream.size(); i++) {
							embStream.add(-1);
						}
					else
						for (int i = extStream.size(); i < embStream.size(); i++) {
							extStream.add(-1);
						}
				
				for (int i = 0; i < embStream.size(); i++) {
					if (embStream.get(i)==extStream.get(i)) {
						signalRest.add(1);
					} else {
						signalRest.add(-1);
					}
				}
				
				int acum = 0;
				for (int i = 0; i < embStream.size(); i++) {
					acum = acum + signalRest.get(i);
					acumRest.add(acum);
				}
			}
		});
		btnStreams.setBounds(304, 112, 89, 23);
		getContentPane().add(btnStreams);
		
		txStart = new JTextField();
		txStart.setText("0");
		txStart.setColumns(10);
		txStart.setBounds(404, 113, 55, 20);
		getContentPane().add(txStart);
		
		txEnd = new JTextField();
		txEnd.setText("5000");
		txEnd.setColumns(10);
		txEnd.setBounds(469, 113, 51, 20);
		getContentPane().add(txEnd);
		
		JButton btnPrint = new JButton("PRINT");
		btnPrint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = Integer.valueOf(txStart.getText()); i <  Integer.valueOf(txEnd.getText()); i++) {
					//System.out.println("VARIATIONS" + signalRest.get(i));
					System.out.println(acumRest.get(i));
				}
			}
		});
		btnPrint.setBounds(428, 144, 89, 23);
		getContentPane().add(btnPrint);
	}
	
	private JButton getBtnEmbedWM(){
		if (this.btnEmbedWM == null){
			this.btnEmbedWM = new JButton("Embed WM");
			btnEmbedWM.setBounds(10, 26, 127, 23);
			this.btnEmbedWM.setEnabled(false);
			this.btnEmbedWM.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(frmEmbedWM == null){
						frmEmbedWM = new FrmEmbedWM(dbConnection, embStream);
						frmEmbedWM.setLocationRelativeTo(null);
					}
					frmEmbedWM.setVisible(true);
				}
			});
		}
		return this.btnEmbedWM;
	}
	
	private JButton getBtnExtractWM(){
		if (this.btnExtractWM == null){
			this.btnExtractWM = new JButton("Extract WM");
			btnExtractWM.setBounds(147, 26, 127, 23);
			this.btnExtractWM.setEnabled(false);
			this.btnExtractWM.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(frmExtractWM == null){
						frmExtractWM = new FrmExtractWM(dbConnection, frmEmbedWM.getSecretKey(), frmEmbedWM.getTupleFract(), frmEmbedWM.getAttrFract(), extStream);
						frmExtractWM.setLocationRelativeTo(null);
					}
					frmExtractWM.setVisible(true);
					
				}
			});
		}
		return this.btnExtractWM;
	}
	
	public void setOriginalData(int pData[][],int pHeight, int pWidth){
		this.originalImage = new int[pHeight][pWidth];
		for (int i = 0; i < pWidth; i++) {
			for (int j = 0; j < pHeight; j++) {
				originalImage[j][i] = pData[j][i];
			}
		}
	}
	
	public void setRecoveredData(int pData[][],int pHeight, int pWidth){
		this.recoveredImage = new int[pHeight][pWidth];
		for (int i = 0; i < pWidth; i++) {
			for (int j = 0; j < pHeight; j++) {
				recoveredImage[j][i] = pData[j][i];
			}
		}
	}
}
