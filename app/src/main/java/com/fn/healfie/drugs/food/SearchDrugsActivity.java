package com.fn.healfie.drugs.food;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.fn.healfie.BR;
import com.fn.healfie.BaseActivity;
import com.fn.healfie.R;
import com.fn.healfie.adapter.DrugsScendInfoAdapter;
import com.fn.healfie.adapter.SearchDrugsListAdapter;
import com.fn.healfie.component.camera.CameraActivity;
import com.fn.healfie.connect.MyConnect;
import com.fn.healfie.consts.MyUrl;
import com.fn.healfie.consts.PrefeKey;
import com.fn.healfie.databinding.SearchDrugsActivityBinding;
import com.fn.healfie.interfaces.BaseOnClick;
import com.fn.healfie.interfaces.ConnectBack;
import com.fn.healfie.interfaces.ConnectLoginBack;
import com.fn.healfie.login.LoginActivity;
import com.fn.healfie.model.BaseBean;
import com.fn.healfie.model.CreateFoodBean;
import com.fn.healfie.model.DrugsInfoBean;
import com.fn.healfie.model.DrugsSearchBean;
import com.fn.healfie.model.DrugsSearchOneBean;
import com.fn.healfie.model.RegisterBean;
import com.fn.healfie.module.SearchModule;
import com.fn.healfie.utils.JsonUtil;
import com.fn.healfie.utils.PrefeUtil;
import com.fn.healfie.utils.StatusBarUtil;
import com.fn.healfie.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * from @zhaojian
 * content 創建食物
 */

public class SearchDrugsActivity extends BaseActivity implements BaseOnClick {

    Activity activity = this;
    SearchDrugsActivityBinding binding;
    SearchModule module;
    String path;
    SearchDrugsListAdapter adapter;
    ArrayList<DrugsSearchBean.ItemBean> list = new ArrayList<>();
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    DrugsSearchBean bean = JsonUtil.getBean(msg.obj.toString(), DrugsSearchBean.class);
                    if (bean.getResultCode().equals("200")&&bean.getItem().size()>0) {
                        binding.tvZjtj.setVisibility(View.VISIBLE);
                        binding.lvFood.setVisibility(View.VISIBLE);
                        binding.buttonSearch.setVisibility(View.GONE);
                        list.addAll(bean.getItem());
                    }else if(bean.getResultCode().equals("200")){
//                        binding.tvZjtj.setVis
                        binding.tvZjtj.setVisibility(View.GONE);
                        binding.lvFood.setVisibility(View.GONE);
                        binding.buttonSearch.setVisibility(View.VISIBLE);
                    } else if (bean.getResultCode().equals("-10010")) {
                        showDialog();
                        sendLogin(new ConnectLoginBack() {
                            @Override
                            public void success(String json, String header) {
                                hideDialog();
                                RegisterBean registerBean = JsonUtil.getBean(json, RegisterBean.class);
                                if (registerBean.getResultCode().equals("200")) {
                                    PrefeUtil.saveString(activity, PrefeKey.TOKEN, registerBean.getItem().getAuthorization());
                                    myHandler.sendEmptyMessage(2);

                                } else {
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void error(String json) {
                                hideDialog();
                                Intent intent = new Intent(activity, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        ToastUtil.errorToast(activity, bean.getResultCode());
                    }
                    break;
                case 2:
                    getFood();
                    break;
                case 3:
                    DrugsSearchOneBean beans = JsonUtil.getBean(msg.obj.toString(), DrugsSearchOneBean.class);
                    if (beans.getResultCode().equals("200")&&beans.getItem().size()>0) {
                        binding.tvZjtj.setVisibility(View.VISIBLE);
                        binding.lvFood.setVisibility(View.VISIBLE);
                        binding.buttonSearch.setVisibility(View.GONE);
                        list.clear();
                        for (int i = 0;i<beans.getItem().size();i++){
                            list.add(new DrugsSearchBean.ItemBean(beans.getItem().get(i).getTakeMode(),beans.getItem().get(i).getCnName()));
                        }
                        adapter.notifyDataSetChanged();
                    }else if(beans.getResultCode().equals("200")){
//                        binding.tvZjtj.setVis
                        binding.tvZjtj.setVisibility(View.GONE);
                        binding.lvFood.setVisibility(View.GONE);
                        binding.buttonSearch.setVisibility(View.VISIBLE);
                    } else if (beans.getResultCode().equals("-10010")) {
                        showDialog();
                        sendLogin(new ConnectLoginBack() {
                            @Override
                            public void success(String json, String header) {
                                hideDialog();
                                RegisterBean registerBean = JsonUtil.getBean(json, RegisterBean.class);
                                if (registerBean.getResultCode().equals("200")) {
                                    PrefeUtil.saveString(activity, PrefeKey.TOKEN, registerBean.getItem().getAuthorization());
                                    myHandler.sendEmptyMessage(4);

                                } else {
                                    Intent intent = new Intent(activity, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }

                            @Override
                            public void error(String json) {
                                hideDialog();
                                Intent intent = new Intent(activity, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        ToastUtil.errorToast(activity, beans.getResultCode());
                    }
                    break;
                case 4:
                    getData();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.StatusBarLightMode(this);
        binding = DataBindingUtil.setContentView(this, R.layout.search_drugs_activity);
        binding.setVariable(BR.click, this);
        module = new SearchModule();
        module.setName("");
        path = getIntent().getStringExtra(CameraActivity.CAMERA_PATH_VALUE1);
        binding.setSearch(module);
         adapter = new SearchDrugsListAdapter(this, list, new BaseOnClick() {
            @Override
            public void onSaveClick(int id) {

            }
        });

        binding.setAdapter(adapter);
        initData();
    }

    private void initData() {
        binding.etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    getData();
                }
                return false;
            }
        });
        getFood();
    }

    private void getData() {
        showDialog();
        MyConnect connect = new MyConnect();
        HashMap<String, String> map = new HashMap<>();
        map.put("authorization", PrefeUtil.getString(activity, PrefeKey.TOKEN, ""));
        map.put("name", binding.etInput.getText().toString());
        map.put("page", "1");
        map.put("limit", "20");
        connect.getData(MyUrl.DRUGSPAGE , map, new ConnectBack() {
            @Override
            public void success(String json) {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = json;
                myHandler.sendMessage(msg);
                hideDialog();
            }

            @Override
            public void error(String json) {
                Log.e("error: ", json);
                hideDialog();
                ToastUtil.errorToast(activity, "-1022");
            }
        });
    }

    private void getFood() {
        showDialog();
        MyConnect connect = new MyConnect();
        HashMap<String, String> map = new HashMap<>();
        map.put("authorization", PrefeUtil.getString(activity, PrefeKey.TOKEN, ""));

        connect.getData(MyUrl.DRUGS , map, new ConnectBack() {
            @Override
            public void success(String json) {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = json;
                myHandler.sendMessage(msg);
                hideDialog();
            }

            @Override
            public void error(String json) {
                Log.e("error: ", json);
                hideDialog();
                ToastUtil.errorToast(activity, "-1022");
            }
        });
    }

    /**
     * from @zhaojian
     * content 点击事件绑定
     */
    @Override
    public void onSaveClick(int id) {
        switch (id) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_zjsw:
                Intent intent = new Intent(activity, CreateDrugsActivity.class);
                intent.putExtra(CameraActivity.CAMERA_PATH_VALUE1, path);
                startActivity(intent);
                break;
        }
    }


}
