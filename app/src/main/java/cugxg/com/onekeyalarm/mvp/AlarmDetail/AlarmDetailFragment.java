package cugxg.com.onekeyalarm.mvp.AlarmDetail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.dialog.AddReceiveManDialog;
import cugxg.com.onekeyalarm.layout.MyImageView;
import cugxg.com.onekeyalarm.layout.ToggleGridRadioGroup;
import cugxg.com.onekeyalarm.model.Case;
import cugxg.com.onekeyalarm.util.AudioRecoderUtils;
import cugxg.com.onekeyalarm.util.BaiduMapLocationUtils;
import cugxg.com.onekeyalarm.util.LocationUtil;
import cugxg.com.onekeyalarm.util.PopupWindowFactory;
import cugxg.com.onekeyalarm.util.SmsUtils.SmsUtil;
import cugxg.com.onekeyalarm.util.TimeUtils;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UserUtils;


public class AlarmDetailFragment extends BaseFragment implements AlarmDetailContract.View{
    static final int VOICE_REQUEST_CODE = 66;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    //调用系统相册-选择图片
    private static final int IMAGE = 1;
    private AlarmDetailContract.Presenter mPresenter;
    private BaiduMapLocationUtils baiduMapLocationUtils;
    @BindView(R.id.edit_receive)
    EditText editReceive;
    @BindView(R.id.edit_venue_detail)
    EditText editVenue;
    @BindView(R.id.edit_describe_detail)
    EditText editDescribe;
    @BindView(R.id.radiogroup_isEnableToPhone)
    RadioGroup radioGroup_isEnableToPhone;
    @BindView(R.id.ToggleGridRadioGroup_caseType)
    ToggleGridRadioGroup toggleGridRadioGroup_caseType;

    @BindView(R.id.btn_audio)
    Button btnAudio;
    @BindView(R.id.f1)
    FrameLayout f1;
    @BindView(R.id.soundfile_layout)
    LinearLayout soundFileLayout;
    @BindView(R.id.photofile_layout)
    LinearLayout photoLayout;

    private AddReceiveManDialog mAddReceiveManDialog;
    private PopupWindowFactory mPop;
    private AudioRecoderUtils mAudioRecoderUtils;
    private ImageView mImageView;
    private TextView mTextView;
    private Dialog dia;
    private double longitude;
    private double latitude;

