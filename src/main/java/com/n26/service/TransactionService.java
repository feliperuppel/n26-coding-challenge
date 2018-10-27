package com.n26.service;

import com.n26.domain.Transaction;

public interface TransactionService {
    void add(Transaction transaction);
    void deleteAll();
}
