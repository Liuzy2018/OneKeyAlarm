package cugxg.com.onekeyalarm.mvp.HistoryCase;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;

import cugxg.com.onekeyalarm.R;
import cugxg.com.onekeyalarm.base.BaseFragment;
import cugxg.com.onekeyalarm.util.UserUtils;
import win.smartown.android.library.tableLayout.TableAdapter;
import win.smartown.android.library.tableLayout.TableLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryCaseFragment extends BaseFragment {
    private List<Content> contentList;
    private TableLayout tableLayout;

    @Override
    public View getLayout(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_case, container, false);
    }

    @Override
    public void onCreateFragment(@Nullable Bundle savedInstanceState) {
        getActivity().setContentView(R.layout.fragment_history_case);
        tableLayout = (TableLayout) getActivity().findViewById(R.id.main_table);
        initContent();

        //        firstColumnAsTitle();
    }

    private void initContent() {
        contentList = new ArrayList<>();
        contentList.add(new Content("案情", "时间", "地点", "描述"));

        String cql = "select * from Case where userId = '"+UserUtils.getUser().getObjectId() +"'";

        AVQuery.doCloudQueryInBackground(cql, new CloudQueryCallback<AVCloudQueryResult>() {
            @Override
            public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                List<AVObject> results = (List<AVObject>) avCloudQueryResult.getResults();
                for (int i = 0; i < results.size(); i++) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                    contentList.add(
                            new Content("案情"+(i+1),sdf.format(results.get(i).getDate("createdAt")),results.get(i).getString("venue"),results.get(i).getString("describe")));
                }
                firstRowAsTitle();
            }
        });
    }

    //将第一行作为标题
    private void firstRowAsTitle() {
        //fields是表格中要显示的数据对应到Content类中的成员变量名，其定义顺序要与表格中显示的相同
        final String[] fields = {"case_num", "case_time", "case_location", "case_describe"};
        tableLayout.setAdapter(new TableAdapter() {
            @Override
            public int getColumnCount() {
                return fields.length;
            }

            @Override
            public String[] getColumnContent(int position) {
                int rowCount = contentList.size();
                String contents[] = new String[rowCount];
                try {
                    Field field = Content.class.getDeclaredField(fields[position]);
                    field.setAccessible(true);
                    for (int i = 0; i < rowCount; i++) {
                        contents[i] = (String) field.get(contentList.get(i));
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return contents;
            }
        });
    }

    //将第一列作为标题
    private void firstColumnAsTitle() {
        tableLayout.setAdapter(new TableAdapter() {
            @Override
            public int getColumnCount() {
                return contentList.size();
            }

            @Override
            public String[] getColumnContent(int position) {
                return contentList.get(position).toArray();
            }
        });
    }

    public static class Content {

        private String case_num;
        private String case_time;
        private String case_location;
        private String case_describe;

        public Content(String case_num, String case_time, String case_location, String case_describe) {
            this.case_num = case_num;
            this.case_time = case_time;
            this.case_location = case_location;
            this.case_describe = case_describe;
        }

        public String[] toArray() {
            return new String[]{case_num, case_time, case_location, case_describe};
        }

    }

    public static HistoryCaseFragment newInstance() {
        return new HistoryCaseFragment();
    }

    private String insertNextlinePerChar(String base,int perchar){
        StringBuffer s1=new StringBuffer(base);
        for(int index=perchar;index<s1.length();index+=(perchar+1)){
            if (Pattern.compile("[0-9]*").matcher(String.valueOf(s1.charAt(index))).matches())
            {
                index=index-perchar;
                continue;
            }
            s1.insert(index, '\n');
        }
        return s1.toString();
    }
}
