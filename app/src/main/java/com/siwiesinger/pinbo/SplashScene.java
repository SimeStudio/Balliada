package com.siwiesinger.pinbo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.util.color.Color;

import java.io.IOException;

/**
 * Created by user on 02.07.2015.
 */
public class SplashScene extends Scene{
    BaseActivity activity;

    public SplashScene() {
        activity = BaseActivity.getSharedInstance();

        setBackground(new Background(Color.RED));

        Text title1 = new Text(0, 0, activity.mFont, "Loading", activity.getVertexBufferObjectManager());
        title1.setPosition(-title1.getWidth(), activity.mCamera.getHeight() / 1.3f);

        attachChild(title1);

        title1.registerEntityModifier(new MoveXModifier(3, title1.getX(), activity.mCamera.getWidth()));
        loadResources();
    }

    public void loadResources()
    {
        activity.ballImage = new BitmapTextureAtlas(activity.getTextureManager(),
                512,512,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.ballRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.ballImage,
                activity.getAssets(), "gfx/ball.png",0,0);

        activity.backgroundImage = new BitmapTextureAtlas(activity.getTextureManager(),
                1652,1800, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.backgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.backgroundImage,
                activity.getAssets(), "gfx/background.png", 0, 0);

        activity.enemyImage = new BitmapTextureAtlas(activity.getTextureManager(),
                300,300,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.enemyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.enemyImage,
                activity.getAssets(), "gfx/enemy.png",0,0);

        activity.bonusImage = new BitmapTextureAtlas(activity.getTextureManager(),
                154,152,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.bonusRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.bonusImage,
                activity.getAssets(), "gfx/coin1.png",0,0);

        activity.shieldImage = new BitmapTextureAtlas(activity.getTextureManager(),
                300,300,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.shieldRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.shieldImage,
                activity.getAssets(), "gfx/shield.png",0,0);

        activity.buttonImage = new BitmapTextureAtlas(activity.getTextureManager(),
                190,45,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.buttonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.buttonImage,
                activity.getAssets(), "gfx/button.png",0,0);

        activity.panelImage = new BitmapTextureAtlas(activity.getTextureManager(),
                100,100,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.panelRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.panelImage,
                activity.getAssets(), "gfx/panel.png",0,0);

        activity.logoImage = new BitmapTextureAtlas(activity.getTextureManager(),
                500,150,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        activity.logoRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(activity.logoImage,
                activity.getAssets(), "gfx/logo.png",0,0);

        activity.ballImage.load();
        activity.backgroundImage.load();
        activity.enemyImage.load();
        activity.bonusImage.load();
        activity.shieldImage.load();
        activity.buttonImage.load();
        activity.panelImage.load();
        activity.logoImage.load();

        activity.ballSprite=new Sprite(0,0,activity.ballRegion,activity.getVertexBufferObjectManager());
        activity.ballSprite.setSize(activity.mCamera.getWidth() / 10.8f, activity.mCamera.getWidth() / 10.8f);

        activity.backgroundSprite=new Sprite(0,0,activity.backgroundRegion,activity.getVertexBufferObjectManager());
        activity.backgroundSprite.setSize(activity.mCamera.getWidth(),activity.mCamera.getHeight());

        activity.enemySprite=new Sprite(0,0,activity.enemyRegion,activity.getVertexBufferObjectManager());
        activity.enemySprite.setSize(activity.mCamera.getWidth()/10.8f*1.5f, activity.mCamera.getWidth()/10.8f*1.5f);

        activity.bonusSprite=new Sprite(0,0,activity.bonusRegion,activity.getVertexBufferObjectManager());
        activity.bonusSprite.setSize(activity.mCamera.getWidth() / 15.4f, activity.mCamera.getWidth() / 15.4f);

        activity.shieldSprite=new Sprite(0,0,activity.shieldRegion,activity.getVertexBufferObjectManager());
        activity.shieldSprite.setSize(activity.mCamera.getWidth() / 10.8f, activity.mCamera.getWidth() / 10.8f);

        activity.shieldSpriteP=new Sprite(0,0,activity.shieldRegion,activity.getVertexBufferObjectManager());
        activity.shieldSpriteP.setSize(activity.mCamera.getWidth() / 10.8f, activity.mCamera.getWidth() / 10.8f);

        activity.logoSprite=new Sprite(0,0,activity.logoRegion,activity.getVertexBufferObjectManager());
        activity.logoSprite.setSize(activity.mCamera.getWidth() - 20, activity.mCamera.getHeight() / 5);

        SoundFactory.setAssetBasePath("sfx/");
        try {
            activity.bonusSound = SoundFactory.createSoundFromAsset(activity
                    .getSoundManager(), activity, "bonus.wav");

            activity.explSound = SoundFactory.createSoundFromAsset(activity
                    .getSoundManager(), activity, "explode.wav");

            activity.menuClickSound = SoundFactory.createSoundFromAsset(activity
                    .getSoundManager(), activity, "MenuSelectionClick.wav");

            activity.getShieldSound = SoundFactory.createSoundFromAsset(activity
                    .getSoundManager(), activity, "shieldOn.wav");

            activity.dropShieldSound = SoundFactory.createSoundFromAsset(activity
                    .getSoundManager(), activity, "shieldOff.wav");

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MusicFactory.setAssetBasePath("sfx/");
        try {
            activity.battleTheme = MusicFactory.createMusicFromAsset(activity.getMusicManager(),
                    activity, "battleTheme.mp3");
            activity.battleTheme.setLooping(true);

            activity.awesomeness = MusicFactory.createMusicFromAsset(activity.getMusicManager(),
                    activity, "awesomeness.wav");
            activity.awesomeness.setLooping(true);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        activity.prefs=activity.getSharedPreferences("highscore", Context.MODE_PRIVATE);

        activity.vibrator=(Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

        activity.interstitialAd=new InterstitialAd(activity);
        activity.interstitialAd.setAdUnitId("ca-app-pub-9260016336188788/3317143555");

        activity.adRequest=new com.google.android.gms.ads.AdRequest.Builder()
                .addTestDevice("46BE8122D76407007768BD4900B9A93C")
                .build();

        activity.analytics=GoogleAnalytics.getInstance(activity);
        activity.analytics.setLocalDispatchPeriod(1800);
        activity.tracker=activity.analytics.newTracker("UA-64992199-3");
        activity.tracker.enableExceptionReporting(true);
        activity.tracker.enableAdvertisingIdCollection(true);
        activity.tracker.enableAutoActivityTracking(true);

        DelayModifier dMod = new DelayModifier(2) {
            @Override
            protected void onModifierFinished(IEntity pItem) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.prefs = activity.getSharedPreferences("localPreferences", Context.MODE_PRIVATE);
                        activity.editor = activity.prefs.edit();

                        if (activity.prefs.getBoolean("isFirstRun", true)) {
                            final TextView message = new TextView(activity);
                            final SpannableString s = new SpannableString("We use device identifiers to personalise content and ads, to provide social media features and to analyse our traffic. We also share such identifiers and other information from your device with our social media, advertising and analytics partners.\nhttp://www.google.com/intl/en/policies/privacy/partners/");
                            Linkify.addLinks(s, Linkify.ALL);
                            message.setText(s);
                            message.setMovementMethod(LinkMovementMethod.getInstance());

                            new AlertDialog.Builder(activity)
                                    .setTitle("Cookies")
                                    .setView(message)
                                    .setNeutralButton("Close message", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    activity.editor.putBoolean("isFirstRun", false);
                                                    activity.editor.commit();
                                                }
                                            }
                                    ).show();
                        }
                    }
                });
                activity.setCurrentScene(new MainMenuScene());

                activity.connectGoogleAPI();
            }
        };
        registerEntityModifier(dMod);
    }
}
