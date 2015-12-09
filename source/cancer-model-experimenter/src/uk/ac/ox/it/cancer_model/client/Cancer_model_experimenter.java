package uk.ac.ox.it.cancer_model.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
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
    private final ExperimentServiceAsync experimentService = GWT.create(ExperimentService.class);
    
    /**
     * a map from local file to server file names
     */
    
    private static HashMap<String, String> serverFiles = new HashMap<String, String>();

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
		errorLabel.setText("");
		String emailAddress = emailField.getText();
		ArrayList<String> parameterNames = new ArrayList<String>();
		NodeList<Element> sliders = RootPanel.getBodyElement().getElementsByTagName("input");
		long numberOfReplicates = 16;
		for (int i = 0; i < sliders.getLength(); i++) {
		    Node slider = sliders.getItem(i);
		    String id = ((Element) slider).getId();
		    if (!id.isEmpty()) {
			parameterNames.add(id);
		    }
		}
	        ArrayList<Double> parameterValues = new ArrayList<Double>();
	        for (String name : parameterNames) {
	            double sliderValue = JavaScript.sliderValue(name);
		    parameterValues.add(sliderValue);
		    if (name.equals("number-of-replicates")) {
			numberOfReplicates = Math.round(sliderValue);
		    }
	        };
		// Then, we send the input to the server.
		experimentButton.setEnabled(false);
		textToServerLabel.setText(emailAddress);
		serverResponseLabel.setText("");
		String date = DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT).format(new Date());
		String host = Window.Location.getHost();
		experimentService.experimentServer(numberOfReplicates,
			                           emailAddress, 
			                           date,
			                           parameterNames,
			                           parameterValues,
			                           serverFiles,
			                           host,
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
	addFileUploadWidget();
    }
    
    public static void addFileUploadWidget() {
	    // Create a FormPanel and point it at a service.
	    // based on http://www.gwtproject.org/javadoc/latest/com/google/gwt/user/client/ui/FileUpload.html
	    final FormPanel form = new FormPanel();
	    form.setAction(GWT.getModuleBaseURL() + "/uploadFileHandler");

	    // Because we're going to add a FileUpload widget, we'll need to set the
	    // form to use the POST method, and multipart MIME encoding.
	    form.setEncoding(FormPanel.ENCODING_MULTIPART);
	    form.setMethod(FormPanel.METHOD_POST);

	    // Create a panel to hold all of the form widgets.
	    VerticalPanel panel = new VerticalPanel();
	    form.setWidget(panel);

	    panel.add(new Label("Use this to upload any customisations of the default settings. Any of mutations.txt, input.txt, or regulatoryGraph.html."));

	    // Create a FileUpload widget.
	    FileUpload upload = new FileUpload();
	    upload.setName("uploadFormElement");
	    panel.add(upload);

	    // Add a 'submit' button.
	    panel.add(new Button("Submit", new ClickHandler() {
	      public void onClick(ClickEvent event) {
	        form.submit();
	      }
	    }));

	    // Add an event handler to the form.
	    form.addSubmitHandler(new FormPanel.SubmitHandler() {
	      public void onSubmit(SubmitEvent event) {
	        // This event is fired just before the form is submitted. We can take
	        // this opportunity to perform validation.
//		  System.out.println(event);
	      }
	    });
	    form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
	      public void onSubmitComplete(SubmitCompleteEvent event) {
	        // When the form submission is successfully completed, this event is
	        // fired. Assuming the service returned a response of type text/html,
	        // we can get the result text here (see the FormPanel documentation for
	        // further explanation).
	        String response = event.getResults();
	        String[] parts = response.split("file-name-token");
	        serverFiles.put(parts[1], parts[2]);
	    }});

	    RootPanel.get().add(form);
	  }
}
