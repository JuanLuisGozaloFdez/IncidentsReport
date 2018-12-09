/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class IncidentListActivity extends SingleFragmentActivity
        implements IncidentListFragment.Callbacks, IncidentFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new IncidentListFragment();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onIncidentSelected(Incident incident){
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = IncidentPagerActivity.newIntent(this, incident.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = IncidentFragment.newInstance(incident.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail).commit();
        }
    }

    @Override
    public void onIncidentUpdated(Incident incident) {
        IncidentListFragment listFragment = (IncidentListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
