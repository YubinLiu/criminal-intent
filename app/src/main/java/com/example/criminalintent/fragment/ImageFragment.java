package com.example.criminalintent.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.criminalintent.R;
import com.example.criminalintent.util.PictureUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends DialogFragment {

    private static String mImagePath;

    private ImageView mBiggerImage;

    public static void setImage(String imagePath) {
        mImagePath = imagePath;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        mBiggerImage = (ImageView) view.findViewById(R.id.bigger_imager);
        Bitmap bitmap = PictureUtils.getScaledBitmap(mImagePath, getActivity());
        mBiggerImage.setImageBitmap(bitmap);
        return view;
    }

}
