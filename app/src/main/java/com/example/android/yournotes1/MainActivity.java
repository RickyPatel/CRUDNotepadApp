package com.example.android.yournotes1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.android.yournotes1.EditNoteActivity.COLOR;
import static com.example.android.yournotes1.EditNoteActivity.NOTE_EXTRA_Key;

public class MainActivity extends AppCompatActivity  implements NoteEventListener{
    private FirebaseAuth fAuth;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private String layoutIcon ="linear";
    private Menu menu;
    public void setListener(NoteEventListener listener) {
        this.listener = listener;
    }

    private NoteEventListener listener;
    private DatabaseReference fNotesDatabase;
    private FirebaseRecyclerAdapter<Note, NoteViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linear_layout);
        fAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //SWIPE TO REFRESH CODE STARTS
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipenya);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // do something
                        Toast.makeText(MainActivity.this, "Refreshed!", Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        //SWIPE TO REFRESH CODE ENDS


        if(fAuth.getCurrentUser()!=null){
            fNotesDatabase = FirebaseDatabase.getInstance().getReference();
        }

        //SWIPE TO DELETE CODE STARTS
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                if (viewHolder instanceof NoteViewHolder) {
                    final int deletedIndex = viewHolder.getAdapterPosition();
                    final Note deletedNote =adapter.getItem(deletedIndex);
                    fNotesDatabase.child("notes").child(fAuth.getCurrentUser().getUid()).child(deletedNote.getNoteId()).removeValue();
                    final String dID =deletedNote.getNoteId();
                    Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_LONG);
                    //TO RESTORE DELETED ITEM
                    Snackbar snackbar = Snackbar
                            .make(linearLayout, deletedNote.getNoteTitle() + " removed from notes list", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fNotesDatabase.child("notes").child(fAuth.getCurrentUser().getUid()).child(dID).setValue(deletedNote);
                        }
                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        //  SWIPE TO DELETE CODE ENDS


        Query query =fNotesDatabase.child("notes").child(fAuth.getCurrentUser().getUid());
        FirebaseRecyclerOptions<Note> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int position, @NonNull final Note note) {
                noteViewHolder.setNote(note);
                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onNoteClick(note);

                    }
                });
                noteViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        listener.onNoteLongClick(note);
                        return false;
                    }
                });
            }

            @Override
            public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };
        setListener(this);
        recyclerView.setAdapter(adapter);
        updateUI();

    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();

        if (adapter!= null) {
            adapter.stopListening();
        }
    }

    private void updateUI() {
        if(fAuth.getCurrentUser()!=null){
            Log.i("MainActivity", "fAuth!=null");
        }else{
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
            Log.i("MainActivity", "fAuth==null");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu =menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id== R.id.add_new_note ) {
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            startActivity(intent);
        }
        if(id==R.id.sign_out){
            fAuth.signOut();
            Intent intent =new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
        }
        if(id==R.id.grid_layout)
        {
            if(layoutIcon =="linear") {
               layoutIcon ="grid";
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_view_linear_black_24dp));
                GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
                layoutManager.setOrientation(GridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
            }else{
                layoutIcon="linear";
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_grid_on_black_24dp));
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
            }
        }

        return true;
    }

    @Override
    public void onNoteClick(Note note) {
        Intent edit=new Intent(this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getNoteId());
        edit.putExtra(COLOR, note.getColor());
        startActivity(edit);
    }

    @Override
    public void onNoteLongClick(final Note note) {
            Log.i("main actvity", "long clik");
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are You Sure")
                .setMessage("Do You Want To Delete")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String id =note.getNoteId();
                        Log.i("okay", "id valu assigned"+id);
                        fNotesDatabase.child("notes").child(fAuth.getCurrentUser().getUid()).child(id).removeValue();
                        Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_LONG);

                    }
                }).show();

    }



}
