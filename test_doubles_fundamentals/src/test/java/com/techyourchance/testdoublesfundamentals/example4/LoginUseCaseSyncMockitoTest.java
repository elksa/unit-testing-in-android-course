package com.techyourchance.testdoublesfundamentals.example4;

import com.techyourchance.testdoublesfundamentals.example4.LoginUseCaseSync.UseCaseResult;
import com.techyourchance.testdoublesfundamentals.example4.authtoken.AuthTokenCache;
import com.techyourchance.testdoublesfundamentals.example4.eventbus.EventBusPoster;
import com.techyourchance.testdoublesfundamentals.example4.eventbus.LoggedInEvent;
import com.techyourchance.testdoublesfundamentals.example4.networking.LoginHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class LoginUseCaseSyncMockitoTest {

    // Constants
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String AUTH_TOKEN = "authToken";

    // Mocks
    private LoginHttpEndpointSync loginHttpEndpointSyncMock;
    private AuthTokenCache authTokenCacheMock;
    private EventBusPoster eventBusPosterMock;

    // System under test
    private LoginUseCaseSync sut;

    @Before
    public void setup() throws Exception {

        loginHttpEndpointSyncMock = mock(LoginHttpEndpointSync.class);
        authTokenCacheMock = mock(AuthTokenCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);

        sut = new LoginUseCaseSync(loginHttpEndpointSyncMock, authTokenCacheMock, eventBusPosterMock);

        setupSuccess();
    }

    @Test
    public void loginSync_success_usernameAndPasswordPassedToEndpoint() throws Exception {

        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);

        sut.loginSync(USERNAME, PASSWORD);

        // Verifies that the method "loginSync" from the mock class was called exactly 1 time
        // Argument captor captures parameters the method was called with
        verify(loginHttpEndpointSyncMock, times(1)).loginSync(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USERNAME));
        assertThat(captures.get(1), is(PASSWORD));
    }

    @Test
    public void loginSync_success_authTokenCached() {

        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);

        sut.loginSync(USERNAME, PASSWORD);

        // Only one argument assumes the method is called once
        verify(authTokenCacheMock).cacheAuthToken(ac.capture());
        assertThat(AUTH_TOKEN, is(ac.getValue()));
    }

    @Test
    public void loginSync_generalError_authTokenNotCached() throws Exception {
        setupGeneralError();
        sut.loginSync(USERNAME, PASSWORD);
        verifyNoMoreInteractions(authTokenCacheMock);
    }

    @Test
    public void loginSync_authError_authTokenNotCached() throws Exception {
        setupAuthError();
        sut.loginSync(USERNAME, PASSWORD);
        verifyNoMoreInteractions(authTokenCacheMock);
    }

    @Test
    public void loginSync_serverError_authTokenNotCached() throws Exception {
        setupServerError();
        sut.loginSync(USERNAME, PASSWORD);
        verifyNoMoreInteractions(authTokenCacheMock);
    }

    @Test
    public void loginSync_success_loggedInEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        sut.loginSync(USERNAME, PASSWORD);
        verify(eventBusPosterMock).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(LoggedInEvent.class)));
    }

    @Test
    public void loginSync_generalError_noInteractionWithEventBusPoster() throws Exception {
        setupGeneralError();
        sut.loginSync(USERNAME, PASSWORD);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void loginSync_authError_noInteractionWithEventBusPoster() throws Exception {
        setupAuthError();
        sut.loginSync(USERNAME, PASSWORD);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void loginSync_serverError_noInteractionWithEventBusPoster() throws Exception {
        setupServerError();
        sut.loginSync(USERNAME, PASSWORD);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void loginSync_success_successReturned() {
        UseCaseResult result = sut.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void loginSync_serverError_failureReturned() throws Exception {
        setupServerError();
        UseCaseResult result = sut.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_authError_failureReturned() throws Exception {
        setupAuthError();
        UseCaseResult result = sut.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_generalError_failureReturned() throws Exception {
        setupGeneralError();
        UseCaseResult result = sut.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_networkError_networkErrorReturned() throws Exception {
        setupNetworkError();
        UseCaseResult result = sut.loginSync(USERNAME, PASSWORD);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    // Helper methods

    private void setupSuccess() throws Exception {
        when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.SUCCESS, AUTH_TOKEN));
    }

    private void setupGeneralError() throws Exception {
        when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, ""));
    }

    private void setupAuthError() throws Exception {
        when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, ""));
    }

    private void setupServerError() throws Exception {
        when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, ""));
    }

    private void setupNetworkError() throws Exception {
        doThrow(new NetworkErrorException()).when(loginHttpEndpointSyncMock).loginSync(any(String.class), any(String.class));
    }
}