package l5r.toolbox.common;

import l5r.toolbox.R;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class GuiUtils {
	
	private static final int separatorColor = Color.parseColor("#80808080");

	public static TableRow generateRow(TableLayout tableLayout, String text, String buttonText, View.OnClickListener action, Activity activity) {
        LayoutInflater factory = activity.getLayoutInflater();
        TableRow tableRow = (TableRow)factory.inflate(R.layout.table_row, tableLayout, false);
        TextView textView = (TextView)tableRow.findViewById(R.id.TableRowTextView);
        textView.setText(text);
        tableRow.setOnClickListener(action);

		return tableRow;
	}
	
	public static View generateSeparator(Activity activity) {
		float dip = activity.getResources().getDisplayMetrics().density;;
		
		//add separator
		TextView separator = new TextView(activity);
		separator.setBackgroundColor(separatorColor);
		separator.setHeight((int) (2*dip));
		
		return separator;
	}
}
