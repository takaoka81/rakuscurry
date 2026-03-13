package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Item;
import com.example.dto.ItemsResponse;
import com.example.service.ItemService;

@RestController
@RequestMapping("/items")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET})
public class ItemsApiController {

@Autowired
private ItemService itemService;

@GetMapping("")
public ResponseEntity<List<ItemsResponse>> getItems(
    @RequestParam(required = false) String name){
        List<Item> items=itemService.findByName(name);
        List<ItemsResponse> itemsResponses=new ArrayList<>();
        for (Item item:items){
            itemsResponses.add(new ItemsResponse(item));
        }
        return ResponseEntity.ok(itemsResponses);
    }
}
