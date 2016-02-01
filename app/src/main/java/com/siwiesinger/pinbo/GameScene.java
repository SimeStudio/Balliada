package com.siwiesinger.pinbo;


import android.hardware.Sensor;
import android.hardware.SensorManager;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityFactory;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.RotationParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.color.Color;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by user on 02.07.2015.
 */
public class GameScene extends Scene{
    BaseActivity activity;
    public Ball ball;
    public LinkedList<Sprite> enemies;
    public LinkedList<Sprite> enemiesToBeAdded;

    public int enemyCount;
    Camera mCamera;
    SensorManager sensorManager;
    Text scoreText;
    boolean bonusHit;
    boolean isLoseNow;
    boolean shieldHit;
    TimerHandler spriteTimerHandlerBonus;
    TimerHandler spriteTimerHandlerShield;

    public GameScene(){
        activity = BaseActivity.getSharedInstance();
        setBackground(new Background(Color.WHITE));
        mCamera= BaseActivity.getSharedInstance().mCamera;
        ball = Ball.getSharedInstance();
        enemies=new LinkedList();
        enemiesToBeAdded=new LinkedList();
        enemyCount=0;
        activity.score=new Score();
        bonusHit = true;
        shieldHit = true;
        activity.isShield = false;

        scoreText = new Text(0, 0, activity.mFont, "Score: 0000000",
                BaseActivity.getSharedInstance().getVertexBufferObjectManager());
        scoreText.setText(activity.score.toString());
        scoreText.setPosition(10, 3);

        attachChild(activity.backgroundSprite);
        attachChild(ball.sprite);
        attachChild(scoreText);
        activity.backgroundSprite.setZIndex(0);
        activity.ballSprite.setZIndex(2);
        activity.shieldSprite.setZIndex(3);
        scoreText.setZIndex(5);

        activity.setCurrentScene(this);
        sensorManager = (SensorManager) BaseActivity.getSharedInstance()
                .getSystemService(BaseGameActivity.SENSOR_SERVICE);
        SensorListener.getSharedInstance();
        sensorManager.registerListener(SensorListener.getSharedInstance(),
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);

        resetValues();
        createSpriteSpawnTimeHandler();
        registerUpdateHandler(detect);
        createScoreTimeHandler();
        createBonusSpawnTimeHandler();
        createShieldSpawnTimeHandler();

        for(int i=0;i<3;i++)
        {
            addTarget();
            enemyCount++;
        }
        isLoseNow=false;
    }

    public void moveBall() {
        ball.moveBall(activity.accelerometerSpeedX, activity.accelerometerSpeedY);
    }

    public void addTarget() {
        Random rand = new Random();

        int minX = 0;
        int maxX = (int) (mCamera.getWidth() - activity.enemySprite.getWidth());
        int rangeX = maxX - minX;
        int y = (int)-activity.enemySprite.getHeight();
        int x = rand.nextInt(rangeX)-minX;

        Sprite target = new Sprite(x,y,activity.enemyRegion,activity.getVertexBufferObjectManager());
        target.setSize(activity.mCamera.getWidth()/10.8f*1.5f, activity.mCamera.getWidth()/10.8f*1.5f);

        target.setZIndex(3);

        attachChild(target);

        int minDuration = 3;
        int maxDuration = 6;
        int rangeDuration = maxDuration - minDuration;
        int actualDuration = rand.nextInt(rangeDuration) + minDuration;
        int actualMoveX = rand.nextInt((int)(mCamera.getWidth()-target.getWidth()));

        MoveModifier mod = new MoveModifier(actualDuration,
                target.getX(), actualMoveX,
                target.getY(), mCamera.getHeight()+target.getHeight());

        target.registerEntityModifier(mod.deepCopy());

        enemiesToBeAdded.add(target);
    }

    public void addBonus()
    {
        if(activity.bonusSprite.hasParent())
        {
            detachChild(activity.bonusSprite);
        }

        activity.bonusSprite=new Sprite(0,0,activity.bonusRegion,
                activity.getVertexBufferObjectManager());
        activity.bonusSprite.setSize(activity.mCamera.getWidth() / 15.4f,
                activity.mCamera.getWidth() / 15.4f);
        activity.bonusSprite.setZIndex(1);

        Random rand=new Random();
        int rangex=(int)(mCamera.getWidth()-100-activity.bonusSprite.getWidth());
        int rangey=(int)(mCamera.getHeight()-100-activity.bonusSprite.getHeight());
        int x=rand.nextInt(rangex);
        int y=rand.nextInt(rangey);

        activity.bonusSprite.setPosition(x+50,y+50);

        attachChild(activity.bonusSprite);
        bonusHit=false;
    }

