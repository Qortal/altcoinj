/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Adapted for Ravencoin in April 2022 by Qortal dev team
 * Thanks to https://github.com/coinext/ravencoinj for the references
 */

package org.libdohj.params;

import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptOpCodes;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkState;
import static org.bitcoinj.core.Coin.FIFTY_COINS;

/**
 * Parameters for the Ravencoin main production network on which people trade
 * goods and services.
 */
public class RavencoinMainNetParams extends AbstractRavencoinParams {
    public static final int MAINNET_MAJORITY_WINDOW = 1000;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = 950;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 750;

    public RavencoinMainNetParams() {
        super();
        id = ID_RVN_MAINNET;
        // Genesis hash is 0000006b444bc2f2ffe627be9d9e7e7a0730000870ef6eb6da46c8eae389df90

        packetMagic = 0x5241564e;
        maxTarget = Utils.decodeCompactBits(0x1e00ffffL);
        port = 8767;
        addressHeader = 60;
        p2shHeader = 122;
        // from https://github.com/RavenProject/Ravencoin/blob/master/src/chainparams.cpp

        /* packetMagic = 0xf9beb4d9;
        maxTarget = Utils.decodeCompactBits(0x1d00ffffL);
        port = 8333;
        addressHeader = 0;
        p2shHeader = 5; */
        // from https://github.com/coinext/ravencoinj/blob/master/core/src/main/java/org/bitcoinj/params/MainNetParams.java

        // acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        dumpedPrivateKeyHeader = 128;

        this.genesisBlock = createGenesis(this);
        spendableCoinbaseDepth = 100;
        subsidyDecreaseBlockCount = 2100000;

        String genesisHash = genesisBlock.getHashAsString();
        checkState(genesisHash.equals("0000006b444bc2f2ffe627be9d9e7e7a0730000870ef6eb6da46c8eae389df90"));
        // from https://github.com/RavenProject/Ravencoin/blob/master/src/chainparams.cpp

        // checkState(genesisHash.equals("000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f"));
        // from https://github.com/coinext/ravencoinj/blob/master/core/src/main/java/org/bitcoinj/params/MainNetParams.java
        alertSigningKey = Hex.decode("04678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5f");

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        dnsSeeds = new String[] {
            "seed-raven.bitactivate.com",
            "seed-raven.ravencoin.com",
            "seed-raven.ravencoin.org"
        };
        bip32HeaderP2PKHpub = 0x0488b21e; // The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderP2PKHpriv = 0x0488ade4; // The 4 byte header that serializes in base58 to "xprv"
    }

    private static AltcoinBlock createGenesis(NetworkParameters params) {
        Transaction t = new Transaction(params);
        try {
            // A script containing the difficulty bits and the following message:
            //
            //   "The Times 03/Jan/2018 Bitcoin is name of the game for new generation of firms"
            byte[] bytes = Utils.HEX.decode("04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73");
            t.addInput(new TransactionInput(params, t, bytes));
            ByteArrayOutputStream scriptPubKeyBytes = new ByteArrayOutputStream();
            Script.writeBytes(scriptPubKeyBytes, Utils.HEX.decode("04678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5f"));
            scriptPubKeyBytes.write(ScriptOpCodes.OP_CHECKSIG);
            t.addOutput(new TransactionOutput(params, t, FIFTY_COINS, scriptPubKeyBytes.toByteArray()));
        } catch (Exception e) {
            // Cannot happen.
            throw new RuntimeException(e);
        }

        Sha256Hash merkleRoot = Sha256Hash.wrap("28ff00a867739a352523808d301f504bc4547699398d70faf2266a8bae5f3516");
        AltcoinBlock genesisBlock = new AltcoinBlock(params, Block.BLOCK_VERSION_GENESIS, Sha256Hash.ZERO_HASH,
                merkleRoot, 1514999494L, 0x1e00ffffL, 25023712, Arrays.asList(t));
                // from https://github.com/RavenProject/Ravencoin/blob/master/src/chainparams.cpp

                // merkleRoot, 1231006505L, 0x1d00ffffL, 2083236893, Arrays.asList(t));
                // from https://github.com/coinext/ravencoinj/blob/master/core/src/main/java/org/bitcoinj/params/MainNetParams.java

        return genesisBlock;
    }

    private static RavencoinMainNetParams instance;
    public static synchronized RavencoinMainNetParams get() {
        if (instance == null) {
            instance = new RavencoinMainNetParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return ID_RVN_MAINNET;
    }

    @Override
    public boolean isTestNet() {
        return false;
    }
}
