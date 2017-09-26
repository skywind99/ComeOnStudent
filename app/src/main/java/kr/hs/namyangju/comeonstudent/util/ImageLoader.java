/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kr.hs.namyangju.comeonstudent.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.hs.namyangju.comeonstudent.R;

public class ImageLoader {
    // private static final String TAG = makeLogTag(ImageLoader.class);
    private static final ModelCache<String, GlideUrl> urlCache = new ModelCache<String, GlideUrl>(
            150);

    private final RequestManager.ImageModelRequest<String> mGlideModelRequest;
    private final CenterCrop mCenterCrop;

    private int mPlaceHolderResId = -1;
    private Context mContext;

    /**
     * Construct a standard ImageLoader object.
     */
    public ImageLoader(Context context) {
        mContext = context;
        VariableWidthImageLoader imageLoader = new VariableWidthImageLoader(
                context);
        mGlideModelRequest = Glide.with(context).using(imageLoader);
        mCenterCrop = new CenterCrop(Glide.get(context).getBitmapPool());
    }

    /**
     * Construct an ImageLoader with a default placeholder drawable.
     */
    public ImageLoader(Context context, int placeHolderResId) {
        this(context);
        mContext = context;
        mPlaceHolderResId = placeHolderResId;
    }

    /**
     * Load an image from a url into an ImageView using the default placeholder
     * drawable if available.
     *
     * @param url             The web URL of an image.
     * @param imageView       The target ImageView to load the image into.
     * @param requestListener A listener to monitor the request result.
     */
    public void loadImage(String url, ImageView imageView,
                          RequestListener requestListener) {
        String mUrl;
        if (url.startsWith("http")) {
            mUrl = url;
        } else if (url.startsWith("www") || url.startsWith("14.63")
                || url.startsWith("dinnerqueen")) {
            mUrl = "http://" + url;
        } else {
            mUrl = url;
        }
        loadImage(mUrl, imageView, requestListener, null, false);
    }

    public void loadImage(String url, ImageView imageView,
                          RequestListener requestListener, int errorResourceId) {
        loadImage(url, imageView, requestListener, null, errorResourceId, false);
    }

    /**
     * Load an image from a url into an ImageView using the given placeholder
     * drawable.
     *
     * @param url                The web URL of an image.
     * @param imageView          The target ImageView to load the image into.
     * @param requestListener    A listener to monitor the request result.
     * @param placholderOverride A placeholder to use in place of the default placholder.
     */
    public void loadImage(String url, ImageView imageView,
                          RequestListener requestListener, Drawable placholderOverride) {
        loadImage(url, imageView, requestListener, placholderOverride, false /* crop */);
    }

    public void loadImage(String url, ImageView imageView,
                          RequestListener requestListener, Drawable placholderOverride,
                          int errorResourceId) {
        loadImage(url, imageView, requestListener, placholderOverride,
                errorResourceId, false /* crop */);
    }

    public void loadImage(String url, ImageView imageView,
                          RequestListener requestListener, int placeHolderResId,
                          int errorResourceId) {
        mPlaceHolderResId = placeHolderResId;
        // gif 파일의 경우 2.3에서 정상적으로 디코딩이 안되는 문제가 발생하여 이미지로딩을 막는다
        // if(url != null && url.toLowerCase().endsWith(".gif")){
        // url = null;
        // }

        loadImage(url, imageView, requestListener, null, errorResourceId, false /* crop */);
    }

    /**
     * Load an image from a url into an ImageView using the default placeholder
     * drawable if available.
     *
     * @param url                 The web URL of an image.
     * @param imageView           The target ImageView to load the image into.
     * @param requestListener     A listener to monitor the request result.
     * @param placeholderOverride A drawable to use as a placeholder for this specific image. If
     *                            this parameter is present, {@link #mPlaceHolderResId} if
     *                            ignored for this request.
     */
    public void loadImage(String url, ImageView imageView,
                          RequestListener requestListener, Drawable placeholderOverride,
                          boolean crop) {
        DrawableRequestBuilder<String> request = beginImageLoad(url,
                requestListener, crop).animate(R.anim.image_fade_in);
        if (placeholderOverride != null) {
            request.placeholder(placeholderOverride);
        } else if (mPlaceHolderResId != -1) {
            request.placeholder(mPlaceHolderResId);
        }
        if (imageView != null) {
            request.into(imageView);
        }
    }

