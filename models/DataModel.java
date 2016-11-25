package com.example.lenovo.correctly.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class DataModel {
    public int STATE_NEW = 0;
    public int STATE_WIP = 1;
    public int STATE_DONE = 2;
    public int CHALLENGE_STATE_NEW = 0;
    public int CHALLENGE_STATE_LEARNED = 1;
    public int CHALLENGE_STATE_MASTERED = 2;
    public int CHALLENGE_STATE_REVISE = 3;
    public int CHALLENGE_STATE_DONE = 4;

    public class Topic extends RealmObject {
        String topic_name;
        public int state = STATE_NEW;
        public RealmList<Level> levels;

        public String getTopicName() {
            return topic_name;
        }

        public void setTopicName(String topic_name) {
            this.topic_name = topic_name;
        }

        public RealmResults<Level> getAllLevels() {
            return this.levels.sort("order");
        }

        public int getLevelCount() {
            return this.levels.size();
        }

        public int getCompleteLevelCount() {
            return this.levels.where().equalTo("state", STATE_DONE).findAll
                    ().size();
        }
    }

    public class Level extends RealmObject {
        String level_name;
        int order;
        int state = STATE_NEW;
        public RealmList<Challenge> challenges;

        public String getLevelName () {
            return level_name;
        }

        public void setLevelName(String level_name) {
            this.level_name = level_name;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public void goToNextState() {
            if (this.state < STATE_DONE) {
                this.state = state++;
            }
        }

        public int getChallengeCount () {
            return this.challenges.size();
        }

        public int getChallengeDone () {
            return this.challenges.where().equalTo("state",
                    CHALLENGE_STATE_DONE).findAll().size();
        }
    }

    public class Challenge extends RealmObject {
        String challenge, challenge_translation;
        int order;
        int state = CHALLENGE_STATE_NEW;

        public void setOrder (int order) {
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
}
