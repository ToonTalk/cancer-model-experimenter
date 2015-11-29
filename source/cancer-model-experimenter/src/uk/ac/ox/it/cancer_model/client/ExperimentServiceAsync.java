package uk.ac.ox.it.cancer_model.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ExperimentServiceAsync {
    void experimentServer(String input, AsyncCallback<String> callback)
	    throws IllegalArgumentException;
}
