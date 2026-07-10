package com.example.service;

import org.springframework.stereotype.Service;

import com.example.domain.StampHistory;
import com.example.repository.StampHistoryRepository;

import lombok.RequiredArgsConstructor;

/**
 * stamp_historyテーブルを操作するrepository
 *
 * @author masashi.saito
 */
@Service
@RequiredArgsConstructor
public class StampHistoryService {
    private final StampHistoryRepository stampHistoryRepository;

    /**
     * stamp_historyに対して追加処理を行います
     * 
     * @param stampHistory
     */
    public void insert(StampHistory stampHistory) {
        stampHistoryRepository.insert(stampHistory);
    }

}
