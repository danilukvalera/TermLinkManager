package com.pax.tlmanager.cusui.activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pax.customui.core.helper.SelectOptionsHelper;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.BaseViewHolder;
import com.pax.tlmanager.cusui.base.RespStatusImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Charles.S on 2017/5/5.
 */

public class SelectAIDActivity extends BaseActivity implements View.OnClickListener, SelectOptionsHelper.ISelectOptionListener {

    RecyclerView mRecyclerView;
    Button confirmBtn;
    private RecyclerView.Adapter<BaseViewHolder<String>> mAdapter;
    private int selected = -1;
    private List<String> selectOption = new ArrayList<>();

    private SelectOptionsHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_options);

        TextView tvPrompt = (TextView) findViewById(R.id.prompt_select);
        mRecyclerView = (RecyclerView) findViewById(R.id.option_select);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(this);
        confirmBtn.setEnabled(false);

        tvPrompt.setText("Please Select Application");
        helper = new SelectOptionsHelper(this, new RespStatusImpl(this));
        helper.start(this, getIntent());

    }

    @Override
    public void onClick(View view) {
        if (selected != -1) {
            helper.sendNext(selectOption, selected);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            helper.sendAbort();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        helper.stop();
        super.onDestroy();
    }


    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Display the message in your TextView.
    }

    @Override
    public void onShowOptions(@NonNull String[] options) {
        /*
        Sometimes some options do not need to be returned to TermLink,
        but only need to be processed by the client application, such as selectLanguage.
        The purpose is to report to the TermLink service whether this transaction has been processed,
        so you can give back to the TermLink service by selecting the String option.
        */
        selectOption = Arrays.asList(options);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerView.Adapter<BaseViewHolder<String>>() {
            @NonNull
            @Override
            public BaseViewHolder<String> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new OptionModelViewHolder(LayoutInflater.from(SelectAIDActivity.this).inflate(R.layout.item_mode_grid, parent, false));
            }

            @Override
            public void onBindViewHolder(@NonNull BaseViewHolder<String> holder, int position) {

                String viewData = selectOption.get(position);

                if (viewData == null)
                    return;
                holder.bindBaseView(viewData, position);
            }

            @Override
            public int getItemCount() {
                return selectOption.size();
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    class OptionModelViewHolder extends BaseViewHolder<String> {
        TextView textView;

        OptionModelViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView() {
            textView = (TextView) itemView.findViewById(R.id.mode_grid_tv);
        }

        @Override
        protected void bindView(View view, final String dataBean, final int pos) {
            textView.setText(dataBean);
            textView.setSelected(selected == pos);
            textView.setOnClickListener(v -> {
                selected = pos;
                confirmBtn.setEnabled(true);
                mAdapter.notifyDataSetChanged();
            });
        }
    }

}
