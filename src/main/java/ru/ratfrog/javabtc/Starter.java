package ru.ratfrog.javabtc;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.nio.channels.CancelledKeyException;
import java.util.Scanner;

@Service
public class Starter implements ApplicationRunner {
    private WalletAppKit wallet;
    private NetworkParameters params;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            Coin balance = wallet.wallet().getBalance();
            System.out.println("Ваш баланс: " + wallet.wallet().getBalance().toPlainString() + "BTC");
            String address = getFromConsole("Введите адрес на который отправить:");
            String amount = getFromConsole("Введите количество(пример 0.0001):");
            String fee = getFromConsole("Введите fee/kb(пример 0.05");
            Address forwardingAddress = Address.fromString(params, address);
            Preconditions.checkNotNull(amount);
            Coin existAmount = Coin.parseCoin(amount);

            SendRequest req = SendRequest.to(forwardingAddress, existAmount);
            req.feePerKb = Coin.parseCoin(fee);
            Wallet.SendResult result = wallet.wallet().sendCoins(wallet.peerGroup(), req);
            Transaction createdTx = result.tx;
        } catch (KeyCrypterException | InsufficientMoneyException | CancelledKeyException e) {
            throw new RuntimeException(e);
        }

    }

    @Autowired
    public void setParams(NetworkParameters params) {
        this.params = params;
    }
    @Autowired
    public void setWallet(WalletAppKit wallet) {
        this.wallet = wallet;
    }

    private String getFromConsole(String msg) {
        String content;
        Scanner scanner = new Scanner(System.in);
        do {
            System.out.println(msg);
            content = scanner.next();
        } while (content == null || content.isEmpty());
        return content;
    }
}
