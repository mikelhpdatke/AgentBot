package tompython.agentbot;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.PacketViewHolder>{
    private Context mContext;
    List<String> list;

    public Adapter(Context context){
        this.mContext = context;
    }

    public void setList(List< String > list){
        this.list = list;
    }

    @Override
    public PacketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("HAUUU", mContext.toString());
        View view = ItemTwoFragment.mInflater.inflate(R.layout.item, parent, false);
        Log.e("PacketView","DKMDKMDKDMKDMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");
        return new PacketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PacketViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PacketViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public PacketViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.packet);

        }
    }
}
