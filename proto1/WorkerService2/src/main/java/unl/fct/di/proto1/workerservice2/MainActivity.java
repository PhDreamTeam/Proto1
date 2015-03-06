package unl.fct.di.proto1.workerservice2;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import unl.fct.di.proto1.common.IConsole;
import unl.fct.di.proto1.common.workerService.WorkerService;


public class MainActivity extends Activity {
    IConsole console = null;
    WorkerService ws;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // must be done after setContentView
        // build GUI object
        WorkerGuiAndroid wg = new WorkerGuiAndroid(this);
        // CONSOLE ------------------------------
        console = wg;

        // must be called after setContentView
        try {
            ws = new WorkerService(wg, "w1");
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

        if (id == R.id.load_partitions) {
            ws.loadPartitionsFromDisk();
            return true;
        }

        if (id == R.id.clear_partitions) {
            ws.clearPartitions();
            return true;
        }

        if (id == R.id.show_files) {
            ws.showFiles();
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
