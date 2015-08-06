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
package com.google.android.gcm.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gcm.demo.R;
import com.google.android.gcm.demo.logic.InstanceIdHelper;

/**
 * Fragment for registering and unregistering GCM tokens, as well as running quick tests.
 * This is the default fragment shown when the app starts.
 */
public class InstanceIdFragment extends AbstractFragment
        implements View.OnClickListener, MainActivity.RefreshableFragment {
    private InstanceIdHelper mInstanceIdHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_instanceid, container, false);
        view.findViewById(R.id.iid_get_token).setOnClickListener(this);
        view.findViewById(R.id.iid_delete_token).setOnClickListener(this);
        view.findViewById(R.id.iid_about_apis).setOnClickListener(this);

        mInstanceIdHelper = new InstanceIdHelper(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iid_get_token:
                mInstanceIdHelper.getGcmTokenInBackground();
                break;
            case R.id.iid_delete_token:
                mInstanceIdHelper.deleteGcmTokeInBackground();
                break;
            case R.id.iid_about_apis:
                toggleAboutApi();
                break;
        }
    }



    @Override
    public void refresh() {

    }

    private void toggleAboutApi() {
        toggleText((TextView) getActivity().findViewById(R.id.iid_about_apis),
                R.string.iid_about_apis, R.string.iid_about_apis_open);
        toggleVisibility(getActivity().findViewById(R.id.iid_about_apis_full_text));
    }




}
