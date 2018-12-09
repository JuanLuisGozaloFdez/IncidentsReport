/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.huanlurobotics.incidentsreport.database.IncidentLab;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class IncidentFragment extends Fragment {

    private static final String TAG = "IncidentsReport";

    private static final String ARG_INCIDENT_ID = "incident_id";
    private static final String DIALOG_DATE = "DialogDate";

    // variables para los códigos de petición
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    private Incident mIncident;
    private EditText mTitleField;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private EditText mDetailsField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private Button mReportButton;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    /**
     * Mandatory interface for activities with fragment
     *
     */
    public interface Callbacks {
        void onIncidentUpdated(Incident incident);
    }

    public static IncidentFragment newInstance (UUID incidentId) {
        Log.d(TAG, "IncidentFragment.newInstance called");
        Bundle args = new Bundle();
        args.putSerializable(ARG_INCIDENT_ID, incidentId);

        IncidentFragment fragment = new IncidentFragment();
        fragment.setArguments(args);
        Log.d(TAG, "IncidentFragment.newInstance completed");
        return fragment;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IncidentFragment.onCreate called");
        setHasOptionsMenu(true);
        // mIncident = new Incident(); //not required when passing data in the extra
        // UUID incidentId = (UUID) getActivity().getIntent()
        //                .getSerializableExtra(IncidentActivity.EXTRA_INCIDENT_ID);
        UUID incidentId = (UUID) getArguments().getSerializable(ARG_INCIDENT_ID);
        mIncident = IncidentLab.get(getActivity()).getIncident(incidentId);
        mPhotoFile = IncidentLab.get(getActivity()).getPhotoFile(mIncident);
        if (mPhotoFile == null) {
            Log.d(TAG, "IncidentFragment.onCreate done with mPhotoFile null");
        } else {
            Log.d(TAG, "IncidentFragment.onCreate done with mPhotoFile " + mPhotoFile.getName());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // TODO si cambiamos para que la edicion solo se haga con el boton ACCEPT, esta linea no es valida aqui
        IncidentLab.get(getActivity()).updateIncident(mIncident);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "IncidentFragment.onCreateView called");
        View v = inflater.inflate(R.layout.fragment_incident, container, false);
        Log.d(TAG, "IncidentFragment.onCreateView inflater fragment_incident done");

        mTitleField = (EditText) v.findViewById(R.id.incident_title);
        mTitleField.setText(mIncident.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mIncident.setTitle(charSequence.toString());
                updateIncident();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mDetailsField = (EditText) v.findViewById(R.id.incident_details);
        mDetailsField.setText(mIncident.getDetails());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mIncident.setDetails(charSequence.toString());
                updateIncident();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mDateButton = (Button) v.findViewById(R.id.incident_date);
        // mDateButton.setEnabled(false); // disable when the listener is coded (what used to avoid user to press before listener is stablished)
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mIncident.getDate());
                dialog.setTargetFragment(IncidentFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.incident_solved);
        mSolvedCheckBox.setChecked(mIncident.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
               // establecer la propiedad del incidentn resuelto
               mIncident.setSolved(isChecked);
               updateIncident();

            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.incident_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
                updateIncident();

            }
        });

        mCallSuspectButton = (Button) v.findViewById(R.id.incident_call_suspect);
        if (mIncident.getSuspect()!= null && !mIncident.getSuspect().equals("")) {
            setSuspectToButton();
            setSuspectPhoneToButton();
        }
        // Check if the Contacts are available and Camera also if not, disable the options
        PackageManager packageManager= getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY)== null) {
            mSuspectButton.setEnabled(false);
            mCallSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.incident_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager)!= null;
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
                updateIncident();

            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.incident_photo);
        updatePhotoView();

        mCallSuspectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v) {
                Intent intent = new Intent (Intent.ACTION_DIAL);
                intent.setData(Uri.parse(getString(R.string.action_dial_tel_label,
                        mIncident.getSuspectPhone())));
                startActivity(intent);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.incident_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v) {
                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.setType ("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getIncidentReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.incident_report_subject));
                intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        // return super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "IncidentFragment.onCreateView completed");
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mIncident.setDate (date);
            updateIncident();
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            // TODO add code to select a time for the incident AND delete from the previous date branch
            updateIncident();
        } else if (requestCode == REQUEST_CONTACT) {
            getSuspectFromContacts(data);
            setSuspectPhoneToButton();
            updateIncident();
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            updateIncident();
        }
    }

    private boolean getSuspectFromContacts(Intent data) {
        Uri contactUri = data.getData();
        // to define which is the field to retrieve the values
        String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
        // Execute the query. The contactUri instruction will work like a "where" condition
        Cursor cursor = getActivity().getContentResolver().query(contactUri,queryFields,
                null,null,null);
        try {
            // validate the results
            if (cursor.getCount() == 0) {
                // no results
                return false;
            }
            // select first column from the first row of the returned data
            // this is the suspect name
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mIncident.setSuspect(suspect);
            setSuspectToButton();
        } finally {
            cursor.close();
        }
        return true;
    }

    private void setSuspectToButton() {
        mSuspectButton.setText(getString(R.string.incident_suspect_label,
                mIncident.getSuspect()));
    }

    private void setSuspectPhoneToButton() {
        getPhoneNumberSuspectFromContacts(mIncident.getSuspect());
        String texto = mIncident.getSuspectPhone();
        //if (mCallSuspectButton == null) {
        //    Log.d(TAG, "suspectphone=" + texto);
        //} else {
            mCallSuspectButton.setText(getString(R.string.incident_suspect_phone_label, texto));
        //}
    }

    private boolean getPhoneNumberSuspectFromContacts(String name) {
        String[] queryFields = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name +"%'";
        Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields,
                selection, null, null);
        try {
            // validate the results
            if (cursor.getCount() == 0) {
                // no results
                mIncident.setSuspectPhone(getString(R.string.incident_suspect_no_phone));
                Log.d(TAG, "suspectphone="+mIncident.getSuspectPhone());
                return false;
            }
            // select the appropiate phone
            cursor.moveToFirst();
            String phoneNumber = cursor.getString(0);
            mIncident.setSuspectPhone(phoneNumber);
        } finally {
            cursor.close();
        }
        Log.d(TAG, "suspectphone="+mIncident.getSuspectPhone());
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_incident_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_item_confirm_edit_incident:
                IncidentLab.get(getActivity()).updateIncident(mIncident);
                returnResult();
                getActivity().finish();
                return true;
            case R.id.menu_item_delete_incident:
                // TODO incorporar un Confirm Dialog
                IncidentLab.get(getActivity()).deleteIncident(mIncident);
                returnResult();
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void updateIncident() {
        IncidentLab.get(getActivity()).updateIncident(mIncident);
        mCallbacks.onIncidentUpdated(mIncident);
    }

    private void updateDate() {
        // mDateButton.setText(DateFormat.getDateInstance(DateFormat.FULL).format(mIncident.getDate()));
        mDateButton.setText(mIncident.getDate().toString());
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            if (mPhotoFile == null) {
                Log.d(TAG, "updatePhotoView with mPhotoFile null");
            } else {
                Log.d(TAG, "updatePhotoView with mPhotoFile not exists");
            }
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());

            mPhotoView.setImageBitmap(bitmap);
        }
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    private String getIncidentReport() {
        String solvedString = null;
        if (mIncident.isSolved()) {
            solvedString = getString(R.string.incident_report_solved);
        } else {
            solvedString = getString(R.string.incident_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format (dateFormat, mIncident.getDate()).toString();

        String suspect = mIncident.getSuspect();
        if (suspect == null || suspect.equals("")) {
            suspect = getString(R.string.incident_report_no_suspect);
        } else {
            suspect = getString(R.string.incident_report_suspect);
        }

        String report = getString(R.string.incident_report_content,
                mIncident.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
