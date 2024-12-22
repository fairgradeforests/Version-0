package com.MyWorkingApp.C5V;

/**
 * Created by softoz on 12/8/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;

public class C5VAdapter extends ArrayAdapter<C5VRecord> {
    private static Activity myActivity = null;
    private static boolean showTitle = true;
    public static ArrayList<Integer> selectedPositions = new ArrayList<Integer>();

    public C5VAdapter(Context context, ArrayList<C5VRecord> values, Activity activity) {
        super(context, -1, values);
        myActivity = activity;
    }

    public void switchTitle() {
        showTitle = !showTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        C5VRecord rec = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);

        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox);
        cb.setEnabled(true);
        cb.setTag(position);
        boolean b = selectedPositions.contains(new Integer(position));
        cb.setSelected(b);
        if (b) {
            int i = 0;
        }
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (v.isSelected()) {
                    selectedPositions.remove(new Integer(position));
                } else {
                    selectedPositions.add(new Integer(position));

                }
                //notify that the model changed
                // notifyDataSetChanged();

            }
        });
        final TextView timeView = (TextView) convertView.findViewById(R.id.timeView);
        String s = new java.text.SimpleDateFormat("kk:mm:ss").format(rec.getMyRecordDate());
        if (showTitle) {
            s = rec.getMyRecordTitle().substring(0, Math.min(12, rec.getMyRecordTitle().length()));
        } else {
            s = new java.text.SimpleDateFormat("kk:mm:ss").format(rec.getMyRecordDate());
        }
        timeView.setText(s);
        timeView.setTag(position);
        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                showDate((C5VRecord) getItem(position));
            }
        });
        timeView.setBackgroundColor(Color.parseColor("#D2FFAF"));

        final TextView dateView = (TextView) convertView.findViewById(R.id.dateView);
        s = new java.text.SimpleDateFormat("dd/MM/yy").format(rec.getMyRecordDate());
        dateView.setText(s);
        dateView.setTag(position);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                showDate((C5VRecord) getItem(position));
            }
        });

        final ImageButton imageText = (ImageButton) convertView.findViewById(R.id.imageButtonText);
        imageText.setEnabled(true);
        imageText.setTag(position);
        imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = (int) v.getTag();
                showText((C5VRecord) getItem(position),imageText);
            }

        });


        ImageButton imageAudio = (ImageButton) convertView.findViewById(R.id.imageButtonAudio);
        if (rec.getMyRecordAudio().length() > 0) {
            imageAudio.setTag(position);
            imageAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    C5VAdapter.playAudio((C5VRecord) getItem(position));
                }
            });
        } else
            ((View) imageAudio).setVisibility(View.GONE);

        ImageButton imagePen = (ImageButton) convertView.findViewById(R.id.imageButtonPen);
        if (rec.getMyRecordPen().length() > 0) {
            imagePen.setTag(position);
        } else
            ((View) imagePen).setVisibility(View.GONE);

        ImageButton imagePhoto = (ImageButton) convertView.findViewById(R.id.imageButtonPhoto);
        if (rec.getMyRecordPhoto().length() > 0) {
            imagePhoto.setTag(position);
            imagePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    C5VAdapter.showPhoto((C5VRecord) getItem(position));
                }
            });
        } else
            imagePhoto.setVisibility(View.GONE);

        ImageButton imageVideo = (ImageButton) convertView.findViewById(R.id.imageButtonVideo);
        if (rec.getMyRecordVideo().length() > 0) {
            imageVideo.setEnabled(true);
            imageVideo.setTag(position);
            imageVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    C5VAdapter.playVideo((C5VRecord) getItem(position));
                }
            });
        } else
            imageVideo.setVisibility(View.GONE);

        ImageButton imageLocation = (ImageButton) convertView.findViewById(R.id.imageButtonGPS);
        if (rec.getMyRecordGps() != null) {
            imageLocation.setEnabled(true);
            imageLocation.setTag(position);
            imageLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    C5VAdapter.showLocation((C5VRecord) getItem(position));
                }
            });
        } else
            imageLocation.setVisibility(View.GONE);

        return convertView;
    }


    public void updateList() {
        notifyDataSetChanged();
    }

    static void showDate(C5VRecord rec) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(myActivity, DateFormat.getInstance().format(rec.getMyRecordDate()), duration);
        toast.show();
    }
/*
    static void showText(C5VRecord rec) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(myActivity, rec.getMyRecordText(), duration);
        toast.show();
    }
    */
static void showText(C5VRecord rec, View v) {
    showText(rec.getMyRecordText(),  v);
}
    static void showText(String toDisplay, View v) {
        LayoutInflater layoutInflater =
                (LayoutInflater) myActivity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //Update TextView in PopupWindow dynamically
        TextView textOut = (TextView) popupView.findViewById(R.id.textout);

        if (!toDisplay.equals("")) {
            textOut.setText(toDisplay);
        }

        Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
        btnDismiss.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(v, 50, -30);
    }

    static void showLocation(C5VRecord rec) {

        if (rec.getMyRecordGps() == null) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(myActivity, "No location", duration);
            toast.show();
            return;
        }
        String label =rec.getMyRecordTitle();
        String uriBegin = "geo:" + rec.getMyRecordGps().getLatitude() + "," + rec.getMyRecordGps().getLongitude();
        String query = rec.getMyRecordGps().getLatitude() + "," + rec.getMyRecordGps().getLongitude() + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        // Make the Intent explicit by setting the Google Maps package
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(myActivity.getPackageManager()) != null) {
            myActivity.startActivity(intent);
        }
    }

    static void playVideo(C5VRecord rec) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://"+rec.getMyRecordVideo()), "video/mp4");
        myActivity.startActivity(intent);
    }

    static void playAudio(C5VRecord rec) {
        MediaPlayer mediaPlayer = MediaPlayer.create(myActivity, Uri.parse(rec.getMyRecordAudio()));
        mediaPlayer.start();
    }

    static void showPhoto(C5VRecord rec) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://"+rec.getMyRecordPhoto()), "image/*");
        myActivity.startActivity(intent);
    }
}