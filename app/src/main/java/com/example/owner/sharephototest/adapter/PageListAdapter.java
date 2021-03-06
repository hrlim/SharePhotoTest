package com.example.owner.sharephototest.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.example.owner.sharephototest.R;
import com.example.owner.sharephototest.model.Album;
import com.example.owner.sharephototest.model.Page;

@SuppressLint({"SetJavaScriptEnabled"})
public class PageListAdapter extends RecyclerView.Adapter<PageListAdapter.ViewHolder> {

    private Context mContext;
    private Album mAlbum;

    private boolean mLoadingFinished = true;
    private boolean mRedirect = false;

    public PageListAdapter(Context context, Album album){
        mContext = context;
        mAlbum = album;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mAlbum.getPageCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.page_editor_page_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Page page = mAlbum.getPage(position);

        // Enable Javascript
        holder.webView.getSettings().setJavaScriptEnabled(true);
        holder.webView.getSettings().setLoadWithOverviewMode(true);
        holder.webView.getSettings().setUseWideViewPort(true);
        holder.webView.setHorizontalScrollBarEnabled(false);
        holder.webView.setVerticalScrollBarEnabled(false);
        holder.webView.getSettings().setBuiltInZoomControls(false);
        holder.webView.getSettings().setSupportZoom(false);
        holder.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        holder.webView.setInitialScale(1);

        // Add a WebViewClient
        holder.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (!mLoadingFinished) {
                    mRedirect = true;
                }
                mLoadingFinished = false;
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mLoadingFinished = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(!mRedirect){
                    mLoadingFinished = true;
                }

                if(mLoadingFinished && !mRedirect){
                    // inject data
                    for (int i = 0 ; i < page.getPictureCount() ; i++) {
                        injectStyleByScript(view, page.getLayout().getStylePath());
                        injectImageByScript(view, "_" + (i + 1), page.getPicture(i).getPath());
                    }

                    int height = view.getHeight();
                    int width = (210 * height / 297);
                    view.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                } else{
                    mRedirect = false;
                }
            }
        });

        holder.webView.loadUrl("file://" + page.getLayout().getFramePath());
    }

    private void injectStyleByScript(WebView view, String stylePath) {
        view.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var link = document.createElement('link');" +
                "link.rel = 'stylesheet';" +
                "link.href = '" + stylePath + "';" +
                "parent.appendChild(link)" +
                "})()");
    }

    private void injectImageByScript(WebView view, String elementId, String picturePath) {
        view.loadUrl("javascript:(function() {" +
                "var target = document.getElementById('" + elementId + "');" +
                "var img = document.createElement('img');" +
                "img.src = '" + picturePath + "';" +
                "target.appendChild(img);" +
                "})()");
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        WebView webView;

        ViewHolder(View itemView) {
            super(itemView);
            webView = (WebView) itemView.findViewById(R.id.page_list_item_webview);
        }
    }
}