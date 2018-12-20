package com.example.meetapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    private Map<String, String> datesToDisplay = new LinkedHashMap<>();


    private ArrayList<Integer> buttonsIdForListeners = new ArrayList<>();
    private int membersAmount = 5;

    private int DAYS_IN_CALENDAR = 7;
    private int TOP_SELECTIONS_TO_DISPLAY = 3;

    private HashMap<TimeSlot,Integer> slotSelections = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setDatesToDisplay();
        setButtonsIdForListeners();
        setListeners();
    }

    public void createDatesMap(){
        datesToDisplay.put("20", "Today");
        datesToDisplay.put("21", "Fri");
        datesToDisplay.put("22", "Sat");
        datesToDisplay.put("23", "Sun");
        datesToDisplay.put("24", "Mon");
        datesToDisplay.put("25", "Tue");
        datesToDisplay.put("26", "Wed");
    }

    public void buttonSelection(TimeSlot timeSlot) {
        if (!timeSlot.getClicked()) {
            clickedOn(timeSlot);
        } else {
            clickedOff(timeSlot);
        }
        slotSelections = sortByValue(slotSelections);
        displayTopSelections();
    }

    public void clickedOn(TimeSlot timeSlot) {
        slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) + 1 : 1);
        String textWithSelectionNumber = slotSelections.get(timeSlot) +
                                         "/" + membersAmount;
        timeSlot.getButton().setText(textWithSelectionNumber);
        timeSlot.setClicked(true);
        float percentage = ((float)slotSelections.get(timeSlot)/(float)membersAmount)*100;
        int bgColor = getColorPercentage(0xe0ffd2, 0x67a34c,(int)percentage);
        timeSlot.getButton().setBackground(setBackGroungColorAndBorder(bgColor));
    }

    public void clickedOff(TimeSlot timeSlot){
        timeSlot.getButton().setBackgroundResource(R.drawable.custom_border);

        timeSlot.setClicked(false);
        String textWithSelectionNumber = timeSlot.getHour();
        timeSlot.getButton().setText(textWithSelectionNumber);
        if (slotSelections.get(timeSlot)>1){
            slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) -1 : 1);
        }else{
            slotSelections.remove(timeSlot);
        }
    }

    public void setButtonsIdForListeners() {
        for (int i = 0; i < DAYS_IN_CALENDAR; i++) {
            int morningBtnId = getResources().getIdentifier("d" + i + "m", "id", getPackageName());
            buttonsIdForListeners.add(morningBtnId);
            int afternoonBtnId = getResources().getIdentifier("d" + i + "a", "id", getPackageName());
            buttonsIdForListeners.add(afternoonBtnId);
            int eveningBtnId = getResources().getIdentifier("d" + i + "e", "id", getPackageName());
            buttonsIdForListeners.add(eveningBtnId);
        }
    }

    public void setListeners(){
        for (int id : buttonsIdForListeners){
            final Button timeSlotButton = findViewById(id);
            int indexOfDate = Integer.valueOf(timeSlotButton.getTag().toString());
            final TimeSlot timeSlot = new TimeSlot(timeSlotButton, datesToDisplay.get(indexOfDate));
            timeSlotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(timeSlot);
                }
            });
        }
    }

    public static HashMap<TimeSlot, Integer> sortByValue(HashMap<TimeSlot, Integer> hm)
    {
        List<Map.Entry<TimeSlot, Integer> > list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<TimeSlot, Integer> >() {
            public int compare(Map.Entry<TimeSlot, Integer> o1,
                               Map.Entry<TimeSlot, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<TimeSlot, Integer> sortedList = new LinkedHashMap<>();
        for (Map.Entry<TimeSlot, Integer> item : list) {
            sortedList.put(item.getKey(), item.getValue());
        }
        return sortedList;
    }

    public void displayTopSelections(){
        String topSelections = "Top Suggesions:\n";
        int iterationNumber = TOP_SELECTIONS_TO_DISPLAY;
        if (iterationNumber > slotSelections.size()){
            iterationNumber = slotSelections.size();
        }
        for (int i = 0; i < iterationNumber; i++){
            TimeSlot topSlot = (TimeSlot) slotSelections.keySet().toArray()[i];
            topSelections = topSelections + topSlot.getDate() + " " + topSlot.getHour() + " - " +
                    + slotSelections.get(topSlot) + "/" + membersAmount + "\n";
        }
    }

    public void setDatesToDisplay(){
        createDatesMap();
        for (int i = 0; i < DAYS_IN_CALENDAR; i++){
            int dayNumTextViewId = getResources().getIdentifier("d" + i + "n" , "id", getPackageName());
            TextView dayNumTextView = findViewById(dayNumTextViewId);
            dayNumTextView.setText((String)datesToDisplay.keySet().toArray()[i]);
            int dayTextViewId = getResources().getIdentifier("d" + i + "d" , "id", getPackageName());
            TextView dayTextView = findViewById(dayTextViewId);
            int dayString = getResources().getIdentifier(datesToDisplay.get((datesToDisplay.keySet().toArray()[i])), "string", getPackageName());
            dayTextView.setText(getString(dayString));
        }
    }

    public static int getColorPercentage(int colorStart, int colorEnd, int percent){
        return Color.rgb(
                ColorPercentageCalculation(Color.red(colorStart), Color.red(colorEnd), percent),
                ColorPercentageCalculation(Color.green(colorStart), Color.green(colorEnd), percent),
                ColorPercentageCalculation(Color.blue(colorStart), Color.blue(colorEnd), percent)
        );
    }

    private static int ColorPercentageCalculation(int colorStart, int colorEnd, int percent){
        return ((Math.max(colorStart, colorEnd)*(100-percent)) + (Math.min(colorStart, colorEnd)*percent)) / 100;
    }


    public Drawable setBackGroungColorAndBorder(int color) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{
                android.R.attr.state_focused, -android.R.attr.state_pressed,
        }, getBackGroundAndBorder(color));
        states.addState(new int[]{
                android.R.attr.state_focused, android.R.attr.state_pressed,
        }, getBackGroundAndBorder(color));
        states.addState(new int[]{
                -android.R.attr.state_focused, android.R.attr.state_pressed,
        }, getBackGroundAndBorder(color));
        states.addState(new int[]{
                android.R.attr.state_enabled
        }, getBackGroundAndBorder(color));

        return states;
    }

    public Drawable getBackGroundAndBorder(int color) {
        Drawable[] drawablesForBackGroundAndBorder = new Drawable[2];
        drawablesForBackGroundAndBorder[0] = getBorder();
        drawablesForBackGroundAndBorder[1] = getBackGround(color);
        LayerDrawable layerDrawable = new LayerDrawable(drawablesForBackGroundAndBorder);
        layerDrawable.setLayerInset(1, 2, 2, 2, 2);
        return layerDrawable.mutate();
    }

    public Drawable getBorder() {
        RectShape rectShape = new RectShape();
        ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        shapeDrawable.getPaint().setStrokeWidth(10f);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return shapeDrawable.mutate();
    }

    public Drawable getBackGround(int color) {
        RectShape rectShape = new RectShape();
        ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return shapeDrawable.mutate();
    }
}
