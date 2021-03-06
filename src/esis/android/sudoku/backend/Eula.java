/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package esis.android.sudoku.backend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.util.Linkify;
import esis.android.sudoku.R;

/**
 * This class handles display of EULAs ("End User License Agreements") to the
 * user.
 */
public class Eula {
	public static final String PREFERENCE_EULA_ACCEPTED = "eula.accepted";
	public static final String PREFERENCES_EULA = "eula";

  public Eula() {/* Nothing to do */}

  /**
   * Displays the EULA if necessary. This method should be called from the
   * onCreate() method of your main Activity.  If the user accepts, the EULA
   * will never be displayed again.  If the user refuses, the activity will
   * finish (exit).
   *
   * @param activity The Activity to finish if the user rejects the EULA
   */
  public static void showEulaRequireAcceptance(final Activity activity) {
    final SharedPreferences preferences =
        activity.getSharedPreferences(PREFERENCES_EULA, Activity.MODE_PRIVATE);
//    if (preferences.getBoolean(PREFERENCE_EULA_ACCEPTED, false)) {
//      return;
//    }TODO needed before release

    final AlertDialog.Builder builder = initDialog(activity);
    builder.setPositiveButton(MyApp.getPositiveText(),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            accept(activity, preferences);
          }
        });
    builder.setNegativeButton(R.string.refuse,
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            refuse(activity);
          }
        });
    builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
      public void onCancel(DialogInterface dialog) {
        refuse(activity);
      }
    });
    builder.show();
  }

  /**
   * Display the EULA to the user in an informational context.  They won't be
   * given the choice of accepting or declining the EULA -- we're simply
   * displaying it for them to read.
   */
  public static void showEula(Context context) {
    AlertDialog.Builder builder = initDialog(context);
    builder.setPositiveButton(MyApp.getPositiveText(), null);
    builder.show();
  }
  
  private static AlertDialog.Builder initDialog(Context c) {
  	final SpannableString ss = new SpannableString(FileSystemTool.readFile(c, R.raw.eula));
  	Linkify.addLinks(ss, Linkify.ALL);
    AlertDialog.Builder builder = new AlertDialog.Builder(c);
    builder.setCancelable(true);
    builder.setTitle(R.string.eula_title);
    builder.setMessage(ss);
    return builder;
  }

  private static void accept(Activity activity, SharedPreferences preferences) {
    preferences.edit().putBoolean(PREFERENCE_EULA_ACCEPTED, true).commit();
  }

  private static void refuse(Activity activity) {
    activity.finish();
  }
}
