package application;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.application.Application;

import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp; //Convert color to gray

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.Raster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.math.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.ArrayList;
import java.io.*;




public class MainController {

	@FXML
	private Button btnUpload;
	
	@FXML
	private Button btn2;
	
	@FXML
	private Button btnStitch;
	
	@FXML
	private Button btnDownload;
	
	@FXML
	private Button btnsave;
	
	@FXML
	private Button btnviewresult;
	
	@FXML
	private Button btnopenwindow;
	
	@FXML
	private ListView listview;
	
	@FXML
	private ImageView imageview;
	
	@FXML
	private ImageView imageviewload;
	
	@FXML
	private ImageView imageviewsave;
	
	@FXML
	private Label labelstatus;
	
	@FXML
	private int numOfImgs = 0;
	
	@FXML
	private int imgPosition;
	
	@FXML
	private ArrayList<BufferedImage> arrImgs;
	
	@FXML
	private ArrayList<String> arrPath = new ArrayList<String>(2);
	
	@FXML
	private ProcessImage obj;
	
	@FXML
	private BufferedImage bgFinal;

	
	
	
	//Load images
	@FXML
	public void ButtonUploadAction(ActionEvent event) throws IOException 
	{
		
		FileChooser fileChooser = new FileChooser();
		
		//Set extension filter
		FileChooser.ExtensionFilter extFilterPNG =
				new FileChooser.ExtensionFilter("PNG file (*.PNG)", "*.PNG");
		FileChooser.ExtensionFilter extFilterpng =
				new FileChooser.ExtensionFilter("png file (*.png)", "*.png");
		
		FileChooser.ExtensionFilter extFilterJPG =
				new FileChooser.ExtensionFilter("JPG file (*.JPG)", "*.JPG");
		FileChooser.ExtensionFilter extFilterjpg =
				new FileChooser.ExtensionFilter("jpg file (*.jpg)", "*.jpg");

		FileChooser.ExtensionFilter extFilterGIF =
				new FileChooser.ExtensionFilter("GIF file (*.GIF)", "*.GIF");
		FileChooser.ExtensionFilter extFiltergif =
				new FileChooser.ExtensionFilter("gif file (*.gif)", "*.gif");
		
		fileChooser.getExtensionFilters().addAll(extFilterPNG, extFilterpng, extFilterJPG, extFilterjpg, extFilterGIF, extFiltergif);
		
		
		//Shows a new file open dialog
		File selectedFile = fileChooser.showOpenDialog(null);
		
		//Display the selected image in Image View
		if (selectedFile != null)
		{
			String path = selectedFile.getAbsolutePath();
			Image image = new Image(selectedFile.toURI().toString());	
			imageview.setImage(image);
			
			//Count the number of images loaded
			numOfImgs += 1;			
			imgPosition = numOfImgs - 1;
			
			//Save the path of image to ArrayList arrPath
			ProcessImage obj = new ProcessImage();
			BufferedImage sourceImg = obj.getImage(path);
			
			arrPath.add(path);

		}
		
		//If no valid file is chosen
		else
		{
			System.out.println("File is not valid.");
		}

	}
	
	
	
	@FXML
	public int getImgPosition() 
	{
		return imgPosition;
	}
	
	
	
	//Show previous image
	@FXML
	public void ButtonPrevAction(ActionEvent event) throws FileNotFoundException 
	{
		
		this.getImgPosition();
		
		if (imgPosition != 0)
		{
			
			FileInputStream input = new FileInputStream(arrPath.get(imgPosition-1));			
			Image image = new Image(input);
			imageview.setImage(image);
			
			imgPosition -= 1;
			
		}
	}

	
	
	//Show next image
	@FXML
	public void ButtonNextAction(ActionEvent event) throws FileNotFoundException 
	{
		
		this.getImgPosition();
		
		if (imgPosition < arrPath.size()-1)
		{
			
			FileInputStream input = new FileInputStream(arrPath.get(imgPosition+1));
			Image image = new Image(input);
			imageview.setImage(image);
			
			imgPosition += 1;
			
		}
	}
	
	
	
	//Get arrPath
	@FXML
	public ArrayList<String> getArrPath()
	{
		return arrPath;
	}
	
	
	
	//Get output image
	@FXML
	public BufferedImage getFinalImg()
	{
		return bgFinal;
	}
	

	
	//Image Stitching
	@FXML
	public void ButtonStitchAction(ActionEvent event) throws Exception 
	{
		
		this.getArrPath();
		
		//Store the coordinates of correlation max value points
		ProcessImage obj = new ProcessImage();
		
		int[][] locationCoords = new int[numOfImgs][2]; 
		
		locationCoords[0][0] = 0;
		locationCoords[0][1] = 0;
		
		int count = 0;
		
		for (int i = 1; i < numOfImgs; i++)
		{

			BufferedImage sourceImg1 = obj.getImage(arrPath.get(i-1));
			BufferedImage sourceImg2 = obj.getImage(arrPath.get(i));
				
			int[][] maxPoint = new int[1][2];
				
			maxPoint = obj.imgStitchingProcess(sourceImg1, sourceImg2);
				
			locationCoords[i][0] = maxPoint[0][0];
			locationCoords[i][1] = maxPoint[0][1];
			
			count += 1;

		}
		
		
		//Draw the final output image
		int finalWidth = 0;
		int finalHeight = 0;
		
		for (int i = 0; i < numOfImgs; i++)
		{
			BufferedImage sourceImg = obj.getImage(arrPath.get(i));
			int w = sourceImg.getWidth();
			int h = sourceImg.getHeight();
			
			finalWidth += w;
			
			System.out.println("final width: " + finalWidth);
			
			if (finalHeight < h)
			{
				finalHeight = h;
			}
		}
		
		finalHeight += 100;

		
		BufferedImage bgFinal = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = bgFinal.createGraphics();
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, finalWidth, finalHeight); 
		
		
		int x_shift = 0;
		int y_shift = 0;
		
		int w = 0;
		int h = 0;
		
		int x_start = 0;
		int y_start = (int)Math.ceil((finalHeight/2)-(obj.getImage(arrPath.get(0)).getHeight()/2));
		

		for (int i = 0; i < numOfImgs; i++)
		{
			
			x_shift = locationCoords[i][0];
			y_shift = locationCoords[i][1];
			
			x_start += w - x_shift;
			y_start += h - y_shift;
			
			BufferedImage sourceImg = obj.getImage(arrPath.get(i));
			w = sourceImg.getWidth();
			h = sourceImg.getHeight();	
				
			g2d.drawImage(sourceImg, x_start, y_start, w, h, obj);			

		}

		g2d.dispose();
		

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save image to...");
		File outputFile = fileChooser.showSaveDialog(null);
		
		if (outputFile != null) 
		{
			try 
			{
				ImageIO.write(bgFinal, "png", outputFile);
			}
			
			catch (IOException e) 
			{
				e.printStackTrace();
			}			
		}
		
		System.out.println("Image stitching completed.");

	}
	
	
	@FXML
	public void ButtonOpenWindowAction(ActionEvent event)
	{
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("/application/Output.fxml"));
			
			Scene outputScene = new Scene(fxmlLoader.load());
			
			Stage outputStage = new Stage();
			
			outputStage.setTitle("Save Image to File");
			outputStage.setScene(outputScene);
			outputStage.show();
		}
			
		catch (IOException e) {
			e.printStackTrace();
		}
	}


}
