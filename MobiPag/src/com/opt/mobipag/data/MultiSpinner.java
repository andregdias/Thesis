package com.opt.mobipag.data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.opt.mobipag.R;

import java.util.List;

public class MultiSpinner extends Spinner implements OnMultiChoiceClickListener, OnCancelListener {
    private List<String> items;
    private boolean[] selected;
    private String spinnerText;
    private MultiSpinnerListener listener;
    private Context context;

    public MultiSpinner(Context context) {
        super(context);
        this.context = context;
    }

    public MultiSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public String getZones() {
        if (spinnerText == null)
            return "";
        return spinnerText;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        int count = 0;
        for (boolean aSelected : selected)
            if (aSelected)
                count++;
        if (count > 12) {
            Context c = getContext();
            if (c != null) {
                Toast toast = Toast.makeText(c, context.getText(R.string.zonelimit), Toast.LENGTH_LONG);
                toast.show();
            }
        }
        selected[which] = isChecked && count <= 12;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // refresh text on spinner
        StringBuilder spinnerBuffer = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (selected[i]) {
                spinnerBuffer.append(items.get(i));
                spinnerBuffer.append(", ");
            }
        }

        spinnerText = spinnerBuffer.toString();
        if (spinnerText.length() > 2)
            spinnerText = spinnerText.substring(0, spinnerText.length() - 2);

        Context c = getContext();
        if (c != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
                    android.R.layout.simple_spinner_item,
                    new String[]{spinnerText});
            setAdapter(adapter);
            listener.onItemsSelected(selected);
        }
    }

    @Override
    public boolean performClick() {
        Context c = getContext();
        if (c != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setMultiChoiceItems(
                    items.toArray(new CharSequence[items.size()]), selected, this);
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.setOnCancelListener(this);
            builder.show();
        }
        return true;
    }

    public void setItems(List<String> items,
                         MultiSpinnerListener listener) {
        this.items = items;
        this.listener = listener;

        // all deselected by default
        selected = new boolean[items.size()];
        for (int i = 0; i < selected.length; i++)
            selected[i] = false;

        // all text on the spinner
        Context c = getContext();
        if (c != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item);
            setAdapter(adapter);
        }
    }

    public interface MultiSpinnerListener {
        public void onItemsSelected(boolean[] selected);
    }
}