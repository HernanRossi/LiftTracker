package space.nandoh.lifttracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**************************************************************************************************
 * Create by Hernan Rossi
 *
 *              Activity to display the data of all of the lifts completed so far today
 *
 **************************************************************************************************/
public class ViewToday extends Activity {

    private String output = "";
    private String date= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_today);
        Intent intent = getIntent();
        if(intent.getStringExtra("day") == null) {
            HashMap<String, String> date_map = new GetDate().dateMap();
            // Get the integer representation of the day month and year for database saving
            String day_num = date_map.get("day_number");
            String day_word = date_map.get("day_word");
            String month = date_map.get("month");
            String year = date_map.get("year");
            date =  day_num + "\\" + month + "\\" + year;
        } else {
            // Get the day from the intent that was added by the history activity
        }
        TextView textViewDate = (TextView) findViewById(R.id.view_today_date);
        textViewDate.setText(date);
        TextView textView = (TextView) findViewById(R.id.view_today_output);
        try {
            SQLiteOpenHelper liftDatabaseHelper = new LiftDatabaseHelper(this);
            SQLiteDatabase db = liftDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query ("SAVE_DATA",
                    new String[] {"DATE", "LIFT_NAME","MUSCLE_GROUP","WEIGHT_TYPE", "REPS_WEIGHT", "TOTAL_WEIGHT"},
                  "DATE = ?",
                   new String[] {date},
                   // null,null,
                   null, null, null);
            if(cursor.moveToFirst()) {
                String lift_name = cursor.getString(1);
                String muscle_group = cursor.getString(2);
                String weight_type = cursor.getString(3);
                String lift_info = cursor.getString(4);
                String total_weight = cursor.getString(5);
                output = "Type: " + muscle_group + "\n" +lift_name + " done using " + weight_type +  ":\n" +
                        lift_info + "\n" + "The total weight lifted for this set was: " +
                        total_weight + " lbs\n";
                while(cursor.moveToNext()) {
                    lift_name = cursor.getString(1);
                    muscle_group = cursor.getString(2);
                    weight_type = cursor.getString(3);
                    lift_info = cursor.getString(4);
                    total_weight = cursor.getString(5);
                    output = output + " -------------------------------------\n" +
                            "Type: " + muscle_group + "\n" +lift_name + " done using " + weight_type +  ":\n" +
                            lift_info + "\n" + "The total weight lifted for this set was: " +
                            total_weight + " lbs\n";;
                }
                textView.setText(output);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            String text = "Failed access the database";
            Toast toast = Toast.makeText(this,e.toString() , Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
