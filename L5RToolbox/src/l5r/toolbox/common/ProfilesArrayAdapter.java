package l5r.toolbox.common;

import l5r.toolbox.profile.ProfileData;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ProfilesArrayAdapter extends ArrayAdapter<ProfileData> implements SpinnerAdapter {

    public ProfilesArrayAdapter(Context context) {
        super(context, android.R.layout.simple_spinner_item);
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setText(getItem(position).getTitle());
        return view;
    }
}