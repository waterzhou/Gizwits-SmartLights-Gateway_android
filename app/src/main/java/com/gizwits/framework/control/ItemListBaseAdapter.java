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
 * Created by water.zhou on 7/31/2015.
 */
public class ItemListBaseAdapter extends ArrayAdapter<scene> {

    protected static final String LOG_TAG = ItemListBaseAdapter.class.getSimpleName();

    private List<scene> items;
    private int layoutResourceId;
    private Context context;

    public ItemListBaseAdapter(Context context, int layoutResourceId, List<scene> items) {
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
        holder.scene = items.get(position);
        holder.removeScene = (ImageButton)row.findViewById(R.id.scene_remove);
        holder.removeScene.setTag(holder.scene);

        holder.switchOnOff = (Button)row.findViewById(R.id.ApplyButton);
        holder.switchOnOff.setTag(holder.scene);
        //setSwitchButtonListeners(holder);

        holder.name = (TextView)row.findViewById(R.id.sceneName);
        setNameTextChangeListener(holder);
        holder.id = (TextView)row.findViewById(R.id.idDetail);
        setIdTextListeners(holder);
        //holder.value = (TextView)row.findViewById(R.id.sceneDetail);
       // setValueTextListeners(holder);

        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(sceneHolder holder) {
        holder.name.setText(holder.scene.getName());
        holder.id.setText(String.valueOf(holder.scene.getId()));
    }

    public void clearData(){
        items.clear();
    }
    public static class sceneHolder {
        scene scene;
        TextView name;
        TextView id;
        //TextView value;
        Button switchOnOff;
        ImageButton removeScene;
    }

    private void setNameTextChangeListener(final sceneHolder holder) {
        holder.name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                holder.scene.setName(s.toString());
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
                    holder.scene.setId(Integer.parseInt(s.toString()));
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