    public void loadImageRound(final String url, final ImageView imageView,
                               RequestListener requestListener,
                               final Drawable placeholderOverride, final boolean crop) {
        // if(requestListener == null){
        // final RequestListener finalRequestListener = requestListener;
        // requestListener = new RequestListener() {
        // @Override
        // public boolean onException(Exception e, Object o, Target target,
        // boolean b) {
        //
        // if(e !=null &&e.toString().contains("recycled")) {
        // loadImageRound(url, imageView, finalRequestListener,
        // placeholderOverride, crop);
        // }
        // return true;
        // }
        //
        // @Override
        // public boolean onResourceReady(Object o, Object o2, Target target,
        // boolean b, boolean b1) {
        // return false;
        // }
        // };
        // }

        DrawableRequestBuilder<String> request = beginImageLoadRound(url,
                requestListener, crop).animate(R.anim.image_fade_in);
        request.into(imageView);
    }

    double time;

    public void loadImageGif(String url, ImageView imageView,
                             RequestListener requestListener, Drawable placeholderOverride,
                             boolean crop) {
        GifRequestBuilder<String> request = beginGifImageLoad(url,
                requestListener, crop).animate(R.anim.image_fade_in);
        request.diskCacheStrategy(DiskCacheStrategy.SOURCE);
        if (placeholderOverride != null) {
            request.placeholder(placeholderOverride);
        } else if (mPlaceHolderResId != -1) {
            request.placeholder(mPlaceHolderResId);
        }
        request.into(imageView);
    }

    public void loadImage(String url, ImageView imageView,
                          RequestListener requestListener, Drawable placeholderOverride,
                          int errorResourceId, boolean crop) {
        DrawableRequestBuilder<String> request = beginImageLoad(url,
                requestListener, errorResourceId, crop).animate(
                R.anim.image_fade_in);
        if (placeholderOverride != null) {
            request.placeholder(placeholderOverride);
        } else if (mPlaceHolderResId != -1) {
            request.placeholder(mPlaceHolderResId);
        }
        request.into(imageView);
    }

    public DrawableRequestBuilder<String> beginImageLoad(String url,
                                                         RequestListener requestListener, boolean crop) {
        if (crop) {
            return mGlideModelRequest.load(url).listener(requestListener)
                    .centerCrop();
        } else {
            return mGlideModelRequest.load(url).listener(requestListener);
        }
    }

    public DrawableRequestBuilder<String> beginImageLoadRound(String url,
                                                              RequestListener requestListener, boolean crop) {
        if (crop) {
            return mGlideModelRequest.load(url).listener(requestListener)
                    .transform(new CircleTransform(mContext)).centerCrop();
        } else {
            return mGlideModelRequest.load(url).listener(requestListener)
                    .transform(new CircleTransform(mContext));
        }
    }

    public GifRequestBuilder<String> beginGifImageLoad(String url,
                                                       RequestListener requestListener, boolean crop) {
        if (crop) {
            return mGlideModelRequest.load(url).asGif()
                    .listener(requestListener).centerCrop();
        } else {
            return mGlideModelRequest.load(url).asGif()
                    .listener(requestListener);
        }
    }

    public DrawableRequestBuilder<String> beginImageLoad(String url,
                                                         RequestListener requestListener, int errorResourceId, boolean crop) {
        if (crop) {
            return mGlideModelRequest.load(url).listener(requestListener)
                    .error(errorResourceId).centerCrop();
        } else {
            return mGlideModelRequest.load(url).listener(requestListener)
                    .error(errorResourceId);
        }
    }

