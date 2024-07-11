package com.pax.tlmanager.cusui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pax.customui.core.api.IRespStatus;
import com.pax.customui.core.helper.ShowMsgHelper;
import com.pax.customui.widget.PaxTextView;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class ShowMsgActivity extends BaseActivity implements ShowMsgHelper.IShowMsgListener {
    private static final String TAG = "ShowMsgActivity";
    private static String initTopDown;
    private static ArrayList<MsgInfoWrapper> cachedMsgList = new ArrayList<>();
    private TextView tvTitle;
    private PaxTextView tvMsg1;
    private PaxTextView tvMsg2;
    private TextView tvTax;
    private TextView tvTotal;
    private RecyclerView rvList;
    private ShowMsgHelper helper;
    private MsgListAdapter msgListAdapter;
    private List<MsgInfoWrapper> showMsgList = new ArrayList<>();

    private static void addItemList(List<MsgInfoWrapper> wrapperList) {
        if (isTopDown()) {
            Collections.reverse(wrapperList);
            cachedMsgList.addAll(0, wrapperList);
        } else {
            cachedMsgList.addAll(wrapperList);
        }
    }

    private static boolean isTopDown() {
        return "Y".equals(initTopDown);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);
        tvTitle = findViewById(R.id.tv_title);
        tvMsg1 = findViewById(R.id.tv_msg1);
        tvMsg2 = findViewById(R.id.tv_msg2);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        rvList = findViewById(R.id.rv_list);
        tvMsg1.setTextSize((int) getResources().getDimension(R.dimen.font_size_prompt));
        tvMsg2.setTextSize((int) getResources().getDimension(R.dimen.font_size_value));

        showMsgList = cachedMsgList;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(linearLayoutManager);
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        msgListAdapter = new MsgListAdapter(this, showMsgList);
        rvList.setAdapter(msgListAdapter);
        helper = new ShowMsgHelper(this, new IRespStatus() {
            @Override
            public void onAccepted() {
                //Do nothing when forward, ShowMessage will replace the idle screen before Reset command.
            }

            @Override
            public void onDeclined(long code, @Nullable String message) {
                //This command can not return the Decline.
            }

            @Override
            public void onMessage(@Nullable String message) {
                //It has no message for ShowMessage command.
            }

            @Override
            public void onReset() {
                //When execute the Reset command, please finish the showMessage screen.
                cachedMsgList.clear();
                finish();
            }

            @Override
            public void onClearMsg() {
                //When execute the ClearMessage command, please clear the showMessage screen.
                Log.d(TAG, "onClearMsg: ");
                clearMsg();
            }
        });
        helper.start(this, getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.stop();
    }

    @Override
    public void onShowTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void onShowTotalTaxLine(String tax, String total) {
        tvTax.setText(tax);
        tvTotal.setText(total);
    }

    @Override
    public void onShowMsg(String topDown, int lineItemAction, ArrayList<String> indexList, String msg1, String msg2) {
        if (TextUtils.isEmpty(initTopDown)) {
            if (!TextUtils.isEmpty(topDown)) {
                initTopDown = topDown;
            } else {
                initTopDown = "Y";
            }
        }
        initMsgList(lineItemAction, indexList, msg1, msg2);
        tvMsg1.setText(msg1);
        tvMsg2.setText(msg2);
        showMsgList = cachedMsgList;
        msgListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShowImg(String imgName, String imageDesc) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(imgName)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            try (InputStream is = cr.openInputStream(Uri.parse(imgName))) {
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            //Show the bitmap to your screen, please set it by yourself.
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        helper.stop();
        helper.start(this, intent);
    }

    public void clearMsg() {
        runOnUiThread(() -> {
            cachedMsgList.clear();
            showMsgList.clear();
            msgListAdapter.notifyDataSetChanged();
            initTopDown = "";
            tvMsg1.setText("");
            tvMsg2.setText("");
            tvTitle.setText("");
            tvTax.setText("");
            tvTotal.setText("");
        });

    }

    private void initMsgList(int itemAction, ArrayList<String> itemIndex, String msg1, String msg2) {
        MsgInfo msgInfo = new MsgInfo();
        msgInfo.setMsg1(msg1);
        msgInfo.setMsg2(msg2);
        boolean indexEmpty = itemIndex.isEmpty();
        switch (itemAction) {
            case 0:
                addItemList(buildWrapperList(msgInfo, itemIndex, indexEmpty));
                break;
            case 1:
                ArrayList<MsgInfoWrapper> wrapperList = buildWrapperList(msgInfo, itemIndex, indexEmpty);
                for (MsgInfoWrapper wrapper : wrapperList) {
                    for (int i = 0; i < cachedMsgList.size(); i++) {
                        if (TextUtils.equals(wrapper.getIndex(), cachedMsgList.get(i).getIndex())) {
                            cachedMsgList.set(i, wrapper);
                        }
                    }
                }
                break;
            case 2:
                if (indexEmpty) {
                    deleteMsgByIndex(null);
                } else {
                    for (String index : itemIndex) {
                        deleteMsgByIndex(index);
                    }
                }
                break;
        }
    }

    private void deleteMsgByIndex(String index) {
        if (cachedMsgList != null) {
            ListIterator<MsgInfoWrapper> iter = cachedMsgList.listIterator();
            while (iter.hasNext()) {
                MsgInfoWrapper wrapper = iter.next();
                if (TextUtils.equals(wrapper.getIndex(), index))
                    iter.remove();
            }
        }
    }

    private ArrayList<MsgInfoWrapper> buildWrapperList(MsgInfo msgInfo, List<String> itemIndex, boolean indexEmpty) {
        ArrayList<MsgInfoWrapper> wrappers = new ArrayList<>();
        int size = indexEmpty ? 1 : itemIndex.size();
        for (int i = 0; i < size; i++) {
            wrappers.add(new MsgInfoWrapper(indexEmpty ? null : itemIndex.get(i), msgInfo));
        }
        return wrappers;
    }

    private List<MsgInfo> reverseList(List<MsgInfo> msgList) {
        List<MsgInfo> tempList = new ArrayList<>();
        for (int i = msgList.size() - 1; i >= 0; i--) {
            tempList.add(msgList.get(i));
        }
        return tempList;
    }

    class MsgListAdapter extends RecyclerView.Adapter<MsgListAdapter.MsgViewHolder> {

        private final LayoutInflater layoutInflater;

        private List<MsgInfoWrapper> msgList;

        private Context context;

        private LinearLayout.LayoutParams lp;

        MsgListAdapter(Context context, List<MsgInfoWrapper> msgList) {
            this.context = context;
            this.msgList = msgList;
            layoutInflater = LayoutInflater.from(context);
            lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        }

        @Override
        public int getItemCount() {
            if (msgList == null) {
                return 0;
            } else {
                return msgList.size();
            }
        }

        @Override
        public void onBindViewHolder(MsgViewHolder holder, int position) {
            MsgInfo msgInfo = msgList.get(position).getMsgInfo();
            holder.msgLayout.setPadding(0, 5, 0, 5);

            String msg1 = msgInfo.getMsg1();
            String msg2 = msgInfo.getMsg2();

            if (TextUtils.isEmpty(msg1) && TextUtils.isEmpty(msg2)) {
                holder.tvMessage1.setText("");
                holder.tvMessage2.setText("");
            } else {
                holder.tvMessage1.setText(msg1);
                holder.tvMessage2.setText(msg2);
            }
        }

        @NonNull
        @Override
        public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MsgViewHolder(layoutInflater.inflate(R.layout.item_msg_list, parent, false));
        }

        class MsgViewHolder extends RecyclerView.ViewHolder {
            PaxTextView tvMessage1;
            PaxTextView tvMessage2;
            LinearLayout msgLayout;

            MsgViewHolder(View view) {
                super(view);
                tvMessage1 = view.findViewById(R.id.tv_msg_1);
                tvMessage2 = view.findViewById(R.id.tv_msg_2);
                msgLayout = view.findViewById(R.id.ll_msg);
                tvMessage1.setTextColor(getResources().getColor(R.color.primary_text_light));
                tvMessage2.setTextColor(getResources().getColor(R.color.secondary_text_light));
            }
        }
    }

    public class MsgInfoWrapper {
        private String index;
        private MsgInfo msgInfo;

        public MsgInfoWrapper() {
        }

        public MsgInfoWrapper(String index, MsgInfo msgInfo) {
            this.index = index;
            this.msgInfo = msgInfo;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public MsgInfo getMsgInfo() {
            return msgInfo;
        }

        public void setMsgInfo(MsgInfo msgInfo) {
            this.msgInfo = msgInfo;
        }
    }

    public class MsgInfo implements Serializable {

        private String msg1;
        private String msg2;

        public String getMsg1() {
            return msg1;
        }

        public void setMsg1(String msg1) {
            this.msg1 = msg1;
        }

        public String getMsg2() {
            return msg2;
        }

        public void setMsg2(String msg2) {
            this.msg2 = msg2;
        }
    }
}
