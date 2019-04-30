package com.fernando.ikigai;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RadioButton;

public class ModeDialogFragment extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        View modeDialogView =
                getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_mode, null);

        builder.setView(modeDialogView);

        builder.setTitle(R.string.title_mode_dialog);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        final RadioButton buttonManual = modeDialogView.findViewById(R.id.buttonManual);
        final RadioButton buttonAutomatic = modeDialogView.findViewById(R.id.buttonAutomatic);

        builder.setPositiveButton(R.string.button_set_mode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (buttonAutomatic.isChecked())
                    doodleView.setPaintMode("automatic");
                else if (buttonManual.isChecked())
                    doodleView.setPaintMode("manual");
                else
                    return;
            }
        });

        return builder.create();
    }

    private MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(
                R.id.doodleFragment);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
