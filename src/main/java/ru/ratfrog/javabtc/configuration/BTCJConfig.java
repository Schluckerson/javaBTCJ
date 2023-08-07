package ru.ratfrog.javabtc.configuration;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.nio.channels.CancelledKeyException;
import java.util.Scanner;

@Configuration
public class BTCJConfig {
    @Value("${bitcoinJ.filePrefix}")
    private String filePrefix;

    @Bean
    public BriefLogFormatter getLogger() {
        BriefLogFormatter logger= new BriefLogFormatter();
        BriefLogFormatter.init();
        return logger;
    }
    @Bean
    public WalletAppKit getWallet(NetworkParameters params, String filePrefix, ECKey key) {
        WalletAppKit wallet = new WalletAppKit(params, new File("."), filePrefix) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1)
                    wallet().importKey(key);
            }
        };

        wallet.startAsync();
        System.out.println("Подключение....");
        wallet.awaitRunning();
        System.out.println("Подключено!");
        System.out.println(wallet.wallet().currentReceiveAddress().toString());
        return wallet;
    }

    @Bean
    public String getFilePrefix() {
        return filePrefix;
    }

    @Bean
    public NetworkParameters getParams() {
        switch (filePrefix) {
            case "forwarding-service-testnet" -> {
                return TestNet3Params.get();
            }
            case "forwarding-service-regtest" -> {
                return RegTestParams.get();
            }
            case "forwarding-service" -> {
                return MainNetParams.get();
            }
        }
        return null;
    }

    @Bean
    public ECKey getECKey() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
                System.out.println("Введите приватный ключ:");

                String key = scanner.next();
                if (key != null && !key.isEmpty()) {
                    return ECKey.fromPrivate(key.getBytes());
                }
        }
    }
}
