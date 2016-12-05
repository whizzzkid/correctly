package com.example.lenovo.correctly.models;

import io.realm.RealmObject;

public class Challenge extends RealmObject {
    public String challenge, challenge_translation;
    public int order;
    private int state = DataModelConstants.CHALLENGE_STATE_NEW;

    public void setOrder(int order) {
        this.order = order;
    }

    public void setChallenge(String challenge, String
            challenge_translation) {
        this.challenge = challenge;
        this.challenge_translation = challenge_translation;
    }

    public void goToNextState() {
        this.state = state++;
    }
}
