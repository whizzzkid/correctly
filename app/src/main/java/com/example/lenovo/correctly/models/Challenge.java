package com.example.lenovo.correctly.models;

import io.realm.RealmObject;

public class Challenge extends RealmObject {
    public String challenge, challenge_translation;
    public int order;
    public boolean isSeen = false;
    public int state = DataModelConstants.CHALLENGE_STATE_NEW;

    public void setOrder(int order) {
        this.order = order;
    }

    public void setChallenge(String challenge, String
            challenge_translation) {
        this.challenge = challenge;
        this.challenge_translation = challenge_translation;
    }

    public void goToNextState(Boolean wasCorrect) {
        this.isSeen = true;
        if (wasCorrect) {
            this.state = Math.min(state+1, DataModelConstants
                    .CHALLENGE_STATE_DONE);
        } else {
            this.state = Math.max(state-2, DataModelConstants
                    .CHALLENGE_STATE_NEW);
        }
    }
}
