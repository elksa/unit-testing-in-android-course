package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    public interface ContactsListener {

        void onFetchContactsSuccess(List<Contact> contacts);

        void onFetchContactsFailure(FailReason reason);
    }

    private GetContactsHttpEndpoint endpoint;
    private List<ContactsListener> listeners;

    public FetchContactsUseCase(GetContactsHttpEndpoint endpoint) {
        this.endpoint = endpoint;
        listeners = new ArrayList<>();
    }

    void fetchContactsAndNotify(String filter) {
        endpoint.getContacts(filter, new Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> contactSchemas) {
                for (ContactsListener listener : listeners) {
                    listener.onFetchContactsSuccess(getContactsFromContactSchemas(contactSchemas));
                }
            }

            @Override
            public void onGetContactsFailed(FailReason failReason) {
                for (ContactsListener listener : listeners) {
                    listener.onFetchContactsFailure(failReason);
                }
            }
        });
    }

    void registerListener(ContactsListener listener) {
        listeners.add(listener);
    }

    void unregisterListener(ContactsListener listener) {
        listeners.remove(listener);
    }

    private List<Contact> getContactsFromContactSchemas(List<ContactSchema> contactSchemas) {

        List<Contact> result = new ArrayList<>();

        for (ContactSchema contactSchema : contactSchemas) {
            result.add(new Contact(contactSchema.getId(), contactSchema.getFullName(), contactSchema.getImageUrl()));
        }

        return result;
    }
}
