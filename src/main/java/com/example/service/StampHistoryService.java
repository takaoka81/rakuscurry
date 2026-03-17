package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.StampHistory;
import com.example.repository.StampHistoryRepository;

/**
 * stamp_historyテーブルを操作するrepository
 *
 * @author masashi.saito
 */
@Service
@Transactional
public class StampHistoryService {

    @Autowired
    private StampHistoryRepository stampHistoryRepository;

    /**
     * stamp_historyに対して追加処理を行います
     * 
     * @param stampHistory
     */
    public void insert(StampHistory stampHistory) {
        stampHistoryRepository.insert(stampHistory);
    }

}
