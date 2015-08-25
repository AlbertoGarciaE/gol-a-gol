package com.google.android.gcm.GolAGol.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GolAGol.R;
import com.google.android.gcm.GolAGol.logic.InstanceIdHelper;
import com.google.android.gcm.GolAGol.model.Constants;

/**
 * Fragment for registering and unregistering GCM tokens.
 * This is the default fragment shown when the app starts.
 */
public class InstanceIdFragment extends AbstractFragment
        implements View.OnClickListener, MainActivity.RefreshableFragment {
    private InstanceIdHelper mInstanceIdHelper;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_instanceid, container, false);
        view.findViewById(R.id.iid_get_token).setOnClickListener(this);
        view.findViewById(R.id.iid_delete_token).setOnClickListener(this);
        view.findViewById(R.id.iid_token_details).setOnClickListener(this);

        mContext = getActivity().getApplicationContext();
        mInstanceIdHelper = new InstanceIdHelper(mContext);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        refresh();
    }

    @Override
    public void onClick(View v) {
        String message = "";
        boolean alreadyRegistered = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
        switch (v.getId()) {
            case R.id.iid_get_token:
                if (!alreadyRegistered) {
                    TextView textView = (TextView) v.getRootView().findViewById(R.id.user_name);
                    String name = textView.getText().toString();
                    if (!name.isEmpty()) {
                        mInstanceIdHelper.getGcmTokenInBackground(name);
                    } else {
                        message = (String) mContext.getResources().getText(R.string.iid_field_error_empty);
                    }
                } else {
                    message = (String) mContext.getResources().getText(R.string.iid_status_token_already_registered);
                }
                if (!message.isEmpty()) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.iid_delete_token:
                if (alreadyRegistered) {
                    mInstanceIdHelper.deleteGcmTokeInBackground();
                } else {
                    message = (String) mContext.getResources().getText(R.string.iid_status_token_not_yet_registered);
                }
                if (!message.isEmpty()) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.iid_token_details:
                toggleAboutApi();
                break;
        }
    }


    @Override
    public void refresh() {
        TextView textViewStatus = (TextView) getView().findViewById(R.id.iid_status_token);
        TextView textViewDetails = (TextView) getView().findViewById(R.id.iid_full_token_details);
        boolean alreadyRegistered = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
        String token = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.GCM_TOKEN, "");
        String name = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Constants.USER_NAME, "");
        if (alreadyRegistered) {
            textViewStatus.setText(R.string.iid_status_exist_token);
            textViewDetails.setText("Hola " + name + "!!! Tu identificador es " + token);
        } else {
            textViewStatus.setText(R.string.iid_status_exist_no_token);
            textViewDetails.setText("No hay detalles de usuario");
        }
    }

    private void toggleAboutApi() {
        toggleText((TextView) getActivity().findViewById(R.id.iid_token_details),
                R.string.iid_about_apis, R.string.iid_about_apis_open);
        toggleVisibility(getActivity().findViewById(R.id.iid_full_token_details));
    }


}
