package com.caloriesdiary.caloriesdiary.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;


public class DiaryAntrFragment extends Fragment {

    private EditText rLeg, lLeg, rHand, lHand, chest, waist, butt, calves, shoulders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.today_antropometry_fragment, null);

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
            JSONObject jsn;
            File f = new File(getActivity().getCacheDir(), "History_antr.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);

                rLeg.setText(jsn.getString("rLeg"));
                rHand.setText(jsn.getString("rHand"));
                lLeg.setText(jsn.getString("lLeg"));
                chest.setText(jsn.getString("chest"));
                lHand.setText(jsn.getString("lHand"));
                waist.setText(jsn.getString("waist"));
                butt.setText(jsn.getString("butt"));
                calves.setText(jsn.getString("calves"));
                shoulders.setText(jsn.getString("shoulders"));
            }

        } catch (Exception e){
            Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

        return v;
    }
}
