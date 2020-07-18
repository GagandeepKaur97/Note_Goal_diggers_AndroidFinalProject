package com.example.note_goal_diggers_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Notes");

        gridView = findViewById(R.id.gridView);

        floatingActionButton = findViewById(R.id.floatingActionButton);

        dataBaseHelper = new  SimpleDatabase(this);
        searchView = findViewById(R.id.searchView);

        filterList = new ArrayList<>();

        loadNotes();

        final IconAdapter iconAdapter = new IconAdapter(this, CategoryModel.listNotes);
        gridView.setAdapter(iconAdapter);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this,DescriptionActivity.class);
                startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()) {

                    if(!newText.isEmpty()){
                        filterList.clear();
                        for(int i = 0;i < CategoryModel.listNotes.size();i++){
                            CategoryModel categoryModelnote =  CategoryModel.listNotes.get(i);
                            if(categoryModelnote.title.contains(newText)){
                                filterList.add(categoryModelnote);
                            }
                        }

                        IconAdapter iconAdapter1 = new IconAdapter(NotesActivity.this,filterList);
                        gridView.setAdapter(iconAdapter1);

                    }

                    if(newText.isEmpty()){
                        IconAdapter iconAdapter1 = new IconAdapter(NotesActivity.this,CategoryModel.listNotes);
                        gridView.setAdapter(iconAdapter1);
                    }



                }
                return false;
            }
        });




        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("grid view click");
                CategoryModel cnote;

                cnote = CategoryModel.listNotes.get(position);

                audioPath = CategoryModel.listNotes.get(position).getAudio();
                Intent intent = new Intent(NotesActivity.this, DescriptionActivity.class);
                intent.putExtra("audio",audioPath);
                intent.putExtra("selected",true);
                intent.putExtra("note",cnote);
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {


                AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
                builder.setTitle("DELETE");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ccid = CategoryModel.listNotes.get(position).getId();




                        if(dataBaseHelper.deletenote(ccid)){
                            Toast.makeText(NotesActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                            loadNotes();
                            IconAdapter iconAdapter = new IconAdapter(NotesActivity.this, CategoryModel.listNotes);
                            gridView.setAdapter(iconAdapter);
                        }else {
                            Toast.makeText(NotesActivity.this, "not deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_date:
                loadsortedNotes(SimpleDatabase.COLUMN_DATE);
                IconAdapter iconAdapter1 = new IconAdapter(NotesActivity.this,CategoryModel.listNotes);
                gridView.setAdapter(iconAdapter1);

                break;
            case R.id.action_title:
                loadsortedNotes(SimpleDatabase.COLUMN_TITLE);
                IconAdapter iconAdapter = new IconAdapter(NotesActivity.this,CategoryModel.listNotes);
                gridView.setAdapter(iconAdapter);

                break;
        }
        return true;
    }

    private void loadNotes(){
        Cursor cursor = dataBaseHelper.getAllNotes();
        CategoryModel.listNotes.clear();



        if(cursor.moveToFirst()){
            do{

                CategoryModel.listNotes.add(new CategoryModel(cursor.getInt(0),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getDouble(5),cursor.getDouble(6),cursor.getString(7),cursor.getString(8)));
                System.out.println(cursor.getInt(0));
                System.out.println(cursor.getString(1));
                System.out.println(cursor.getString(2));
                System.out.println(cursor.getString(3));
                System.out.println(cursor.getString(4));
                System.out.println(cursor.getString(5));
                System.out.println(cursor.getString(6));

            }while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void loadsortedNotes(String sort){
        Cursor cursor = dataBaseHelper.getAllSortedNotes(sort);
        CategoryModel.listNotes.clear();



        if(cursor.moveToFirst()){
            do{

                CategoryModel.listNotes.add(new CategoryModel(cursor.getInt(0),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getDouble(5),cursor.getDouble(6),cursor.getString(7),cursor.getString(8)));
                System.out.println(cursor.getInt(0));
                System.out.println(cursor.getString(1));
                System.out.println(cursor.getString(2));
                System.out.println(cursor.getString(3));
                System.out.println(cursor.getString(4));
                System.out.println(cursor.getString(5));
                System.out.println(cursor.getString(6));

            }while (cursor.moveToNext());
            cursor.close();
        }
    }




}
