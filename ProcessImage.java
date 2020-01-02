package application;

import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp; //Convert color to gray

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;

import java.io.File;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


import java.math.*;

import java.util.Scanner;

public class ProcessImage extends JPanel
{
	
	public double maxValue = -1;
	public int row = 1, column = 1;

	
	//Read image in
	public BufferedImage getImage(String fileUrl) throws IOException
	{

		File f = new File(fileUrl);
		return ImageIO.read(f);
	}
	
	
	
	//Convert color image to gray scale
    public BufferedImage toGray(BufferedImage img) {
        if (img.getType() == BufferedImage.TYPE_BYTE_GRAY) 
        {
            return img;
        } 
        else 
        {
            //Convert to gray scale
            BufferedImage grayImage = new BufferedImage(img.getWidth(),
                    img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
                    .filter(img, grayImage);
            return grayImage;
        }
    }
    
    
    
    //Get gray value of each pixel
    public int getGrayValue(BufferedImage img, int x, int y)
    {
    	int grayValue = img.getRGB(x, y) & 0xFF;
    	return grayValue;
    	
    	/** Another way to get gray value
    	Color c = new Color(img.getRGB(x, y));
    	int r = c.getRed();
    	int g = c.getGreen();
    	int b = c.getBlue();
    	
    	int grayValue = (r + g + b) / 3;
    	*/

    }
    
    
    
    //Cross Correlation
    public double crossCorrelation(BufferedImage finalImage, int imgPartOf1Data[][], double avg_A, 
    		int xStart, int yStart, int w, int h)
    {
    	
    	//Get subimage starting from different points
    	BufferedImage imgPartOf2 = finalImage.getSubimage(xStart, yStart, w, h); //(x_start, y_start, width, height)
    	int[][] imgPartOf2Data = new int[w][h];
    	
    	int sum_B = 0;
    	double avg_B;
    	
    	
    	//Get the average gray value of image B
    	for (int x = 0; x < w; x++)
    	{
    		for (int y = 0; y < h; y++)
    		{
    			imgPartOf2Data[x][y] = this.getGrayValue(imgPartOf2, x, y);
    	    	sum_B += imgPartOf2Data[x][y];  
    		}
    	}
    	
    	avg_B = sum_B / (w * h);
	
    	
    	double numerator = 0.0;
    	double denominator = 0.0;
    	double sqr_A = 0.0, sqr_B = 0.0;
    	
    	for (int x = 0; x < w; x++)
    	{
    		for (int y = 0; y < h; y++)
    		{
    	    	numerator += (imgPartOf1Data[x][y] - avg_A) * (imgPartOf2Data[x][y] - avg_B);
    	    	
    	    	sqr_A += Math.pow(imgPartOf1Data[x][y] - avg_A, 2);

    	    	sqr_B += Math.pow(imgPartOf2Data[x][y] - avg_B, 2);
    		}
    	}
    	
    	denominator = Math.sqrt(sqr_A * sqr_B);
    	
    	double corr = numerator / denominator;
    	return corr;
    	
    }
    
    

    //Get the coordinate of the point with max cross correlation value
    public ProcessImage getMaxPoint(double[][] corrArray, double maxValue)
    {
    	ProcessImage location = new ProcessImage();
				
    	for (int x = 1; x < corrArray.length; x++)
    	{
    		for (int y = 1; y < corrArray[1].length; y++)
    		{
    			if (corrArray[x][y] == maxValue)
    			{
    				location.maxValue = corrArray[x][y];
    				location.row = x;
    				location.column = y;

    			}
    		}
    	}
    	return location;
    }
    
    
    
    //Get the max cross correlation value
    public double getMaxValue(double[][] corrArray)
    {
    	double maxValue = corrArray[1][1];
    	System.out.println("max corr value: " +maxValue);
    	
    	for (int x = 1; x < corrArray.length; x++)
    	{
    		for (int y = 1; y < corrArray[1].length; y++)
    		{
    			if (corrArray[x][y] > maxValue)
    			{
    				maxValue = corrArray[x][y];
    			}  
    		}
    	}
    	    	
    	return maxValue;
    }
    
    
    
    //Process image stitching
	public int[][] imgStitchingProcess(BufferedImage sourceImg1, BufferedImage sourceImg2) throws Exception
	{
		
		ProcessImage obj = new ProcessImage();
		
		//Convert to GrayScale
		BufferedImage img1 = obj.toGray(sourceImg1);
		BufferedImage img2 = obj.toGray(sourceImg2); 
				
		//Get the width and height
		int w1 = img1.getWidth();
		int w2 = img2.getWidth();
						
		int h1 = img1.getHeight(); 
		int h2 = img2.getHeight(); 
		
		
		//Calculate the width and height for the image for comparison
		int w_test = 50;
					
		int w_new = w_test * 2 + w2;
		int h_new = h1 * 2 + h2;    

		
		//Draw the image for comparison
		BufferedImage expandImage = new BufferedImage(w_new, h_new, BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = expandImage.createGraphics();
						
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w_new, h_new); 
		g.drawImage(img2, w_test, h1, null); //(draw which img, x_start, y_start, ?do nothing if null)
		g.dispose();

		
		//Get subimage from img1
		BufferedImage imgPartOf1 = img1.getSubimage(w1-w_test, 0, w_test, h1); //(x_start, y_start, width, height)
	
		
		//Save the gray value for subimg1
		int[][] imgPartOf1Data = new int[w_test][h1];
		int sum_A = 0;
						
		for (int x = 0; x < w_test; x++)
		{
			for (int y = 0; y < h1; y++)
			{
				imgPartOf1Data[x][y] = obj.getGrayValue(imgPartOf1, x, y);
				sum_A += imgPartOf1Data[x][y];
			}
		}

		double avg_A;
				
		avg_A = sum_A / (w_test * h1);
		    	

		//Save cross correlation value to a two dimensional array
		double[][] corrArray = new double[w_new - w_test][h_new - h1];
		double corr;
				

		for (int x = 0; x < w_new - w_test; x++)
		{
			for (int y = 0; y < h_new - h1; y++)
			{
				corrArray[x][y] = obj.crossCorrelation(expandImage, imgPartOf1Data, avg_A, x, y, w_test, h1);
			}
		}

		double maxValue;
				
		maxValue = obj.getMaxValue(corrArray);

		
		int maxLocationX, maxLocationY;
		maxLocationX = obj.getMaxPoint(corrArray, maxValue).row;
		maxLocationY = obj.getMaxPoint(corrArray, maxValue).column;
		
		int[][] maxPoint = new int[1][2];
		
		maxPoint[0][0] = maxLocationX;
		maxPoint[0][1] = maxLocationY;
		
		//System.out.println("The correlation completed.");
		
		return maxPoint;
    
	}
    
}

