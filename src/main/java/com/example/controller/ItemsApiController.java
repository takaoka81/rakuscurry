package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Item;
import com.example.dto.ItemsResponse;
import com.example.exception.MyApiSpecificException;
import com.example.service.ItemService;

import lombok.RequiredArgsConstructor;

/**
 * 商品一覧情報を取得するためのコントローラー
 * 
 * @author ren.tsuchiya
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET })
public class ItemsApiController {
    private final ItemService itemService;

    /**
     * 商品一覧情報を取得
     * 
     * @param name あいまい検索に使う商品名
     * @return 検索結果
     */
    @GetMapping("")
    public ResponseEntity<List<ItemsResponse>> getItems(
            @RequestParam(required = false) String name) {
        try {
            List<Item> items = itemService.findByName(name);
            List<ItemsResponse> itemsResponses = new ArrayList<>();
            for (Item item : items) {
                itemsResponses.add(new ItemsResponse(item));
            }
            return ResponseEntity.ok(itemsResponses);
        } catch (RuntimeException e) {
            throw new MyApiSpecificException("Internal Server Error ");
        } catch (Exception ex) {
            throw ex;
        }
    }
}
