package com.caloriesdiary.caloriesdiary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class ProfileAntropometryFragment extends Fragment{
    private TextView rLeg, lLeg, calves, butt, rHand, lHand, chest, shoulders, waist;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  v = inflater.inflate(R.layout.profile_antropometry_fragment, null);

        shoulders = (TextView) v.findViewById(R.id.profile_shoulders_count);
        rHand = (TextView) v.findViewById(R.id.profile_right_hand_count);
        lHand = (TextView) v.findViewById(R.id.profile_left_hand_count);
        lLeg = (TextView) v.findViewById(R.id.profile_left_leg_count);
        rLeg = (TextView) v.findViewById(R.id.profile_right_leg_count);
        chest = (TextView) v.findViewById(R.id.profile_chest_count);
        waist = (TextView) v.findViewById(R.id.profile_waist_count);
        butt = (TextView) v.findViewById(R.id.profile_butt_count);
        calves = (TextView) v.findViewById(R.id.profile_calves_count);

        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsn = new JSONObject();
            File f = new File(getActivity().getCacheDir(), "Today_params.txt");
            if (f.exists()) {
                FileInputStream in = new FileInputStream(f);
                ObjectInputStream inObject = new ObjectInputStream(in);
                String text = inObject.readObject().toString();
                inObject.close();


                jsn = new JSONObject(text);
                jsonArray = jsn.getJSONArray("today_params");
                jsn.remove("today_params");
            }

            if (jsonArray!=null){
                shoulders.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("shoulders")+" см");
                rLeg.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("rLeg")+" см");
                lLeg.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("lLeg")+" см");
                lHand.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("lHand")+" см");
                rHand.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("rHand")+" см");
                calves.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("calves")+" см");
                chest.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("chest")+" см");
                waist.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("waist")+" см");
                butt.setText(jsonArray.getJSONObject(jsonArray.length()-1).getString("butt")+" см");
            }
        } catch (Exception e){

        }
        return v;
    }
}
