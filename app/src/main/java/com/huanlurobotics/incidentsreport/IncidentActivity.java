/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class IncidentActivity extends SingleFragmentActivity {

    private static final String EXTRA_INCIDENT_ID = "com.huanlurobotics.criminalintent.incident_id";

    // This method is called when the activity is created first time
    @Override
    protected Fragment createFragment() {

        UUID incidentId = (UUID) getIntent().getSerializableExtra(EXTRA_INCIDENT_ID);
        return IncidentFragment.newInstance(incidentId);
    }

    public static Intent newIntent(Context packageContext, UUID incidentId) {
        Intent intent = new Intent(packageContext, IncidentActivity.class);
        intent.putExtra(EXTRA_INCIDENT_ID, incidentId);
        return intent;
    }
}