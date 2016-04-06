package com.example.ttran.spelling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class Start extends AppCompatActivity {
    String mName;
    EditText nameEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nameEdit = (EditText)findViewById(R.id.nameEdit);
        Intent intent = getIntent();
        String player = intent.getStringExtra("player");
        if (player != null && player.length() > 0){
            nameEdit.setText(player);
            nameEdit.selectAll();
        }

    }

    public void exit(View v){
        //Activity parent = this.getParent();
        Intent myIntent = new Intent(getApplicationContext(),MainActivity.class);
        myIntent.putExtra("name",nameEdit.getText().toString());
        startActivityForResult(myIntent,0);
        finish();
    }
}
