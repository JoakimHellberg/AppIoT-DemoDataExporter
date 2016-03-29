package com.ericsson.appiot.demo.data.export.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.ericsson.appiot.demo.data.export.ApplicationProperties;
import com.ericsson.appiot.demo.data.export.DemoDataExporter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import se.sigma.sensation.client.sdk.AuthenticationException;
import se.sigma.sensation.client.sdk.AuthenticationManager;

public class LoginController implements Initializable {
	@FXML private Text actiontarget;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {

		try {
			
			String username = usernameField.getText();
			String password = passwordField.getText();
			ApplicationProperties properties = DemoDataExporter.getProperties();
			
			String authorizationToken = AuthenticationManager.getAuthorizationToken(
					properties.getTenant(), properties.getResourceId(), properties.getClientId(), username, password);
			DemoDataExporter.setAuthenticationToken(authorizationToken);
		} catch (AuthenticationException e) {
			actiontarget.setText(e.getMessage());
		}
    }
}
