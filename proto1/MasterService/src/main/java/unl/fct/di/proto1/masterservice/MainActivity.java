package unl.fct.di.proto1.masterservice;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.masterService.MasterService;


public class MainActivity extends Activity {

    static IConsole console = null;
    MasterService ms = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // must be done after setContentView
        // build GUI object
        MasterGuiAndroid masterGui = new MasterGuiAndroid(this);
        // keep console
        console = masterGui;

        // must be called after setContentView
        try {
            ms = new MasterService(masterGui, "m1");
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

        if (id == R.id.load_state) {
            ms.loadState();
            return true;
        }

        if (id == R.id.save_state) {
            ms.saveState();
            return true;
        }

        if (id == R.id.clear_persistent_state) {
            ms.clearPersistentState();
            return true;
        }

        if (id == R.id.show_files) {
            ms.showFiles();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }
}
