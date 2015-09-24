package com.num.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.num.R;
import com.num.model.Survey;

import android.R.layout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SurveyListAdapter extends ArrayAdapter<Survey> {
    private Context context;
    private List<Survey> values;

    public SurveyListAdapter(Context context, List<Survey> values){
        super(context, R.layout.row_survey, values);
        this.context = context;
        this.values = values;
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_survey, parent, false);
        }
        TextView title = (TextView) rowView.findViewById(R.id.survey_title);
        TextView desc = (TextView) rowView.findViewById(R.id.survey_desc);

        Survey survey = values.get(pos);
        title.setText(survey.title);
        desc.setText(survey.desc);
        return rowView;
    }

}
