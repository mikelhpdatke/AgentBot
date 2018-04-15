/*
 * Copyright (c) 2017. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package tompython.agentbot;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import eu.chainfire.libsuperuser.Shell;

public class ItemTwoFragment extends Fragment {
    Context context;
    Activity activity;
    TextView textView;
    Adapter adapter;
    static LayoutInflater mInflater;
    static List<String> list = new ArrayList<String>();
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    static FetchData task;
    static boolean is_run = false;

    public static ItemTwoFragment newInstance() {
        ItemTwoFragment fragment = new ItemTwoFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = (View) inflater.inflate(R.layout.fragment_item_two, container, false);

        final Button bt = (Button) view.findViewById(R.id.button_start);
        final Button bt_stop = (Button) view.findViewById(R.id.button_stop);
        Log.e("Debug_Tom","Initialising");
        Log.e("Debug_Tom","Requesting root permissions..");
        mInflater = inflater;

        adapter = new Adapter(getActivity());
        ////////////////
        //

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCapture(view);
            }
        });

        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAndExitActivity(view);
            }
        });
        //
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Boolean isRootAvailable = Shell.SU.available();
                Boolean processExists = false;
                String pid = null;
                if(isRootAvailable) {
                    List<String> out = Shell.SH.run("ps | grep tcpdump.bin");
                    Log.e("Threaddddddd", Integer.toString(out.size()));
                    if(out.size() == 1) {
                        processExists = true;
                        pid = (out.get(0).split("\\s+"))[1];
                    }
                    else if(out.size() == 0) {
                        if (loadTcpdumpFromAssets() != 0)
                            throw new RuntimeException("Copying tcpdump binary failed.");
                    }
                    else
                        throw new RuntimeException("Searching for running process returned unexpected result.");

                    /// busybox.bin

                    out = Shell.SH.run("ps | grep busybox.bin");
                    if(out.size() == 1) {
                        processExists = true;
                        pid = (out.get(0).split("\\s+"))[1];
                    }
                    else if(out.size() == 0) {
                        if (loadBusyboxFromAssets() != 0)
                            throw new RuntimeException("Copying busybox.bin binary failed.");
                    }
                    else
                        throw new RuntimeException("Searching for running process returned unexpected result.");
                }

                final Boolean processExistsFinal = processExists;
                final String pidFinal = pid;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isRootAvailable) {
                            ((TextView)view.findViewById(R.id.tv_status)).setText("Root permission denied or phone is not rooted!");
                            (view.findViewById(R.id.button_start)).setEnabled(false);
                        }
                        else {
                            if(processExistsFinal){
                                ((TextView)view.findViewById(R.id.tv_status)).setText("Tcpdump already running at pid: " + pidFinal );
                                bt.setText("Stop  Capture");
                                bt.setTag(1);
                            }
                            else {
                                ((TextView)view.findViewById(R.id.tv_status)).setText("Initialization Successful.");
                                bt.setTag(0);
                            }
                        }
                    }
                });

            }
        };
        new Thread(runnable).start();
        Log.e("WTF", "WTFFF");
        recyclerView = (RecyclerView) view.findViewById(R.id.rycycleView);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setList(list);
        recyclerView.setAdapter(adapter);
        return view;
    }




    public void  startCapture(View view) {

        //
        //String ip_server = "";
        //EditText editText = (EditText) findViewById(R.id.tv_ipsv);
        //ip_server = editText.getText().toString();
        //
        //hideKeyboard
        //hideKeyboardFrom(this,editText);
        //LogActivity.addString("StarttttttWTF");


        //
        Button bt = (Button)view.findViewById(R.id.button_start);
        bt.setEnabled(false);
        if((int)bt.getTag() == 1){
            //Using progress dialogue from main. See comment in: TcpdumpPacketCapture.stopTcpdumpCapture
            Log.e("Debug_Tom","Killing Tcpdump && Busybox process.");
            //LogActivity.addString(Calendar.getInstance().getTime().toString() + ":" +"Killing Tcpdump && Busybox process.");
            TcpdumpPacketCapture.stopTcpdumpCapture(getActivity());
            bt.setText("Start Capture");
            bt.setTag(0);
            ((TextView)view.findViewById(R.id.tv_status)).setText("Packet capture stopped");
        }
        else if ((int)bt.getTag() == 0){
            TcpdumpPacketCapture.initialiseCapture(getActivity(), ItemThreeFragment.ip_server);
            bt.setText("Stop  Capture");
            bt.setTag(1);
        }
        bt.setEnabled(true);

        Log.e("EditText", Calendar.getInstance().getTime().toString() + ":" + ItemThreeFragment.ip_server);
        String cmd_url = new StringBuffer().append("http://").append(ItemThreeFragment.ip_server).append(":9200").append("/android/_search").toString();
        Log.e("IP Server::", cmd_url);
        task = new FetchData(getActivity(), adapter, recyclerView, cmd_url);
        task.execute(cmd_url);


    }

    public void stopAndExitActivity(View v) {

        TcpdumpPacketCapture.stopTcpdumpCapture(getActivity());
        getActivity().finish();
    }

    private int loadTcpdumpFromAssets(){
        int retval = 0;
        // updating progress message from other thread causes exception.
        // progressbox.setMessage("Setting up data..");
        String rootDataPath = context.getApplicationInfo().dataDir + "/files";
        String filePath = rootDataPath + "/tcpdump.bin";
        File file = new File(filePath);
        AssetManager assetManager = context.getAssets();

        try{
            if (file.exists()) {
                Shell.SH.run("chmod 755 " + filePath);
                return retval;
            }
            new File(rootDataPath).mkdirs();
            retval = copyFileFromAsset(assetManager, "tcpdump.bin", filePath);
            // Mark the binary executable
            Shell.SH.run("chmod 755 " + filePath);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            retval = -1;
        }
        return retval;
    }

    private int loadBusyboxFromAssets(){
        int retval = 0;
        // updating progress message from other thread causes exception.
        // progressbox.setMessage("Setting up data..");
        String rootDataPath = context.getApplicationInfo().dataDir + "/files";
        String filePath = rootDataPath + "/busybox.bin";
        File file = new File(filePath);
        AssetManager assetManager = getActivity().getAssets();

        try{
            if (file.exists()) {
                Shell.SH.run("chmod 755 " + filePath);
                return retval;
            }
            new File(rootDataPath).mkdirs();
            retval = copyFileFromAsset(assetManager, "busybox.bin", filePath);
            // Mark the binary executable
            Shell.SH.run("chmod 755 " + filePath);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            retval = -1;
        }
        return retval;
    }

    private int copyFileFromAsset(AssetManager assetManager, String sourcePath, String destPath) {
        byte[] buff = new byte[1024];
        int len;
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(sourcePath);
            new File(destPath).createNewFile();
            out = new FileOutputStream(destPath);
            // write file
            while((len = in.read(buff)) != -1){
                out.write(buff, 0, len);
            }
            in.close();
            out.flush();
            out.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
            return -1;
        }
        return 0;
    }


}
