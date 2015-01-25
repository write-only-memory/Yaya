package ca.currybox.yaya;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by Kanchana on 1/25/2015.
 */
public class customNames {

    public void write (List<Anime> shows, Context ctx)
    {
        try
        {
            FileOutputStream fos = ctx.openFileOutput("custom-names.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(shows);
            oos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Anime> getCustomList (Context ctx)
    {
        List<Anime> customList;
        try
        {
            FileInputStream fis = ctx.openFileInput("custom-names.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            customList = (List<Anime>) ois.readObject();
            ois.close();
        }
        catch (Exception e)
        {
            customList = null;
            e.printStackTrace();
        }
        Log.i("reader", "done");
        return customList;

    }
}
