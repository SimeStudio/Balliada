package com.siwiesinger.pinbo;

import com.google.android.gms.games.Games;

public class Score{
    BaseActivity activity;
    int score;
    public Score()
    {
        activity=BaseActivity.getSharedInstance();
        score=0;
    }
    public void onCollect ()
    {
        score+=100;
    }

    @Override
    public String toString() {
        return "Score: " + score;
    }
    public void onLose()
    {
        checkDB();
    }
    public void checkDB ()
    {
        if(score>activity.prefs.getInt("highscore",0))
        {
            activity.editor=activity.prefs.edit();
            activity.highscore=score;
            activity.editor.putInt("highscore",score);
            activity.editor.commit();
        }
        else
        {
            activity.highscore=activity.prefs.getInt("highscore",0);
        }

        activity.editor.putInt("deaths", activity.prefs.getInt("deaths", 0) + 1);
        activity.editor.commit();

        if(activity.mGoogleApiClient != null && activity.mGoogleApiClient.isConnected()) {
            Games.Achievements.unlock(activity.mGoogleApiClient, activity.getResources().getString(R.string.achievement_balliada_starter));
            if (activity.highscore > 1000)
                Games.Achievements.unlock(activity.mGoogleApiClient, activity.getResources().getString(R.string.achievement_balliada_amateur));
            if (activity.highscore > 5000)
                Games.Achievements.unlock(activity.mGoogleApiClient, activity.getResources().getString(R.string.achievement_balliada_pro));
            if (activity.highscore > 10000)
                Games.Achievements.unlock(activity.mGoogleApiClient, activity.getResources().getString(R.string.achievement_balliada_master));
            if (activity.highscore > 15000)
                Games.Achievements.unlock(activity.mGoogleApiClient, activity.getResources().getString(R.string.achievement_balliada_god));

            if(activity.prefs.getInt("deaths", 0)>=10)
                Games.Achievements.unlock(activity.mGoogleApiClient, activity.getResources().getString(R.string.achievement_balliada_lover));
            if(activity.prefs.getInt("deaths", 0)>=100)
                Games.Achievements.unlock(activity.mGoogleApiClient, activity.getResources().getString(R.string.achievement_true_balliada_lover));

            Games.Leaderboards.submitScore(activity.mGoogleApiClient, activity.getResources().getString(R.string.leaderboard_balliada_leaderboard), activity.highscore);
        }
    }
    public int getScore()
    {
        return score;
    }

    public void add()
    {
        score+=10;
        activity.editor.putInt("time", activity.prefs.getInt("time",0)+1);
        activity.editor.commit();
    }
    public void reset()
    {
        score=0;
    }
}
