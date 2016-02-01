package com.siwiesinger.pinbo;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

/**
 * Created by user on 22.08.2015.
 */
public class StatsScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {
    BaseActivity activity;
    final int BACK=0;

    public StatsScene()
    {
        super(BaseActivity.getSharedInstance().mCamera);
        activity = BaseActivity.getSharedInstance();

        setBackground(new Background(Color.WHITE));
        attachChild(activity.backgroundSprite);

        Text back = new Text(0,0,activity.mFont,"Back", activity.getVertexBufferObjectManager());
        back.setPosition(mCamera.getWidth() / 2 - back.getWidth() / 2,
                mCamera.getHeight() - back.getHeight() - 20);
        IMenuItem backButton = new SpriteMenuItem(BACK, activity.buttonRegion, activity.getVertexBufferObjectManager());
        backButton.setPosition(mCamera.getWidth() / 10, back.getY());
        backButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, back.getHeight());

        Text coins = new Text(0,0,activity.mSmallFont,"Collected Coins: " + activity.prefs.getInt("coinsCollected",0), activity.getVertexBufferObjectManager());
        coins.setPosition(mCamera.getWidth() / 2 - coins.getWidth() / 2, mCamera.getHeight()/6);

        Text shields = new Text(0,0,activity.mSmallFont,"Collected Shields: " + activity.prefs.getInt("shieldsCollected",0), activity.getVertexBufferObjectManager());
        shields.setPosition(mCamera.getWidth() / 2 - shields.getWidth() / 2, coins.getY() + coins.getHeight() + 10);

        Text deaths = new Text(0,0,activity.mSmallFont,"Deaths: " + activity.prefs.getInt("deaths",0), activity.getVertexBufferObjectManager());
        deaths.setPosition(mCamera.getWidth() / 2 - deaths.getWidth() / 2, shields.getY() + shields.getHeight() + 10);

        int timeInSec = activity.prefs.getInt("time",0);
        int h = timeInSec / 3600;
        int m = (timeInSec - (h * 3600)) / 60;
        int s = timeInSec - h * 3600 - m * 60;
        String timeInString = "Playtime: " + h + ":" + m + ":" + s;
        if(m < 10)
        {
            timeInString = "Playtime: " + h + ":0" + m + ":" + s;
        }
        if(s < 10) {
            if(m < 10) {
                timeInString = "Playtime: " + h + ":0" + m + ":0" + s;
            }
            else
            {
                timeInString = "Playtime: " + h + ":" + m + ":0" + s;
            }
        }

        Text time = new Text(0,0,activity.mSmallFont, timeInString, activity.getVertexBufferObjectManager());
        time.setPosition(mCamera.getWidth() / 2 - time.getWidth() / 2, deaths.getY() + deaths.getHeight() + 10);

        Sprite panel = new Sprite(mCamera.getWidth() / 10, coins.getY() - 10, activity.panelRegion, activity.getVertexBufferObjectManager());
        panel.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, time.getY() - panel.getY() + time.getHeight() + 20);

        attachChild(panel);
        addMenuItem(backButton);
        attachChild(back);
        attachChild(coins);
        attachChild(shields);
        attachChild(time);
        attachChild(deaths);

        setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1, float arg2, float arg3) {
        switch (arg1.getID())
        {
            case BACK:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                detachChildren();
                activity.setCurrentScene(new MainMenuScene());
                return true;
            default: break;
        }
        return false;
    }
}
