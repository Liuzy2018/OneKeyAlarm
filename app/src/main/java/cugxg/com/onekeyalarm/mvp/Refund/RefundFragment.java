package cugxg.com.onekeyalarm.mvp.Refund;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.layout.PopupList;
import cugxg.com.onekeyalarm.model.Case;
import cugxg.com.onekeyalarm.model.User;
import cugxg.com.onekeyalarm.model.source.UserDataSource;
import cugxg.com.onekeyalarm.model.source.UserRepository;
import cugxg.com.onekeyalarm.mvp.HistoryCase.HistoryCaseFragment;
import cugxg.com.onekeyalarm.mvp.Refund.ViewRefundProcess.ViewRefundProcessActivity;
import cugxg.com.onekeyalarm.util.ActivityUtils;
import cugxg.com.onekeyalarm.util.ToastUtils;
import cugxg.com.onekeyalarm.util.UiUtils;
import cugxg.com.onekeyalarm.util.UserUtils;

import static android.text.TextUtils.TruncateAt.END;
import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;

public class RefundFragment extends BaseFragment {
    @BindView(R.id.linearLayout_refund)
    LinearLayout linearLayout_refund;

    public static RefundFragment newInstance() {
        return new RefundFragment();
    }

    private List<Case> caseList;
    private int int_curline = 0;
    private List<String> popupMenuItemList = new ArrayList<>();

