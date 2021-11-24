package com.nisaefendioglu.translate;

import android.content.Context;
import android.view.LayoutInflater;

import com.nisaefendioglu.translate.databinding.LayoutProgressBarBinding;
import androidx.appcompat.app.AlertDialog;

public class DialogUtil {
    private static AlertDialog mLoader;

    static AlertDialog showLoader(Context context, Boolean cancelable) {

        LayoutProgressBarBinding binding = LayoutProgressBarBinding.inflate(LayoutInflater.from(context), null, false);

        hideLoader();
        mLoader = new AlertDialog.Builder(context, R.style.TransparentDialog)
                .setView(binding.getRoot())
                .setCancelable(cancelable)
                .create();

        mLoader.show();

        return mLoader;
    }

    static void hideLoader() {
        try {
            mLoader.dismiss();
        } catch (Exception ignored){
        }
    }
}
