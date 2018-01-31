package com.tuling.wallethdemo;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tuling.wallethdemo.utils.KeyStoreUtils;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by pc on 2018/1/23.
 */

public class ImportKeystore extends BaseActivity {


    @BindView(R.id.et_keystore)
    EditText etKeystore;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_import)
    Button btnImport;
    @BindView(R.id.tv_mgs)
    TextView tvMgs;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_keystore;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.btn_import)
    public void onViewClicked() {

        importKeyStore();
    }

    private void importKeyStore() {
        checkInput();
        String pwd = etPassword.getText().toString();
        String keystore = etKeystore.getText().toString();
        try {

            ObjectMapper mapper = new ObjectMapper();

            WalletFile walletFile = mapper.readValue(keystore, WalletFile.class);
            Credentials credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));
            ECKeyPair ecKeyPair = credentials.getEcKeyPair();
            KeyStoreUtils.genKeyStore2Files(ecKeyPair);
            String msg = "address:\n" + credentials.getAddress()
                    + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                    + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());

            tvMgs.setText(msg);


            Log.e("importKey", msg);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }


    }

    private boolean checkInput() {
        if (etKeystore.length() == 0) {
            etKeystore.setError("请输入keystore");
            return false;
        }
        if (etPassword.length() == 0) {
            etPassword.setError("请输入password");
            return false;
        }
        return true;
    }

}
