package com.example.ttran.spelling;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static java.util.Locale.ENGLISH;

public class MainActivity extends AppCompatActivity implements SpellCheckerSession.SpellCheckerSessionListener {
    SpellCheckerSession session;
    TextView tv1;
    EditText ed1;
    Button btn1;
    Button restartBtn;
    Button spelledWordsBtn;
    ListView listView;
    TextView nameView;
    TextView scoreView;
    TextView askSomeoneView;

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    TextView wordCount;
    int iwordCount = 0;
//    String sb = "";
    String sCount = "";
    boolean bCorrect = false;
    String msg = "";
    String spelledWord = "";
    MediaPlayer mediaPlayer;
    SQLiteDatabase myDatabase;
    int mScore = 0;
    String mPlayer;

    public void changePlayer(){
        Intent intent = new Intent(getApplicationContext(),Start.class);
        intent.putExtra("player",mPlayer);
        startActivity(intent);
    }

    public void checkSpelling(View v){
        // Toast.makeText(getApplicationContext(),"before Text Service Mgr",Toast.LENGTH_LONG).show();
        final TextServicesManager tsm = (TextServicesManager)getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE);
        // Toast.makeText(getApplicationContext(),"before Text Session",Toast.LENGTH_LONG).show();
        session = tsm.newSpellCheckerSession(null,ENGLISH,this,false);
        if (ed1.getText().length() > 0){
            // Toast.makeText(getApplicationContext(),"before Text Info",Toast.LENGTH_LONG).show();
            TextInfo[] textInfo = {new TextInfo(ed1.getText().toString())};
            if (session != null) {
                // Toast.makeText(getApplicationContext(), "session is good", Toast.LENGTH_LONG).show();
                session.getSentenceSuggestions(textInfo, 3);

            }
            else {

                Toast.makeText(getApplicationContext(), "session is null", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),ed1.getText().toString(),Toast.LENGTH_LONG).show();
                //session.getSentenceSuggestions(textInfo,3);
            }
        }
        scoreView.setText(String.valueOf(mScore));
    }

    public void soundAlarm(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        // player = MediaPlayer.create(getApplicationContext(),uri);

        mediaPlayer =MediaPlayer.create(this,R.raw.airhorn);
        // mediaPlayer.setDataSource(this, uri);
        //mediaPlayer.setAudioStreamType(AudioManager.AU);

        //mediaPlayer.prepare();
        mediaPlayer.start();
        //mediaPlayer.stop();
        //mediaPlayer.reset();
        //mediaPlayer.release();
    }

    public void soundNotification(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //player = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer = MediaPlayer.create(this,R.raw.glass_ping);
        //mediaPlayer.setDataSource(this, uri);
        //mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);

        //mediaPlayer.prepare();
        mediaPlayer.start();
        //mediaPlayer.stop();
        //mediaPlayer.reset();
        //mediaPlayer.release();
    }

    public boolean playerExist(String name){
        boolean bExist = false;
        String [] args = {name};
        Cursor result = myDatabase.rawQuery("SELECT * FROM player where name=?",args);
        result.moveToFirst();
        int nameIndex = result.getColumnIndex("name");
        if (result.getString(nameIndex).compareToIgnoreCase(name) == 0){
            bExist = true;
        }
        return bExist;
    }

    public int getPlayerScore(String name){
        int score = 0;
        String [] args = {name};
        Cursor result = myDatabase.rawQuery("SELECT * FROM player wher name=?", args);
        if (result != null && result.moveToFirst()){
            int scoreIndex = result.getColumnIndex("score");
            score = result.getInt(scoreIndex);
        }
        return score;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        //tv1 = (TextView)findViewById(R.id.tv1);
        ed1 = (EditText)findViewById(R.id.editText1);

        restartBtn = (Button)findViewById(R.id.restartBtn);
        btn1 = (Button)findViewById(R.id.checkBtn);
        spelledWordsBtn = (Button)findViewById(R.id.spelledWordsBtn);
        nameView = (TextView)findViewById(R.id.nameView);
        scoreView = (TextView)findViewById(R.id.scoreTV);
        listView = (ListView)findViewById(R.id.listView);
        listView.setVisibility(View.INVISIBLE);
        wordCount = (TextView)findViewById(R.id.countText);
        askSomeoneView = (TextView)findViewById(R.id.askSomeoneTV);
        askSomeoneView.animate().alpha(1f).setDuration(5000);
//        int times = 0;
//        while (times < 200){
//            askSomeoneView.animate().alpha(.01f).setDuration(5000);
//            askSomeoneView.animate().alpha(1f).setDuration(5000);
//            times++;
//        } ;

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);

        mediaPlayer = new MediaPlayer();

        myDatabase = openOrCreateDatabase("spelling",MODE_PRIVATE,null);

        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS player (name VARCHAR, score INT(5))");

        Intent startIntent = getIntent();
        Bundle startExtra = startIntent.getExtras();
        mPlayer = startExtra.getString("name");
        nameView.setText(mPlayer);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Cursor result = myDatabase.rawQuery("SELECT * FROM player WHERE name = '" + mPlayer + "'", null);

        if (result != null && result.moveToFirst()){
            int scoreColIndex = result.getColumnIndex("score");
            mScore = result.getInt(scoreColIndex);
            scoreView.setText(String.valueOf(mScore));
        }
        else {
            myDatabase.execSQL("INSERT INTO player (name) VALUES('" + mPlayer + "')");
        }

