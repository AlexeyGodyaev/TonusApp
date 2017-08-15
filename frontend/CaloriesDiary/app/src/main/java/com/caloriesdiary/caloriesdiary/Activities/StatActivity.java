package com.caloriesdiary.caloriesdiary.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.caloriesdiary.caloriesdiary.HTTP.GetDays;
import com.caloriesdiary.caloriesdiary.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class StatActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.test_container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.test_tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    public static class PlaceholderFragment extends Fragment {
        SharedPreferences sharedPref;

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        private GraphView massGraph, eatedCaloriesGraph, bernCaloriesGraph, rLegGraph, lLegGraph, calvesGraph, buttGraph, rHandGraph,
                lHandGraph, chestGraph, shouldersGraph, waistGraph;
        private TextView setGraphs, massGraphTxt, eatedCaloriesGraphTxt, bernCaloriesGraphTxt, rLegGraphTxt, lLegGraphTxt,
                calvesGraphTxt, buttGraphTxt, rHandGraphTxt, lHandGraphTxt, chestGraphTxt, shouldersGraphTxt, waistGraphTxt, viewParams;
        private LinearLayout mainLayout;
        private JSONArray graphArr=null;
        private JSONObject graphDraw=null;
        private String s="";
        private String graphHor [];
        String args[] = new String[2];




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView;

            sharedPref = getActivity().getSharedPreferences("GlobalPref", MODE_PRIVATE);

            args[0] = String.valueOf(sharedPref.getInt("PROFILE_ID", 0));
            args[1] = FirebaseInstanceId.getInstance().getToken();

            if (getArguments().getInt(ARG_SECTION_NUMBER)==1){
                rootView = inflater.inflate(R.layout.graph_stat_layout, container, false);



                DataPoint massData [], eatedData[], bernData [], rLegData [], lLegData [], rHandData [],
                        lHandData [], waistData [], chestData [], buttData [], shouldersData [], calvesData [];

                viewParams =  rootView.findViewById(R.id.graph_params_value);
                mainLayout =  rootView.findViewById(R.id.graph_layout);
                final LinearLayout lp = new LinearLayout(getActivity().getApplicationContext());
                lp.setOrientation(LinearLayout.VERTICAL);
                int a;
                setGraphs =  rootView.findViewById(R.id.graph_params);

                try {
                    File f = new File(getActivity().getCacheDir(), "Graph_params.txt");
                    if (f.exists()) {
                        FileInputStream in = new FileInputStream(f);
                        ObjectInputStream inObject = new ObjectInputStream(in);
                        String text = inObject.readObject().toString();
                        inObject.close();
                        graphDraw = new JSONObject(text);
                    }
                }  catch (Exception e){
                    Toast.makeText(getActivity().getApplicationContext(), e.toString() , Toast.LENGTH_LONG).show();
                }

                setGraphs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final CheckBox massCheck, eatedCheck, bernCheck, buttCheck, shouldersCheck,  calvesCheck, chestCheck,
                                waistCheck, handsCheck, legsCheck;
                        try{
                        massCheck = new CheckBox(rootView.getContext());
                        massCheck.setText("Масса");
                        eatedCheck = new CheckBox(rootView.getContext());
                        eatedCheck.setText("Потребленные калории");
                        bernCheck = new CheckBox(rootView.getContext());
                        bernCheck.setText("Затраченные калории");
                        buttCheck = new CheckBox(rootView.getContext());
                        buttCheck.setText("Ягодицы");
                        shouldersCheck = new CheckBox(rootView.getContext());
                        shouldersCheck.setText("Плечи");
                        calvesCheck = new CheckBox(rootView.getContext());
                        calvesCheck.setText("Икры");
                        chestCheck = new CheckBox(rootView.getContext());
                        chestCheck.setText("Грудь");
                        waistCheck = new CheckBox(rootView.getContext());
                        waistCheck.setText("Талия");
                        handsCheck = new CheckBox(rootView.getContext());
                        handsCheck.setText("Руки");
                        legsCheck = new CheckBox(rootView.getContext());
                        legsCheck.setText("Бедра");

                        lp.addView(massCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(eatedCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(bernCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(shouldersCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(handsCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(chestCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(waistCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(buttCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(legsCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        lp.addView(calvesCheck, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                            massCheck.setChecked(true);
                            eatedCheck.setChecked(true);
                            bernCheck.setChecked(true);
                            shouldersCheck.setChecked(true);
                            handsCheck.setChecked(true);
                            chestCheck.setChecked(true);
                            waistCheck.setChecked(true);
                            buttCheck.setChecked(true);
                            legsCheck.setChecked(true);
                            calvesCheck.setChecked(true);

                            File f = new File(getActivity().getCacheDir(), "Graph_params.txt");
                            if (f.exists()) {
                                FileInputStream in = new FileInputStream(f);
                                ObjectInputStream inObject = new ObjectInputStream(in);
                                String text = inObject.readObject().toString();
                                inObject.close();
                                JSONObject jsn = new JSONObject(text);

                                if (jsn.getString("mass").equals("false"))
                                    massCheck.setChecked(false);
                                if (jsn.getString("eated").equals("false"))
                                    eatedCheck.setChecked(false);
                                if (jsn.getString("bern").equals("false"))
                                    bernCheck.setChecked(false);
                                if (jsn.getString("shoulders").equals("false"))
                                    shouldersCheck.setChecked(false);
                                if (jsn.getString("hands").equals("false"))
                                    handsCheck.setChecked(false);
                                if (jsn.getString("chest").equals("false"))
                                    chestCheck.setChecked(false);
                                if (jsn.getString("waist").equals("false"))
                                    waistCheck.setChecked(false);
                                if (jsn.getString("butt").equals("false"))
                                    buttCheck.setChecked(false);
                                if (jsn.getString("legs").equals("false"))
                                    legsCheck.setChecked(false);
                                if (jsn.getString("calves").equals("false"))
                                    calvesCheck.setChecked(false);

                                f.createNewFile();
                            }

                        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext());
                        builder.setTitle("Выберите отображаемые графики")
                                .setCancelable(false)
                                .setNegativeButton("Отмена",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (lp.getChildCount() > 0)
                                                    lp.removeAllViews();
                                                ((ViewManager) lp.getParent()).removeView(lp);
                                                dialog.cancel();
                                            }
                                        })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        try {
                                            JSONObject jsn;
                                            File f = new File(getActivity().getCacheDir(), "Graph_params.txt");

                                            jsn = new JSONObject();

                                            jsn.put("mass", massCheck.isChecked());
                                            jsn.put("shoulders", shouldersCheck.isChecked());
                                            jsn.put("calves", calvesCheck.isChecked());
                                            jsn.put("chest", chestCheck.isChecked());
                                            jsn.put("waist", waistCheck.isChecked());
                                            jsn.put("butt", buttCheck.isChecked());
                                            jsn.put("bern", bernCheck.isChecked());
                                            jsn.put("eated", eatedCheck.isChecked());
                                            jsn.put("legs", legsCheck.isChecked());
                                            jsn.put("hands", handsCheck.isChecked());


                                            FileOutputStream out = new FileOutputStream(f);
                                            ObjectOutputStream outObject = new ObjectOutputStream(out);
                                            outObject.writeObject(jsn.toString());
                                            outObject.flush();
                                            out.getFD().sync();
                                            outObject.close();

                                            getActivity().recreate();
                                        }
                                        catch (Exception iEx){
                                            Toast.makeText(getActivity().getApplicationContext(), iEx.toString() , Toast.LENGTH_LONG).show();
                                        }

                                        if (lp.getChildCount() > 0)
                                            lp.removeAllViews();
                                        ((ViewManager) lp.getParent()).removeView(lp);


                                    }
                                });

                        builder.setView(lp);
                        AlertDialog alert = builder.create();
                        alert.show();

                        }  catch (Exception e){
                            Toast.makeText(getActivity().getApplicationContext(), e.toString() , Toast.LENGTH_LONG).show();
                        }
                    }
                });

                try {
                    JSONObject jsn;
                   // File f = new File(getActivity().getCacheDir(), "Today_params.txt");
//                    if (f.exists()) {
//                        FileInputStream in = new FileInputStream(f);
//                        ObjectInputStream inObject = new ObjectInputStream(in);
//                        String text = inObject.readObject().toString();
//                        inObject.close();
                        GetDays get = new GetDays();

                    get.execute(args);

                        jsn = new JSONObject(get.get());
                        graphArr = jsn.getJSONArray("days");
                        jsn.remove("days");

                    if (graphArr.length()>7) {
                        JSONArray buf = new JSONArray();
                        for (int i = graphArr.length() - 7; i < graphArr.length(); i++) {
                            buf.put(graphArr.getJSONObject(i));
                        }
                        graphArr = new JSONArray(buf.toString());
                    }
                  //  }


                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                massGraphTxt =  rootView.findViewById(R.id.mass_text);
                eatedCaloriesGraphTxt =  rootView.findViewById(R.id.eated_text);
                bernCaloriesGraphTxt =  rootView.findViewById(R.id.bern_text);
                shouldersGraphTxt =  rootView.findViewById(R.id.shoulders_text);
                rHandGraphTxt =  rootView.findViewById(R.id.rh_text);
                lHandGraphTxt =  rootView.findViewById(R.id.lh_text);
                lLegGraphTxt =  rootView.findViewById(R.id.ll_text);
                rLegGraphTxt =  rootView.findViewById(R.id.rl_text);
                calvesGraphTxt =  rootView.findViewById(R.id.calves_text);
                buttGraphTxt =  rootView.findViewById(R.id.butt_text);
                chestGraphTxt =  rootView.findViewById(R.id.chest_text);
                waistGraphTxt =  rootView.findViewById(R.id.waist_text);

                massGraph = rootView.findViewById(R.id.mass_graph_container);
                try {
                    if(graphDraw!=null&&graphDraw.getString("mass").equals("false")) {
                        mainLayout.removeView(massGraph);
                        mainLayout.removeView(massGraphTxt);
                    } else {
                        massGraph.setAlpha(0.5f);
                        massGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        massGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        massGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        massGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                } catch (Exception e){

                }

                eatedCaloriesGraph = rootView.findViewById(R.id.eated_calories_graph_container);
                try {
                    if (graphDraw != null && graphDraw.getString("eated").equals("false")) {
                        mainLayout.removeView(eatedCaloriesGraph);
                        mainLayout.removeView(eatedCaloriesGraphTxt);
                    } else {
                        eatedCaloriesGraph.setAlpha(0.5f);
                        eatedCaloriesGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        eatedCaloriesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        eatedCaloriesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        eatedCaloriesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                }catch (Exception e){

                }

                bernCaloriesGraph = rootView.findViewById(R.id.bern_calories_graph_container);
                try {
                    if (graphDraw != null && graphDraw.getString("bern").equals("false")) {
                        mainLayout.removeView(bernCaloriesGraph);
                        mainLayout.removeView(bernCaloriesGraphTxt);
                    } else {
                        bernCaloriesGraph.setAlpha(0.5f);
                        bernCaloriesGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        bernCaloriesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        bernCaloriesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        bernCaloriesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                }catch (Exception e){

                }

                shouldersGraph = rootView.findViewById(R.id.shoulders_graph_container);
                try {
                    if(graphDraw!=null&&graphDraw.getString("shoulders").equals("false")) {
                        mainLayout.removeView(shouldersGraph);
                        mainLayout.removeView(shouldersGraphTxt);
                    } else {
                        shouldersGraph.setAlpha(0.5f);
                        shouldersGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        shouldersGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        shouldersGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        shouldersGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                } catch (Exception e){

                }

                rHandGraph = rootView.findViewById(R.id.right_hand_graph_container);
                lHandGraph = rootView.findViewById(R.id.left_hand_graph_container);
                try {
                    if(graphDraw!=null&&graphDraw.getString("hands").equals("false")){
                        mainLayout.removeView(rHandGraph);
                        mainLayout.removeView(lHandGraph);
                        mainLayout.removeView(rHandGraphTxt);
                        mainLayout.removeView(lHandGraphTxt);
                    } else {
                        rHandGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        rHandGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        rHandGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        rHandGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                        rHandGraph.setAlpha(0.5f);

                        lHandGraph.setAlpha(0.5f);
                        lHandGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        lHandGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        lHandGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        lHandGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                } catch (Exception e) {

                }

                lLegGraph =  rootView.findViewById(R.id.left_leg_graph_container);
                rLegGraph =  rootView.findViewById(R.id.right_leg_graph_container);
                try {
                    if(graphDraw!=null&&graphDraw.getString("legs").equals("false")){
                        mainLayout.removeView(lLegGraph);
                        mainLayout.removeView(rLegGraph);
                        mainLayout.removeView(lLegGraphTxt);
                        mainLayout.removeView(rLegGraphTxt);
                    } else {
                        lLegGraph.setAlpha(0.5f);
                        lLegGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        lLegGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        lLegGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        lLegGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

                        rLegGraph.setAlpha(0.5f);
                        rLegGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        rLegGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        rLegGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        rLegGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                } catch (Exception e) {

                }

                calvesGraph = rootView.findViewById(R.id.calves_graph_container);
                try {if(graphDraw!=null&&graphDraw.getString("calves").equals("false")) {
                    mainLayout.removeView(calvesGraph);
                    mainLayout.removeView(calvesGraphTxt);
                } else {
                    calvesGraph.setAlpha(0.5f);
                    calvesGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                    calvesGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                    calvesGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                    calvesGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                }
                } catch (Exception e){

                }

                buttGraph =  rootView.findViewById(R.id.butt_graph_container);
                try {
                    if(graphDraw!=null&&graphDraw.getString("butt").equals("false")) {
                        mainLayout.removeView(buttGraph);
                        mainLayout.removeView(buttGraphTxt);
                    } else {
                        buttGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        buttGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        buttGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        buttGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                        buttGraph.setAlpha(0.5f);
                    }
                } catch (Exception e){

                }

                chestGraph = rootView.findViewById(R.id.chest_graph_container);
                try {
                    if(graphDraw!=null&&graphDraw.getString("chest").equals("false")) {
                        mainLayout.removeView(chestGraph);
                        mainLayout.removeView(chestGraphTxt);
                    } else {
                        chestGraph.setAlpha(0.5f);
                        chestGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        chestGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        chestGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        chestGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                } catch (Exception e){

                }

                waistGraph =  rootView.findViewById(R.id.waist_graph_container);
                try {
                    if(graphDraw!=null&&graphDraw.getString("waist").equals("false")) {
                        mainLayout.removeView(waistGraph);
                        mainLayout.removeView(waistGraphTxt);
                    } else {
                        waistGraph.setAlpha(0.5f);
                        waistGraph.setBackgroundColor(Color.parseColor("#1ED4B5"));
                        waistGraph.getGridLabelRenderer().setGridColor(Color.WHITE);
                        waistGraph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
                        waistGraph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
                    }
                } catch (Exception e){

                }


                try {
                    if (graphArr!=null&&graphArr.length()>2) {

                        massData = new DataPoint[graphArr.length()];
                        eatedData = new DataPoint[graphArr.length()];
                        rLegData = new DataPoint[graphArr.length()];
                        lLegData = new DataPoint[graphArr.length()];
                        rHandData = new DataPoint[graphArr.length()];
                        lHandData = new DataPoint[graphArr.length()];
                        calvesData = new DataPoint[graphArr.length()];
                        buttData = new DataPoint[graphArr.length()];
                        waistData = new DataPoint[graphArr.length()];
                        shouldersData = new DataPoint[graphArr.length()];
                        chestData = new DataPoint[graphArr.length()];
                        bernData = new DataPoint[graphArr.length()];
                        graphHor = new String[graphArr.length()];

                        for (int i=0; i < graphArr.length(); i++) {
                            if (graphArr.getJSONObject(i).getString("mass").equals(""))
                                if(i==0) massData[i] = new DataPoint(i, 0); else
                                    massData[i] = new DataPoint(i, massData[i-1].getY());
                            else
                                massData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("mass")));

                            if (graphArr.getJSONObject(i).getString("food_sum").equals(""))
                                if(i==0) eatedData[i] = new DataPoint(i, 0); else
                                    eatedData[i] = new DataPoint(i, eatedData[i-1].getY());
                            else
                                eatedData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("food_sum")));

                            if (graphArr.getJSONObject(i).getString("active_sum").equals(""))
                                if(i==0) bernData[i] = new DataPoint(i, 0); else
                                    bernData[i] = new DataPoint(i, bernData[i-1].getY());
                            else
                                bernData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("active_sum")));

                            if (graphArr.getJSONObject(i).getString("rLeg").equals(""))
                                if(i==0) rLegData[i] = new DataPoint(i, 0); else
                                    rLegData[i] = new DataPoint(i, rLegData[i-1].getY());
                            else
                                rLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rLeg")));

                            if (graphArr.getJSONObject(i).getString("lLeg").equals(""))
                                if(i==0) lLegData[i] = new DataPoint(i, 0); else
                                    lLegData[i] = new DataPoint(i, lLegData[i-1].getY());
                            else
                                lLegData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lLeg")));

                            if (graphArr.getJSONObject(i).getString("rHand").equals(""))
                                if(i==0) rHandData[i] = new DataPoint(i, 0); else
                                    rHandData[i] = new DataPoint(i, rHandData[i-1].getY());
                            else
                                rHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("rHand")));

                            if (graphArr.getJSONObject(i).getString("lHand").equals(""))
                                if(i==0) lHandData[i] = new DataPoint(i, 0); else
                                    lHandData[i] = new DataPoint(i, lHandData[i-1].getY());
                            else
                                lHandData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("lHand")));

                            if (graphArr.getJSONObject(i).getString("waist").equals(""))
                                if(i==0) waistData[i] = new DataPoint(i, 0); else
                                    waistData[i] = new DataPoint(i, waistData[i-1].getY());
                            else
                                waistData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("waist")));

                            if (graphArr.getJSONObject(i).getString("chest").equals(""))
                                if(i==0) chestData[i] = new DataPoint(i, 0); else
                                    chestData[i] = new DataPoint(i, chestData[i-1].getY());
                            else
                                chestData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("chest")));

                            if (graphArr.getJSONObject(i).getString("shoulders").equals(""))
                                if(i==0) shouldersData[i] = new DataPoint(i, 0); else
                                    shouldersData[i] = new DataPoint(i, shouldersData[i-1].getY());
                            else
                                shouldersData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("shoulders")));

                            if (graphArr.getJSONObject(i).getString("butt").equals(""))
                                if(i==0) buttData[i] = new DataPoint(i, 0); else
                                    buttData[i] = new DataPoint(i,  buttData[i-1].getY());
                            else
                                buttData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("butt")));

                            if (graphArr.getJSONObject(i).getString("calves").equals(""))
                                if(i==0) calvesData[i] = new DataPoint(i, 0); else
                                    calvesData[i] = new DataPoint(i, calvesData[i-1].getY());
                            else
                                calvesData[i] = new DataPoint(i, Double.parseDouble(graphArr.getJSONObject(i).getString("calves")));
                            graphHor[i] = graphArr.getJSONObject(i).getString("date")
                                    .substring(8,graphArr.getJSONObject(i).getString("date").length());
                        }

                        if(graphDraw!=null&&graphDraw.getString("mass").equals("true")) {
                            LineGraphSeries<DataPoint> massSeries = new LineGraphSeries<>(massData);
                            massSeries.setThickness(2);
                            StaticLabelsFormatter massLabelsFormatter = new StaticLabelsFormatter(massGraph);
                            massLabelsFormatter.setHorizontalLabels(graphHor);
                            massGraph.getGridLabelRenderer().setLabelFormatter(massLabelsFormatter);
                            massSeries.setColor(Color.WHITE);
                            massGraph.addSeries(massSeries);
                            s+="Масса, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("eated").equals("true")){
                            LineGraphSeries<DataPoint> eatedSeries = new LineGraphSeries<>(eatedData);
                            eatedSeries.setThickness(2);
                            StaticLabelsFormatter eatedLabelsFormatter = new StaticLabelsFormatter(eatedCaloriesGraph);
                            eatedLabelsFormatter.setHorizontalLabels(graphHor);
                            eatedCaloriesGraph.getGridLabelRenderer().setLabelFormatter(eatedLabelsFormatter);
                            eatedSeries.setColor(Color.WHITE);
                            eatedCaloriesGraph.addSeries(eatedSeries);
                            s+="Потребляемые калории, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("bern").equals("true")) {
                            LineGraphSeries<DataPoint> bernSeries = new LineGraphSeries<>(bernData);
                            StaticLabelsFormatter bernLabelsFormatter = new StaticLabelsFormatter(bernCaloriesGraph);
                            bernSeries.setThickness(2);
                            bernLabelsFormatter.setHorizontalLabels(graphHor);
                            bernCaloriesGraph.getGridLabelRenderer().setLabelFormatter(bernLabelsFormatter);
                            bernSeries.setColor(Color.WHITE);
                            bernCaloriesGraph.addSeries(bernSeries);
                            s+="Затраченные калории, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("shoulders").equals("true")){
                            LineGraphSeries<DataPoint> shouldersSeries = new LineGraphSeries<>(shouldersData);
                            shouldersSeries.setThickness(2);
                            StaticLabelsFormatter shouldersLabelsFormatter = new StaticLabelsFormatter(shouldersGraph);
                            shouldersLabelsFormatter.setHorizontalLabels(graphHor);
                            shouldersGraph.getGridLabelRenderer().setLabelFormatter(shouldersLabelsFormatter);
                            shouldersSeries.setColor(Color.WHITE);
                            shouldersGraph.addSeries(shouldersSeries);
                            s+="Плечи, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("hands").equals("true")){
                            LineGraphSeries<DataPoint> rHandSeries = new LineGraphSeries<>(rHandData);
                            rHandSeries.setThickness(2);
                            StaticLabelsFormatter rHandLabelsFormatter = new StaticLabelsFormatter(rHandGraph);
                            rHandLabelsFormatter.setHorizontalLabels(graphHor);
                            rHandGraph.getGridLabelRenderer().setLabelFormatter(rHandLabelsFormatter);
                            rHandSeries.setColor(Color.WHITE);
                            rHandGraph.addSeries(rHandSeries);

                            LineGraphSeries<DataPoint> lHandSeries = new LineGraphSeries<>(lHandData);
                            lHandSeries.setThickness(2);
                            StaticLabelsFormatter lHandLabelsFormatter = new StaticLabelsFormatter(lHandGraph);
                            lHandLabelsFormatter.setHorizontalLabels(graphHor);
                            lHandGraph.getGridLabelRenderer().setLabelFormatter(lHandLabelsFormatter);
                            lHandSeries.setColor(Color.WHITE);
                            lHandGraph.addSeries(lHandSeries);
                            s+="Руки, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("chest").equals("true")) {
                            LineGraphSeries<DataPoint> chestSeries = new LineGraphSeries<>(chestData);
                            chestSeries.setThickness(2);
                            StaticLabelsFormatter chestLabelsFormatter = new StaticLabelsFormatter(chestGraph);
                            chestLabelsFormatter.setHorizontalLabels(graphHor);
                            chestGraph.getGridLabelRenderer().setLabelFormatter(chestLabelsFormatter);
                            chestSeries.setColor(Color.WHITE);
                            chestGraph.addSeries(chestSeries);
                            s+="Грудь, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("waist").equals("true")) {
                            LineGraphSeries<DataPoint> waistSeries = new LineGraphSeries<>(waistData);
                            StaticLabelsFormatter waistLabelsFormatter = new StaticLabelsFormatter(waistGraph);
                            waistSeries.setThickness(2);
                            waistLabelsFormatter.setHorizontalLabels(graphHor);
                            waistGraph.getGridLabelRenderer().setLabelFormatter(waistLabelsFormatter);
                            waistSeries.setColor(Color.WHITE);
                            waistGraph.addSeries(waistSeries);
                            s+="Талия, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("butt").equals("true")) {
                            LineGraphSeries<DataPoint> buttSeries = new LineGraphSeries<>(buttData);
                            buttSeries.setThickness(2);
                            StaticLabelsFormatter buttLabelsFormatter = new StaticLabelsFormatter(buttGraph);
                            buttLabelsFormatter.setHorizontalLabels(graphHor);
                            buttGraph.getGridLabelRenderer().setLabelFormatter(buttLabelsFormatter);
                            buttSeries.setColor(Color.WHITE);
                            buttGraph.addSeries(buttSeries);
                            s+="Ягодицы, ";
                        }

                        if(graphDraw!=null&&graphDraw.getString("legs").equals("true")){
                            LineGraphSeries<DataPoint> rLegSeries = new LineGraphSeries<>(rLegData);
                            rLegSeries.setThickness(2);
                            StaticLabelsFormatter rLegLabelsFormatter = new StaticLabelsFormatter(rLegGraph);
                            rLegLabelsFormatter.setHorizontalLabels(graphHor);
                            rLegGraph.getGridLabelRenderer().setLabelFormatter(rLegLabelsFormatter);
                            rLegSeries.setColor(Color.WHITE);
                            rLegGraph.addSeries(rLegSeries);

                            LineGraphSeries<DataPoint> lLegSeries = new LineGraphSeries<>(lLegData);
                            lLegSeries.setThickness(2);
                            StaticLabelsFormatter lLegLabelsFormatter = new StaticLabelsFormatter(lLegGraph);
                            lLegLabelsFormatter.setHorizontalLabels(graphHor);
                            lLegGraph.getGridLabelRenderer().setLabelFormatter(lLegLabelsFormatter);
                            lLegSeries.setColor(Color.WHITE);
                            lLegGraph.addSeries(lLegSeries);
                            s+="Бедра, ";
                        }


                        if(graphDraw!=null&&graphDraw.getString("calves").equals("true")) {
                            LineGraphSeries<DataPoint> calvesSeries = new LineGraphSeries<>(calvesData);
                            calvesSeries.setThickness(2);
                            StaticLabelsFormatter calvesLabelsFormatter = new StaticLabelsFormatter(calvesGraph);
                            calvesLabelsFormatter.setHorizontalLabels(graphHor);
                            calvesGraph.getGridLabelRenderer().setLabelFormatter(calvesLabelsFormatter);
                            calvesSeries.setColor(Color.WHITE);
                            calvesGraph.addSeries(calvesSeries);
                            s+= "Икры, ";
                        }

                        s = s.substring(0, s.length()-2)+".";
                        viewParams.setText(s);
                    }
                    else Toast.makeText(getActivity().getApplicationContext(), "Статистика доступна после трех заполненных дней", Toast.LENGTH_LONG).show();


                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

            } else {
                rootView = inflater.inflate(R.layout.table_stat_layout, container, false);
                TableLayout table = rootView.findViewById(R.id.table_layout);

                JSONArray tableDataArray;
                table.setStretchAllColumns(true);
//                table.setShrinkAllColumns(true);
                TableRow rowDayLabels = new TableRow(getActivity().getApplicationContext());
                TableRow rowMass = new TableRow(getActivity().getApplicationContext());
                rowMass.setBackgroundColor(Color.GRAY);
                TableRow rowBurnCalories = new TableRow(getActivity().getApplicationContext());
                rowBurnCalories.setBackgroundColor(Color.GRAY);
                TableRow rowEatedCalories = new TableRow(getActivity().getApplicationContext());
                TableRow rowShoulders = new TableRow(getActivity().getApplicationContext());
                TableRow rowLHand = new TableRow(getActivity().getApplicationContext());
                TableRow rowRHand = new TableRow(getActivity().getApplicationContext());
                rowRHand.setBackgroundColor(Color.GRAY);
                TableRow rowCalves = new TableRow(getActivity().getApplicationContext());
                TableRow rowLLeg = new TableRow(getActivity().getApplicationContext());
                TableRow rowRLeg = new TableRow(getActivity().getApplicationContext());
                rowLLeg.setBackgroundColor(Color.LTGRAY);
                TableRow rowChest = new TableRow(getActivity().getApplicationContext());
                TableRow rowWaist = new TableRow(getActivity().getApplicationContext());
                rowChest.setBackgroundColor(Color.LTGRAY);
                TableRow rowButt = new TableRow(getActivity().getApplicationContext());
                rowButt.setBackgroundColor(Color.GRAY);

                rowDayLabels.setMinimumHeight(40);
                rowMass.setMinimumHeight(40);
                rowEatedCalories.setMinimumHeight(40);
                rowBurnCalories.setMinimumHeight(40);
                rowShoulders.setMinimumHeight(40);
                rowRHand.setMinimumHeight(40);
                rowLHand.setMinimumHeight(40);
                rowChest.setMinimumHeight(40);
                rowWaist.setMinimumHeight(40);
                rowButt.setMinimumHeight(40);
                rowRLeg.setMinimumHeight(40);
                rowLLeg.setMinimumHeight(40);
                rowCalves.setMinimumHeight(40);

                try {
                    JSONObject jsn;
                    // File f = new File(getActivity().getCacheDir(), "Today_params.txt");
//                    if (f.exists()) {
//                        FileInputStream in = new FileInputStream(f);
//                        ObjectInputStream inObject = new ObjectInputStream(in);
//                        String text = inObject.readObject().toString();
//                        inObject.close();
                    GetDays getTable = new GetDays();

                    getTable.execute(args);

                    jsn = new JSONObject(getTable.get());
                    tableDataArray = jsn.getJSONArray("days");
                    jsn.remove("days");

                for (int i = 0; i<tableDataArray.length()+1; i++) {
                    if(i==0) {
                        TextView dayLabel = new TextView(getActivity().getApplicationContext());
                        dayLabel.setText("");
                        dayLabel.setTextColor(Color.GRAY);

                        TextView dayMass = new TextView(getActivity().getApplicationContext());
                        dayMass.setText("Масса");
                        dayMass.setTextColor(Color.BLACK);
                        dayMass.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayEated = new TextView(getActivity().getApplicationContext());
                        dayEated.setText("Потр. Калории");
                        dayEated.setTextColor(Color.BLACK);
                        dayEated.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayBurn = new TextView(getActivity().getApplicationContext());
                        dayBurn.setText("Затр. Калории");
                        dayBurn.setTextColor(Color.BLACK);
                        dayBurn.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayShoulders = new TextView(getActivity().getApplicationContext());
                        dayShoulders.setText("Плечи");
                        dayShoulders.setTextColor(Color.BLACK);
                        dayShoulders.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayRHand = new TextView(getActivity().getApplicationContext());
                        dayRHand.setText("Пр. рука");
                        dayRHand.setTextColor(Color.BLACK);
                        dayRHand.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayLHand = new TextView(getActivity().getApplicationContext());
                        dayLHand.setText("Лев. Рука");
                        dayLHand.setTextColor(Color.BLACK);
                        dayLHand.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayChest = new TextView(getActivity().getApplicationContext());
                        dayChest.setText("Грудь");
                        dayChest.setTextColor(Color.BLACK);
                        dayChest.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayWaist = new TextView(getActivity().getApplicationContext());
                        dayWaist.setText("Талия");
                        dayWaist.setTextColor(Color.BLACK);
                        dayWaist.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayButt = new TextView(getActivity().getApplicationContext());
                        dayButt.setText("Ягодицы");
                        dayButt.setTextColor(Color.BLACK);
                        dayButt.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayRLeg = new TextView(getActivity().getApplicationContext());
                        dayRLeg.setText("Пр. бедро");
                        dayRLeg.setTextColor(Color.BLACK);
                        dayRLeg.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayLLeg = new TextView(getActivity().getApplicationContext());
                        dayLLeg.setText("Лев. бедро");
                        dayLLeg.setTextColor(Color.BLACK);
                        dayLLeg.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayCalves = new TextView(getActivity().getApplicationContext());
                        dayCalves.setText("Икры");
                        dayCalves.setTextColor(Color.BLACK);
                        dayCalves.setGravity(Gravity.CENTER_HORIZONTAL);

                        rowDayLabels.addView(dayLabel);
                        rowMass.addView(dayMass);
                        rowEatedCalories.addView(dayEated);
                        rowBurnCalories.addView(dayBurn);
                        rowShoulders.addView(dayShoulders);
                        rowRHand.addView(dayRHand);
                        rowLHand.addView(dayLHand);
                        rowChest.addView(dayChest);
                        rowWaist.addView(dayWaist);
                        rowButt.addView(dayButt);
                        rowRLeg.addView(dayRLeg);
                        rowLLeg.addView(dayLLeg);
                        rowCalves.addView(dayCalves);
                    } else {
                        TextView dayLabel = new TextView(getActivity().getApplicationContext());
                        dayLabel.setText(tableDataArray.getJSONObject(i-1).getString("date"));
                        dayLabel.setPadding(10,0,10,0);
                        dayLabel.setTextColor(Color.GRAY);

                        TextView dayMass = new TextView(getActivity().getApplicationContext());
                        dayMass.setText(tableDataArray.getJSONObject(i-1).getString("mass"));
                        dayMass.setTextColor(Color.BLACK);
                        dayMass.setGravity(Gravity.CENTER);

                        TextView dayEated = new TextView(getActivity().getApplicationContext());
                        dayEated.setText(tableDataArray.getJSONObject(i-1).getString("food_sum"));
                        dayEated.setTextColor(Color.BLACK);
                        dayEated.setGravity(Gravity.CENTER);

                        TextView dayBurn = new TextView(getActivity().getApplicationContext());
                        dayBurn.setText(tableDataArray.getJSONObject(i-1).getString("active_sum"));
                        dayBurn.setTextColor(Color.BLACK);
                        dayBurn.setGravity(Gravity.CENTER);

                        TextView dayShoulders = new TextView(getActivity().getApplicationContext());
                        dayShoulders.setText(tableDataArray.getJSONObject(i-1).getString("shoulders"));
                        dayShoulders.setTextColor(Color.BLACK);
                        dayShoulders.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayRHand = new TextView(getActivity().getApplicationContext());
                        dayRHand.setText(tableDataArray.getJSONObject(i-1).getString("rHand"));
                        dayRHand.setTextColor(Color.BLACK);
                        dayRHand.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayLHand = new TextView(getActivity().getApplicationContext());
                        dayLHand.setText(tableDataArray.getJSONObject(i-1).getString("lHand"));
                        dayLHand.setTextColor(Color.BLACK);
                        dayLHand.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayChest = new TextView(getActivity().getApplicationContext());
                        dayChest.setText(tableDataArray.getJSONObject(i-1).getString("chest"));
                        dayChest.setTextColor(Color.BLACK);
                        dayChest.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayWaist = new TextView(getActivity().getApplicationContext());
                        dayWaist.setText(tableDataArray.getJSONObject(i-1).getString("waist"));
                        dayWaist.setTextColor(Color.BLACK);
                        dayWaist.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayButt = new TextView(getActivity().getApplicationContext());
                        dayButt.setText(tableDataArray.getJSONObject(i-1).getString("butt"));
                        dayButt.setTextColor(Color.BLACK);
                        dayButt.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayRLeg = new TextView(getActivity().getApplicationContext());
                        dayRLeg.setText(tableDataArray.getJSONObject(i-1).getString("rLeg"));
                        dayRLeg.setTextColor(Color.BLACK);
                        dayRLeg.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayLLeg = new TextView(getActivity().getApplicationContext());
                        dayLLeg.setText(tableDataArray.getJSONObject(i-1).getString("lLeg"));
                        dayLLeg.setTextColor(Color.BLACK);
                        dayLLeg.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView dayCalves = new TextView(getActivity().getApplicationContext());
                        dayCalves.setText(tableDataArray.getJSONObject(i-1).getString("calves"));
                        dayCalves.setTextColor(Color.BLACK);
                        dayCalves.setGravity(Gravity.CENTER_HORIZONTAL);

                        rowDayLabels.addView(dayLabel);
                        rowMass.addView(dayMass);
                        rowEatedCalories.addView(dayEated);
                        rowBurnCalories.addView(dayBurn);
                        rowShoulders.addView(dayShoulders);
                        rowRHand.addView(dayRHand);
                        rowLHand.addView(dayLHand);
                        rowChest.addView(dayChest);
                        rowWaist.addView(dayWaist);
                        rowButt.addView(dayButt);
                        rowRLeg.addView(dayRLeg);
                        rowLLeg.addView(dayLLeg);
                        rowCalves.addView(dayCalves);
                    }
                }
                table.addView(rowDayLabels);
                table.addView(rowMass);
                table.addView(rowEatedCalories);
                table.addView(rowBurnCalories);
                table.addView(rowShoulders);
                table.addView(rowRHand);
                table.addView(rowLHand);
                table.addView(rowChest);
                table.addView(rowWaist);
                table.addView(rowButt);
                table.addView(rowRLeg);
                table.addView(rowLLeg);
                table.addView(rowCalves);

                } catch (Exception e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG ).show();
                }

            }
            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Графики";
                case 1:
                    return "Таблица";
            }
            return null;
        }
    }
}