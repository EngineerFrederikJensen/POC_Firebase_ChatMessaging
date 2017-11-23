package makeable.poc_firebase_chatmessaging;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    // This segment is only necessary because of the decision to be able to post "anonymously" under a chosen name.
    //It would not be necessary if Firebase Authentication alone had been chosen.
    public static final String PREFS_NAME = "Name"; // The preference for name
    public static final String DEFAULT_NAME = "Anon"; // The default name in case all dialogues are cancelled
    public static final String NAME_KEY = "name"; // The key SharedPreferences saves the name under
    private FirebaseListAdapter<Chatmessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // Just making sure there's a name.
        if (prefs.getString(NAME_KEY, "").isEmpty()){
            openNameDialogue(); // If there's no name stored already, ask for one.
        }
        displayChatmessages(); // Display Chat messages from before current session


        //Find the "Sendmessage" button
        FloatingActionButton sendMessageButton = findViewById(R.id.sendMessageButton);
        //Set on click listener
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); //Because I didn't use Firebase Authentication, here's a locally saved name from prefs.

                //Take input
                EditText messageInput = findViewById(R.id.messageInput);
                // and put in in a Chatmessage for the cloud database
                FirebaseDatabase.getInstance()
                        .getReference() //root of database
                        .push() // generate key (in key-value pair for the database)
                        .setValue(new Chatmessage(messageInput.getText().toString(),prefs.getString(NAME_KEY, DEFAULT_NAME))); // make value object from text and name

                messageInput.setText(""); //Clear the messageInput
            }
        });
    }

    private void displayChatmessages(){

        ListView chatmessagesList = findViewById(R.id.chatmessageList);

        Query query = FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions<Chatmessage> options = new FirebaseListOptions.Builder<Chatmessage>()
                .setQuery(query, Chatmessage.class).setLayout(R.layout.chatmessage)
                .build();

        //        adapter = new FirebaseListAdapter<Chatmessage>(this, Chatmessage.class, R.layout.chatmessage, FirebaseDatabase.getInstance().getReference()){
        adapter = new FirebaseListAdapter<Chatmessage>(options) {
          @Override
            protected void populateView(View v, Chatmessage model, int position){
              // Get references to the views in chatmessage.xml
              TextView messageText = v.findViewById(R.id.messageText);
              TextView messageUser = v.findViewById(R.id.messageUser);
              TextView messageTime = v.findViewById(R.id.messageTime);

              //Set the text in the two first
              messageText.setText(model.getMessageText());
              messageUser.setText(model.getMessageUser());

              //Format and set time
              messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));

          }


        };
        chatmessagesList.setAdapter(adapter);
        adapter.startListening();
    }

    // Use of menu and option item below is only needed because I chose not to use firebase authentificate.
    // This is done in order to set an arbitrary name without a logon. Further discussed in attached document.



    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settingsmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        // Handle menu clicks, currently there is only "set name"
        int id = item.getItemId();

        switch (id){
            case R.id.changeName:// If "Set Name" is pressed

            openNameDialogue();
        }

        return true;
    }

    public void openNameDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.changeNameDialog_message)
                .setTitle(R.string.changeNameDialog_title); //Make an alert dialog based on the string resources.

        // Set up the input
        final EditText input = new EditText(this);

        // Input type
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Add the OK button
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!input.getText().toString().isEmpty()){
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString(NAME_KEY, input.getText().toString());
                    editor.commit(); //commit to avoid the event where the user sends a chatmessage before editor.apply() is done.
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!input.getText().toString().isEmpty()){
                    dialog.cancel();
                }
            }
        });

        AlertDialog dialog = builder.create(); //build the dialog
        dialog.show();
    }
}
