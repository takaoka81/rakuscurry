package com.example.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.domain.StampHistory;
import com.example.repository.StampHistoryRepository;

@ExtendWith(MockitoExtension.class)
class StampHistoryServiceTest {

    @Mock
    private StampHistoryRepository stampHistoryRepository;

    @InjectMocks
    private StampHistoryService stampHistoryService;

    @Test
    @DisplayName("insertメソッドが正しくRepositoryのinsertを呼び出しているか")
    void testInsert_ShouldCallRepositoryInsert() {
        StampHistory history = new StampHistory();

        stampHistoryService.insert(history);

        verify(stampHistoryRepository, times(1)).insert(history);
    }
}
