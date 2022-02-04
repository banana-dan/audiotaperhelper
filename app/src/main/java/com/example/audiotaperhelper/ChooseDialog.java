package com.example.audiotaperhelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class ChooseDialog extends DialogFragment {
    AlertDialog.Builder builder;
    String[] a;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] selected = {null};
        builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.pick_toppings);

        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected

        builder.setSingleChoiceItems(a, -1, new DialogInterface.OnClickListener() // Item click listener
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Get the alert dialog selected item's text
//                String selectedItem = flowers.get(i);

                // Display the selected item's text on toast
            }
        });

        // Set the action buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK, so save the selectedItems results somewhere
                // or return them to the component that opened the dialog
                //...
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
//                   ...
            }
        });

        return builder.create();
    }

    public void setArrayRefVar(ArrayList<String> arrayRefVar) {
        this.a = new String[arrayRefVar.size()];
        for (int i = 0; i < arrayRefVar.size(); i++) {
            this.a[i] = arrayRefVar.get(i);
        }

    }
}
