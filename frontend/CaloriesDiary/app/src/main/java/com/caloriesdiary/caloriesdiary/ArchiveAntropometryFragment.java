package com.caloriesdiary.caloriesdiary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ArchiveAntropometryFragment extends Fragment{
    TextView rLeg, lLeg, rHand, lHand, mass, chest, waist, butt, calves, shoulders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.archive_antropometry_fragment, container, false);
            rLeg = v.findViewById(R.id.right_leg_count);
            rLeg.setText(getActivity().getIntent().getStringExtra("rLeg"));

            shoulders = v.findViewById(R.id.shoulders_count);
            shoulders.setText(getActivity().getIntent().getStringExtra("shoulders"));

            lLeg = v.findViewById(R.id.left_leg_count);
            lLeg.setText(getActivity().getIntent().getStringExtra("lLeg"));

            rHand =  v.findViewById(R.id.right_hand_count);
            rHand.setText(getActivity().getIntent().getStringExtra("rHand"));

            lHand = v.findViewById(R.id.left_hand_count);
            lHand.setText(getActivity().getIntent().getStringExtra("lHand"));

            butt =  v.findViewById(R.id.butt_count);
            butt.setText(getActivity().getIntent().getStringExtra("butt"));

            mass =  v.findViewById(R.id.mass_count);
            mass.setText(getActivity().getIntent().getStringExtra("mass"));

            chest = v.findViewById(R.id.chest_count);
            chest.setText(getActivity().getIntent().getStringExtra("chest"));

            waist = v.findViewById(R.id.waist_count);
            waist.setText(getActivity().getIntent().getStringExtra("waist"));

            calves = v.findViewById(R.id.calves_count);
            calves.setText(getActivity().getIntent().getStringExtra("calves"));

        return v;
    }
}
