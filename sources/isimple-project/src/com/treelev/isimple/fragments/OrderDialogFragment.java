package com.treelev.isimple.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShoppingCartActivity;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.domain.db.Order;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;


public class OrderDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener,
        TextWatcher{

    public static final int SELECT_TYPE = 2;
    public static final int PHONE_TYPE = 0;   //id item list
    public static final int EMAIL_TYPE = 1;   // id item list
    public static final int SUCCESS_TYPE = 3;

    private OrderDialogFragment mDialogFragment;
    private int mType;
    private String mContactInfo;
    private EditText mEditContactInfo;
    private Button mBtnPositive;
    private Dialog mDialog;
    private boolean mSuccess;


    public OrderDialogFragment(int typeDialog){
        mType = typeDialog;
    }
    public void setSuccess(boolean success){
        mSuccess = success;
    }

    @Override
    public void onStart() {
        super.onStart();

        Analytics.screen_OrderInfo(getActivity());

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
        TextView tvSuccess = (TextView)mDialog.findViewById(R.id.message_success);
        if( tvSuccess != null ){
            if(mSuccess){
                tvSuccess.setText(Html.fromHtml(getString(R.string.message_success_send_orders)));
            } else {
                tvSuccess.setText(Html.fromHtml(getString(R.string.message_not_success_send_orders)));
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i){
            case Dialog.BUTTON_POSITIVE:
                new SendOrders(getActivity(), mContactInfo).execute();
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
            case SUCCESS_TYPE:
                adb.setView(inflater.inflate(R.layout.dialog_success_send_orders, null));
                break;
            default:
                adb = null;
        }

        mDialog = adb.create();
        return mDialog;
    }

    private boolean mFormatting;
    private int mPositionCursor = 3;
    private int mBefore = 0;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean enable = false;
        switch(mType){
            case PHONE_TYPE:
                if(!mFormatting){
                    mPositionCursor = start;
                    mBefore = before;
                }
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
                    mContactInfo = formatPhone(editable.toString());
                    mEditContactInfo.setText(mContactInfo);
                    mEditContactInfo.setSelection(getFormattedCursorPosition());
                    mFormatting = false;
                }
                break;
        }
    }


    private int getFormattedCursorPosition(){
        int cursorPosition = 3;
        int offset;
        int length = mContactInfo.length();
        if(mPositionCursor > 15){
            cursorPosition = 16;
        } else if(mBefore == 0 && mPositionCursor > 1) { //insert char
            offset = 1;
            if((length == 4 || length == 7 || length == 8 || length == 11 || length == 12 || length == 14 || length == 15)
                    && mPositionCursor < length - 1){
                offset = 2;
            }
            cursorPosition = mPositionCursor + offset;
        } else if(length > 3 && mPositionCursor > 3){//delete char
            if(length == 6 || length == 10 || length == 13){
                cursorPosition = length;
            } else {
                cursorPosition = mPositionCursor;
            }
        }
        return cursorPosition < length ? cursorPosition : length;
    }

    private int mLengthOld = 3;
    private String mOldPhoneNumber = "+7 ";

    private String formatPhone(String s){
        String newPhoneNumber = "";
        int lengthNew = s.length();
        if(lengthNew < 17 && lengthNew > 2){
            if(mBefore  == 0 ){ //insert
                newPhoneNumber = getFormattedAfterInsertChar(s);
            } else if(mBefore == 1) {// delete 1 char
                newPhoneNumber = getFormattedAfterDeleteChar(s);
            }
        }
        if(newPhoneNumber.length() > 0){
            mOldPhoneNumber = newPhoneNumber;
        }
        return mOldPhoneNumber;
    }

    private String getFormattedAfterInsertChar(String s){
        String newPhoneNumber = "";
        if (Character.isDigit(s.charAt(mPositionCursor)) && mPositionCursor > 1){
            String str = new String(s);
            str = str.replace("+", "").replace(" ", "").replace("-", "");
            int length = str.length();
            if(length > 1 && length < 4){
                newPhoneNumber = String.format("+7 %s",
                        str.substring(1));
            } else if(length == 4){
                newPhoneNumber = String.format("+7 %s ",
                        str.substring(1) );
            }
            else if(length > 4 && length < 7){
                newPhoneNumber = String.format("+7 %s %s",
                        str.substring(1,4), str.substring(4));
            } else if(length == 7){
                newPhoneNumber = String.format("+7 %s %s-",
                        str.substring(1,4), str.substring(4));
            } else if(length > 7 && length < 9){
                newPhoneNumber = String.format("+7 %s %s-%s",
                        str.substring(1,4), str.substring(4, 7), str.substring(7));
            } else if(length == 9){
                newPhoneNumber = String.format("+7 %s %s-%s-",
                        str.substring(1,4), str.substring(4, 7), str.substring(7));
            } else if(length > 9) {
                newPhoneNumber = String.format("+7 %s %s-%s-%s",
                        str.substring(1,4), str.substring(4, 7), str.substring(7, 9), str.substring(9));
            }
        }
        return newPhoneNumber;
    }

    private String getFormattedAfterDeleteChar(String s){
        String newPhoneNumber = "";
        if (mPositionCursor > 2){
            String str = new StringBuffer(s).toString();
            if(mPositionCursor == 6 || mPositionCursor == 10 || mPositionCursor == 13){
                if(mPositionCursor != str.length()){
                    str = str.substring(0, mPositionCursor-1) + str.substring(mPositionCursor);
                }
            }
            str = str.replace("+", "").replace(" ", "").replace("-", "");
            String newStr = new StringBuilder(str).toString();
            int length = newStr.length();
            if(length == 1){
                newPhoneNumber = "+7 ";
            } if(length > 1 && length <= 4){
                newPhoneNumber = String.format("+7 %s",
                        str.substring(1));
            }
            else if(length > 4 && length <= 7){
                newPhoneNumber = String.format("+7 %s %s",
                        str.substring(1,4), str.substring(4));
            } else if(length > 7 && length <= 9){
                newPhoneNumber = String.format("+7 %s %s-%s",
                        str.substring(1,4), str.substring(4, 7), str.substring(7));
            } else if(length > 9) {
                newPhoneNumber = String.format("+7 %s %s-%s-%s",
                        str.substring(1,4), str.substring(4, 7), str.substring(7, 9), str.substring(9));
            }
        }
        return newPhoneNumber;
    }

