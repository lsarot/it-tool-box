package com.example.leo.redisdemo.util;

import java.util.List;

public interface LedgerServiceInterface {
    List<String> getEntries(int count);
}