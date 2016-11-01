package org.cm.podd.urban.report.helper;


import android.content.Context;
import android.net.Uri;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;

@EBean(scope = EBean.Scope.Singleton)
public class UrisKeeper {
    @RootContext
    Context context;

    private ArrayList<String> paths = new ArrayList<String>();
    private ArrayList<Uri> uris  = new ArrayList<Uri>();


    public void addPath(String path) {
        paths.add(path);
    }

    public void addUri(Uri uri) {
        uris.add(uri);
    }

    public void clear() {
        paths.clear();
        uris.clear();
    }

    public ArrayList<String> getPaths() {
        return paths;
    }


    public ArrayList<Uri> getUris() {
        return uris;
    }


}
