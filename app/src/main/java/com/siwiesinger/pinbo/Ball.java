package com.siwiesinger.pinbo;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;

/**
 * Created by user on 02.07.2015.
 */
public class Ball {
    BaseActivity activity;
    public Sprite sprite;
    public static Ball instance;
    Camera mCamera;


    public static Ball getSharedInstance(){
        if(instance == null)
            instance = new Ball();
        return instance;
    }

    private Ball()
    {
        activity = BaseActivity.getSharedInstance();

        sprite=activity.ballSprite;

        mCamera = BaseActivity.getSharedInstance().mCamera;
        sprite.setPosition(mCamera.getWidth() / 2 - activity.ballSprite.getWidth() / 2,
                mCamera.getHeight() - activity.ballSprite.getHeight() - 150);
    }

    public void moveBall(float accelerometerSpeedX,float accelerometerSpeedY) {
        int newX;
        int newY;
        float accX=activity.acceleroModX-accelerometerSpeedX*activity.prefs.getFloat("accX",mCamera.getWidth()/360f);
        float accY=accelerometerSpeedY*activity.prefs.getFloat("accY",mCamera.getWidth()/360f)-activity.acceleroModY;

        if(sprite.getX() + accX>0)
        {
            if(sprite.getX() + accX<mCamera.getWidth()-sprite.getWidth())
            {
                newX = (int) (sprite.getX() + accX);
            }
            else
            {
                newX = (int) (mCamera.getWidth()-sprite.getWidth());
            }
        }
        else
        {
            newX = 0;
        }

        if(sprite.getY() + accY>0)
        {
            if(sprite.getY() + accY<mCamera.getHeight()-sprite.getHeight())
            {
                newY = (int) (sprite.getY() + accY);
            }
            else
            {
                newY = (int) (mCamera.getHeight()-sprite.getHeight());
            }
        }
        else
        {
            newY = 0;
        }

        sprite.setPosition(newX, newY);
        activity.shieldSprite.setPosition(newX, newY);
    }

    public void restart() {
        Camera mCamera = BaseActivity.getSharedInstance().mCamera;
        sprite.setPosition(mCamera.getWidth() / 2 - activity.ballSprite.getWidth() / 2,
                mCamera.getHeight() - activity.ballSprite.getHeight()-150);
    }
}
