package com.sms.partyview.fragments;

import com.sms.partyview.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SignOutDialogFragment extends DialogFragment {

    // Define the listener of the interface type
    // listener is the activity itself
    private SignOutDialogListener mListener;

    public SignOutDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setMessage(R.string.dialog_sign_out)
                .setPositiveButton(R.string.alert_sign_out, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        mListener.onDialogNegativeClick();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof SignOutDialogListener) {
            mListener = (SignOutDialogListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement SignOutDialogFragment.SignOutDialogListener");
        }
    }

    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it.
         * */
    public interface SignOutDialogListener {
        public void onDialogPositiveClick();
        public void onDialogNegativeClick();
    }

}
