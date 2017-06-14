package uk.co.danielnunn.mmapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Item> items;
    private MainActivity mainActivity;
    public ItemListAdapter(Context context, ArrayList<Item> arrayList, MainActivity activity) {
        super();
        mContext = context;
        items = arrayList;
        mainActivity = activity;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.result, null);
        if (view != null) {
            view = inflater.inflate(R.layout.result, null);
            TextView title = (TextView) view.findViewById(R.id.titleText);
            TextView brand = (TextView) view.findViewById(R.id.brandText);
            final TextView serial = (TextView) view.findViewById(R.id.serialText);
            Button viewOnline = (Button) view.findViewById(R.id.viewOnlineButton);
            final ImageView itemImage = (ImageView) view.findViewById(R.id.itemImage);
            final Item item = items.get(position);
            //Library which loads images into imageviews from urls
            if(item.getImageURL()!=null && !item.getImageURL().equals("")) {
                Picasso.with(mContext).load(item.getImageURL()).into(itemImage);
            }
            title.setText(item.getTitle());
            brand.setText(item.getBrand());
            serial.setText(item.getSerial());
            viewOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.openeBay(item.getSerial());
                }
            });

        }
        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

}