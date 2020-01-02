package application;
	
import javafx.application.Application;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import javax.swing.JFrame;
import javax.swing.JPanel;

import javax.imageio.ImageIO;

import java.awt.image.IndexColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp; 
import java.awt.image.Raster;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;



public class ImageStitchingProject extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception, MalformedURLException 
	{	

		Parent root = FXMLLoader.load(getClass().getResource("/application/ImageStitchingMain.fxml"));
		
		Scene scene = new Scene(root); 
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		primaryStage.setTitle("Image Stitching");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	} 
	

	public static void main(String[] args)
	{
		launch(args);
	}
	
}
