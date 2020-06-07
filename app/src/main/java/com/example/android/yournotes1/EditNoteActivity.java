package com.example.android.yournotes1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditNoteActivity extends AppCompatActivity {

    static final int SET_REMINDER = 1;
    private EditText inputNote,inputTitle;
    private Menu menu;
    int mDefaultColor;
    public static final String COLOR ="0";
    private String noteId ="no";
    LinearLayout linearLayout;
    private DatabaseReference fNotesDatabase;
    private FirebaseAuth fAuth;
    public static final String NOTE_EXTRA_Key="note_id";

    Note temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        inputNote=findViewById(R.id.input_note);
        inputTitle=findViewById(R.id.input_title);
        linearLayout = findViewById(R.id.edit_note_activity_layout);
        mDefaultColor = ContextCompat.getColor(EditNoteActivity.this, R.color.white);
        fAuth =FirebaseAuth.getInstance();
        fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("notes").child(fAuth.getCurrentUser().getUid());


        if(getIntent().getExtras()!=null)
        {
            //TODO:how to show the starred button starred if the note was previously starred
            mDefaultColor=getIntent().getExtras().getInt(COLOR, 0);
            linearLayout.setBackgroundColor(mDefaultColor);
            noteId =getIntent().getExtras().getString(NOTE_EXTRA_Key,"0");
            fNotesDatabase.child(noteId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String text =dataSnapshot.child("noteText").getValue().toString();
                    String text2=dataSnapshot.child("noteTitle").getValue().toString();
                    inputNote.setText(text);
                    inputTitle.setText(text2);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.edite_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id== R.id.save_note) {
            onSaveNote();
        }
        if(id==R.id.reminder_in_menu)
        {
            long eventStartInMillis = System.currentTimeMillis();
            long eventEndInMillis = System.currentTimeMillis();
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("title", "It's time to have a look at Your Notes --" + inputTitle.getText().toString());
            intent.putExtra("description", "Some description");
            intent.putExtra("beginTime", eventStartInMillis);
            intent.putExtra("endTime", eventEndInMillis);
            startActivityForResult(intent, SET_REMINDER);
        }
        if(id==R.id.share_in_menu)
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Title - " + inputTitle.getText().toString() + "\nNote - " + inputNote.getText().toString());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
        if(id==R.id.choose_color)
        {
            final AmbilWarnaDialog colorPicker =new AmbilWarnaDialog(this, mDefaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    mDefaultColor=color;
                    linearLayout.setBackgroundColor(mDefaultColor);
                }
            });
            colorPicker.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_REMINDER) {
            Log.i("okay", "successful");
            // Make sure the request was successful
            if (resultCode != RESULT_CANCELED) {
                inputNote.setText(inputNote.getText() + "\n\nREMINDER SET FOR THIS NOTE\n");
            }
        }
    }

    private void onSaveNote(){
        String text = inputTitle.getText().toString();
        String text2= inputNote.getText().toString();

        if(!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text2)){

            long date = new Date().getTime();
            if(fAuth.getCurrentUser()!=null){
            if(noteId=="no") {
                String id = fNotesDatabase.push().getKey();
                temp = new Note(id, text, text2, date , mDefaultColor);
                fNotesDatabase.child(id).setValue(temp);
                finish();
            }else{
                Map updateMap  = new HashMap();
                updateMap.put("noteTitle",inputTitle.getText().toString());
                updateMap.put("noteText",inputNote.getText().toString());
                updateMap.put("noteDate", date);
                updateMap.put("color", mDefaultColor);
                fNotesDatabase.child(noteId).updateChildren(updateMap);
                finish();
            }
            }else{
                Toast.makeText(EditNoteActivity.this, "USER IS NOT SIGNED IN " ,Toast.LENGTH_LONG).show();
            }

        }else{
            Snackbar.make(linearLayout, "Fill Empty Fields", Snackbar.LENGTH_LONG).show();
        }
    }

}
