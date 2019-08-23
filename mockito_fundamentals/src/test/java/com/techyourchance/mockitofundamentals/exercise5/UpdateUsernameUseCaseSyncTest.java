package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResult;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.EndpointResultStatus;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    // Constants
    private final String USER_ID = "user_id";
    private final String USER_NAME = "username";
    // System under test
    private UpdateUsernameUseCaseSync sut;
    // Mocks
    private UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    private UsersCache usersCacheMock;
    private EventBusPoster eventBusPosterMock;

    @Before
    public void setUp() throws Exception {

        updateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);

        sut = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock,
                eventBusPosterMock);

        setupSuccess();
    }

    @Test
    public void updateUsername_success_userIdAndUsernamePassedToEndpoint() throws Exception {

        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);

        sut.updateUsernameSync(USER_ID, USER_NAME);

        verify(updateUsernameHttpEndpointSyncMock, times(1)).updateUsername(ac.capture(), ac.capture());
        assertThat(USER_ID, is(ac.getAllValues().get(0)));
        assertThat(USER_NAME, is(ac.getAllValues().get(1)));
    }

    @Test
    public void updateUsername_success_userCached() {

        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);

        sut.updateUsernameSync(USER_ID, USER_NAME);

        verify(usersCacheMock).cacheUser(ac.capture());
        User user = ac.getValue();
        assertThat(user.getUserId(), is(USER_ID));
        assertThat(user.getUsername(), is(USER_NAME));
    }

    @Test
    public void updateUsername_generalError_userNotCached() throws Exception {
        setupGeneralError();
        sut.updateUsernameSync(USER_ID, USER_NAME);
        verifyZeroInteractions(usersCacheMock);
    }

    @Test
    public void updateUsername_authError_userNotCached() throws Exception {
        setupAuthError();
        sut.updateUsernameSync(USER_ID, USER_NAME);
        verifyZeroInteractions(usersCacheMock);
    }

    @Test
    public void updateUsername_serverError_userNotCached() throws Exception {
        setupServerError();
        sut.updateUsernameSync(USER_ID, USER_NAME);
        verifyZeroInteractions(usersCacheMock);
    }

    @Test
    public void updateUsername_success_loggedInEventPosted() {

        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);

        sut.updateUsernameSync(USER_ID, USER_NAME);

        verify(eventBusPosterMock).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsername_generalError_noInteractionWithEventBusPoster() throws Exception {
        setupGeneralError();
        sut.updateUsernameSync(USER_ID, USER_NAME);
        verifyZeroInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsername_authError_noInteractionWithEventBusPoster() throws Exception {
        setupAuthError();
        sut.updateUsernameSync(USER_ID, USER_NAME);
        verifyZeroInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsername_serverError_noInteractionWithEventBusPoster() throws Exception {
        setupServerError();
        sut.updateUsernameSync(USER_ID, USER_NAME);
        verifyZeroInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUsername_success_successReturned() {
        UseCaseResult result = sut.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsername_serverError_failureReturned() throws Exception {
        setupServerError();
        UseCaseResult result = sut.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_authError_failureReturned() throws Exception {
        setupAuthError();
        UseCaseResult result = sut.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_generalError_failureReturned() throws Exception {
        setupGeneralError();
        UseCaseResult result = sut.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_networkError_networkErrorReturned() throws Exception {
        setupNetworkError();
        UseCaseResult result = sut.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    // Helper methods
    private void setupSuccess() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, USER_NAME));
    }

    private void setupGeneralError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void setupAuthError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void setupServerError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void setupNetworkError() throws NetworkErrorException {
        doThrow(new NetworkErrorException()).when(updateUsernameHttpEndpointSyncMock)
                .updateUsername(any(String.class), any(String.class));
    }
}