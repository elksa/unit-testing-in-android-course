package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncProdTest {

    // region Constants
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final User USER = new User(USER_ID, USER_NAME);
    // endregion

    // region Helper fields
    private FetchUserUseCaseSyncProd sut;
    private FetchUserHttpEndpointSyncMyTestDouble fetchUserHttpEndpointSyncMyTestDouble;
    @Mock
    private UsersCache usersCacheMock;
    // endregion

    @Before
    public void setUp() {
        fetchUserHttpEndpointSyncMyTestDouble = new FetchUserHttpEndpointSyncMyTestDouble();
        sut = new FetchUserUseCaseSyncProd(fetchUserHttpEndpointSyncMyTestDouble, usersCacheMock);

        prepareUserNotInCache();
        prepareEndpointSuccessResponse();
    }

    @Test
    public void fetchUserSync_notInCache_userIdPassedToEndpoint() {
        // Arrange
        // Act
         sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(fetchUserHttpEndpointSyncMyTestDouble.getUserId(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_notInCache_userIdPassedToCache() {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        sut.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_successStatus() {
        // Arrange
        // Act
         UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_userFetched() {
        // Arrange
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUserSync_userFetchedFromServer_userStoredInCache() {
        // Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        // Act
        sut.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock).cacheUser(ac.capture());
        assertThat(ac.getValue(), is(USER));
    }

    @Test
    public void fetchUserSync_userCached_cachedUserReturned() {
        // Arrange
        prepareUserInCache();
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
        verify(usersCacheMock).getUser(ac.capture());
        assertThat(ac.getValue(), is(USER_ID));
        assertThat(result.getUser(), is(USER));
    }

    @Test
    public void fetchUserSync_userCached_endpointNotPolled() {
        // Arrange
        prepareUserInCache();
        // Act
        sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(fetchUserHttpEndpointSyncMyTestDouble.getRequestCount(), is(0));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointGeneralError_failureReturned() {
        // Arrange
        prepareEndpointGeneralErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointGeneralError_nothingCached() {
        // Arrange
        prepareEndpointGeneralErrorResponse();
        // Act
        sut.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointGeneralError_nullReturned() {
        // Arrange
        prepareEndpointGeneralErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertNull(result.getUser());
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_failureReturned() {
        // Arrange
        prepareEndpointAuthErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_nothingCached() {
        // Arrange
        prepareEndpointAuthErrorResponse();
        // Act
        sut.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_nullReturned() {
        // Arrange
        prepareEndpointAuthErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertNull(result.getUser());
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_failureReturned() {
        // Arrange
        prepareEndpointServerErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_nothingCached() {
        // Arrange
        prepareEndpointServerErrorResponse();
        // Act
        sut.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_nullReturned() {
        // Arrange
        prepareEndpointServerErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertNull(result.getUser());
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_netWorkErrorReturned() {
        // Arrange
        prepareEndpointNetworkErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_nothingCached() {
        // Arrange
        prepareEndpointNetworkErrorResponse();
        // Act
        sut.fetchUserSync(USER_ID);
        // Assert
        verify(usersCacheMock, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_nullReturned() {
        // Arrange
        prepareEndpointNetworkErrorResponse();
        // Act
        UseCaseResult result = sut.fetchUserSync(USER_ID);
        // Assert
        assertNull(result.getUser());
    }

    // region Helper classes
    class FetchUserHttpEndpointSyncMyTestDouble implements FetchUserHttpEndpointSync {

        private String userId;
        private boolean authError;
        private boolean generalError;
        private boolean serverError;
        private boolean networkError;
        private int requestCount;

        void setAuthError(boolean authError) {
            this.authError = authError;
        }

        void setGeneralError(boolean generalError) {
            this.generalError = generalError;
        }

        int getRequestCount() {
            return requestCount;
        }

        void setNetworkError(boolean networkError) {
            this.networkError = networkError;
        }

        public boolean isServerError() {
            return serverError;
        }

        void setServerError(boolean serverError) {
            this.serverError = serverError;
        }

        String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @Override
        public EndpointResult fetchUserSync(String userId) throws NetworkErrorException {

            EndpointResult result;

            this.userId = userId;
            requestCount++;

            if (authError) {
                result = new EndpointResult(EndpointStatus.AUTH_ERROR, null, null);
            } else if (serverError || generalError) {
                result = new EndpointResult(EndpointStatus.GENERAL_ERROR, null, null);
            } else if (networkError) {
                throw new NetworkErrorException();
            } else {
                result = new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USER_NAME);
            }

            return result;
        }
    }
    // endregion

    // region Helper methods
    private void prepareUserNotInCache() {
        when(usersCacheMock.getUser(any(String.class))).thenReturn(null);
    }

    private void prepareUserInCache() {
        when(usersCacheMock.getUser(USER_ID)).thenReturn(USER);
    }

    private void prepareEndpointSuccessResponse() {
        // endpoint test double is set up for success by default; this method is for clarity of intent
    }

    private void prepareEndpointAuthErrorResponse() {
        fetchUserHttpEndpointSyncMyTestDouble.setAuthError(true);
    }

    private void prepareEndpointGeneralErrorResponse() {
        fetchUserHttpEndpointSyncMyTestDouble.setGeneralError(true);
    }

    private void prepareEndpointServerErrorResponse() {
        fetchUserHttpEndpointSyncMyTestDouble.setServerError(true);
    }

    private void prepareEndpointNetworkErrorResponse() {
        fetchUserHttpEndpointSyncMyTestDouble.setNetworkError(true);
    }
    // endregion
}