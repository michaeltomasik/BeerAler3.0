package alessandro.firebaseandroid.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import alessandro.firebaseandroid.R;
import alessandro.firebaseandroid.adapter.ClickListenerChatFirebase;
import alessandro.firebaseandroid.gps.TrackGPS;
import alessandro.firebaseandroid.model.ChatModel;
import alessandro.firebaseandroid.model.MapModel;
import alessandro.firebaseandroid.model.UserModel;
import alessandro.firebaseandroid.util.Util;

import static alessandro.firebaseandroid.R.id.imageView;
import static java.lang.System.in;

/**
 * Created by michal on 1/24/2017.
 */


public class BeerActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener {


    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    private ImageView userImg;
    private TextView textViewUsername;
    private ImageButton imageButton;
    //CLass Model
    private UserModel userModel;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private String USER_DB = "users";
    private GoogleApiClient client;

    private TrackGPS gps;

    private List<String> usersListIds = new ArrayList<>();
    private List<UserModel> usersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        userImg = (ImageView) findViewById(R.id.userImage);
        textViewUsername = (TextView) findViewById(R.id.textViewUsername);


        verificaUsuarioLogado();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        imageButton =(ImageButton)findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(BeerActivity.this, BeerBuddyFoundActivity.class);
                UserModel randomUser = getRandomUser();
                intent.putExtra("RandomUser", randomUser);
                intent.putExtra("CurrentUser", userModel);
                startActivity(intent);
                finish();
            }
        });

        gps = new TrackGPS(BeerActivity.this);
        if (gps!=null && mFirebaseUser != null){
            userModel.setLatitude(gps.getLatitude());
            userModel.setLongitude(gps.getLongitude());


            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        //child is each element in the finished list
                        Map<String, Object> message = (Map<String, Object>)child.getValue();

                        String id = (String) message.get("id");
                        double latitude;
                        double longitude;
                        try{
                            latitude = Double.longBitsToDouble((Long)message.get("latitude"));
                            longitude = Double.longBitsToDouble((Long)message.get("longitude"));
                        } catch (Exception e){
                            latitude = (double)message.get("latitude");
                            longitude = (double)message.get("longitude");
                        }

                        String name = (String) message.get("name");
                        String photo_profile = (String) message.get("photo_profile");

                        UserModel user = new UserModel( id,  name,  photo_profile,  latitude,  longitude);

                        usersList.add(user);
                        usersListIds.add(id);
                    }
                    // dodaj do bazy jak nie ma go w id
                    for(String s : usersListIds){
                        if(!s.contains(userModel.getId())){
                            mFirebaseDatabaseReference.child(USER_DB).child(userModel.getId()).setValue(userModel);
                        }
                    }

                    if(usersListIds.size() == 0){
                        mFirebaseDatabaseReference.child(USER_DB).child(userModel.getId()).setValue(userModel);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };

            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mFirebaseDatabaseReference.child(USER_DB).addListenerForSingleValueEvent(listener);


        }else{
            //PLACE IS NULL
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.sign_out:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Sign Out no login
     */
    private void signOut(){
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void authUser() {
        // dodaj do bazy jak nie ma go w id
        for(String s : usersListIds){
            if(s.contains(userModel.getId())){
                return;
            };
            mFirebaseDatabaseReference.child(USER_DB).setValue(userModel);
        }
    }

    public boolean containsId(List<UserModel> list, String name, String url) {
        for (UserModel object : list) {
            if (object.getName() == name ) {
                return true;
            }
        }
        return false;
    }

    private void verificaUsuarioLogado() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(BeerActivity.this, LoginActivity.class));
            finish();
        } else {
            userModel = new UserModel(mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString(), mFirebaseUser.getUid());
            //userImg = userModel.getPhoto_profile();
            Picasso.with(this).load(userModel.getPhoto_profile()).into(userImg);
            textViewUsername.setText(userModel.getName());
//            URL url = new URL("http://image10.bizrate-images.com/resize?sq=60&uid=2216744464");
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            userImg.setImageBitmap(bmp);
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Beer Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    public UserModel getRandomUser() {
//        Random randomGenerator = new Random();
//        int index = randomGenerator.nextInt(usersListIds.size());
        UserModel randomUser;
        List<UserModel> listWithoutCurrentId = new ArrayList<UserModel>();
       for (UserModel user : usersList){
           if(user.getId().equals(userModel.getId())){
           } else {

               listWithoutCurrentId.add(user);
           }
       }

            Collections.shuffle(listWithoutCurrentId);
            randomUser = listWithoutCurrentId.get(0);

        return randomUser;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Util.initToast(this,"Google Play Services error.");
    }

}