//    private String getFormattedPhoneNumber(CharSequence s){
//        String newPhoneNumber = "";
//        if (Character.isDigit(s.charAt(mPositionCursor)) && mPositionCursor > 2){
//            String str = s.toString();
//            str = str.replace("+", "").replace(" ", "").replace("-", "");
//            int length = s.length();
//            if(length > 2 && length < 5){
//                newPhoneNumber = String.format("+7 %s",
//                        str.substring(1));
//            } else if(length > 4 && length < 8){
//                newPhoneNumber = String.format("+7 %s %s",
//                        str.substring(1,4), str.substring(4));
//            } else if(length > 7 && length < 10){
//                newPhoneNumber = String.format("+7 %s %s-%s",
//                        str.substring(1,3), str.substring(4, 7),str.substring(8));
//            } else {
//                newPhoneNumber = String.format("+7 %s %s-%s-%s",
//                        str.substring(1,3), str.substring(4, 7), str.substring(8, 9), str.substring(10));
//            }
//        }
//        return newPhoneNumber;
//    }

//    private String getFormattedAfterDelete(CharSequence s){
//        StringBuilder formatted = new StringBuilder("+7 ");
//        return formatted.toString();
//    }
//    private String formatPhone(CharSequence s){
//        StringBuilder formatted = new StringBuilder();
//        int positionEnd = s.length()-1;
//        formatted.append("+7 ");
//        if (Character.isDigit(s.charAt(positionEnd)) && mLengthOld < (positionEnd+1)){
//            if(positionEnd > 2 && positionEnd < 16){
//                formatted.append(s.subSequence(3, positionEnd+1));
//                if(positionEnd == 5){
//                    formatted.append(" ");
//                } else if(positionEnd == 9 || positionEnd == 12){
//                    formatted.append("-");
//                }
//            } else {
//                formatted.append(s.subSequence(3, 16));
//            }
//        } else {
//            if(positionEnd > 2 && positionEnd < 16 ) {
//                if(positionEnd == 6 || positionEnd == 10 || positionEnd == 13){
//                    formatted.append(s.subSequence(3, positionEnd));
//                } else {
//                    if(!isSpecialCharacters(s.charAt(positionEnd))){
//                        formatted.append(s.subSequence(3, positionEnd+1));
//                    }
//                }
//            }
//        }
//        mLengthOld = formatted.length();
//        return formatted.toString();
//    }

    private boolean isSpecialCharacters(char c){
        return c == ' ' || c == '+' || c == '-' || c == '.' || c == '(' || c == ')' || c == '/' || c == ',' || c == '*' || c == '#'
                || c == 'N';
    }

    private class SendOrders extends AsyncTask<Void, Void, Boolean>{

        private Context mContext;
        private String mContactInfo;
        private Dialog mDialog;
        private ProxyManager mProxyManager;

        public SendOrders(Context context, String contacInfo){
            mContext = context;
            mContactInfo = contacInfo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideKeyBoard();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.registration_orders), false, false);
            ((ShoppingCartActivity)mContext).setResultSendOrders(true);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean result = postData();
            if(result){
               getProxyManager().deleteAllShoppingCartData();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            if(((ShoppingCartActivity)mContext).isSaveInstancceState()){
                ((ShoppingCartActivity)mContext).setResultSendOrders(result);
                ((ShoppingCartActivity)mContext).sendOrderSetFlag(true);
            } else {
                OrderDialogFragment dialog = new OrderDialogFragment(SUCCESS_TYPE);
                dialog.setSuccess(result);
                dialog.show(((Activity) mContext).getSupportFragmentManager(), "SUCCESS_TYPE");
                ((ShoppingCartActivity)mContext).setResultSendOrders(result);
                ((ShoppingCartActivity)mContext).updateList();
            }
        }

        private ProxyManager getProxyManager() {
            if (mProxyManager == null) {
                mProxyManager = ProxyManager.getInstanse();
            }
            return mProxyManager;
        }

        private boolean postData() {
            boolean result = false;
            HttpPost httppost = new HttpPost(mContext.getString(R.string.url_send_order).trim());

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("Device-ID", getDeviceID()));
                nameValuePairs.add(new BasicNameValuePair("MD5", getMD5()));
                nameValuePairs.add(new BasicNameValuePair("Order-Info", getListOrder()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request

                HttpClient httpclient = Utils.newHttpClient();

                HttpResponse response = httpclient.execute(httppost);
                if(getResponseAnswer(response.getEntity()) > 0 ){
                    result = true;
                }

            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            } finally {
                return result;
            }
            
//            OkHttpClient client = new OkHttpClient();
//            RequestBody body = RequestBody.create(JSON, json);
//            Request request = new Request.Builder()
//                .url(mContext.getString(R.string.url_send_order).trim())
//                .post(body)
//                .build();
//            Response response = client.newCall(request).execute();
//            response.body().string();
        }

        private int getResponseAnswer(HttpEntity entity){
            int result = 0;
            try {
                String answer = parserResponseAnswer(entity.getContent());
                result = Integer.valueOf(answer);
            } catch (Exception e) {
            }
            finally {
                return result;
            }
        }

        private String parserResponseAnswer(InputStream inputStream) throws IOException {
            InputStreamReader is = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(is);
            StringBuilder stringBuilder = new StringBuilder();
            String read = br.readLine();
            while(read != null){
                stringBuilder.append(read);
                read = br.readLine();
            }
            return stringBuilder.toString();
        }

        private String getDeviceID(){
            TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(mContext.TELEPHONY_SERVICE);
            String itemiID = telephonyManager.getDeviceId();
            if( itemiID == null){
                itemiID = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return itemiID;
        }

        private String getMD5() {
            try {
                String s = String.format("%s%s", mContext.getString(R.string.shared_secret).trim(), getDeviceID());
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
            List<Order> orders =  getProxyManager().getOrders();
            for(Order orderItem : orders){
                sbListOrder.append(String.format("<OrderItem><ItemID>%s</ItemID><Quantity>%s</Quantity><Price>%s</Price></OrderItem>", orderItem.getItemID(), orderItem.getQuantity(), orderItem.getPrice()));
            }
            sbListOrder.append("</Order>");
            return sbListOrder.toString();
        }

        private void hideKeyBoard(){
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
}
