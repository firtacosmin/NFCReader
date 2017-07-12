package com.kisi.acai.nfcreader;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.presenter.ComPresenter;
import com.kisi.acai.nfcreader.communication.view.ComView;
import com.kisi.acai.nfcreader.databinding.ActivityMainBinding;
import com.kisi.acai.nfcreader.di.activity.ActivityComponent;
import com.kisi.acai.nfcreader.di.activity.DaggerActivityComponent;
import com.kisi.acai.nfcreader.di.activity.modules.ComViewModule;
import com.kisi.acai.nfcreader.di.activity.modules.ContextModule;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ComView {


    private static final int STOP_SPLASH = 1;
    public static final long SPLASH_DELAY = 1000;


    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ActivityComponent component;
    @Inject
    public ComPresenter presenter;

    @BindView(R.id.mainLayout)
    ConstraintLayout mainLayout;
    @BindView(R.id.mainText)
    TextView mainText;


    private Handler delayHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(binding.appBarMain.toolbar);
        component = DaggerActivityComponent.builder()
                .contextModule(new ContextModule(this))
                .comViewModule(new ComViewModule(this))
                .applicationComponent(((MainApp)getApplication()).getComponent())
                .build();
        component.bind(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        binding.drawerLayout.setFitsSystemWindows(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int flags = binding.drawerLayout.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            binding.drawerLayout.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        delayHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if ( msg.what == STOP_SPLASH ){
                    presenter.splashFinished();
                }
            }
        };

        presenter.activityCreated(savedInstanceState);
    }



    public void onResume(){
        super.onResume();

        presenter.activityResumed();

    }

    public void onPause(){
        super.onPause();

        presenter.activityPaused();
    }

    public void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);

        presenter.activitySaveInstanceState(state);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        presenter.processViewIntent(intent);
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Process the messages array.
            }


            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return ;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
//                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
//                        text.setText(readText(ndefRecord));
                        Toast.makeText(this, readText(ndefRecord), Toast.LENGTH_LONG).show();
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
//                }
            }
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        Log.d(TAG,"::other option: "+new String(payload, 0, payload.length  - 1, textEncoding));
        return new String(payload, 0, payload.length - 1, textEncoding);
    }

    @Override
    public void showSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.splashScreenColor, getTheme()));
        }else{
            mainLayout.setBackgroundColor(getResources().getColor(R.color.splashScreenColor));
        }
        mainText.setVisibility(View.INVISIBLE);
        delayHandler.sendMessageDelayed(delayHandler.obtainMessage(STOP_SPLASH), SPLASH_DELAY);
    }

    @Override
    public void showHome() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white, getTheme()));
        }else{
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }
        mainText.setVisibility(View.VISIBLE);
    }

    @Override
    public void showUser(ComModel.User user) {

    }

    @Override
    public void showUnlockAnimation() {

    }
}
