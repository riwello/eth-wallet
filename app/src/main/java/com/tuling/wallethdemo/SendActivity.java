package com.tuling.wallethdemo;

import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tuling.wallethdemo.Model.Web3JService;

import com.tuling.wallethdemo.utils.KeyStoreUtils;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by pc on 2018/1/23.
 */

public class SendActivity extends BaseActivity {

    private static final int FROM_ADDRESS = 232;
    private static final int TO_ADDRESS = 255;
    @BindView(R.id.tv_from)
    TextView tvFrom;
    @BindView(R.id.btn_select_from)
    Button btnSelectFrom;
    @BindView(R.id.et_to)
    EditText etTo;
    @BindView(R.id.btn_select_to)
    Button btnSelectTo;
    @BindView(R.id.et_value)
    EditText etValue;
    @BindView(R.id.et_gas_price)
    EditText etGasPrice;
    @BindView(R.id.et_gas_limit)
    EditText etGasLimit;
    @BindView(R.id.et_nonce)
    EditText etNonce;
    @BindView(R.id.btn_send)
    Button btnSend;

    @BindView(R.id.tv_mgs)
    TextView tvMgs;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_transaction;
    }

    @Override
    protected void initView() {

        tvFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    getTansactionNonce(s.toString());
            }
        });
    }

    @Override
    protected void initData() {
        etGasPrice.setText("18000000000");
        etGasLimit.setText("90000");
        etNonce.setText("1");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FROM_ADDRESS:
                tvFrom.setText(data.getStringExtra("address"));
                break;
            case TO_ADDRESS:
                etTo.setText(data.getStringExtra("address"));
                break;
        }

    }


    @OnClick({R.id.btn_select_from, R.id.btn_select_to, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_from:
                Intent fromIntent = new Intent(this, SwitchWalletActivity.class);
                fromIntent.putExtra("SwitchMode", true);
                startActivityForResult(fromIntent, FROM_ADDRESS);
                break;
            case R.id.btn_select_to:
                Intent toInetnt = new Intent(this, SwitchWalletActivity.class);
                toInetnt.putExtra("SwitchMode", true);
                startActivityForResult(toInetnt, TO_ADDRESS);
                break;
            case R.id.btn_send:
                sendTrasaction();
                break;

        }
    }



    private void sendTrasaction() {

        String from = tvFrom.getText().toString();
        String to = etTo.getText().toString();
        String value = etValue.getText().toString();
        String gasPrice = etGasPrice.getText().toString();
        String gasLimit = etGasLimit.getText().toString();
        String nonce = etNonce.getText().toString();
        Observable
                .create((ObservableOnSubscribe<EthSendTransaction>) e -> {
                    Web3j web3j = Web3JService.getInstance();
                    String hexValue = KeyStoreUtils.signedTransactionData(from, to, nonce, gasPrice, gasLimit, value);
                    EthSendTransaction send = web3j.ethSendRawTransaction(hexValue).send();
                    e.onNext(send);
                    e.onComplete();

                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respon -> {
                    String result = respon.getResult();
                    if (respon.getError() == null) {
                        sendTrasactionSucceed(result);

                    } else {
                        tvMgs.setText(respon.getError().getMessage() + "");
                        Log.e("transaction", respon.getError().getMessage() + "");
                    }


                }, throwable -> throwable.printStackTrace());

    }

    private void sendTrasactionSucceed(String result) {
        Log.e("transaction", result + "");
        tvMgs.setText(result + "");
        Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
        tvFrom.setText("");
        etTo.setText("");
        etValue.setText("");
    }


    public void getTansactionNonce(String address) {
        Observable.create((ObservableOnSubscribe<EthGetTransactionCount>) e -> {
            EthGetTransactionCount count = Web3JService.getInstance().ethGetTransactionCount("0x" + address, () -> "latest").send();
            if (count.getError() == null) {
                e.onNext(count);
                e.onComplete();
            } else {
                e.onError(new Throwable(count.getError().getMessage()));
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ethGetTransactionCount -> {
                    BigInteger bigInteger = Numeric.decodeQuantity(ethGetTransactionCount.getResult());
                    etNonce.setText(bigInteger.toString());
                }, throwable -> {
                    throwable.printStackTrace();
                });

    }



}
