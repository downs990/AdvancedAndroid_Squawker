package android.example.com.squawker.fcm;

import android.content.ContentValues;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.example.com.squawker.utilities.NotificationUtils;
import android.os.AsyncTask;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class SquawkFirebaseMessagingService extends FirebaseMessagingService {


    private static final String JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR;
    private static final String JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY;
    private static final String JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE;
    private static final String JSON_KEY_DATE = SquawkContract.COLUMN_DATE;
    // TODO (1) Make a new Service in the fcm package that extends from FirebaseMessagingService.
    // TODO (2) As part of the new Service - Override onMessageReceived.


    // TODO (3) As part of the new Service - If there is message data, get the data using
    // the keys and do two things with it :
    // 1. Display a notification with the first 30 character of the message
    // 2. Use the content provider to insert a new message into the local database

    // Hint: You shouldn't be doing content provider operations on the main thread.
    // If you don't know how to make notifications or interact with a content provider
    // look at the notes in the classroom for help.



    // This method will be triggered whenever a squawk is received. You can get the data from the squawk
    // message using getData(). When you send a test message, this data will include the
    // following key/value pairs:
    // test: true
    // author: Ex. "TestAccount"
    // authorKey: Ex. "key_test"
    // message: Ex. "Hello world"
    // date: Ex. 1484358455343
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        NotificationUtils.triggerNewNotification(this, data);

        insertSquawk(data);
    }


    /**
     * Inserts a single squawk into the database;
     *
     * @param data Map which has the message data in it
     */
    private void insertSquawk(final Map<String, String> data) {

        // Database operations should not be done on the main thread
        AsyncTask<Void, Void, Void> insertSquawkTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues newMessage = new ContentValues();
                newMessage.put(SquawkContract.COLUMN_AUTHOR, data.get(JSON_KEY_AUTHOR));
                newMessage.put(SquawkContract.COLUMN_MESSAGE, data.get(JSON_KEY_MESSAGE).trim());
                newMessage.put(SquawkContract.COLUMN_DATE, data.get(JSON_KEY_DATE));
                newMessage.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get(JSON_KEY_AUTHOR_KEY));
                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, newMessage);
                return null;
            }
        };

        insertSquawkTask.execute();
    }
}
