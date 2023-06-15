package com.mpi.exam_sample.utility;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.mpi.exam_sample.R;

import static android.view.View.GONE;

public class CustomDialogBuilder {
    public static AlertDialog singleButtonDialogBox(final Context mContext,
                                                    final String title, final String message,
                                                    final String button, final Runnable runEvent) {

        LayoutInflater factory = LayoutInflater.from(mContext);
        final View alertDialogView = factory.inflate(R.layout.custom_alert_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setView(alertDialogView);
        TextView ttlTxt = alertDialogView.findViewById(R.id.custom_dialog_title_one_button);
        ttlTxt.setText(title);
        TextView msgTxt = alertDialogView.findViewById(R.id.custom_dialog_message_one_button);
        msgTxt.setText(message);
        Button btn = (Button) alertDialogView.findViewById(R.id.dialog_custom_close_button);
        btn.setText(button);

        if (title.equals("")) {
            ttlTxt.setVisibility(GONE);
        }
        if (message.equals("")) {
            msgTxt.setVisibility(GONE);
        }

        alertDialogView.findViewById(R.id.dialog_custom_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runEvent != null) {
                    runEvent.run();
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        return alertDialog;
    }

    public static void showAlertDialog(final AlertDialog alertDialog) {
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }
    }
}
