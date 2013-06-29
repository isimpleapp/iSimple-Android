package com.treelev.isimple.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShoppingCartActivity;
import com.treelev.isimple.domain.db.Order;
import com.treelev.isimple.httpclient.HttpClientCert;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.holoeverywhere.app.*;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import javax.security.cert.CertificateException;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;


public class OrderDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener,
        TextWatcher{

    public static final int SELECT_TYPE = 2;
    public static final int PHONE_TYPE = 0;   //id item list
    public static final int EMAIL_TYPE = 1;   // id item list
    public static final int SUCCESS_TYPE = 3;

    public static final String LIST_ORDERS = "LIST_ORDERS";

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
            if(result){
                ((ShoppingCartActivity)mContext).updateList();
            }
            if(((ShoppingCartActivity)mContext).isIsSaveInstancceState()){
                ((ShoppingCartActivity)mContext).setResultSendOrders(result);
                ((ShoppingCartActivity)mContext).sendOrderSetFlag(true);
            } else {
                OrderDialogFragment dialog = new OrderDialogFragment(SUCCESS_TYPE);
                dialog.setSuccess(result);
                dialog.show(((Activity) mContext).getSupportFragmentManager(), "SUCCESS_TYPE");
                ((ShoppingCartActivity)mContext).setResultSendOrders(false);
            }
        }

        private ProxyManager getProxyManager() {
            if (mProxyManager == null) {
                mProxyManager = new ProxyManager(mContext);
            }
            return mProxyManager;
        }

        private boolean postData() {
            // Create a new HttpClient and Post Header

            HttpPost httppost = new HttpPost(mContext.getString(R.string.url_send_order).trim());

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("Device-ID", getDeviceID()));
                nameValuePairs.add(new BasicNameValuePair("MD5", getMD5()));
                nameValuePairs.add(new BasicNameValuePair("Order-Info", getListOrder()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                Log.v("OrderSend", getDeviceID());
                Log.v("OrderSend", getMD5());
                Log.v("OrderSend", getListOrder());
                // Execute HTTP Post Request

                HttpClientCert httpclient = new HttpClientCert(mContext);

                HttpResponse response = httpclient.execute(httppost);
                Log.v("Error epick ", EntityUtils.toString(response.getEntity()));

            } catch (ClientProtocolException e) {
                Log.v("Error epick ClientProtocolException", e.getMessage());
                Log.v("Error epick IOException", e.getStackTrace().toString());
                return  false;
            } catch (IOException e) {
//                Log.v("Error epick IOException", e.getMessage());
//                Log.v("Error epick IOException", e.getStackTrace().toString());
                Log.v("Test test", e.getMessage() );
                return  false;
            }
            return true;
        }

        private String getDeviceID(){

            TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(mContext.TELEPHONY_SERVICE);
            String imeiid = telephonyManager.getDeviceId();
            if( imeiid == null){
                imeiid = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return imeiid;
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
                sbListOrder.append(String.format("<OrderItem><ItemID>%s</ItemID><Quantity>%s</Quantity></OrderItem>", orderItem.getItemID(), orderItem.getQuantity()));
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
