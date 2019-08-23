package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncProd implements FetchUserUseCaseSync {

    // region Fields
    private FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private UsersCache usersCache;
    // endregion

    public FetchUserUseCaseSyncProd(FetchUserHttpEndpointSync fetchUserHttpEndpointSync,
                                    UsersCache usersCache) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {

        UseCaseResult result;
        User user = usersCache.getUser(userId);

        if (user == null) {
            try {
                EndpointResult endpointResult = fetchUserHttpEndpointSync.fetchUserSync(userId);

                if (endpointResult.getStatus() == EndpointStatus.AUTH_ERROR || endpointResult.getStatus() == EndpointStatus.GENERAL_ERROR) {
                    result = new UseCaseResult(Status.FAILURE, null);
                } else {
                    User endpointUser = new User(endpointResult.getUserId(), endpointResult.getUsername());
                    result = new UseCaseResult(Status.SUCCESS, endpointUser);
                    usersCache.cacheUser(endpointUser);
                }
            } catch (NetworkErrorException e) {
                result = new UseCaseResult(Status.NETWORK_ERROR, null);
            }
        } else {
            result = new UseCaseResult(Status.SUCCESS, user);
        }

        return result;
    }
}