    public static AlarmDetailFragment newInstance() {
        return new AlarmDetailFragment();
    }

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_detail, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        baiduMapLocationUtils = BaiduMapLocationUtils.getInstance(mContext);
        try{
            for(int i = 0;i<toggleGridRadioGroup_caseType.getChildCount();i++)
            {
                ToggleButton t = (ToggleButton) toggleGridRadioGroup_caseType.getChildAt(i);
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String describe = editDescribe.getText().toString();
                        String str_caseType = "";
                        int i = toggleGridRadioGroup_caseType.mCheckedId;

                        switch (i){
                                case 1:
                                    str_caseType="我乘坐了黑车。";
                                    break;
                                case 2:
                                    str_caseType="我遭遇了非法囚禁。";
                                    break;
                                case 3:
                                    str_caseType="我目击了非法交易。";
                                    break;
                                case 4:
                                    str_caseType="我遭遇了拦路抢劫。";
                                    break;
                                case 5:
                                    str_caseType="我目击了聚众赌博。";
                                    break;
                                default:
                                    str_caseType="";
                                    break;
                            }
                            String[] arr = describe.split("。");
                            if(arr.length>2)
                                describe=arr[0]+"。"+str_caseType+arr[2]+"。";
                            else if (arr.length==2)
                                describe=arr[0]+"。"+str_caseType+arr[1]+"。";
                            editDescribe.setText(describe);
                        }
                });
            }
            radioGroup_isEnableToPhone.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    String describe = editDescribe.getText().toString();
                    String[] arr = describe.split("。");
                    describe="";
                    for(int i=0;i<arr.length-1;i++)
                        describe=describe+arr[i]+"。";
                    String str_isEnableToPhone = radioGroup_isEnableToPhone.getCheckedRadioButtonId()==R.id.radiogroup_yes?"我方便接听电话。":"我不方便接听电话。";
                    describe=describe+str_isEnableToPhone;
                    editDescribe.setText(describe);
                }
            });
            setAlarmInfo();
        }catch (Exception e){
            e.toString();
        }

        //PopupWindow的布局文件
        final View view = View.inflate(mContext, R.layout.layout_microphone, null);

        mPop = new PopupWindowFactory(mContext, view);

        //PopupWindow布局文件里面的控件
        mImageView = (ImageView) view.findViewById(R.id.iv_recording_icon);
        mTextView = (TextView) view.findViewById(R.id.tv_recording_time);

        mAudioRecoderUtils = new AudioRecoderUtils();

        //录音回调
        mAudioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                mImageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                mTextView.setText(TimeUtils.long2String(time));
            }

            //录音结束，filePath为保存路径
            @Override
            public void onStop(String filePath) {
                mTextView.setText(TimeUtils.long2String(0));
            }
        });
        requestPermissions();

    }

    @OnClick({R.id.btn_add_receive, R.id.btn_photo,R.id.btn_alarm_detail,R.id.btn_location_detail})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_receive:
                mAddReceiveManDialog = new AddReceiveManDialog(mContext);
                mAddReceiveManDialog.show(getChildFragmentManager(), "dialog");
                mAddReceiveManDialog.setOnAddReceiveCallback(new AddReceiveManDialog.OnAddReceiveCallback() {
                    @Override
                    public void onAddSuccess(String phone) {
                        String old = editReceive.getText().toString();
                        editReceive.setText(old + phone);
                        mAddReceiveManDialog.dismiss();
                    }
                });
                break;
            case R.id.btn_photo:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
                break;
            case R.id.btn_alarm_detail:
                alarm();
                break;
            case R.id.btn_location_detail:
                double[] loc = new double[]{baiduMapLocationUtils.getCur_latitude(),baiduMapLocationUtils.getCur_longitude()};
                if (loc != null) {
                    longitude=loc[1];
                    latitude=loc[0];
                    String location = "东经：" + longitude + ",北维：" + latitude;
                    editVenue.setText(location);
                }
                break;
        }
    }

    private void alarm(){
        String receive = editReceive.getText().toString();
        String describe = editDescribe.getText().toString();
        List<String> photourls = new ArrayList<>();
        List<String> soundurls = new ArrayList<>();
//        int numOfFiles = Math.max(0,photoLayout.getChildCount()-1)+Math.max(0,soundFileLayout.getChildCount()-1);
//        final CountDownLatch latch = new CountDownLatch(numOfFiles);
        final List<String> photo_to_upload=new ArrayList<>();
        final List<String> sound_to_upload=new ArrayList<>();

        if(photoLayout.getChildCount()>1) {
            for(int i=1;i<photoLayout.getChildCount();i++){
                LinearLayout temp = (LinearLayout) photoLayout.getChildAt(i);
                TextView textView = (TextView) temp.getChildAt(0);
                String url = textView.getTag().toString();
                photourls.add(url);
                try {
                    final AVFile file = AVFile.withAbsoluteLocalPath(textView.getText().toString(),url);
                    file.save();
                    photo_to_upload.add(file.getUrl());
//                    file.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(AVException e) {
//                            latch.countDown();
//                            photo_to_upload.add(file.getUrl());
//                        }
//                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if(soundFileLayout.getChildCount()>1) {
            for (int i = 1; i < soundFileLayout.getChildCount(); i++) {
                LinearLayout temp = (LinearLayout) soundFileLayout.getChildAt(i);
                TextView textView = (TextView) temp.getChildAt(0);
                String url = textView.getTag().toString();
                soundurls.add(url);
                try {
                    final AVFile file = AVFile.withAbsoluteLocalPath(textView.getText().toString(),url);
                    file.save();
                    sound_to_upload.add(file.getUrl());
//                    file.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(AVException e) {
//                            latch.countDown();
//                            sound_to_upload.add(file.getUrl());
//                        }
//                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
//        try {
//            latch.await();
//        }catch (InterruptedException e){
//            e.printStackTrace();
//        }
        Case cas = new Case("Case");
        cas.setVenue(editVenue.getText().toString());
        String str_recipients=editReceive.getText().toString();
        ArrayList<String> recipients = new ArrayList<String>(Arrays.asList(str_recipients.split(";")));
        cas.setRecipient(recipients);
        cas.setUserId(UserUtils.getUser().getObjectId());
        cas.setDescribe(editDescribe.getText().toString());
        cas.setEnableToPhone(radioGroup_isEnableToPhone.getCheckedRadioButtonId()==R.id.radiogroup_yes);
        cas.setLatitude(latitude);
        cas.setLongitude(longitude);
        cas.setType(Case.list_type[toggleGridRadioGroup_caseType.mCheckedId-1>-1?toggleGridRadioGroup_caseType.mCheckedId-1:5]);
        cas.setPhotoUrl((ArrayList<String>) photo_to_upload);
        cas.setSoundUrl((ArrayList<String>) sound_to_upload);
        mPresenter.saveCase(cas);

        startActivity(SmsUtil.sendMMS("求救信息",photourls,receive,describe));
    }
    private void setAlarmInfo() {
        double[] loc = new double[]{baiduMapLocationUtils.getCur_latitude(),baiduMapLocationUtils.getCur_longitude()};
        if (loc != null) {
            String areacode = LocationUtil.getAreaCode(loc[1], loc[0]);
            longitude=loc[1];
            latitude=loc[0];
            areacode = areacode.substring(areacode.length() - 3, areacode.length());
//            editReceive.setText(String.valueOf(12110) + areacode + ";");
            editReceive.setText("17752649865;");
            String location = "东经：" + loc[1] + ",北维：" + loc[0];
            editVenue.setText(location);

            JSONObject addrObject = LocationUtil.getAddrObjectByBD(loc[1],loc[0]);

            String describe = "我是"+UserUtils.getUser().getName()+",我在"+location+","+addrObject.get("formatted_address")+"。";
            String str_caseType = "";
            int i = toggleGridRadioGroup_caseType.mCheckedId;
            switch (i){
                case 1:
                    str_caseType="我乘坐了黑车。";
                    break;
                case 2:
                    str_caseType="我遭遇了非法囚禁。";
                    break;
                case 3:
                    str_caseType="我目击了非法交易。";
                    break;
                case 4:
                    str_caseType="我遭遇了拦路抢劫。";
                    break;
                case 5:
                    str_caseType="我目击了聚众赌博。";
                    break;
                default:
                    str_caseType="";
                    break;
            }
            String str_isEnableToPhone = radioGroup_isEnableToPhone.getCheckedRadioButtonId()==R.id.radiogroup_yes?"我方便接听电话。":"我不方便接听电话。";
            describe=describe+str_caseType;
            describe=describe+str_isEnableToPhone;
            editDescribe.setText(describe);
        }
    }

    /**
     * 开启扫描之前判断权限是否打开
     */
    private void requestPermissions() {
        //判断是否开启摄像头权限
        if ((ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                ) {
            StartListener();

        } else {
            //请求获取摄像头权限
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VOICE_REQUEST_CODE);
        }

    }

    /**
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VOICE_REQUEST_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                StartListener();
            } else {
                Toast.makeText(mContext, "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void StartListener() {
        //Button的touch监听
        btnAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPop.showAtLocation(f1, Gravity.CENTER, 0, 0);
                        ToastUtils.show(mContext, "松开保存...");
                        mAudioRecoderUtils.startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                        HashMap<String, Object> info = new HashMap<>();
                        info = mAudioRecoderUtils.stopRecord();        //结束录音（保存录音文件）
//                        mAudioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                        mPop.dismiss();
                        addSound(info.get("path").toString());
                        break;
                }
                return true;
            }
        });
    }

    private void addSound(String path) {
        if (soundFileLayout.getChildCount()==0) {
            TextView textView = new TextView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(80, 0, 0, 0);
            textView.setLayoutParams(lp);
            textView.setText("语音报警材料");
            soundFileLayout.addView(textView);
        }
        LinearLayout linearLayout = new LinearLayout(mContext);
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        final TextView textView = new TextView(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(80, 20, 0, 0);
        textView.setLayoutParams(lp);
        textView.setText(fileName);
        textView.setTag(path);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mMediaPlayer.setDataSource(v.getTag().toString());
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        final Button button = new Button(mContext);
        button.setText(R.string.delete);
        button.setTag(soundFileLayout.getChildCount());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundFileLayout.removeViewAt((Integer) button.getTag());
            }
        });
        button.setGravity(Gravity.RIGHT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(textView);
        linearLayout.addView(button);
        soundFileLayout.addView(linearLayout);
    }

    private void addPhoto(String path) {

        if (photoLayout.getChildCount()==0){
            TextView textView = new TextView(mContext);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(80, 0, 0, 0);
            textView.setLayoutParams(lp);
            textView.setText("图片报警材料");
            photoLayout.addView(textView);
        }
        LinearLayout linearLayout = new LinearLayout(mContext);
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        TextView textView = new TextView(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(80, 20, 0, 0);
        textView.setLayoutParams(lp);
        textView.setText(fileName);
        textView.setTag(path);
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dia = new Dialog(mContext, R.style.edit_AlertDialog_style);
                    dia.setContentView(R.layout.activity_start_dialog);
                    MyImageView imageView = (MyImageView) dia.findViewById(R.id.start_img);
                    Bitmap bitmap = BitmapFactory.decodeFile(v.getTag().toString());
                    imageView.setImageBitmap(bitmap);

                    //选择true的话点击其他地方可以使dialog消失，为false的话不会消失
                    dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
                    Window w = dia.getWindow();
                    WindowManager.LayoutParams lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia.onWindowAttributesChanged(lp);
                    imageView.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dia.dismiss();
                                }
                            });
                } catch (Exception e) {
                    e.toString();
                }

                dia.show();
            }
        });
        final Button button = new Button(mContext);
        button.setTag(photoLayout.getChildCount());
        button.setWidth(ActionBar.LayoutParams.WRAP_CONTENT);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photoLayout.removeViewAt((Integer) button.getTag());
            }
        });
        button.setText(R.string.delete);
        button.setGravity(Gravity.RIGHT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(textView);
        linearLayout.addView(button);
        photoLayout.addView(linearLayout);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getActivity().getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            addPhoto(imagePath);
            c.close();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void setPresenter(AlarmDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
