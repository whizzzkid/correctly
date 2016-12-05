package com.example.lenovo.correctly.utils;

import com.example.lenovo.correctly.models.Challenge;
import com.example.lenovo.correctly.models.Level;
import com.example.lenovo.correctly.models.Topic;

import io.realm.Realm;

public class ChallengeManager {
    private Realm realm = Realm.getDefaultInstance();
    private String topic, level;

    public ChallengeManager(String topic, String level) {
        this.topic = topic;
        this.level = level;
    }

    public Challenge getNextChallenge(int previousChallenge) {
        Topic topic = realm.where(Topic.class).equalTo("topic_name",
                this.topic).findFirst();
        Level level = topic.getAllLevels().where().equalTo("level_name", this
                .level).findFirst();
        Challenge challenge = level.challenges.where().equalTo("order",
                previousChallenge++).findFirst();
        if (challenge == null) {
            challenge = level.challenges.where().equalTo("order",
                    0).findFirst();
        }
        return challenge;
    }
}
