package com.example.lenovo.correctly.utils;

import android.util.Log;

import com.example.lenovo.correctly.models.Challenge;
import com.example.lenovo.correctly.models.DataModelConstants;
import com.example.lenovo.correctly.models.Topic;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

import static android.content.ContentValues.TAG;

public class ChallengeManager {
    private RealmList<Challenge> challenges;
    private Realm realm = Realm.getDefaultInstance();


    public ChallengeManager(String topic, String level) {
        challenges = realm.where(Topic.class).equalTo(
                "topic_name", topic).findFirst().getAllLevels().where()
                .equalTo("level_name", level).findFirst().challenges;
    }

    public Challenge getNextChallenge(final Challenge challenge, final
    Boolean wasCorrect) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                challenge.goToNextState(wasCorrect);
            }
        });
        Log.v(TAG, String.valueOf(challenge.state));
        // Getting new challenge.
        int previousChallengeOrder = challenge.order;
        Challenge newChallenge = this.challenges.where().equalTo("order",
                ++previousChallengeOrder).findFirst();
        if (newChallenge == null) {
            newChallenge = this.challenges.where().equalTo("order",
                    0).findFirst();
        }
        return newChallenge;
    }

    public Challenge getFirstChallenge() {
        return this.challenges.first();
    }
    
    public Map<String, Integer> getProgress() {
        Map<String, Integer> progress = new HashMap<String, Integer>();
        progress.put("total", this.challenges.size());
        progress.put("new", (int) this.challenges.where().equalTo("state",
                DataModelConstants.CHALLENGE_STATE_NEW).count());
        progress.put("learned", (int) this.challenges.where().equalTo("state",
                DataModelConstants.CHALLENGE_STATE_LEARNED).count());
        progress.put("mastered", (int) this.challenges.where().equalTo("state",
                DataModelConstants.CHALLENGE_STATE_MASTERED).count());
        progress.put("revising", (int) this.challenges.where().equalTo("state",
                DataModelConstants.CHALLENGE_STATE_REVISE).count());
        progress.put("done", (int) this.challenges.where().equalTo("state",
                DataModelConstants.CHALLENGE_STATE_DONE).count());
        return progress;
    }
}