//        Intent startIntent = new Intent(getApplicationContext(),Start.class);
//        startActivityForResult(startIntent,0);

//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                 if (ed1.getText().length() > 0){
//                    TextInfo[] textInfos = {new TextInfo(ed1.getText().toString())};
//                    //Toast.makeText(getApplicationContext(),ed1.getText().toString(),Toast.LENGTH_LONG).show();
//                    session.getSentenceSuggestions(textInfos,3);
//                }
//            }
//        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed1.setText("");
                wordCount.setText("0000");
                arrayList.clear();
                adapter.clear();
            }
        });

        spelledWordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(),SpelledWordList.class);
                myIntent.putStringArrayListExtra("arrayList",arrayList);
                startActivityForResult(myIntent,0);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.change_player) {
            changePlayer();
            return true;
        }
        else if (id == R.id.exit ){
            exit();
        }


        return super.onOptionsItemSelected(item);
    }

    public boolean exit(){
        if (mPlayer != null)
            mediaPlayer.release();
        this.finish();
        return true;
    }
    @Override
    public void onGetSuggestions(SuggestionsInfo[] results) {

    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
        bCorrect = false;
        StringBuilder sb = new StringBuilder();

        int count = results[0].getSuggestionsCount();
//        for (int i=0; i < count; i++){
//            sb.append(results[0].getSuggestionsInfoAt(0).getSuggestionAt(i));
//        }

        spelledWord = ed1.getText().toString();
        //final String sb = "";


        //if (sb.compareToIgnoreCase(spelledWord) == 0)
        //Toast.makeText(getApplicationContext(),"That's Correct!",Toast.LENGTH_LONG).show();
        //else
        //   Toast.makeText(getApplicationContext(),"That's Incorrect!",Toast.LENGTH_LONG).show();

        // if (sb.compareToIgnoreCase(spelledWord) == 0)
        if ((results[0].getSuggestionsInfoAt(0).getSuggestionAt(0)).compareToIgnoreCase(spelledWord) == 0)
        {
            soundNotification();
            iwordCount++;
            arrayList.add(spelledWord);
            listView.setAdapter(adapter);
            if (iwordCount < 10)
                sCount = "000" + String.valueOf(iwordCount);
            else if (iwordCount < 99)
                sCount = "00" + String.valueOf(iwordCount);
            else if (iwordCount < 999)
                sCount = "0" + String.valueOf(iwordCount);
            else
                sCount = String.valueOf(iwordCount);
            bCorrect = true;
            wordCount.setText(sCount);
            Toast.makeText(getApplicationContext(), "That's Correct!", Toast.LENGTH_LONG).show();
            ed1.setText("");
            soundNotification();
            mScore += 10;
            scoreView.setText(String.valueOf(mScore));
            myDatabase.execSQL("UPDATE player SET score = " + mScore + " WHERE name = '" + mPlayer + "'");
            launchReward();

        }
        else
        {
            msg = "The correct Spelling of the word is: " + results[0].getSuggestionsInfoAt(0).getSuggestionAt(0);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            soundAlarm();
        }
    }

    public void launchReward(){
        Intent intent = new Intent(getApplicationContext(),RewardActivity.class);
        startActivity(intent);
    }
}
