package com.example.diplomaapp.dataClasses;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class DeleteConfirmationDialog {

    public interface DeleteDialogListener {
        void onDeleteConfirmed();
    }

    public interface CancelDialogListener {
        void onCancel();
    }

    public static void show(Context context, String message, final DeleteDialogListener deleteListener,
                            final CancelDialogListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (deleteListener != null) {
                            deleteListener.onDeleteConfirmed();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (cancelListener != null) {
                            cancelListener.onCancel();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

