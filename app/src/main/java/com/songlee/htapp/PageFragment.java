package com.songlee.htapp;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class PageFragment extends AbstractFragment implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private PageFragment activity;
    private ListView infoListView;
    private List<Bean> list = new ArrayList<>();
    private List<Bean> listTemple = new ArrayList<>();
    private String url_first_half = "";
    private String url_second_half = "page/";
    private String next_page_url = "";
    private int currentPage = 1;
    private int currentPosition;
    private String url;
    private View mProgressBarFooter;
    View v;
    private ProgressDialog dialog;
    // popupWindow
    private PopupWindow popupWindow;
    private ListView menuListView;
    private String[] menuNameTemple;
    private int lastLastitem = 0;
    private Handler mHandler;
    boolean firstIn = true;
    boolean refresh_flage = false;
    private menuListAdapter adapter;
    int index;
    int top;
    AlertDialog.Builder alertBuilder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        thisContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.first_main, container, false);
        alertBuilder = new AlertDialog.Builder(v.getContext());
        url_first_half = getArguments().getString(Constants.FRAGEMENT_KEY);

        refresh();
        mProgressBarFooter = ((LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.progress_bar, null, false);
        infoListView = (ListView) v.findViewById(R.id.info_list_view);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
         mHandler = new Handler();
        makeRequest(1);
    }

    public List<Bean> getData() {
        List<Bean> listget = new ArrayList<>();
        listget.addAll(list);
        return listget;
    }

    public void setData(List<Bean> data) {
        list = data;
    }

public void refresh(){
    ((MainPageActivity) getActivity()).setFragmentRefreshListener(new MainPageActivity.FragmentRefreshListener() {
        @Override
        public void onRefresh() {
            dialog=new ProgressDialog(thisContext);
            dialog.show();
            // Refresh  Fragment
            refresh_flage=true;
            list.clear();
            listTemple.clear();
            lastLastitem = 0;
            adapter.notifyDataSetChanged();
            infoListView.removeFooterView(mProgressBarFooter);
            new Thread(runnable).start();


        }
    });
}
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Connection conn = Jsoup.connect(url);
            conn.header("User-Agent", Constants.HTML_HEADER);
            conn.timeout(Constants.TIME_OUT);
            // broswer header desktop device
            Document doc = null;
            try {
                doc = conn.get();

            currentPage = Integer.parseInt(doc.select("body div div div div nav span.page-numbers.current").text());
            String nextPageURL = doc.select("body div div div div a.page-numbers").first().attr("abs:href");
            Elements elements1 = doc.select("body div div div ul div ");

            int limtTopic = 20;
            for (int i = 0; i < limtTopic; i++) {
                Element element = elements1.get(i);
                String itemLink = element.select("a").attr("href").toString();
                String menuName = element.select("a").attr("title").toString();
                String imageName = element.select("img").attr("src").toString();
                Bean bean = new Bean();
                bean.setItemUrl(itemLink);
                bean.setText(menuName);
                bean.setUrl(imageName);
                listTemple.add(bean);
            }
            handler.sendEmptyMessage(0);
            setData(listTemple);
            Log.d("itemList item number", String.valueOf(listTemple.size()));
            } catch (Exception e) {
                e.printStackTrace();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(thisContext)
                                .setTitle(Constants.GENERAL_ERROR_TITLE)
                                .setMessage(Constants.NO_NEWTORK_MESSAGE)
                                .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(runnable).start();
                                    }
                                }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);  // Exit the apps
                            }
                        }).show();
                    }
                });
            }
        }
    };


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // send the hander after network connected
            initView();
        }
    };

    @Override
    public void onPause() {

        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void initView() {
        if( dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
        if(firstIn){
            infoListView.addFooterView(mProgressBarFooter);

        }else if(refresh_flage){
            infoListView.addFooterView(mProgressBarFooter);
            refresh_flage=false;
            dialog.dismiss();
        }

        if (list.isEmpty()) {

        } else {
            list = getData();

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (firstIn) {
                        adapter = new menuListAdapter(thisContext, list);
                        if (adapter.isEmpty()) {
                            // Popup the dialog
                            new AlertDialog.Builder(thisContext)
                                    .setTitle(Constants.GENERAL_ERROR_TITLE)
                                    .setMessage(Constants.NO_NEWTORK_MESSAGE)
                                    .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            makeRequest(0);
                                        }
                                    }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);  // Exit the apps
                                }
                            }).show();
                        } else {
                            infoListView.setAdapter(adapter);
                        }

                        infoListView.setSelection(lastLastitem);
                        adapter.notifyDataSetChanged();
                        firstIn = false;
                    } else {
                        adapter.refreshList(list);
                        infoListView.setSelection(lastLastitem - 8);
                        adapter.notifyDataSetChanged();
                    }

                    infoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String itemURL = list.get(i).getItemUrl();
                            Intent intent = new Intent(thisContext, ItemActivity.class);
                            intent.putExtra("itemURL", itemURL);
