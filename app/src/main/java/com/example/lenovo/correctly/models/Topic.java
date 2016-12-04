package com.example.lenovo.correctly.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class Topic extends RealmObject {
    public int state = DataModelConstants.LEVEL_STATE_NEW;
    public RealmList<Level> levels;
    private String topic_name, topic_img;
    private int order;

    public String getTopicName() {
        return topic_name;
    }

    public void setTopicName(String topic_name) {
        this.topic_name = topic_name;
    }

    public String getTopicImg() {
        return topic_img;
    }

    public void setTopicImg(String topic_img) {
        this.topic_img = topic_img;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public RealmResults<Level> getAllLevels() {
        return this.levels.sort("order");
    }

    public int getLevelCount() {
        return this.levels.size();
    }

    public int getCompleteLevelCount() {
        return this.levels.where().equalTo("state", DataModelConstants
                .LEVEL_STATE_DONE)
                .findAll
                        ().size();
    }
}
