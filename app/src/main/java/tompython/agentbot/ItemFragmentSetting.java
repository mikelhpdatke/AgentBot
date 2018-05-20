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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import es.dmoral.toasty.Toasty;

public class ItemFragmentSetting extends Fragment {
    static String ip_server = null;
    Context context;
    Activity activity;
    static int foundIP = 0;
    static MongoClient mongo ;
    static MongoDatabase database ;
    static MongoCollection<Document> collection ;
    public static ItemFragmentSetting newInstance() {
        ItemFragmentSetting fragment = new ItemFragmentSetting();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = (View) inflater.inflate(R.layout.fragment_item_setting, container, false);
        final EditText editText = (EditText) view.findViewById(R.id.input_server);


        Button button = (Button) view.findViewById(R.id.setting_button_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Debug View on Fragment", "What Heppenennnn");
//                Log.e("Debug View on Fragment", v.getTransitionName());
                ip_server = editText.getText().toString();
                mongo = new MongoClient(ip_server, 27017);
                database = mongo.getDatabase("DNSParser");
                collection = database.getCollection("Collection_FoundIP");
                Log.e("DEBUG",ip_server);
                Log.e("DEBUG",String.valueOf(collection.find()));
                Toasty.success(context, "Success! Your IP Server is " + ip_server, Toast.LENGTH_SHORT, true).show();

                //FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                //transaction.replace(R.id.frame_layout, ItemFragmentHome.newInstance());
                //transaction.commit();
            }
        });

        return view;
    }
}
