package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Item;
import com.example.domain.Topping;
import com.example.dto.ItemDetailResponse;
import com.example.exception.EntityNotFoundException;
import com.example.service.ItemService;

import lombok.RequiredArgsConstructor;

/**
 * 商品詳細を取得するためのコントローラー
 *
 * @author ren.tsuchiya
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/item")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET })
public class ItemDetailsApiController {
    private final ItemService itemService;

    /**
     * 商品詳細を取得
     * 
     * @param id 指定するid
     * @return 詳細結果
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailResponse> getItemDetail(@PathVariable Integer id) {

        Item items = itemService.ApiShowItemDetail(id);
        if (items == null) {
            throw new EntityNotFoundException("Not Found");
        }

        List<Topping> toppings = itemService.findAllTopping();
        items.setToppingList(toppings);
        return ResponseEntity.ok(new ItemDetailResponse(items));
    }
}
