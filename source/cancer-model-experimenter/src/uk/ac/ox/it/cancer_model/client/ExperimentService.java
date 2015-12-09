package uk.ac.ox.it.cancer_model.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("experiment")
public interface ExperimentService extends RemoteService {
    String experimentServer(long numberOfReplicates,
	                    String email,
	                    String startTime,
	                    ArrayList<String> parameterNames,
	                    ArrayList<Double> parameterValues,
	                    HashMap<String, String> serverFiles,
	                    String host) throws IllegalArgumentException;
}
