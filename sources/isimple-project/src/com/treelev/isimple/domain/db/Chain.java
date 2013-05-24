package com.treelev.isimple.domain.db;

import com.treelev.isimple.enumerable.chain.ChainType;

import java.io.Serializable;

public class Chain implements Serializable{

    public final static String UI_TAG_ID_CHAIN = "_id";
    public final static String UI_TAG_NAME_CHAIN = "chain_name";
    public final static String UI_TAG_TYPE_CHAIN = "chain_type";

    private String chainId;
    private String chainName;
    private ChainType chainType;

    public static String[] getUITagsChain() {
        return new String[] { UI_TAG_ID_CHAIN, UI_TAG_NAME_CHAIN, UI_TAG_TYPE_CHAIN };
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public ChainType getChainType() {
        return chainType;
    }

    public void setChainType(ChainType chainType) {
        this.chainType = chainType;
    }
}
