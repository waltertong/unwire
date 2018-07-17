package com.songlee.htapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Walter on 02/04/2017.
 */

public class ItemActivity extends ActionBarActivity {
    String itemURL;
    public static String newline = System.getProperty("line.separator");
    private Map<String, Object> contentMap = new HashMap<String, Object>();
    HashMap<Integer, List<String>> hmap = new HashMap<>();
    ;
    private ProgressDialog dialog;
    private ItemActivity activity;
    private List<String> content = new ArrayList<>();
    private String mTitle;
    private Context context;
    private boolean isYoutubeVideo = false;
    private boolean hasTriangleImage = false;
    private LinearLayout ln;
    private LinearLayout youTubeLn;
    private ProgressBar progressBar;
    private TextView titleTextView;
    //        private JCVideoPlayerStandard facebookVideoView;
    private RoundedWebView facebookVideoView;
    private List<String> imageURLArrayList;
    private List<String> contentArrayList;
    private List<String> contentTitleArrayList;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    public static final String DEVELOPER_KEY = "AIzaSyC9ZnLf206JodcZlxMNQWPea9nN6QnVQ0k";
    public static String YOUTUBE_VIDEO_CODE = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_main);
        showLoadingProgress();
        this.itemURL = getIntent().getStringExtra("itemURL");
        this.mTitle = getIntent().getStringExtra("mTitle");
        this.context = getApplicationContext();
        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubeplayerfragment);

        this.titleTextView = (TextView) this.findViewById(R.id.titleTextView);
        this.ln = (LinearLayout) this.findViewById(R.id.linlay);
        this.youTubeLn = (LinearLayout) this.findViewById(R.id.youtubelinlay);
