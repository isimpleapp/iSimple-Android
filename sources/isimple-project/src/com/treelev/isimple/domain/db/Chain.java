package com.treelev.isimple.domain.db;

import com.treelev.isimple.enumerable.chain.ChainType;

public class Chain {

    private String chainId;
    private String chainName;
    private ChainType chainType;

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
