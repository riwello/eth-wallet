package com.tuling.wallethdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tuling.wallethdemo.Model.Web3JService;
import com.tuling.wallethdemo.contract.SampleContract;
import com.tuling.wallethdemo.utils.KeyStoreUtils;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class SampleContractActivity extends AppCompatActivity {

    private String contractAddress = "0xe8bb91414fae190894b02cf7ebf10b6f69b74b26";
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.btn_select)
    Button btnSelect;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.btn_get)
    Button btnGet;
    @BindView(R.id.et_set)
    EditText etSet;
    @BindView(R.id.btn_set)
    Button btnSet;
    private SampleContract sampleContract;
    private String gasPrice = "18000000000";
    private String gasLimit = "90000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract);
        ButterKnife.bind(this);

    }


    @OnClick({R.id.btn_select, R.id.btn_get, R.id.btn_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                Intent toInetnt = new Intent(this, SwitchWalletActivity.class);
                toInetnt.putExtra("SwitchMode", true);
                startActivityForResult(toInetnt, SwitchWalletActivity.FROM_ADDRESS);
                break;
            case R.id.btn_get:

                getContractValue();


                break;
            case R.id.btn_set:

                setContractValue();
                break;
        }
    }

    private void setContractValue() {
        if (etSet.length() == 0) {
            return;
        }
        Observable.create(e -> {

            sampleContract.set(new BigInteger(etSet.getText().toString())).send();
            e.onNext(new Object());
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> Toast.makeText(SampleContractActivity.this, "设置成功", Toast.LENGTH_SHORT).show()
                        , throwable -> throwable.printStackTrace());
    }


    public void getContractValue() {
        Observable.create((ObservableOnSubscribe<BigInteger>) e -> {


            BigInteger send = sampleContract.get().send();
            e.onNext(send);
            e.onComplete();

        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {
                    tvContent.setText(value.toString());

                }, throwable -> {
                    throwable.printStackTrace();

                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SwitchWalletActivity.FROM_ADDRESS:
                String address = data.getStringExtra("address");
                tvAddress.setText(address);
                Observable.create((ObservableOnSubscribe<SampleContract>) emitter -> {
                    // TODO: 2018/3/3 合约调用前要先部署 ,已经部署过直接load
                    SampleContract sampleContract = new SampleContract(contractAddress,
                            Web3JService.getInstance(),
                            KeyStoreUtils.getCredentials(address),
                            new BigInteger(gasPrice),
                            new BigInteger(gasLimit));
                    emitter.onNext(sampleContract);
                    emitter.onComplete();

                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sampleContract -> {
                            this.sampleContract = sampleContract;
                            Toast.makeText(this, "合约加载完成", Toast.LENGTH_SHORT).show();
                        }, throwable -> throwable.printStackTrace())
                ;

                break;

        }

    }
}
