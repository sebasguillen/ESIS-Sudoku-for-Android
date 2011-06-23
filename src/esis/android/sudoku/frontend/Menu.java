package esis.android.sudoku.frontend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import esis.android.sudoku.R;
import esis.android.sudoku.backend.MyApp;

/**
 * @author Sebastian Guillen
 * 
 */

public class Menu extends Activity{

    private static final String TAG = Menu.class.getSimpleName();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	setContentView(R.layout.welcome);
        InitRadioGroup();
        InitButtons();
        setListenerAndReloadDifficulty();
    }

	private void setListenerAndReloadDifficulty() {
		OnSharedPreferenceChangeListener l = new OnSharedPreferenceChangeListener() {
		    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
			reloadGuiDifficulty(sp, key);
		    }
		};
		SharedPreferences settings = getSharedPreferences(MyApp.PREFERED_DIFFICULTY, MODE_WORLD_READABLE);
		settings.registerOnSharedPreferenceChangeListener(l);
	        reloadGuiDifficulty(settings, MyApp.PREFERED_DIFFICULTY);
	}
	
	    private void reloadGuiDifficulty(SharedPreferences sp, String key) {
		RadioGroup rg = (RadioGroup) findViewById(R.id.DifficultyRadioGroup);
	        int id = sp.getInt(key, MyApp.EASY);
		rg.check(MyApp.getDifficultyID(id));
	    	Log.d(TAG, "Difficulty reloaded "+key+", now: "+id);
	    }
	    
	private void InitRadioGroup() {		
	    final RadioGroup difficultyRadioGroup = (RadioGroup) findViewById(R.id.DifficultyRadioGroup);
	    for(int i = 0; i < difficultyRadioGroup.getChildCount(); i++){
	    	RadioButton radioButton = (RadioButton)difficultyRadioGroup.getChildAt(i);
		    radioButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {				
				    SharedPreferences s = getSharedPreferences(MyApp.PREFERED_DIFFICULTY, MODE_WORLD_WRITEABLE);
				    Editor e = s.edit();
				    e.putInt(MyApp.PREFERED_DIFFICULTY, MyApp.getDifficulty(difficultyRadioGroup.getCheckedRadioButtonId()));
				    e.commit();
				}
		    });
	    }
	}

    private void InitButtons() {
		Button b = (Button) findViewById(R.id.NewGameButton);
		b.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			newGameButtonAction(v);
		    }
		});
		b = (Button) findViewById(R.id.LoadGameButton);
		b.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			loadgameButtonAction(v);
		    }
		});
		b = (Button) findViewById(R.id.HighscoresButton);
		b.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(Menu.this, Highscores.class);
			Menu.this.startActivity(intent);
		    }
		});
		b = (Button) findViewById(R.id.FeedbackButton);
		b.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			launchFeedbackDialog(v);
		    }
		});
		b = (Button) findViewById(R.id.ExitButton);
		b.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			ExitGame();
		    }
		});
    }

    private void newGameButtonAction(View v) {
		if (MyApp.saved_game_exists) {
		    deleteSavedGameWarning(v);
		} else {
		    RadioGroup rg = (RadioGroup) findViewById(R.id.DifficultyRadioGroup);
		    CharSequence difficulty = ((RadioButton) findViewById(rg.getCheckedRadioButtonId())).getText();
		    Toast.makeText(v.getContext(), "Creating " + difficulty + " Sudoku", Toast.LENGTH_LONG).show();
		    Intent intent = new Intent();
		    intent.setClass(Menu.this, Game.class);
		    Menu.this.startActivity(intent);
		}
    }

    private void loadgameButtonAction(View v) {
	Log.d(TAG, "Loading Game");
	if (MyApp.saved_game_exists) {
	    Toast.makeText(v.getContext(), "Loading Game",
		    Toast.LENGTH_LONG).show();
	    Intent intent = new Intent();
	    intent.setClass(Menu.this, Game.class);
	    Menu.this.startActivity(intent);
	} else
	    Toast.makeText(v.getContext(), R.string.no_game_to_load,
		    Toast.LENGTH_SHORT).show();
    }
	
    private void ExitGame(){
	    Log.d(TAG, "Exiting App");
	    this.finish();
	}	

	private void launchFeedbackDialog(View v) {
        	final SpannableString s = new SpannableString(getString(R.string.Feedback_Message));
        	Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES);
        	final Button b = (Button) findViewById(R.id.FeedbackButton);
        	final AlertDialog d = new AlertDialog.Builder(v.getContext())
                    .setIcon(R.drawable.icon)
                    .setTitle(b.getText())
                    .setMessage(s)
                    .setPositiveButton("Good to know", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	//Just go away.
                        }
                    }).create();
        	d.show();
        	// Make the textview clickable. Must be called after show()
        	((TextView)d.findViewById(android.R.id.message))
        	.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void deleteSavedGameWarning(View v) {
		final Button b = (Button) findViewById(R.id.NewGameButton);
		final Button lb = (Button) findViewById(R.id.LoadGameButton);
		new AlertDialog.Builder(v.getContext())
		    .setMessage("This will delete a previously saved game")
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            /* User clicked OK so start new game */  
		            MyApp.saved_game_exists = false;
		            b.performClick();
		        }
		    })
		    .setNegativeButton("Load Instead", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            /* load the game*/
		            lb.performClick();
		        }
		    }).create().show();
	}
	
}