package com.siwiesinger.pinbo;

import android.widget.Toast;

import com.google.android.gms.games.Games;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

/**
 * Created by user on 02.07.2015.
 */
public class MainMenuScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {
    BaseActivity activity;
    final int MENU_START=0;
    final int MENU_SETTINGS=1;
    final int MENU_LEADERBOARD=2;
    final int MENU_ACHIEVEMENT=3;
    final int MENU_STATS=4;

    public MainMenuScene() {
        super(BaseActivity.getSharedInstance().mCamera);
        activity = BaseActivity.getSharedInstance();

        setBackground(new Background(Color.WHITE));
        attachChild(activity.backgroundSprite);

        Text start = new Text(0,0,activity.mFont,"Start", activity.getVertexBufferObjectManager());
        start.setPosition(mCamera.getWidth() / 2 - start.getWidth() / 2,
                mCamera.getHeight() / 2 - start.getHeight() / 2);

        Text leaderboard = new Text(0,0,activity.mFont,"Leaderboard", activity.getVertexBufferObjectManager());
        leaderboard.setPosition(mCamera.getWidth() / 2 - leaderboard.getWidth() / 2,
                mCamera.getHeight() / 2 - leaderboard.getHeight() / 2 + start.getHeight() + 10);

        Text achieve = new Text(0,0,activity.mFont,"Achievements", activity.getVertexBufferObjectManager());
        achieve.setPosition(mCamera.getWidth() / 2 - achieve.getWidth() / 2,
                mCamera.getHeight() / 2 - achieve.getHeight() / 2 + start.getHeight() + leaderboard.getHeight() + 20);

        Text stats = new Text(0,0,activity.mFont,"Stats", activity.getVertexBufferObjectManager());
        stats.setPosition(mCamera.getWidth() / 2 - stats.getWidth() / 2,
                mCamera.getHeight() / 2 - stats.getHeight() / 2 + start.getHeight() + leaderboard.getHeight() + achieve.getHeight() + 30);

        Text settings = new Text(0,0,activity.mFont,"Options", activity.getVertexBufferObjectManager());
        settings.setPosition(mCamera.getWidth() / 2 - settings.getWidth() / 2,
                mCamera.getHeight() / 2 - settings.getHeight() / 2 + start.getHeight() + leaderboard.getHeight() + achieve.getHeight() + stats.getHeight() + 40);

        IMenuItem startButton = new SpriteMenuItem(MENU_START, activity.buttonRegion, activity.getVertexBufferObjectManager());
        startButton.setPosition(mCamera.getWidth() / 10, start.getY());
        startButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, start.getHeight());

        IMenuItem leaderboardButton = new SpriteMenuItem(MENU_LEADERBOARD, activity.buttonRegion, activity.getVertexBufferObjectManager());
        leaderboardButton.setPosition(mCamera.getWidth() / 10, leaderboard.getY());
        leaderboardButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, leaderboard.getHeight());

        IMenuItem achievButton = new SpriteMenuItem(MENU_ACHIEVEMENT, activity.buttonRegion, activity.getVertexBufferObjectManager());
        achievButton.setPosition(mCamera.getWidth() / 10, achieve.getY());
        achievButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, achieve.getHeight());

        IMenuItem settingsButton = new SpriteMenuItem(MENU_SETTINGS, activity.buttonRegion, activity.getVertexBufferObjectManager());
        settingsButton.setPosition(mCamera.getWidth() / 10, settings.getY());
        settingsButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, settings.getHeight());

        IMenuItem statsButton = new SpriteMenuItem(MENU_STATS, activity.buttonRegion, activity.getVertexBufferObjectManager());
        statsButton.setPosition(mCamera.getWidth() / 10, stats.getY());
        statsButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, stats.getHeight());


        activity.logoSprite.setPosition(10, mCamera.getHeight() / 8);

        addMenuItem(startButton);
        addMenuItem(settingsButton);
        addMenuItem(leaderboardButton);
        addMenuItem(achievButton);
        addMenuItem(statsButton);
        attachChild(start);
        attachChild(settings);
        attachChild(leaderboard);
        attachChild(achieve);
        attachChild(stats);
        attachChild(activity.logoSprite);


        if(activity.isMusic()) {
            if (!activity.awesomeness.isPlaying()) {
                activity.awesomeness.play();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.interstitialAd.loadAd(activity.adRequest);
                    }
                });
            }
        }

        setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1, float arg2, float arg3) {
        switch (arg1.getID()) {
            case MENU_START:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                detachChildren();
                activity.setCurrentScene(new GameScene());
                return true;
            case MENU_SETTINGS:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                detachChildren();
                activity.setCurrentScene(new SettingsScene());
                return true;
            case MENU_LEADERBOARD:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(activity.mGoogleApiClient != null && activity.mGoogleApiClient.isConnected()) {activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(activity.mGoogleApiClient,
                                activity.getResources().getString(R.string.leaderboard_balliada_leaderboard)), 0);
                        }
                        else
                        {
                            Toast.makeText(activity,"Sign in to Google Play to see Leaderboards",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                return true;
            case MENU_ACHIEVEMENT:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(activity.mGoogleApiClient != null && activity.mGoogleApiClient.isConnected()) {
                            activity.startActivityForResult(Games.Achievements.getAchievementsIntent(activity.mGoogleApiClient), 0);
                        }
                        else
                        {
                            Toast.makeText(activity,"Sign in to Google Play to see Achievements",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            case MENU_STATS:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                detachChildren();
                activity.setCurrentScene(new StatsScene());
            default:
                break;
        }
        return false;
    }
}
