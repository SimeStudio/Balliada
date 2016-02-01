package com.siwiesinger.pinbo;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GoogleBaseGameActivity;


import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.region.TextureRegion;

public class BaseActivity extends GoogleBaseGameActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    static float CAMERA_WIDTH = 480;
    static float CAMERA_HEIGHT = 800;

    public float accelerometerSpeedX;
    public float accelerometerSpeedY;
    public float acceleroModX;
    public float acceleroModY;

    public int highscore;
    public Score score;
    public boolean isShield;

    public Font mFont;
    public Font mSmallFont;
    public Camera mCamera;

    public BitmapTextureAtlas ballImage;
    public TextureRegion ballRegion;
    public Sprite ballSprite;

    public BitmapTextureAtlas shieldImage;
    public TextureRegion shieldRegion;
    public Sprite shieldSprite;
    public Sprite shieldSpriteP;

    public BitmapTextureAtlas backgroundImage;
    public TextureRegion backgroundRegion;
    public Sprite backgroundSprite;

    public BitmapTextureAtlas enemyImage;
    public TextureRegion enemyRegion;
    public Sprite enemySprite;

    public BitmapTextureAtlas bonusImage;
    public TextureRegion bonusRegion;
    public Sprite bonusSprite;

    public BitmapTextureAtlas logoImage;
    public TextureRegion logoRegion;
    public Sprite logoSprite;

    public BitmapTextureAtlas buttonImage;
    public TextureRegion buttonRegion;
    public BitmapTextureAtlas panelImage;
    public TextureRegion panelRegion;

    public Scene mCurrentScene;
    public static BaseActivity instance;

    public Music battleTheme;
    public Music awesomeness;
    public Sound bonusSound;
    public Sound explSound;
    public Sound menuClickSound;
    public Sound getShieldSound;
    public Sound dropShieldSound;

    public Vibrator vibrator;

    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;

    public boolean resumeAwe;
    public boolean resumeBat;

    public GoogleApiClient mGoogleApiClient;
    public InterstitialAd interstitialAd;
    public AdRequest adRequest;
    public Tracker tracker;
    public GoogleAnalytics analytics;

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;
    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";


    @Override
    protected void onCreateResources() {
        final ITexture smallFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
        final ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);

        mFont = FontFactory.createFromAsset(this.getFontManager(), fontTexture, this.getAssets(),
                "ttf/kenvector_future_thin.ttf",mCamera.getWidth()/10, true,android.graphics.Color.WHITE);
        mFont.load();

        mSmallFont = FontFactory.createFromAsset(this.getFontManager(), smallFontTexture, this.getAssets(),
                "ttf/kenvector_future_thin.ttf", mCamera.getWidth() / 12, true, android.graphics.Color.WHITE);
        mSmallFont.load();
    }

    @Override
    protected Scene onCreateScene() {
        mEngine.registerUpdateHandler(new FPSLogger());

        mCurrentScene = new SplashScene();

        return mCurrentScene;
    }

    public EngineOptions onCreateEngineOptions(){
        float width;
        float height;
        if (android.os.Build.VERSION.SDK_INT < 21) {
            Display display = getWindowManager().getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            height = size.y;
            width = size.x;
        }

        float aspectRatio = width / height;
        CAMERA_WIDTH = Math.round(aspectRatio * CAMERA_HEIGHT);
        Log.v("aspectRatio", CAMERA_WIDTH+" "+aspectRatio );

        instance = this;
        mCamera = new Camera(0,0,CAMERA_WIDTH,CAMERA_HEIGHT);

        EngineOptions en=new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED,
                new RatioResolutionPolicy(CAMERA_WIDTH,CAMERA_HEIGHT),mCamera);

        en.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);

        return en;
    }

    public static BaseActivity getSharedInstance() {
        return instance;
    }

    public void setCurrentScene(Scene scene) {
        mCurrentScene = scene;
        getEngine().setScene(mCurrentScene);
    }

    @Override
    public void onBackPressed() {
        if (mCurrentScene instanceof GameScene)
            ((GameScene) mCurrentScene).detach();

        mCurrentScene = null;
        SensorListener.instance = null;
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.isGameLoaded())
        {
            if(isMusic()) {
                if (awesomeness.isPlaying()) {
                    awesomeness.pause();
                    resumeAwe = true;
                } else {
                    battleTheme.pause();
                    resumeBat = true;
                }
            }
        }
    }

    @Override
    protected synchronized void onResume() {
        super.onResume();
        if(this.isGameLoaded())
        {
            if(resumeAwe)
            awesomeness.resume();
            if(resumeBat)
            battleTheme.resume();
        }
        if(mGoogleApiClient!=null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSignInClicked = true;
                    mGoogleApiClient.connect();
                }
            });
        }
    }

    public void connectGoogleAPI()
    {
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES).build();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSignInClicked = true;
                mGoogleApiClient.connect();
            }
        });
    }

    public boolean isMusic()
    {
        if(prefs.getBoolean("music", true))
            return true;
        else
            return false;
    }

    public boolean isSfx()
    {
        if(prefs.getBoolean("sfx", true))
            return true;
        else
            return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (Exception e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((BaseActivity) getActivity()).onDialogDismissed();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, 1);
            }
        }
    }


    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}
