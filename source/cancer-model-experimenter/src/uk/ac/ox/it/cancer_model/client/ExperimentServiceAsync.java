package uk.ac.ox.it.cancer_model.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ExperimentServiceAsync {
    void experimentServer(String email, String startTime, ArrayList<String> parameterNames, ArrayList<Double> parameterValues, AsyncCallback<String> callback)
	    throws IllegalArgumentException;
}