    private List<List<String>> toload_files_lists = new ArrayList<>();//文件名
    private List<List<String>> toload_filename_lists = new ArrayList<>();//文件完整路径
    private List<List<String>> loaded_file_url = new ArrayList<>();//文件上传到leancloud返回的url
    private List<String> caseid_lists = new ArrayList<>();
    private int loaded_file_num = 0;
    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_refund, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        popupMenuItemList.add(getString(R.string.delete));
        setInfo();
    }

    private void setInfo() {
        String cql = "select * from Case where userId = '" + UserUtils.getUser().getObjectId() + "'";

        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                List<AVObject> results = (List<AVObject>) avCloudQueryResult.getResults();
                for (int i = 0; i < results.size(); i++) {
                    toload_files_lists.add(new ArrayList<String>());
                    toload_filename_lists.add(new ArrayList<String>());
                    loaded_file_url.add(new ArrayList<String>());
                    caseid_lists.add(results.get(i).getObjectId());

                    LinearLayout.LayoutParams lp;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    LinearLayout linearLayout = new LinearLayout(mContext);
                    linearLayout.setOrientation(HORIZONTAL);
                    TextView textView_num = new TextView(mContext);
                    textView_num.setText("案情" + (i + 1));
                    textView_num.setGravity(1);
                    lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(2, 20, 2, 2);
                    textView_num.setLayoutParams(lp);

                    TextView textView_info = new TextView(mContext);
                    String info = "时间：" + sdf.format(results.get(i).getDate("createdAt")) + "\n"
                            + "地点：" + results.get(i).getString("venue") + "\n"
                            + "描述：" + results.get(i).getString("describe") + "\n";
                    textView_info.setText(info);
                    textView_info.setEllipsize(END);
                    textView_info.setSingleLine(true);
                    lp = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(20, 20, 2, 2);
                    textView_info.setLayoutParams(lp);
                    textView_info.setGravity(2);
                    textView_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder ab = new AlertDialog.Builder(mContext);  //(普通消息框)
                            ab.setTitle("详细信息");  //设置标题
                            TextView tv = (TextView) v;
                            ab.setMessage(tv.getText().toString());
                            ab.show();
                        }
                    });

                    Button button = new Button(mContext);
                    button.setWidth(5);
                    button.setText("选择文件");
                    button.setTag(i + 1);
                    button.setGravity(1);
                    lp = new LinearLayout.LayoutParams(250, LinearLayout.LayoutParams.WRAP_CONTENT);
                    //此处相当于布局文件中的Android:layout_gravity属性
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    button.setLayoutParams(lp);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int_curline = (int) v.getTag();
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");//无类型限制
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, 1);
                        }
                    });

                    ListView listView_loaded = new ListView(mContext);
                    listView_loaded.setForegroundGravity(2);
                    listView_loaded.setTag(i);
                    PopupList popupList = new PopupList(mContext);
                    popupList.bind(listView_loaded, popupMenuItemList, new PopupList.PopupListListener() {
                        @Override
                        public boolean showPopupList(View adapterView, View contextView, int contextPosition) {
                            return true;
                        }

                        @Override
                        public void onPopupListClick(View contextView, int contextPosition, int position) {
                            int curindex = (int) contextView.getTag();
                            toload_files_lists.get(curindex).remove(contextPosition);
                            LinearLayout tl = (LinearLayout) linearLayout_refund.getChildAt(curindex + 1);
                            ListView tlistv = (ListView) tl.getChildAt(3);
                            ArrayAdapter<String> arr_aAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, toload_files_lists.get(curindex));
                            tlistv.setAdapter(arr_aAdapter);
                        }
                    });
                    linearLayout.addView(textView_num);
                    linearLayout.addView(textView_info);
                    linearLayout.addView(button);
                    linearLayout.addView(listView_loaded);
                    linearLayout_refund.addView(linearLayout);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                String path = uri.getPath();

                toload_filename_lists.get(int_curline - 1).add(path);
                toload_files_lists.get(int_curline - 1).add(path.substring(path.lastIndexOf("/") + 1));

                LinearLayout temp_linearlayout = (LinearLayout) linearLayout_refund.getChildAt(int_curline);
                ListView temp_listview = (ListView) temp_linearlayout.getChildAt(3);

                ArrayAdapter<String> arr_aAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, toload_files_lists.get(int_curline - 1));
                temp_listview.setAdapter(arr_aAdapter);
                ToastUtils.show(mContext, path);
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                String path = getPath(mContext, uri);
                if (toload_files_lists.get(int_curline - 1) == null)
                    toload_files_lists.add(int_curline - 1, new ArrayList<String>());
                toload_filename_lists.get(int_curline - 1).add(path);
                toload_files_lists.get(int_curline - 1).add(path.substring(path.lastIndexOf("/") + 1));

                LinearLayout temp_linearlayout = (LinearLayout) linearLayout_refund.getChildAt(int_curline);
                ListView temp_listview = (ListView) temp_linearlayout.getChildAt(3);

                ArrayAdapter<String> arr_aAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, toload_files_lists.get(int_curline - 1));
                temp_listview.setAdapter(arr_aAdapter);
            }
        }
    }

    @OnClick({R.id.btn_submit_evidence,R.id.btn_view_refund_process})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_submit_evidence:
                submitEvidence();
                if (loaded_file_num==caseid_lists.size()) {
                    User user = UserUtils.getUser();
                    user.setRefundProcess(2);
                    UserRepository userRepository = new UserRepository();
                    userRepository.saveUserInfo(user, new UserDataSource.SaveUserInfoCallback() {
                        @Override
                        public void saveSuccess() {

                        }

                        @Override
                        public void saveFail(Error e) {
                            e.getMessage();
                        }
                    });
                    intent = new Intent();
                    intent.setClass(mContext,ViewRefundProcessActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.btn_view_refund_process:
                intent = new Intent();
                intent.setClass(mContext,ViewRefundProcessActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void submitEvidence() {
        for (int i = 0; i < caseid_lists.size(); i++) {
            int j = 0;
            if (toload_files_lists.get(i).size()==0)
                continue;
            loaded_file_num=loaded_file_num+1;
            for (; j < toload_files_lists.size(); j++) {
                try {
                    final AVFile file = AVFile.withAbsoluteLocalPath(toload_files_lists.get(i).get(j), toload_filename_lists.get(i).get(j));
                    file.save();
                    loaded_file_url.get(i).add(file.getUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            AVObject todo = AVObject.createWithoutData("Case", caseid_lists.get(i));
            todo.put("evidenceUrl", loaded_file_url.get(i));
            todo.saveInBackground();

        }

    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
