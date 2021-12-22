package wrd.ibw;

import wrd.ibw.gui.FrmMain;


public class Run {
	private static FrmMain frmDB = null;
	
	public static void main(String[] args) {
		
		if(frmDB == null){
			frmDB = new FrmMain();
			frmDB.setLocationRelativeTo(null);
		}
		frmDB.setVisible(true);
	}

}
