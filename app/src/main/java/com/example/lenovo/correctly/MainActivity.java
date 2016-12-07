package com.example.lenovo.correctly;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.lenovo.correctly.fragments.TopicsFragment;
import com.example.lenovo.correctly.models.Challenge;
import com.example.lenovo.correctly.models.DataModelConstants;
import com.example.lenovo.correctly.models.Level;
import com.example.lenovo.correctly.models.Topic;
import com.example.lenovo.correctly.utils.FragmentLoader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {


    private static final int REQUEST_MICROPHONE = 2;

    public Challenge createChallenge(Realm realm, int order, String
            challenge, String translation) {
        Challenge c = realm.createObject(Challenge.class);
        c.setOrder(order);
        c.setChallenge(challenge, translation);
        return c;
    }

    public Level createLevel(Realm realm, int order, String name, String img,
                             Topic parent) {
        Level l = realm.createObject(Level.class);
        l.setOrder(order);
        l.setLevelName(name);
        l.setLevelImg(img);
        l.setParent(parent);
        return l;
    }

    public Topic createTopic(Realm realm, int order, String name, String img) {
        Topic t = realm.createObject(Topic.class);
        t.setTopicName(name);
        t.setTopicImg(img);
        t.setOrder(order);
        return t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(
                R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Adding back button if we're in a fragment other than main fragment.
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager
                .OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    toolbar.setNavigationOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);
        }




        Realm.init(this);
        Log.v(TAG, "Populating DB");
        Realm db = Realm.getDefaultInstance();
        db.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Topic> results = realm.where(
                        Topic.class).findAll();
                if (results.size() == 0) {
                    // Adding Topic.
                    Topic travel = createTopic(realm, 0, "Travel",
                            "images/travel.jpg");
                    Topic school = createTopic(realm, 1, "School",
                            "images/school.jpg");
                    Topic city = createTopic(realm, 2, "City",
                            "images/city.jpg");
                    Topic family = createTopic(realm, 1, "Family",
                            "images/family.jpg");

                    // Adding Travel Levels.
                    Level travel_level0 = createLevel(realm, 0, "Basic Words",
                            "images/travel_words.jpg", travel);


                    Level travel_level1 = createLevel(realm, 1, "Sentences",
                            "images/travel_sentences.jpg", travel);

                    // Adding School Levels.
                    Level school_level0 = createLevel(realm, 0, "Basic Words",
                            "images/school_words.jpg", school);
                    Level school_level1 = createLevel(realm, 0, "Sentences",
                            "images/school_sentences.jpg", school);

                    // Adding Travel Challenges.
                    travel_level0.challenges.add(
                            createChallenge(
                                    realm, 0, "Bonjour", "Good Morning"));
                    travel_level0.challenges.add(
                            createChallenge(
                                    realm, 1, "Magasin", "Store"));
                    travel_level1.challenges.add(
                            createChallenge(
                                    realm, 0, "Bonjour voici le$magasin",
                                    "Hello this is the store"
                            )
                    );

                    // Adding School Challenges.
                    school_level0.challenges.add(
                            createChallenge(realm, 0, "Bonjour", "Good Morning"));
                    school_level0.challenges.add(
                            createChallenge(realm, 1, "Fille", "Boy"));
                    school_level0.challenges.add(
                            createChallenge(realm, 2, "Aimer", "Like"));
                    school_level0.challenges.add(
                            createChallenge(realm, 3, "Chanter", "Sing"));
                    school_level0.challenges.add(
                            createChallenge(realm, 4, "Chanson", "Song"));
                    school_level0.challenges.add(
                            createChallenge(realm, 5, "Cuisiner", "Cook"));
                    school_level0.challenges.add(
                            createChallenge(realm, 6, "Nous", "We"));
                    school_level0.challenges.add(
                            createChallenge(realm, 7, "Vous", "You"));
                    school_level0.challenges.add(
                            createChallenge(realm, 8, "Rouge", "Red"));
                    school_level0.challenges.add(
                            createChallenge(realm, 9, "Blanche", "White"));
                    school_level1.challenges.add(
                            createChallenge(realm, 0, "Mon crayon est$rouge",
                                    "My Pencil Is Red"));
                    school_level1.challenges.add(
                            createChallenge(realm, 1, "Nous chantons des " +
                                    "chansons", "We Sing Songs"));
                    school_level1.challenges.add(
                            createChallenge(realm, 2, "Elle aime cuisiner",
                                    "She Likes To Cook"));
                    school_level1.challenges.add(
                            createChallenge(realm, 3, "Ma$gomme est blanche",
                                    "My Eraser Is White"));

                    // Addding everything back.
                    travel.levels.add(travel_level0);
                    travel.levels.add(travel_level1);
                    school.levels.add(school_level0);
                    school.levels.add(school_level1);

                }
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            new FragmentLoader(getFragmentManager(), getIntent().getExtras(),
                    new TopicsFragment()).Load();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_reset_progress: {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.deleteAll();
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);

                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_words: {
            }

            case R.id.nav_sentences: {
            }

            case R.id.nav_manage: {
            }

            case R.id.nav_share: {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<Challenge> wordsLearned = realm.where(Challenge.class).equalTo("state",
                        DataModelConstants.CHALLENGE_STATE_DONE).findAll();
                int numLearned = wordsLearned.size();
                Iterator<Challenge> words = wordsLearned.iterator();
                Set<String> wordsLearn = new HashSet<>();

                while (words.hasNext()) {
                    Challenge word = words.next();
                    wordsLearn.add(word.challenge);
                }

                String shareBody = "I learned " + numLearned + " new french " +
                        "words: " + TextUtils.join(", ", wordsLearn);
                Intent sharingIntent = new Intent(android.content.Intent
                        .ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Progress"));
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
