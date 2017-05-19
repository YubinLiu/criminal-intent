package com.example.criminalintent.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.criminalintent.R;
import com.example.criminalintent.bean.Crime;
import com.example.criminalintent.bean.CrimeLab;
import com.example.criminalintent.util.DateFormatUtil;
import com.example.criminalintent.util.PictureUtils;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by yubin on 2017/4/23.
 */

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";

    private static final String DIALOG_DATE = "DialogDate";

    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;

    private static final int REQUEST_TIME = 1;

    private static final int REQUEST_CONTACT = 2;

    public static final int REQUEST_PHOTO = 3;

    private Crime mCrime;

    private File mPhotoFile;

    private ImageButton mPhotoButton;

    private ImageView mPhotoView;

    private EditText mTitleField;

    private Button mDateButton;

    private Button mTimeButton;

    private CheckBox mSolvedCheckBox;

    private Button mSuspectButton;

    private Button mReportButton;

    private Button mCallButton;

    private int contactsId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setTitle(R.string.warn_dialog_title)
                        .setMessage(R.string.warn_dialog_message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getTime());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(fragmentManager, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        //隐式Intent
        final Intent pickContact = new Intent(
                Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                updatePhotoView(mPhotoView);
            }
        });
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                ImageFragment.setImage(mPhotoFile.getPath());
                new ImageFragment().show(manager, "ImageFragment");
            }
        });

        //检查有无联系人应用
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareCompat.IntentBuilder sc = ShareCompat.IntentBuilder.from(getActivity());
                sc.setType("text/plain");
                sc.setText(getCrimeReport());
                sc.setSubject(getString(R.string.crime_report_subject));
                sc.createChooserIntent();
                sc.startChooser();
            }
        });

        mCallButton = (Button) v.findViewById(R.id.crime_call);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getActivity(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS,},
                            1);
                } else {
                    callSuspect();
                }
            }
        });

        return v;
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                break;
            case REQUEST_TIME:
                Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                mCrime.setTime(time);
                updateTime();
                break;
            case REQUEST_CONTACT:
                Uri contactUri = data.getData();
                String[] queryFields = new String[]{
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts._ID
                };

                Cursor c = getActivity().getContentResolver().query(
                        contactUri,
                        queryFields,
                        null,
                        null,
                        null);

                try {
                    if (c.getCount() == 0) {
                        return;
                    }

                    c.moveToFirst();
                    String suspect = c.getString(0);
                    contactsId = c.getInt(1);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                } finally {
                    c.close();
                }
                break;
            case REQUEST_PHOTO:
                updatePhotoView(mPhotoView);
                break;
            default:
        }
    }

    private void updateDate() {
        mDateButton.setText(DateFormatUtil.dateFormat().format(mCrime.getDate()));
    }

    private void updateTime() {
        mTimeButton.setText(DateFormatUtil.timeFormat().format(mCrime.getTime()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = DateFormatUtil.dateFormat().format(mCrime.getDate());
        String timeString = DateFormatUtil.timeFormat().format(mCrime.getTime());

        String suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, timeString, solvedString, suspect);

        return report;
    }

    private void callSuspect() {
        if (contactsId != -1) {
            String number = null;
            Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            String[] fields = {ContactsContract.CommonDataKinds.Phone.NUMBER};
            String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
            String[] whereArgs = {Integer.toString(contactsId)};

            Cursor cursor = getActivity().getContentResolver().query(
                    contentUri, fields, whereClause, whereArgs, null
            );

            try {
                if (cursor.getCount() == 0) {
                    return;
                }

                cursor.moveToFirst();
                number = cursor.getString(0);
            } finally {
                cursor.close();
            }

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        }
    }

    private void updatePhotoView(ImageView container) {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap;
            if(container == null)
            {
                bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            }
            else
            {
                bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), container);
            }

            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }
}
