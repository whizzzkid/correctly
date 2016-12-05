package com.example.lenovo.correctly.models;

import io.realm.RealmObject;

import static com.google.common.primitives.UnsignedInts.max;
import static com.google.common.primitives.UnsignedInts.min;

public class Challenge extends RealmObject {
    public String challenge, challenge_translation;
    public int order;
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
        if (wasCorrect) {
            state = min(state+1, DataModelConstants.CHALLENGE_STATE_DONE);
        } else {
            state = max(state-2, DataModelConstants.CHALLENGE_STATE_NEW);
        }
    }
}
