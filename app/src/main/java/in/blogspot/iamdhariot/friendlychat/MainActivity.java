package in.blogspot.iamdhariot.friendlychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static String TAG = "MainActivity";
    public static  final  String   ANONYMOUS= "anonymous";
    public static final int DEFAULT_MSG_LENGTH = 1000;
    private ListView mMsgListView;
    private  MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPikerButton;
    private EditText mMsgEditText;
    private Button mSendbutton;
    private String mUserName;

    // firebase instance

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    private   ChildEventListener childEventListener;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public static final int RC_SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the fiebase stuffs
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Messages");
        mFirebaseAuth = FirebaseAuth.getInstance();


        mUserName = ANONYMOUS;
        // initialize the reference to Views

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mMsgListView = (ListView)findViewById(R.id.messageListView);
        mPhotoPikerButton = (ImageButton)findViewById(R.id.photoPickerButton);
        mMsgEditText = (EditText)findViewById(R.id.messageEditText);
        mSendbutton = (Button)findViewById(R.id.sendButton);
        // initialize the ListView and its adapter
        List<Message> messages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this,R.layout.item_message,messages);
        mMsgListView.setAdapter(mMessageAdapter);
        // initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPikerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fire to pick a image
            }
        });

        // Enable Send button when there's text to send
        mMsgEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()>0){
                    mSendbutton.setEnabled(true);

                }else{
                    mSendbutton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMsgEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH)});

        // Send button sends a message and clears the EditText
        mSendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send the msg
                Message message = new Message(mMsgEditText.getText().toString(),mUserName,null);
                // by using push to database
                mDatabaseReference.push().setValue(message);






                // clear the edit text view
                mMsgEditText.setText("");
            }
        });


       // AuthStateListener
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    // user signed in
                    Toast.makeText(MainActivity.this, "You're signed in. Welcome to the FriendlyChat App.", Toast.LENGTH_SHORT).show();
                    onSigninInitalize(user.getDisplayName());

                    
                }else{
                    // user is signed out
                    onSignoutCleanup();

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),

                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };

    }



    private void onSigninInitalize(String userName) {
        mUserName = userName;
        attachDatabaseReadListener();



    }
    private void onSignoutCleanup() {
        mUserName = ANONYMOUS;
        mMessageAdapter.clear();
        detachDatabaseReadListner();
    }

    private void attachDatabaseReadListener(){
        if(childEventListener==null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    Message messages = dataSnapshot.getValue(Message.class);
                    mMessageAdapter.add(messages);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };


            mDatabaseReference.addChildEventListener(childEventListener);
        }

    }
    private void detachDatabaseReadListner(){
        if(childEventListener!=null) {
            mDatabaseReference.removeEventListener(childEventListener);
            childEventListener=null;
        }}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater  menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sign_out_menu:
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListner();
        mMessageAdapter.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                Toast.makeText(this,"Signed in!",Toast.LENGTH_SHORT).show();


            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,"Sign in cancelled.",Toast.LENGTH_SHORT).show();
                finish();


            }

        }
    }


}
