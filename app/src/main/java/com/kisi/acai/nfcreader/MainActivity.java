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

import com.kisi.acai.nfcreader.communication.model.ComEndpointInterface;
import com.kisi.acai.nfcreader.communication.model.ComModel;
import com.kisi.acai.nfcreader.communication.presenter.ComPresenter;
import com.kisi.acai.nfcreader.communication.view.ComView;
import com.kisi.acai.nfcreader.communication.view.TimingHandler;
import com.kisi.acai.nfcreader.databinding.ActivityMainBinding;
import com.kisi.acai.nfcreader.di.activity.ActivityComponent;
import com.kisi.acai.nfcreader.di.activity.DaggerActivityComponent;
import com.kisi.acai.nfcreader.di.activity.modules.ComViewModule;
import com.kisi.acai.nfcreader.di.activity.modules.ContextModule;
import com.kisi.acai.nfcreader.di.application.ApplicationScope;
import com.kisi.acai.nfcreader.util.GifImageView;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ComView, GifImageView.AnimationEndListener {


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
    @BindView(R.id.unlock_gif)
    GifImageView gifImageView;
    TextView username;
    TextView email;


    private TimingHandler delayHandler;

//    @Inject
//    @ApplicationScope
//    HttpLoggingInterceptor comEndpointInterface;


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

        delayHandler = new TimingHandler();
        delayHandler.setActivity(this);

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
        Log.d(TAG,"::showHome");
        hideSplash();
        mainText.setVisibility(View.VISIBLE);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void showUser(ComModel.User user) {
        Log.d(TAG,"::showUser");
        hideSplash();
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
        username.setText(user.getUsername());
        email.setText(user.getEmail());


    }

    @Override
    public void showUnlockAnimation() {
        hideSplash();
        Log.d(TAG,"::showUnlockAnimation");
        gifImageView.setAnimationEndListener(this);
        gifImageView.setStopAtEnd(true);
        gifImageView.setGifImageResource(R.drawable.gif);
    }

    @Override
    public void animationEnded() {
        Log.d(TAG,"::animationEnded");
        /*when the animation has ended then hide the gif and show the user data*/
//        gifImageView.setVisibility(View.GONE);
    }

    private void hideSplash(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white, getTheme()));
        }else{
            mainLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    /**
     * methid called by {@link com.kisi.acai.nfcreader.communication.view.TimingHandler} when the time ends
     */
    public void timerTicked() {


        presenter.splashFinished();

    }
}
