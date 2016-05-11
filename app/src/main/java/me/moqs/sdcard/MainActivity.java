package me.moqs.sdcard;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import me.moqs.sdcard.modal.SDCard;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressBar mLoading;
    private List<SDCard> mSDCardList = new ArrayList<>();
    private SDCardAdapter mAdapter;

    private Handler mHandler = new Handler(new HandlerCallback());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        // 加载SDCard数据
        loadData();
    }

    private void initViews() {
        mLoading = (ProgressBar) findViewById(R.id.loading);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new SDCardAdapter(this, mSDCardList);
        recyclerView.setAdapter(mAdapter);
    }

    private class SDCardHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView name;
        public TextView size;

        public SDCardHolder(View itemView) {
            super(itemView);

            initViews(itemView);
        }

        private void initViews(View itemView) {
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            size = (TextView) itemView.findViewById(R.id.size);
        }

    }

    private class SDCardAdapter extends RecyclerView.Adapter<SDCardHolder> {

        private Context context;
        private List<SDCard> sdCardList;

        public SDCardAdapter(Context context, List<SDCard> sdCardList) {
            this.context = context;
            this.sdCardList = sdCardList;
        }

        @Override
        public SDCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.sdcard_item, parent,false);
            return new SDCardHolder(v);
        }

        @Override
        public void onBindViewHolder(SDCardHolder holder, int position) {
            SDCard sdCard = sdCardList.get(position);

            if (sdCard.isDirectory()) {
                holder.icon.setImageResource(R.drawable.img_dir);
            } else {
                holder.icon.setImageResource(R.drawable.img_file);
            }
            holder.name.setText(sdCard.getName());
            holder.size.setText(String.valueOf(sdCard.getSize()));
        }

        @Override
        public int getItemCount() {
            return sdCardList.size();
        }
    }

    private class HandlerCallback implements Handler.Callback {

        public static final int MSG_UPDATE = 1;

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    update(msg);
                    break;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        private void update(Message msg) {
            mLoading.setVisibility(View.GONE);

            List<SDCard> sdCards = (List<SDCard>) msg.obj;
            if (sdCards != null && sdCards.size() != 0) {
                mSDCardList.addAll(sdCards);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void loadData() {
        mLoading.setVisibility(View.VISIBLE);

        new Thread(new SDCardDataLoad()).start();
    }

    private class SDCardDataLoad implements Runnable {

        @Override
        public void run() {
            List<SDCard> sdCards = new ArrayList<>();

            File sdcard = Environment.getExternalStorageDirectory();
            File[] files = sdcard.listFiles();
            for (File f : files) {
                boolean isDir = f.isDirectory();

                SDCard card = new SDCard();
                card.setDirectory(isDir);
                card.setName(f.getName());
                card.setSize(computeSize(f));


                Log.i(TAG, card.toString() + " - " + f.length());

                sdCards.add(card);
            }

            Message msg = new Message();
            msg.what = HandlerCallback.MSG_UPDATE;
            msg.obj = sdCards;
            mHandler.sendMessage(msg);
        }

        /**
         * 计算文件大小
         * @param f file or directory
         * @return float
         */
        private String computeSize(File f) {
            DecimalFormat fmt = new DecimalFormat("#.##");
            if (f.isFile()) {
                long len = f.length();
                if (len < 1024) {
                    return fmt.format(len) + "Byte";
                } else if (len > 1024) {
                    return fmt.format(f.length() / 1024) + "KB";
                } else if (len > 1024 * 1024) {
                    return fmt.format(f.length() / 1024 / 1024) + "M";
                }
            }
            return "0M";
        }
    }
}
