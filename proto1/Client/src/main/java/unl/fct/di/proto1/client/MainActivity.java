package unl.fct.di.proto1.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.client.Client;


public class MainActivity extends Activity {

    static IConsole console = null;
    private Client client = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // build GUI object
        ClientGuiAndroid clientGui = new ClientGuiAndroid(this);
        console = clientGui;

        // must be called after setContentView
        try {
            client = new Client(clientGui, "c1");
        } catch (Exception e) {
            console.printException(e);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.create_DDInt) {
            client.workWithInts();
            return true;
        }
        if (id == R.id.create_DDObject) {
            client.workWithObjects();
            return true;
        }
        if (id == R.id.open_DDObject) {
            getUUIDAndWorkWithExistingObjects();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUUIDAndWorkWithExistingObjects() {
        final EditText txtUUID = new EditText(this);

        // Set the default text to a link of the Queen
        txtUUID.setHint("Set UUID of DD to open...");

        //txtUUID.setText(client.ddTest);

        new AlertDialog.Builder(this)
                .setTitle("Set UUID of DD to Open")
                .setMessage("Enter UUID of DD to Open")
                .setView(txtUUID)
                .setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        client.workWithExistingObjects(txtUUID.getText().toString().trim());

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }


//    @Override
//    public void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
