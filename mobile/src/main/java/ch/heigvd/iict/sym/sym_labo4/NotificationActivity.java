package ch.heigvd.iict.sym.sym_labo4;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ch.heigvd.iict.sym.wearcommon.Constants;

/**
 * NotificationActivity used to test different kind of notifications sent to a wearable
 *
 * @author Michael Spierer
 * @author Edward Ransome
 * @author Rémi Jacquemard
 */
public class NotificationActivity extends AppCompatActivity {

    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    // A click on one of these buttons send each a different kind of notifications to the wearable.
    private Button pendingButton;
    private Button actionButton;
    private Button wearableButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        pendingButton = findViewById(R.id.pendingButton);
        actionButton = findViewById(R.id.actionButton);
        wearableButton = findViewById(R.id.wearableButton);

        if (getIntent() != null)
            onNewIntent(getIntent());

        /**
         * We test here pending notification. These are simple notification: we are using the fact
         * that notification shown on the phone are also shown on the watch.
         */
        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 1;

                // Creating the intent called when the notification has been clicked by the user
                PendingIntent pendingIntent = createPendingIntent(0, "Notification has been clicked !");

                // Creating the notification
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(NotificationActivity.this, "SYM")
                                .setSmallIcon(R.drawable.ic_lightbulb_on_black_18dp)
                                .setContentTitle("New notification")
                                .setContentText("New pending notification")
                                .setContentIntent(pendingIntent);

                // Sending the notification
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(NotificationActivity.this);

                notificationManager.notify(1, notificationBuilder.build());
            }
        });

        /**
         * This notification has some action that the user can click. Here, the user can
         * just click on the "OK" button. It could have been easy to add some more action, and
         * associate with each of these a different PendingIntent.
         */
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 1;
                // Creating the intent called when the notification has been clicked by the user
                PendingIntent pendingIntent = createPendingIntent(0, "Notification has been clicked !");

                // Creating the notification
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(NotificationActivity.this, "SYM")
                                .setSmallIcon(R.drawable.ic_lightbulb_on_black_18dp)
                                .setContentTitle("New notification")
                                .setContentText("New pending notification")
                                .setContentIntent(pendingIntent)
                                // We are adding here an action button the user can click
                                .addAction(R.drawable.ic_lightbulb_on_grey600_18dp,
                                        "OK", pendingIntent);

                // Sending the notification
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(NotificationActivity.this);

                notificationManager.notify(1, notificationBuilder.build());
            }
        });

        /**
         * Here, we create 2 different pendingIntent: one for the phone and one for the wearable.
         * To show how this can be usefull, we here have decided that the phone has less possible
         * action thant the wearable.
         */
        wearableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 1;
                // Creating the phone PendingIntent. The user cannot say anything else other than "OK"
                PendingIntent phonePendingIntent = createPendingIntent(0, "The user said he's ok !");
                // Creating the wearablePendingIntent. The user can say here anything he wants.
                PendingIntent wearPendingIntent = createPendingIntent(0);

                // The user can speak to the watch to respond to the notification. He can also click
                // on "yes" or "no"
                RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                        .setLabel("Are you okay ?")
                        .setChoices(new String[]{"Yes", "No"})
                        .build();

                // Create the wearable-specific action
                NotificationCompat.Action action =
                        new NotificationCompat.Action.Builder(R.drawable.ic_message_bulleted_white_18dp,
                                "Reply", wearPendingIntent)
                                .addRemoteInput(remoteInput)
                                .build();

                // Creating an extender. All of the wearable-specific actions has to be bundled
                // within this extender.
                NotificationCompat.WearableExtender wearableExtender =
                        new NotificationCompat.WearableExtender()
                                .setHintHideIcon(true)
                                .addAction(action);

                // Creating the notification
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(NotificationActivity.this, "SYM")
                                .setSmallIcon(R.drawable.ic_lightbulb_on_black_18dp)
                                .setContentTitle("Are you ok ?")
                                .setContentText("Really ok ?")
                                .setContentIntent(phonePendingIntent)
                                // This is the basic option, the one called from the phone
                                .addAction(R.drawable.ic_lightbulb_on_grey600_18dp,
                                        "I'm ok, no choice", phonePendingIntent)
                                // We extends here our notification whith the wearable-specific actions
                                .extend(wearableExtender);

                // Sending notification
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(NotificationActivity.this);

                notificationManager.notify(1, notificationBuilder.build());
            }
        });
    }


    /*
     *  Method called by system when a new Intent is received
     *  Display a toast with a message if the Intent is generated by
     *  createPendingIntent method.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // We do here different things regarding the PendingIntent associated with the action
        if (intent == null) return;
        if (Constants.MY_PENDING_INTENT_ACTION.equals(intent.getAction()))
            Toast.makeText(this, "" + intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
        if (Constants.MY_PENDING_INTENT_ACTION_FROM_WEAR.equals(intent.getAction()))
            Toast.makeText(this, "The user said: " + getMessageText(intent), Toast.LENGTH_SHORT).show();
    }

    /**
     * Used to extract what the user said with the wearable
     *
     * @param intent
     * @return
     */
    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(EXTRA_VOICE_REPLY);
        }
        return null;
    }

    /**
     * Method used to create a PendingIntent without any message
     * The intent will start a new activity Instance or bring to front an existing one.
     * See parentActivityName and launchMode options in Manifest
     * See https://developer.android.com/training/notify-user/navigation.html for TaskStackBuilder
     *
     * @param requestCode The request code
     * @return The pending Intent
     */
    private PendingIntent createPendingIntent(int requestCode) {
        Intent myIntent = new Intent(NotificationActivity.this, NotificationActivity.class);
        myIntent.setAction(Constants.MY_PENDING_INTENT_ACTION_FROM_WEAR);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(myIntent);

        return stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Method used to create a PendingIntent with the specified message
     * The intent will start a new activity Instance or bring to front an existing one.
     * See parentActivityName and launchMode options in Manifest
     * See https://developer.android.com/training/notify-user/navigation.html for TaskStackBuilder
     *
     * @param requestCode The request code
     * @param message     The message
     * @return The pending Intent
     */
    private PendingIntent createPendingIntent(int requestCode, String message) {
        Intent myIntent = new Intent(NotificationActivity.this, NotificationActivity.class);
        myIntent.setAction(Constants.MY_PENDING_INTENT_ACTION);
        myIntent.putExtra("msg", message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(myIntent);

        return stackBuilder.getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
