package wrd.ibw.utils;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import javax.imageio.ImageIO;
import JSci.maths.ArrayMath;

import java.awt.*;

public class Util {
	private static File imageFie = null;
	private static int matrixWidth = 0;  //corresponding to the x - values
	private static int matrixHeight = 0; //corresponding to the y - values
	private static int pixelMatrix[][];
	private static Vector<Integer> vector = new Vector<>();
	
	public static void clearImage(){
		vector.clear();
		pixelMatrix = null;
	}
	
	public static int[][] getImageMatrix(){
		return pixelMatrix;
	}
	
	private static void getMatrixFromImageFile(File imgFile) throws Exception{
		try {
			imageFie = imgFile;
			BufferedImage imgBuffered = ImageIO.read(imageFie);
			Raster raster = imgBuffered.getData();
			matrixWidth = raster.getWidth(); 
			matrixHeight = raster.getHeight();
			pixelMatrix = new int[matrixHeight][matrixWidth];
			for (int x = 0; x < matrixWidth; x++){
		        for(int y = 0; y < matrixHeight; y++){
		        	pixelMatrix[y][x] = raster.getSample(x,y,0);
		        }
		    }
		} catch (Exception e) {
			throw e;
		}
	}
		
	public static void defineImageArray(File imgFile)throws Exception{
		Util.getMatrixFromImageFile(imgFile);
		for(int y = 0; y < matrixHeight; y++){
			for (int x = 0; x < matrixWidth; x++){
	        	vector.add(pixelMatrix[y][x]);
	        }
	    }
	}
	
	public static Vector<Integer> getImageVector(){
		return Util.vector;
	}
	
	public static int getImageWidth(){
		return Util.matrixWidth;
	}
	
	public static int getImageHeight(){
		return Util.matrixHeight;
	}
	
	
	public static Image getImageFromArray(int[] pixels, int width, int height) {
        try {
	        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        WritableRaster raster = (WritableRaster) image.getData();
	        //raster.setPixels(0,0,width,height,pixels);
	        raster.setPixels(0,0,3,3,pixels);
	        return image;
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
    }
	
	public static String minimizeVariation(String originalBinary, String changedBinary, int lsb_pos, int pos){
		String newBinary = changedBinary;
		
		char main_char = '0';
		char replace_char = '1';
		int change_counter = 0;
		
		if(changedBinary.charAt(changedBinary.length()-lsb_pos)=='1'){
			main_char = '1';
			replace_char = '0';
		}
		
		
		if((lsb_pos > 1) && (originalBinary.charAt(originalBinary.length()-lsb_pos) != changedBinary.charAt(changedBinary.length()-lsb_pos))){
			for (int i = changedBinary.length()-lsb_pos+1; i < changedBinary.length(); i++) {
				if(changedBinary.charAt(i) == main_char){
					newBinary = changedBinary.substring(0,i) + replace_char + changedBinary.substring(i+1,changedBinary.length());
					changedBinary = newBinary;
					change_counter++;
					
					if (pos == change_counter) {
						i = changedBinary.length();
					} 
					//i = changedBinary.length();
				}
			}
		}
		return newBinary;
		
	}
	
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage){
	        return (BufferedImage) img;
	    }
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_BGR);
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    return bimage;
	}
	
	public static double getCorrelation(Integer[] xs, Integer[] ys) {
	    double sx = 0.0;
	    double sy = 0.0;
	    double sxx = 0.0;
	    double syy = 0.0;
	    double sxy = 0.0;

	    int n = xs.length;

	    for(int i = 0; i < n; ++i) {
	      double x = xs[i];
	      double y = ys[i];

	      sx += x;
	      sy += y;
	      sxx += x * x;
	      syy += y * y;
	      sxy += x * y;
	    }
	    double cov = sxy / n - sx * sy / n / n;
	    double sigmax = Math.sqrt(sxx / n -  sx * sx / n / n);
	    double sigmay = Math.sqrt(syy / n -  sy * sy / n / n);
	    return cov / sigmax / sigmay;
	  }
	
	public static double getMyCorrelation(Integer[] pX, Integer[] pY) {
		double[] temp1 = new double[pX.length];
		double[] temp2 = new double[pX.length];
		
		for (int i = 0; i < pY.length; i++) {
			temp1[i] = pX[i];
			temp2[i] = pY[i];
		}
		return ArrayMath.correlation(temp1, temp2);
	  }
	

	public static Vector<Integer> getVectorFromMatrix(int[][] pMatrix, int pWidth, int pHeight)throws Exception{
		Vector<Integer> result = new Vector<Integer>();
		for(int y = 0; y < pHeight; y++){
			for (int x = 0; x < pWidth; x++){
				result.add(pMatrix[y][x]);
	        }
	    }
		
		return result;
	}
	
	
	public static String md5Java(String message){
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
           StringBuilder sb = new StringBuilder(2*hash.length);
           for(byte b : hash){
               sb.append(String.format("%02x", b&0xff));
           }
           digest = sb.toString();
          
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
        	ex.printStackTrace();
        }
        return digest;
    }
	
	public static String fillDif(String pValue){
		if((pValue.equals("0"))||(pValue.equals("-0")))
			return "";
		else return pValue;
	}
	
	
	public static float allowMovement(Vector<Integer> pBoundaries, float pNewValue, float pOldValue){
		float result = pOldValue;
		if ((pNewValue > pBoundaries.get(0))||(pNewValue < pBoundaries.get(1))) {
			result = pNewValue;
		}
		return result;
	}
	
	public static Vector<Integer> getBoundaries(float pValue, Vector<Integer> pElements){
		Vector<Integer> result = new Vector<Integer>(2);
		for (int i = 0; i < pElements.size(); i++) {
			if ((pValue > pElements.get(i))&&(pValue <= pElements.get(i+1))) {
				result.add(pElements.get(i));
				result.add(pElements.get(i+1));
				break;
			}
		}
		
		return result;
	}
	
	public static char[] buildVerifSignal(char[] arr, int pos, int num) {
		String bin_ft = Integer.toBinaryString(num);
		int pos_bin = 0;
		for(int i = (pos-bin_ft.length()); i < pos; i++) {
			arr[i] = bin_ft.charAt(pos_bin);
			pos_bin++;
		}
		return arr;
	}
	
	public static int buildParamVal(Vector<Vector<Integer>> arr, int pos) {
		int start = pos - 8;
		
		int ones = 0;
		int zeros = 0;
		StringBuilder binval = new StringBuilder("000000000000000000000000");
		
		for(int i = start; i < pos; i++) {
			ones = 0;
			zeros = 0;
			
			for(int j = 0; j < pos; j++) {
				if(arr.get(i).get(j) == 1)
					ones++;
				else
					zeros++;
			}
			
			if(ones > zeros)
				binval.setCharAt(i, '1');
			else
				binval.setCharAt(i, '0');
			
		}
		
		return  Integer.parseInt(binval.substring(start,pos),2);
	}
	
}
