package id.kiosku.xbug.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.kiosku.xbug.XBug;
import id.kiosku.xbug.app.R;
import id.kiosku.xbug.app.models.DebugModel;

/**
 * Created by Dina on 12/16/2016.
 */

public class ListViewAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<DebugModel> list;

    @SuppressWarnings("unchecked")
    public ListViewAdapter(Context context, ArrayList<DebugModel> list) {
        super(context, R.layout.list_debug_info,list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {

        @BindView(R.id.packagename)
        TextView packageName;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.message)
        TextView message;
        @BindView(R.id.tanggal)
        TextView tanggal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_debug_info, null);
            ViewHolder holder = new ViewHolder();
            ButterKnife.bind(holder, view);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.packageName.setText(list.get(position).packageName);
        holder.title.setText(list.get(position).title);
        holder.message.setText(list.get(position).message);
        holder.tanggal.setText(list.get(position).tanggal);

        switch (list.get(position).type){
            case XBug.TYPE_VERBOSE:{
                holder.type.setText("V");
            }break;
            case XBug.TYPE_DEBUG:{
                holder.type.setText("D");
            }break;
            case XBug.TYPE_INFO:{
                holder.type.setText("I");
            }break;
            case XBug.TYPE_WARNING:{
                holder.type.setText("W");
            }break;
            case XBug.TYPE_ERROR:{
                holder.type.setText("E");
            }break;
            case XBug.TYPE_ASSERT:{
                holder.type.setText("A");
            }break;
            default: holder.type.setText("X");
        }

        return view;
    }
}



