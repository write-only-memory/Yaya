package ca.currybox.yaya;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by write_only_memory on 10/3/2014.
 */
public class ListViewAdapter extends BaseAdapter {

    //Declare Variable
    Context context;
    LayoutInflater inflater;

    ArrayList<HashMap<String, String>> data;
    private List<Anime> animeList = null;
    private ArrayList<Anime> arraylist;

    public ListViewAdapter(Context context, List<Anime> animeList) {
        this.context = context;
        this.animeList = animeList;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Anime>();
        this.arraylist.addAll(animeList);
    }

    public class ViewHolder {
        TextView title;
        TextView episodes;
        TextView watched;
        TextView updated;
        TextView status;
    }

    @Override
    public int getCount() {
        return animeList.size();
    }

    @Override
    public Object getItem(int position) {
        return animeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        holder = new ViewHolder();
        view = inflater.inflate(R.layout.listview_item, null);
        //Locate the TextView in listview_item.xml
        holder.title = (TextView) view.findViewById(R.id.list_title);
        holder.episodes = (TextView) view.findViewById(R.id.list_episodes);
        holder.watched = (TextView) view.findViewById(R.id.watched);
        holder.status = (TextView) view.findViewById(R.id.list_status);
        holder.updated = (TextView) view.findViewById(R.id.updated);
        /**
         if (view == null)
         {
         holder = new ViewHolder();
         view = inflater.inflate(R.layout.listview_item, null);
         //Locate the TextView in listview_item.xml
         holder.title = (TextView) view.findViewById(R.id.list_title);
         holder.episodes = (TextView) view.findViewById(R.id.list_episodes);
         holder.watched = (TextView) view.findViewById(R.id.watched);
         holder.status = (TextView) view.findViewById(R.id.list_status);
         holder.updated = (TextView) view.findViewById(R.id.updated);
         Log.i("VIEWHOLDER","in null: " + Thread.currentThread());
         } else {
         holder = (ViewHolder) view.getTag();
         Log.i("VIEWHOLDER","in else: " + Thread.currentThread());
         }
         */
        //Set the results into TextViews
        //Use String.valueOf() to convert int return types to string
        holder.title.setText(animeList.get(position).getTitle());
        holder.episodes.setText(String.valueOf(animeList.get(position).getWatched()) + "/" + String.valueOf(animeList.get(position).getEpisodes()));
        holder.status.setText(String.valueOf(animeList.get(position).getStatus()));
        String date = new SimpleDateFormat("MMM dd yyyy 'at' KK:mm:ss a").format(new Date(animeList.get(position).getUpdated() * 1000L));
        holder.updated.setText(date);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //Send single item click data to SingleItemView class
                Intent intent = new Intent(context, SingleItemView.class);
                //Pass all data title
                intent.putExtra("title", (animeList.get(position).getTitle()));
                //Pass all data episodes
                intent.putExtra("episodes", (animeList.get(position).getEpisodes()));
                //Pass all data watched
                intent.putExtra("watched", (animeList.get(position).getWatched()));
                //Pass all data status
                intent.putExtra("status", (animeList.get(position).getStatus()));
                //Pass all data updated
                intent.putExtra("updated", (animeList.get(position).getUpdated()));

                //Start activity
                context.startActivity(intent);


            }
        });
        return view;
    }

    //Filter class
    public void filter(int type) {
        //String text = charText.toLowerCase();
        animeList.clear();
        for (Anime an : arraylist) {
            if (an.getStatus() == type) {
                animeList.add(an);
            }
        }

        notifyDataSetChanged();
    }

    public void sortByUpdated() {
        Comparator<Anime> comparator = new Comparator<Anime>() {
            @Override
            public int compare(Anime anime, Anime anime2) {
                return (anime.getUpdated() > anime2.getUpdated() ? -1 : (anime.getUpdated() == anime2.getUpdated() ? 0 : 1)); //sorts by last updated first
            }
        };
        Collections.sort(animeList, comparator);
        notifyDataSetChanged();
    }


}
