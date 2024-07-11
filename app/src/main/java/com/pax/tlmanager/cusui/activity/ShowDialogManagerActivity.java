package com.pax.tlmanager.cusui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.pax.customui.widget.PaxTextView;
import com.pax.tlmanager.R;
import com.pax.tlmanager.cusui.base.BaseActivity;
import com.pax.tlmanager.cusui.base.BaseViewHolder;
import com.pax.tlmanager.cusui.base.ManagerStatusImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Justin.Z on 2019-12-18
 */
public class ShowDialogManagerActivity extends BaseActivity implements View.OnClickListener, SelectOptionsHelper.ISelectOptionListener {

    RecyclerView mRecyclerView;
    Button confirmBtn;
    private PaxTextView tvPrompt;
    private RecyclerView.Adapter<BaseViewHolder<String>> mAdapter;
    private int selected = -1;
    private List<String> selectOption = new ArrayList<>();

    private SelectOptionsHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_options);

        tvPrompt = findViewById(R.id.prompt_select);
        tvPrompt.setCenterAlign(true);
        tvPrompt.setTextSize((int) getResources().getDimension(R.dimen.font_size_prompt));
        tvPrompt.setTextColor(getResources().getColor(R.color.primary_text_light));
        mRecyclerView = (RecyclerView) findViewById(R.id.option_select);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(this);
        confirmBtn.setEnabled(false);

        helper = new SelectOptionsHelper(this, new ManagerStatusImpl(this) {
            @Override
            public void onReset() {
                ShowDialogManagerActivity.super.finish();
            }

            @Override
            public void onClearMsg() {
                tvPrompt.setText(null);
            }

            @Override
            public void onAccepted() {
                ShowDialogManagerActivity.super.finish();
            }
        });
        helper.start(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("onNewIntent", intent.getAction());
        setIntent(intent);
        helper.start(this, intent);
    }

    @Override
    public void onClick(View view) {
        if (selected != -1) {
            helper.sendNext(selected);
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
    public void finish() {
        if (helper != null && helper.isNoBlocking()) {
            return;
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        helper.stop();
        super.onDestroy();
    }


    @Override
    public void onShowMessage(@Nullable String transName, @Nullable String message) {
        //Show message to your TextView.
    }

    @Override
    public void showDetail(String msg) {
        super.showDetail(msg);
        confirmBtn.setVisibility(helper.isNoBlocking() ? View.GONE : View.VISIBLE);
        tvPrompt.setText(helper.getTitle());
    }

    @Override
    public void onShowOptions(@NonNull String[] options) {
        selectOption = Arrays.asList(options);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerView.Adapter<BaseViewHolder<String>>() {
            @NonNull
            @Override
            public BaseViewHolder<String> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowDialogManagerActivity.OptionModelViewHolder(LayoutInflater.from(ShowDialogManagerActivity.this).inflate(R.layout.item_mode_grid, parent, false));
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