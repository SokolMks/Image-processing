import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
 
public class Demo extends Component implements ActionListener {
    
    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************
  
    String descs[] = {
        "Original", 
        "Negative",
        "Scaled",
        "Shifted",
        "Randomised then Scaled and Shifted",
        "Add",
        "Subtract",
        "Multiply",
        "Divide",
        "NOT",
        "AND",
        "OR",
        "XOR",
        "ROI AND",
        "ROI Multiply",
        "ROI NOT",
        "Negative Linear Transform",
        "Logarithic Function",
        "Power-Law",
        "Random Look-Up Table",
        "Bit-Plane Slicing",
        "Histogram Equalization",
        "Averaging",
        "Weighted Averaging",
        "4-Neighbour Laplacian",
        "8-Neighbour Laplacian",
        "4-Neighbour Laplacian Enhancement",
        "8-Neighbour Laplacian Enhancement",
        "Roberts 1",
        "Roberts 2",
        "Sobel X",
        "Sobel Y",
        "Add Salt-Peper Noise",
        "Min Filter",
        "Max Filter",
        "Midpoint Filter",
        "Median Filter",
        "Simple thresholding",
        
    };
 
    int opIndex; 
    int lastOp;

    private BufferedImage bui, bui3, buiFiltered, buiScaled, roi;   // the input image saved as bi;//
    private ArrayList<BufferedImage> buiList, buiFilteredList;
    int w, h;
    
    //Look up tables
    private static int[] LUT = new int[256];
    private double[]  HistogramR = new double[256];
    private double[]  HistogramG = new double[256];
    private double[]  HistogramB = new double[256];
    private double[]  HistogramRNorm = new double[256];
    private double[]  HistogramGNorm = new double[256];
    private double[]  HistogramBNorm = new double[256];
    private double[]  HistogramREq = new double[256];
    private double[]  HistogramGEq = new double[256];
    private double[]  HistogramBEq = new double[256];
    private float[][] Mask = new float[3][3];
    private float[][] aMask = {{1,1,1},{1,1,1},{1,1,1}};
    private float[][] wAMask = {{1,2,1},{2,4,2},{1,2,1}};
    private float[][] fourNLMask = {{0,-1,0},{-1,4,-1},{0,-1,0}};
    private float[][] eightNLMask = {{-1,-1,-1},{-1,8,-1},{-1,-1,-1}};
    private float[][] fourNLEnhancedMask = {{0,-1,0},{-1,5,-1},{0,-1,0}};
    private float[][] eightNLEnhancedMask = {{-1,-1,-1},{-1,9,-1},{-1,-1,-1}};
    private float[][] robertsOneMask = {{0,0,0},{0,0,-1},{0,1,0}};
    private float[][] robertsTwoMask = {{0,0,0},{0,-1,0},{0,0,1}};
    private float[][] sobelXMask = {{-1,0,1},{-2,0,2},{-1,0,1}};
    private float[][] sobelYMask = {{-1,-2,-1},{0,0,0},{1,2,1}};
    
