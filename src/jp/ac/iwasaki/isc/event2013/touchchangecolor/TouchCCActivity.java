package jp.ac.iwasaki.isc.event2013.touchchangecolor;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class TouchCCActivity extends Activity {

	TouchCCView view;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        view = new TouchCCView(this);
        setContentView(view);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}