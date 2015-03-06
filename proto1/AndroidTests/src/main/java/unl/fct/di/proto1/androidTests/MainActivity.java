package unl.fct.di.proto1.androidTests;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;
import android.widget.TextView;


public class MainActivity extends Activity {

    TextView console = null;
    Scroller mSlr = null;

    public void println(final String msg) {
        console.post(new Runnable() {
            @Override
            public void run() {
                console.setText(">" + msg + "\n" + console.getText());
//                final int scrollAmount = console.getLayout().getLineTop(
//                        console.getLineCount()) - console.getHeight();
//                // if there is no need to scroll, scrollAmount will be <=0
//                if (scrollAmount > 0)
                    //console.scrollTo(0, 280);
//                final Layout layout = console.getLayout();
//                if(layout != null){
//                    int scrollDelta = layout.getLineBottom(console.getLineCount() - 1)
//                            - console.getScrollY() - console.getHeight();
//                    if(scrollDelta > 0) {
//                        console.scrollBy(0, /*scrollDelta*/16);
//                        console.append("\nESCREVIIIIII Devia ter feito scroll: " + scrollDelta );
//                    }
//                }



                int scrollingLen = 14;
//                int distance = scrollingLen - consolegetWidth();
//                int duration = (new Double(mRndDuration * distance * 1.00000
//                        / scrollingLen)).intValue();


//                mSlr.startScroll(0, 0, 0, 80, 1);
//                else
//                    console.scrollTo(0, 0);


            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        console = (TextView) findViewById(R.id.TextViewConsole);
//        console.setMovementMethod(new ScrollingMovementMethod());
//        console.setLongClickable(true);

        mSlr = new Scroller(this, new LinearInterpolator());
        console.setScroller(mSlr);
        console.setVisibility(TextView.VISIBLE);

        println("umqqqqq");
        println("um");
        println("um");
        println("um");
        println("um");
        println("um");
        println("um");
        println("um");
        println("um");
        println("um");
        println("um");
        println("dois");
        println("três");
        println("quatro");
        mSlr.startScroll(0, 0, 0, -150, 1);

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

        return super.onOptionsItemSelected(item);
    }
}
