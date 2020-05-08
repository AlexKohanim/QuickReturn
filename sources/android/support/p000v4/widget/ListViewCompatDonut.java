package android.support.p000v4.widget;

import android.view.View;
import android.widget.ListView;

/* renamed from: android.support.v4.widget.ListViewCompatDonut */
class ListViewCompatDonut {
    ListViewCompatDonut() {
    }

    static void scrollListBy(ListView listView, int y) {
        int firstPosition = listView.getFirstVisiblePosition();
        if (firstPosition != -1) {
            View firstView = listView.getChildAt(0);
            if (firstView != null) {
                listView.setSelectionFromTop(firstPosition, firstView.getTop() - y);
            }
        }
    }
}
