package com.example.diplomaapp.dataClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomaapp.DevicesActivity;
import com.example.diplomaapp.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Devices> mDataset;
    private Context context;
    private MqttAndroidClient mqttAndroidClient;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textNameCard, textDataCard;
        public Switch switchButton;
        public ImageView imageView;
        public View view;

        public ViewHolder(View v) {
            super(v);
            textNameCard = v.findViewById(R.id.textNameCard);
            textDataCard = v.findViewById(R.id.textDataCard);
            switchButton = v.findViewById(R.id.switchBtn);
            imageView = v.findViewById(R.id.imageView);
        }

        public void SetDetails(Devices device){
            if(device.getFriendlyName() != null)
                textNameCard.setText(device.getFriendlyName() + "\n(" + device.getDeviceId() + ")");
            else textDataCard.setText(device.getType());
            if(device.getDiodeChannel() == null){
                switchButton.setVisibility(View.INVISIBLE);
            }
            else {
                if (device.getLastAcceptedData() != null) {

                    String[] keyValuePairs = device.getLastAcceptedData().split("\n");
                    StringBuilder newDataStringBuilder = new StringBuilder();
                    for (String pair : keyValuePairs) {
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();

                            if (!key.equals("state_" + device.getDiodeChannel())) {
                                newDataStringBuilder.append(pair).append("\n");
                            }
                        }
                    }

                    textDataCard.setText(device.getType() + "\n\n" + newDataStringBuilder.toString());

                    for (String pair : keyValuePairs) {
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();

                            if (key.equals("state_" + device.getDiodeChannel())) {
                                switchButton.setChecked(!value.equals("OFF"));
                                break;
                            }
                        }
                    }
                }
            }

            if (device.getImgPath() != null){
                if (!device.getImgPath().isEmpty()) {
                    try {
                        File file = new File(device.getImgPath());
                        if (file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public View getView() {
            return view;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, ArrayList<Devices> myDataset, MqttAndroidClient mqttAndroidClient) {
        this.mDataset = myDataset;
        this.context = context;
        this.mqttAndroidClient = mqttAndroidClient;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Devices device = mDataset.get(position);
        holder.SetDetails(device);

        holder.switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MqttMessage mqttMessage = new MqttMessage();
            if (isChecked) {
                mqttMessage.setPayload("On".getBytes());
                Log.i("button", "On " + holder.getAdapterPosition());
            } else {
                Log.i("button", "Off " + holder.getAdapterPosition());
                mqttMessage.setPayload("Off".getBytes());
            }
            try {
                Log.i("MQTT", device.getMqttPrefix());
                mqttAndroidClient.publish(device.getMqttPrefix() + "/" + device.getDeviceId() + "/" + device.getDiodeChannel() + "/set", mqttMessage);
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
