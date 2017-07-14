package com.kisi.acai.nfcreader;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.presenter.ComPresenter;
import com.kisi.acai.nfcreader.communication.view.ComView;
import com.kisi.acai.nfcreader.communication.view.TimingHandler;
import com.kisi.acai.nfcreader.databinding.ActivityMainBinding;
import com.kisi.acai.nfcreader.di.activity.ActivityComponent;
import com.kisi.acai.nfcreader.di.activity.DaggerActivityComponent;
import com.kisi.acai.nfcreader.di.activity.modules.ComViewModule;
import com.kisi.acai.nfcreader.di.activity.modules.ContextModule;
import com.kisi.acai.nfcreader.util.GifImageView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ComView, GifImageView.AnimationEndListener, View.OnClickListener {


    private static final int STOP_SPLASH = 1;
    public static final long SPLASH_DELAY = 60*1000;


    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ActivityComponent component;
    @Inject
    public ComPresenter presenter;
    @Inject
    public NfcAdapter nfcAdapter;

    @BindView(R.id.mainLayout)
    ConstraintLayout mainLayout;
    @BindView(R.id.mainText)
    TextView mainText;
    @BindView(R.id.unlock_gif)
    GifImageView gifImageView;
    TextView username;
    TextView email;


    private TimingHandler delayHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(binding.appBarMain.toolbar);
        component = DaggerActivityComponent.builder()
                .applicationComponent(((MainApp)getApplication()).getComponent())
                .contextModule(new ContextModule(this))
                .comViewModule(new ComViewModule(this))
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
        View header=navigationView.getHeaderView(0);

        username = (TextView)header.findViewById(R.id.username);
        email = (TextView)header.findViewById(R.id.email);
        ((Button)header.findViewById(R.id.logoutBtn)).setOnClickListener(this);

        delayHandler = new TimingHandler();
        delayHandler.setActivity(this);

        presenter.activityCreated(savedInstanceState);
    }



    public void onResume(){
        super.onResume();

        presenter.activityResumed();
        nfcAdapter.enableReaderMode(this, presenter, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                null);

    }

    public void onPause(){
        super.onPause();

        presenter.activityPaused();
        nfcAdapter.disableReaderMode(this);
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
    }

    @Override
    public void showSplashScreen() {

        Log.d(TAG,"::showSplashScreen");
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
        if ( Thread.currentThread().getId() == 1 ) {
            Log.d(TAG, "::showHome");
            hideSplash();
            hideGif();
            mainText.setVisibility(View.VISIBLE);
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }else{
            runOnUiThread(this::showHome);
        }

    }

    @Override
    public void showUser(ComModel.User user) {
        if ( Thread.currentThread().getId() == 1 ) {
            Log.d(TAG, "::showUser");
            hideSplash();
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
            username.setText(user.getUsername());
            email.setText(user.getEmail());
        }else{
            runOnUiThread(() -> showUser(user));
        }


    }

    @Override
    public void showUnlockAnimation() {
        if ( Thread.currentThread().getId() == 1 ) {
            hideSplash();
            Log.d(TAG, "::showUnlockAnimation");
            gifImageView.setVisibility(View.VISIBLE);
            mainText.setVisibility(View.INVISIBLE);
            gifImageView.setAnimationEndListener(this);
            gifImageView.setStopAtEnd(true);
            gifImageView.setGifImageResource(R.drawable.gif);
        }else{
            runOnUiThread(this::showUnlockAnimation);
        }
    }

    @Override
    public void showNothingMessage() {
        if ( Thread.currentThread().getId() == 1 ) {
            Snackbar.make(mainLayout, "Nothing received", Snackbar.LENGTH_SHORT).show();
        }else{
            runOnUiThread(this::showNothingMessage);
        }
    }

    @Override
    public void animationEnded() {
        Log.d(TAG,"::animationEnded");
        /*when the animation has ended then hide the gif and show the user data*/
//        gifImageView.setVisibility(View.GONE);
    }

    private void hideSplash(){
        if ( Thread.currentThread().getId() == 1 ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mainLayout.setBackgroundColor(getResources().getColor(R.color.white, getTheme()));
            } else {
                mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }else{
            runOnUiThread(this::hideSplash);
        }
    }


    private void hideGif() {
        if ( Thread.currentThread().getId() == 1 ) {
            gifImageView.setVisibility(View.INVISIBLE);
        }else{
            runOnUiThread(this::hideGif);
        }
    }
    /**
     * methid called by {@link com.kisi.acai.nfcreader.communication.view.TimingHandler} when the time ends
     */
    public void timerTicked() {

        presenter.splashFinished();

    }

    public void logoutClick(){
        presenter.logoutPressed();
    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.logoutBtn ){
            logoutClick();
        }
    }
}