    /**
     * Load an image from a url into the given image view using the default
     * placeholder if available.
     *
     * @param url       The web URL of an image.
     * @param imageView The target ImageView to load the image into.
     */
    public void loadImage(String url, ImageView imageView) {
        String mUrl;
        if (url.startsWith("http")) {
            mUrl = url;
        } else if (url.startsWith("www") || url.startsWith("14.63")
                || url.startsWith("dinnerqueen")) {
            mUrl = "http://" + url;
        } else {
            mUrl = url;
        }

        Log.e("Woo", "Img = " + mUrl);

        // loadImage(url, imageView, false /*crop*/);
        loadImage(mUrl, imageView, false /* crop */);
    }

    public void loadImageRound(String url, ImageView imageView) {
        String mUrl;
        if (url.startsWith("http")) {
            mUrl = url;
        } else if (url.startsWith("www") || url.startsWith("14.63")
                || url.startsWith("dinnerqueen")) {
            mUrl = "http://" + url;
        } else {
            mUrl = url;
        }

        // loadImageRound(url, imageView, false /*crop*/);
        loadImageRound(mUrl, imageView, false /* crop */);
    }

    public void loadImageGif(String url, ImageView imageView) {
        String mUrl;
        if (url.startsWith("http")) {
            mUrl = url;
        } else if (url.startsWith("www") || url.startsWith("14.63")
                || url.startsWith("dinnerqueen")) {
            mUrl = "http://" + url;
        } else {
            mUrl = url;
        }
        // loadImageRound(url, imageView, false /*crop*/);
        loadImageGif(mUrl, imageView, false /* crop */);
    }

    public void showLoading(ImageView imageView) {
        // Glide.with(mContext)
        // .load(R.drawable.loading_alyac)
        // .diskCacheStrategy(DiskCacheStrategy.SOURCE)
        // .placeholder(R.drawable.loading_alyac)
        // .into(imageView);;
    }

    public void loadImage(String url, ImageView imageView, int errorResourceId) {
        loadImage(url, imageView, errorResourceId, false /* crop */);
    }

    /**
     * Load an image from a url into an ImageView using the default placeholder
     * drawable if available.
     *
     * @param url       The web URL of an image.
     * @param imageView The target ImageView to load the image into.
     * @param crop      True to apply a center crop to the image.
     */
    public void loadImage(String url, ImageView imageView, boolean crop) {
        if (url.substring(url.length() - 4, url.length()).toLowerCase()
                .equals(".gif")) {
            loadImageGif(url, imageView, null, null, crop);
        } else {
            loadImage(url, imageView, null, null, crop);
        }
    }

    public void loadImageRound(String url, ImageView imageView, boolean crop) {
        loadImageRound(url, imageView, null, null, crop);
    }

    public void loadImageGif(String url, ImageView imageView, boolean crop) {
        loadImageGif(url, imageView, null, null, crop);
    }

    public void loadImage(String url, ImageView imageView, int errorResourceId,
                          boolean crop) {
        loadImage(url, imageView, null, null, errorResourceId, crop);
    }

    private static class VariableWidthImageLoader extends
            BaseGlideUrlLoader<String> {
        private static final Pattern PATTERN = Pattern
                .compile("__w-((?:-?\\d+)+)__");

        public VariableWidthImageLoader(Context context) {
            super(context, urlCache);
        }

        /**
         * If the URL contains a special variable width indicator (eg
         * "__w-200-400-800__") we get the buckets from the URL (200, 400 and
         * 800 in the example) and replace the URL with the best bucket for the
         * requested width (the bucket immediately larger than the requested
         * width).
         */
        @Override
        protected String getUrl(String model, int width, int height) {
            Matcher m = PATTERN.matcher(model);
            int bestBucket = 0;
            if (m.find()) {
                String[] found = m.group(1).split("-");
                for (String bucketStr : found) {
                    bestBucket = Integer.parseInt(bucketStr);
                    if (bestBucket >= width) {
                        // the best bucket is the first immediately bigger than
                        // the requested width
                        break;
                    }
                }
                if (bestBucket > 0) {
                    model = m.replaceFirst("w" + bestBucket);
                    // LOGD(TAG,
                    // "width="+width+", URL successfully replaced by "+model);
                }
            }
            return model;
        }
    }

    public Target<GlideDrawable> preloadImage(String url, int width, int height) {
        return mGlideModelRequest.load(url).preload(width, height);
    }
}
