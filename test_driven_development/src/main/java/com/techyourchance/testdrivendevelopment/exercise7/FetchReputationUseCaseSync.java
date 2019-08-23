package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;

public class FetchReputationUseCaseSync {

    private GetReputationHttpEndpointSync endpoint;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync endpoint) {
        this.endpoint = endpoint;
    }

    EndpointResult fetchReputationUseCase() {

        EndpointResult result;
        try {
            result = endpoint.getReputationSync();
            if (result.getStatus() == EndpointStatus.GENERAL_ERROR) {
                result.setReputation(0);
            }
        } catch (Exception e) {
            result = new EndpointResult(EndpointStatus.NETWORK_ERROR, 0);
        }

        return result;
    }
}
