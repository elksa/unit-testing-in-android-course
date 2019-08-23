package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.FetchContactsUseCase.ContactsListener;
import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    // region Constants
    private static final String FILTER = "filter";
    private static final String ID = "id";
    private static final String FULL_NAME = "full name";
    private static final String IMAGE_URL = "image URL";
    private static final String FULL_PHONE_NUMBER = "fullPhoneNumber";
    private static final int AGE = 23;
    // endregion

    // region Helper fields
    private FetchContactsUseCase sut;
    @Mock
    GetContactsHttpEndpoint getContactsHttpEndpointMock;
    @Mock
    private ContactsListener listener1;
    @Mock
    private ContactsListener listener2;
    @Captor
    private ArgumentCaptor<List<Contact>> acListContact;
    // endregion

    @Before
    public void setUp() {
        sut = new FetchContactsUseCase(getContactsHttpEndpointMock);
    }

    @Test
    public void getContacts_correctParametersPassedToEndpoint() {

        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        sut.fetchContactsAndNotify(FILTER);
        // Assert
        verify(getContactsHttpEndpointMock).getContacts(ac.capture(), any(Callback.class));
        assertThat(ac.getValue(), is(FILTER));
    }

    @Test
    public void getContacts_success_observersNotifiedWithCorrectData() {

        // Arrange
        setupSuccess();
        // Act
        sut.registerListener(listener1);
        sut.registerListener(listener2);
        sut.fetchContactsAndNotify(FILTER);
        // Assert
        verify(listener1).onFetchContactsSuccess(acListContact.capture());
        verify(listener2).onFetchContactsSuccess(acListContact.capture());
        List<Contact> capture1 = acListContact.getAllValues().get(0);
        List<Contact> capture2 = acListContact.getAllValues().get(1);
        assertThat(capture1, is(getContacts()));
        assertThat(capture2, is(getContacts()));
    }

    @Test
    public void getContacts_success_unregisteredObserversNotNotified() {

        // Arrange
        setupSuccess();
        // Act
        sut.registerListener(listener1);
        sut.registerListener(listener2);
        sut.unregisterListener(listener2);
        sut.fetchContactsAndNotify(FILTER);
        // Assert
        verify(listener1).onFetchContactsSuccess(acListContact.capture());
        verifyZeroInteractions(listener2);
        assertThat(acListContact.getValue(), is(getContacts()));
    }

    @Test
    public void getContacts_generalError_observersNotifiedWithGeneralError() {

        // Arrange
        ArgumentCaptor<FailReason> ac = ArgumentCaptor.forClass(FailReason.class);
        setupGeneralError();
        // Act
        sut.registerListener(listener1);
        sut.registerListener(listener2);
        sut.fetchContactsAndNotify(FILTER);
        // Assert
        verify(listener1).onFetchContactsFailure(ac.capture());
        verify(listener2).onFetchContactsFailure(ac.capture());
        FailReason capture1 = ac.getAllValues().get(0);
        FailReason capture2 = ac.getAllValues().get(1);
        assertThat(capture1, is(FailReason.GENERAL_ERROR));
        assertThat(capture2, is(FailReason.GENERAL_ERROR));
    }

    @Test
    public void getContacts_NetworkError_observersNotifiedWithNetworkError() {

        // Arrange
        ArgumentCaptor<FailReason> ac = ArgumentCaptor.forClass(FailReason.class);
        setupNetworkError();
        // Act
        sut.registerListener(listener1);
        sut.registerListener(listener2);
        sut.fetchContactsAndNotify(FILTER);
        // Assert
        verify(listener1).onFetchContactsFailure(ac.capture());
        verify(listener2).onFetchContactsFailure(ac.capture());
        FailReason capture1 = ac.getAllValues().get(0);
        FailReason capture2 = ac.getAllValues().get(1);
        assertThat(capture1, is(FailReason.NETWORK_ERROR));
        assertThat(capture2, is(FailReason.NETWORK_ERROR));
    }

    // region Helper methods

    private void setupSuccess() {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactSchemas());

                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void setupGeneralError() {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);

                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void setupNetworkError() {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {

                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);

                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contacts;
    }

    private List<ContactSchema> getContactSchemas() {
        List<ContactSchema> contacts = new ArrayList<>();
        contacts.add(new ContactSchema(ID, FULL_NAME, FULL_PHONE_NUMBER, IMAGE_URL, AGE));
        return contacts;
    }

    // endregion
}