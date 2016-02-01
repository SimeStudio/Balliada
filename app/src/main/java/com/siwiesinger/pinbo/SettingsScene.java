package com.siwiesinger.pinbo;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

/**
 * Created by user on 08.08.2015.
 */
public class SettingsScene extends MenuScene implements MenuScene.IOnMenuItemClickListener {
    BaseActivity activity;
    final int MUSICON=1;
    final int SFXON=2;
    final int MUSICOFF=3;
    final int SFXOFF=4;
    final int BACK=5;
    IMenuItem musicOnButton;
    IMenuItem sfxOnButton;
    IMenuItem musicOffButton;
    IMenuItem sfxOffButton;
    IMenuItem backButton;
    Text disMusic;
    Text enMusic;
    Text disSFX;
    Text enSFX;
    Text back;

    public SettingsScene() {
        super(BaseActivity.getSharedInstance().mCamera);
        activity = BaseActivity.getSharedInstance();

        setBackground(new Background(Color.WHITE));
        attachChild(activity.backgroundSprite);

        disMusic = new Text(0,0,activity.mSmallFont,"Disable Music", activity.getVertexBufferObjectManager());
        enMusic = new Text(0,0,activity.mSmallFont,"Enable Music", activity.getVertexBufferObjectManager());
        disSFX = new Text(0,0,activity.mSmallFont,"Disable SFX", activity.getVertexBufferObjectManager());
        enSFX = new Text(0,0,activity.mSmallFont,"Enable SFX", activity.getVertexBufferObjectManager());
        back = new Text(0,0,activity.mFont,"Back", activity.getVertexBufferObjectManager());

        disMusic.setPosition(mCamera.getWidth() / 2 - disMusic.getWidth() / 2,
                mCamera.getHeight() / 2 - disMusic.getHeight() / 2);
        disSFX.setPosition(mCamera.getWidth() / 2 - disSFX.getWidth() / 2,
                mCamera.getHeight() / 2 - disSFX.getHeight() / 2 + disMusic.getHeight() + 10);
        enMusic.setPosition(mCamera.getWidth() / 2 - enMusic.getWidth() / 2,
                mCamera.getHeight() / 2 - enMusic.getHeight() / 2);
        enSFX.setPosition(mCamera.getWidth() / 2 - enSFX.getWidth() / 2,
                mCamera.getHeight() / 2 - enSFX.getHeight() / 2 + enMusic.getHeight() + 10);
        back.setPosition(mCamera.getWidth() / 2 - back.getWidth() / 2,
                mCamera.getHeight() - back.getHeight() - 20);

        musicOnButton = new SpriteMenuItem(MUSICON, activity.buttonRegion, activity.getVertexBufferObjectManager());
        sfxOnButton = new SpriteMenuItem(SFXON, activity.buttonRegion, activity.getVertexBufferObjectManager());
        musicOffButton = new SpriteMenuItem(MUSICOFF, activity.buttonRegion, activity.getVertexBufferObjectManager());
        sfxOffButton = new SpriteMenuItem(SFXOFF, activity.buttonRegion, activity.getVertexBufferObjectManager());
        backButton = new SpriteMenuItem(BACK, activity.buttonRegion, activity.getVertexBufferObjectManager());

        musicOnButton.setPosition(mCamera.getWidth() / 10, disMusic.getY());
        musicOnButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, disMusic.getHeight());

        sfxOnButton.setPosition(mCamera.getWidth() / 10, disSFX.getY());
        sfxOnButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, disSFX.getHeight());

        musicOffButton.setPosition(mCamera.getWidth() / 10, enMusic.getY());
        musicOffButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, enMusic.getHeight());

        sfxOffButton.setPosition(mCamera.getWidth() / 10, enSFX.getY());
        sfxOffButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, enSFX.getHeight());

        backButton.setPosition(mCamera.getWidth() / 10, back.getY());
        backButton.setSize(mCamera.getWidth() - mCamera.getWidth() / 5, back.getHeight());

        addMenuItem(backButton);
        attachChild(back);

        if(activity.prefs.getBoolean("music",true)) {
            addMenuItem(musicOnButton);
            attachChild(disMusic);
        }
        else {
            addMenuItem(musicOffButton);
            attachChild(enMusic);
        }
        if(activity.prefs.getBoolean("sfx",true)) {
            addMenuItem(sfxOnButton);
            attachChild(disSFX);
        }
        else {
            addMenuItem(sfxOffButton);
            attachChild(enSFX);
        }

        setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClicked(MenuScene arg0, IMenuItem arg1, float arg2, float arg3) {
        switch (arg1.getID()) {
            case MUSICON:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                activity.editor.putBoolean("music",false);
                activity.editor.commit();
                updateItems();
                return true;
            case SFXON:
                activity.editor.putBoolean("sfx",false);
                activity.editor.commit();
                if(activity.isSfx())
                    activity.menuClickSound.play();
                updateItems();
                return true;
            case MUSICOFF:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                activity.editor.putBoolean("music",true);
                activity.editor.commit();
                updateItems();
                return true;
            case SFXOFF:
                activity.editor.putBoolean("sfx",true);
                activity.editor.commit();
                if(activity.isSfx())
                    activity.menuClickSound.play();
                updateItems();
                return true;
            case BACK:
                if(activity.isSfx())
                    activity.menuClickSound.play();
                detachChildren();
                activity.setCurrentScene(new MainMenuScene());
                return true;
            default:
                break;
        }
        return false;
    }

    private void updateItems() {
        clearMenuItems();
        detachChildren();
        attachChild(activity.backgroundSprite);
        if(activity.prefs.getBoolean("music", true)) {
            addMenuItem(musicOnButton);
            attachChild(disMusic);
            activity.awesomeness.play();
        } else {
            addMenuItem(musicOffButton);
            attachChild(enMusic);
            activity.awesomeness.pause();
            activity.awesomeness.seekTo(0);
        }
        if(activity.prefs.getBoolean("sfx", true)) {
            addMenuItem(sfxOnButton);
            attachChild(disSFX);
        } else {
            addMenuItem(sfxOffButton);
            attachChild(enSFX);
        }

        addMenuItem(backButton);
        attachChild(back);
    }
}
