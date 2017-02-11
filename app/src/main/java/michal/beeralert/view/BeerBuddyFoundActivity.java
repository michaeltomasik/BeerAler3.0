package michal.beeralert.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import michal.beeralert.R;
import michal.beeralert.model.UserModel;


/**
 * Created by michal on 1/24/2017.
 */

public class BeerBuddyFoundActivity extends Activity{
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;

    private Button chatButton;
    private ImageView foundBuddy;
    private String chatId;
    private UserModel userModel;
    private String USER_DB = "users";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_buddy_found);
        foundBuddy = (ImageView) findViewById(R.id.foundBuddy);

        UserModel randomUser = (UserModel) getIntent().getSerializableExtra("RandomUser");
        UserModel currentUser = (UserModel) getIntent().getSerializableExtra("CurrentUser");

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        chatId = "chatmodel-" + dateFormat.format(cal.getTime());

        randomUser.setChatId(chatId);
        randomUser.setActive(true);
        currentUser.setChatId(chatId);
        currentUser.setActive(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseDatabaseReference.child(USER_DB).child(randomUser.getId()).setValue(randomUser);
        mFirebaseDatabaseReference.child(USER_DB).child(currentUser.getId()).setValue(currentUser);

        Picasso.with(this).load(randomUser.getPhoto_profile()).into(foundBuddy);

        foundBuddy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(BeerBuddyFoundActivity.this, MessagerActivity.class);
                intent.putExtra("chatId", chatId);
                startActivity(intent);
                finish();
            }
        });
    }

}
