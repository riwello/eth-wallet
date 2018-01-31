package com.tuling.wallethdemo;

import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tuling.wallethdemo.utils.KeyStoreUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
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
            String fileName = WalletUtils.generateLightNewWalletFile(password, fileDir);
//            String fileName = WalletUtils.generateFullNewWalletFile(password, fileDir);

            Credentials credentials = WalletUtils.loadCredentials(password, fileDir.getPath() + "/" + fileName);
            ECKeyPair ecKeyPair = credentials.getEcKeyPair();

            KeyStoreUtils.genKeyStore2Files(ecKeyPair);

            String msg = "address:\n" + credentials.getAddress()
                    + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                    + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());
            tvMgs.setText(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
