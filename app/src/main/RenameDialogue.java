package com.course.mqttapptest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

public class RenameDialogue extends DialogFragment {

    public interface RenameDialogueListener{
        void applyName(String newName);
    }

    private EditText etNewName;
    public RenameDialogueListener listener;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_rename, null);
        etNewName = view.findViewById(R.id.etNewName);

        builder.setView(view)
                .setTitle("Enter new device name")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
          .setPositiveButton("ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                    String newName = etNewName.getText().toString();
                    if(!etNewName.equals("")) {
                        listener.applyName(newName);
                    }
              }
          });


       return builder.create();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (RenameDialogueListener) getTargetFragment();
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString() + "must implement RenameDialogueListener");
        }
    }


}
