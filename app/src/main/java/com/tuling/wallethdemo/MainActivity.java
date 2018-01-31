package com.tuling.wallethdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tuling.wallethdemo.Model.Web3JService;

import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        String s = Environment.getDataDirectory().toString();

        Log.e("dir", "main:" + WalletUtils.getMainnetKeyDirectory());
        Log.e("dir", "def:" + WalletUtils.getDefaultKeyDirectory());
        Log.e("dir", "data:" + s);
        Log.e("dir", "getfiles:" + getFilesDir().getAbsolutePath());
//        Web3JService.getInstance().transactionObservable().subscribeOn(rx.schedulers.Schedulers.io())
//                .subscribe(transaction -> {
//                        Log.e("tansaction","from:"+transaction.getFrom()
//                                +"\nto:"+transaction.getTo()
//                        +"\nvalue:"+transaction.getValue()
//                        +"\ngas:"+transaction.getGas()
//                        +"\ngasPrice"+transaction.getGasPrice()
//                        );
//
//                }, throwable -> throwable.printStackTrace());

    }


    public void importKeystore(View view) {
        startActivity(new Intent(this, ImportKeystore.class));

    }

    public void importPrivateKey(View view) {
        startActivity(new Intent(this, ImportPrivateKey.class));
    }

    public void transation(View view) {
        startActivity(new Intent(this, SendActivity.class));
    }

    public void generateWallet(View view) {

        startActivity(new Intent(this, GenerateWallet.class));

    }

    public void switchWallet(View view) {

        startActivity(new Intent(this, SwitchWalletActivity.class));
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.MOUNT_UNMOUNT_FILESYSTEMS"

    };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void testNetwork(View view) {
        Observable.create((ObservableOnSubscribe<Web3ClientVersion>) e -> {
            Web3ClientVersion send = Web3JService.getInstance().web3ClientVersion().send();
            e.onNext(send);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(web3ClientVersion -> {
                    String version = web3ClientVersion.getWeb3ClientVersion();
                    Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
                    Log.i("web3j",version);
                }, throwable -> throwable.printStackTrace());
    }
}
