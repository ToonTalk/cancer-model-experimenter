package uk.ac.ox.it.cancer_model.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface ExperimentService extends RemoteService {
    String experimentServer(String email, String startTime, ArrayList<String> parameterNames, ArrayList<Double> parameterValues) throws IllegalArgumentException;
}
