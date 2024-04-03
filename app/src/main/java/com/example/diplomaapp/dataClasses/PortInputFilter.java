package com.example.diplomaapp.dataClasses;

import android.text.InputFilter;

public class PortInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               android.text.Spanned dest, int dstart, int dend) {
        if (end > start) {
            String destTxt = dest.toString();
            String resultingTxt = destTxt.substring(0, dstart)
                    + source.subSequence(start, end)
                    + destTxt.substring(dend);
            try {
                int port = Integer.parseInt(resultingTxt);
                if (port < 0 || port > 65535) {
                    return "";
                }
            } catch (NumberFormatException e) {
                return "";
            }
        }
        return null;
    }
}

