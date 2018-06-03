package in.blogspot.iamdhariot.friendlychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {


    private static String TAG = "MainActivity";
    public static  final  String   ANONYMOUS= "anonymous";
    public static final int DEFAULT_MSG_LENGTH = 1000;
    private ListView mMsgListView;
    private  MessageAdapter messageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPikerButton;
    private EditText mMsgEditText;
    private Button mSendbutton;
    private String mUserName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserName = ANONYMOUS;
        // initialize the reference to Views


    }
}
