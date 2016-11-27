package com.a3did.partner.account;

import android.content.Context;
import android.icu.text.MessagePattern;
import android.support.v4.content.ContextCompat;

import com.a3did.partner.adapterlist.AchievementListData;
import com.a3did.partner.adapterlist.AssistantListData;
import com.a3did.partner.adapterlist.RewardListData;
import com.a3did.partner.partner.R;

import java.util.ArrayList;

/**
 * Created by Joonhyun on 2016-11-26.
 */


public class UserManager {

    private static UserManager mInstance;
    private ArrayList<PartnerUserInfo> mUserInfoList;
    private PartnerUserInfo mCurrentUserInfo;
    private Context mContext;
    private UserManager(){
        mInstance = null;
        mUserInfoList = new ArrayList<PartnerUserInfo>();
        mCurrentUserInfo = null;
    }

    public void setContext(Context context){
        mContext = context;
    }
    public static UserManager getInstance(){
        if(mInstance == null)
            mInstance = new UserManager();

        return mInstance;
    }

    public PartnerUserInfo getCurrentUserInfo(){
        return mCurrentUserInfo;
    }

    public ArrayList<PartnerUserInfo> getUserInfoLIst(){
        return mUserInfoList;
    }
    public void setCurrentUserInfo(PartnerUserInfo userInfo){
        mCurrentUserInfo = userInfo;
    }

    public void setCurrentUserInfo(int index){
        mCurrentUserInfo = mUserInfoList.get(index);
    }

    public String getCurrentUserName(){
        if(mCurrentUserInfo != null){
            return mCurrentUserInfo.mName;
        }
        return null;
    }

    public void generateInformation(){
        // to do :여기에 유저들 정보 2명정도 미리 만들어 두기

        //최준현
        PartnerUserInfo userInfo = new PartnerUserInfo();
        userInfo.mName = "준현";
        userInfo.mStarNumber = 500;
        userInfo.mBeaconInfo = "24DDF4118CF1440C87CDE368DAF9C93E";
        userInfo.mUserPhoneNumber = "01093348599";
        userInfo.mParentPhoneNumber = "01062747927";
        userInfo.mUID = hashCode();
        //정보 기입

        //assistant
        AssistantListData listData = new AssistantListData();
        listData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp),
                "수학 숙제하기", "11월 19일 오후 4시") ;
        userInfo.mScheduleInfoList.add(listData);

        listData = new AssistantListData();
        listData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_home_black_24dp),
                "강아지 먹이주기", "11월 19일 오후 5시") ;
        userInfo.mScheduleInfoList.add(listData);

        listData = new AssistantListData();
        listData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_border_black_24dp),
                "미술학원 가기", "11월 19일 오후 6시") ;
        userInfo.mScheduleInfoList.add(listData);

        listData = new AssistantListData();
        listData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_assignment_black_24dp),
                "인터넷 강의 보기", "11월 19일 오후 8시") ;
        userInfo.mScheduleInfoList.add(listData);

        listData = new AssistantListData();
        listData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_home_black_24dp),
                "자기 전에 영양제먹기", "11월 19일 오후 9시") ;
        userInfo.mScheduleInfoList.add(listData);

        //achievement
        AchievementListData achievementlistData = new AchievementListData();
        achievementlistData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_border_black_24dp),
                "수학 시험 100점 맞기", 10, "11월 19일 오후 3시", "12월 20일 오후 3시");
        userInfo.mAchievementInfoList.add(achievementlistData);

        achievementlistData = new AchievementListData();
        achievementlistData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_border_black_24dp),
                "집 어지럽히지 않고 5일 유지하기", 5, "11월 19일 오후 4시", "11월 24일 오후 4시");
        userInfo.mAchievementInfoList.add(achievementlistData);

        achievementlistData = new AchievementListData();
        achievementlistData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_border_black_24dp),
                "두시간 집중해서 독서하기", 1, "11월 19일 오후 5시", "11월 29일 오후 7시");
        userInfo.mAchievementInfoList.add(achievementlistData);

        achievementlistData = new AchievementListData();
        achievementlistData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_border_black_24dp),
                "운동 주 5회 하기", 2, "11월 19일 오후 9시", "11월 26일 오후 9시");
        userInfo.mAchievementInfoList.add(achievementlistData);

        achievementlistData = new AchievementListData();
        achievementlistData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_bookmark_border_black_24dp),
                "강아지 목욕시켜주기", 1, "11월 20일 오후 8시", "11월 20일 오후 9시");
        userInfo.mAchievementInfoList.add(achievementlistData);

        //reward
        RewardListData rewardListData = new RewardListData();
        // 첫 번째 아이템 추가.
        rewardListData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_card_giftcard_black_24dp),
                "뽀로로 컴퓨터 사기", 100); ;
        userInfo.mRewardInfoList.add(rewardListData);
        // 두 번째 아이템 추가.
        rewardListData = new RewardListData();
        rewardListData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_card_giftcard_black_24dp),
                "놀이동산 가기", 60) ;
        userInfo.mRewardInfoList.add(rewardListData);

        // 세 번째 아이템 추가.
        rewardListData = new RewardListData();
        rewardListData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_card_giftcard_black_24dp),
                "짜장면 먹기",  10) ;
        userInfo.mRewardInfoList.add(rewardListData);

        // 네 번째 아이템 추가.
        rewardListData = new RewardListData();
        rewardListData.setItem(ContextCompat.getDrawable(mContext, R.drawable.ic_card_giftcard_black_24dp),
                "컴퓨터 이용 1시간 쿠폰!", 20) ;
        userInfo.mRewardInfoList.add(rewardListData);

        //safety

        //completed

        //missed

        mUserInfoList.add(userInfo);


        //서광균
        userInfo = new PartnerUserInfo();
        userInfo.mName = "광균";
        userInfo.mStarNumber = 400;
        userInfo.mBeaconInfo = "";
        userInfo.mUserPhoneNumber = "01093348599";
        userInfo.mParentPhoneNumber = "01062747927";
        userInfo.mUID = hashCode();

        //정보 기입


        mUserInfoList.add(userInfo);

    }
}