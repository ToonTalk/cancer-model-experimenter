package uk.ac.ox.it.cancer_model.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Cancer_model_experimenter implements EntryPoint {
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
	    + "attempting to contact the server. Please check your network "
	    + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side Experiment service.
     */
    private final ExperimentServiceAsync experimentService = GWT
	    .create(ExperimentService.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
	final Button experimentButton = new Button("Run experiment");
	final TextBox emailField = new TextBox();
	emailField.setTitle("Enter your email address if you wish to be notified when the results are ready.");
	final Label errorLabel = new Label();

	// We can add style names to widgets
	experimentButton.addStyleName("sendButton");

	// Add the nameField and sendButton to the RootPanel
	// Use RootPanel.get() to get the entire body element
	RootPanel.get("emailFieldContainer").add(emailField);
	RootPanel.get("experimentButtonContainer").add(experimentButton);
	RootPanel.get("errorLabelContainer").add(errorLabel);

	// Focus the cursor on the email field when the app loads
	emailField.setFocus(true);
	emailField.selectAll();

	// Create the popup dialog box
	final DialogBox dialogBox = new DialogBox();
	dialogBox.setText("Remote Procedure Call");
	dialogBox.setAnimationEnabled(true);
	final Button closeButton = new Button("Close");
	// We can set the id of a widget by accessing its Element
	closeButton.getElement().setId("closeButton");
	final Label textToServerLabel = new Label();
	final HTML serverResponseLabel = new HTML();
	VerticalPanel dialogVPanel = new VerticalPanel();
	dialogVPanel.addStyleName("dialogVPanel");
	dialogVPanel.add(serverResponseLabel);
	dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
	dialogVPanel.add(closeButton);
	dialogBox.setWidget(dialogVPanel);

	// Add a handler to close the DialogBox
	closeButton.addClickHandler(new ClickHandler() {
	    public void onClick(ClickEvent event) {
		dialogBox.hide();
		experimentButton.setEnabled(true);
		experimentButton.setFocus(true);
	    }
	});

	// Create a handler for the sendButton and nameField
	class MyHandler implements ClickHandler, KeyUpHandler {
	    /**
	     * Fired when the user clicks on the sendButton.
	     */
	    public void onClick(ClickEvent event) {
		sendParametersToServer();
	    }

	    /**
	     * Fired when the user types in the nameField.
	     */
	    public void onKeyUp(KeyUpEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
		    sendParametersToServer();
		}
	    }

	    /**
	     * Send the name from the nameField to the server and wait for a response.
	     */
	    private void sendParametersToServer() {
		// First, we validate the input.
		errorLabel.setText("");
		String emailAddress = emailField.getText();
		ArrayList<String> parameterNames = new ArrayList<String>();
		NodeList<Element> sliders = RootPanel.getBodyElement().getElementsByTagName("input");
		for (int i = 0; i < sliders.getLength(); i++) {
		    Node slider = sliders.getItem(i);
		    String id = ((Element) slider).getId();
		    if (!id.isEmpty()) {
			parameterNames.add(id);
		    }
		}
	        ArrayList<Double> parameterValues = new ArrayList<Double>();
	        for (String name : parameterNames) {
	            parameterValues.add(JavaScript.sliderValue(name));
	        };
		// Then, we send the input to the server.
		experimentButton.setEnabled(false);
		textToServerLabel.setText(emailAddress);
		serverResponseLabel.setText("");
		String date = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT).format(new Date());
		experimentService.experimentServer(emailAddress, date, parameterNames, parameterValues,
		        new AsyncCallback<String>() {
			    public void onFailure(Throwable caught) {
			        // Show the RPC error message to the user
			        dialogBox.setText("Failure to communicate with server");
			        serverResponseLabel.addStyleName("serverResponseLabelError");
			        serverResponseLabel.setHTML(SERVER_ERROR);
			        dialogBox.center();
			        closeButton.setFocus(true);
			    }

			    public void onSuccess(String result) {
			        dialogBox.setText("Server replied");
			        serverResponseLabel.removeStyleName("serverResponseLabelError");
			        serverResponseLabel.setHTML(result);
			        dialogBox.center();
			        closeButton.setFocus(true);
			    }
		        });
	    }
	}

	// Add a handler to send the name to the server
	MyHandler handler = new MyHandler();
	experimentButton.addClickHandler(handler);
	emailField.addKeyUpHandler(handler);
    }
}
