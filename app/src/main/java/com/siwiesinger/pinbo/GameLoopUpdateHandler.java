package com.siwiesinger.pinbo;

import org.andengine.engine.handler.IUpdateHandler;

/**
 * Created by user on 02.07.2015.
 */
public class GameLoopUpdateHandler implements IUpdateHandler {
    @Override
    public void onUpdate(float pSecondsElapsed) {
        ((GameScene) BaseActivity.getSharedInstance().mCurrentScene).moveBall();
    }

    @Override
    public void reset() {

    }
}
