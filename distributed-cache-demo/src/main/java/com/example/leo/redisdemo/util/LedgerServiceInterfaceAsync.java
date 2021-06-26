package com.example.leo.redisdemo.util;

import java.util.List;

import org.redisson.api.RFuture;
import org.redisson.api.annotation.RRemoteAsync;

@RRemoteAsync(LedgerServiceInterface.class)
public interface LedgerServiceInterfaceAsync {
    RFuture<List<String>> getEntries(int count);
}