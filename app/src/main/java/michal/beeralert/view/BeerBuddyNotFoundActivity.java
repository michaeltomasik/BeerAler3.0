package michal.beeralert.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import at.markushi.ui.CircleButton;
import michal.beeralert.R;

public class BeerBuddyNotFoundActivity extends Activity {
    CircleButton sadFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_buddy_not_found);

        sadFace =(CircleButton)findViewById(R.id.sadFace);
        sadFace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(BeerBuddyNotFoundActivity.this, BeerActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
