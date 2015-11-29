package uk.ac.ox.it.cancer_model.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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
		String textToServer = emailField.getText();

		// Then, we send the input to the server.
		experimentButton.setEnabled(false);
		textToServerLabel.setText(textToServer);
		serverResponseLabel.setText("");
		experimentService.experimentServer(textToServer,
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
