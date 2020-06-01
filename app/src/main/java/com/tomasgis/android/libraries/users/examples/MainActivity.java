package com.tomasgis.android.libraries.users.examples;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tomasgis.android.libraries.userslibrary.AppDatabase;
import com.tomasgis.android.libraries.userslibrary.Bookmark;
import com.tomasgis.android.libraries.userslibrary.User;
import com.tomasgis.android.libraries.userslibrary.UserAccessInterface;
import com.tomasgis.android.libraries.userslibrary.UserProvider;

import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAccessInterface {

    private Button signupButton;
    private Button loginButton;
    private Button deleteButton;
    private Button bookmarkButton;
    private EditText userName;
    private  EditText userPassword;
    private Activity activity;

    private boolean isBookmark =true;
    private String data_base= "database-name";

    enum TypeBookmark {News,Article,Events,Calendar};

    private static final String TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.activity = this;

        //Obtain reference to BBDD
        final AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, data_base).build();

        //Obtain Buttons references
        signupButton = this.findViewById(R.id.singup_button);
        loginButton = this.findViewById(R.id.login_button);
        deleteButton = this.findViewById(R.id.delete_button);
        bookmarkButton = this.findViewById(R.id.bookmark_button);

        //Obtain input text references
        userName = this.findViewById(R.id.user_email_edit_text);
        userPassword = this.findViewById(R.id.password_email_edit_text);

        //To register a new user. The user can only be registered once
        //To set the current user as active use the field user.active
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                user.email = userName.getText().toString();
                user.password = userPassword.getText().toString();
                user.uid = user.email.hashCode();
                user.active=true;

                UserProvider.insertUser(db,user,(UserAccessInterface) MainActivity.this);
            }
        });

        //To login a user using email and password credentials
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = new User();
                user.email = userName.getText().toString();
                user.password = userPassword.getText().toString();

                UserProvider.isValidUser(db,user,MainActivity.this);
            }
        });

        //To delete a user. The user is deleted by email
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                User user = new User();
                user.email = userName.getText().toString();

                UserProvider.deleteUser(db,user,MainActivity.this);
            }
        });

        //To bookmark an object of type TypeBookmark
        bookmarkButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                User user = new User();
                user.email = userName.getText().toString();
                user.password = userPassword.getText().toString();
                //Identificador únic mitjançant el codi hash del títol
                user.uid = user.email.hashCode();
                user.active=true;

                Bookmark bookmark = new Bookmark();
                bookmark.type = TypeBookmark.Article.toString();
                bookmark.title = "Article name 1";
                //Identificador únic mitjançant el codi hash del títol
                bookmark.uid = bookmark.title.hashCode();
                bookmark.user_id = user.uid;

                Bookmark bookmark2 = new Bookmark();
                bookmark2.type = TypeBookmark.Article.toString();
                bookmark2.title = "Article name 2";
                //Identificador únic mitjançant el codi hash del títol
                bookmark2.uid = bookmark2.title.hashCode();
                bookmark2.user_id = user.uid;

                Bookmark bookmark3 = new Bookmark();
                bookmark3.type = TypeBookmark.Article.toString();
                bookmark3.title = "Article name 3";
                //Identificador únic mitjançant el codi hash del títol
                bookmark3.uid = bookmark3.title.hashCode();
                bookmark3.user_id = user.uid;


                if (isBookmark){
                    UserProvider.insertBookmark(db,bookmark,MainActivity.this);
                    UserProvider.insertBookmark(db,bookmark2,MainActivity.this);
                    UserProvider.insertBookmark(db,bookmark3,MainActivity.this);

                    //Get all bookmarks of an user
                    UserProvider.getAllBookmarks(db,user,MainActivity.this);
                }else{
                    UserProvider.deleteBookmark(db,bookmark,MainActivity.this);
                    UserProvider.deleteBookmark(db,bookmark2,MainActivity.this);
                    UserProvider.deleteBookmark(db,bookmark3,MainActivity.this);
                }
                isBookmark = !isBookmark;

            }
        });


        Log.e(TAG,db.toString());
    }

    /*Callback methods to get feedback of the BBDD operatons*/
    @Override
    public void callbackIsValidUser(User user, final boolean isValid) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity,String.format(getString(R.string.valid_user_toast),isValid),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void callbackInserted(User user, final boolean isInserted) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity,String.format(getString(R.string.inserted_user_toast),isInserted),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void callbackDeleted(User user, final boolean isDeleted) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,String.format(getString(R.string.deleted_user_toast),isDeleted),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void callbackInsertBookmark(Bookmark bookmark, final boolean isBookmark) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,String.format(getString(R.string.inserted_bookmark_toast),isBookmark),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void callbackDeletedBookmark(Bookmark bookmark, final boolean isBookmark) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,String.format(getString(R.string.deleted_bookmark_toast),isBookmark),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void callBackGetAllBookmarks(User user, final List<Bookmark> bookmarkLits) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                for (Bookmark bookmark:bookmarkLits){
                    String msg = String.format("Title: %1$s of type: %2$s",bookmark.title,bookmark.type);
                    Log.d(TAG,msg);
                }
            }
        });
    }

    @Override
    public void callbackSetActive(User user, boolean isActive) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this,String.format(getString(R.string.deleted_bookmark_toast),isBookmark),Toast.LENGTH_LONG).show();
            }
        });
    }


}
