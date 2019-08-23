package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    // region Constants
    private final int REPUTATION = 5;
    private final int DEFAULT_REPUTATION = 0;
    // endregion

    // region Helper fields
    private FetchReputationUseCaseSync sut;
    @Mock
    private GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;
    // endregion


    @Before
    public void setUp() throws Exception {
        sut = new FetchReputationUseCaseSync(getReputationHttpEndpointSyncMock);
    }

    @Test
    public void fetchReputationUseCase_completesSuccessfully_successReturned() {
        // Arrange
        setupSuccess();
        // Act
        EndpointResult result = sut.fetchReputationUseCase();
        // Assert
        assertThat(result.getStatus(), is(EndpointStatus.SUCCESS));
    }

    @Test
    public void fetchReputationUseCase_completesSuccessfully_reputationReturned() {
        // Arrange
        setupSuccess();
        // Act
        EndpointResult result = sut.fetchReputationUseCase();
        // Assert
        assertThat(result.getReputation(), is(REPUTATION));
    }

    @Test
    public void fetchReputationUseCase_generalError_generalErrorReturned() {
        // Arrange
        setupGeneralError();
        // Act
        EndpointResult result = sut.fetchReputationUseCase();
        // Assert
        assertThat(result.getStatus(), is(EndpointStatus.GENERAL_ERROR));
    }

    @Test
    public void fetchReputationUseCase_generalError_zeroReputationReturned() {
        // Arrange
        setupGeneralError();
        // Act
        EndpointResult result = sut.fetchReputationUseCase();
        // Assert
        assertThat(result.getReputation(), is(DEFAULT_REPUTATION));
    }

    @Test
    public void fetchReputationUseCase_networkError_networkErrorReturned() {
        // Arrange
        setupNetworkError();
        // Act
        EndpointResult result = sut.fetchReputationUseCase();
        // Assert
        assertThat(result.getStatus(), is(EndpointStatus.NETWORK_ERROR));
    }

    @Test
    public void fetchReputationUseCase_networkError_zeroReputationReturned() {
        // Arrange
        setupNetworkError();
        // Act
        EndpointResult result = sut.fetchReputationUseCase();
        // Assert
        assertThat(result.getReputation(), is(DEFAULT_REPUTATION));
    }

    //region Helper methods

    private void setupSuccess() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new EndpointResult(EndpointStatus.SUCCESS, REPUTATION));
    }

    private void setupGeneralError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, REPUTATION));
    }

    private void setupNetworkError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync()).thenThrow(new RuntimeException("Network error"));
    }

    // endregion
}