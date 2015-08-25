package com.google.android.gcm.GolAGol.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GolAGol.R;

/**
 * This is the base class for all the fragments used by the {@link MainActivity}.
 * This class provides useful methods for performing UI operations such as creating expandable text,
 * handling long clicks by copying the content of a view to clipboard and handling text that is
 * too long to be displayed.
 */
public abstract class AbstractFragment extends Fragment implements View.OnLongClickListener {

    @Override
    public boolean onLongClick(View v) {
        // Copy in the clipboard the value of the selected widget.
        String value = getValue(v.getId());
        if (value.length() > 0) {
            ClipboardManager clipboard =
                    (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("GcmTestApp clipboard", value));
            Toast.makeText(getActivity(),
                    value + "\nhas been copied in the clipboard", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    protected String getValue(int viewId) {
        View view = getActivity().findViewById(viewId);
        if (view.getTag(R.id.tag_clipboard_value) instanceof String) {
            return (String) view.getTag(R.id.tag_clipboard_value);
        }
        if (view instanceof TextView) {
            return ((TextView) view).getText().toString();
        }
        if (view instanceof Spinner) {
            return ((Spinner) view).getSelectedItem().toString();
        }
        return "";
    }

    protected void toggleVisibility(View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    protected void toggleText(TextView textView, int textA, int textB) {
        String stringA = getString(textA);
        if (textView.getText().toString().equals(stringA)) {
            textView.setText(textB);
        } else {
            textView.setText(textA);
        }
    }

    public static String truncateToLongString(String value) {
        return truncateString(value, 27);
    }

    public static String truncateToMediumString(String value) {
        return truncateString(value, 19);
    }

    public static String truncateToShortString(String value) {
        return truncateString(value, 11);
    }

    public static String truncateString(String value, int maxLength) {
        maxLength = (maxLength < 5) ? 5 : maxLength;
        int tail = (maxLength - 3) / 2;
        if (value.length() > maxLength) {
            return value.substring(0, tail) + "..."
                    + value.substring(value.length() - tail, value.length());
        } else {
            return value;
        }
    }

    protected static boolean isNotEmpty(String s) {
        return s != null && !"".equals(s.trim());
    }

}
