package net.dongjian.meet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dongjian.framwork.helper.GlideHelper;

import net.dongjian.meet.R;
import net.dongjian.meet.model.AddFriendModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 多Type--RecyclerView中的布局是多种形式的
 * 已经用万能适配了，这个保留 但不用了
 */
public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //多type：标题和内容
    public static final int TYPE_TITLE = 0;
    public static final int TYPE_CONTENT = 1;

    private Context mContext;
    //这个model就是RecyclerView可以多type的关键
    private List<AddFriendModel> mList;
    private LayoutInflater inflater;


    //设置点击事件
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    /**
     * 构造函数
     * @param mContext
     * @param mList
     */
    public AddFriendAdapter(Context mContext, List<AddFriendModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 创建ViewHolder ： 根据不同的类型，创建并返回不同的viewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_TITLE){
            return new TitleViewHolder(inflater.inflate(R.layout.layout_search_title_item,null));
        }else if(viewType == TYPE_CONTENT){
            return new ContentViewHolder(inflater.inflate(R.layout.layout_search_user_item,null));
        }
        return null;
    }

    /**
     * 绑定ViewHolder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //拿到当前的model
        AddFriendModel model = mList.get(position);
        //如果当前model是标题类型的
        if(model.getType() == TYPE_TITLE){
            ((TitleViewHolder)holder).tv_title.setText(model.getTitle());
        }else if(model.getType() == TYPE_CONTENT){ //如果当前model是内容类型的
            //用Glide设置头像
            GlideHelper.loadUrl(mContext,model.getPhoto(),((ContentViewHolder)holder).iv_photo);
            //设置性别 : boolean isSex
            ((ContentViewHolder)holder).iv_sex.setImageResource(model.isSex() ? R.drawable.img_boy_icon : R.drawable.img_girl_icon);
            ((ContentViewHolder)holder).tv_nickname.setText(model.getNickName());
            ((ContentViewHolder)holder).tv_age.setText(model.getAge() + "岁");
            ((ContentViewHolder)holder).tv_desc.setText(model.getDesc());

            //这里就是用通讯录查找的了，格式和直接查找的不太一样
            if(model.isContact()){
                ((ContentViewHolder)holder).ll_contact_info.setVisibility(View.VISIBLE);
                ((ContentViewHolder)holder).tv_contact_name.setText(model.getContactName());
                ((ContentViewHolder)holder).tv_contact_phone.setText(model.getContactPhone());
            }
        }

        //设置好view的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //判断有没有设置点击事件
                if(onClickListener != null ){
                    onClickListener.OnClick(position);
                }
            }
        });
    }

    /**
     * RecyclerView专门开放给我们用来实现item-Types的方法，最优雅,不影响性能
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        //因为需要识别不同的type（比单item多了一个type字段来区分），所以做了一个model包里面的AddFriendModel类
        return mList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //分别初始化两个不同的ViewHodler 及对应的布局控件
    class TitleViewHolder extends RecyclerView.ViewHolder{


        private TextView tv_title;
        public TitleViewHolder(View itemView){
            super(itemView );
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView iv_photo;
        private ImageView iv_sex;
        private TextView tv_nickname;
        private TextView tv_age;
        private TextView tv_desc ;

        //通过通讯录查找用户的时候，格式稍微有所不同
        private LinearLayout ll_contact_info;
        private TextView tv_contact_name;
        private TextView tv_contact_phone;

        public ContentViewHolder(View itemView){
            super(itemView );
            iv_photo = itemView.findViewById(R.id.iv_photo);
            iv_sex = itemView.findViewById(R.id.iv_sex);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_age = itemView.findViewById(R.id.tv_age);
            tv_desc = itemView.findViewById(R.id.tv_desc);

            ll_contact_info = itemView.findViewById(R.id.ll_contact_info);
            tv_contact_name = itemView.findViewById(R.id.tv_contact_name);
            tv_contact_phone = itemView.findViewById(R.id.tv_contact_phone);
        }
    }


    //点击事件
    public interface  OnClickListener{
        void OnClick(int position);
    }

}
