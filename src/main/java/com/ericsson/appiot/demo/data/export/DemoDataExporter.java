package com.ericsson.appiot.demo.data.export;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.ericsson.appiot.demo.data.export.controller.DataListController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@SuppressWarnings({ "unchecked", "rawtypes", "restriction"})
public class DemoDataExporter extends Application {
	
	private static DemoDataExporter instance;
	private static ApplicationProperties properties = new ApplicationProperties(); 
	private static Stage stage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static ApplicationProperties getProperties() {
		return properties;
	}
	
	public void start(final Stage stage) {
		
		instance = this;
		this.stage = stage;
		try {
			InputStream in = getClass().getResourceAsStream("/application.properties");
			properties.load(in);
			in.close();
			
			//Show login window
			URL url = getClass().getResource("/Login.fxml");
			Pane loginPane = FXMLLoader.load(url);
			Scene scene = new Scene(loginPane, 1024, 768);
		    
	        stage.setTitle("DEMO Data Exporter");
	        stage.setScene(scene);
	        stage.show();			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Sets the authentication token to be used by the application when calling Client API.
	 * @param token The token to use.
	 */
	public static void setAuthenticationToken(String token) {
		FXMLLoader fxmlLoader = new FXMLLoader(instance.getClass().getResource("/DataList.fxml")); 
		Parent root;
		try {
			root = (Parent)fxmlLoader.load();
			Scene scene = new Scene(root, 1024, 768); 
			
			stage.setScene(scene);   
			stage.show();
			
			DataListController controller = fxmlLoader.<DataListController>getController();
			controller.setAuthorizationToken(token);

		} catch (IOException e) {
			e.printStackTrace();
		}    
	}
}
