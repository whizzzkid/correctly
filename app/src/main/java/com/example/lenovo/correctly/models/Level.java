package com.example.lenovo.correctly.models;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class Level extends RealmObject {
    public RealmList<Challenge> challenges;
    public Topic parent;
    private String level_name, level_img;
    private int order;
    private int state = DataModelConstants.LEVEL_STATE_NEW;

    public String getLevelName() {
        return level_name;
    }

    public void setLevelName(String level_name) {
        this.level_name = level_name;
    }

    public String getLevelImg() {
        return level_img;
    }

    public void setLevelImg(String level_img) {
        this.level_img = level_img;
    }

    public Topic getParent() {
        return parent;
    }

    public void setParent(Topic parent) {
        this.parent = parent;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getState() {
        return this.state;
    }

    public void goToNextState() {
        if (this.state < DataModelConstants.LEVEL_STATE_DONE) {
            this.state = state++;
        }
    }

    public int getChallengeCount() {
        return this.challenges.size();
    }

    public int getChallengeDone() {
        return this.challenges.where().equalTo("state", DataModelConstants
                .CHALLENGE_STATE_DONE).findAll().size();
    }

    public boolean isLocked() {
        if (order == DataModelConstants.LEVEL_START_INDEX) {
            return false;
        } else {
            Realm realm = Realm.getDefaultInstance();
            Topic parentFromRealm = realm.where(parent.getClass()).equalTo
                    ("name", parent.getTopicName()).findFirst();
            Level previousLevel = parentFromRealm.getAllLevels().where()
                    .equalTo("order", order--).findFirst();
            return previousLevel.getState() != DataModelConstants
                    .LEVEL_STATE_DONE;
        }
    }
}
