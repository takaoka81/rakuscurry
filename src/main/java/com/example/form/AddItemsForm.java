package com.example.form;

import java.util.List;

/**
 * ショッピングカートに複数商品をまとめて追加するためのフォームクラス
 */
public class AddItemsForm {

	// 追加する商品のリスト
	private List<Entry> items;

	public List<Entry> getItems() {
		return items;
	}

	public void setItems(List<Entry> items) {
		this.items = items;
	}

	public static class Entry {

		// 商品id
		private Integer productId;
		// サイズ
		private String size;
		// 数量
		private Integer quantity;
		// 選択されたトッピングのidリスト
		private List<Integer> toppingIds;

		public Integer getProductId() {
			return productId;
		}

		public void setProductId(Integer productId) {
			this.productId = productId;
		}

		public Integer getQuantity() {
			return quantity;
		}

		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}

		public List<Integer> getToppingIds() {
			return toppingIds;
		}

		public void setToppingIds(List<Integer> toppingIds) {
			this.toppingIds = toppingIds;
		}

		public String getSize() {
			return size;
		}

		public void setSize(String size) {
			this.size = size;
		}
		
	}
}
