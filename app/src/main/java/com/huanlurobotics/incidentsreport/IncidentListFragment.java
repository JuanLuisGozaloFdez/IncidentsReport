/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huanlurobotics.incidentsreport.database.IncidentLab;

import java.util.List;

public class IncidentListFragment extends Fragment {

    private static final String TAG = "IncidentsReport";
    private static final int REQUEST_INCIDENT = 1;

    private RecyclerView mIncidentRecyclerView;
    private LinearLayout mIncidentEmptyLinearLayout;
    private LinearLayout mIncidentRetryLinearLayout;
    private ProgressBar mIncidentFragmentLoadProgressBar;
    private IncidentAdapter mAdapter;

    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final String EMPTY_INCIDENT_LIST = "emptyIncidentList";

    /*
    * Mandatory interface for activities with fragment
     */
    public interface Callbacks {
        void onIncidentSelected (Incident incident);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mCallbacks = (Callbacks) getActivity();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mCallbacks = (Callbacks) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "IncidentListFragment.onCreateView called");
        View view = inflater.inflate(R.layout.fragment_incident_list, container, false);
        Log.d(TAG, "IncidentListFragment.onCreateView inflater fragment_incident_list done");

        mIncidentRecyclerView = (RecyclerView) view.findViewById(R.id.incident_recycler_view);
        mIncidentEmptyLinearLayout = (LinearLayout) view.findViewById(R.id.ll_empty_fragment_incident_list);
        mIncidentRetryLinearLayout = (LinearLayout) view.findViewById(R.id.ll_retry_fragment_incident_list);
        mIncidentFragmentLoadProgressBar = (ProgressBar)
                view.findViewById(R.id.progress_bar_fragment_incident_list);

        checkViewElementStatus();
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();
        Log.d(TAG, "IncidentListFragment.onCreateView done");
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        checkViewElementStatus();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_incident_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_new_incident:
                Log.d(TAG, "IncidentListFragment.onOptionsItemSelected menu_item_new_incident called");
                Incident new_incident = new Incident(); // new empty incident
                Log.d(TAG, new_incident.toString());
                IncidentLab.get(getActivity()).addIncident(new_incident);
                /* These two lines are before Layout two Panes and Callbacks were coded
                Intent intent = IncidentPagerActivity.newIntent(getActivity(), new_incident.getId());
                startActivity(intent);
                * and the next two lines was added when callbacks was coded
                */
                updateUI();
                mCallbacks.onIncidentSelected(new_incident);
                Log.d(TAG, "IncidentListFragment.onOptionsItemSelected menu_item_new_incident completed");
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            case R.id.menu_item_hide_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    private void checkViewElementStatus() {
        IncidentLab incidentLab = IncidentLab.get(getActivity());
        if (incidentLab.getIncidents().size() == 0) {
            // ocultar fragment_recycler, retry y progress_bar
            mIncidentEmptyLinearLayout.setVisibility(View.VISIBLE);
            mIncidentRetryLinearLayout.setVisibility(View.GONE);
            mIncidentFragmentLoadProgressBar.setVisibility(View.GONE);
            mIncidentRecyclerView.setVisibility(View.GONE);
            Log.d(TAG, "IncidentListFragment.checkViewElementStatus size=0 done");
        } else {
            mIncidentEmptyLinearLayout.setVisibility(View.GONE);
            mIncidentRetryLinearLayout.setVisibility(View.GONE);
            mIncidentFragmentLoadProgressBar.setVisibility(View.GONE);
            mIncidentRecyclerView.setVisibility(View.VISIBLE);

            mIncidentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Log.d(TAG, "IncidentListFragment.checkViewElementStatus size!=0  done");
        }
    }

    // public when callbacks are coded to allow Activity to call it
    public void updateUI() {
        IncidentLab incidentLab = IncidentLab.get(getActivity());
        List<Incident> incidents = incidentLab.getIncidents();

        if (mAdapter == null) {
            mAdapter = new IncidentAdapter(incidents);
            mIncidentRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setIncidents(incidents);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }



    private void updateSubtitle() {
        IncidentLab incidentLab = IncidentLab.get(getActivity());
        int incidentCount = incidentLab.getIncidents().size();
        String subtitle = getResources().
                getQuantityString(R.plurals.subtitle_plural, incidentCount, incidentCount);

        if (!mSubtitleVisible) {
            subtitle = "";
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class IncidentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Incident mIncident;

        private TextView mTitleTextView;
        private TextView mDetailsTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public IncidentHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_incident_title_text_view);
            mDetailsTextView = (TextView)
                    itemView.findViewById(R.id.list_item_incident_details_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_incident_date_text_view);
            mSolvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_incident_solved_checked_box);

        }

        public void bindIncident(Incident incident) {
            if (incident == null) {
                return;
            }
            mIncident = incident;
            mTitleTextView.setText(mIncident.getTitle());
            mDetailsTextView.setText(mIncident.getDetails());
            mDateTextView.setText(mIncident.getDate().toString());
            mSolvedCheckBox.setChecked(mIncident.isSolved());
        }

        @Override
        public void onClick(View view) {
            // Intent intent = new Intent (getActivity(), IncidentActivity.class);
            // Intent intent = IncidentActivity.newIntent (getActivity(), mIncident.getId());
            /* These two lines are before Layout two Panes and Callbacks were coded
                Intent intent = IncidentPagerActivity.newIntent(getActivity(), mIncident.getId());
                startActivity(intent);
                * and the next line was added when callbacks was coded
            */
            mCallbacks.onIncidentSelected(mIncident);
            //startActivityForResult(intent, REQUEST_INCIDENT);
        }

        // TODO comprobar donde hay que poner el siguiente metodo porque no es un override aqui
        /*
        @Override
        public void onActivityResult (int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_INCIDENT) {
                // hacer algo con el resultado obtenido
            }
        }
        */
    }

    private class IncidentAdapter extends RecyclerView.Adapter<IncidentHolder> {

        public List<Incident> mIncidents;

        public IncidentAdapter(List<Incident> incidents) {

            mIncidents = incidents;
        }

        @NonNull
        @Override
        public IncidentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_incident,
                    parent, false);
            return new IncidentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IncidentHolder holder, int position) {
            Incident incident = mIncidents.get(position);
            holder.bindIncident(incident);
        }

        @Override
        public int getItemCount() {
            return mIncidents.size();
        }

        public void setIncidents(List<Incident> incidents) {
            mIncidents = incidents;
        }
    }
}

