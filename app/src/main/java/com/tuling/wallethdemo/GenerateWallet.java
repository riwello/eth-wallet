package com.tuling.wallethdemo;

import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuling.wallethdemo.utils.KeyStoreUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by pc on 2018/1/24.
 */

public class GenerateWallet extends BaseActivity {
    @BindView(R.id.btn_generate)
    Button btnGenerate;
    @BindView(R.id.tv_mgs)
    TextView tvMgs;
    @BindView(R.id.et_password)
    EditText etPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_generate_wallet;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.btn_generate)
    public void onViewClicked() {

        genWallet();
    }

    private void genWallet() {
        if (etPassword.length() == 0) {
            etPassword.setError("请输入password");
            return;
        }
        String password = etPassword.getText().toString();
        try {
            File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/LightWallet");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();

            //在外置卡生成
            String filename = WalletUtils.generateWalletFile(password, ecKeyPair, fileDir, false);

            KeyStoreUtils.genKeyStore2Files(ecKeyPair);

            String msg = "fileName:\n" + filename
                    + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                    + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());
            tvMgs.setText(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
