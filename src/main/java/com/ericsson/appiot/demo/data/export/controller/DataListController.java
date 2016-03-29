package com.ericsson.appiot.demo.data.export.controller;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.ericsson.appiot.demo.data.export.DemoDataExporter;
import com.ericsson.appiot.demo.data.export.model.DeviceModel;
import com.ericsson.appiot.demo.data.export.model.DeviceNetworkModel;
import com.ericsson.appiot.demo.data.export.model.TagModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import se.sigma.sensation.client.sdk.ClientManager;
import se.sigma.sensation.client.sdk.SensationException;
import se.sigma.sensation.client.sdk.dto.DeviceNetworkResponse;
import se.sigma.sensation.client.sdk.dto.PagedResponse;
import se.sigma.sensation.client.sdk.dto.SensorCollectionResponse;
import se.sigma.sensation.client.sdk.dto.SensorListResponse;
import se.sigma.sensation.client.sdk.dto.TagResponse;

public class DataListController implements Initializable {
	private ClientManager clientApi;
	@FXML private ComboBox<DeviceNetworkModel> deviceNetworkCB;
	@FXML private VBox tagsPanel;
	@FXML private TableView<DeviceModel> tableView;
	@FXML private BorderPane borderPane;
	private ObservableList<TagModel> tagList = FXCollections.observableArrayList();
	private ObservableList<DeviceModel> deviceList = FXCollections.observableArrayList();
	private ObservableList<DeviceNetworkModel> deviceNetworkList = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    public void initialize(URL url, ResourceBundle rb) {
		TableColumn<DeviceModel, String> nameCol = new TableColumn<DeviceModel, String>("Name");
        nameCol.setMinWidth(100);
        nameCol.setCellValueFactory(new PropertyValueFactory<DeviceModel, String>("name"));

		TableColumn<DeviceModel, String> tempCol = new TableColumn<DeviceModel, String>("Temperature");
		tempCol.setMinWidth(100);
		tempCol.setCellValueFactory(new PropertyValueFactory<DeviceModel, String>("temperature"));

		TableColumn<DeviceModel, String> humCol = new TableColumn<DeviceModel, String>("Humidity");
		humCol.setMinWidth(100);
		humCol.setCellValueFactory(new PropertyValueFactory<DeviceModel, String>("humidity"));
        
        tableView.getColumns().addAll(nameCol, tempCol, humCol);
        tableView.setItems(deviceList);
        
        borderPane.getCenter().boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
				tableView.setPrefWidth(newValue.getWidth());
			}
		});
    }    
    
    public void handleDeviceNetwork(ActionEvent event) {
    	DeviceNetworkModel selectedDeviceNetwork = deviceNetworkCB.getSelectionModel().getSelectedItem();
    	// ### Call Client API to set device network ###
    	clientApi.setDeviceNetwork(selectedDeviceNetwork.getId());
	    loadTags();
    }
    
	private void updateTagSelection(String tagName, Boolean new_val) {
		deviceList.clear();
		for(TagModel tag : tagList) {
			if(tag.getName().equals(tagName)) {
				tag.setSelected(new_val);
			}
			if(tag.isSelected()) {
				try {
					// ### Call Client API to get sensors by selected tag ###
					PagedResponse<SensorCollectionResponse> sensorCollectionPages = clientApi.getTagManager().getSensorCollectionsByTag(tag.getId());
					if(sensorCollectionPages.getNbrOfRows() > 0) {
						Collection<SensorCollectionResponse> sensorCollections = sensorCollectionPages.getRows();
						for(SensorCollectionResponse sensorCollection : sensorCollections) {
							DeviceModel model = new DeviceModel();
							model.setId(sensorCollection.getId());
							model.setName(sensorCollection.getName());
							// ### Call Client API to get sensors by device id ###
							List<SensorListResponse> sensors = clientApi.getSensorCollectionManager().getSensors(sensorCollection.getId());
							Iterator<SensorListResponse> iterator = sensors.iterator();
							while(iterator.hasNext()) {
								SensorListResponse sensor = iterator.next();
								if(sensor.getLatestMeasurement() != null) {
									double measurement = sensor.getLatestMeasurement().getValues()[0];								
									int typeId = sensor.getSensorTypeTypeId();
									switch(typeId) {
										case 1:
											model.setTemperature(measurement);
											break;
										case 2:
											model.setHumidity(measurement);
											break;
										default:
											break;											
									}
								}
							}
							deviceList.add(model);
						}
					}
				} catch (SensationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
    private void loadTags() {
    	tagsPanel.getChildren().clear();    	
    	try {
    		// ### Call Client API to get available tags ###
			PagedResponse<TagResponse> tagPages = clientApi.getTagManager().getTags();
			if(tagPages.getNbrOfRows() > 0) {
				Collection<TagResponse> tags = tagPages.getRows();
				for(TagResponse tag : tags) {
					TagModel model = new TagModel();
					model.setId(tag.getId());
					model.setName(tag.getName());
					tagList.add(model);
					CheckBox cb = new CheckBox(tag.getName());
					cb.setPadding(new Insets(4));
					cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
				        public void changed(ObservableValue<? extends Boolean> ov,
				            Boolean old_val, Boolean new_val) {
				            updateTagSelection(cb.getText(), new_val);
				        }
				    });
					tagsPanel.getChildren().add(cb);
				}
			}
		} catch (SensationException e) {
			e.printStackTrace();
		}
    }

    public void setAuthorizationToken(String authorizationToken) {
    	// ### Setup the client api manager
    	clientApi = new ClientManager(DemoDataExporter.getProperties().getApiAddress(), authorizationToken);
    	List<DeviceNetworkResponse> deviceNetworks;
		try {
			// ### Call Client API to get available device networks ###
			deviceNetworks = clientApi.getDeviceNetworkManager().getDeviceNetworks();
			for(DeviceNetworkResponse deviceNetwork : deviceNetworks) {
				
				DeviceNetworkModel model = new DeviceNetworkModel();
				model.setId(deviceNetwork.getId());
				model.setName(deviceNetwork.getName());
				deviceNetworkList.add(model);	
			}		
			deviceNetworkCB.setItems(deviceNetworkList);
		} catch (SensationException e) {
			e.printStackTrace();
		}
    }
}
