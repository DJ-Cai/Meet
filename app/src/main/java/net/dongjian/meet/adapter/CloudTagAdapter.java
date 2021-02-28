package net.dongjian.meet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongjian.framwork.helper.GlideHelper;
import com.moxun.tagcloudlib.view.TagsAdapter;

import net.dongjian.framework.R;
import net.dongjian.meet.model.StarModel;

import java.util.List;


/**
 * 3D星球View的适配器
 * 重点是getView()
 */
public class CloudTagAdapter extends TagsAdapter {

    private Context mContext;
    private List<StarModel> mList;
    private LayoutInflater inflater;

    public CloudTagAdapter(Context mContext, List<StarModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @return 返回Tag数量
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * @param context
     * @param position
     * @param parent
     * @return 返回每个Tag实例
     */
    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        //初始化View和控件
        View view = inflater.inflate(R.layout.layout_star_view_item, null);
        ImageView iv_star_view = view.findViewById(R.id.iv_star_icon);
        TextView tv_star_name = view.findViewById(R.id.tv_star_name);

        StarModel model = mList.get(position);
        GlideHelper.loadUrl(mContext,model.getPhotoUrl(),iv_star_view);
        tv_star_name.setText(model.getNickName());
        return view;

    }

    /**
     * @param position
     * @return 返回Tag数据--返回当前位置上的item
     */
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    /**
     * @param position
     * @return 针对每个Tag返回一个权重值，该值与ThemeColor和Tag初始大小有关；一个简单的权重值生成方式是对一个数N取余或使用随机数
     */
    @Override
    public int getPopularity(int position) {
        //demo里是返回7，所以这里取7
        return 7;
    }

    /**
     * Tag主题色发生变化时会回调该方法
     *
     * @param view
     * @param themeColor
     */
    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }
}
