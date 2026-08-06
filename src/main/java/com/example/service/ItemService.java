package com.example.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Item;
import com.example.domain.Topping;
import com.example.repository.ItemRepository;
import com.example.repository.ToppingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

	private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

	private final ItemRepository repository;

	private final ToppingRepository toppingRepository;

	public List<Item> findAll() {
		return repository.findAll();
	}

	public List<Item> findByName(String name) {
		if (name == null || "".equals(name)) {
			logger.warn("find NULL or empty string");
			return repository.findAll();
		} else {
			return repository.findByName(name);
		}
	}

	/**
	 * 商品IDをItemRepository内のshowItemDetailに渡す
	 * 
	 * @param id
	 * @return 商品情報を１件
	 */
	public Item showItemDetail(Integer id) {
		return repository.showItemDetail(id).orElse(null);
	}

	/**
	 * 商品IDのリストから該当する商品をまとめて取得する
	 * （ループ内で1件ずつ検索するN+1を避けるため、まとめて取得してMap化できる形で返す）
	 *
	 * @param ids 商品IDのリスト
	 * @return 該当する商品のリスト
	 */
	public List<Item> findByIds(List<Integer> ids) {
		return repository.findByIds(ids);
	}

	/**
	 * 登録されてるトッピングを全件表示
	 * 
	 * @return トッピングの全件リスト
	 */
	public List<Topping> findAllTopping() {
		return toppingRepository.findAllTopping();
	}

	/**
	 * 登録されている商品の名前全件
	 * 
	 * @return 商品の名前全件
	 */
	public List<String> getAllNames() {
		return repository.getAllNames();
	}

	/**
	 * 商品をページごとに表示
	 * 
	 * 
	 */
	@Transactional
	public Page<Item> showListPaging(int page, int size, List<Item> itemList) {
		// 表示させたいページ数を-1しなければうまく動かない
		page--;
		// どの従業員から表示させるかと言うカウント値
		int startItemCount = page * size;
		// 絞り込んだ後の従業員リストが入る変数
		List<Item> list;

		if (itemList.size() < startItemCount) {
			list = Collections.emptyList();
		} else {
			// 該当ページに表示させる従業員一覧を作成
			int toIndex = Math.min(startItemCount + size, itemList.size());
			list = itemList.subList(startItemCount, toIndex);
		}
		logger.warn("itemList={}", itemList);
		// 上記で作成した該当ページに表示させる従業員一覧をページングできる形に変換して返す
		Page<Item> employeePage = new PageImpl<Item>(list, PageRequest.of(page, size), itemList.size());
		return employeePage;
	}

	public Item ApiShowItemDetail(Integer id) {
		return repository.ApiShowItemDetail(id).orElse(null);
	}
}
