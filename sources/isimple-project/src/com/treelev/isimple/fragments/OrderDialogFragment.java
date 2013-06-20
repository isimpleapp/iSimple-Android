package com.treelev.isimple.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.treelev.isimple.R;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ContextThemeWrapperPlus;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class OrderDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener,
        TextWatcher{

    public static final int SELECT_TYPE = 2;
    public static final int PHONE_TYPE = 0;   //id item list
    public static final int EMAIL_TYPE = 1;   // id item list

    public static final String LIST_ORDERS = "LIST_ORDERS";

    private OrderDialogFragment mDialogFragment;
    private int mType;
    private String mContactInfo;
    private EditText mEditContactInfo;
    private Button mBtnPositive;
    private Dialog mDialog;

    public OrderDialogFragment(int typeDialog){
        mType = typeDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mEditContactInfo = (EditText) mDialog.findViewById(R.id.contact_info);
        if(mEditContactInfo != null){
            mEditContactInfo.addTextChangedListener(this);
            if(mType == PHONE_TYPE){
                String start = "+7 ";
                mEditContactInfo.setText(start);
                mEditContactInfo.setSelection(start.length());
            }
        }
        mBtnPositive = ((AlertDialog) (mDialog)).getButton(AlertDialog.BUTTON_POSITIVE);
        if(mBtnPositive != null) {
            mBtnPositive.setEnabled(false);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        View view = null;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        switch (i){
            case Dialog.BUTTON_POSITIVE:
                sendOrder();
                break;
            case Dialog.BUTTON_NEGATIVE:
                mDialogFragment = new OrderDialogFragment(SELECT_TYPE);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "SELECT_TYPE");
                break;
            case PHONE_TYPE:
                mDialogFragment = new OrderDialogFragment(PHONE_TYPE);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "PHONE_TYPE");
                break;
            case EMAIL_TYPE:
                mDialogFragment = new OrderDialogFragment(EMAIL_TYPE);
                mDialogFragment.show(getActivity().getSupportFragmentManager(), "EMAIL_TYPE");
                break;
        }
    }

    private Dialog createDialog(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        switch(mType){
            case SELECT_TYPE:
                adb.setTitle(getString(R.string.title_first_step_order_dialog));
                adb.setItems(getResources().getStringArray(R.array.type_order_items), this);
                break;
            case PHONE_TYPE:
                adb.setTitle(getString(R.string.title_contact_phone_order_dialog));
                adb.setView(inflater.inflate(R.layout.dialog_order_phone, null));
                adb.setPositiveButton(R.string.dialog_order_button_complite, this);
                adb.setNegativeButton(R.string.dialog_order_button_cancel, this);
                adb.setCancelable(false);
                break;
            case EMAIL_TYPE:
                adb.setTitle(getString(R.string.title_contact_email_order_dialog));
                adb.setView(inflater.inflate(R.layout.dialog_order_email, null));
                adb.setPositiveButton(R.string.dialog_order_button_complite, this);
                adb.setNegativeButton(R.string.dialog_order_button_cancel, this);
                adb.setCancelable(false);
                break;
            default:
                adb = null;
        }

        mDialog = adb.create();
        return mDialog;
    }

    private void sendOrder(){

    }

    private void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(getString(R.string.url_send_order));

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("Device-ID", getDeviceID()));
            nameValuePairs.add(new BasicNameValuePair("MD5", getMD5()));
            nameValuePairs.add(new BasicNameValuePair("Order-Info", getListOrder()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }
    }

    private String getDeviceID(){
        TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(getActivity().TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    private String getMD5() {
        try {
            String s = String.format("%s%s", getString(R.string.shared_secret), getDeviceID());
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    private String getListOrder(){
        StringBuffer sbListOrder = new StringBuffer();
        sbListOrder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        sbListOrder.append(String.format("<Order contactInfo = \"%s\">", mContactInfo));
        String itemID = "";
        String quantity = "";
        sbListOrder.append(String.format("<OrderItem><ItemID>%s</ItemID><Quantity>%s</Quantity></OrderItem>", itemID, quantity));

        sbListOrder.append("</Order>");
        return sbListOrder.toString();
    }

    private boolean mFormatting;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean enable = false;
        switch(mType){
            case PHONE_TYPE:
                enable = s.length() == 16;
                break;
            case EMAIL_TYPE:
                enable = android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
                break;
        }
        if(mBtnPositive != null ){
            mBtnPositive.setEnabled(enable);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        switch (mType){
            case PHONE_TYPE:
                if(!mFormatting){
                    mFormatting = true;
                    mContactInfo = formatPhone(editable);
                    mEditContactInfo.setText(mContactInfo);
                    mEditContactInfo.setSelection(mContactInfo.length());
                    mFormatting = false;
                }
                break;
        }
    }

    private int mLengthOld = 3;

    private String formatPhone(CharSequence s){
        StringBuilder formatted = new StringBuilder();
        int positionEnd = s.length()-1;
        formatted.append("+7 ");
        if (Character.isDigit(s.charAt(positionEnd)) && mLengthOld < (positionEnd+1)){
            if(positionEnd > 2 && positionEnd < 16){
                formatted.append(s.subSequence(3, positionEnd+1));
                if(positionEnd == 5){
                    formatted.append(" ");
                } else if(positionEnd == 9 || positionEnd == 12){
                    formatted.append("-");
                }
            } else {
                formatted.append(s.subSequence(3, 16));
            }
        } else {
            if(positionEnd > 2 && positionEnd < 16 ) {
                if(positionEnd == 6 || positionEnd == 10 || positionEnd == 13){
                    formatted.append(s.subSequence(3, positionEnd));
                } else {
                    if(!isSpecialCharacters(s.charAt(positionEnd))){
                        formatted.append(s.subSequence(3, positionEnd+1));
                    }
                }
            }
        }
        mLengthOld = formatted.length();
        return formatted.toString();
    }

    private boolean isSpecialCharacters(char c){
        return c == ' ' || c == '+' || c == '-' || c == '.' || c == '(' || c == ')' || c == '/' || c == ',' || c == '*' || c == '#'
                || c == 'N';
    }
}
