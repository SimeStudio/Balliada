package com.siwiesinger.pinbo;

import android.widget.Toast;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

public class GameoverScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {
    BaseActivity activity;
    final int MENU_MAINMENU=0;
    Text continueText;
    Text gameOverText;
    Text scoreText;
    Text highscoreText;

    public GameoverScene() {
        super(BaseActivity.getSharedInstance().mCamera);
        activity = BaseActivity.getSharedInstance();

        setBackground(new Background(Color.WHITE));
        attachChild(activity.backgroundSprite);

        continueText = new Text(0,0,activity.mFont,"Continue", activity.getVertexBufferObjectManager());
        continueText.setPosition(mCamera.getWidth() / 2 - continueText.getWidth() / 2,
                mCamera.getHeight() - continueText.getHeight() - 20);

        gameOverText = new Text(0,0, activity.mFont, "Game Over", activity.getVertexBufferObjectManager());
        gameOverText.setPosition(mCamera.getWidth() / 2 - gameOverText.getWidth() / 2, mCamera.getHeight()/6);

        scoreText = new Text(0,0, activity.mSmallFont, "Score: "+ activity.score.getScore(), activity.getVertexBufferObjectManager());
        scoreText.setPosition(mCamera.getWidth() / 2 - scoreText.getWidth() / 2,
                mCamera.getHeight() / 2 - scoreText.getHeight() / 2 - gameOverText.getHeight());

        highscoreText = new Text(0,0,activity.mSmallFont, "Highscore: " + activity.highscore, activity.getVertexBufferObjectManager());
        highscoreText.setPosition(mCamera.getWidth() / 2 - highscoreText.getWidth() / 2,
                mCamera.getHeight() / 2 - scoreText.getHeight() / 2 - continueText.getHeight() + scoreText.getHeight()
                        + activity.mCamera.getWidth() / 108f);

        IMenuItem startButton = new SpriteMenuItem(MENU_MAINMENU, activity.buttonRegion, activity.getVertexBufferObjectManager());
        startButton.setPosition(mCamera.getWidth() / 10, continueText.getY());
        startButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, continueText.getHeight());

        Sprite panel = new Sprite(mCamera.getWidth() / 10 - mCamera.getWidth()/20, gameOverText.getY() - 20, activity.panelRegion, activity.getVertexBufferObjectManager());
        panel.setSize(mCamera.getWidth() - mCamera.getWidth() / 10, mCamera.getHeight() - panel.getY() - (mCamera.getHeight() - highscoreText.getY()) + highscoreText.getHeight() + 40);

        attachChild(panel);
        addMenuItem(startButton);
        attachChild(gameOverText);
        attachChild(highscoreText);
        attachChild(scoreText);
        attachChild(continueText);

        setOnMenuItemClickListener(this);

        if(activity.mGoogleApiClient == null || !activity.mGoogleApiClient.isConnected()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Not connected to Google Play Services. Score not uploaded. Highscore gets uploaded automatically next time.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1, float arg2, float arg3) {
        switch (arg1.getID()) {
            case MENU_MAINMENU:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                detachChildren();
                activity.setCurrentScene(new MainMenuScene());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(activity.interstitialAd.isLoaded())
                        {
                            activity.interstitialAd.show();
                        }
                        else {
                            activity.interstitialAd.loadAd(activity.adRequest);
                            if (activity.interstitialAd.isLoaded()) {
                                activity.interstitialAd.show();
                            }
                        }
                    }
                });

                return true;
            default:
                break;
        }
        return false;
    }
}
