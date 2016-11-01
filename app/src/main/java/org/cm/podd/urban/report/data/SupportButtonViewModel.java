package org.cm.podd.urban.report.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.cm.podd.urban.report.BR;

/**
 * Created by nat on 11/2/15 AD.
 */
public class SupportButtonViewModel  extends BaseObservable {
    private boolean meToo;
    private boolean like;

    @Bindable
    public boolean isMeToo() {
        return this.meToo;
    }
    @Bindable
    public boolean isLike() {
        return this.like;
    }
    public void setMeToo(boolean meToo) {
        this.meToo = meToo;
        notifyPropertyChanged(BR.meToo);
    }
    public void setLike(boolean like) {
        this.like = like;
        notifyPropertyChanged(BR.like);
    }
}
