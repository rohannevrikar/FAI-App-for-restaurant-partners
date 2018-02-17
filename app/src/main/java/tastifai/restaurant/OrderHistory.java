package tastifai.restaurant;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.rohannevrikar.restaurant.R;

import java.util.Calendar;
import java.util.TimeZone;

import static android.content.ContentValues.TAG;

/**
 * Created by Rohan Nevrikar on 27-01-2018.
 */

public class OrderHistory extends Fragment{
    View myView;
    private Calendar toCalendar;
    private Calendar fromCalendar;
    private EditText from;
    private EditText to;
    private int from_day, from_month, from_year;
    private int to_day, to_month, to_year;
    private DatePickerDialog.OnDateSetListener fromDataSetListener;
    private DatePickerDialog.OnDateSetListener toDataSetListener;
    private DatePickerDialog datePickerDialogFrom;
    private DatePickerDialog datePickerDialogTo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_orderhistory,container,false);
        from = (EditText)myView.findViewById(R.id.from);
        to = (EditText)myView.findViewById(R.id.to);
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromCalendar = Calendar.getInstance();
                from_day= fromCalendar.get(Calendar.DAY_OF_MONTH);
                from_month = fromCalendar.get(Calendar.MONTH);
                from_year = fromCalendar.get(Calendar.YEAR);
                from_month = from_month + 1;
                fromDataSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Log.d(TAG, "onDateSet: " + day + "/" + month + "/" + year);
                        month = month + 1;
                        from.setText(day + "/" + month + "/" + year);
                        from.setGravity(Gravity.CENTER);

                    }
                };
                datePickerDialogFrom = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog, fromDataSetListener, from_year, from_month, from_day);
                datePickerDialogFrom.show();

            }
        });
        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCalendar = Calendar.getInstance();
                to_day= toCalendar.get(Calendar.DAY_OF_MONTH);
                to_month = toCalendar.get(Calendar.MONTH);
                to_year = toCalendar.get(Calendar.YEAR);
                to_month = to_month+ 1;
                Log.d(TAG, "onClick: ");
                toDataSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        to.setText(day + "/" + month + "/" + year);
                        to.setGravity(Gravity.CENTER);

                    }
                };
                datePickerDialogTo = new DatePickerDialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog, toDataSetListener, to_year, to_month, to_day);
                datePickerDialogTo.show();


            }
        });

        return myView;
    }


}
