package in.blogspot.iamdhariot.friendlychat;

import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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

    // firebase database stuffs
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the the DB
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Messages");

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





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater  menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