    public Demo() {
        try {
            bui = ImageIO.read(new File("Baboon.bmp"));
            w = bui.getWidth(null);
            h = bui.getHeight(null);
        	
            System.out.println(bui.getType());
            if (bui.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bui2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bui2.getGraphics();
                big.drawImage(bui, 0, 0, null);
                buiFiltered = bui = bui2;
            }
        } catch (IOException e) {      // exception if th image has problem/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }                       
    
    public Demo(String flName) {
        try {
        	//Get file type
        	String splitter = "\\.";
        	String[] nameSplitting = flName.split(splitter);
        	String flType = nameSplitting[nameSplitting.length-1];
        	///
        	if (flType.equals("tiff")) {
        		File image = new File(flName);
        		bui = ImageIO.read(image);
        	} else {
                bui = ImageIO.read(new File(flName));
        	}
        	w = bui.getWidth();
            h = bui.getHeight();

            buiScaled = rescale(bui, 2);
             
            System.out.println(bui.getType());
            if (bui.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bui, 0, 0, null);
                big.drawImage(bui, w, 0, null);
                buiFiltered = bui = bi2;
            }
            
 
            
        } catch (IOException e) {      // exception if th image has a problem/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }   
    
    public Demo(String flName, String flName2) {
        try {
        	//Get file type
        	String splitter = "\\.";
        	String[] nameSplit = flName.split(splitter);
        	String fileType = nameSplit[nameSplit.length-1];
        	if (fileType.equals("tiff")) {
        		File image = new File(flName);
        		bui = ImageIO.read(image);
        	} else {
                bui = ImageIO.read(new File(flName));
        	}
        	bui3 = ImageIO.read(new File(flName2));
        	w = bui.getWidth(null);
            h = bui.getHeight(null);

            roi = ImageIO.read(new File("roi.bmp"));
            System.out.println(bui.getType());
            if (bui.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bui, 0, 0, null);
                big.drawImage(bui, w, 0, null);
                big.drawImage(bui3, 0, h, null);
                big.drawImage(roi, w, h, null);
                buiFiltered = bui = bi2;
            }
            
        } catch (IOException e) {      // exception if th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }   

    public Dimension getPreferredSize() {
        return new Dimension(2*w, 2*h);
    }
 
    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
 
    void setOpIndex(int i) {
        opIndex = i;
    }
//  Repaint will call this function to change image.
    public void paint(Graphics g) { 
        filterImage();      
        g.drawImage(bui, 0, 0, null);
        g.drawImage(bui3, 0, h, null);
        g.drawImage(buiFiltered, w, 0, null);
        g.drawImage(roi, w, h, null);
    }
    	
    //  image to array  
    private static int[][][] convertToArray(BufferedImage image){
      int width = image.getWidth();
      int height = image.getHeight();

      int[][][] result = new int[width][height][4];

      for (int y = 0; y < height; y++) {
         for (int x = 0; x < width; x++) {
            int p = image.getRGB(x,y);
            int a = (p>>24)&0xff;
            int r = (p>>16)&0xff;
            int g = (p>>8)&0xff;
            int b = p&0xff;

            result[x][y][0]=a;
            result[x][y][1]=r;
            result[x][y][2]=g;
            result[x][y][3]=b;
         }
      }
      return result;
    }

    
    //  array to buff image
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];
                
                //set RGB

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }


    //Image Negative
    public BufferedImage ImageNegative(BufferedImage timng){
        int width = timng.getWidth();
        int height = timng.getHeight();

        int[][][] ImageArray = convertToArray(timng);//image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }
        
        return convertToBimage(ImageArray);  //array to BufferedImage
    }


    //************************************
    //  Your turn now:  Add more function below
    //************************************




    //************************************
    //  You need to register your functioin here
    //************************************
    public void filterImage() {
    	String scale;
    	String shift;
    	int q=3;
    	float s=3;
    	boolean validInput = false;
		
        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
        case 0: buiFiltered = bui; /* original */
                return; 
        case 1: buiFiltered = ImageNegative(bui); /* Image Negative */
                return;
        case 2: while (s>2 || s<0) {
	        		try {
	        			scale = JOptionPane.showInputDialog("Enter a scale factor between 0 - 2");
	        			s = Float.parseFloat(scale);
	        		} catch (NumberFormatException e) {
	        			JOptionPane.showMessageDialog(null, "Wrong number, try again");
	         		}
        		}
        		buiFiltered = rescale(bui, (float)s);
        		return;
        case 3: while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a shift value");
		    			q = Integer.parseInt(shift);
		    			validInput =true;
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
				buiFiltered = shift(bui, q);
        		return;
        case 4: while (s>2 || s<0) {
		    		try {
		    			scale = JOptionPane.showInputDialog("Enter a scale factor between 0 - 2");
		    			s = Float.parseFloat(scale);
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a shift value");
		    			q = Integer.parseInt(shift);
		    			validInput =true;
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        buiFiltered = randomShiftAndScale(bui, s, q);
        		return;
        case 5: while (s>2 || s<0) {
		    		try {
		    			scale = JOptionPane.showInputDialog("Enter a scale factor between 0 - 2");
		    			s = Float.parseFloat(scale);
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a shift value");
		    			q = Integer.parseInt(shift);
		    			validInput =true;
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        buiFiltered = add(bui, bui3);
		        buiFiltered = shift(buiFiltered, q);
		        buiFiltered = rescale(buiFiltered, s);
        		return;
        case 6: while (s>2 || s<0) {
		    		try {
		    			scale = JOptionPane.showInputDialog("Enter a scale factor between 0 - 2");
		    			s = Float.parseFloat(scale);
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a shift value");
		    			q = Integer.parseInt(shift);
		    			validInput =true;
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        buiFiltered = subtract(bui, bui3);
		        buiFiltered = shift(buiFiltered, q);
		        buiFiltered = rescale(buiFiltered, s);
        		return;
        case 7: while (s>2 || s<0) {
		    		try {
		    			scale = JOptionPane.showInputDialog("Enter a scale factor between 0 - 2");
		    			s = Float.parseFloat(scale);
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a shift value");
		    			q = Integer.parseInt(shift);
		    			validInput =true;
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        buiFiltered = multiply(bui, bui3);
		        buiFiltered = shift(buiFiltered, q);
		        buiFiltered = rescale(buiFiltered, s);
				return;
        case 8: while (s>2 || s<0) {
		    		try {
		    			scale = JOptionPane.showInputDialog("Enter a scale factor between 0 - 2");
		    			s = Float.parseFloat(scale);
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a shift value");
		    			q = Integer.parseInt(shift);
		    			validInput =true;
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
		        buiFiltered = divide(bui, bui3);
		        buiFiltered = shift(buiFiltered, q);
		        buiFiltered = rescale(buiFiltered, s);
				return;
        case 9: buiFiltered = not(bui);
				return;
        case 10: buiFiltered = and(bui, bui3);
				return;
        case 11: buiFiltered = or(bui, bui3);
    			return;
        case 12: buiFiltered = xor(bui, bui3);
				return;
        case 13: buiFiltered = roiAND(bui, roi);
				return;
        case 14: buiFiltered = roiMultiply(bui, roi);
				return;
        case 15: buiFiltered = roiNOT(bui, roi);
				return;
        case 16: buiFiltered = negLinT(bui);
  				return;
        case 17: buiFiltered = logF(bui);
				return;
        case 18:  while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a power value (between 0.01 and 25)");
		    			s = Float.parseFloat(shift);
		    			if (s>=0.01 && s<=25) {
		    				validInput = true;
		    			}
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
        		buiFiltered = powerLaw(bui, s);
				return;
        case 19: buiFiltered = randLT(bui);
				return;
        case 20:  while (!validInput) {
		    		try {
		    			shift = JOptionPane.showInputDialog("Enter a power value (between 0 and 7)");
		    			q = Integer.parseInt(shift);
		    			if (q>=0 && q<=7) {
		    				validInput = true;
		    			}
		    		} catch (NumberFormatException e) {
		    			JOptionPane.showMessageDialog(null, "Wrong number, try again");
		     		}
				}
        		buiFiltered = bitplaneSlice(bui, q);
				return;
        case 21: buiFiltered = equalizeHistogram(bui);
        		return;
        case 22: buiFiltered = average(bui, bui3);
        		return;
        case 23: buiFiltered = weightedAverage(bui);
				return;
        case 24: buiFiltered = fourNeighbourLaplacian(bui);
				return;
        case 25: buiFiltered = eightNeighbourLaplacian(bui);
			return;
        case 26: buiFiltered = fourNeighbourLaplacianEnhanced(bui);
 				return;
         case 27: buiFiltered = eightNeighbourLaplacianEnhanced(bui);
 				return;
         case 28: buiFiltered = robertsOne(bui);
      			return;
         case 29: buiFiltered = robertsTwo(bui);
      			return;
         case 30: buiFiltered = sobelX(bui);
      			return;
         case 31: buiFiltered = sobelY(bui);
      			return;
         case 32: buiFiltered = saltandpepper(bui);
			return;
         case 33: buiFiltered = minFilter(bui);
  				return;
         case 34: buiFiltered = maxFilter(bui);
  				return;
         case 35: buiFiltered = midpointFilter(bui);
         		return;
         case 36: buiFiltered = medianFilter(bui);
         		return;
         case 37: buiFiltered = simpleThresholding(bui);
         		return;
        }
 
    }
 

 
     public void actionPerformed(ActionEvent e) {
         JComboBox cb = (JComboBox)e.getSource();
         if (cb.getActionCommand().equals("SetFilter")) {
             setOpIndex(cb.getSelectedIndex());
             repaint();
         } else if (cb.getActionCommand().equals("Formats")) {
             String format = (String)cb.getSelectedItem();
             File saveFile = new File("savedimage."+format);
             JFileChooser chooser = new JFileChooser();
             chooser.setSelectedFile(saveFile);
             int rval = chooser.showSaveDialog(cb);
             if (rval == JFileChooser.APPROVE_OPTION) {
                 saveFile = chooser.getSelectedFile();
                 try {
                     ImageIO.write(buiFiltered, format, saveFile);
                 } catch (IOException ex) {
                 }
             }
         }
    };
    
    public BufferedImage simpleThresholding(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);

        int threshV = 150;//variation from 0-255

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int grey_scaled = (ImageArray[x][y][1] + ImageArray[x][y][2] + ImageArray[x][y][3]) / 3;

                if (grey_scaled > threshV) {
                    grey_scaled = 255;
                } else {
                    grey_scaled = 0;
                }

                ImageArray[x][y][1] = grey_scaled;
                ImageArray[x][y][2] = grey_scaled;
                ImageArray[x][y][3] = grey_scaled;

            }
        }

        return convertToBimage(ImageArray);
    }
    
    
    
    
    
    public BufferedImage rescale(BufferedImage originalImage, float s){
    	int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray1 = convertToArray(originalImage); //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(originalImage);  
    	
    	for(int y=0; y<height; y++){
    		for(int x=0; x<width; x++){
    			ImageArray2[x][y][1] =  Math.round((s*(ImageArray1[x][y][1]))); //r
	    		ImageArray2[x][y][2] = Math.round((s*(ImageArray1[x][y][2]))); //g
	    		ImageArray2[x][y][3] = Math.round((s*(ImageArray1[x][y][3]))); //b
	    		if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
	    		if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
	    		if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
	    		if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
	    		if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
	    		if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
    		}
    	}

    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage shift(BufferedImage originalImage, int s){
    	int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray1 = convertToArray(originalImage); //image to array
        int[][][] ImageArray2 = convertToArray(originalImage);  
    	
    	for(int y=0; y<height; y++){
    		for(int x=0; x<width; x++){
    			ImageArray2[x][y][1] =  Math.round(((ImageArray1[x][y][1]+s))); //r
	    		ImageArray2[x][y][2] = Math.round(((ImageArray1[x][y][2]+s))); //g
	    		ImageArray2[x][y][3] = Math.round(((ImageArray1[x][y][3]+s))); //b
	    		if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
	    		if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
	    		if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
	    		if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
	    		if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
	    		if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
    		}
    	}

    	return convertToBimage(ImageArray2);
    }
    public static int randomNum() {
    	int r =(int)( Math.random()*256);
		double posNeg = Math.random();
		if (posNeg>0.5) {
			r = r * -1;
		}
		return r;
    }
    
    public BufferedImage randomShiftAndScale(BufferedImage originalImage, float s, int t) {
    	int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray1 = convertToArray(originalImage);//image to array
        int[][][] ImageArray2 = convertToArray(originalImage); 
        
        for(int y=0; y<height; y++){
        	for(int x=0; x<width; x++){
        		int r1 = randomNum();
        		ImageArray2[x][y][1] = (ImageArray1[x][y][1])+r1; //r
        		int r2 = randomNum();
            	ImageArray2[x][y][2] = (ImageArray1[x][y][2])+r2; //g
            	int r3 = randomNum();
            	ImageArray2[x][y][3] = (ImageArray1[x][y][3])+r3; //b

        	}
        }
        
    	int rmin, bmin, gmin;
    	int rmax, bmax, gmax;
    	rmin = Math.round(s*(ImageArray2[0][0][1]+t)); 
    	rmax = rmin;
    	gmin = Math.round(s*(ImageArray2[0][0][2]+t));
    	gmax = gmin;
    	bmin = Math.round(s*(ImageArray2[0][0][3]+t)); 
    	bmax = bmin;
    	for(int y=0; y<height; y++){
	    	for(int x=0; x<width; x++){
		    	ImageArray2[x][y][1] = Math.round(s*(ImageArray2[x][y][1]+t)); //r
		    	ImageArray2[x][y][2] = Math.round(s*(ImageArray2[x][y][2]+t)); //g
		    	ImageArray2[x][y][3] = Math.round(s*(ImageArray2[x][y][3]+t)); //b
		    	if (rmin>ImageArray2[x][y][1]) { rmin = ImageArray2[x][y][1]; }
		    	if (gmin>ImageArray2[x][y][2]) { gmin = ImageArray2[x][y][2]; }
		    	if (bmin>ImageArray2[x][y][3]) { bmin = ImageArray2[x][y][3]; }
		    	if (rmax<ImageArray2[x][y][1]) { rmax = ImageArray2[x][y][1]; }
		    	if (gmax<ImageArray2[x][y][2]) { gmax = ImageArray2[x][y][2]; }
		    	if (bmax<ImageArray2[x][y][3]) { bmax = ImageArray2[x][y][3]; }
	    	}
	    }
    	for(int y=0; y<height; y++){
	    	for(int x =0; x<width; x++){
	    		if (rmax-rmin ==0) {
	    			ImageArray2[x][y][1]=255*(ImageArray2[x][y][1]-rmin);
	    		} else {
	    			ImageArray2[x][y][1]=255*(ImageArray2[x][y][1]-rmin)/(rmax-rmin);
	    		}
	    		if (gmax-gmin ==0) {
	    			ImageArray2[x][y][2]=255*(ImageArray2[x][y][2]-gmin);
	    		} else {
	    			ImageArray2[x][y][2]=255*(ImageArray2[x][y][2]-gmin)/(gmax-gmin);
	    		}
	    		if (bmax-bmin ==0) {
	    			ImageArray2[x][y][3]=255*(ImageArray2[x][y][3]-bmin);
	    		} else {
	    			ImageArray2[x][y][3]=255*(ImageArray2[x][y][3]-bmin)/(bmax-bmin);
	    		}
	    	}
	    }
    	
    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage add(BufferedImage originalImage, BufferedImage image2){
    	int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray1 = convertToArray(originalImage); //image to array
        int[][][] ImageArray2 = convertToArray(image2);  
    	
        for(int y=0; y<height; y++){
	    	for(int x=0; x<width; x++){
		    	ImageArray2[x][y][1] = ImageArray1[x][y][1] + ImageArray2[x][y][1];
		    	ImageArray2[x][y][2] = ImageArray1[x][y][2] + ImageArray2[x][y][2];
		    	ImageArray2[x][y][3] = ImageArray1[x][y][3] + ImageArray2[x][y][3];
	    		if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
	    		if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
	    		if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
	    		if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
	    		if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
	    		if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
		    }
	    }
    	
    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage subtract(BufferedImage originalImage, BufferedImage image2){
    	int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray1 = convertToArray(originalImage);//image to array
        int[][][] ImageArray2 = convertToArray(image2);  
    	
        for(int y=0; y<height; y++){
	    	for(int x=0; x<width; x++){
		    	ImageArray2[x][y][1] = Math.abs(ImageArray1[x][y][1] - ImageArray2[x][y][1]);
		    	ImageArray2[x][y][2] = Math.abs(ImageArray1[x][y][2] - ImageArray2[x][y][2]);
		    	ImageArray2[x][y][3] = Math.abs(ImageArray1[x][y][3] - ImageArray2[x][y][3]);
	    		if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
	    		if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
	    		if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
	    		if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
	    		if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
	    		if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
	    		ImageArray2[x][y][1] = (ImageArray2[x][y][1] + 255)/2;
		    	ImageArray2[x][y][2] = (ImageArray2[x][y][2] + 255)/2;
		    	ImageArray2[x][y][3] = (ImageArray2[x][y][3] + 255)/2;
	    		
	    	}
	    }
        
    	
    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage multiply(BufferedImage originalImage, BufferedImage image2){
    	int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray1 = convertToArray(originalImage);//image to array
        int[][][] ImageArray2 = convertToArray(image2);  
    	
        for(int y=0; y<height; y++){
	    	for(int x=0; x<width; x++){
		    	ImageArray2[x][y][1] = ImageArray1[x][y][1] * ImageArray2[x][y][1];
		    	ImageArray2[x][y][2] = ImageArray1[x][y][2] * ImageArray2[x][y][2];
		    	ImageArray2[x][y][3] = ImageArray1[x][y][3] * ImageArray2[x][y][3];
	    		if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
	    		if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
	    		if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
	    		if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
	    		if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
	    		if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
		    }
	    }
    	
    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage divide(BufferedImage originalImage, BufferedImage image2){
    	int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray1 = convertToArray(originalImage);//image to array
        int[][][] ImageArray2 = convertToArray(image2);  
    	
        for(int y=0; y<height; y++){
	    	for(int x=0; x<width; x++){
		    	ImageArray2[x][y][1] = ImageArray1[x][y][1] / ImageArray2[x][y][1];
		    	ImageArray2[x][y][2] = ImageArray1[x][y][2] / ImageArray2[x][y][2];
		    	ImageArray2[x][y][3] = ImageArray1[x][y][3] / ImageArray2[x][y][3];
	    		if (ImageArray2[x][y][1]<0) { ImageArray2[x][y][1] = 0; }
	    		if (ImageArray2[x][y][2]<0) { ImageArray2[x][y][2] = 0; }
	    		if (ImageArray2[x][y][3]<0) { ImageArray2[x][y][3] = 0; }
	    		if (ImageArray2[x][y][1]>255) { ImageArray2[x][y][1] = 255; }
	    		if (ImageArray2[x][y][2]>255) { ImageArray2[x][y][2] = 255; }
	    		if (ImageArray2[x][y][3]>255) { ImageArray2[x][y][3] = 255; }
		    }
	    }
    	
    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage not(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage);// image to array
        int[][][] ImageArray2 = convertToArray(originalImage);
    	int r, b, g;
        for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1]; //r
	    		g = ImageArray1[x][y][2]; //g
	    		b = ImageArray1[x][y][3]; //b
	    		ImageArray2[x][y][1] = (~r)&0xFF; //r
	    		ImageArray2[x][y][2] = (~g)&0xFF; //g
	    		ImageArray2[x][y][3] = (~b)&0xFF; //b
	    	}
    	}
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage and(BufferedImage originalImage, BufferedImage image2) {
    	int[][][] ImageArray1 = convertToArray(originalImage); //image to array
        int[][][] ImageArray2 = convertToArray(image2);
    	int r, b, g;
        for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1] & ImageArray2[x][y][1]; //r
	    		g = ImageArray1[x][y][2]  & ImageArray2[x][y][2]; //g
	    		b = ImageArray1[x][y][3]  & ImageArray2[x][y][3]; //b
	    		ImageArray2[x][y][1] = r; //r
	    		ImageArray2[x][y][2] = g; //g
	    		ImageArray2[x][y][3] = b; //b
	    	}
    	}
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage or(BufferedImage originalImage, BufferedImage image2) {
    	int[][][] ImageArray1 = convertToArray(originalImage);// image to array
        int[][][] ImageArray2 = convertToArray(image2);
    	int r, b, g;
        for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1] | ImageArray2[x][y][1]; //r
	    		g = ImageArray1[x][y][2]  | ImageArray2[x][y][2]; //g
	    		b = ImageArray1[x][y][3]  | ImageArray2[x][y][3]; //b
	    		ImageArray2[x][y][1] = r; //r
	    		ImageArray2[x][y][2] = g; //g
	    		ImageArray2[x][y][3] = b; //b
	    	}
    	}
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage xor(BufferedImage originalImage, BufferedImage image2) {
    	int[][][] ImageArray1 = convertToArray(originalImage);// image to array
        int[][][] ImageArray2 = convertToArray(image2);
    	int r, b, g;
        for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1] ^ ImageArray2[x][y][1]; //r
	    		g = ImageArray1[x][y][2]  ^ ImageArray2[x][y][2]; //g
	    		b = ImageArray1[x][y][3]  ^ ImageArray2[x][y][3]; //b
	    		ImageArray2[x][y][1] = r; //r
	    		ImageArray2[x][y][2] = g; //g
	    		ImageArray2[x][y][3] = b; //b
	    	}
    	}
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage roiAND(BufferedImage originalImage, BufferedImage image2) {
    	int[][][] ImageArray1 = convertToArray(originalImage);// image to array
        int[][][] ImageArray2 = convertToArray(image2);
    	int r, b, g;
        for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1] & ImageArray2[x][y][1]; //r
	    		g = ImageArray1[x][y][2]  & ImageArray2[x][y][2]; //g
	    		b = ImageArray1[x][y][3]  & ImageArray2[x][y][3]; //b
	    		ImageArray2[x][y][1] = r; //r
	    		ImageArray2[x][y][2] = g; //g
	    		ImageArray2[x][y][3] = b; //b
	    	}
    	}
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage roiMultiply(BufferedImage originalImage, BufferedImage image2) {
    	int[][][] ImageArray1 = convertToArray(originalImage);// image to array
        int[][][] ImageArray2 = convertToArray(image2);
    	int r, b, g;
        for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
    			if (ImageArray2[x][y][1] == 255) {r = ImageArray1[x][y][1] * 1;} else {r = ImageArray1[x][y][1] *  0;}  //r
    			if (ImageArray2[x][y][2] == 255) {g = ImageArray1[x][y][2] * 1;} else {g = ImageArray1[x][y][2] *  0;}  //g
    			if (ImageArray2[x][y][2] == 255) {b = ImageArray1[x][y][3] * 1;} else {b = ImageArray1[x][y][3] *  0;}  //b
	    		ImageArray2[x][y][1] = r; //r
	    		ImageArray2[x][y][2] = g; //g
	    		ImageArray2[x][y][3] = b; //b
	    	}
    	}
        
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage roiNOT(BufferedImage originalImage, BufferedImage image2) {
    	int[][][] ImageArray1 = convertToArray(originalImage);// image to array
        int[][][] ImageArray2 = convertToArray(not(image2));
    	int r, b, g;
        for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1] & ImageArray2[x][y][1]; //r
	    		g = ImageArray1[x][y][2]  & ImageArray2[x][y][2]; //g
	    		b = ImageArray1[x][y][3]  & ImageArray2[x][y][3]; //b
	    		ImageArray2[x][y][1] = r; //r
	    		ImageArray2[x][y][2] = g; //g
	    		ImageArray2[x][y][3] = b; //b
	    	}
    	}
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage negLinT(BufferedImage originalImage) {
    	for(int k=0; k<=255; k++){
    		LUT[k] = 256-1-k;
    	}
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage);
    	int r, b, g;
    	for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1]; //r
	    		g = ImageArray1[x][y][2]; //g
	    		b = ImageArray1[x][y][3]; //b
	    		ImageArray2[x][y][1] = LUT[r]; //r
	    		ImageArray2[x][y][2] = LUT[g]; //g
	    		ImageArray2[x][y][3] = LUT[b]; //b
	    	}
    	}
		return convertToBimage(ImageArray2);
    }
    
    public BufferedImage logF(BufferedImage originalImage) {
    	for(int k=0; k<=255; k++){
    		LUT[k] = (int)(Math.log(1+k)*255/Math.log(256));
    	}
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage);
    	int r, b, g;
    	for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1]; //r
	    		g = ImageArray1[x][y][2]; //g
	    		b = ImageArray1[x][y][3]; //b
	    		ImageArray2[x][y][1] = LUT[r]; //r
	    		ImageArray2[x][y][2] = LUT[g]; //g
	    		ImageArray2[x][y][3] = LUT[b]; //b
	    	}
    	}
		return convertToBimage(ImageArray2);
    }
    
    public BufferedImage powerLaw(BufferedImage originalImage, float m) {
    	for(int k=0; k<=255; k++){
    		LUT[k] = (int)(Math.pow(255,1-m)*Math.pow(k,m));
    	}
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage);
    	int r, b, g;
    	for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1]; //r
	    		g = ImageArray1[x][y][2]; //g
	    		b = ImageArray1[x][y][3]; //b
	    		ImageArray2[x][y][1] = LUT[r]; //r
	    		ImageArray2[x][y][2] = LUT[g]; //g
	    		ImageArray2[x][y][3] = LUT[b]; //b
	    	}
    	}
		return convertToBimage(ImageArray2);
    }
    
    public BufferedImage randLT(BufferedImage originalImage) {
    	for(int k=0; k<=255; k++){
    		LUT[k] = (int)(Math.random() * 256);
    	}
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage);
    	int r, b, g;
    	for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1]; //r
	    		g = ImageArray1[x][y][2]; //g
	    		b = ImageArray1[x][y][3]; //b
	    		ImageArray2[x][y][1] = LUT[r]; //r
	    		ImageArray2[x][y][2] = LUT[g]; //g
	    		ImageArray2[x][y][3] = LUT[b]; //b
	    	}
    	}
		return convertToBimage(ImageArray2);
    }
    
    public BufferedImage bitplaneSlice(BufferedImage originalImage, int k) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage);
    	int r, g, b;
    	for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		r = ImageArray1[x][y][1]; //r
	    		g = ImageArray1[x][y][2]; //g
	    		b = ImageArray1[x][y][3]; //b
	    		ImageArray2[x][y][1] = (r>>k); //r
	    		ImageArray2[x][y][2] = (g>>k); //g
	    		ImageArray2[x][y][3] = (b>>k); //b
    		}
    	}

		return convertToBimage(ImageArray2);
    }
    
    public void createHistogram(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	for(int k=0; k<=255; k++){ // Initialisation
    		HistogramR[k] = 0;
    		HistogramG[k] = 0;
    		HistogramB[k] = 0;
    	}
    	int r, b, g;
    	for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
    			r = ImageArray1[x][y][1]; //r
    			g = ImageArray1[x][y][2]; //g
    			b = ImageArray1[x][y][3]; //b
    			HistogramR[r]++;
    			HistogramG[g]++;
    			HistogramB[b]++;
    		}
    	}
    }
    
    public void normaliseHistogram(BufferedImage originalImage) {
    	createHistogram(originalImage);
    	for(int k=0; k<=255; k++){ // Initialisation
    		HistogramRNorm[k] = HistogramR[k]/(w*h);
    		HistogramGNorm[k] = HistogramG[k]/(w*h);
    		HistogramBNorm[k] = HistogramB[k]/(w*h);
    	}
    }
    
    public BufferedImage equalizeHistogram(BufferedImage originalImage) {
    	normaliseHistogram(originalImage);
    	HistogramREq[0] = HistogramRNorm[0];
    	HistogramGEq[0] = HistogramGNorm[0];
    	HistogramBEq[0] = HistogramBNorm[0];
    	for(int k=1; k<=255; k++){ // Initialisation
    		HistogramREq[k] = HistogramREq[k-1] + HistogramRNorm[k];
    		HistogramGEq[k] = HistogramGEq[k-1] + HistogramGNorm[k];
    		HistogramBEq[k] = HistogramBEq[k-1] + HistogramBNorm[k];
    	}
    	for(int k=0; k<=255; k++){ 
    		HistogramREq[k] = Math.round(HistogramREq[k] * 255);
    		HistogramGEq[k] = Math.round(HistogramGEq[k] * 255);
    		HistogramBEq[k] = Math.round(HistogramBEq[k] * 255);
    	}
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	for(int y=0; y<h; y++){
    		for(int x=0; x<w; x++){
	    		ImageArray1[x][y][1] = (int) HistogramREq[ImageArray1[x][y][1]]; //r
	    		ImageArray1[x][y][2] = (int) HistogramGEq[ImageArray1[x][y][2]]; //g
	    		ImageArray1[x][y][3] = (int) HistogramBEq[ImageArray1[x][y][3]]; //b
	    	}
    	}
        return convertToBimage(ImageArray1);
    }
    
    public BufferedImage average(BufferedImage originalImage, BufferedImage bi2) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = aMask;
    	float total= 0;
    	for (int row = 0; row < 3; row ++) {
            for (int col = 0; col < 3; col++) {
                total = total + Mask[row][col];
            }
    	}
    	int[][][] ImageArray3 = convertToArray(bi2);
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
//	    		System.out.println(r + " " + g + " " + b);
	    		ImageArray2[x][y][1] = (int)(Math.round(r/9)); //r
	    		ImageArray2[x][y][2] = (int)(Math.round(g/9)); //g
	    		ImageArray2[x][y][3] = (int)(Math.round(b/9)); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		int red, blue, green;
	    		red = ImageArray2[x][y][1] - ImageArray3[x][y][1];
	    		green = ImageArray2[x][y][2] - ImageArray3[x][y][2];
	    		blue = ImageArray2[x][y][3] - ImageArray3[x][y][3];
	    		System.out.println(red + " " + green + " " + blue);
    		}
    	}
    	System.out.println(total);
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage weightedAverage(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = wAMask;
    	float total= 0;
    	for (int row = 0; row < 3; row ++) {
            for (int col = 0; col < 3; col++) {
                total = total + Mask[row][col];
            }
    	}
       	int[][][] ImageArray3 = convertToArray(bui3);
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(Math.round(r/total)); //r
	    		ImageArray2[x][y][2] = (int)(Math.round(g/total)); //g
	    		ImageArray2[x][y][3] = (int)(Math.round(b/total)); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		int red, blue, green;
	    		red = ImageArray2[x][y][1] - ImageArray3[x][y][1];
	    		green = ImageArray2[x][y][2] - ImageArray3[x][y][2];
	    		blue = ImageArray2[x][y][3] - ImageArray3[x][y][3];
	    		System.out.println(red + " " + green + " " + blue);
    		}
    	}
    	System.out.println(total);
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage fourNeighbourLaplacian(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = fourNLMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
      
    public BufferedImage eightNeighbourLaplacian(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = eightNLMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage fourNeighbourLaplacianEnhanced(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = fourNLEnhancedMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
      
    public BufferedImage eightNeighbourLaplacianEnhanced(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = eightNLEnhancedMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage robertsOne(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = robertsOneMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage robertsTwo(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = robertsTwoMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage sobelX(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = sobelXMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage sobelY(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	
    	Mask = sobelYMask;
    	
    	float r, g, b;
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		r = 0; g = 0; b = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		r = r + Mask[1-s][1-t]*ImageArray1[x+s][y+t][1]; //r
			    		g = g + Mask[1-s][1-t]*ImageArray1[x+s][y+t][2]; //g
			    		b = b + Mask[1-s][1-t]*ImageArray1[x+s][y+t][3]; //b
		    		}
		    		
	    		}
	    		ImageArray2[x][y][1] = (int)(r); //r
	    		ImageArray2[x][y][2] = (int)(g); //g
	    		ImageArray2[x][y][3] = (int)(b); //b
	    		System.out.println(ImageArray2[x][y][1] + " " + ImageArray2[x][y][2]+ " " + ImageArray2[x][y][3]);
	    		
    		}
    	}
    	return rescale(convertToBimage(ImageArray2), 1);
    }
    
    public BufferedImage saltandpepper(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int[][][] ImageArray2 = convertToArray(originalImage);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double random = Math.random() * 1;

                if (random < 0.05) {
                    ImageArray2[x][y][0] = 255;
                    ImageArray2[x][y][1] = 0;
                    ImageArray2[x][y][2] = 0;
                    ImageArray2[x][y][3] = 0;
                } else if (random > 0.95) {
                    ImageArray2[x][y][0] = 255;
                    ImageArray2[x][y][1] = 255;
                    ImageArray2[x][y][2] = 255;
                    ImageArray2[x][y][3] = 255;
                }
            }
        }
        return convertToBimage(ImageArray2);
    }
    
    public BufferedImage minFilter(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	int k;
    	int[] rWindow = new int[9];
    	int[] gWindow = new int[9];
    	int[] bWindow = new int[9];
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		k = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		rWindow[k] = ImageArray1[x+s][y+t][1]; //r
			    		gWindow[k] = ImageArray1[x+s][y+t][2]; //g
			    		bWindow[k] = ImageArray1[x+s][y+t][3]; //b
			    		k++;
		    		}
	    		}
	    		Arrays.sort(rWindow);
	    		Arrays.sort(gWindow);
	    		Arrays.sort(bWindow);
	    		ImageArray2[x][y][1] = rWindow[0]; //r
	    		ImageArray2[x][y][2] = gWindow[0]; //g
	    		ImageArray2[x][y][3] = bWindow[0]; //b
    		}
    	}
    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage maxFilter(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	int k;
    	//3x3
    	int[] rWindow = new int[9];
    	int[] gWindow = new int[9];
    	int[] bWindow = new int[9];
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		k = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		rWindow[k] = ImageArray1[x+s][y+t][1]; //r
			    		gWindow[k] = ImageArray1[x+s][y+t][2]; //g
			    		bWindow[k] = ImageArray1[x+s][y+t][3]; //b
			    		k++;
		    		}
	    		}
	    		Arrays.sort(rWindow);
	    		Arrays.sort(gWindow);
	    		Arrays.sort(bWindow);
	    		ImageArray2[x][y][1] = rWindow[8]; //r
	    		ImageArray2[x][y][2] = gWindow[8]; //g
	    		ImageArray2[x][y][3] = bWindow[8]; //b
    		}
    	}
    	return convertToBimage(ImageArray2);
    }
    
    
    
    
    
    public BufferedImage midpointFilter(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	int k;
    	//3x3
    	int[] rWindow = new int[9];
    	int[] gWindow = new int[9];
    	int[] bWindow = new int[9];
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		k = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		rWindow[k] = ImageArray1[x+s][y+t][1]; //r
			    		gWindow[k] = ImageArray1[x+s][y+t][2]; //g
			    		bWindow[k] = ImageArray1[x+s][y+t][3]; //b
			    		k++;
		    		}
	    		}
	    		Arrays.sort(rWindow);
	    		Arrays.sort(gWindow);
	    		Arrays.sort(bWindow);
	    		ImageArray2[x][y][1] = (rWindow[0] + rWindow[8])/2; //r
	    		ImageArray2[x][y][2] = (gWindow[0] + gWindow[8])/2; //g
	    		ImageArray2[x][y][3] = (bWindow[0] + bWindow[8])/2; //b
    		}
    	}
    	return convertToBimage(ImageArray2);
    }
    
    public BufferedImage medianFilter(BufferedImage originalImage) {
    	int[][][] ImageArray1 = convertToArray(originalImage); 
    	int[][][] ImageArray2 = convertToArray(originalImage); 
    	int k;
    	//3x3
    	int[] rWindow = new int[9];
    	int[] gWindow = new int[9];
    	int[] bWindow = new int[9];
    	for(int y=1; y<h-1; y++){
    		for(int x=1; x<w-1; x++){
	    		k = 0;
	    		for(int s=-1; s<=1; s++){
		    		for(int t=-1; t<=1; t++){
			    		rWindow[k] = ImageArray1[x+s][y+t][1]; //r
			    		gWindow[k] = ImageArray1[x+s][y+t][2]; //g
			    		bWindow[k] = ImageArray1[x+s][y+t][3]; //b
			    		k++;
		    		}
	    		}
	    		Arrays.sort(rWindow);
	    		Arrays.sort(gWindow);
	    		Arrays.sort(bWindow);
	    		ImageArray2[x][y][1] = rWindow[4]; //r
	    		ImageArray2[x][y][2] = gWindow[4]; //g
	    		ImageArray2[x][y][3] = bWindow[4]; //b
    		}
    	}
    	return convertToBimage(ImageArray2);
    }
    
    public static void main(String s[]) {    	
        JFrame f = new JFrame("Image Processing Demo");
        JPanel pane = new JPanel();
        f.add(pane);
        
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        Demo de = new Demo("BaboonRGB.tif", "LenaRGB.bmp");
        
        pane.add("Center", de);

        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}