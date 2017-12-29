package com.zzia.graduationproject.ui.activity.smallchat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.shcyd.lib.adapter.BasicAdapter;
import com.shcyd.lib.adapter.ViewHolder;
import com.shcyd.lib.utils.StringUtils;
import com.zzia.graduationproject.R;
import com.zzia.graduationproject.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class LocationActivity extends BaseActivity {
    @Bind(R.id.acl_address_list)
    ListView locationLv;
    @Bind(R.id.acl_search_edit)
    EditText searchEdit;
    @Bind(R.id.acl_clear_search_lyt)
    RelativeLayout clearSearchLyt;

    private TextView notShowLocationTv;
    private View headerView;
    private TextView cityTxt;
    private ImageView cityChooseIv;
    private BasicAdapter<PoiInfo> mAdapter;


    //定位得到的结果
    private List<PoiInfo> locationResult = new ArrayList<>();
    //搜索得到的结果
    private List<PoiInfo> searchResult = new ArrayList<>();
    //地图相关
    private LocationClient mLocationClient;
    private BDLocationListener mLocationListener;
    private GeoCoder geoCoder;  //地理位置反编码，已知经纬度，得到位置名称
    private PoiSearch mPoiSearch;  //兴趣点查找位置
    //之前选中的位置
    private String selectLocation;
    private double latitude;
    private double longitude;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_location;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {
        super.getBundleExtras(extras);
        selectLocation = extras.getString("selectLocation");
    }

    @Override
    protected void initViewsAndEvents() {
        initViews();
        initAdapter();
        initGeoCoder();
        initLocation();
        initListener();

    }

    private void initViews() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        headerView = getLayoutInflater().inflate(R.layout.item_location_head, null);
        notShowLocationTv = (TextView) headerView.findViewById(R.id.acl_not_showLocation_tv);
        cityTxt = (TextView) headerView.findViewById(R.id.acl_city_tv);
        cityChooseIv = (ImageView) headerView.findViewById(R.id.haveChoose_iv);
        if (selectLocation != null && selectLocation.equals(cityTxt.getText().toString())) {
            cityChooseIv.setVisibility(View.VISIBLE);
        } else {
            cityChooseIv.setVisibility(View.GONE);
        }
        locationLv.addHeaderView(headerView);
        //不显示位置
        notShowLocationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndBack(null);
            }
        });
        //市点击效果
        cityTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!StringUtils.isEmpty(cityTxt.getText().toString())) {
                    finishAndBack(cityTxt.getText().toString());
                }
            }
        });

    }

    private void initAdapter() {
        mAdapter = new BasicAdapter<PoiInfo>(this, R.layout.item_location) {
            @Override
            protected void render(ViewHolder holder, PoiInfo item, int position) {
                holder.setText(R.id.il_name_txt, item.name);
                holder.setText(R.id.il_detail_txt, item.address);
                if (StringUtils.isEmpty(selectLocation)) {
                    holder.gone(R.id.il_choose_iv);
                } else {
                    if (selectLocation.equals(item.name)) {
                        holder.visible(R.id.il_choose_iv);
                    } else {
                        holder.gone(R.id.il_choose_iv);
                    }
                }
            }
        };
        mAdapter.setDataList(locationResult);
        locationLv.setAdapter(mAdapter);
    }

    private void initGeoCoder() {
        geoCoder = GeoCoder.newInstance();
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    return;
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    return;
                } else {
                    if (result.getPoiList() != null && result.getPoiList().size() > 0) {
                        locationResult = result.getPoiList();
                        mAdapter.getDataList().clear();
                        mAdapter.getDataList().addAll(locationResult);
                        mAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }


    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        //设置相关定位属性
        LocationClientOption options = new LocationClientOption();
        //设置返回的定位结果坐标系
        options.setCoorType("bd0911");
        //设置是否使用gps
        options.setOpenGps(true);
        //设置是否需要地址信息
        options.setIsNeedAddress(true);
        //设置是否需要位置语义化结果
        options.setIsNeedLocationDescribe(true);
        //设置是否需要poi结果
        options.setIsNeedLocationPoiList(true);
        options.setIgnoreKillProcess(false);//杀死服务
        options.setEnableSimulateGps(false);

        mLocationClient.setLocOption(options);
        mLocationClient.start();
    }

    class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {
            //分Gps、网络、离线定位
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                //  showToast("Gps定位已获得位置");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                //showToast("网络定位已获得位置");

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                //showToast("离线定位已获得位置");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                //服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因;
                showToast("错误码:" + location.getLocType() + ",服务端错误！");
                return;

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                //网络不同导致定位失败，请检查网络是否通畅;
                showToast("错误码:" + location.getLocType() + ",请检查你的网络");
                return;
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                //无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机;
                showToast("错误码:" + location.getLocType() + "请尝试重启手机！");
                return;
            } else {
                showToast("错误码:" + location.getLocType());
                return;
            }
            if (!StringUtils.isEmpty(location.getCity())) {
                //在主线程中设置
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cityTxt.setText(location.getCity());
                    }
                });

            }
            //得到经度
            longitude = location.getLongitude();
            //得到纬度
            latitude = location.getLatitude();
            //通过经纬度，发起反编码
            ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption().location(new LatLng(latitude, longitude));
            geoCoder.reverseGeoCode(reverseGeoCodeOption);

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }

    private void initListener() {
        clearSearchLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdit.setText("");
            }
        });

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String key = searchEdit.getText().toString().trim();
                if (StringUtils.isEmpty(key)) {
                    clearSearchLyt.setVisibility(View.GONE);
                    mAdapter.setDataList(locationResult);
                    mAdapter.notifyDataSetChanged();
                } else {
                    searchResult = new ArrayList<>();
                    mAdapter.setDataList(searchResult);
                    mAdapter.notifyDataSetChanged();
                    clearSearchLyt.setVisibility(View.VISIBLE);
                    if (mPoiSearch == null) {
                        mPoiSearch = PoiSearch.newInstance();
                        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
                            @Override
                            public void onGetPoiResult(PoiResult poiResult) {
                                if (StringUtils.isEmpty(searchEdit.getText().toString().trim())) {
                                    clearSearchLyt.setVisibility(View.GONE);
                                    mAdapter.setDataList(locationResult);
                                    mAdapter.notifyDataSetChanged();
                                    return;
                                }
                                if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
                                    // 没有检测到结果
                                    return;
                                }
                                if (poiResult.getAllPoi() != null) {
                                    searchResult = poiResult.getAllPoi();
                                }
                                mAdapter.setDataList(searchResult);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                            }

                            @Override
                            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                            }
                        };
                        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
                    }

                    PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption().city(cityTxt.getText().toString().trim()).keyword(key);
                    mPoiSearch.searchInCity(poiCitySearchOption);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //item的点击事件
        locationLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View view1, int position, long l) {
                if (position == 0) {
                    return;
                }
                finishAndBack(mAdapter.getDataList().get(position - locationLv.getHeaderViewsCount()).name);
            }
        });
        //滑动是清除键盘
        locationLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void finishAndBack(String name) {
        Intent intent = new Intent();
        intent.putExtra("selectLocation", name);
        setResult(8, intent);
        this.finish();
    }
}
