
package com.treelev.isimple.fragments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.treelev.isimple.R;
import com.treelev.isimple.activities.ShoppingCartActivity;
import com.treelev.isimple.analytics.Analytics;
import com.treelev.isimple.domain.db.Order;
import com.treelev.isimple.utils.LogUtils;
import com.treelev.isimple.utils.Utils;
import com.treelev.isimple.utils.managers.ProxyManager;
import com.treelev.isimple.utils.managers.SharedPreferencesManager;

public class OrderDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

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

    public OrderDialogFragment(int typeDialog, String region) {
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
                        tvSuccess.setText(Html
                                .fromHtml(String.format(getString(R.string.message_success_send_orders), mResultCode)));
                    } else {
                        tvSuccess.setText(Html
                                .fromHtml(getString(R.string.message_not_success_send_orders)));
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
                SharedPreferencesManager.setContactDataName(getActivity(), mNameField.getText()
                        .toString());
                SharedPreferencesManager.setContactDataEmail(getActivity(), mEmailField.getText()
                        .toString());
                SharedPreferencesManager.setContactDataPhone(getActivity(), mPhoneField.getText()
                        .toString());
                new SendOrders(getActivity(), mNameField.getText().toString(), mEmailField
                        .getText().toString(), mPhoneField.getText().toString()).execute();
                break;
        }
    }

    private void validateFields() {
        if (!TextUtils.isEmpty(mNameField.getText().toString()) && (isEmailValid
                || isPhoneValid)) {
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
            if ((length == 4 || length == 7 || length == 8 || length == 11 || length == 12
                    || length == 14 || length == 15)
                    && mPositionCursor < length - 1) {
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

    private int mLengthOld = 3;
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
            }
            else if (length > 4 && length < 7) {
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
            }
            else if (length > 4 && length <= 7) {
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

    private boolean isSpecialCharacters(char c) {
        return c == ' ' || c == '+' || c == '-' || c == '.' || c == '(' || c == ')' || c == '/'
                || c == ',' || c == '*' || c == '#'
                || c == 'N';
    }

    private class SendOrders extends AsyncTask<Void, Void, Integer> {

        private Context mContext;
        private Dialog mDialog;
        private ProxyManager mProxyManager;
        private String contacInfoName;
        private String contacInfoEmail;
        private String contacInfoPhone;

        public SendOrders(Context context, String contacInfoName, String contacInfoEmail,
                String contacInfoPhone) {
            mContext = context;
            this.contacInfoName = contacInfoName;
            this.contacInfoEmail = contacInfoEmail;
            this.contacInfoPhone = contacInfoPhone;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            hideKeyBoard();
            mDialog = ProgressDialog.show(mContext, mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.registration_orders), false, false);
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
                OrderDialogFragment dialog = new OrderDialogFragment(SUCCESS_TYPE, null);
                dialog.setResultCode(result);
                dialog.show(((Activity) mContext).getSupportFragmentManager(), "SUCCESS_TYPE");
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

        @SuppressWarnings("deprecation")
        private int postData() {
            HttpPost httppost = new HttpPost(mContext.getString(R.string.url_send_order).trim());
            int resultCode = 0;
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
                resultCode = getResponseAnswer(response.getEntity());
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            return resultCode;

        }

        private int getResponseAnswer(HttpEntity entity) {
            int result = 0;
            JsonObject jobj;
            try {
                jobj = new Gson().fromJson(parserResponseAnswer(entity.getContent()),
                        JsonObject.class);
                result = jobj.get("orderId").getAsInt();
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        private String parserResponseAnswer(InputStream inputStream) throws IOException {
            InputStreamReader is = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(is);
            StringBuilder stringBuilder = new StringBuilder();
            String read = br.readLine();
            while (read != null) {
                stringBuilder.append(read);
                read = br.readLine();
            }
            return stringBuilder.toString();
        }

        private String getDeviceID() {
            TelephonyManager telephonyManager = (TelephonyManager) mContext
                    .getSystemService(mContext.TELEPHONY_SERVICE);
            String itemiID = telephonyManager.getDeviceId();
            if (itemiID == null) {
                itemiID = Settings.Secure.getString(mContext.getApplicationContext()
                        .getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            return itemiID;
        }

        private String getMD5() {
            try {
                String s = String.format("%s%s", mContext.getString(R.string.shared_secret).trim(),
                        getDeviceID());
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

        private String getListOrder() {
            PackageInfo pInfo;
            String version = "";
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(
                        getActivity().getPackageName(), 0);
                version = pInfo.versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            String delivery = "";
            try {
                delivery = URLEncoder.encode("самовывоз", "UTF-8");
                region = URLEncoder.encode(region, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
                    SharedPreferencesManager.getDatePriceUpdate(getActivity()),
                    ""// TODO add device gcm token
            ));
            List<Order> orders = getProxyManager().getOrders();
            for (Order orderItem : orders) {
                sbListOrder
                        .append(String
                                .format("<OrderItem><ItemID>%s</ItemID><Quantity>%s</Quantity><Price>%s</Price></OrderItem>",
                                        orderItem.getItemID(), orderItem.getQuantity(),
                                        orderItem.getPrice()));
            }
            sbListOrder.append("</Order>");
            LogUtils.i("", "sbListOrder.toString() = " + sbListOrder.toString());
            return sbListOrder.toString();
        }

        private void hideKeyBoard() {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
}
