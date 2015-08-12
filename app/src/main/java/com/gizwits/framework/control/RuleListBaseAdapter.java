package com.gizwits.framework.control;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gizwits.smartlight.R;

import java.util.List;

/**
 * Created by water.zhou on 8/7/2015.
 */
public class RuleListBaseAdapter extends ArrayAdapter<iftttrule> {

    protected static final String LOG_TAG = RuleListBaseAdapter.class.getSimpleName();

    private List<iftttrule> items;
    private int layoutResourceId;
    private Context context;

    public RuleListBaseAdapter(Context context, int layoutResourceId, List<iftttrule> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final sceneHolder holder = new sceneHolder();

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        //holder = new sceneHolder();
        holder.iftttrule = items.get(position);
        holder.removeRule = (ImageButton)row.findViewById(R.id.rule_remove);
        holder.removeRule.setTag(holder.iftttrule);

        holder.apply = (Button)row.findViewById(R.id.ApplyRuleButton);
        holder.apply.setTag(holder.iftttrule);

        holder.input = (TextView)row.findViewById(R.id.ruleInput);
        setInputTextChangeListener(holder);
        holder.output = (TextView)row.findViewById(R.id.ruleOutput);
        setOutputTextChangeListener(holder);
        holder.condition = (TextView)row.findViewById(R.id.ruleCondition);
        setConditionTextChangeListener(holder);
        holder.id = (TextView)row.findViewById(R.id.ruleId);
        setIdTextListeners(holder);
        row.setTag(holder);
        setupItem(holder);
        return row;
    }

    private void setupItem(sceneHolder holder) {
        holder.input.setText(holder.iftttrule.getInput());
        holder.output.setText(holder.iftttrule.getOutput());
        holder.condition.setText(holder.iftttrule.getCondition());
        holder.id.setText(String.valueOf(holder.iftttrule.getId()));
    }

    public void clearData(){
        items.clear();
    }
    public static class sceneHolder {
        iftttrule iftttrule;
        TextView input;
        TextView output;
        TextView condition;
        TextView id;
        Button apply;
        ImageButton removeRule;
    }

    private void setInputTextChangeListener(final sceneHolder holder) {
        holder.input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.iftttrule.setInput(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setOutputTextChangeListener(final sceneHolder holder) {
        holder.output.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.iftttrule.setOutput(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setConditionTextChangeListener(final sceneHolder holder) {
        holder.condition.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.iftttrule.setCondition(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    private void setIdTextListeners(final sceneHolder holder) {
        holder.id.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    holder.iftttrule.setId((byte)Integer.parseInt(s.toString()));
                } catch (NumberFormatException e) {
                    Log.e(LOG_TAG, "error reading string value: " + s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}
