package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiDatePickerDialog */
public class TiDatePickerDialog extends DatePickerDialog {
    public TiDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            super.onClick(dialog, which);
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
    }
}
