
package com.treelev.isimple.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShoppingCartActivity;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.domain.db.Order;
import com.treelev.isimple.utils.LogUtils;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;
import com.treelev.isimple.utils.managers.WebServiceManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class OrderDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public static final int FILL_CONTACT_DATA_TYPE = 1;
    public static final int SUCCESS_TYPE = 2;
    private int mType;
    private String region;
    private String mContactInfo;
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPhoneField;
    private Button mBtnPositive;
    private Dialog mDialog;
    private int mResultCode;
    private boolean isEmailValid;
    private boolean isPhoneValid;

    public OrderDialogFragment() {
    }

    public void setData(int typeDialog, String region) {
        mType = typeDialog;
        this.region = region;
    }

    public void setResultCode(int resultCode) {
        mResultCode = resultCode;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public void onStart() {
        super.onStart();
        Analytics.screen_OrderInfo(getActivity());
        mBtnPositive = ((AlertDialog) (mDialog)).getButton(AlertDialog.BUTTON_POSITIVE);
        if (mBtnPositive != null) {
            mBtnPositive.setEnabled(false);
        }
        switch (mType) {
            case FILL_CONTACT_DATA_TYPE:
                mNameField = (EditText) mDialog.findViewById(R.id.contact_info_name);
                mNameField.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        validateFields();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                String name = SharedPreferencesManager.getContactDataName(getActivity());
                if (name != null) {
                    mNameField.setText(name);
                }
                mEmailField = (EditText) mDialog.findViewById(R.id.contact_info_email);
                mEmailField.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
                        validateFields();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                String email = SharedPreferencesManager.getContactDataEmail(getActivity());
                if (email != null) {
                    mEmailField.setText(email);
                }
                mPhoneField = (EditText) mDialog.findViewById(R.id.contact_info_phone);
                mPhoneField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!mFormatting) {
                            mPositionCursor = start;
                            mBefore = before;
                        }
                        isPhoneValid = s.length() == 16;
                        validateFields();
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!mFormatting) {
                            mFormatting = true;
                            mContactInfo = formatPhone(s.toString());
                            mPhoneField.setText(mContactInfo);
                            mPhoneField.setSelection(getFormattedCursorPosition());
                            mFormatting = false;
                        }
                    }
                });
                String phone = SharedPreferencesManager.getContactDataPhone(getActivity());
                if (phone != null) {
                    mPhoneField.setText(phone);
                } else {
                    String start = "+7 ";
                    mPhoneField.setText(start);
                    mPhoneField.setSelection(start.length());
                }
                break;
            case SUCCESS_TYPE:
                TextView tvSuccess = (TextView) mDialog.findViewById(R.id.message_success);
                if (tvSuccess != null) {
                    if (mResultCode > 0) {
                        tvSuccess.setText(Html.fromHtml(String.format(getString(R.string.message_success_send_orders), mResultCode)));
                    } else {
                        tvSuccess.setText(Html.fromHtml(getString(R.string.message_not_success_send_orders)));
                    }
                }
                break;
            default:
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case Dialog.BUTTON_POSITIVE:
                SharedPreferencesManager.setContactDataName(getActivity(), mNameField.getText().toString());
                SharedPreferencesManager.setContactDataEmail(getActivity(), mEmailField.getText().toString());
                SharedPreferencesManager.setContactDataPhone(getActivity(), mPhoneField.getText().toString());
                new SendOrders(getActivity(), mNameField.getText().toString(), mEmailField.getText().toString(), mPhoneField.getText().toString()).execute();
                break;
        }
    }

    private void validateFields() {
        if (!TextUtils.isEmpty(mNameField.getText().toString()) && (isEmailValid || isPhoneValid)) {
            mBtnPositive.setEnabled(true);
        } else {
            mBtnPositive.setEnabled(false);
        }
    }

    private Dialog createDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        switch (mType) {
            case FILL_CONTACT_DATA_TYPE:
                adb.setTitle(getString(R.string.title_contact_data_order_dialog));
                adb.setView(inflater.inflate(R.layout.dialog_order_contact_data, null));
                adb.setPositiveButton(R.string.dialog_order_button_complite, this);
                adb.setCancelable(true);
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

    private int getFormattedCursorPosition() {
        int cursorPosition = 3;
        int offset;
        int length = mContactInfo.length();
        if (mPositionCursor > 15) {
            cursorPosition = 16;
        } else if (mBefore == 0 && mPositionCursor > 1) { // insert char
            offset = 1;
            if ((length == 4 || length == 7 || length == 8 || length == 11 || length == 12 || length == 14 || length == 15) && mPositionCursor < length - 1) {
                offset = 2;
            }
            cursorPosition = mPositionCursor + offset;
        } else if (length > 3 && mPositionCursor > 3) {// delete char
            if (length == 6 || length == 10 || length == 13) {
                cursorPosition = length;
            } else {
                cursorPosition = mPositionCursor;
            }
        }
        return cursorPosition < length ? cursorPosition : length;
    }

    // private int mLengthOld = 3;
    private String mOldPhoneNumber = "+7 ";

    private String formatPhone(String s) {
        String newPhoneNumber = "";
        int lengthNew = s.length();
        if (lengthNew < 17 && lengthNew > 2) {
            if (mBefore == 0) { // insert
                newPhoneNumber = getFormattedAfterInsertChar(s);
            } else if (mBefore == 1) {// delete 1 char
                newPhoneNumber = getFormattedAfterDeleteChar(s);
            }
        }
        if (newPhoneNumber.length() > 0) {
            mOldPhoneNumber = newPhoneNumber;
        }
        return mOldPhoneNumber;
    }

    private String getFormattedAfterInsertChar(String s) {
        String newPhoneNumber = "";
        if (Character.isDigit(s.charAt(mPositionCursor)) && mPositionCursor > 1) {
            String str = new String(s);
            str = str.replace("+", "").replace(" ", "").replace("-", "");
            int length = str.length();
            if (length > 1 && length < 4) {
                newPhoneNumber = String.format("+7 %s",
                        str.substring(1));
            } else if (length == 4) {
                newPhoneNumber = String.format("+7 %s ",
                        str.substring(1));
            } else if (length > 4 && length < 7) {
                newPhoneNumber = String.format("+7 %s %s",
                        str.substring(1, 4), str.substring(4));
            } else if (length == 7) {
                newPhoneNumber = String.format("+7 %s %s-",
                        str.substring(1, 4), str.substring(4));
            } else if (length > 7 && length < 9) {
                newPhoneNumber = String.format("+7 %s %s-%s",
                        str.substring(1, 4), str.substring(4, 7), str.substring(7));
            } else if (length == 9) {
                newPhoneNumber = String.format("+7 %s %s-%s-",
                        str.substring(1, 4), str.substring(4, 7), str.substring(7));
            } else if (length > 9) {
                newPhoneNumber = String.format("+7 %s %s-%s-%s",
                        str.substring(1, 4), str.substring(4, 7), str.substring(7, 9),
                        str.substring(9));
            }
        }
        return newPhoneNumber;
    }

    private String getFormattedAfterDeleteChar(String s) {
        String newPhoneNumber = "";
        if (mPositionCursor > 2) {
            String str = new StringBuffer(s).toString();
            if (mPositionCursor == 6 || mPositionCursor == 10 || mPositionCursor == 13) {
                if (mPositionCursor != str.length()) {
                    str = str.substring(0, mPositionCursor - 1) + str.substring(mPositionCursor);
                }
            }
            str = str.replace("+", "").replace(" ", "").replace("-", "");
            String newStr = new StringBuilder(str).toString();
            int length = newStr.length();
            if (length == 1) {
                newPhoneNumber = "+7 ";
            }
            if (length > 1 && length <= 4) {
                newPhoneNumber = String.format("+7 %s",
                        str.substring(1));
            } else if (length > 4 && length <= 7) {
                newPhoneNumber = String.format("+7 %s %s",
                        str.substring(1, 4), str.substring(4));
            } else if (length > 7 && length <= 9) {
                newPhoneNumber = String.format("+7 %s %s-%s",
                        str.substring(1, 4), str.substring(4, 7), str.substring(7));
            } else if (length > 9) {
                newPhoneNumber = String.format("+7 %s %s-%s-%s",
                        str.substring(1, 4), str.substring(4, 7), str.substring(7, 9),
                        str.substring(9));
            }
        }
        return newPhoneNumber;
    }

    // private String getFormattedPhoneNumber(CharSequence s){
    // String newPhoneNumber = "";
    // if (Character.isDigit(s.charAt(mPositionCursor)) && mPositionCursor > 2){
    // String str = s.toString();
    // str = str.replace("+", "").replace(" ", "").replace("-", "");
    // int length = s.length();
    // if(length > 2 && length < 5){
    // newPhoneNumber = String.format("+7 %s",
    // str.substring(1));
    // } else if(length > 4 && length < 8){
    // newPhoneNumber = String.format("+7 %s %s",
    // str.substring(1,4), str.substring(4));
    // } else if(length > 7 && length < 10){
    // newPhoneNumber = String.format("+7 %s %s-%s",
    // str.substring(1,3), str.substring(4, 7),str.substring(8));
    // } else {
    // newPhoneNumber = String.format("+7 %s %s-%s-%s",
    // str.substring(1,3), str.substring(4, 7), str.substring(8, 9),
    // str.substring(10));
    // }
    // }
    // return newPhoneNumber;
    // }

    // private String getFormattedAfterDelete(CharSequence s){
    // StringBuilder formatted = new StringBuilder("+7 ");
    // return formatted.toString();
    // }
    // private String formatPhone(CharSequence s){
    // StringBuilder formatted = new StringBuilder();
    // int positionEnd = s.length()-1;
    // formatted.append("+7 ");
    // if (Character.isDigit(s.charAt(positionEnd)) && mLengthOld <
    // (positionEnd+1)){
    // if(positionEnd > 2 && positionEnd < 16){
    // formatted.append(s.subSequence(3, positionEnd+1));
    // if(positionEnd == 5){
    // formatted.append(" ");
    // } else if(positionEnd == 9 || positionEnd == 12){
    // formatted.append("-");
    // }
    // } else {
    // formatted.append(s.subSequence(3, 16));
    // }
    // } else {
    // if(positionEnd > 2 && positionEnd < 16 ) {
    // if(positionEnd == 6 || positionEnd == 10 || positionEnd == 13){
    // formatted.append(s.subSequence(3, positionEnd));
    // } else {
    // if(!isSpecialCharacters(s.charAt(positionEnd))){
    // formatted.append(s.subSequence(3, positionEnd+1));
    // }
    // }
    // }
    // }
    // mLengthOld = formatted.length();
    // return formatted.toString();
    // }

    // private boolean isSpecialCharacters(char c) {
    // return c == ' ' || c == '+' || c == '-' || c == '.' || c == '(' || c ==
    // ')' || c == '/'
    // || c == ',' || c == '*' || c == '#'
    // || c == 'N';
    // }

    private class SendOrders extends AsyncTask<Void, Void, Integer> {

        private Context mContext;
        private Dialog mDialog;
        private ProxyManager mProxyManager;
        private String contacInfoName;
        private String contacInfoEmail;
        private String contacInfoPhone;

        public SendOrders(Context context, String contacInfoName, String contacInfoEmail, String contacInfoPhone) {
            mContext = context;
            this.contacInfoName = contacInfoName;
            this.contacInfoEmail = contacInfoEmail;
            this.contacInfoPhone = contacInfoPhone;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideKeyBoard();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title), mContext.getString(R.string.registration_orders), false, false);
            ((ShoppingCartActivity) mContext).setResultSendOrders(0);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int resultCode = postData();
            if (resultCode > 0) {
                getProxyManager().deleteAllShoppingCartData();
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(final Integer result) {
            super.onPostExecute(result);
            mDialog.dismiss();
            if (((ShoppingCartActivity) mContext).isSaveInstancceState()) {
                ((ShoppingCartActivity) mContext).setResultSendOrders(result);
                ((ShoppingCartActivity) mContext).sendOrderSetFlag(true);
            } else {
                OrderDialogFragment dialog = new OrderDialogFragment();
                dialog.setData(SUCCESS_TYPE, null);
                dialog.setResultCode(result);
                dialog.show(((Activity) mContext).getFragmentManager(), "SUCCESS_TYPE");
                ((ShoppingCartActivity) mContext).setResultSendOrders(result);
                ((ShoppingCartActivity) mContext).updateList();
            }
        }

        private ProxyManager getProxyManager() {
            if (mProxyManager == null) {
                mProxyManager = ProxyManager.getInstanse();
            }
            return mProxyManager;
        }

        private String getQuery(List<Pair<String, String>> params) {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Pair<String, String> pair : params) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }
                result.append(pair.first);
                result.append("=");
                result.append(pair.second);
            }
            return result.toString();
        }

        private int postData() {
            int resultCode = 0;
            HttpURLConnection urlConnection = null;
            URL downloadUrl;
            try {
                downloadUrl = new URL(mContext.getString(R.string.url_send_order).trim());
                urlConnection = (HttpURLConnection) downloadUrl.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.addRequestProperty("Accept-Charset", "UTF-8");
                List<Pair<String, String>> nameValuePairs = new ArrayList<Pair<String, String>>(3);
                nameValuePairs.add(new Pair<String, String>("Device-ID", getDeviceID()));
                nameValuePairs.add(new Pair<String, String>("MD5", getMD5()));
                nameValuePairs.add(new Pair<String, String>("Order-Info", getListOrder()));
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(nameValuePairs));
                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();
                resultCode = getResponseAnswer(WebServiceManager.readStream(urlConnection.getInputStream()));
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            // HttpPost httppost = new
            // HttpPost(mContext.getString(R.string.url_send_order).trim());
            // int resultCode = 0;
            // try {
            // // Add your data
            // List<NameValuePair> nameValuePairs = new
            // ArrayList<NameValuePair>(3);
            // nameValuePairs.add(new BasicNameValuePair("Device-ID",
            // getDeviceID()));
            // nameValuePairs.add(new BasicNameValuePair("MD5", getMD5()));
            // nameValuePairs.add(new BasicNameValuePair("Order-Info",
            // getListOrder()));
            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // // Execute HTTP Post Request
            //
            // HttpClient httpclient = Utils.newHttpClient();
            //
            // HttpResponse response = httpclient.execute(httppost);
            // resultCode = getResponseAnswer(response.getEntity());
            // } catch (ClientProtocolException e) {
            // } catch (IOException e) {
            // }

            return resultCode;

        }

        private int getResponseAnswer(String response) {
            LogUtils.i("", "getResponseAnswer response = " + response);
            int result = 0;
            JsonObject jobj;
            try {
                jobj = new Gson().fromJson(response, JsonObject.class);
                result = jobj.get("orderId").getAsInt();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            return result;
        }

        private String getDeviceID() {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String itemiID = telephonyManager.getDeviceId();
            if (itemiID == null) {
                itemiID = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return itemiID;
        }

        private String getMD5() {
            try {
                String s = String.format("%s%s", mContext.getString(R.string.shared_secret).trim(), getDeviceID());
                // Create MD5 Hash
                MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
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

        private String getListOrder() {
            PackageInfo pInfo;
            String version = "";
            try {
                pInfo = mContext.getPackageManager().getPackageInfo( mContext.getPackageName(), 0);
                version = pInfo.versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            String delivery = "самовывоз";
            // try {
            // delivery = URLEncoder.encode(delivery, "UTF-8");
            // region = URLEncoder.encode(region, "UTF-8");
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            // }
            StringBuffer sbListOrder = new StringBuffer();
            sbListOrder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            sbListOrder.append(String.format(
                    "<Order contactName = \"%s\" "
                            + "contactMail = \"%s\" "
                            + "contactPhone = \"%s\" "
                            + "delivery = \"%s\" "
                            + "region = \"%s\" "
                            + "version = \"%s\" "
                            + "date = \"%s\" "
                            + "device_token = \"%s\" >",
                    contacInfoName,
                    contacInfoEmail,
                    contacInfoPhone,
                    delivery,
                    region,
                    version,
                    SharedPreferencesManager.getDatePriceUpdate(mContext),
                    ""// TODO add device gcm token
            ));
            List<Order> orders = getProxyManager().getOrders();
            for (Order orderItem : orders) {
                sbListOrder.append(String.format("<OrderItem><ItemID>%s</ItemID><Quantity>%s</Quantity><Price>%s</Price></OrderItem>",
                        orderItem.getItemID(), orderItem.getQuantity(), orderItem.getPrice()));
            }
            sbListOrder.append("</Order>");
            LogUtils.i("", "sbListOrder.toString() = " + sbListOrder.toString());
            return sbListOrder.toString();
        }

        private void hideKeyBoard() {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
}