//        this.facebookVideoView = (JCVideoPlayerStandard) this.findViewById(R.id.video_view);
        this.facebookVideoView = (RoundedWebView) this.findViewById(R.id.video_view);
        this.progressBar = (ProgressBar) this.findViewById(R.id.fbVideo_progressBar);


        facebookVideoView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO show you progress image
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                facebookVideoView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO hide your progress image
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                facebookVideoView.setVisibility(View.VISIBLE);
            }

        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkNetworkAvailable();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showLoadingProgress() {
        dialog = new ProgressDialog(this);
        dialog.setMessage(Constants.LOADING_MESSAGE);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissLoadingProgress() {
        dialog.dismiss();
    }

    public void checkNetworkAvailable() {
        if (isNetworkAvailable(ItemActivity.this)) {
            new Thread(runnable).start();
        } else {
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


//    private void initView() {
//        dismissLoadingProgress();
//
//        if (contentMap.isEmpty()) {
//            new AlertDialog.Builder(this)
//                    .setTitle(Constants.GENERAL_ERROR_TITLE)
//                    .setMessage(Constants.GENERAL_ERROR_MESSAGE)
//                    .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            new Thread(runnable).start();
//                        }
//                    }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    System.exit(0);  // Exit the apps
//                }
//            }).show();
//        } else {
//
//            final String title = (String) contentMap.get("title");
//            final String description = (String) contentMap.get("description");
//            final String content = (String) contentMap.get("content");
//            final String contentTitle = (String) contentMap.get("contentTitle");
//            final String video = (String) contentMap.get("video");
//
//
//            this.runOnUiThread(new Runnable() {
//                public void run() {
//
//                    //ImageView Setup
//
//                    TextView contextTextView = new TextView(ItemActivity.this);
//                    LinearLayout.LayoutParams contextTextViewParams = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT
//                    );
//
//
//                    titleTextView.setText(title);
//                    contextTextView.setLayoutParams(contextTextViewParams);
//
//                    ln.addView(contextTextView);
//                    if (video != null && video != "") {
//                        if (isYoutubeVideo) {
//                            YOUTUBE_VIDEO_CODE = video;
//                            youTubeLn.setVisibility(View.VISIBLE);
//                            addYouTubeView();
//                        } else {
//                            progressBar.setIndeterminate(true);
//                            progressBar.setVisibility(View.VISIBLE);
//                            addWebView(video);
//                        }
//                    }
//
//
////                    facebookVideoView.setUp("https://www.facebook.com/unwirehk/videos/10154789924819103/"
////                            , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,title);
////
//
//                    if (contentArrayList.size() != 0) {
//                        int count = 0;
//                        Log.d("App", "contentArrayList Size = " + contentArrayList.size());
//                        Log.d("App", "imageURLArrayList Size = " + imageURLArrayList.size());
//                        for (String space : contentArrayList) {
//                            if (space == null) {
//                                count++;
//                            }
//                        }
//                        Log.d("App", "contentArrayList Space Size count = " + count);
//
//
//                        for (int i = 0; i < contentArrayList.size(); i++) {
//                            int lastElement = contentArrayList.size() - 3;
//                            String content = contentArrayList.get(i);
//
//
//                                if (content.contains(Constants.TRIANGLE_IMAGE)) {
//                                    if (imageURLArrayList != null && imageURLArrayList.size() != 0 && imageURLArrayList.size() > i) {
//                                        if (content.contains(Constants.TRIANGLE_IMAGE)) {
//                                            ln.addView(addImageView(imageURLArrayList.get(i - 1)));
//                                            ln.addView(addTextView(content));
//                                        }
//                                    }
//                                } else {
//                                    ln.addView(addTextView(content));
//                                    if (contentTitleArrayList != null && contentTitleArrayList.size() != 0 && contentTitleArrayList.size() > i) {
//                                        String contentTitle = contentTitleArrayList.get(i);
//                                        contentTitle = contentTitle + newline;
//                                        ln.addView(addBoldTextView(contentTitle));
//                                    }
//                                    if (i == lastElement) {
//                                        if (imageURLArrayList != null && imageURLArrayList.size() != 0 && imageURLArrayList.size() > i) {
//                                            for (int index = i; index < imageURLArrayList.size(); index++) {
//                                                ln.addView(addImageView(imageURLArrayList.get(index)));
//                                            }
//                                        }
//                                    }
//                                    if (i < 1) {
//                                        if (i < lastElement) {
//                                            if (imageURLArrayList != null && imageURLArrayList.size() != 0 && imageURLArrayList.size() > i) {
//                                                ln.addView(addImageView(imageURLArrayList.get(i)));
//                                            }
//                                        }
//                                    }
//
//
//
//                                continue;
//                            }
//
//                            ln.addView(addTextView(content));
//
//                            if (content == null) {
//                                Object imageList  = hmap.get(i);
//                                for(int index=0;index<imageList.size();index++){
//                                    String a= (String) imageList.get(index);
//                                    Log.d("App", "imageList.get(" + index + ") = " + a);
//                                }
//
//                            }
//                            content = content + newline;
//
//                            if (contentTitleArrayList != null && contentTitleArrayList.size() != 0 && contentTitleArrayList.size() > i) {
//                                String contentTitle = contentTitleArrayList.get(i);
//                                contentTitle = contentTitle + newline;
//                                ln.addView(addBoldTextView(contentTitle));
//                            }
//
//
//                            if (i == lastElement) {
//                                if (imageURLArrayList != null && imageURLArrayList.size() != 0 && imageURLArrayList.size() > i) {
//                                    for (int index = i; index < imageURLArrayList.size(); index++) {
//                                        ln.addView(addImageView(imageURLArrayList.get(index)));
//                                    }
//                                }
//                            } else if (i < lastElement) {
//                                if (imageURLArrayList != null && imageURLArrayList.size() != 0 && imageURLArrayList.size() > i) {
//                                    ln.addView(addImageView(imageURLArrayList.get(i)));
//                                }
//                            }
//
//                        }
//                    }
//                }
//
//            });
//
//        }
//    }

    private void initView() {
        dismissLoadingProgress();

        if (contentMap.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle(Constants.GENERAL_ERROR_TITLE)
                    .setMessage(Constants.GENERAL_ERROR_MESSAGE)
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
        } else {

            final String title = (String) contentMap.get("title");
            final String video = (String) contentMap.get("video");
//final ArrayList imageList= (ArrayList) hmap.get() ;

            this.runOnUiThread(new Runnable() {
                public void run() {

                    //ImageView Setup

                    TextView contextTextView = new TextView(ItemActivity.this);
                    LinearLayout.LayoutParams contextTextViewParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );


                    titleTextView.setText(title);
                    contextTextView.setLayoutParams(contextTextViewParams);

                    ln.addView(contextTextView);
                    if (video != null && video != "") {
                        if (isYoutubeVideo) {
                            YOUTUBE_VIDEO_CODE = video;
                            youTubeLn.setVisibility(View.VISIBLE);
                            addYouTubeView();
                        } else {
                            progressBar.setIndeterminate(true);
                            progressBar.setVisibility(View.VISIBLE);
                            addWebView(video);
                        }
                    }

                    if (contentArrayList.size() != 0) {
                        int count = 0;
                        Log.d("App", "contentArrayList Size = " + contentArrayList.size());
                        Log.d("App", "imageURLArrayList Size = " + imageURLArrayList.size());
                        for (String space : contentArrayList) {
                            if (space == null) {
                                count++;
                            }
                        }
                        Log.d("App", "contentArrayList Space Size count = " + count);


                        for (int i = 0; i < contentArrayList.size(); i++) {
                            if(i==0){
                                continue;
                            }
                            int lastElement = contentArrayList.size() - 3;
                            String content = contentArrayList.get(i);

                            if (content == null) {

                                final ArrayList imageList = (ArrayList) hmap.get(i);
                                Log.d("App", "imageList content = " + imageList);
                                if (imageList != null) {
                                    Iterator itr = imageList.iterator();
                                    while (itr.hasNext()) {
                                        Object element = itr.next();
                                        Object[] array = {element};
                                        for (Object imageUrl : array) {
                                            Log.d("App", "index " + i + " : imageUrl = " + imageUrl);
                                            ln.addView(addImageView(imageUrl.toString()));
                                        }
                                    }

                                }
                            } else if (content != null) {
                                content += newline;
                                ln.addView(addTextView(content));
                            }
                        }
                    }
                }

            });

        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            Connection conn = Jsoup.connect(itemURL);
            Log.d("App", "itemURL = " + itemURL);
            conn.header("User-Agent", Constants.HTML_HEADER);
            // broswer header desktop device
            conn.timeout(Constants.TIME_OUT);
            Document doc = null;
            try {
                doc = conn.get();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }


            Elements elements = doc.getAllElements();
            for (int i = 0; i < 1; i++) {
                Element element = elements.get(i);
                String titleAndUnwire = element.select("meta[property=og:title]").attr("content").toString();
                String description = element.select("meta[property=og:description]").attr("content").toString();
                Elements imageElements = element.select("div div div div div p img");
                //imageURL = element.select("div div div div div p img").attr("src").toString();
                Elements contentTitleElements = element.select("div div div div div h3 strong");
//                String content = element.select("div div div div div p:not([class]):not(:has(a)):not(:has(span))").text();
//                Elements contentElements = element.select("div div div div div p:not([class])");
                Elements contentElements = element.select("div div div div div p");
                Elements secondContentElements = element.select("div div div div div p[class='p1']");
                String video = element.select("div div div div div p iframe").attr("src").toString();


                String[] titleTemple = titleAndUnwire.split("-");
                String title = titleTemple[0];

                if (video != null && video != "") {
                    if (video.contains("youtube")) {
                        isYoutubeVideo = true;
                        String questionMark = "\\?";
                        String[] youTubeUrl = video.split("embed/");
                        String[] youTubeUrlTemple = youTubeUrl[1].split(questionMark);
                        video = youTubeUrlTemple[0];
                    } else if (video.contains("facebook")) {
                        isYoutubeVideo = false;
                    }
                } else {
                    video = null;
                }

                contentMap.put("title", title);
                contentMap.put("description", description);
                //contentMap.put("imageURL", imageURL);
//                contentMap.put("contentTitle", contentTitle);
                contentMap.put("video", video);
                imageURLArrayList = new ArrayList<String>();
                contentArrayList = new ArrayList<String>();
                contentTitleArrayList = new ArrayList<String>();

                for (int a = 0; a < imageElements.size(); a++) {
                    Element imageElement = imageElements.get(a);
                    String imageURL = imageElement.attr("src");
                    imageURLArrayList.add(imageURL);
                }


                for (int index = 0; index < contentElements.size(); index++) {

                    String contentElement = contentElements.get(index).text();
                    contentElement = StringUtils.stripToEmpty(contentElement);
                    if (contentElement.isEmpty()
                            || !StringUtils.isNotBlank(contentElement)
                            || contentElement.trim().length() <= 0
                            || contentElement.matches(("\\s"))) {
                        contentElement = null;
                        Element contentWithPicElement = contentElements.get(index);
                        Elements imageElement = contentWithPicElement.select("img");
                        List<String> imageList = new ArrayList<>();
                        for (Element imageLink : imageElement) {
                            String imageUrl = imageLink.attr("src");
                            imageList.add(imageUrl);
                        }
                        Log.d("App", "No. " + index + " of imageList = " + imageList + "\n" + "imageList size= " + imageList.size());
                        Log.d("App", "Put key = " + index);
                        Log.d("App", "Put value = " + imageList);
                        Log.d("App", "No. " + index + " of <p> = " + contentWithPicElement);
                        hmap.put(index, imageList);
                    }

                    if (contentElement!=null&&!contentElement.isEmpty()&&contentElement.contains("Tags")) {
                        break;
                    }
                    contentArrayList.add(contentElement);
                }
                for (Integer no : hmap.keySet()) {
                    Log.d("App", "hmap key = " + no + "\n" + "hmap value = " + hmap.get(no));
                }

                for (int index = 0; index < contentTitleElements.size(); index++) {
                    String contentTitleElement = contentTitleElements.get(index).text();
                    contentTitleArrayList.add(contentTitleElement);
                }

            }


            handler.sendEmptyMessage(0);
            initView();
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // send the hander after network connected
        }
    };

    public static boolean isStringNullOrWhiteSpace(String value) {
        if (value == null) {
            return true;
        }

        for (int i = 0; i < value.length(); i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public FrameLayout addImageView(String url) {
//        ProgressBar progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleSmall);

        FrameLayout layout = new FrameLayout(this);
        final ProgressBar progressBar = new ProgressBar(ItemActivity.this, null, android.R.attr.progressBarStyleLarge);
        final ImageView imageViewTemple = new ImageView(ItemActivity.this);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.TOP;
        layout.addView(progressBar, params);
        imageViewTemple.setPadding(0, 0, 0, 20);
        layout.addView(imageViewTemple);
        imageViewTemple.setVisibility(View.GONE);
//        imageViewTemple.setImageResource(R.drawable.progress_animation);
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                Log.d("App", "onBitmapLoaded");
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                    int newHeight = displayMetrics.heightPixels;
                int newWidth = displayMetrics.widthPixels;

                float scaleFactor = (float) newWidth / (float) imageWidth;
                int newHeight = (int) (imageHeight * scaleFactor);

                Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(bitmap, newWidth, newHeight);


//                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                imageViewTemple.setImageBitmap(thumbBitmap);
                progressBar.setVisibility(View.GONE);
                imageViewTemple.setVisibility(View.VISIBLE);
                Log.e("App", "Success to load image in onBitmapLoaded method");
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {
                Log.e("App", "Failed to load image in onBitmapFailed method");
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.e("App", "Prepare to load image in onPrepareLoad method");
            }
        };

        try {
            System.gc();

            Picasso.with(context).load(url).into(target);
            imageViewTemple.setTag(target);

        } catch (Exception e) {
            Log.e("App", "Picasso Failed");
        }


        imageViewTemple.setAdjustViewBounds(true);
        imageViewTemple.setScaleType(ImageView.ScaleType.FIT_XY);
        ;
        return layout;
    }


    public TextView addTextView(String text) {
        TextView textViewTemple = new TextView(ItemActivity.this);
        textViewTemple.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewTemple.setText(text);
        return textViewTemple;
    }

    public void addYouTubeView() {

        youTubePlayerFragment.initialize(DEVELOPER_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                youTubePlayer.cueVideo(YOUTUBE_VIDEO_CODE);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                if (youTubeInitializationResult.isUserRecoverableError()) {
                    youTubeInitializationResult.getErrorDialog(activity, RECOVERY_DIALOG_REQUEST).show();
                } else {
                    String errorMessage = String.format(
                            getString(R.string.error_player), youTubeInitializationResult.toString());
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void addWebView(String url) {
        WebSettings settings = facebookVideoView.getSettings();
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        facebookVideoView.getSettings().setJavaScriptEnabled(true);
        facebookVideoView.setBackgroundColor(0);
        facebookVideoView.loadUrl(url);
        facebookVideoView.setVisibility(View.VISIBLE);
    }

    public TextView addBoldTextView(String text) {
        TextView textViewTemple = new TextView(ItemActivity.this);
        textViewTemple.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewTemple.setTextSize(15);
        textViewTemple.setTypeface(null, Typeface.BOLD);
        textViewTemple.setText(text);
        return textViewTemple;
    }


//    public void refresh() {
//        if (isNetworkAvailable(ItemActivity.this)) {
//            //refesh
//            dialog = new ProgressDialog(this);
//            dialog.setMessage(Constants.LOADING_MESSAGE);
//            dialog.setCancelable(false);
//            dialog.show();
//            //
////            list.clear();
//            new Thread(runnable).start();
//        } else {
//            // show dialog
//            new AlertDialog.Builder(this)
//                    .setTitle(Constants.GENERAL_ERROR_TITLE)
//                    .setMessage(Constants.NO_NEWTORK_MESSAGE)
//                    .setPositiveButton(Constants.NO_NEWTORK_POSITIVE, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            checkNetworkAvailable();
//                        }
//                    }).setNegativeButton(Constants.NO_NEWTORK_NEGATIVE, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    System.exit(0);  // Exit the apps
//                }
//            }).show();
//        }
//    }


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

//    @Override
//    public void onPrepared(MediaPlayer mediaPlayer) {
//        String videoUrl = "url";
//
//        mVideoView.setVideoURI(Uri.parse(videoUrl));
//    }

//    @Override
//    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//        youTubePlayer.loadVideo(YOUTUBE_VIDEO_CODE);
//
//        // Hiding player controls
//        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
//
//    }
//
//    @Override
//    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//        if (youTubeInitializationResult.isUserRecoverableError()) {
//            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
//        } else {
//            String errorMessage = String.format(
//                    getString(R.string.error_player), youTubeInitializationResult.toString());
//            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
//        }
//    }
}
