package uk.ac.ox.it.cancer_model.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface ExperimentService extends RemoteService {
    String experimentServer(String name) throws IllegalArgumentException;
}