    public void addShield()
    {
        detachChild(activity.shieldSpriteP);

        activity.shieldSpriteP=new Sprite(0,0,activity.shieldRegion,
                activity.getVertexBufferObjectManager());
        activity.shieldSpriteP.setSize(activity.mCamera.getWidth()/15.4f,
                activity.mCamera.getWidth()/15.4f);
        activity.shieldSpriteP.setZIndex(1);

        Random rand=new Random();
        int rangex=(int)(mCamera.getWidth()-100-activity.shieldSpriteP.getWidth());
        int rangey=(int)(mCamera.getHeight()-100-activity.shieldSpriteP.getHeight());
        int x=rand.nextInt(rangex);
        int y=rand.nextInt(rangey);

        activity.shieldSpriteP.setPosition(x + 50, y + 50);

        attachChild(activity.shieldSpriteP);
        shieldHit = false;
    }

    private void createSpriteSpawnTimeHandler() {
        TimerHandler spriteTimerHandler;
        float mEffectSpawnDelay = 15f;

        spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
                new ITimerCallback() {

                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        if(enemyCount<10)
                        {
                            addTarget();
                            enemyCount++;
                        }
                    }
                });

        registerUpdateHandler(spriteTimerHandler);
    }

    private void createBonusSpawnTimeHandler() {
        float mEffectSpawnDelay = 7f;

        spriteTimerHandlerBonus = new TimerHandler(mEffectSpawnDelay, true,
                new ITimerCallback() {

                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        addBonus();
                    }
                });

        registerUpdateHandler(spriteTimerHandlerBonus);
    }

    private void createShieldSpawnTimeHandler() {
        float mEffectSpawnDelay = 30f;

        spriteTimerHandlerShield = new TimerHandler(mEffectSpawnDelay, true,
                new ITimerCallback() {

                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        if(!activity.isShield)
                            addShield();
                    }
                });

        registerUpdateHandler(spriteTimerHandlerShield);
    }

    private void createScoreTimeHandler() {
        TimerHandler scoreTimerHandler;
        float mEffectSpawnDelay = 1f;

        scoreTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
                new ITimerCallback() {

                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        activity.score.add();
                        scoreText.setText(activity.score.toString());
                    }
                });

        registerUpdateHandler(scoreTimerHandler);
    }

    public void removeSprite(final Sprite _sprite, Iterator it) {
        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                detachChild(_sprite);
            }
        });
        it.remove();
    }

    IUpdateHandler detect = new IUpdateHandler() {
        @Override
        public void reset() {
        }

        @Override
        public void onUpdate(float pSecondsElapsed) {
            synchronized (this) {

                Iterator<Sprite> targets = enemies.iterator();
                Sprite _target;
                Sprite _bonus;
                Sprite _shield;

                while (targets.hasNext()) {
                    _target = targets.next();
                    _bonus = activity.bonusSprite;
                    _shield = activity.shieldSpriteP;

                    if (_target.getY() >= _target.getWidth() + mCamera.getHeight()) {
                        removeSprite(_target, targets);
                        addTarget();
                        break;
                    }

                    if (isColliding(activity.ballSprite.getX()+activity.ballSprite.getWidth()/2,
                            activity.ballSprite.getY()+activity.ballSprite.getWidth()/2,
                            _target.getX()+activity.enemySprite.getWidth()/2,
                            _target.getY()+activity.enemySprite.getHeight()/2,
                            (int)activity.enemySprite.getHeight()/2)) {
                        enemyCollide(_target, targets);
                        break;
                    }
                    if (!bonusHit)
                        if(isColliding(activity.ballSprite.getX()+activity.ballSprite.getWidth()/2,
                            activity.ballSprite.getY()+activity.ballSprite.getWidth()/2,
                            _bonus.getX()+activity.bonusSprite.getWidth()/2,
                            _bonus.getY()+activity.bonusSprite.getHeight()/2,
                            (int)activity.bonusSprite.getHeight()/2))
                    {
                        bonusCollide();
                    }
                    if(!shieldHit)
                        if(isColliding(activity.ballSprite.getX()+activity.ballSprite.getWidth()/2,
                                activity.ballSprite.getY()+activity.ballSprite.getWidth()/2,
                                _shield.getX()+activity.shieldSpriteP.getWidth()/2,
                                _shield.getY()+activity.shieldSpriteP.getHeight()/2,
                                (int)activity.shieldSpriteP.getHeight()/2))
                        {
                            shieldCollide();
                        }
                }
                enemies.addAll(enemiesToBeAdded);
                enemiesToBeAdded.clear();
                sortChildren();
            }
        }
    };

    public void enemyCollide(Sprite _target, Iterator<Sprite> targets)
    {
        if(activity.isShield)
        {
            activity.isShield = false;
            detachChild(activity.shieldSprite);
            createExplosion(_target.getX() + _target.getWidth() / 2,
                    _target.getY() + _target.getHeight() / 2, Color.RED,
                    15, mCamera.getWidth()/10);

            createExplosion(ball.sprite.getX() + ball.sprite.getWidth() / 2,
                    ball.sprite.getY() + ball.sprite.getHeight() / 2, Color.BLUE, 5, mCamera.getWidth() / 17);

            if(activity.isSfx())
                activity.dropShieldSound.play();

            removeSprite(_target, targets);
            addTarget();
        }
        else {
            if (!isLoseNow) {
                if (activity.isMusic()) {
                    activity.battleTheme.pause();
                    activity.battleTheme.seekTo(0);
                }
                if (activity.isSfx()) {
                    activity.explSound.play();
                }
                activity.score.onLose();
                activity.score.score-=20;
                activity.editor.putInt("time", activity.prefs.getInt("time",0)-2);
                activity.editor.commit();

                activity.vibrator.vibrate(300);


                createExplosion(ball.sprite.getX() + ball.sprite.getWidth() / 2,
                        ball.sprite.getY() + ball.sprite.getHeight() / 2, Color.RED, 10, mCamera.getWidth()/20);

                detachChild(ball.sprite);
                detachChild(scoreText);

                DelayModifier dMod = new DelayModifier(2) {
                    @Override
                    protected void onModifierFinished(IEntity pItem) {
                        if (activity.isMusic())
                            activity.awesomeness.resume();
                        restart();
                    }
                };
                registerEntityModifier(dMod);
                isLoseNow = true;
            }
        }
    }

    public void bonusCollide()
    {
        activity.score.onCollect();
        detachChild(activity.bonusSprite);
        bonusHit=true;
        if (activity.isSfx()) {
            activity.bonusSound.play();
        }
        scoreText.setText(activity.score.toString());
        spriteTimerHandlerBonus.reset();

        activity.editor.putInt("coinsCollected", activity.prefs.getInt("coinsCollected", 0) + 1);
        activity.editor.commit();
    }

    public void shieldCollide()
    {
        detachChild(activity.shieldSpriteP);
        attachChild(activity.shieldSprite);

        if(activity.isSfx())
            activity.getShieldSound.play();

        shieldHit = true;
        activity.isShield = true;

        spriteTimerHandlerShield.reset();

        activity.editor.putInt("shieldsCollected", activity.prefs.getInt("shieldsCollected",0)+1);
        activity.editor.commit();
    }

    public void resetValues() {
        enemyCount = 0;
        ball.restart();
        clearChildScene();
        registerUpdateHandler(new GameLoopUpdateHandler());
        if(activity.isMusic())
        {
            activity.awesomeness.pause();
            activity.awesomeness.seekTo(0);
            activity.battleTheme.play();
        }
        activity.score.reset();
    }

    public void restart() {

        activity.runOnUpdateThread(new Runnable() {

            @Override
            public void run() {
                detachChildren();
                activity.setCurrentScene(new GameoverScene());
            }
        });
        enemiesToBeAdded.clear();
        enemies.clear();
    }

    public void detach() {
        clearUpdateHandlers();
        enemies.clear();
        detachChildren();
        Ball.instance = null;
    }

    public boolean isColliding(float cx, float cy, float cx2, float cy2, int rad)
    {
        float xd = cx - cx2;
        float yd = cy - cy2;

        int sumRadius = (int)(rad + activity.ballSprite.getWidth()/2);
        int sqrRadius = (sumRadius * sumRadius);

        int distSqr = (int)((xd * xd) + (yd * yd));

        if (distSqr < sqrRadius)
        {
            return true;
        }

        return false;
    }

    private void createExplosion(final float posX, final float posY,final Color color, final int rec, final float size) {
        int mNumPart = 25;
        int mTimePart = 2;

        CircleParticleEmitter particleEmitter = new CircleParticleEmitter(posX, posY, size);
        IEntityFactory recFact1 = new IEntityFactory() {
            @Override
            public Rectangle create(float pX, float pY) {
                Rectangle rect = new Rectangle(posX, posY, rec, rec, activity.getVertexBufferObjectManager());
                rect.setColor(color);
                return rect;
            }
        };

        final ParticleSystem particleSystem = new ParticleSystem(recFact1, particleEmitter, 200, 700, mNumPart);
        particleSystem.addParticleInitializer(new VelocityParticleInitializer(
                -mCamera.getHeight() / 12, mCamera.getHeight() / 12, -mCamera.getHeight() / 12, mCamera.getHeight() / 12));
        particleSystem.addParticleModifier(new AlphaParticleModifier(0, mTimePart * 0.8f, 1, 0));
        particleSystem.addParticleModifier(new RotationParticleModifier(0,mTimePart,50,480));

        particleSystem.setZIndex(10);

        attachChild(particleSystem);

        registerUpdateHandler(new TimerHandler(mTimePart, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                particleSystem.detachSelf();
                sortChildren();
                unregisterUpdateHandler(pTimerHandler);
            }
        }));
    }
}
