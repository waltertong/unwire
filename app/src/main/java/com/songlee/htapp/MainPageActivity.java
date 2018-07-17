package com.songlee.htapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainPageActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private ListView infoListView;
    private List<Bean> list = new ArrayList<>();
    private List<Bean> listTemple = new ArrayList<>();
    private String url_first_half = "https://unwire.hk/articles/";
    private String url_second_half = "page/";
    private String next_page_url = "";
    private int currentPage = 1;
    private int currentPosition;
    private String url;
    private ProgressDialog dialog;
    // popupWindow
    private PopupWindow popupWindow;
    private ListView menuListView;
    private int lastLastitem = 0;
    boolean firstIn = true;
    private menuListAdapter adapter;
    int index;
    int top;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        infoListView = (ListView) findViewById(R.id.info_list_view);
    }


    public void showPopUpOnce(){
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                    .putBoolean("isFirstRun", false).commit();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                    .putBoolean("isFirstRun", true).commit();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("hi").setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }




    }

    @Override
    public void onBackPressed() {
      finish();
    }


    // Check network allowance
    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
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


    public void switchOver(final int position) {
//        showPopUpOnce();
        if (isNetworkAvailable(MainPageActivity.this)) {
            // display the dialog
            dialog = new ProgressDialog(this);
            dialog.setMessage("Connecting.....");
            dialog.setCancelable(false);
//            dialog.show();
            url = url_first_half + url_second_half + currentPage;
        } else {
            // Popup the dialog
            new AlertDialog.Builder(MainPageActivity.this)
                    .setTitle("Warning")
                    .setMessage("No Network！!!")
                    .setPositiveButton("Pls try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switchOver(position);
                        }
                    }).setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // Exit the apps
                }
            }).show();
        }
    }

    public void checkNetworkAvailable(){
        if (!isNetworkAvailable(MainPageActivity.this)){
            // Popup the dialog
            new AlertDialog.Builder(MainPageActivity.this)
                    .setTitle(Constants.GENERAL_ERROR_TITLE)
                    .setMessage(Constants.NO_NEWTORK_MESSAGE)
                    .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkNetworkAvailable();
                        }
                    }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // Exit the apps
                }
            }).show();
        }

    }
    @Override
    public void onPause(){

        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
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
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
//        currentPosition = position;
//        currentPage = 1;
//        switchOver(position);
        checkNetworkAvailable();
    }

    public void onSectionAttached(int number) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        PageFragment pageFragment = new PageFragment();
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.MAINPAGE_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.MOBILE_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.NOTEBOOK_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.AUDIO_VISUAL_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.MOBILE_MUSIC_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.DC_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 7:
                mTitle = getString(R.string.title_section7);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.REVIEW_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 8:
                mTitle = getString(R.string.title_section8);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.TIPS_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 9:
                mTitle = getString(R.string.title_section9);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.IOS_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 10:
                mTitle = getString(R.string.title_section10);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.ANDROID_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 11:
                mTitle = getString(R.string.title_section11);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.PC_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 12:
                mTitle = getString(R.string.title_section12);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.MAC_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 13:
                mTitle = getString(R.string.title_section13);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.WINDOWS_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 14:
                mTitle = getString(R.string.title_section14);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.TECHPLUS_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 15:
                mTitle = getString(R.string.title_section15);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.BIKE_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 16:
                mTitle = getString(R.string.title_section16);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.PRETTY01_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 17:
                mTitle = getString(R.string.title_section17);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.SPORT_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 18:
                mTitle = getString(R.string.title_section18);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.LOVE_YOUR_CHILD_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 19:
                mTitle = getString(R.string.title_section19);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.DRONE_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 20:
                mTitle = getString(R.string.title_section20);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.WIRELESS_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 21:
                mTitle = getString(R.string.title_section21);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.TRAVEL_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 22:
                mTitle = getString(R.string.title_section22);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.GAME_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 23:
                mTitle = getString(R.string.title_section23);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.HOTTOPIC_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 24:
                mTitle = getString(R.string.title_section24);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.SOCIAL_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 25:
                mTitle = getString(R.string.title_section25);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.MOVIE_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 26:
                mTitle = getString(R.string.title_section26);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.GREEN_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            case 27:
                mTitle = getString(R.string.title_section27);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.COLUMN_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case 28:
                mTitle = getString(R.string.title_section28);
                setTitle(mTitle);
                bundle.putString(Constants.FRAGEMENT_KEY, Constants.ARCHIVE_FRAGEMENT_URL);
                pageFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, pageFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public interface FragmentRefreshListener{
        void onRefresh();
    }

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    private FragmentRefreshListener fragmentRefreshListener;

    public void refresh() {
        if (isNetworkAvailable(MainPageActivity.this)) {
            //refresh
            dialog = new ProgressDialog(this);
            dialog.setMessage("Refreshing...");
            dialog.setCancelable(false);
            if(getFragmentRefreshListener()!=null){
                getFragmentRefreshListener().onRefresh();
            }
        } else {
            // show dialog
            new AlertDialog.Builder(this)
                    .setTitle(Constants.GENERAL_ERROR_TITLE)
                    .setMessage(Constants.NO_NEWTORK_MESSAGE)
                    .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkNetworkAvailable();
                        }
                    }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);  // Exit the apps
                }
            }).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                refresh();
                return true;
//            case R.id.more:
//                if (popupWindow.isShowing())
//                    popupWindow.dismiss();
//                else
//                    popUp();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPopupWindow() {
        View view = getLayoutInflater().inflate(R.layout.popup_window, null);
        menuListView = (ListView) view.findViewById(R.id.popup_list_view);
        popupWindow = new PopupWindow(view, 160, WindowManager.LayoutParams.WRAP_CONTENT);
        // 数据
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("menu_text", "Previous Page");
        data.add(map);
        map = new HashMap<>();
        map.put("menu_text", "Next Page");
        data.add(map);
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.menu_list_item,
                new String[]{"menu_text"}, new int[]{R.id.menu_text});
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        prePage();  // Previous Page
                        break;
                    case 1:
//                        nextPage(); // Next Page
                }
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }





    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            ((MainPageActivity) context).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));

        }
    }

}
