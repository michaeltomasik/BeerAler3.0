package alessandro.firebaseandroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import alessandro.firebaseandroid.MainActivity;
import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.model.UserModel;

import static alessandro.firebaseandroid.R.id.userImg;

/**
 * Created by michal on 1/24/2017.
 */

public class BeerBuddyFoundActivity extends Activity{
    private Button chatButton;
    private ImageView foundBuddy;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_buddy_found);
        foundBuddy = (ImageView) findViewById(R.id.foundBuddy);

        UserModel userModel = (UserModel) getIntent().getSerializableExtra("RandomUser");
        Picasso.with(this).load(userModel.getPhoto_profile()).into(foundBuddy);
        foundBuddy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(BeerBuddyFoundActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
