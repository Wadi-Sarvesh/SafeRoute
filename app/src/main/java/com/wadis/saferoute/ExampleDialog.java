package com.wadis.saferoute;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ExampleDialog extends AppCompatDialogFragment {
    private EditText editLatitude;
    private EditText editLongitude;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Report Location of Infected Person")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        if(editLatitude.getText().toString().isEmpty() || editLatitude.getText().toString().isEmpty() )
                        {
                            Log.d("Check_Class","Entering if");
                            listener.applyCordinates(21.0989835, 79.0668006);
                            }





                        else
                        {
                            Log.d("Check_Class","Entering else");
                            Double latitude = Double.valueOf(editLatitude.getText().toString());
                            Double longitude = Double.valueOf(editLongitude.getText().toString());
                            listener.applyCordinates(latitude, longitude);}
                        }


                });
        editLatitude = view.findViewById(R.id.edit_lat);
        editLongitude = view.findViewById(R.id.edit_lng);
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }
    public interface ExampleDialogListener {
        void applyCordinates(Double latitude, Double longitude);
    }
}