package com.example.lenovo.correctly.utils;

import android.util.Log;
import android.util.SparseArray;

import com.example.lenovo.correctly.models.Challenge;
import com.example.lenovo.correctly.models.DataModelConstants;
import com.example.lenovo.correctly.models.Level;
import com.example.lenovo.correctly.models.Topic;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ChallengeManager {
    private RealmList<Challenge> challenges;
    private Realm realm = Realm.getDefaultInstance();
    private SparseArray<Challenge> challengeQueue = new SparseArray<>();
    private int challengeCounter = 1;
    private String topic_name, level_name;
    private Level level;
    private Topic topic;
    public ChallengeManager(String topic, String level) {
        this.topic_name = topic;
        this.level_name = level;
        this.topic = realm.where(Topic.class).equalTo(
                "topic_name", topic_name).findFirst();
        this.level = this.topic.getAllLevels().where()
                .equalTo("level_name", level_name).findFirst();
        this.challenges = this.level.challenges;
    }

    public void pushResult(final Challenge challenge, final Boolean
            wasCorrect) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                challenge.goToNextState(wasCorrect);
            }
        });
        boolean existsInQueue = false;
        for (int i = 0; i<challengeQueue.size(); i++) {
            if(challengeQueue.valueAt(i).order == challenge.order) {
                existsInQueue = true;
                break;
            }
        }
        if (!existsInQueue) {
            int position = challengeCounter;
            if (wasCorrect) {
                position += challenge.state + DataModelConstants.SKIP_IF_CORRECT;
            } else {
                position += DataModelConstants.SKIP_IF_NOT_CORRECT;
            }
            placeInQueue(challenge, position);
            Log.v("position", String.valueOf(position));
            Log.v("queue", String.valueOf(challengeQueue));
        }
    }

    private void placeInQueue(Challenge challenge, int position) {
        if (challenge.state != DataModelConstants.CHALLENGE_STATE_DONE ) {
            Challenge existing = null;
            try {
                existing = challengeQueue.get(position);
            } catch (Exception ignored) {
            }
            if (existing != null) {
                placeInQueue(challenge, position + 1);
            } else {
                challengeQueue.put(position, challenge);
            }
        }
    }

    public Challenge getNextChallenge() {
        // Getting new challenge.
        challengeCounter++;
        Challenge newChallenge;
        if (challengeQueue.get(challengeCounter) != null) {
            newChallenge = challengeQueue.get(challengeCounter);
            challengeQueue.remove(challengeCounter);
        } else {
            RealmQuery<Challenge> find = this.challenges.where().notEqualTo
                    ("state", DataModelConstants.CHALLENGE_STATE_DONE);
            int firstFound = -1;
            for(int i = 0; i < challengeQueue.size(); i++) {
                if(challengeQueue.keyAt(i) > challengeCounter) {
                    if (firstFound == -1) {
                        firstFound = i;
                    }
                    find.notEqualTo("order", challengeQueue.valueAt(i).order);
                    Log.v("order not:", String.valueOf(challengeQueue.valueAt(i).order));
                }
            }

            RealmResults<Challenge> findChallenge = find.findAllSorted("state");
            if (findChallenge.size() == 0 && challengeQueue.size() != 0) {
                newChallenge = challengeQueue.valueAt(firstFound);
            } else {
                newChallenge = find.findAllSorted("state").first();
            }
        }
        return newChallenge;
    }

    public Challenge getFirstChallenge() {
        return this.challenges.where().findAllSorted("state").first();
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