//                            intent.putExtra("mTitle", getTitle());
                            startActivity(intent);
                            Log.d("ItemURL=", itemURL);
                        }
                    });


                    infoListView.setOnScrollListener(new AbsListView.OnScrollListener() {

                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            if (infoListView.getAdapter() == null)
                                return;

                            if (infoListView.getAdapter().getCount() == 0)
                                return;

//                            if(!firstIn) {
//                                if (firstVisibleItem == 0) {
//                                    // check if we reached the top or bottom of the list
//                                    View v = infoListView.getChildAt(0);
//                                    int offset = (v == null) ? 0 : v.getTop();
//                                    if (offset == 0) {
//                                        // reached the top:
//                                        refresh();
//                                        return;
//                                    }
//                                }
//                            }
                            final int lastItem = firstVisibleItem + visibleItemCount;
                            if (lastItem == totalItemCount) {
                                if (lastLastitem != lastItem) { //to avoid multiple calls for last item, declare it as a field in your Activity
                                    lastLastitem = lastItem;
//                                    infoListView.addFooterView(mProgressBarFooter);

                                    nextPage();
//                                    infoListView.removeFooterView(mProgressBarFooter);
                                    new Handler().post(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    infoListView.setSelection(lastLastitem);

                                                }
                                            });

                                    dialog.dismiss();
                                }
                            }

                        }


                    });
                }
            });

            infoListView.requestLayout();


        }
    }

//    private class Task extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        protected Void doInBackground(Void... voids) {
//            Log.d("APPS" + " PreExceute", "On pre Exceute......");
//            Connection conn = Jsoup.connect(url);
//            conn.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/    20100101 Firefox/32.0");
//            int time = 70000;
//            conn.timeout(time);
//            // broswer header desktop device
//            Document doc = null;
//            try {
//                doc = conn.get();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            currentPage = Integer.parseInt(doc.select("body div div div div nav span.page-numbers.current").text());
//            String nextPageURL = doc.select("body div div div div a.page-numbers").first().attr("abs:href");
//            Elements elements1 = doc.select("body div div div ul div ");
//
//            int limtTopic = 20;
//            for (int i = 0; i < limtTopic; i++) {
//                Element element = elements1.get(i);
//                String itemLink = element.select("a").attr("href").toString();
//                String menuName = element.select("a").attr("title").toString();
//                String imageName = element.select("img").attr("src").toString();
//                Bean bean = new Bean();
//                bean.setItemUrl(itemLink);
//                bean.setText(menuName);
//                bean.setUrl(imageName);
//                listTemple.add(bean);
//            }
//
//            setData(listTemple);
//            Log.d("itemList item number", String.valueOf(listTemple.size()));
//            return null;
//        }
//    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PageFragment.PlaceholderFragment.newInstance(position + 1))
                .commit();
        currentPosition = position;
        currentPage = 1;

    }


//    public void prePage() {
//        if (isNetworkAvailable()) {
//            if (currentPage == 1)
//                Toast.makeText(thisContext, "已經是第一頁了", Toast.LENGTH_SHORT).show();
//            else {
//                --currentPage;
//                doNext(currentPosition);
//            }
//        } else {
//            new AlertDialog.Builder(thisContext)
//                    .setTitle("上一页")
//                    .setMessage("当前没有网络连接！")
//                    .setPositiveButton("重试", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            prePage();
//                        }
//                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    System.exit(0);
//                }
//            }).show();
//        }
//    }


    public void nextPage() {
        if (isNetworkAvailable()) {
            if (next_page_url.equals("#"))
                Toast.makeText(thisContext, Constants.LAST_PAGE_MESSAGE, Toast.LENGTH_SHORT).show();
            else {
                currentPage++;
                makeRequest(currentPosition);
            }
        } else {
            new AlertDialog.Builder(thisContext)
                    .setTitle(Constants.GENERAL_ERROR_TITLE)
                    .setMessage(Constants.NO_NEWTORK_MESSAGE)
                    .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nextPage();
                        }
                    }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // Exit the apps
                }
            }).show();
        }
    }

    // Check network allowance
    public boolean isNetworkAvailable() {
        Context context = thisContext;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        else {
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++)
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
            }
        }
        return false;
    }

    public void makeRequest(final int position) {
        if (isNetworkAvailable()) {
            // display the dialog

            showDialog();
            url = url_first_half + url_second_half + currentPage;
            new Thread(runnable).start();  //new Thread
//            Task task = new Task();
//            task.execute();
        } else {
            // Popup the dialog
            // Popup the dialog
            new AlertDialog.Builder(thisContext)
                    .setTitle(Constants.GENERAL_ERROR_TITLE)
                    .setMessage(Constants.NO_NEWTORK_MESSAGE)
                    .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            makeRequest(position);
                        }
                    }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // Exit the apps
                }
            }).show();
        }
    }

    public void showDialog(){
        dialog = new ProgressDialog(thisContext);
        dialog.setMessage(Constants.LOADING_MESSAGE);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static class PlaceholderFragment extends AbstractFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PageFragment.PlaceholderFragment newInstance(int sectionNumber) {
            PageFragment.PlaceholderFragment fragment = new PageFragment.PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }


    }
}
