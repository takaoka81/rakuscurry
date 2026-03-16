"use strict";
$(function () {
  calc_price();
  $(".size").on("change", function () {
    calc_price();
  });

  $(".checkbox").on("change", function () {
    calc_price();
  });

  $("#currynum").on("change", function () {
    calc_price();
  });

  // 値段の計算をして変更する関数
  function calc_price() {
    let size = $(".size:checked").val();
    let topping_count = $("#topping input:checkbox:checked").length;
    let curry_num = $("#currynum option:selected").val();
    let size_price = 0;
    let topping_price = 0;
    // 選択されたサイズに応じて、MかLの価格を加算する
    if (size === "M") {
      size_price = Number($("#sizeM").val());
    } else {
      // 'L'
      size_price = Number($("#sizeL").val());
    }
    // チェックされているトッピングを1つずつループ
    $(".checkbox:checked").each(function () {
      // HTMLの th:priceM / th:priceL から値を取得
      if (size === "M") {
        topping_price += Number($(this).data("priceM"));
      } else {
        topping_price += Number($(this).data("priceL"));
      }
    });
    let price = (size_price + topping_price) * curry_num;
    $("#totalprice").text(price.toLocaleString());
  }
  // ページが読み込まれた時に、初期の合計金額を計算して表示する

  window.onload = calcTotalPrice;
});
