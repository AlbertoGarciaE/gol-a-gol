/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.google.android.gcm.GolAGol.ui;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GolAGol.logic.InstanceIdHelper;
import com.google.android.gcm.GolAGol.R;
import com.google.android.gcm.GolAGol.model.Constants;

/**
 * Fragment for registering and unregistering GCM tokens, as well as running quick tests.
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

        Button btnGetToken = (Button) view.findViewById(R.id.iid_get_token);
        Button btnEraseToken = (Button) view.findViewById(R.id.iid_delete_token);


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
        //TextView textViewStatus = (TextView) v.getRootView().findViewById(R.id.iid_status_token);
        switch (v.getId()) {
            case R.id.iid_get_token:
                if (!alreadyRegistered) {
                    TextView textView = (TextView) v.getRootView().findViewById(R.id.user_name);
                    String name = textView.getText().toString();
                    //TextView textViewStatus = (TextView) v.getRootView().findViewById(R.id.iid_status_token);
                    if (!name.isEmpty()) {
                        mInstanceIdHelper.getGcmTokenInBackground(name);
                        //textViewStatus.setText(R.string.iid_status_token_success);
                        // message = (String) mContext.getResources().getText(R.string.iid_status_token_success);
                    } else {
                        //textViewStatus.setText(R.string.iid_field_error_empty);
                        message = (String) mContext.getResources().getText(R.string.iid_field_error_empty);
                    }
                } else {
                    message = (String) mContext.getResources().getText(R.string.iid_status_token_already_registered);
                }

                //textViewStatus.setText(message);
                if (!message.isEmpty()) {
                    Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.iid_delete_token:
                if (alreadyRegistered) {
                    mInstanceIdHelper.deleteGcmTokeInBackground();
                    //message = (String) mContext.getResources().getText(R.string.iid_status_token_success);
                } else {
                    message = (String) mContext.getResources().getText(R.string.iid_status_token_not_yet_registered);
                }
                //textViewStatus.setText(message);
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
        if (alreadyRegistered) {
            textViewStatus.setText(R.string.iid_status_exist_token);
            textViewDetails.setText("Tu ID es " + token);
        } else {
            textViewStatus.setText(R.string.iid_status_exist_no_token);
            textViewDetails.setText("Tu ID es " + token);
        }
    }

    private void toggleAboutApi() {
        toggleText((TextView) getActivity().findViewById(R.id.iid_token_details),
                R.string.iid_about_apis, R.string.iid_about_apis_open);
        toggleVisibility(getActivity().findViewById(R.id.iid_full_token_details));
    }


}
