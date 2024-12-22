package com.MyWorkingApp.C5V;

import android.app.DialogFragment;

/**
 * Created by softoz on 12/27/2015.
 */
        import android.app.Dialog;
import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
        import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;

public class C5VDetailsDialog extends DialogFragment {

    private C5VRecord myRecord;

    static C5VDetailsDialog newInstance(C5VRecord rec) {
        C5VDetailsDialog f = new C5VDetailsDialog();

        Bundle args = new Bundle();
        args.putSerializable("myRecord", rec);
        f.setArguments(args);

        return f;
    }

    public C5VDetailsDialog() {
        myRecord = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        myRecord = (C5VRecord) getArguments().getSerializable("myRecord");

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.row);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        LinearLayout myll = (LinearLayout) dialog.findViewById(R.id.rowLinearLayout);
        myll.setOrientation(LinearLayout.VERTICAL);
        final TextView textView = (TextView) dialog.findViewById(R.id.dateView);
        String s =  DateFormat.getDateTimeInstance().format(myRecord.getMyRecordDate());
        textView.setText(s);
        textView.setTag(myRecord);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                C5VRecord r = (C5VRecord) v.getTag();
                C5VAdapter.showDate(r);
            }
        });

        final ImageButton imageText = (ImageButton) dialog.findViewById(R.id.imageButtonText);
        imageText.setEnabled(true);
        imageText.setTag(myRecord);
        imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                C5VRecord r = (C5VRecord) v.getTag();
                C5VAdapter.showText(r,imageText);
            }

        });

        ImageButton imageAudio = (ImageButton) dialog.findViewById(R.id.imageButtonAudio);
        if (myRecord.getMyRecordAudio().length() > 0) {
            imageAudio.setTag(myRecord);
            imageAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    C5VRecord r = (C5VRecord) v.getTag();
                    C5VAdapter.playAudio((C5VRecord) r);
                }
            });
        } else
            ((View) imageAudio).setVisibility(View.GONE);

        ImageButton imagePen = (ImageButton) dialog.findViewById(R.id.imageButtonPen);
        if (myRecord.getMyRecordPen().length() > 0) {
            imagePen.setTag(myRecord);
        } else
            ((View) imagePen).setVisibility(View.GONE);

        ImageButton imagePhoto = (ImageButton) dialog.findViewById(R.id.imageButtonPhoto);
        if (myRecord.getMyRecordPhoto().length() > 0) {
            imagePhoto.setTag(myRecord);
            imagePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    C5VRecord r = (C5VRecord) v.getTag();
                    C5VAdapter.showPhoto(r);
                }
            });
        } else
            imagePhoto.setVisibility(View.GONE);

        ImageButton imageVideo = (ImageButton) dialog.findViewById(R.id.imageButtonVideo);
        if (myRecord.getMyRecordVideo().length() > 0) {
            imageVideo.setEnabled(true);
            imageVideo.setTag(myRecord);
            imageVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    C5VRecord r = (C5VRecord) v.getTag();
                    C5VAdapter.playVideo(r);
                }
            });
        } else
            imageVideo.setVisibility(View.GONE);

        ImageButton imageLocation = (ImageButton) dialog.findViewById(R.id.imageButtonGPS);
            imageLocation.setVisibility(View.GONE);

        return dialog;
    }
}