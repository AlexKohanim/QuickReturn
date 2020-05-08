package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiTimePickerDialog */
public class TiTimePickerDialog extends TimePickerDialog {
    public TiTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
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
