package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {

    // Constants
    public static final String USER_ID = "userId";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";
    // Fields
    private FetchUserProfileUseCaseSync sut;
    private UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;
    private UsersCacheTd usersCacheTd;

    @Before
    public void setUp() {

        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        usersCacheTd = new UsersCacheTd();

        sut = new FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd, usersCacheTd);
    }

    // Test methods

    @Test
    public void fetchUserProfileSync_success_userIdPassedToEndpoint() {
        sut.fetchUserProfileSync(USER_ID);
        assertThat(userProfileHttpEndpointSyncTd.getUserId(), is(USER_ID));
    }

    @Test
    public void fetchUserProfileSync_success_userCached() {
        userProfileHttpEndpointSyncTd.resetErrors();
        sut.fetchUserProfileSync(USER_ID);
        User userCached = usersCacheTd.getUser(USER_ID);
        assertThat(userCached.getUserId(), is(USER_ID));
        assertThat(userCached.getFullName(), is(FULL_NAME));
        assertThat(userCached.getImageUrl(), is(IMAGE_URL));
    }

    @Test
    public void fetchUserProfileSync_generalError_userNotCached() {
        userProfileHttpEndpointSyncTd.resetErrors();
        userProfileHttpEndpointSyncTd.setGeneralError(true);
        sut.fetchUserProfileSync(USER_ID);
        User userCached = usersCacheTd.getUser(USER_ID);
        assertNull(userCached);
    }

    @Test
    public void fetchUserProfileSync_authError_userNotCached() {
        userProfileHttpEndpointSyncTd.resetErrors();
        userProfileHttpEndpointSyncTd.setAuthError(true);
        sut.fetchUserProfileSync(USER_ID);
        User userCached = usersCacheTd.getUser(USER_ID);
        assertNull(userCached);
    }

    @Test
    public void fetchUserProfileSync_serverError_userNotCached() {
        userProfileHttpEndpointSyncTd.resetErrors();
        userProfileHttpEndpointSyncTd.setServerError(true);
        sut.fetchUserProfileSync(USER_ID);
        User userCached = usersCacheTd.getUser(USER_ID);
        assertNull(userCached);
    }

    @Test
    public void fetchUserProfileSync_success_successReturned() {
        userProfileHttpEndpointSyncTd.resetErrors();
        UseCaseResult result = sut.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchUserProfileSync_generalError_failureReturned() {
        userProfileHttpEndpointSyncTd.resetErrors();
        userProfileHttpEndpointSyncTd.setGeneralError(true);
        UseCaseResult result = sut.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_authError_failureReturned() {
        userProfileHttpEndpointSyncTd.resetErrors();
        userProfileHttpEndpointSyncTd.setAuthError(true);
        UseCaseResult result = sut.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_serverError_failureReturned() {
        userProfileHttpEndpointSyncTd.resetErrors();
        userProfileHttpEndpointSyncTd.setServerError(true);
        UseCaseResult result = sut.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfileSync_networkError_networkErrorReturned() {
        userProfileHttpEndpointSyncTd.resetErrors();
        userProfileHttpEndpointSyncTd.setNetworkError(true);
        UseCaseResult result = sut.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    // Helper classes

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        // Fields
        private String userId;
        private boolean isGeneralError;
        private boolean isAuthError;
        private boolean isServerError;
        private boolean isNetworkError;

        void resetErrors() {
            isGeneralError = false;
            isAuthError = false;
            isServerError = false;
            isNetworkError = false;
        }

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            this.userId = userId;

            EndpointResult result;

            if (isGeneralError) {
                result = new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            }
            else if (isAuthError) {
                result = new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            }
            else if (isServerError) {
                result = new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            }
            else if (isNetworkError) {
                throw new NetworkErrorException();
            }
            else {
                result = new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }

            return result;
        }

        String getUserId() {
            return userId;
        }

        void setGeneralError(boolean generalError) {
            isGeneralError = generalError;
        }

        void setAuthError(boolean authError) {
            isAuthError = authError;
        }

        void setServerError(boolean serverError) {
            isServerError = serverError;
        }

        void setNetworkError(boolean networkError) {
            isNetworkError = networkError;
        }
    }

    private static class UsersCacheTd implements UsersCache {

        // Fields
        private ArrayList<User> users;

        UsersCacheTd() {
            users = new ArrayList<>();
        }

        @Override
        public void cacheUser(User user) {

            User existingUser = getUser(user.getUserId());

            if (existingUser != null) {
                users.remove(existingUser);
            }

            users.add(user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {

            int pos = 0;
            while (pos < users.size() && !users.get(pos).getUserId().equals(userId)) {
                pos++;
            }

            return pos < users.size() ? users.get(pos) : null;
        }
    }
}