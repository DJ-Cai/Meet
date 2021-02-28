package net.dongjian.meet.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.dongjian.framwork.adapter.CommonAdapter;
import com.dongjian.framwork.adapter.CommonViewHolder;
import com.dongjian.framwork.base.BaseBackActivity;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.manager.DialogManager;
import com.dongjian.framwork.manager.MapManager;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.view.DialogView;
import com.dongjian.framwork.view.LoadingView;

import net.dongjian.meet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: LocationActivity
 * Profile: 地图
 * 1、实现高德地图预览
 * 2、实现兴趣点查找
 * 3、实现正反编码
 *
 */
public class LocationActivity extends BaseBackActivity implements View.OnClickListener, PoiSearch.OnPoiSearchListener {

    /**
     * 跳转
     *
     * @param mActivity
     * @param isShow
     * @param la
     * @param lo
     * @param address
     * @param requestCode
     */
    public static void startActivity(Activity mActivity, boolean isShow, double la, double lo
            , String address, int requestCode) {
        Intent intent = new Intent(mActivity, LocationActivity.class);
        //传入 显示 或 隐藏 发送按钮
        intent.putExtra(Constants.INTENT_MENU_SHOW, isShow);
        //经纬度和地址
        intent.putExtra("la", la);
        intent.putExtra("lo", lo);
        intent.putExtra("address", address);
        mActivity.startActivityForResult(intent, requestCode);
    }

    private MapView mMapView;
    private EditText et_search;
    private ImageView iv_poi;

    private AMap aMap;

    //判断是否为展示地址，而不是自己选取以后要发送的地址（两者需要的不一样，展示的需要接受外界传递的地址）
    private boolean isShow;

    private DialogView mPoiView;
    private RecyclerView mConstellationnView;
    private TextView tv_cancel;

    private CommonAdapter<PoiItem> mPoiListAdapter;
    private List<PoiItem> mList = new ArrayList<>();

    private PoiSearch.Query query;
    private PoiSearch poiSearch;

    private LoadingView mLodingView;

    private double ILa;
    private double ILo;
    private String IAddress;

    private int ITEM = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initPoiView();
        initView(savedInstanceState);
    }

    private void initPoiView() {

        mLodingView = new LoadingView(this);
        mLodingView.setLoadingText(getString(R.string.text_location_search));

        mPoiView = DialogManager.getInstance().initView(
                this, R.layout.dialog_select_constellation, Gravity.BOTTOM);
        mPoiView.setCancelable(false);
        mConstellationnView = mPoiView.findViewById(R.id.mConstellationnView);
        tv_cancel = mPoiView.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogManager.getInstance().hide(mPoiView);
            }
        });

        mConstellationnView.setLayoutManager(new LinearLayoutManager(this));
        mConstellationnView.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        mPoiListAdapter = new CommonAdapter<>(mList, new CommonAdapter.OnBindDataListener<PoiItem>() {
            @Override
            public void onBindViewHolder(final PoiItem model, CommonViewHolder viewHolder, int type, final int position) {
                viewHolder.setText(R.id.tv_age_text, model.toString());

                //设置选中dialog中的地址后的点击事件
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ITEM = position;
                        DialogManager.getInstance().hide(mPoiView);
                        /**
                         * 已知条件：地址
                         * 地址 转换 经纬度
                         */
                        MapManager.getInstance().address2poi(model.toString(), new MapManager.OnAddress2PoiGeocodeListener() {
                            @Override
                            public void address2poi(double la, double lo, String address) {
                                ILa = la;
                                ILo = lo;
                                IAddress = address;

                                updatePoi(la, lo, address);
                            }
                        });
                    }
                });
            }

            @Override
            public int getLayoutId(int type) {
                return R.layout.layout_me_age_item;
            }
        });
        mConstellationnView.setAdapter(mPoiListAdapter);
    }

    private void initView(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.mMapView);
        et_search = (EditText) findViewById(R.id.et_search);
        iv_poi = (ImageView) findViewById(R.id.iv_poi);

        iv_poi.setOnClickListener(this);

        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        //显示定位蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        //缩放
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));


        //拿到Intent的isshow来判断是否需要发送按钮
        Intent intent = getIntent();
        isShow = intent.getBooleanExtra(Constants.INTENT_MENU_SHOW, false);
        if (!isShow) {
            //如果不显示 则作为展示类地图 接收外界传递的地址显示
            double la = intent.getDoubleExtra("la", 0);
            double lo = intent.getDoubleExtra("lo", 0);
            String address = intent.getStringExtra("address");

            updatePoi(la, lo, address);
        }

        //监听位置变化   (因为获取到的位置 有时获取不到 为null)
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LogUtils.i("location:" + location.toString());
                LogUtils.i("location.getExtras:" + location.getExtras().toString());
            }
        });
    }

    /**
     * 更新地点
     *
     * @param la
     * @param lo
     * @param address
     */
    private void updatePoi(double la, double lo, String address) {
        //关闭定位
        aMap.setMyLocationEnabled(true);
        //刷新菜单
        supportInvalidateOptionsMenu();
        //显示位置 -- 做经纬度
        LatLng latLng = new LatLng(la, lo);
        //清理之前的地图
        aMap.clear();
        //加载latLng位置
        aMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.text_location)).snippet(address));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //兴趣点搜索
            case R.id.iv_poi:
                String keyWord = et_search.getText().toString().trim();
                if (TextUtils.isEmpty(keyWord)) {
                    return;
                }
                poiSearch(keyWord);
                break;
        }
    }

    /**
     * 关键字POI搜索
     *
     * @param keyWord
     */
    private void poiSearch(String keyWord) {
        mLodingView.show();
        query = new PoiSearch.Query(keyWord, "", "");
        query.setPageSize(6);
        query.setPageNum(1);
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //展示地图，才加载“发送”按钮
        if (isShow) {
            getMenuInflater().inflate(R.menu.location_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //点击了menu上的发送按钮
        if (item.getItemId() == R.id.menu_send) {
            Intent intent = new Intent();
            if (ITEM > 0) {
                //直接点击
                intent.putExtra("la", ILa);
                intent.putExtra("lo", ILo);
                intent.putExtra("address", IAddress);
            } else {
                //直接点击：获取经纬度
                intent.putExtra("la", aMap.getMyLocation().getLatitude());
                intent.putExtra("lo", aMap.getMyLocation().getLongitude());
                intent.putExtra("address", aMap.getMyLocation().getExtras().getString("desc"));
            }
            //返回成功---到chatActivity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        //得到搜索的结果
        mLodingView.hide();
        if (mList.size() > 0) {
            mList.clear();
        }
        mList.addAll(poiResult.getPois());
        mPoiListAdapter.notifyDataSetChanged();
        DialogManager.getInstance().show(mPoiView);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }
}
