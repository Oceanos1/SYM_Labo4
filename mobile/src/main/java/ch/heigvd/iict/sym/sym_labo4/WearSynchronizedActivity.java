package ch.heigvd.iict.sym.sym_labo4;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import ch.heigvd.iict.sym.wearcommon.Constants;

public class WearSynchronizedActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {

    private static final String TAG = WearSynchronizedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearsynchronized);
        Wearable.getDataClient(this).addListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }


    /**
     * Allows this listener to adapt when receiving data from wearable. Calls
     * updateColors() using the received RGB parameters.
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                //If the data is COLOUR_MAP type, then we ajust screen colour
                if (item.getUri().getPath().compareTo(Constants.COLOUR_MAP) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    updateColor(dataMap.getInt(Constants.RED_COLOUR_KEY),
                            dataMap.getInt(Constants.GREEN_COLOUR_KEY),
                            dataMap.getInt(Constants.BLUE_COLOUR_KEY));
                }
            }
        }
    }


    /*
     *  Code utilitaire fourni
     */

    /**
     * Method used to update the background color of the activity
     * @param r The red composant (0...255)
     * @param g The green composant (0...255)
     * @param b The blue composant (0...255)
     */
    private void updateColor(int r, int g, int b) {
        View rootView = findViewById(android.R.id.content);
        rootView.setBackgroundColor(Color.argb(255, r,g,b));
    }

}
