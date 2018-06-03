package in.blogspot.iamdhariot.friendlychat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Dhariot on 03-Jun-18.
 */

public class MessageAdapter extends ArrayAdapter<Message>  {
    public MessageAdapter(@NonNull Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message,parent,false);

        }
        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = (TextView)convertView.findViewById(R.id.messageTextView);
        TextView nameTextView= (TextView)convertView.findViewById(R.id.nameTextView);
        Message message = getItem(position);
        boolean isPhoto = message.getPhotoUrl()!=null;
        if(isPhoto){
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());
        }
        nameTextView.setText(message.getName());

        return convertView;
    }
}