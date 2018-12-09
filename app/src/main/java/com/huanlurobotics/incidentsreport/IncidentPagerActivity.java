/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.huanlurobotics.incidentsreport.database.IncidentLab;

import java.util.List;
import java.util.UUID;

public class IncidentPagerActivity extends AppCompatActivity  implements IncidentFragment.Callbacks {

    private static final String TAG = "IncidentsReport";

    private static final String EXTRA_INCIDENT_ID = "com.huanlurobotics.criminalintent.incident_id";
    private ViewPager mViewPager;
    private List<Incident> mIncidents;

    public static Intent newIntent (Context packageContext, UUID incidentId) {
        Intent intent = new Intent(packageContext, IncidentPagerActivity.class);
        intent.putExtra(EXTRA_INCIDENT_ID, incidentId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_pager);
        UUID incidentId = (UUID) getIntent().getSerializableExtra(EXTRA_INCIDENT_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_incident_pager_view_pager);

        mIncidents = IncidentLab.get(this).getIncidents();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Incident incident = mIncidents.get(position);
                return IncidentFragment.newInstance(incident.getId());
            }

            @Override
            public int getCount() {
                return mIncidents.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Incident incident = mIncidents.get(position);
                if (incident.getTitle() != null) {
                    setTitle(incident.getTitle());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        Log.d(TAG, "IncidentPagerActivity stablishing initial element of pager incidentId="+incidentId +", size ="+mIncidents.size()+", in mIncidents="+mIncidents);
        for (int i = 0; i < mIncidents.size(); i++) {
            Log.d(TAG, "mIncidents.get("+i+")="+mIncidents.get(i)+" .getId()="+mIncidents.get(i).getId());
            if (mIncidents.get(i).getId().equals(incidentId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onIncidentUpdated(Incident incident) {

    }
}
