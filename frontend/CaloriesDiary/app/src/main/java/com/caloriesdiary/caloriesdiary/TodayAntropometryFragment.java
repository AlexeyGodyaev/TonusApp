package com.caloriesdiary.caloriesdiary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;

public class TodayAntropometryFragment extends Fragment{
    private EditText rLeg, lLeg, rHand, lHand, chest, waist, butt, calves, shoulders;
    private Calendar calendar = Calendar.getInstance();
    JSONArray todayParams= null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.today_antropometry_fragment, null);
        try {
            JSONObject jsn;
            File f = new File(getActivity().getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);
                todayParams = jsn.getJSONArray("today_params");
                jsn.remove("today_params");
            }
        }catch (Exception e){
            Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
        rLeg =  v.findViewById(R.id.edit_right_leg);
        lLeg =  v.findViewById(R.id.edit_left_leg);
        rHand =  v.findViewById(R.id.edit_right_hand);
        lHand =  v.findViewById(R.id.edit_left_hand);
        chest =  v.findViewById(R.id.edit_chest);
        waist =  v.findViewById(R.id.edit_waist);
        butt =  v.findViewById(R.id.edit_butt);
        calves =  v.findViewById(R.id.edit_calves);
        shoulders =  v.findViewById(R.id.edit_shoulders);
        try {

            if ( todayParams != null && todayParams.length() > 0 && todayParams.getJSONObject(todayParams.length() - 1).getString("date")
                    .equals(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "." + String.valueOf(calendar.get(Calendar.MONTH)) +
                            "." + String.valueOf(calendar.get(Calendar.YEAR)))) {

                rLeg.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("rLeg"));
                rHand.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("rHand"));
                lLeg.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("lLeg"));
                chest.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("chest"));
                lHand.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("lHand"));
                waist.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("waist"));
                butt.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("butt"));
                calves.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("calves"));
                shoulders.setText(todayParams.getJSONObject(todayParams.length() - 1).getString("shoulders"));
            }
        } catch (Exception e){
            Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    public EditText getrLeg() {
        return rLeg;
    }

    public EditText getlLeg() {
        return lLeg;
    }

    public EditText getrHand() {
        return rHand;
    }

    public EditText getlHand() {
        return lHand;
    }

    public EditText getChest() {
        return chest;
    }

    public EditText getWaist() {
        return waist;
    }

    public EditText getButt() {
        return butt;
    }

    public EditText getCalves() {
        return calves;
    }

    public EditText getShoulders() {
        return shoulders;
    }
}
