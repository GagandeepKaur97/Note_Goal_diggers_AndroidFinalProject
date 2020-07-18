package com.example.note_goal_diggers_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NotesActivity extends AppCompatActivity {
    GridView gridView;


    FloatingActionButton floatingActionButton;
   SimpleDatabase dataBaseHelper;
    String audioPath;
    int ccid;
    SearchView searchView;

    List<CategoryModel> filterList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
    }
}